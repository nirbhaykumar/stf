package com.tecnotree.stf.main;

import java.util.Map;

import org.apache.log4j.Logger;

import com.tecnotree.stf.util.STConstant;
import com.tecnotree.stf.util.StringUtility;

public class SystemTest {

	final static Logger logger = Logger.getLogger(SystemTest.class);
	private Map<String, Map<String, Object>> inputConfigDataMap;
	private Map<String, Map<String, Object>> systemTestConfig;
	private Map<String, Object> stfConfigMap;
	private Map<String, Object> mockConfigMap;

	public void setInputConfigDataMap(Map<String, Map<String, Object>> inputConfigDataMap) {
		this.inputConfigDataMap = inputConfigDataMap;
	}
	
	public void setSystemTestConfig(Map<String, Map<String, Object>> systemTestConfig) {
		this.systemTestConfig = systemTestConfig;
	}

	public void execute(){
		
		logger.info("inputConfigDataMap :: "+inputConfigDataMap);
		logger.info("systemTestConfig :: "+systemTestConfig);
		
		stfConfigMap = systemTestConfig.get("STF_CONFIG");
		logger.info("stfConfigMap :: "+stfConfigMap);
		mockConfigMap = systemTestConfig.get("MOCK_CONFIG");
		logger.info("mockConfigMap :: "+mockConfigMap);

		Map<String, Object> registrationConfig = inputConfigDataMap.get(STConstant.REGISTRATION_CONFIG);
		logger.info("registrationConfig :: "+registrationConfig);
		
		Map<String, Object> oauthConfig = inputConfigDataMap.get(STConstant.OAUTH_CONFIG);
//		logger.info("oauthConfig :: "+oauthConfig);

		//Set directory path based on OS
		StringUtility.setDefaultDirPath();
		
		new Processor(stfConfigMap, mockConfigMap, registrationConfig, oauthConfig).registrationWithTestReport();
	}
}
