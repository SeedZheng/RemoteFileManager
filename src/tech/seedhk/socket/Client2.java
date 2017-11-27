package tech.seedhk.socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client2 {
	/*
	 * is.read(data) 中出现了 Software caused connection abort: recv failed 错误
	 * 原因是客户端准备读取数据时，服务端的socket被关闭了
	 */
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		
		//Socket s=new Socket("39.108.208.62",6666);
		Socket s=null;
		while(true){
			s=new Socket("127.0.0.1",6666);
			System.out.println("和服务器的连接建立成功");
			//先准备接受数据
			byte[] data=new byte[1024*5];
			InputStream is=new DataInputStream(s.getInputStream());
			is.read(data);
			String ret=new String(data,"utf-8");
			System.out.println("收到服务端的数据："+ret);
			//准备发送数据
			BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
			OutputStream os=new DataOutputStream(s.getOutputStream());
			System.out.println("请输入:");
			String resp=reader.readLine();
			System.out.println("发送数据："+resp);
			os.write(resp.getBytes());
			os.flush();
			//os.close();
			System.out.println("发送完毕");
		}
	}

}
