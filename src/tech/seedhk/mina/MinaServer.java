package tech.seedhk.mina;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class MinaServer {
	
	private int port=8888;
	IoAcceptor acceptor=null;
	public void init(){
		try {
			acceptor=new NioSocketAcceptor();
			//添加字符解码器
			acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(
					new TextLineCodecFactory(
							Charset.forName("UTF-8"),
							LineDelimiter.WINDOWS.getValue(),
							LineDelimiter.WINDOWS.getValue())));
			acceptor.getSessionConfig().setReadBufferSize(1024);
			acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 40);
			acceptor.setHandler(new ServerHandler());
			acceptor.bind(new InetSocketAddress(port));
			System.out.println("server started at :"+port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		new MinaServer().init();
	}
	

}
