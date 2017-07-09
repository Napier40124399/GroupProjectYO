package ServerManagers;

import java.net.InetSocketAddress;

import DataStore.GlobalLists;
import Handlers.Handler;
import Handlers.TraderHandler;

public class TraderManager extends Manager
{

	public TraderManager(InetSocketAddress[] addresses)
	{
		super(addresses);
	}
	
	@Override
	public Handler getHandler(int id)
	{
		GlobalLists.initializeTraderQueue(id);
		return new TraderHandler(GlobalLists.getTraderHashMap());
	}

}
