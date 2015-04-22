package com.tecnotree.stf.common;

import java.io.IOException;
import java.util.Map;

import com.tecnotree.stf.util.FileOperation;
import com.tecnotree.stf.util.STConstant;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PaymentMethod {
	
	
	public static Object getPaymentMethods(String methodTypes, Map<String, String> paymentMap){
		
		String fileName = STConstant.PAYLOAD_TEMPLATE_DIR+"PaymentMethod_Payload.txt";
		String paymentMethodJson = null;;
		try {
			paymentMethodJson = FileOperation.readInputfile(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = JSONObject.fromObject(paymentMethodJson);
		JSONArray jArray = (JSONArray) jsonObject.get("paymentMethod");
		JSONArray jArrayRequired = new JSONArray();
		String[] methodType = methodTypes.split(",");
		for (int i = 0; i < methodType.length; i++) {
			if(paymentMap.containsKey(methodType[i])){
				Integer index = Integer.valueOf(paymentMap.get(methodType[i]));
				jArrayRequired.add(jArray.get(index));
			}
		}
		return (Object) jArrayRequired;
	}

}
