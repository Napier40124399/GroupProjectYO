package DataStore;

import java.nio.channels.SocketChannel;

import Handlers.Handler;

public class Connection
{
	private final int id;
	private Thread thread;
	private SocketChannel channel;
	private Handler handler;
	
	public Connection(int id, Handler handler, SocketChannel channel)
	{
		this.id = id;
		this.handler = handler;
		this.thread = new Thread(handler);
		this.channel = channel;
	}
	
	public Handler getHandler()
	{
		return handler;
	}
	
	public Thread getThread()
	{
		return thread;
	}
	
	public int getId()
	{
		return id;
	}
	
	public SocketChannel getChannel()
	{
		return channel;
	}
}
