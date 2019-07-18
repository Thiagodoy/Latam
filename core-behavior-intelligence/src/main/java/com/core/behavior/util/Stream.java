/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.util;

/**
 *
 * @author thiag
 */
public enum Stream {	
	
	SHORT_LAYOUT("ticket","static/INTEGRATION_CSV.xml");
	
	private String streamId;
	private String streamFile;
	
	private Stream(String streamId, String streamFile) {
		this.streamId = streamId;
		this.streamFile = streamFile;
	}

	public String getStreamFile() {
		return this.streamFile;
	}
	
	public String getStreamId() {
		return this.streamId;
	}
}
