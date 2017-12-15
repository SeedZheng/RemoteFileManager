package tech.seedhk.nio;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 控制端，基于NIO,单线程足矣
 * @author Seed
 * 2017年12月6日 下午5:09:09
 */
public class Client {
	
	private  Selector selector;
	private boolean isGet=false;
	
	public static void main(String[] args) throws Exception {
		Client client=new Client();
		client.register("127.0.0.1", 8888);
	}
	
	public void register(String host,int port) throws Exception{

		Socket s;
		
		while(!isGet){
			 s=new Socket(host,port);
			System.out.println("连接中继器成功");
			InputStream is=new DataInputStream(s.getInputStream());
			OutputStream os=new DataOutputStream(s.getOutputStream());

			tech.seedhk.bean.ByteBuffer buffer=new tech.seedhk.bean.ByteBuffer();
			buffer.write(os, "client");
			byte[] data = tech.seedhk.bean.ByteBuffer.read(is);
			String ret=new String(data,"utf-8");
			System.out.println(ret);
			if(ret.contains("get ip")){
				String ip=ret.substring(ret.indexOf(":")+1);
				System.out.println("已获取到IP地址，地址是: "+ip);
				is.close();
				os.close();
				s.close();
				System.out.println(s.isClosed());
				initClient(ip,port);
				isGet=true;
			}	
		}
	}

	private void initClient(String ip,int port) throws Exception {
		
		//获得一个socket通道
		SocketChannel sc=SocketChannel.open();
		
		sc.configureBlocking(false);
		
		this.selector=Selector.open();
		
		//执行完这步后，服务器端的select连接阻塞状态会解除,如果通道的阻塞模式为false，此方法会立即返回
		boolean isDone=sc.connect(new InetSocketAddress(ip,port));
		System.out.println(isDone);
		
		sc.register(selector, SelectionKey.OP_CONNECT);
		
		listen();
	}
	
	public void listen() throws Exception{
		
		while(true){
			selector.select();
			Iterator iterator=this.selector.selectedKeys().iterator();
			while(iterator.hasNext()){
				SelectionKey key=(SelectionKey) iterator.next();
				
				iterator.remove();
				
				if(key.isConnectable()){
					SocketChannel channel=(SocketChannel) key.channel();
					
					//如果正在连接，则完成该连接
					if(channel.isConnectionPending())
						channel.finishConnect();
					
					channel.configureBlocking(false);
					
					//channel.write(ByteBuffer.wrap("向服务端发送了一条消息".getBytes()));
					channel.write(writeData("text","向服务端发送了一条消息"));
					
					channel.register(this.selector, SelectionKey.OP_READ);
				}else if(key.isReadable()){
					read(key);
				}
			}
			
		}
		
	}


	private void read(SelectionKey key) throws Exception {
		SocketChannel sChannel=(SocketChannel) key.channel();
		
		ByteBuffer[] buffers=new ByteBuffer[2];
		
		sChannel.read(buffers);
		
		Scanner scan=new Scanner(System.in);
		
		System.out.println("请输入文件类型，text、rpc、getFile");
		String head=scan.nextLine().trim();
		System.out.println("请输入数据：");
		String body=scan.nextLine().trim();
		ByteBuffer[] buffer=writeData(head, body);
		sChannel.write(buffer);
		scan.close();
		
		
	}
	
	private ByteBuffer[] writeData(String h,String b){
		
		byte[] by=new byte[10];
		byte[] data=h.trim().getBytes();
		for(int i=0;i<by.length;i++){
			if(i<data.length)
					by[i]=data[i];
			else
				by[i]='\0';
		}
		
		ByteBuffer head = ByteBuffer.wrap(by);
		ByteBuffer body = ByteBuffer.wrap(b.trim().getBytes());
		
		return new ByteBuffer[]{head,body};
	}
	
	private void getData(ByteBuffer[] buffers) throws Exception{
		
		ByteBuffer h=buffers[0];
		ByteBuffer b=buffers[1];
		
		String type=new String(h.array()).trim();
		System.out.println("收到的数据类型为: "+type);
		
		if(type.equals("text") || type.equals("rpc")){
			String msg=new String(h.array()).trim();
			System.out.println("数据内容："+msg);
		}
		
		if(type.contains("file")){
			String suffix=type.substring(type.indexOf(":"));
			byte[] data=b.array();
			File file=new File("data"+suffix);
			FileOutputStream fos=new FileOutputStream(file);
			fos.write(data);
			fos.flush();
			fos.close();
			System.out.println("文件输出完毕");
		}
	}

}
