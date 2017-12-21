package tech.seedhk.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


public class Test {

	@org.junit.Test
	public void test() throws Exception {
		RandomAccessFile raf=new RandomAccessFile("d:/test.txt", "rw");
		for(int i=0;i<10;i++){
			raf.writeInt(i);
		}
		raf.close();
		raf=new RandomAccessFile("d:/test.txt", "rw");
		raf.seek(3);
		for(int i=0;i<10;i++){
			int readInt = raf.readInt();
			System.out.println(readInt);
		}
		raf.close();
		
	}
	
	@org.junit.Test
	public void testMapperFile() throws Exception{
		
		long start=System.currentTimeMillis();
		
		int length=0x8000000;
		
		FileChannel fc=new RandomAccessFile("d:/1.dat", "rw").getChannel();
		MappedByteBuffer mbb=fc.map(FileChannel.MapMode.READ_WRITE, 0, length);
		System.out.println(length);
		
		for(int i=0;i<length;i++){
			mbb.put((byte) i);
		}
		System.out.println("finish");
		
		long middle=System.currentTimeMillis();
		System.out.println("写入花费的时间是："+(middle-start));
		byte[] b=new byte[10];
		
		int j=0;
		for (int i = length / 2; i < length / 2 + 6; i++) {  
            ///System.out.print((int) mbb.get(i));  
			b[j]=mbb.get(i);
            j++;
        }  
        fc.close();  
        
        System.out.println("取数据花时间："+(System.currentTimeMillis()-middle));
	}
	
	@org.junit.Test
	public void testCharBuffer(){
		
		List<String> list=new ArrayList<>();
		for(int i=0;i<10;i++){
			list.add(i+"");
		}
		
		List<String> list2=list;
		
		System.out.println(list.get(1));
		
		list2.set(1, "9");
		
		System.out.println(list.get(1));
		
	}
	
	@org.junit.Test
	public void test2(){
		
		ByteBuffer buffer=ByteBuffer.allocate(10);
		buffer.putInt(12000);
		System.out.println(buffer);
		
		/*
		byte[] b=new byte[]{49,49,49,49};
		
		ByteBuffer buffer=ByteBuffer.wrap(b);
		long a=buffer.getLong();
		System.out.println(a);*/
	}
	
	@org.junit.Test
	public void testByte2String(){
		byte[] b=new byte[]{-17, -69, -65, 35, 105, 110, 99, 108, 117, 100, 101, 60, 115, 116, 100, 105, 111, 46, 104, 62, 13, 10, 35, 105, 110, 99, 108, 117, 100, 101, 60, 
				109, 97, 108, 108, 111, 99, 46, 104, 62, 13, 10, 13, 10, 116, 121, 112, 101, 100, 101, 102, 32, 115, 116, 114, 117, 99, 116, 32, 78, 111, 100, 101, 
				123, 13, 10, 9, 13, 10, 9, 105, 110, 116, 32, 100, 97, 116, 97, 59, 47, 47, 63, 63, 63, 63, 32, 13, 10, 9, 115, 116, 114, 117, 99, 116, 32, 78, 111, 100,
				 101, 32, 42, 32, 112, 78, 101, 120, 116, 59, 9, 47, 47, 63, 63, 63, 63, 63, 63, 63, 63, 63, 32, 13, 10, 13, 10, 125, 78, 79, 68, 69, 44, 42, 80, 78, 79, 
				 68, 69, 59, 13, 10, 13, 10, 80, 78, 79, 68, 69, 32, 99, 114, 101, 97, 116, 101, 78, 111, 100, 101, 40, 41, 123, 13, 10, 9, 13, 10, 9, 80, 78, 79, 68, 69, 32, 110, 111,
				 100, 101, 61, 40, 80, 78, 79, 68, 69, 41, 109, 97, 108, 108, 111, 99, 40, 115, 105, 122, 101, 111, 102, 40, 78, 79, 68, 69, 41, 41, 59, 13, 10, 9, 110, 111, 100, 101, 
				 45, 62, 100, 97, 116, 97, 61, 48, 59, 13, 10, 9, 110, 111, 100, 101, 45, 62, 112, 78, 101, 120, 116, 61, 78, 85, 76, 76, 59, 32, 13, 10, 9, 13, 10, 9, 114, 101, 116,
				 117, 114, 110, 32, 110, 111, 100, 101, 59, 13, 10, 9, 9, 47, 47, 112, 76, 97, 115, 116, 61, 40, 78, 111, 100, 101, 32, 42, 41, 109, 97, 108, 108, 111, 99, 40, 115, 
				 105, 122, 101, 111, 102, 40, 78, 111, 100, 101, 41, 41, 59, 13, 10, 9, 13, 10, 125, 13, 10, 9, 13, 10, 105, 110, 116, 32, 97, 100, 100, 78, 111, 100, 101, 40, 80, 
				 78, 79, 68, 69, 32, 112, 72, 101, 97, 100, 44, 80, 78, 79, 68, 69, 32, 112, 76, 97, 115, 116, 44, 105, 110, 116, 32, 100, 97, 116, 97, 49, 41, 123, 13, 10, 9, 13,
				 10, 9, 9, 47, 47, 49, 46, 63, -48, -74, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 121, 63, 63, 63, 63, 
				 63, 63, 63, 13, 10, 9, 9, 105, 102, 40, 78, 85, 76, 76, 61, 61, 112, 72, 101, 97, 100, 45, 62, 112, 78, 101, 120, 116, 41, 123, 13, 10, 9, 9, 9, 47, 47, 63, 121,
				 63, 63, 63, 63, 63, 63, 63, 13, 10, 9, 9, 9, 80, 78, 79, 68, 69, 32, 112, 61, 40, 80, 78, 79, 68, 69, 41, 109, 97, 108, 108, 111, 99, 40, 115, 105, 122, 101, 111,
				 102, 40, 80, 78, 79, 68, 69, 41, 41, 59, 9, 47, 47, 63, 63, 63, 63, 63, 63, 63, 63, 32, 13, 10, 9, 9, 9, 112, 45, 62, 100, 97, 116, 97, 61, 100, 97, 116, 97, 49, 
				 59, 13, 10, 9, 9, 9, 112, 45, 62, 112, 78, 101, 120, 116, 61, 78, 85, 76, 76, 59, 13, 10, 9, 9, 9, 112, 72, 101, 97, 100, 45, 62, 112, 78, 101, 120, 116, 61, 112, 
				 59, 9, 47, 47, 32, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 13, 10, 9, 9, 9, 112, 76, 97, 115, 116, 61, 112, 59, 9, 
				 9, 47, 47, 63, 63, 63, 63, 63, -50, -78, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 32, 13, 10, 9, 9, 125, 101, 108, 115, 101, 123, 13, 10, 9, 9, 9, 47, 47, 63, 63, 63,
				 63, 63, 63, 63, 63, 63, -50, -78, 63, 63, 63, 13, 10, 9, 9, 9, 80, 78, 79, 68, 69, 32, 112, 61, 40, 80, 78, 79, 68, 69, 41, 109, 97, 108, 108, 111, 99, 40, 115, 105,
				 122, 101, 111, 102, 40, 80, 78, 79, 68, 69, 41, 41, 59, 13, 10, 9, 9, 9, 112, 45, 62, 100, 97, 116, 97, 61, 100, 97, 116, 97, 49, 59, 13, 10, 9, 9, 9, 112, 45, 62, 112,
				 78, 101, 120, 116, 61, 78, 85, 76, 76, 59, 13, 10, 9, 9, 9, 112, 76, 97, 115, 116, 45, 62, 112, 78, 101, 120, 116, 61, 112, 59, 13, 10, 9, 9, 125, 32, 13, 10, 9, 9, 9,
				 13, 10, 125, 32, 13, 10, 105, 110, 116, 32, 108, 105, 115, 116, 78, 111, 100, 101, 40, 80, 78, 79, 68, 69, 32, 112, 72, 101, 97, 100, 41, 123, 13, 10, 13, 10, 9, 13,
				 10, 9, 47, 47, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 13, 10, 9, 80, 78, 79, 68, 69, 32, 110, 111, 100, 101, 61, 112, 72, 101, 97, 100, 59, 13, 10, 9, 
				 119, 104, 105, 108, 101, 40, 78, 85, 76, 76, 33, 61, 110, 111, 100, 101, 45, 62, 112, 78, 101, 120, 116, 41, 32, 123, 13, 10, 9, 9, 110, 111, 100, 101, 61, 110, 
				 111, 100, 101, 45, 62, 112, 78, 101, 120, 116, 59, 13, 10, 9, 9, 112, 114, 105, 110, 116, 102, 40, 34, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 37, 100, 92, 110,
				 34, 44, 110, 111, 100, 101, 45, 62, 100, 97, 116, 97, 41, 59, 13, 10, 9, 125, 13, 10, 13, 10, 125, 13, 10, 13, 10, 105, 110, 116, 32, 109, 97, 105, 110, 40, 41, 123, 
				 13, 10, 9, 13, 10, 9, 47, 42, 13, 10, 9, 49, 46, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 
				 13, 10, 9, 50, 46, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 13, 10, 9, 51, 46,
				 -50, -78, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 32, 13, 10, 9, 13, 10, 9, 42, 47, 32, 13, 10, 9, 13, 10, 9, 13, 10, 9, 80, 78, 79, 68, 69,
				 32, 112, 72, 101, 97, 100, 61, 99, 114, 101, 97, 116, 101, 78, 111, 100, 101, 40, 41, 59, 9, 47, 47, 63, 63, 63, 63, 32, 13, 10, 9, 
				 80, 78, 79, 68, 69, 32, 112, 76, 97, 115, 116, 61, 99, 114, 101, 97, 116, 101, 78, 111, 100, 101, 40, 41, 59, 9, 47, 47, -50, -78, 63, 63, 63, 32, 13, 10, 13, 10,
				 9, 13, 10, 9, 102, 111, 114, 40, 105, 110, 116, 32, 105, 61, 49, 59, 105, 60, 61, 49, 48, 59, 105, 43, 43, 41, 123, 13, 10, 9, 9, 47, 47, 105, 110, 116, 32, 100, 97, 
				 116, 97, 59, 13, 10, 9, 9, 47, 47, 112, 114, 105, 110, 116, 102, 40, 34, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 58, 34, 41, 59, 13, 10, 9, 47, 47, 9, 115, 99, 97,
				 110, 102, 40, 34, 37, 100, 92, 110, 34, 44, 38, 100, 97, 116, 97, 41, 59, 13, 10, 9, 9, 47, 47, 105, 102, 40, 48, 61, 61, 100, 97,
				 116, 97, 41, 13, 10, 9, 9, 47, 47, 9, 98, 114, 101, 97, 107, 59, 13, 10, 9, 9, 112, 114, 105, 110, 116, 102, 40, 34, 63, 63, 63, 63, 63, 63, 63, 63, 63, 37, 100,
				 92, 110, 34, 44, 105, 41, 59, 13, 10, 9, 9, 97, 100, 100, 78, 111, 100, 101, 40, 112, 72, 101, 97, 100, 44, 112, 76, 97, 115, 116, 44, 105, 41, 59, 13, 10, 9, 125, 
				 13, 10, 9, 108, 105, 115, 116, 78, 111, 100, 101, 40, 112, 72, 101, 97, 100, 41, 59, 13, 10, 9, 13, 10, 9, 13, 10, 9, 13, 10, 9, 114, 101, 116, 117, 114, 110, 
				32, 48, 59, 13, 10, 125, 32, 13, 10, 13, 10};
		System.out.println(new String (b));
	}
	
	@org.junit.Test
	public void testFileSave() throws Exception{
		byte[] data=new byte[]{102, 105, 108, 101, 58, 99, 112, 112};
		File file=new File("d:/data.cpp");
		FileOutputStream fos=new FileOutputStream(file);
		fos.write(data);
		fos.flush();
		fos.close();
	}
	
	@org.junit.Test
	public void testDir(){
		System.err.println(System.getProperty("user.dir"));
	}

}
