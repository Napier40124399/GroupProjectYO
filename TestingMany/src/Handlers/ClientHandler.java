package Handlers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import DataStore.GlobalLists;
import DataStore.Order;

public class ClientHandler extends Handler
{
	public ClientHandler(ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> myQueue)
	{
		super(myQueue);
	}

	@Override
	public void handleMessage(String inbound)
	{
		String[] details = inbound.split(super.getDelimiter());
		switch(details[0])
		{
		case "0":newOrder(details);
		break;
		case "4":sendToClient(inbound);
		break;
		default:System.err.println("CLIENT UNKOWN MESSAGE: "+inbound);
		}
	}
	
	private void newOrder(String[] details)
	{
		//TODO create order in order hashMap
		int orderId = GlobalLists.addOrder(new Order(super.getId(), Integer.parseInt(details[1]), details[2]));
		System.out.println("before order id == "+orderId);
		String outbound = "1|"+orderId+"|"+GlobalLists.getOrder(orderId).toString();
		super.sendToClient(outbound);
		
		outbound = "2|"+GlobalLists.getOrder(orderId).toString();
		GlobalLists.addToTraderQueue(outbound);
	}
}
