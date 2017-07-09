package Clients;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;

import DataStore.Order;
import DataStore.Slice;

public class Trader
{
	private static final int[] backOff = new int[]
	{ 100, 200, 500, 1000, 2000 };
	private static final int timeOut = 25;
	private SocketChannel channel;
	private static final String delimiter = "\\|";

	private HashMap<Integer, Order> orders;

	public Trader(InetSocketAddress address)
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
		case "2":
			newOrder(details);
			break;
		case "5":
			slice(details);
			break;
		default:
			System.err.println("READ NOT RECOGNIZED >> " + inbound);
		}
	}

	private void slice(String[] details)
	{
		Order order = new Order(details[1]);
		Order myOrder = orders.get(order.getId());
		myOrder.setPrice(order.getPrice());
		// TODO set price from gui
		myOrder.newSlice(myOrder.getVolume() - 50);
		sendMsg("5|"+order.getId()+"|"+(myOrder.getVolume() - 50));
	}

	private void newOrder(String[] details)
	{
		// TODO plug into GUI
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		Order order = new Order(details[1]);
		orders.put(order.getId(), order);
		System.err.println("TRADER SIDE ID: "+order.getId());
		sendMsg("3|" + order.getId());
	}
}
