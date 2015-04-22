package com.tecnotree.stf.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.tecnotree.stf.util.FileOperation;
import com.tecnotree.stf.util.STConstant;
import com.tecnotree.stf.util.StringUtility;

public class CombinationGenerator {
	
	final static Logger logger = Logger.getLogger(CombinationGenerator.class);
	List<GraphNodeHolder> aList = new ArrayList<GraphNodeHolder>();
	static String outputJsonPath = null;

	public void run(Map<String, Object> combinationMap, String templatePath) throws Exception {
		StringBuilder csvHeader = new StringBuilder();
		for (Entry<String, Object> entry : combinationMap.entrySet()){
			logger.info("key:: "+entry.getKey() + "\t Value:: " + entry.getValue());
			csvHeader.append(entry.getKey()).append(STConstant.HIPHEN_DELIMITER);
			GraphNodeHolder gh = new GraphNodeHolder();
			gh.name = entry.getKey();
			aList.add( gh );
			String [] strAX1 = entry.getValue().toString().split(STConstant.HIPHEN_DELIMITER);
			for ( int i = 0; i < strAX1.length; i++ ) {
				if ( gh.aList == null )  gh.aList = new ArrayList<Node>();
				Node node = new Node();
				node.name = strAX1[i];
				gh.aList.add( node );
			} 
		}
		System.out.println("Header:: "+csvHeader);
		PayloadGenerator.readJsonTemplate(csvHeader.toString(), templatePath);
		//FileOperation.writeDataToFile(csvHeader+System.lineSeparator());
		
		createGraph();
		printGraph( aList.get(0) );
	}

	private void printGraph( GraphNodeHolder gn ) {
		for ( Node nodeX : gn.aList ) {
			StringBuilder sb = new StringBuilder();
			sb.append( sb.toString() );
			printGraph( nodeX , sb );
		}
	}

	static int sequence = 1;
	private void printGraph( Node nodeX , StringBuilder combinationStr ) {
		combinationStr.append(nodeX.name).append(STConstant.HIPHEN_DELIMITER);
		if ( nodeX.childList == null ) {
			//StringBuilder prepend = new StringBuilder("OFFER "+sequence+STConstant.COMMA_DELIMITER+"CODE"+sequence);
			//StringUtility.replaceString(combinationStr, "unique_id", sequence+"");
			//System.out.println("Combination "+sequence+":: "+combinationStr);
			PayloadGenerator.createJsonPayload(combinationStr.toString(), outputJsonPath, sequence);
			//FileOperation.writeDataToFile(combinationStr.append(System.lineSeparator()).toString());
			logger.info("Combination "+sequence+":: "+combinationStr);
			sequence++;
			return;
		}
		for ( Node nodeXX : nodeX.childList ) {
			StringBuilder sb1 = new StringBuilder();
			sb1.append( combinationStr.toString() );
			printGraph( nodeXX , sb1 );
		}
	}

	private void createGraph() {
		//Collections.sort( aList , new MyCompare() );
		for ( int i = 1; i < aList.size(); i++ ) {
			GraphNodeHolder ghPrev = aList.get ( i - 1 ); 
			GraphNodeHolder ghCur = aList.get ( i ); 
			for ( Node nodeP : ghPrev.aList ) {
				for ( Node nodeX : ghCur.aList ) {
					if ( nodeP.childList == null ) nodeP.childList = new ArrayList<Node>();
					nodeP.childList.add( nodeX );
				}
			} 
		}
	}

	public static void createCombinationAndPayload(Map<String, Object> combinationMap, String templatePath, String jsonPath) {
		sequence = 1;
		outputJsonPath = jsonPath;
		logger.info("Creating File ::"+templatePath);
		try {
			new CombinationGenerator().run(combinationMap, templatePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(sequence-1+" Combinations have been created. !");
	}

	class GraphNodeHolder {
		public String name;
		public List<Node> aList;
	}

	class Node {
		public String name;
		List<Node> childList = null;
	}

	class MyCompare implements Comparator<GraphNodeHolder> {
		public int compare(GraphNodeHolder toObj1, GraphNodeHolder toObj2) {
			return toObj1.aList.size() - toObj2.aList.size();
		}
	}
	
	static Map<Integer, String> technicalChannelMap = new HashMap<Integer, String>();
	static private int id = 1;
	static void createTechnicalChannelCombinations(int totalItems, int noOfItems, int[] arrTC, int temp) {
		File file = new File(STConstant.OUTPUT_DIR);
		if (!file.exists()) {
			file.mkdir();
			System.out.println("file::"+file.getPath());
		}
		for (int index = totalItems; index >= noOfItems; index --) {
	    	StringBuilder sb = new StringBuilder();
	    	sb.append("ResourceName"+id+","+"ResourceCode"+id+",");
	    	arrTC[noOfItems - 1] = index;
	        if (noOfItems > 1) { 
	        	createTechnicalChannelCombinations(index - 1, noOfItems - 1, arrTC, temp);
	        } else {
	        	StringBuilder sbTemp = new StringBuilder();
	            for (int subIndex = 0; subIndex < temp; subIndex ++) {
	            	sbTemp.append("{\"code\": \""+technicalChannelMap.get(arrTC[subIndex])+"\"}|");
	            }
	            sb.append(sbTemp);
	            String tc = sb.substring(0, sb.length()-1);
	            tc = tc.concat(System.lineSeparator());
	            // TODO write code to read resource_RA template file and change the values dynamically.
	            System.out.println(tc);
	            FileOperation.writeDataToFile(tc);
	            logger.info("Technical Channels ::"+tc);
	            id++;
	        }
	    }
	}
	
	static public void generateTcCombination(String technicalChannels, int noOfItems, String combinationFilePath){
		String[] technicalChannel = technicalChannels.split(STConstant.COMMA_DELIMITER);
	    int[] arr = new int[noOfItems];
	    for (int itemIndex = 0; itemIndex < technicalChannel.length; itemIndex++) {
	    	technicalChannelMap.put(itemIndex+1, technicalChannel[itemIndex]);
		}
	    logger.info("Creating File ::"+combinationFilePath);
	    FileOperation.createFile(combinationFilePath, false);
	    //TODO need to change resource file header constant value
	    //FileOperation.writeDataToFile(STConstant.RESOURCE_FILE_HEADER+System.lineSeparator());
	    createTechnicalChannelCombinations(technicalChannel.length, noOfItems, arr, noOfItems);
	    FileOperation.closeFile(FileOperation.getFileWriter());
	    logger.info("File has been closed ::"+combinationFilePath);
	    logger.info(id-1+" Technical Combinations have been created. !");
	    System.out.println(id-1+" Technical Combinations have been created. !");
	}
	
	public static void main(String[] args){
		String technicalChannels = "3G,LTE,lob,IaaS,Paas,SaaS,CDMA,GSM,WiMax,FibretoHome,DSL,FXL,DTH,IPTV";
		int noOfItems= 2;
		CombinationGenerator.generateTcCombination(technicalChannels, noOfItems,"");
	}
}
