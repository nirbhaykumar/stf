package com.tecnotree.stf.main;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class SystemTestExecuter {
	
	final static Logger logger = Logger.getLogger(SystemTestExecuter.class);
	
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("log4j.properties");
		System.out.println("SystemTestExecuter Start.");
		logger.info("SystemTestExecuter Start.");
		Resource resource = new FileSystemResource("SystemTestingConfiguration.xml");
		BeanFactory context = new XmlBeanFactory(resource);
		logger.info("Reading SystemTestingConfiguration.xml.");
		SystemTest objSysTest = (SystemTest) context.getBean("systemTest");
		objSysTest.execute();
		System.out.println("SystemTestExecuter End.");
	}
}
