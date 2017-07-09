package Handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import DataGenerators.LiveMarketData;
import DataStore.Connection;

public class Handler implements Runnable
{
	private Connection connection;
	private SocketChannel channel;
	private int id;
	private static final String delimiter = "\\|";
	private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> myQueue;

	public Handler(ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> myQueue)
	{
		this.myQueue = myQueue;
	}
	
	@Override
	public void run()
	{
		new Thread(() ->
		{
			checkComms();
		}).start();
		checkQueue();
	}

	private void checkComms()
	{
		while (true)
		{
			ByteBuffer buffer = ByteBuffer.allocate(256);
			try
			{
				channel.read(buffer);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			handleMessage(new String(buffer.array(), Charset.forName("UTF-8")).trim());
		}
	}

	private void checkQueue()
	{
		String value;
		while (true)
		{
			try
			{
				while ((value = myQueue.get(id).peek()) != null)
				{
					handleMessage(myQueue.get(id).poll());
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			sleep(1000);
		}
	}

	public void handleMessage(String inbound)
	{
		System.err.println("NOT OVERRIDING :: Handlers.Handler#handleMessage(String inbound)");
	}

	public void sendToClient(String outbound)
	{
		ByteBuffer buffer = ByteBuffer.allocate(256);
		buffer.clear();
		buffer.put(outbound.getBytes());

		buffer.flip();

		try
		{
			while (buffer.hasRemaining())
			{
				channel.write(buffer);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		buffer.clear();
	}

	public void setConnection(Connection connection)
	{
		this.connection = connection;
		this.channel = connection.getChannel();
		this.id = connection.getId();
	}

	public Connection getConnection()
	{
		return connection;
	}

	public SocketChannel getChannel()
	{
		return channel;
	}

	public int getId()
	{
		return id;
	}

	public String getDelimiter()
	{
		return delimiter;
	}
	private static void sleep(int time)
	{
		try
		{
			Thread.sleep(time);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
