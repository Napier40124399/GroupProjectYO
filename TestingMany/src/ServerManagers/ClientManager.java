package ServerManagers;

import java.net.InetSocketAddress;

import DataStore.GlobalLists;
import Handlers.ClientHandler;
import Handlers.Handler;

public class ClientManager extends Manager
{
	public ClientManager(InetSocketAddress[] addresses)
	{
		super(addresses);
	}
	
	@Override
	public Handler getHandler(int id)
	{
		GlobalLists.initializeClientQueue(id);
		return new ClientHandler(GlobalLists.getClientHashMap());
	}
}
