package tech.seedhk.nio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tech.seedhk.bean.ByteBuffer;

public class Repeater {
	
	private static ExecutorService threadPool=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	private Map<String, String> map=new HashMap<>();
	
	public static void main(String[] args) throws Exception {
		Repeater r=new Repeater();
		r.initServer(6666);
	}
	
	@SuppressWarnings("resource")
	private void initServer(int port)throws Exception{
		ServerSocket ss=new ServerSocket(port);
		System.out.println("repeater starting");
		while(true){
			Socket s = ss.accept();
			String ip=s.getInetAddress().getHostAddress();
			System.out.println("ip："+ip);
			if(map.get("server")!=null){
				if(ip.equals(map.get("server"))){
					threadPool.execute(new Sender(s));
				}
			}
			if(map.get("client")!=null){
				if(ip.equals(map.get("client"))){
					threadPool.execute(new Geter(s));
				}
			}
			threadPool.execute(new Register(s));
		}
	}
	
	/**
	 * server端(被控端) 、client端(控制端)的注册过程
	 * @author Seed
	 * 2017年12月6日 下午3:09:24
	 */
	class Register implements Runnable{
		
		private Socket socket;
		public Register(Socket s) {
			this.socket=s;
		}
		
		InputStream is;
		OutputStream os;

		@Override
		public void run() {
			try {
				is=new DataInputStream(socket.getInputStream());
				os=new DataOutputStream(socket.getOutputStream());
				
				byte[] data=ByteBuffer.read(is);
				String ip=socket.getInetAddress().getHostAddress();
				String type=new String(data,"utf-8");
				if("client".equals(type))
					map.put("client", ip);
				if("server".equals(type))
					map.put("server", type);
				
				ByteBuffer buffer=new ByteBuffer();
				buffer.write(os, "success");
				
				os.close();
				is.close();
				
				System.out.println( type+"注册完毕");
					
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	/**
	 * 控制端，从这里拿到被控端的IP
	 * @author Seed
	 * 2017年12月6日 下午2:26:21
	 */
	class Geter implements Runnable{
		
		private Socket socket;

		public Geter(Socket s){
			this.socket=s;
		}
		
		//InputStream is;
		OutputStream os;

		@Override
		public void run() {
			try {
				//is=new DataInputStream(socket.getInputStream());
				os=new DataOutputStream(socket.getOutputStream());
				ByteBuffer buffer=new ByteBuffer();
				
				if(map.get("server")==null)
					buffer.write(os, "no ip");
				else
					buffer.write(os, "get ip:"+map.get("server"));
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					os.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	
	
	/**
	 * 注册成为被控端
	 * @author Seed
	 * 2017年12月6日 下午2:23:18
	 */
	class Sender implements Runnable{
		
		private Socket socket;
		
		public Sender(Socket s){
			this.socket=s;
		}

		InputStream is;
		OutputStream os;
		
		@Override
		public void run() {
			//如果当前已经有被控端了，这里应该替换原来的
			
			try {
				is=new DataInputStream(socket.getInputStream());
				os=new DataOutputStream(socket.getOutputStream());
				
				byte[] data=ByteBuffer.read(is);
				String ip=socket.getInetAddress().getHostAddress();
				String type=new String(data,"utf-8");
				if("client".equals(type))
					map.put("client", ip);
				if("server".equals(type))
					map.put("server", type);
				
				ByteBuffer buffer=new ByteBuffer();
				buffer.write(os, "success");
				
				os.close();
				is.close();
				
				System.out.println( type+"注册完毕");
					
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
}
