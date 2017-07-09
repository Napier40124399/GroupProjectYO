package Clients;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;

import DataStore.Order;

public class Client
{
	private static final int[] backOff = new int[]
	{ 100, 200, 500, 1000, 2000 };
	private static final int timeOut = 25;
	private SocketChannel channel;
	private static final String delimiter = "\\|";
	private HashMap<Integer, Order> orders;

	public Client(InetSocketAddress address)
	{
		channel = connect(address);
		orders = new HashMap<>();
		new Thread(() ->
		{
			listen();
		}).start();
		try
		{
			Thread.sleep(1000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendOrder(10, "VOD");
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

	private void sendOrder(int volume, String instrument)
	{
		String outbound = "0|" + volume + "|" + instrument;
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
		case "1":
			receivedConfirmation(details);
			break;
		case "4":
			updateOrder(details);
			break;
		default:
			System.err.println("READ NOT RECOGNIZED >> " + inbound);
		}
	}

	private void updateOrder(String[] details)
	{
		orders.get(Integer.parseInt(details[1])).setStatus(details[2]);
	}

	private void receivedConfirmation(String[] details)
	{
		// TODO plug into GUI
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		orders.put(Integer.parseInt(details[1]), new Order(details[2]));
	}
}
