package com.vidiun.media_server.services;

/**
 * Created by ron.yadgar on 02/06/2016.
 */
public class Constants {

    public final static String HTTP_PROVIDER_KEY = "diagnostics";
    public final static int VIDIUN_REJECTED_STEAMS_SIZE = 100;
    public final static String VIDIUN_PERMANENT_SESSION_KEY = "vidiunWowzaPermanentSessionKey";
    public final static String CLIENT_PROPERTY_CONNECT_URL = "connecttcUrl";
    public final static String CLIENT_PROPERTY_ENCODER = "connectflashVer";
    public final static String CLIENT_PROPERTY_SERVER_INDEX = "serverIndex";
    public final static String CLIENT_PROPERTY_VIDIUN_LIVE_ENTRY = "VidiunLiveEntry";
    public final static String CLIENT_PROPERTY_VIDIUN_LIVE_ASSET_LIST = "VidiunLiveAssetList";
    public final static String REQUEST_PROPERTY_PARTNER_ID = "p";
    public final static String REQUEST_PROPERTY_ENTRY_ID = "e";
    public final static String REQUEST_PROPERTY_SERVER_INDEX = "i";
    public final static String REQUEST_PROPERTY_TOKEN = "t";
    public final static String VIDIUN_SERVER_URL = "VidiunServerURL";
    public final static String VIDIUN_SERVER_ADMIN_SECRET = "VidiunServerAdminSecret";
    public final static String VIDIUN_SERVER_PARTNER_ID = "VidiunPartnerId";
    public final static String VIDIUN_SERVER_TIMEOUT = "VidiunServerTimeout";
    public final static String VIDIUN_SERVER_UPLOAD_XML_SAVE_PATH = "uploadXMLSavePath";
    public final static String VIDIUN_SERVER_WOWZA_WORK_MODE = "VidiunWorkMode";
    public final static String WOWZA_WORK_MODE_VIDIUN = "vidiun";
    public final static String VIDIUN_RECORDED_FILE_GROUP = "VidiunRecordedFileGroup";
    public final static String DEFAULT_RECORDED_FILE_GROUP = "vidiun";
    public final static String DEFAULT_RECORDED_SEGMENT_DURATION_FIELD_NAME = "DefaultRecordedSegmentDuration";
    public final static String COPY_SEGMENT_TO_LOCATION_FIELD_NAME = "CopySegmentToLocation";
    public final static String INVALID_SERVER_INDEX = "-1";
    public final static String LIVE_STREAM_EXCEEDED_MAX_RECORDED_DURATION = "LIVE_STREAM_EXCEEDED_MAX_RECORDED_DURATION";
    public final static String RECORDING_ANCHOR_TAG_VALUE = "recording_anchor";
    public final static int DEFAULT_RECORDED_SEGMENT_DURATION = 900000; //~15 minutes
    public final static int MEDIA_SERVER_PARTNER_ID = -5;
    public static final String AMFSETDATAFRAME = "amfsetdataframe";
    public static final String ONMETADATA_AUDIODATARATE = "audiodatarate";
    public static final String ONMETADATA_VIDEODATARATE = "videodatarate";
    public static final String ONMETADATA_WIDTH = "width";
    public static final String ONMETADATA_HEIGHT = "height";
    public static final String ONMETADATA_FRAMERATE= "framerate";
    public static final String ONMETADATA_VIDEOCODECIDSTR = "videocodecidstring";
    public static final String ONMETADATA_AUDIOCODECIDSTR = "audiocodecidstring";
    public static final String[] streamParams = {ONMETADATA_AUDIODATARATE, ONMETADATA_VIDEODATARATE, ONMETADATA_WIDTH,
            ONMETADATA_HEIGHT, ONMETADATA_FRAMERATE, ONMETADATA_VIDEOCODECIDSTR, ONMETADATA_AUDIOCODECIDSTR};
    public static final int DEFAULT_CHUNK_DURATION_MILLISECONDS = 10000;
    public static final String STREAM_ACTION_LISTENER_PROPERTY = "VidiunStreamActionListenerProperty";
    public static final int VIDIUN_SYNC_POINTS_INTERVAL_PROPERTY = 60 * 1000;
    public static final String VIDIUN_LIVE_ENTRY_ID = "VidiunLiveEntryId";
    public static final String VIDIUN_ENTRY_VALIDATED_TIME = "VidiunEntryValidatedTime";
    public static final String VIDIUN_ENTRY_AUTHENTICATION_LOCK = "VidiunEntryAuthenticationLock";
    public static final String VIDIUN_ENTRY_AUTHENTICATION_ERROR_FLAG = "VidiunEntryAuthenticationFlag";
    public static final String VIDIUN_ENTRY_AUTHENTICATION_ERROR_MSG = "VidiunEntryAuthenticationMsg";
    public static final String VIDIUN_ENTRY_AUTHENTICATION_ERROR_TIME = "VidiunEntryAuthenticationTime";
    public static final int VIDIUN_PERSISTENCE_DATA_MIN_ENTRY_TIME = 30000;
    public static final int VIDIUN_ENTRY_PERSISTENCE_CLEANUP_START = 10000;
    public static final int VIDIUN_TIME_BETWEEN_PERSISTENCE_CLEANUP = 60000;
    public static final int VIDIUN_MIN_TIME_BETWEEN_AUTHENTICATIONS = 10000;
}
