Vidiun Media Server   
==========================
---
This project is a wrapper for media streaming servers that integrates to streaming servers with the [Vidiun Server](https://github.com/vidiun/server). 
The Vidiun Media Server is an application built to run over the Wowza Streaming Engine
By default, the Vidiun Streaming application is called **vLive**.
---

Vidiun API Integration
---
---
#### API Usage ####
The Vidiun media server can use API client in one of two configurable ways:
1.	Opposite built-in partner (-5), which impersonates the handled partner for all API calls.
2.	As an eCDN installation- in this case the streaming server (e.g. Wowza) is installed on the client's machine while the partner continues working opposite the SaaS installation (as opposed to on-prem installations). The streaming server thus installed works only opposite the partnerId it belongs to and does not use impersonation.  
The VS is regenerated periodically according to the VS expiry time.  

---

#### Broadcast URL ####
Example URL to vLive application: rtmp://domain/vLive/?p=102&e=0_rkij47dr&i=0&t=dj94kfit  
The broadcast URL consists of the following arguments:
*	p – partner id.
*	e – entry id.
*	i – index (0 for primary and 1 for backup).
*	t – token.

Stream name example: 0_rkij47dr_1  
The stream name consists of entry id, underscore and stream index (between 1 to 3).
The stream index could be used for multiple bitrate streams ingestion, if only one bitrate is streamed the index should be 1.  

---

#### Integration points ####
* Connection – **liveStream.authenticate** action is called to validate the entry id with the supplied token.
The action should return a VidiunLiveStreamEntry object, or an exception in case the authentication failed.
The returned entry object could be used later to determine if DVR and recording are enabled for this entry.
* Transcoding - **wowza_liveStreamConversionProfile.serve** is called for each published entry. This action returns an XML formatted according to the Wowza required structure, indicating the transcoding that the stream should undergo.

## Deployment ##
Instructions to install a Wowza SaaS server can be found here:
[deployment doc](deployment.md)

<br>

## Commercial Editions and Paid Support ##

The Open Source Vidiun Platform is provided under the [AGPLv3 license](http://www.gnu.org/licenses/agpl-3.0.html) and with no
commercial support or liability.  

[Vidiun Inc.](http://corp.vidiun.com) also provides commercial solutions and services including pro-active platform monitoring,
applications, SLA, 24/7 support and professional services. If you're looking for a commercially supported video platform  with
integrations to commercial encoders, streaming servers, eCDN, DRM and more - Start a [Free Trial of the Vidiun.com Hosted
Platform](http://corp.vidiun.com/free-trial) or learn more about [Vidiun' Commercial OnPrem
Edition™](http://corp.vidiun.com/Deployment-Options/Vidiun-On-Prem-Edition). For existing RPM based users, Vidiun offers
commercial upgrade options.


## License and Copyright Information
All code in this project is released under the [AGPLv3 license](http://www.gnu.org/licenses/agpl-3.0.html) unless a different license for a particular library is specified in the applicable library path.


Copyright © Vidiun Inc. All rights reserved.

