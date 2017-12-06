package tech.seedhk.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import sun.misc.Unsafe;


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

}
