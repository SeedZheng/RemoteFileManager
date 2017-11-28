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

import tech.seedhk.bean.ByteBuffer;
import tech.seedhk.bean.ProxyObject;

/**
 * 这是接收端，负责发送命令并接收数据
 * @author Seed
 * 2017年11月24日 下午12:02:56
 */
public class ClientGeter {
	
	private static boolean first=true;
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		
		Socket s=null;
		while(true){
			try {
				//s=new Socket("39.108.208.62", 6666);
				s=new Socket("127.0.0.1", 6666);
				System.out.println("geter端连接服务器成功");
				InputStream is=new DataInputStream(s.getInputStream());
				byte[] data=ByteBuffer.read(is);
				String ret=new String(data,"utf-8");
				System.out.println("从服务端接收到的数据是："+ret);
				//准备发送命令
				BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
				OutputStream os=new DataOutputStream(s.getOutputStream());
				ByteBuffer buffer= new ByteBuffer();
				if(first){
					buffer.write(os, "geter");
					first=false;
				}else{
					System.out.println("请输入欲执行的命令");
					ProxyObject po=ProxyObject.newInstance();
					String resp=reader.readLine();
					po.getMethod("tech.seedhk.utils.FileUtils", "showDire", new Object[]{resp}, String.class);
					System.out.println("发送数据："+resp);
					buffer.write(os, po);	
				}
				os.flush();
				System.out.println("发送完毕");
			} catch (Exception e){
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

}
