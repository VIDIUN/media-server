package com.vidiun.media_server.services;

import com.vidiun.media_server.services.Constants;
import com.vidiun.client.services.VidiunPermissionService;
import com.vidiun.client.*;
import com.vidiun.client.enums.VidiunSessionType;
import com.vidiun.client.types.*;
import com.vidiun.client.enums.VidiunEntryServerNodeType;
import com.vidiun.client.enums.VidiunBeaconObjectTypes;
import com.vidiun.client.enums.VidiunNullableBoolean;


import org.apache.log4j.Logger;

import java.io.File;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ron.yadgar on 15/05/2016.
 */

public class VidiunAPI {

    // use the same session key for all Wowza sessions, so all (within a DC) will be directed to the same sphinx to prevent synchronization problems


    private static Logger logger = Logger.getLogger(VidiunAPI.class);
    private static Map<String, Object> serverConfiguration;
    private static VidiunClient client;
    private static String hostname;
    private   VidiunConfiguration clientConfig;
    private  static VidiunAPI VidiunAPIInstance = null;
    private final int ENABLE = 1;

    public static synchronized void  initVidiunAPI(Map<String, Object> serverConfiguration)  throws VidiunServerException {
        if  (VidiunAPIInstance!=null){
            logger.warn("services.VidiunAPI instance is already initialized");
            return;
        }
        VidiunAPIInstance =  new VidiunAPI(serverConfiguration);
    }

    public static synchronized VidiunAPI getVidiunAPI(){
        if (VidiunAPIInstance== null){
            throw new NullPointerException("services.VidiunAPI is not initialized");
        }
        return VidiunAPIInstance;
    }

    private VidiunAPI(Map<String, Object> serverConfiguration)  throws VidiunServerException {
        logger.info("Initializing VidiunUncaughtException handler");
        this.serverConfiguration = serverConfiguration;
        try {
            hostname = Utils.getMediaServerHostname();
            logger.debug("Vidiun server host name: " + hostname);
            initClient();
        } catch (Exception e) {
            if (e instanceof UnknownHostException){
                logger.error("Failed to determine server host name: ", e);
            }
            throw new VidiunServerException("Error while loading services.VidiunAPI: " + e.getMessage());
        }
    }

    private void initClient() throws VidiunServerException {
        clientConfig = new VidiunConfiguration();

        int partnerId = serverConfiguration.containsKey(Constants.VIDIUN_SERVER_PARTNER_ID) ? (int) serverConfiguration.get(Constants.VIDIUN_SERVER_PARTNER_ID) : Constants.MEDIA_SERVER_PARTNER_ID;


        if (!serverConfiguration.containsKey(Constants.VIDIUN_SERVER_URL))
            throw new VidiunServerException("Missing configuration [" + Constants.VIDIUN_SERVER_URL + "]");

        if (!serverConfiguration.containsKey(Constants.VIDIUN_SERVER_ADMIN_SECRET))
            throw new VidiunServerException("Missing configuration [" + Constants.VIDIUN_SERVER_ADMIN_SECRET + "]");

        clientConfig.setEndpoint((String) serverConfiguration.get(Constants.VIDIUN_SERVER_URL));
        logger.debug("Initializing Vidiun client, URL: " + clientConfig.getEndpoint());

        if (serverConfiguration.containsKey(Constants.VIDIUN_SERVER_TIMEOUT))
            clientConfig.setTimeout(Integer.parseInt((String) serverConfiguration.get(Constants.VIDIUN_SERVER_TIMEOUT)) * 1000);

        client = new VidiunClient(clientConfig);
        client.setPartnerId(partnerId);
        client.setClientTag("MediaServer-" + hostname);
        generateClientSession();

        TimerTask generateSession = new TimerTask() {

            @Override
            public void run() {     //run every 24 hours
                generateClientSession();
            }
        };

        long sessionGenerationInterval = 23*60*60*1000;// refresh every  23 hours  (VS is valid for a 24h);

        Timer timer = new Timer("clientSessionGeneration", true);
        timer.schedule(generateSession, sessionGenerationInterval, sessionGenerationInterval);
    }

    private void generateClientSession() {
        int partnerId = serverConfiguration.containsKey(Constants.VIDIUN_SERVER_PARTNER_ID) ? (int) serverConfiguration.get(Constants.VIDIUN_SERVER_PARTNER_ID) : Constants.MEDIA_SERVER_PARTNER_ID;
        String adminSecretForSigning = (String) serverConfiguration.get(Constants.VIDIUN_SERVER_ADMIN_SECRET);
        String userId = "MediaServer";
        VidiunSessionType type = VidiunSessionType.ADMIN;
        int expiry = 86400; // ~24 hours
        String privileges = "disableentitlement,sessionkey:" + Constants.VIDIUN_PERMANENT_SESSION_KEY;
        String sessionId;

        try {
            sessionId = client.generateSession(adminSecretForSigning, userId, type, partnerId, expiry, privileges);
        } catch (Exception e) {
            logger.error("Initializing Vidiun client, URL: " + client.getVidiunConfiguration().getEndpoint());
            return;
        }

        client.setSessionId(sessionId);
        logger.debug("Vidiun client session id: " + sessionId);    //session id - VS
    }
    
    public String getKS() {
        return client.getSessionId();
    }

    public VidiunLiveStreamEntry authenticate(String entryId, int partnerId, String token, VidiunEntryServerNodeType serverIndex) throws Exception {
        if (partnerId == -5){
            VidiunClient Client= getClient();
            VidiunLiveEntry liveEntry = Client.getLiveStreamService().get(entryId);
            partnerId = liveEntry.partnerId;
        }

        VidiunClient impersonateClient = impersonate(partnerId);

        VidiunLiveStreamEntry updatedEntry = impersonateClient.getLiveStreamService().authenticate(entryId, token, hostname, serverIndex);

        return updatedEntry;
    }

    private VidiunClient getClient() {
        logger.warn("getClient");
        //return client;

        KalturaClient cloneClient = new KalturaClient(clientConfig);
        cloneClient.setSessionId(client.getSessionId());
        return cloneClient;
    }

    private VidiunClient  impersonate(int partnerId) {

        VidiunConfiguration impersonateConfig = new VidiunConfiguration();
        impersonateConfig.setEndpoint(clientConfig.getEndpoint());
        impersonateConfig.setTimeout(clientConfig.getTimeout());

        VidiunClient cloneClient = new VidiunClient(impersonateConfig);
        cloneClient.setPartnerId(partnerId);
        cloneClient.setClientTag(client.getClientTag());
        cloneClient.setSessionId(client.getSessionId());

        return cloneClient;
    }

    public VidiunLiveAsset getAssetParams(VidiunLiveEntry liveEntry, int assetParamsId) {
        //check this function
        if(liveEntry.conversionProfileId <= 0) {
            return null;
        }
        VidiunConversionProfileAssetParamsFilter assetParamsFilter = new VidiunConversionProfileAssetParamsFilter();
        assetParamsFilter.conversionProfileIdEqual = liveEntry.conversionProfileId;

        VidiunLiveAssetFilter asstesFilter = new VidiunLiveAssetFilter();
        asstesFilter.entryIdEqual = liveEntry.id;

        VidiunClient impersonateClient = impersonate(liveEntry.partnerId);
        impersonateClient.startMultiRequest();
        try {
            impersonateClient.getConversionProfileAssetParamsService().list(assetParamsFilter);
            impersonateClient.getFlavorAssetService().list(asstesFilter);
            VidiunMultiResponse responses = impersonateClient.doMultiRequest();

            Object flavorAssetsList = responses.get(1);

            if(flavorAssetsList instanceof VidiunFlavorAssetListResponse){
                for(VidiunFlavorAsset liveAsset : ((VidiunFlavorAssetListResponse) flavorAssetsList).objects){
                    if(liveAsset instanceof VidiunLiveAsset){
                        if (liveAsset.flavorParamsId == assetParamsId){
                            return (VidiunLiveAsset)liveAsset;
                        }
                    }
                }
            }

        } catch (VidiunApiException e) {
            logger.error("Failed to load asset params for live entry [" + liveEntry.id + "]:" + e);
        }
        return null;
    }

    public VidiunFlavorAssetListResponse getVidiunFlavorAssetListResponse(VidiunLiveEntry liveEntry) {
        //check this function
        if(liveEntry.conversionProfileId <= 0) {
            return null;
        }

        VidiunLiveAssetFilter asstesFilter = new VidiunLiveAssetFilter();
        asstesFilter.entryIdEqual = liveEntry.id;

        VidiunClient impersonateClient = impersonate(liveEntry.partnerId);

        try {
            VidiunFlavorAssetListResponse list = impersonateClient.getFlavorAssetService().list(asstesFilter);
            return list;
        } catch (VidiunApiException e) {
            logger.error("Failed to load asset params for live entry [" + liveEntry.id + "]:" + e);
        }
        return null;
    }

    public VidiunLiveEntry appendRecording(int partnerId, String entryId, String assetId, VidiunEntryServerNodeType nodeType, String filePath, double duration, boolean isLastChunk) throws Exception{
        VidiunDataCenterContentResource resource = getContentResource(filePath, partnerId);
        VidiunClient impersonateClient = impersonate(partnerId);
        VidiunLiveEntry updatedEntry = impersonateClient.getLiveStreamService().appendRecording(entryId, assetId, nodeType, resource, duration, isLastChunk);

        return updatedEntry;
    }

    public boolean isNewRecordingEnabled(VidiunLiveEntry liveEntry) {
        try {
            VidiunClient impersonateClient = impersonate(liveEntry.partnerId);
            VidiunPermission vidiunPermission = impersonateClient.getPermissionService().get("FEATURE_LIVE_STREAM_VIDIUN_RECORDING");

            return vidiunPermission.status.getHashCode() == ENABLE;
        }
        catch (VidiunApiException e) {
            logger.error("(" + liveEntry.id + ") Error checking New Recording Permission. Code: " + e.code + " Message: " + e.getMessage());
            return false;
        }
    }

    private VidiunDataCenterContentResource getContentResource (String filePath,  int partnerId) {
        if (!this.serverConfiguration.containsKey(Constants.VIDIUN_SERVER_WOWZA_WORK_MODE) || (this.serverConfiguration.get(Constants.VIDIUN_SERVER_WOWZA_WORK_MODE).equals(Constants.WOWZA_WORK_MODE_VIDIUN))) {
            VidiunServerFileResource resource = new VidiunServerFileResource();
            resource.localFilePath = filePath;
            return resource;
        }
        else {
            VidiunClient impersonateClient = impersonate(partnerId);
            try {
                impersonateClient.startMultiRequest();
                impersonateClient.getUploadTokenService().add(new VidiunUploadToken());

                File fileData = new File(filePath);
                impersonateClient.getUploadTokenService().upload("{1:result:id}", new VidiunFile(fileData));
                VidiunMultiResponse responses = impersonateClient.doMultiRequest();

                VidiunUploadedFileTokenResource resource = new VidiunUploadedFileTokenResource();
                Object response = responses.get(1);
                if (response instanceof VidiunUploadToken)
                    resource.token = ((VidiunUploadToken)response).id;
                else {
                    if (response instanceof VidiunApiException) {
                    }
                    logger.error("Content resource creation error: " + ((VidiunApiException)response).getMessage());
                    return null;
                }

                return resource;

            } catch (VidiunApiException e) {
                logger.error("Content resource creation error: " + e);
            }
        }

        return null;
    }

    public void cancelReplace(VidiunLiveEntry liveEntry){

        try{
            VidiunClient impersonateClient = impersonate(liveEntry.partnerId);
            impersonateClient.getMediaService().cancelReplace(liveEntry.recordedEntryId);
        }
        catch (Exception e) {

            logger.error("Error occured: " + e);
        }
    }
    public static VidiunLiveAsset getliveAsset(VidiunFlavorAssetListResponse liveAssetList, int assetParamsId){

        for(VidiunFlavorAsset liveAsset :  liveAssetList.objects){
            if(liveAsset instanceof VidiunLiveAsset){
                if (liveAsset.flavorParamsId == assetParamsId){
                    return (VidiunLiveAsset)liveAsset;
                }
            }
        }
        return null;
    }

    public void sendBeacon(String entryId, int partnerId, String alertType, String parameter, String beaconTag) throws Exception {
        try {
            if (partnerId == -5) {
                VidiunClient Client = getClient();
                VidiunLiveEntry liveEntry = Client.getLiveStreamService().get(entryId);
                partnerId = liveEntry.partnerId;
            }

            VidiunBeacon beacon = new VidiunBeacon();
            beacon.relatedObjectType = VidiunBeaconObjectTypes.ENTRY_BEACON;
            beacon.eventType = beaconTag;
            beacon.objectId = entryId;
            beacon.privateData = createAlertJson(entryId, alertType, parameter);

            VidiunClient impersonateClient = impersonate(partnerId);

            impersonateClient.getBeaconService().add(beacon, VidiunNullableBoolean.TRUE_VALUE);
        }
        catch (Exception e) {
            logger.error("Exception in sendBeacon: ", e);
        }
    }

    private String createAlertJson(String entryId, String alertType, String parameter) {
        String msg = "{\"alerts\":[";
        // Add parameters
        msg += "{\"Arguments\":{\"entryId\":\"" + entryId + "\",\"alertType\":\"" + alertType + "\",";
        if (!parameter.equals("")) {
            msg += "\"parameter\":\"" + parameter + "\"";
        }
        // Add alert time and code
        msg += "},\"Time\":" + new Date().getTime() + ",\"Code\":" + getErrorCode(alertType) + "}], ";
        // Add beacon max severity
        msg += "\"maxSeverity\": " + Constants.AUTHENTICATION_ALERT_SEVERITY + "}";

        return msg;
    }

    private int getErrorCode(String errorType) {
        switch (errorType) {
            case "LIVE_STREAM_INVALID_TOKEN":
                return Constants.AUTHENTICATION_ALERT_INVALID_TOKEN;
            case "INCORRECT_STREAM_NAME":
                return Constants.AUTHENTICATION_ALERT_INCORRECT_STREAM;
            case "ENTRY_ID_NOT_FOUND":
                return Constants.AUTHENTICATION_ALERT_ENTRY_NOT_FOUND;
            case "LIVE_STREAM_EXCEEDED_MAX_PASSTHRU":
                return Constants.AUTHENTICATION_ALERT_TOO_MANY_STREAMS;
            case "LIVE_STREAM_EXCEEDED_MAX_TRANSCODED":
                return Constants.AUTHENTICATION_ALERT_TOO_MANY_TRANSCODED_STREAMS;
            default:
                return -1;
        }
    }

}
