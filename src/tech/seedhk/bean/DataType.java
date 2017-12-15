package tech.seedhk.bean;

public class DataType {
	
	private static final String text="text";
	
	private static final String file="file";
	
	private static final String rpc="rpc";
	
	
	public static java.nio.ByteBuffer string2Buffer(String type){
		
		return java.nio.ByteBuffer.wrap(type.getBytes());
	}

}
