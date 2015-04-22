package com.tecnotree.stf.common;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.tecnotree.stf.util.FileOperation;
import com.tecnotree.stf.util.STConstant;


/**
 * @author guptani
 * Perform Client CRUDQ Operations.
 */
public class WebCall {
	final static Logger logger = Logger.getLogger(WebCall.class);
	protected Client client = null;
	protected WebResource webResource = null;
	protected ClientResponse clientResponse = null;
	
	public String createOauth(Map<String, Object> oauthData){
		client = Client.create();
		String response = null;
		try {
			webResource = client.resource(oauthData.get("url").toString());
			clientResponse = webResource.type(oauthData.get("contentType").toString()).post(ClientResponse.class, oauthData.get("body"));
			response = clientResponse.getEntity(String.class);
			System.out.println("Response\t"+response+"\n");
		} catch (Exception e) {
			logger.error("Exception:: "+e);
			//System.out.println("Exception:: "+e);
		}
		if(response == null){
			//response = FileOperation.readInputfile(STConstant.PAYLOAD_TEMPLATE_DIR+"Oauth_Payload.txt");
		}
		System.out.println("\nresponse:: "+response);
		return response;
	}
	
	public String createClient(String url, String contentType, String payload, String responseStatusKey) {
		client = Client.create();
		String response = null;
		long timeTaken = 0;
		System.out.println("URL:: "+url);
		logger.info("URL:: "+url);
		try {
			long startTime = System.currentTimeMillis();
			webResource = client.resource(url);
			clientResponse = webResource.type(contentType).post(ClientResponse.class, payload);
			long endTime = System.currentTimeMillis();
			timeTaken = endTime - startTime;
			response = clientResponse.getEntity(String.class);
			System.out.println("Time Taken:: "+timeTaken+"\nResponse:: "+response+"\n");
			logger.info("Time Taken:: "+timeTaken+"\nResponse:: "+response+"\n");
			if (responseStatusKey == null && clientResponse.getStatus() != 200) {
				response = "FAIL";
			}
		} catch (Exception e) {
			logger.error("Exception:: "+e);
			System.out.println("Exception:: "+e);
			return "FAIL";
		}
		return response;
	}

	public String createQuery(String url, String contentType, String payload, boolean isPayload, String responseStatusKey){
		client = Client.create();
		String response = null;
		long timeTaken = 0;
		System.out.println("URL:: "+url);
		logger.info("URL:: "+url);
		try {
			long startTime = System.currentTimeMillis();
			if(!isPayload){
				webResource = client.resource(url).path(payload.trim());
				clientResponse = webResource.type(contentType).get(ClientResponse.class);
			} else {
				webResource = client.resource(url);
				clientResponse = webResource.type(contentType).put(ClientResponse.class, payload);
			}
			long endTime = System.currentTimeMillis();
			timeTaken = endTime - startTime;
			response = clientResponse.getEntity(String.class);
			System.out.println("Time Taken:: "+timeTaken+"\nResponse:: "+response+"\n");
			logger.info("Time Taken:: "+timeTaken+"\nResponse:: "+response+"\n");
			if (responseStatusKey == null && clientResponse.getStatus() != 200) {
				response = STConstant.FAIL;
			}
		} catch (Exception e) {
			logger.error("Exception:: "+e);
			System.out.println("Exception:: "+e);
			return STConstant.FAIL;
		}
		return response;
	}
	
	public String updateClient(String url, String contentType, String payload, String responseStatusKey){
		client = Client.create();
		String response = null;
		System.out.println("URL:: "+url);
		logger.info("URL:: "+url);
		long timeTaken = 0l;
		try {
			long startTime = System.currentTimeMillis();
			webResource = client.resource(url);
			clientResponse = webResource.type(contentType).put(ClientResponse.class, payload);
			long endTime = System.currentTimeMillis();
			timeTaken = endTime - startTime;
			response = clientResponse.getEntity(String.class);
			System.out.println("Time Taken:: "+timeTaken+"\nResponse:: "+response+"\n");
			logger.info("Time Taken:: "+timeTaken+"\nResponse:: "+response+"\n");
			if (responseStatusKey == null && clientResponse.getStatus() != 200) {
				response = STConstant.FAIL;
			}
		} catch (Exception e) {
			logger.error("Exception:: "+e);
			System.out.println("Exception:: "+e);
			return STConstant.FAIL;
		}
		return response;
	}

	public static String createRestURL(Map<String, Object> systemTestConfig, WebData wb){
		String requestType = wb.getApiName();
		/*String additionalUrl = wb.getAdditionalUrl();
		if(additionalUrl != null && !additionalUrl.isEmpty()){
			requestType = requestType+additionalUrl;
		}*/
		
		if(requestType.contains(STConstant.UNDERSCORE_DELIMITER)){
			requestType = requestType.replaceAll(STConstant.UNDERSCORE_DELIMITER, STConstant.FRONT_SHLASH_DELIMITER);
		}
		
		String hostName = (String)systemTestConfig.get(STConstant.HOST_NAME);
		hostName = "http://"+hostName+":";
		String portNo = (String)systemTestConfig.get(STConstant.PORT_NO);
		String service = (String)systemTestConfig.get(STConstant.SERVICE);
		String tenantId = (String)systemTestConfig.get(STConstant.TENANT_ID);
		String productName= (String)systemTestConfig.get(STConstant.PRODUCT_NAME);
		String version= (String)systemTestConfig.get(STConstant.VERSION);
		StringBuilder url = new StringBuilder();
		url.append(hostName).append(portNo).append(service).append("/"+tenantId).append("/"+productName).append("/"+version).append("/"+requestType);

		return url.toString();
	}
	
	private String getContentType(String contentType){
		if("xml".equalsIgnoreCase(contentType)){
			return MediaType.APPLICATION_XML;
		} else if ("json".equalsIgnoreCase(contentType)){
			return MediaType.APPLICATION_JSON;
		}
		return null;
	}
	
	public String initRestCall(WebData webData, boolean isPayload, String payload){
		String contentType = getContentType(webData.getContentType());
		String operation = webData.getOperation();
		String url = webData.getUrl();
		String responseStatusKey = webData.getResponseStatusKey();
		if(contentType == null){
			contentType = MediaType.APPLICATION_JSON;
		}
		String response = null;
		if("Create".equalsIgnoreCase(operation)){
			response = createClient(url, contentType, payload, responseStatusKey);
		} else if("Read".equalsIgnoreCase(operation)){
			response = createQuery(url, contentType, payload, isPayload, responseStatusKey);
		} else if("Update".equalsIgnoreCase(operation)){
			response = updateClient(url, contentType, payload, responseStatusKey);
		} else if("Delete".equalsIgnoreCase(operation)){
			//TODO
		}
		return response;
	}
}
