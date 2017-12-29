package tech.seedhk.buffer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 目的：
 * 	  整合head和body的数据，并根据headBuffer中的分隔符对数据进行分块，分段传输
 * 	内容：
 * 		1.head
 * 		2.body
 * 		3.数据结束符
 * 		4.数据块分隔符
 * @author Seed
 * 2017年12月27日 下午2:06:34
 */
public class DataBuffer implements Serializable{
	/**
	 * 写数据流程：
	 * 	1.准备好数据
	 *  2.将数据写入bodyBuffer的cmd、content、attach中
	 *  3.在最后加上三个字节的结束符
	 *  4.算出bodyBuffer的大小，写入head的body_size中(要去掉末尾的空格)
	 *  5.先发送headBuffer
	 *  6.睡眠1s，对每个segment都进行write操作(是否可行待测试)
	 */
	private Logger log=Logger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	//private static final  byte[] dataSep;	//segment分隔符
	private static final byte[] dataEnd;	//数据结束符
	
	private HeadBuffer head;
	private BodyBuffer body;
	private ByteBuffer buff;//临时存储buffer
	
	static{
		//dataSep="bs_$".getBytes();
		dataEnd="be_$".getBytes();
	}
	
	public void setBody(BodyBuffer body) {
		this.body = body;
	}
	/**
	 * 分割body
	 * @param channel
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void  sendBody(SocketChannel channel){
		
		BodyBuffer buffer=this.body;
		long start=System.currentTimeMillis();
		if(!buffer.isReady()){
			log.error("body尚未准备完毕");
		}else{
			//buff=bean2Buffer(buffer);
			//buff.flip();//wrap得到的buffer无需flip
			try {
				int s1=channel.write(buff);
				//发送数据
				s1+=channel.write(ByteBuffer.wrap(dataEnd));
				log.info("发送数据大小： "+s1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//避免效率低下，不再分段发送数据
			/*int num=0;
			double d=b.length/1024.0;//这里必须加.0，否则会被认为是int类型，取不到小数
			double d2=(double)(num=(int)d);
			num=d>d2?num+1:num;
			for(int i=0;i<b.length;){
				ByteBuffer buf=ByteBuffer.allocate(1027);
				for(int j=i;j<j+1024;j++){
					buf.put(b[i]);
					i=j+1;
				}
				buf.put(dataSep);
				channel.write(buf);//发送数据
				//Thread.sleep(10);//睡眠10毫秒
			}*/
			log.info(new Date()+" 此次body数据发送耗时："+(System.currentTimeMillis()-start));
			
		}
	}
	public void sendHead(SocketChannel channel){
		
		long start=System.currentTimeMillis();
		
		if(this.head==null)
			this.head=new HeadBuffer();
		
		HeadBuffer buffer=this.head;
		
		if(this.body!=null || this.body.isReady()){
			buff=bean2Buffer(this.body);
			int buff_size=buff.limit()+4;
			log.info("body的大小是："+buff_size);
			buffer.setBody_size(buff_size);
			ByteBuffer head_buf=bean2Buffer(buffer);
			
			ByteBuffer data=ByteBuffer.allocate(HeadBuffer.getHeadStart().length+head_buf.limit()+HeadBuffer.getHeadEnd().length);
			data.put(HeadBuffer.getHeadStart());
			data.put(head_buf);
			data.put(HeadBuffer.getHeadEnd());
			data.flip();
			
			log.info("head 的大小是："+data.limit());
			
			try {
				channel.write(data);
				this.body.setReady(true);//准备发送body部分
				//Thread.sleep(100);//睡眠100毫秒
			} catch (IOException e) {
				e.printStackTrace();
			}
			log.info(new Date()+" 此次head数据发送耗时："+(System.currentTimeMillis()-start));
		}else{
			log.error("body尚未准备完毕");
		}
		
		
	}
	
	
	public long getHead(SocketChannel channel){
		ByteBuffer buffer=ByteBuffer.allocate(1024);
		try {
			
			int n=channel.read(buffer);
			log.info("本次拿到的head大小是: "+n);
			//如果head拿到的是0.那么代表有客户端退出
			if(n==0){
				return -1;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		byte[] b=buffer.array();
		byte[] h=new byte[b.length-8];	//减去头部的4个字节和尾部的4个字节
		byte[] head_start=HeadBuffer.getHeadStart();
		byte[] head_end=HeadBuffer.getHeadEnd();
		
		for(int i=0;i<b.length-2;i++){
			if(b[0]==head_start[0] && b[1]==head_start[1] && b[2]==head_start[2]  && b[3]==head_start[3]){
				//head开始
				if(b[i]==head_end[0] && b[i+1]==head_end[1] && b[i+2]==head_end[2] && b[i+3]==head_end[3]){
					//i以前是完整的head
					h=Arrays.copyOfRange(b,4, i);
				}
			}
			
		}
		
		HeadBuffer head_buff=(HeadBuffer) byte2Bean(h);
		
		return head_buff.getBody_size();
	}
	
	public static void main(String[] args) {
		//测试head部分的数据截取问题
		byte[] head=new byte[]{'h','s','_','$','d','a','t','a','h','e','_','$','1','1','0','2','3'};
		byte[] head_start=HeadBuffer.getHeadStart();
		byte[] head_end=HeadBuffer.getHeadEnd();
		byte[] h=new byte[head.length-8];	//减去头部的4个字节和尾部的4个字节
		for(int i=0;i<head.length-3;i++){
			if(head[0]==head_start[0] && head[1]==head_start[1] && head[2]==head_start[2] && head[3]==head_start[3]){
				//head开始
				if(head[i]==head_end[0] && head[i+1]==head_end[1] && head[i+2]==head_end[2] && head[i+3]==head_end[3]){
					//i以前是完整的head
					h=Arrays.copyOfRange(head,4, i);
				}
			}
			
		}
		for(int i=0;i<h.length;i++){
			System.out.print((char)h[i]);
			System.out.print(" ");
		}
		
		
	}
	
	
	public BodyBuffer getBody(int body_size,SocketChannel channel){
		
		ByteBuffer body=ByteBuffer.allocate(body_size);
		int i=-1;
		long sum=0;
		while(sum!=body_size){
			try {
				i=channel.read(body);
			} catch (IOException e) {
				e.printStackTrace();
			}
			sum+=i;
		}
		if(i<0){
			log.info("一个客户端退出了");
			return null;
		}else{
			log.info("本次接收到的body大小是： "+sum);
			byte[] b=body.array();
			if(b[b.length-3]==dataEnd[0] && b[b.length-2]==dataEnd[1] && b[b.length-1]==dataEnd[2]){
				byte[] d=Arrays.copyOf(b, body_size-4);
				BodyBuffer buffer=(BodyBuffer) byte2Bean(d);
				return buffer;
			}else{
				log.error("此次接受到的body不完整！");
				return null;
			}
		}
		
		
	}
	
	
	private static ByteBuffer bean2Buffer(Object obj){
		
		byte[] b=null;
		String[] strs=null;
		ByteBuffer buffer=null;
		if(obj instanceof String[]){
			strs=(String[])obj;
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<strs.length;i++){
			sb.append(strs[i]);	
			}
			buffer=ByteBuffer.wrap(sb.toString().getBytes());
		}else{
			try {
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				ObjectOutputStream oos=new ObjectOutputStream(baos);
				oos.writeObject(obj);
				b = baos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
			buffer=ByteBuffer.wrap(b);
		}
		return buffer;
	}
	
	private static byte[] bean2bytes(Object obj){
		
		byte[] b=null;
		String[] strs=null;
		if(obj instanceof String[]){
			strs=(String[])obj;
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<strs.length;i++){
				sb.append(strs[i]);	
			}
			b=sb.toString().getBytes();
		}else{
			try {
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				ObjectOutputStream oos=new ObjectOutputStream(baos);
				oos.writeObject(obj);
				b = baos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return b;
	}
	
	
	public static Object byte2Bean(byte[] b){
		Object o=null;
		try {
			ByteArrayInputStream bais=new ByteArrayInputStream(b);
			ObjectInputStream ios=new ObjectInputStream(bais);
			o=ios.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return o;
	}
	

}
