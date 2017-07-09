package ServerManagers;

import java.net.InetSocketAddress;

import DataStore.GlobalLists;
import Handlers.Handler;
import Handlers.RouterHandler;

public class RouterManager extends Manager
{

	public RouterManager(InetSocketAddress[] addresses)
	{
		super(addresses);
	}
	
	@Override
	public Handler getHandler(int id)
	{
		GlobalLists.initializeRouterQueue(id);
		return new RouterHandler(GlobalLists.getRouterHashMap());
	}
}
