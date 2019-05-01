package com.vidiun.media_server.listeners;

import org.apache.log4j.Logger;

import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.server.*;
import com.wowza.wms.vhost.IVHost;
import com.wowza.wms.vhost.VHostSingleton;
import com.vidiun.media_server.services.VidiunAPI;
import com.vidiun.media_server.services.VidiunUncaughtExceptionHnadler;

import java.util.Map;

public class ServerListener implements IServerNotify2 {

	protected static Logger logger = Logger.getLogger(ServerListener.class);

	private static Map<String, Object> config;

	public void onServerConfigLoaded(IServer server) {
	}

	public void onServerCreate(IServer server) {
	}


	@SuppressWarnings("unchecked")
	public void onServerInit(IServer server) {
		config = server.getProperties();
		try {
			VidiunAPI.initVidiunAPI(config);
			logger.info("listeners.ServerListener::onServerInit Initialized Vidiun server");

			loadAndLockAppInstance(IVHost.VHOST_DEFAULT, "vLive", IApplicationInstance.DEFAULT_APPINSTANCE_NAME);

		} catch ( Exception e) {
			logger.error("listeners.ServerListener::onServerInit Failed to initialize services.VidiunAPI: " + e.getMessage());
		}
		Thread.setDefaultUncaughtExceptionHandler(new VidiunUncaughtExceptionHnadler());
	}

	public void onServerShutdownStart(IServer server) {

	}

	public void onServerShutdownComplete(IServer server) {
	}

	private void loadAndLockAppInstance(String vhostName, String appName, String appInstanceName)
	{
		IVHost vhost = VHostSingleton.getInstance(vhostName);
		if(vhost != null)
		{
			if (vhost.startApplicationInstance(appName, appInstanceName))	//invoke OnAppsrart in all managers
			{
				vhost.getApplication(appName).getAppInstance(appInstanceName).setApplicationTimeout(0); //stop the instance from shutting down:
			}
			else
			{
				logger.warn("Application folder ([install-location]/applications/" + appName + ") is missing");
			}
		}
	}
	public static Map<String, Object> getServerConfig(){
		return config;
	}
}
