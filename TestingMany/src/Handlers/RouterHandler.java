package Handlers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import DataStore.GlobalLists;

public class RouterHandler extends Handler
{

	public RouterHandler(ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> myQueue)
	{
		super(myQueue);
	}
	
	@Override
	public void handleMessage(String inbound)
	{
		String[] details = inbound.split(super.getDelimiter());
		switch (details[0])
		{
		case "6":priceAtSize(inbound);
			break;
		case "7":bestPrice(details);
		break;
		default:
			System.err.println("ROUTER UNKOWN MESSAGE: " + inbound);
		}
	}
	
	private void priceAtSize(String inbound)
	{
		GlobalLists.getOrder(Integer.parseInt(inbound.split(super.getDelimiter())[1])).incrementPendingReply();
		sendToClient(inbound);
	}
	
	private void bestPrice(String[] details)
	{
		if(GlobalLists.getOrder(Integer.parseInt(details[1])).decrementPendingReply(Float.parseFloat(details[2])))
		{
			System.out.println("BEST PRICE :: "+GlobalLists.getOrder(Integer.parseInt(details[1])).getPrice());//TODO here <<<
		}
	}
}
