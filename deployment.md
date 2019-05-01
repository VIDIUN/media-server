## Machine prerequisites:
 - RHEL/CentOS 6.4 or above or Ubuntu 16.0.4 or above
 - WowzaStreamingEngine 4.5.0+
 - Java jre 1.7.
 - vidiun group (gid = 613) or any other group that apache user is associated with.
 - Write access to @WEB_DIR@/content/recorded directory.

## Admin Console:
- Add admin.ini new permissions, see admin.template.ini:
    *  FEATURE_LIVE_STREAM_RECORD
    * FEATURE_VIDIUN_LIVE_STREAM
    * FEATURE_VIDIUN_LIVE_STREAM_TRANSCODE

## Media-server Installation:
1. Download the install zip from the tag: 
https://github.com/vidiun/media-server/releases/download/rel-4.5.14.78/VidiunWowzaServer-install-4.5.14.78.zip 
2. Copy lib folder in the zip into @WOWZA_DIR@/lib/
3. Copy ./installation/configTemplates/* from zip to  @WOWZA_DIR@/conf directory.
4. Replace all @WOWZA_DIR@/conf/Server.xml.template parameters and rename to Server.xml:
	* VIDIUN_SERVICE_URL - vidiun service url (for example http://www.vidiun.com)
	* VIDIUN_PARTNER_ID - partner id or -5 for all 
	* VIDIUN_PARTNER_ADMIN_SECRET - admin secret for the partner or -5 secret
	
5. Replace all @WOWZA_DIR@/conf/vLive/Application.xml.template parameters and rename to Application.xml:
    * VIDIUN_SERVICE_URL - must match as the server.xml setting