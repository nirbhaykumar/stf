package com.tecnotree.stf.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.tecnotree.stf.util.FileOperation;
import com.tecnotree.stf.util.JsonUtility;
import com.tecnotree.stf.util.STConstant;

public class PayloadGenerator {

	private static final Logger logger = Logger.getLogger(PayloadGenerator.class);
	static String[] fields = null;
	static String jsonTemplate = null;


	public static void readJsonTemplate(String header, String templatePath)  {
		logger.info("Start to generate JSON Payload");
		logger.info("templatePath ::"+templatePath+"\t field header ::"+ header);
		if(header != null){
			fields = header.split(STConstant.HIPHEN_DELIMITER);
		}
		try {
			jsonTemplate = FileOperation.readInputfile(templatePath);
		} catch (IOException e) {
			System.out.println("Problem to read file, File name:: "+templatePath+"\n"+e);
			logger.error("Problem to read file, File name:: "+templatePath+"\n"+e);
		}
	}
	
	
	public static void createJsonPayload(String combinationStr, String outputJsonPath, int sequence){
		JSONObject jObject = JSONObject.fromObject(jsonTemplate);
		Map<String,Object> jsonKeyValue = new  HashMap<String,Object>();
		String[] values = combinationStr.split(STConstant.HIPHEN_DELIMITER);
		String fName = outputJsonPath+values[0]+"_"+sequence+STConstant.JSON_FILE_EXTENSION;
		//Replacing all the values
		for (int i = 0; i < values.length; i++) {
			jsonKeyValue.put(fields[i].trim(),  values[i].trim());
		}
		JsonUtility.updateJsonPayload(jObject , jsonKeyValue , null);
		FileOperation.writeAllDataToFile(fName, jObject.toString());
		logger.info("File has written successfully ::"+fName);
		System.out.println("File has written successfully ::"+fName);
	}

	//This method not being used, becoz not generating combination csv file
	private void readJsonPaylaod(String combinationFilePath, String jsonTemplate, String outputJsonPath){
		try {
			BufferedReader input = new BufferedReader(new FileReader(combinationFilePath));
			String line = null;
			String[] fields = null;
			//reading csv file line by line
			int lineCounter = 0;
			JSONObject jObject = JSONObject.fromObject(jsonTemplate);
			Map<String,Object> jsonKeyValue = new  HashMap<String,Object>();
			while ((line = input.readLine()) != null) {
				lineCounter++;
				if(lineCounter == 1){
					fields = line.split(STConstant.COMMA_DELIMITER);
					//Remove last comma
					continue;
				}
				String[] values = line.split(STConstant.COMMA_DELIMITER);
				String fName = outputJsonPath+values[0]+STConstant.JSON_FILE_EXTENSION;
				//Replacing all the values
				for (int i = 0; i < values.length; i++) {
					jsonKeyValue.put(fields[i].trim(),  values[i].trim());
				}
				JsonUtility.updateJsonPayload(jObject , jsonKeyValue , null);
				FileOperation.writeAllDataToFile(fName, jObject.toString());
				logger.info("File has written successfully ::"+fName);
				System.out.println("File has written successfully ::"+fName);
			}
		}catch (IOException ex){
			logger.error("Not able to read Combination file/JSON payload file :: "+ex);
			System.out.println("Reading problem."+ex);
		}
	}
	
	//This method not being used, becoz not generating combination csv file
	public static void generateJsonPayload(String templatePath, String combinationFilePath, String outputJsonPath)  {
		logger.info("Start to generate JSON Payload");
		logger.info("templatePath ::"+templatePath+"\t combinationFilePath ::"+ combinationFilePath+"\t outputJsonPath ::"+ outputJsonPath);
		//String jsonTemplate = FileOperation.readInputfile(templatePath);
		new PayloadGenerator().readJsonPaylaod(combinationFilePath, jsonTemplate, outputJsonPath);
	}
}
