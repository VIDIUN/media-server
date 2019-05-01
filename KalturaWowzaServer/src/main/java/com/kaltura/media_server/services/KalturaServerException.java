package com.vidiun.media_server.services;

@SuppressWarnings("serial")
public class VidiunServerException extends Exception {

	public VidiunServerException(String message) {
		super(message);
	}

	public VidiunServerException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
