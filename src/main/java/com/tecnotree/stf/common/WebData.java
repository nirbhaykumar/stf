package com.tecnotree.stf.common;

import java.util.List;


/**
 * @author guptani
 * Contains all the data for Request API
 */
public class WebData {
	
	private String operation;
	private String contentType;
	private String url;
	private String additionalUrl;
	private String apiName;
	private String response;
	private String responseStatusKey;
	private String keysToReplace;
	private List<String> payloadList;
	
	public String getResponseStatusKey() {
		return responseStatusKey;
	}
	public void setResponseStatusKey(String responseStatusKey) {
		this.responseStatusKey = responseStatusKey;
	}
	public String getApiName() {
		return apiName;
	}
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	public String getAdditionalUrl() {
		return additionalUrl;
	}
	public void setAdditionalUrl(String additionalUrl) {
		this.additionalUrl = additionalUrl;
	}
	public String getKeysToReplace() {
		return keysToReplace;
	}
	public void setKeysToReplace(String keysToReplace) {
		this.keysToReplace = keysToReplace;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public List<String> getPayloadList() {
		return payloadList;
	}
	public void setPayloadList(List<String> payloadList) {
		this.payloadList = payloadList;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
}
