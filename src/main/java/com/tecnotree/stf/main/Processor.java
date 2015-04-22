package com.tecnotree.stf.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.tecnotree.stf.common.CombinationGenerator;
import com.tecnotree.stf.common.WebCall;
import com.tecnotree.stf.common.WebData;
import com.tecnotree.stf.util.FileOperation;
import com.tecnotree.stf.util.JsonUtility;
import com.tecnotree.stf.util.STConstant;
import com.tecnotree.stf.util.StringUtility;

/**
 * @author guptani
 * Populate API data and Process all the Request API.
 */
public class Processor {

	final static Logger logger = Logger.getLogger(Processor.class);

	final static String LINE = "---------------------------------------------------------------------------------------------------------";

	private Map<String, Object> stfConfigMap;
	private Map<String, Object> mockConfigMap;
	private Map<String, Object> registrationConfig;
	private Map<String, Object> oauthConfig;
	
	private static String productName;
	private static int successCount;
	private static int NoOfIteration = 1;
	private List<String> apiList = new ArrayList<String>();
	private Map<String, WebData> apiDataMap = new HashMap<String, WebData>();
	private StringBuilder reportContent = new StringBuilder();

	public Processor(){}

	public Processor(Map<String, Object> stfConfigMap, Map<String, Object> mockConfigMap,
						Map<String, Object> registrationConfig, Map<String, Object> oauthConfig){
		this.stfConfigMap = stfConfigMap;
		this.mockConfigMap = mockConfigMap;
		this.registrationConfig = registrationConfig;
		this.oauthConfig = oauthConfig;
	}

	/**
	 * This method will populate all the configuration details related to API
	 */
	private void populateApiData(){
		List<String> apisWithKeysList = (List<String>) registrationConfig.get("apiSequence");
		Map<String, String> statusMap = (Map<String, String>) registrationConfig.get("status");
		for(String apiNameWithKey: apisWithKeysList){
			if(apiNameWithKey.indexOf(STConstant.HIPHEN) > 0){
				WebData wb = new WebData();
				String[] apiArray = apiNameWithKey.split(STConstant.HIPHEN);
				if(apiArray == null && apiArray.length <= 3){
					System.out.println("API Configuration is not valid for api name - "+apiNameWithKey);
					return;
				}
				String operation = apiArray[0]; 
				String type = apiArray[1]; 
				//String apiNameWithAddParams = apiArray[2];
				String apiName = apiArray[2];
				/*if(apiNameWithAddParams.contains(STConstant.UNDERSCORE_DELIMITER)){
					apiName = apiNameWithAddParams.substring(0, apiNameWithAddParams.indexOf(STConstant.UNDERSCORE_DELIMITER));
					String additionalUrl = apiNameWithAddParams.substring(apiNameWithAddParams.indexOf(STConstant.UNDERSCORE_DELIMITER));
					additionalUrl = additionalUrl.replaceAll(STConstant.UNDERSCORE_DELIMITER, STConstant.FRONT_SHLASH_DELIMITER);
					wb.setAdditionalUrl(additionalUrl);
				} else {
					apiName = apiNameWithAddParams;
				}*/
				String keysToReplace = apiArray[3];
				reportContent.append(apiName+STConstant.COMMA_DELIMITER);
				wb.setApiName(apiName);
				wb.setOperation(operation);
				if(statusMap != null){
					wb.setResponseStatusKey(statusMap.get(apiName));
				}
				wb.setContentType(type);
				wb.setKeysToReplace(keysToReplace);
				if("mock".equalsIgnoreCase(type)){
					wb.setUrl( WebCall.createRestURL(mockConfigMap, wb));
				} else {
					wb.setUrl( WebCall.createRestURL(stfConfigMap, wb));
				}
				apiList.add(apiName);
				apiDataMap.put(apiName, wb);
			} else {
				apiList.add(apiNameWithKey);
			}
		}
		reportContent.append(System.lineSeparator());
	}

	public void registrationWithTestReport(){
		successCount = 0;
		productName = (String) registrationConfig.get("productName");
		populateApiData();
		//oauth authentication process.
		if(oauthConfig != null && !oauthConfig.isEmpty()){
			//String response = new WebCall().createOauth(oauthConfig);
			WebData wb = new WebData();
		//	wb.setResponse(response);
			apiDataMap.put("oauth", wb);
		}
		for(String apiName: apiList){
			//if its mock call, no need to create combination
			if("mock".equalsIgnoreCase(apiDataMap.get(apiName).getContentType())){
				continue;
			}
			
			String templatePath = STConstant.PAYLOAD_TEMPLATE_DIR+productName+STConstant.FRONT_SHLASH_DELIMITER+apiName+"_Payload.txt";
			logger.info("templatePath for API "+templatePath);
			System.out.println("templatePath for API "+templatePath);
			String jsonDirPath = STConstant.OUTPUT_DIR+apiName+"/";
			// Create directory to store/save JSON payload
			FileOperation.createDirectory(jsonDirPath);
			//Read config map values
			Map<String, Object> configMap = (Map<String, Object>) registrationConfig.get(apiName);
			if(configMap == null || configMap.isEmpty()){
				System.out.println("Input Configuaration not found for API :: "+apiName);
				continue;
			}
			CombinationGenerator.createCombinationAndPayload(configMap, templatePath, jsonDirPath);
			//Need to replace map values with payload
			//configMaps.put(apiName, configMap);
			WebData webData = apiDataMap.get(apiName);
			File directory = new File(STConstant.OUTPUT_DIR+apiName+"/");
			if(directory != null && directory.exists()){
				webData.setPayloadList(Arrays.asList(directory.list()));
			}
		}
		String iteration = (String) registrationConfig.get("iteration");
		if(iteration != null && !iteration.isEmpty()){
			NoOfIteration = Integer.valueOf(iteration);
		}
		processRegistration(NoOfIteration);
		System.out.println("Creating Test Report File...");
		FileOperation.writeAllDataToFile(STConstant.PARENT_DIR+productName+"_System_Test_Report.csv", reportContent.toString());
		System.out.println("Created Test Report...");
	}

	/**
	 * This method is populate all the replacement keys and values pair for all API's
	 * @param apiName
	 * @return
	 */
	private Map<String, Object> populateInputData(String apiName){
		String replaceString = apiDataMap.get(apiName).getKeysToReplace();
		if("NA".equalsIgnoreCase(replaceString)){
			logger.info("No keys found to replace in api name - "+apiName);
			System.out.println("No keys found to replace in api name - "+apiName);
			return null;
		}else {
			String[] keyValuePairs = replaceString.split(STConstant.COMMA_DELIMITER);
			Map<String, Object> updateJsonKeys = new HashMap<String, Object>();
			for(String keyValue : keyValuePairs){
				String[] keys = keyValue.split(STConstant.COLON);
				if(keys.length != 3){
					logger.error("Keys and values are mismatched in api name - "+apiName);
					System.out.println("Keys and values are mismatched in api name - "+apiName);
					continue;
				}
				String responseStr = apiDataMap.get(keys[1].trim()).getResponse();
				if(responseStr != null && !responseStr.isEmpty()){
					String Value = JsonUtility.getJsonValue(responseStr, keys[2].trim());
					if(Value != null && !Value.isEmpty()){
						updateJsonKeys.put(keys[0].trim(), Value);
					}
				}
			}
			return updateJsonKeys;
		}
	}

	/**
	 * Get the payload from combination generator path, If not found then get payload template.
	 * @param apiName
	 * @param index - if more file are available, than get based on index value
	 * @return
	 */
	private String getPayload(String apiName, int fileIndex){
		String payload = null;
		String filePath = null;
		List<String> fileList = apiDataMap.get(apiName).getPayloadList();
		try {
			if(fileList == null || fileList.isEmpty()){
				filePath = STConstant.PAYLOAD_TEMPLATE_DIR+productName+STConstant.FRONT_SHLASH_DELIMITER+apiName+"_Payload.txt";
				payload = FileOperation.readInputfile(filePath);
			} else {
				int totalFiles = fileList.size();
				if(fileIndex >= totalFiles){
					fileIndex = fileIndex % totalFiles;
				}
				filePath = STConstant.OUTPUT_DIR+apiName+"/"+fileList.get(fileIndex);
				payload = FileOperation.readInputfile(filePath);
			}
		} catch (IOException e) {
			System.out.println("Payload file not found in given path:: "+filePath);
			logger.info("Payload file not found in given path:: "+filePath);
		}
		return payload;
	}

	/**
	 * This method will get the all the required configuration data and process web/request call.
	 */
	private void processRegistration(int NoOfIteration){
		logger.info("**************************Report for "+productName+" date on :: "+new Date()+" **************************");
		System.out.println("\n**************************Report for "+productName+" date on :: "+new Date()+" **************************");
		for(int index = 0; index < NoOfIteration; index++){
			successCount = 0;
			StringBuilder iteration = new StringBuilder(productName).append(":: ").append(index+1);
			System.out.println(iteration);
			logger.info(iteration);
			for (String apiName: apiList) {
				String response = initWebCallProcess(apiName, index);
				if(STConstant.FAIL.equalsIgnoreCase(response)){
					generateReport();
					break;
				}
				reportContent.append("PASS,");
				successCount++;
			}
			reportContent.append(System.lineSeparator());
			System.out.println(LINE);
			logger.info(LINE);
		}
		
		logger.info("**************************Report created for "+productName+" date on :: "+new Date()+" **************************");
		System.out.println("**************************Report created for "+productName+" date on :: "+new Date()+" **************************");
	}
	
	private void generateReport(){
		int failCount = apiList.size() - successCount;
		for (int i = 0; i < failCount; i++) {
			reportContent.append("FAIL,");
		}
	}
	
	private String initWebCallProcess(String apiName, int fileIndex){
		boolean isPayload = true;
		WebData webData = apiDataMap.get(apiName);
		String payload = getPayload(apiName, fileIndex);
		boolean isXmlContentType = false;
		if("xml".equalsIgnoreCase(webData.getContentType())){
			isXmlContentType = true;
		}
		Map<String, Object> updateJsonMap = populateInputData(apiName);
		if(!isXmlContentType && payload != null && updateJsonMap != null && !updateJsonMap.isEmpty()){
			JSONObject payloadObject = JSONObject.fromObject(payload);
			JsonUtility.updateJsonPayload(payloadObject, updateJsonMap, null);
			payload = payloadObject.toString();
		}
		if(payload == null){
			if("Read".equalsIgnoreCase(webData.getOperation()) && !"mock".equalsIgnoreCase(webData.getOperation())){
				try{
					if(updateJsonMap != null){
						payload = (String)updateJsonMap.values().toArray()[0];
					}
				} catch(Exception ex){
					System.out.println("Invalid response.");
				}
				isPayload = false;
			}
		}
		if(isXmlContentType){
			payload = changeXMLData(payload, updateJsonMap);
		}
		System.out.println("ApiName:: "+apiName+"\nPayload:: "+payload);
		if(payload != null && payload.indexOf("unique_id") > 0){
			String uniqueNo = StringUtility.getUniqueNumber()+"";
			StringBuilder tempPayload = new StringBuilder(payload);
			StringUtility.replaceString(tempPayload, "unique_id", uniqueNo);
			payload = tempPayload.toString();
			System.out.println("Updated Payload ::"+payload);
		}
		//call API and set the response to map.
		String response = new WebCall().initRestCall(webData, isPayload, payload);
		if(response == null || response.isEmpty()){
			System.out.println(apiName+ "Response is null");
		}
		webData.setResponse(response);
		String responseStatusKey = webData.getResponseStatusKey();
		if(responseStatusKey != null && !responseStatusKey.isEmpty()){
			response = validateResponseStatus(response, responseStatusKey);
		}
		//System.out.println("Response:: "+response+"\n");
		//logger.info("ApiName:: "+apiName+"\nPayload:: "+payload+"\nResponse:: "+response+"\n");
		return response;
	}
	
	/** check the response based on status configured in configuration file
	 * @param response
	 * @param responseStatusKey
	 * @return
	 */
	private String validateResponseStatus(String response, String responseStatusKey){
		String statusKeyWithValue[] = responseStatusKey.split(STConstant.HIPHEN);
		int matchedCount = 0;
		for(int index = 0; index < statusKeyWithValue.length; index++){
			String keyWithValue[] = statusKeyWithValue[index].split(STConstant.COLON);
			if(keyWithValue.length == 2){
				String value = JsonUtility.getJsonValue(response, keyWithValue[0]);
				if(value != null && value.equalsIgnoreCase( keyWithValue[1])){
					matchedCount++;
				}
			}
		}
		if(matchedCount == statusKeyWithValue.length){
			return response;
		}
		return STConstant.FAIL;
	}

	/**
	 * Iterate Map and change/update the XML payload for given keys and values.
	 * @param payload
	 * @param updateJsonKeys
	 * @return
	 */
	private String changeXMLData(String payload, Map<String, Object> updateJsonKeys){
		StringBuilder sbPayload = new StringBuilder(payload);
		for(Map.Entry<String, Object> entry : updateJsonKeys.entrySet()){
			StringUtility.replaceString(sbPayload, STConstant.COLON+entry.getKey(), ""+entry.getValue());
		}
		return sbPayload.toString();
	}


}
