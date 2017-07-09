package Main;

import java.net.InetSocketAddress;

import Clients.Client;
import Clients.Router;
import Clients.Trader;
import ServerManagers.ClientManager;
import ServerManagers.RouterManager;
import ServerManagers.TraderManager;

public class Main
{
	private static int port = 3000;

	public static void main(String[] args)
	{
		// Clients
		int clients = 10;
		InetSocketAddress[] address = new InetSocketAddress[clients];
		for (int i = 0; i < clients; i++)
		{
			address[i] = makeAd(port++);
			final int ii = i;
			new Thread(() ->
			{
				new Client(address[ii]);
			}).start();
		}

		new Thread(() ->
		{
			new ClientManager(address);
		}).start();

		// Traders
		int traders = 1;
		InetSocketAddress[] address2 = new InetSocketAddress[traders];
		for (int i = 0; i < traders; i++)
		{
			address2[i] = makeAd(port++);
			final int ii = i;
			new Thread(() ->
			{
				new Trader(address2[ii]);
			}).start();
		}

		new Thread(() ->
		{
			new TraderManager(address2);
		}).start();

		// Routers
		int routers = 3;
		InetSocketAddress[] address3 = new InetSocketAddress[routers];
		for (int i = 0; i < routers; i++)
		{
			address3[i] = makeAd(port++);
			final int ii = i;
			new Thread(() ->
			{
				new Router(address3[ii]);
			}).start();
		}

		new Thread(() ->
		{
			new RouterManager(address3);
		}).start();

	}

	private static InetSocketAddress makeAd(int port)
	{
		return new InetSocketAddress("localhost", port);
	}
}
