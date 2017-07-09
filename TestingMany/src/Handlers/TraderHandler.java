package Handlers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import DataGenerators.LiveMarketData;
import DataStore.GlobalLists;
import DataStore.Order;

public class TraderHandler extends Handler
{
	public TraderHandler(ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> myQueue)
	{
		super(myQueue);
	}

	@Override
	public void handleMessage(String inbound)
	{
		String[] details = inbound.split(super.getDelimiter());
		switch (details[0])
		{
		case "2":
			super.sendToClient(inbound);
			break;
		case "3":
			acceptOrder(details);
			break;
		case "5":
			slice(details);
			break;
		default:
			System.err.println("TRADER UNKOWN MESSAGE: " + inbound);
		}
	}

	private void slice(String[] details)
	{
		//Order order = new Order(details[1]);
		GlobalLists.getOrder(Integer.parseInt(details[1])).newSlice(Integer.parseInt(details[2]));
		// TODO internal cross
		routeOrder(GlobalLists.getOrder(Integer.parseInt(details[1])));
	}
	
	private void routeOrder(Order order)
	{
		GlobalLists.addToAllRouterQueues("6|"+order.getId()+"|"+order.getInstrument());
	}
	
	private void acceptOrder(String[] details)
	{
		System.out.println("after order id == " + details[1]);
		Order order = GlobalLists.getOrder(Integer.parseInt(details[1]));
		if (!order.getStatus().equals("N"))
		{
			System.err.println("ORDER ALREADY ACCEPTED OR COMPLETED! orderId=" + order.getId());
			return;
		}
		order.setStatus("A");
		GlobalLists.getClientQueue(order.getClientId()).add("4|" + order.getId() + "|" + order.getStatus());

		price(order);
	}

	private void price(Order order)
	{
		// TODO generate market data price and send to trader
		LiveMarketData.price(order);
		sendToClient("5|" + order.toString());
	}
}
