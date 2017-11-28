package tech.seedhk.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import tech.seedhk.bean.ByteBuffer;
import tech.seedhk.utils.FileUtils;

public class Server3 implements Runnable{
	
	
	public static void main(String[] args) {
		Server3 s3=new Server3();
		Thread t1=new Thread(s3);
		t1.start();
	
	}
	
	//数据
	private static volatile byte[] data=null;	//用于存储任务和其他命令
	private static volatile byte[] sendData=null;//用于存储准备发送的数据
	private int port=6666;
	private Map<String, String> map=new HashMap<>();

	@Override
	public void run() {
		
		try {
			@SuppressWarnings("resource")
			ServerSocket ss=new ServerSocket(port);
			System.out.println("服务器启动成功");
			while(true){
				Socket s = ss.accept();
				String ip=s.getInetAddress().getHostAddress();
				System.out.println("ip："+ip);
				//System.out.println(s.isClosed());
				if(map.get("sender")!=null){
					if(ip.equals(map.get("sender"))){
						new Sendler(s);//是发送者
						continue;	//跳出本次循环
						//break;	//跳出所有循环
					}
				}
				if(map.get("geter")!=null){
					if(ip.equals(map.get("geter"))){
						new getler(s);//是接受者
						continue;
					}
				}
				new Handler(s);	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private class getler implements Runnable{
		
		private Socket s;

		public getler(Socket s) {
			this.s = s;
			new Thread(this).start();//启动一个新线程来处理
		}

		@Override
		public void run() {
			//我是接收服务端，负责将数据发送给接收客户端
			InputStream is;
			OutputStream os;
			try {
				os=new DataOutputStream(s.getOutputStream());
				ByteBuffer buffer=new ByteBuffer();
				//if(data!=null && sendData==null)
				
				if(sendData==null)
					buffer.write(os, "no data");
				else
					buffer.writeObj(os, sendData);
				//os.write(sendData);
				os.flush();
				sendData=null;//接收完命令后清空该缓冲区
				is=new DataInputStream(s.getInputStream());
				data=ByteBuffer.read(is);
				//data=new byte[is.available()];
				//is.read(data);//一直阻塞在接收返回值上
				//System.out.println(is.read());
				is.close();
				os.close();
			} catch (Exception e) {
				System.out.println("getler 服务端发生异常");
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
	
	private class Sendler implements Runnable{
		
		private Socket s;
		public Sendler(Socket s) {
			this.s = s;
			new Thread(this).start();//启动一个新线程来处理
		}

		@Override
		public void run() {
			/*
			 * sendData不为空---》下发命令----》接收请求
			 * sendData为空----》阻塞线程直到sendData不为空
			 */
			InputStream is;
			OutputStream os;
			try {
				os=new DataOutputStream(s.getOutputStream());
				while(data==null){
					Thread.sleep(3000);
				}
				ByteBuffer buffer=new ByteBuffer();
				buffer.writeObj(os, data);
				//os.write(data);
				os.flush();
				data=null;//接收完命令后清空该缓冲区
				is=new DataInputStream(s.getInputStream());
				sendData=ByteBuffer.read(is);
				//sendData=new byte[is.available()];
				//is.read(sendData);//一直阻塞在接收返回值上
				is.close();
				os.close();
			} catch (Exception e) {
				System.out.println("sender 服务端发生异常");
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
	/**
	 * 第一次连接
	 * @author Seed
	 * 2017年11月23日 下午6:28:11
	 */
	private class Handler implements Runnable{

		private Socket s;
		private boolean done=false;	//客户端是否断开
		public Handler(Socket socket) {
			this.s=socket;
			new Thread(this).start();//启动一个新线程来处理
		}

		@Override
		public void run() {
			//判断客户端是否已经断开
			/*try {
				s.sendUrgentData(1);
			} catch (IOException  e) {
				e.printStackTrace();
				done=true;
			}*/
			
			//这是发送者和接受者的第一次连接
			//先发送一段数据
			OutputStream os;
			InputStream is;
			try {
				if(!done){
					os = new DataOutputStream(s.getOutputStream());
					ByteBuffer buffer=new ByteBuffer();
					buffer.write(os, "connect successed");
					//os.write("connect successed\n".getBytes());
					os.flush();
					//os.close();//自带flush，且关闭当前socket
					is=new DataInputStream(s.getInputStream());
					/*
					 *如果有多个客户端对应一个server端，且多个客户端同事发送数据，数据是否会冲突？
					 *不会，因为不同的server线程对应不同的客户端，每个线程有自己独立的socket通道，且read方法不会相互阻塞
					 */
					byte[] da=ByteBuffer.read(is);
					//int i=0;
					//StringBuilder sb=new StringBuilder();
					//while((i=is.read())!=10){
					//	sb.append((char)i);
						//System.out.println(i);
					//}
					//is.read(da);
					String ret=new String(da,"utf-8");
					System.out.println("收到客户端数据："+ret);
					//String ip=s.getInetAddress().getHostName();
					String ip=s.getInetAddress().getHostAddress();
					if("sender".equals(ret.trim())){
						//这是发送者
						map.put("sender", ip);
					}
					if("geter".equals(ret.trim())){
						//这是接受者
						map.put("geter", ip);
					}
					is.close();	
					os.close();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				if(s!=null){
					try {
						s.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}}
	
	

}
