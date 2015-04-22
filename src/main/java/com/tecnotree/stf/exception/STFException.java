package com.tecnotree.stf.exception;

public class STFException extends RuntimeException {
	
	public static final String OPERATION = "OPERATION IS NULL/EMPTY";
	public static final String SIM_NUMBER_IS_NULL = "SIM NUMBER IS NULL/EMPTY";
	public static final String IMSI_NUMBER_IS_NULL = "IMSI NUMBER IS NULL/EMPTY";
	public static final String SERVICE_NUMBER_IS_NULL = "SERVICE NUMBER IS NULL/EMPTY";
	
	public STFException() {
		super();
	}

	public STFException(String msg) {
		super(msg);
	}
	

}
