package tech.seedhk.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log {
	
	
	public static Logger getInstance(Class<?> clazz){
		PropertyConfigurator.configure("resource/log4j.properties");
		Logger log=Logger.getLogger(clazz);
		return log;
	}
	
	
	public static void main(String[] args) {
		
		Logger log=getInstance(Log.class);
		log.info("info");
		log.warn("info");
		log.error("info");
		log.debug("info");
		System.out.println(log.getLevel());
		
	}


}
