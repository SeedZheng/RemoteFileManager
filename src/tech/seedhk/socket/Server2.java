package tech.seedhk.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server2 {
	
	/*
	 * 思路：
	 * 服务器收到客户端连接后，立即发送数据，然后阻塞在read方法上，等待客户端的数据，而后逻辑处理，处理完毕后将数据发送，再阻塞在read方法上
	 * 客户端连接成功后阻塞在read方法上，等待服务器数据，而后接受数据并再发送数据，以此往复
	 */
	/*
	 * 当前发现问题：
	 * 如果关闭了OutPutStream,会导致socket被关闭
	 * 如果关闭了 socket的OutPutStream ,会导致下次获取不到新的outPutStream
	 * 
	 * 解决思路：
	 * 服务器端：main线程只负责接收客户端请求，每次接收到后重新打开一个线程用于处理数据，处理完毕后该线程关闭
	 * 客户端：传输完一次数据关闭一个socket 第二次重新连接
	 */
	
	private static byte[] data=null;
	
	public static void main(String[] args) throws Exception {
		
		@SuppressWarnings( "resource" )
		ServerSocket ss=new ServerSocket(6666);
		while(true){
			Socket s = ss.accept();
			System.out.println("接收到一个客户端的连接");
			handler(s);
		}
		
	}

	private static void handler(Socket s) throws Exception {
		
		while(true){
			System.out.println("先发送一段数据");
			//先发送一段数据
			OutputStream os=new DataOutputStream(s.getOutputStream());
			if(data!= null)
				os.write(data);
			else
				os.write("prepare send data".getBytes());
			os.flush();
			//os.close();//该方法会关闭当前的socket
			data=null;
			Thread.sleep(3000);
			//准备接受数据
			System.out.println("准备接受数据");
			InputStream is=new DataInputStream(s.getInputStream());
			byte[] data=new byte[1024*5];//最多只支持5MB的数据
			is.read(data);
			String ret=new String(data,"utf-8");
			System.out.println("从客户端接受到的数据为："+ret);
			process("listRoot");
		}
		
	}
	
	private static void process(String opt){
		System.out.println("开始逻辑处理");
		
		StringBuilder sb=new StringBuilder();
		
		if("listRoot".equals(opt)){
			File[] files=File.listRoots();
			for(File f:files){
				sb.append(f.getAbsolutePath());
			}
			
		}
		data=new byte[1024*5];
		data=sb.toString().getBytes();
	}

}
