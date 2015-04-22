package com.tecnotree.stf.util;

import java.util.Random;


public class StringUtility {
	
	/**
	 * Utility method to replace the string from StringBuilder.
	 * @param sb          the StringBuilder object.
	 * @param toReplace   the String that should be replaced.
	 * @param replacement the String that has to be replaced by.
	 * 
	 */
	public static void replaceString(StringBuilder sb, String toReplace, String replacement) {      
		int index = -1;
		while ((index = sb.lastIndexOf(toReplace)) != -1) {
			sb.replace(index, index + toReplace.length(), replacement);
		}
	}
	
	//Set directory path based on OS
	public static void setDefaultDirPath(){
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.indexOf("win") >= 0) {
			STConstant.OUTPUT_DIR = "c:/stf/output/";
			STConstant.PAYLOAD_TEMPLATE_DIR = "c:/stf/payloadTemplate/";
		} else {
			STConstant.OUTPUT_DIR = "/home/tecnotree/stf/output/";
			STConstant.PAYLOAD_TEMPLATE_DIR = "/home/tecnotree/stf/payloadTemplate/";
		}
		System.out.println("Output Directory Path::"+STConstant.OUTPUT_DIR+"\t Input Payload Directory Path::"+STConstant.PAYLOAD_TEMPLATE_DIR);
		System.out.println("Default directory path has been created.");
	}
	
	public static int getUniqueNumber(){
		Random rand = new Random();  
		return rand.nextInt(100000);
	}

}
