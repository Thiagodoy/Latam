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
	
    
        SHORT_LAYOUT_PARSER("ticket","static/TICKET_SHORT_LAYOUT.xml"),
        FULL_LAYOUT_PARSER("ticket","static/TICKET.xml"),
        
        HEADER_LAYOUT_SHORT("streamHeader","static/HEADER_LAYOUT_SHORT.xml"),
        HEADER_LAYOUT_FULL("streamHeader","static/HEADER_LAYOUT.xml"),
        
	SHORT_LAYOUT_INTEGRATION("ticket","static/SHORT_INTEGRATION_CSV.xml"),
        FULL_LAYOUT_INTEGRATION("ticket","static/FULL_INTEGRATION_CSV.xml");
	
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
