package Clients;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Random;

import DataStore.Order;

public class Router
{
	private static final int[] backOff = new int[]
	{ 100, 200, 500, 1000, 2000 };
	private static final int timeOut = 25;
	private SocketChannel channel;
	private static final String delimiter = "\\|";

	private HashMap<Integer, Order> orders;

	public Router(InetSocketAddress address)
	{
		channel = connect(address);
		orders = new HashMap<>();
		new Thread(() ->
		{
			listen();
		}).start();
	}

	private SocketChannel connect(InetSocketAddress address)
	{
		SocketChannel socketChannel;

		for (int i = 0; i < timeOut; i++)
		{
			try
			{
				socketChannel = SocketChannel.open();
				socketChannel.connect(address);
				return socketChannel;
			} catch (IOException e)
			{

				try
				{
					Thread.sleep(backOff[((i < backOff.length) ? i : backOff.length - 1)]);
				} catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}
			}
		}
		return null;
	}

	private void listen()
	{
		while (true)
		{
			ByteBuffer buffer = ByteBuffer.allocate(256);
			try
			{
				channel.read(buffer);
				handleMessage(new String(buffer.array(), Charset.forName("UTF-8")).trim());
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void sendMsg(String outbound)
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

	private void handleMessage(String inbound)
	{
		String[] details = inbound.split(delimiter);
		switch (details[0])
		{
		case "6":
			priceAtSize(details);
			break;
		default:
			System.err.println("READ NOT RECOGNIZED >> " + inbound);
		}
	}
	
	private void priceAtSize(String[] details)
	{
		Random r = new Random();
		float price = r.nextFloat() * 20;
		System.out.println(price);
		sendMsg("7|"+details[1]+"|"+price);
	}
}
