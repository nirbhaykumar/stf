package com.tecnotree.stf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public class FileOperation {
	
	private static final Logger logger = Logger.getLogger(FileOperation.class);
	private static FileWriter writer = null;

	public static FileWriter getFileWriter(){
		return writer;
	}
	
	public static void createDirectory(String dirPath){
		File jsonDir = new File(dirPath);
		if (!jsonDir.exists()) {
			if (jsonDir.mkdirs()) {
				logger.debug(dirPath+" Directory has been created!");
			} else {
				logger.debug("Failed to create directory "+dirPath);
			}
		}
	}
	
	public static void deleteFile(String dirPath){
		File file = new File(dirPath);
		if(file.isDirectory()){
			String fileList[] = file.list();
			for(String fName: fileList) {
				String fullPath = file.getPath()+"\\"+fName;
				deleteFile(fullPath);
			}
			System.out.println(file.getPath()+ " has been deleted.");
			logger.info(file.getPath()+ " has been deleted.");
			file.delete();
		}else {
			System.out.println(file.getPath()+ " has been deleted.");
			logger.info(file.getPath()+ " has been deleted.");
			file.delete();
		}
	}
	
	public static void createFile(String fileName, boolean isAppend){
		try {
			//if(writer == null){
				logger.info("Creating file writer object");
				writer = new FileWriter(fileName, isAppend);
			//}
		}
		catch(IOException e) {
			logger.error("Excetion to create file "+fileName+" ::"+e);
			e.printStackTrace();
		} 
	}

	public static void writeDataToFile(String content){
		try {
			if(writer == null){
				logger.error("Not able to write, file stream is closed.");
				return;
			}
			writer.append(content);
		}
		catch(IOException e) {
			closeFile(writer);
			logger.error("file stream has closed."+e);
			e.printStackTrace();
		} 
	}

	public static void closeFile(FileWriter writer){
		try {
			if(writer == null) return;
			writer.flush();
			writer.close();
			writer = null;
		} catch (IOException e) {
			logger.error("Error to close file ::"+e);
			e.printStackTrace();
		}

	}
	
	public static void writeAllDataToFile(String fileName, String content){
		try {
			FileWriter writer = new FileWriter(fileName);
			writer.append(content);
			writer.flush();
			writer.close();
		}
		catch(IOException e) {
			logger.error("Error to close file ::"+e);
			e.printStackTrace();
		} 
	}
	
	public static String readInputfile(String inputFile) throws IOException{
		String line = null;
		BufferedReader input = new BufferedReader(new FileReader(inputFile));
		StringBuilder sb = new StringBuilder();
		while (( line = input.readLine()) != null) {
			sb.append(line);
		}
		if(sb != null){
			return sb.toString();
		}
		return null;
	}
}
