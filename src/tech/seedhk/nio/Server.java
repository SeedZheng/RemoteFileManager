package tech.seedhk.nio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tech.seedhk.bean.ProxyObject;


/**
 * 服务端，即被控端，基于NIO
 * @author Seed
 * 2017年12月6日 下午2:32:56
 */
public class Server {
	
	private Selector selector;
	private static ExecutorService service=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
	
	private void initServer(int port) throws Exception{
		ServerSocketChannel ssChannel=ServerSocketChannel.open();
		
		ssChannel.configureBlocking(false);
		
		//ssChannel.socket().setReuseAddress(true);//允许在前一个连接TIME_WAIT状态时绑定新的端口
		ssChannel.socket().bind(new InetSocketAddress(port));
		
		//获得一个通道管理器
		this.selector=Selector.open();
		
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	private void listen()throws Exception{
		System.out.println("服务器启动成功，进入监听状态");
		
		while(true){
			int n=selector.select();
			if(n==0)
				continue;
			Iterator iterator = selector.selectedKeys().iterator();
			while(iterator.hasNext()){
				SelectionKey key=(SelectionKey) iterator.next();
				service.execute(new Process(key));
			}
			
		}
	}
	
	class Process implements Runnable{

		private SelectionKey key;

		public Process(SelectionKey key) {
			this.key=key;
		}

		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName()+" 开始执行任务");
			if(key.isAcceptable()){
				try {
					ServerSocketChannel ssc=(ServerSocketChannel) key.channel();
					SocketChannel sc=ssc.accept();
					sc.configureBlocking(false);
					
					sc.write(ByteBuffer.wrap("connection success".getBytes()));
					
					sc.register(selector, SelectionKey.OP_ACCEPT | SelectionKey.OP_READ);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(key.isReadable()){
				try {
					read(key);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}

		private void read(SelectionKey key2) throws Exception {
			ServerSocketChannel ssc=(ServerSocketChannel) key2.channel();
			SocketChannel sc=ssc.accept();
			
			ByteBuffer buffer=ByteBuffer.allocate(1024);
			
			sc.read(buffer);
			
			rpc(buffer);
			
			sc.write(buffer);
			System.out.println("命令调用完成");
		}

		private ByteBuffer rpc(ByteBuffer buffer) {
			//RPC调用
			ProxyObject po=(ProxyObject) ProxyObject.buffer2Bean(buffer);
			Object object=null;
			if(po!=null){
				object=ProxyObject.invoke(po.getClassName(), po.getMethodName(), po.getObjects(), String.class);
			}
			buffer.clear();
			buffer=ProxyObject.bean2Buffer(object);
			return buffer;
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		//先连接中继器，注册成功后启动Server端
		Server server=new Server();
		server.resister("127.0.0.1", 6666);
	}
	
	
	private  void resister(String host,int port) throws Exception{
		Socket s=new Socket(host, port);
		System.out.println("连接中继器成功");
		InputStream is=new DataInputStream(s.getInputStream());
		OutputStream os=new DataOutputStream(s.getOutputStream());

		tech.seedhk.bean.ByteBuffer buffer=new tech.seedhk.bean.ByteBuffer();
		buffer.write(os, "sender");
		byte[] data = tech.seedhk.bean.ByteBuffer.read(is);
		String ret=new String(data,"utf-8");
		System.out.println(ret);
		if("success".equals(ret)){
			System.out.println("注册成功，启动sever");
			is.close();
			os.close();
			s.close();
			System.out.println(s.isClosed());
			initServer(port);
		}
		
		
	}

}
