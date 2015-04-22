package com.tecnotree.stf.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.tecnotree.stf.common.PaymentMethod;

public class JsonUtility {

	public static boolean compareJsonPayload(String inputJson, String responseJson){
		System.out.println("inputJson:: "+inputJson);
		System.out.println("responseJson:: "+responseJson);
		boolean isEqual = false;
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tree1 = mapper.readTree(inputJson);
			JsonNode tree2 = mapper.readTree(responseJson);
			isEqual =  tree1.equals(tree2);
		}catch (IOException e) {
			e.printStackTrace();
		}
		return isEqual;
	}
	
	static public JSONObject getJsonObjectByCompletePathKey(String jsonStr, String keyStr){
		String[] keys = keyStr.split("\\.");
		JSONObject jObject = JSONObject.fromObject(jsonStr);
		for(String key: keys){
			if(jObject.has(key)){
				if(key.equalsIgnoreCase(keys[keys.length-1]) ){
					break;
				}
				jObject = getJsonObject(jObject, key);
			}
		}
		return jObject;
	}
	
	static public JSONObject getJsonObject(JSONObject jObject, String exactKey){
		Object jObjX = jObject.get( exactKey );
		if ( jObjX.getClass().getName().contains("JSONObject") == true ) {
			jObject = jObject.getJSONObject(exactKey);
		} else if ( jObjX.getClass().getName().contains("JSONArray") == true ) {
			JSONArray jArray = (JSONArray)jObjX;
			Object objAr = jArray.get(0);
			if ( objAr.getClass().getName().contains("JSONObject") == true ) {
				jObject = (JSONObject)objAr;
			}
		}
		return jObject;
	}
	
	static public String getJsonValue(String jsonStr, String keyStr){
		String[] keys = keyStr.split("\\.");
		JSONObject jObject = null;
		try{
			jObject = JSONObject.fromObject(jsonStr);
		} catch(JSONException je){
			System.out.println("JSON is not Valid ::" +jsonStr);
			return null;
		}
		String lastKey = "";
		for(String key: keys){
			if(jObject.has(key)){
				lastKey = key;
				if(key.equalsIgnoreCase(keys[keys.length-1]) ){
					break;
				}
				jObject = getJsonObject(jObject, key);
			}
		}
		if(jObject.has(lastKey)){
			return jObject.getString(lastKey);
		}else {
			return null;
		}
	}
	
	static public void updateJsonPayload( JSONObject jObject , Map<String,Object> uniqueField , String key ) {
		Iterator itr =  jObject.keys();
		List<String> strList = new ArrayList<String>();
		while ( itr.hasNext() == true ) {
			strList.add( (String)itr.next() );
		}
		for ( String strX : strList ) {
			String keyStr = null; 
			if ( key != null ) { 
				keyStr = key + "." + strX;
			} else {
				keyStr = strX;
			}
			if ( uniqueField.get( keyStr ) != null ) {
				Object jObjX = jObject.remove( strX );
				jObject.put( strX , uniqueField.get(keyStr) );
				continue;
			}
			Object jObjX = jObject.get( strX );
			if ( jObjX.getClass().getName().contains("JSONObject") == true ) {
				updateJsonPayload( (JSONObject)jObjX , uniqueField , keyStr );
			} else if ( jObjX.getClass().getName().contains("JSONArray") == true ) {
				JSONArray jArray = (JSONArray)jObjX;
				for ( int i = 0; i < jArray.size(); i++ ) {
					Object objAr = jArray.get(i);
					if ( objAr.getClass().getName().contains("JSONObject") == true ) {
						updateJsonPayload( (JSONObject)objAr , uniqueField , keyStr );
					}
				}
			}
		}
	}
	
	
	static public void modifyJsonPayload(JSONObject jObject, Map<String, ArrayList<String>> m, String key,
			Map<String, Map<String, String>> keyval) {

		Iterator itr = jObject.keys();
		List<String> strList = new ArrayList<String>();

		while (itr.hasNext() == true) {
			strList.add((String) itr.next());
		}
		for (String strX : strList) {
			String keyStr = null;
			if (key != null) {
				keyStr = key + "." + strX;
			} else {
				keyStr = strX;
			}

			if (m.get(keyStr) != null) {

				Object jObjX1 = jObject.get(strX);

				if (jObjX1.getClass().getName().contains("JSONArray") == true) {
					arrayRemove(jObject, keyStr, strX, m, keyval);
				}
				if (jObjX1.getClass().getName().contains("JSONArray") == false) {
					String s1 = m.get(keyStr).get(0);
					if (s1 != null) {
						Object jObjX = jObject.remove(strX);
						// jObject.put(strX, m.get(keyStr));
						jObject.put(strX, s1);
						continue;
					}
				}
			}
			Object jObjX = jObject.get(strX);
			if (jObjX.getClass().getName().contains("JSONObject") == true) {
				modifyJsonPayload((JSONObject) jObjX, m, keyStr, keyval);
			} else if (jObjX.getClass().getName().contains("JSONArray") == true) {
				JSONArray jArray = (JSONArray) jObjX;
				for (int i = 0; i < jArray.size(); i++) {
					Object objAr = jArray.get(i);
					if (objAr.getClass().getName().contains("JSONObject") == true) {
						modifyJsonPayload((JSONObject) objAr, m, keyStr, keyval);
					}
				}
			}
		}
	}

	public static void arrayRemove(JSONObject jObject, String keyStr, String strX, Map<String, ArrayList<String>> m,
			Map<String, Map<String, String>> keyval) {

		ArrayList<String> al = null;

		for (Entry<String, ArrayList<String>> entry : m.entrySet()) {
			al = entry.getValue();

		}
		JSONArray arr = new JSONArray();
		Object jObjX1 = jObject.get(strX);
		if (jObjX1.getClass().getName().contains("JSONArray") == true) {
			JSONArray jArray = (JSONArray) jObjX1;
			int size = jArray.size();
			for (int i = 0; i < size; i++) {
				Object objAr = jArray.get(i);
				JSONObject js = (JSONObject) objAr;
				for (String al1 : al) {
					if (js.containsValue(al1)) {
						Map<String, String> findvar = new HashMap<String, String>();
						findvar = keyval.get(al1);
						for (Entry<String, String> entry : findvar.entrySet()) {
							Map<String, Object> findvarchange = new HashMap<String, Object>();
							findvarchange.put(entry.getKey(), entry.getValue());
							updateJsonPayload(js, findvarchange, null);
						}
						arr.add(js);
					}
				}
			}
		}
		if (arr.size() > 0) {
			jObject.remove(keyStr);
			jObject.put(strX, arr);
		}
	}


	public static void main(String[] args) throws IOException {
		String fmp = "D:/MakeRegistrationPayment_Payload.txt";
		//String fpd = "D:/PaymentMethod_Payload.txt";
		String mpJson = FileOperation.readInputfile(fmp);
		JSONObject mpJsonObj = JSONObject.fromObject(mpJson);
		//String pdJson = FileOperation.readInputfile(fpd);
		System.out.println("BEFORE " + mpJsonObj);
		//call new method
		Map<String, String> paymentMap = new HashMap<String, String>();
		paymentMap.put("CASH","0");paymentMap.put("CHEQUE", "1");
		paymentMap.put("CREDITCARD", "2");paymentMap.put("DEBITCARD", "3");
		Object pdddObj = PaymentMethod.getPaymentMethods("CASH,CHEQUE", paymentMap);
		Map<String, Object> uniqueField = new HashMap<String, Object>();
		System.out.println("pdddObj::"+pdddObj);
		uniqueField.put("makePayment.paymentDetails.paymentMethod", pdddObj);
		JsonUtility.updateJsonPayload(mpJsonObj, uniqueField, null);
		System.out.println("AFTER " + mpJsonObj);
		//JSONObject j = getJsonObjectByCompletePathKey(jsonInput, "makePayment.paymentDetails.paymentMethod");
		//JSONObject j = JSONObject.fromObject(jsonInput1);
		//System.out.println("After " + j);
		
	}

}
