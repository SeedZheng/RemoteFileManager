package tech.seedhk.mina;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class ServerHandler extends IoHandlerAdapter{
	
	private AtomicInteger i=new AtomicInteger(0);

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("sessionCreated"+" : " +i.getAndIncrement());
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("sessionOpened"+" : " +i.getAndIncrement());
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("sessionClosed"+" : " +i.getAndIncrement());
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		System.out.println("sessionIdle"+" : " +i.getAndIncrement());
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		System.out.println("exceptionCaught"+" : " +i.getAndIncrement());
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		System.out.println("messageReceived"+" : " +i.getAndIncrement());
		//收到消息
		String msg=(String)message;
		System.out.println("receiver data ："+msg);
		session.write(msg+" : "+new Date());
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out.println("messageSent"+" : " +i.getAndIncrement());
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
		System.out.println("inputClosed"+" : " +i.getAndIncrement());
		session.closeNow();
	}
	
	
	

}
