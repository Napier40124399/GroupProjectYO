package ServerManagers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import DataStore.Connection;
import Handlers.Handler;

public class Manager
{
	private ArrayList<SocketChannel> channels;
	private ArrayList<Thread> threads;
	private ArrayList<Connection> connections;
	
	private static final int[] backOff = new int[]{100,200,500,1000,2000};
	private static final int timeout = 25;
	private int idCounter = 0;

	public Manager(InetSocketAddress[] addresses)
	{
		connections = new ArrayList<>();
		for(InetSocketAddress address : addresses)
		{
			connect(address);
			//new Thread(() -> {connect(address);}).start();
		}
		prepareHandlers();
	}
	
	public void prepareHandlers()
	{
		joinThreads();
		for(Connection con : connections)
		{
			con.getThread().start();
		}
	}
	
	private void joinThreads()
	{
		for(Connection con : connections)
		{
			try
			{
				con.getThread().join();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	private synchronized void addChannel(SocketChannel channel)
	{
		//channels.add(channel);
		Connection connection = new Connection(idCounter, getHandler(idCounter), channel);
		connection.getHandler().setConnection(connection);
		connections.add(connection);
		idCounter++;
	}
	
	private void connect(InetSocketAddress address)
	{
		Boolean connected = false;
		int attempts = 0;
		for (int i = 0; i < timeout; i++)
		{
			try
			{
				ServerSocketChannel servSockChan = ServerSocketChannel.open();
				servSockChan.socket().bind(address);
				SocketChannel channel = servSockChan.accept();
				addChannel(channel);
				break;
			} catch (IOException e)
			{
				try
				{
					Thread.sleep(backOff[((i < backOff.length) ? i : backOff.length-1)]);
				} catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}
			}
		}
	}
	
	public Handler getHandler(int id)
	{
		return null;
	}
	
	public ArrayList<Connection> getConnections()
	{
		return connections;
	}
}
