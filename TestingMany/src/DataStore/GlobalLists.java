package DataStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GlobalLists
{
	
//CONNECTION DATA
	//VARS
	private static ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> clientHashMap = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> traderHashMap = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> routerHashMap = new ConcurrentHashMap<>();
	
	//GET HASH-MAPS
	public synchronized static ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> getClientHashMap()
	{return clientHashMap;}
	public synchronized static ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> getTraderHashMap()
	{return traderHashMap;}
	public synchronized static ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> getRouterHashMap()
	{return routerHashMap;}
	
	//GET QUEUE
	public synchronized static ConcurrentLinkedQueue getClientQueue(int clientId)
	{return clientHashMap.get(clientId);}
	public synchronized static ConcurrentLinkedQueue getTraderQueue(int traderId)
	{return clientHashMap.get(traderId);}
	public synchronized static ConcurrentLinkedQueue getRouterQueue(int routerId)
	{return clientHashMap.get(routerId);}
	
	//ADD TO SPECIFIC QUEUE
	public synchronized static void addToClientQueue(int clientId, String message)
	{clientHashMap.get(clientId).add(message);}
	public synchronized static void addToTraderQueue(int traderId, String message)
	{traderHashMap.get(traderId).add(message);}
	public synchronized static void addToRouterQueue(int routerId, String message)
	{routerHashMap.get(routerId).add(message);}
	
	//ADD TO ANY QUEUE
	public synchronized static void addToTraderQueue(String message)
	{
		//TODO make this more fair!!
		traderHashMap.get(0).add(message);
	}
	public synchronized static void addToRouterQueue(String message)
	{
		//TODO make this more fair!!
		routerHashMap.get(0).add(message);
	}
	
	//ADD TO ALL QUEUES
	public synchronized static void addToAllRouterQueues(String message) //HEAVY ON SYSTEM LOAD!!
	{
		for(Map.Entry<Integer, ConcurrentLinkedQueue<String>> entry : routerHashMap.entrySet())
		{
			entry.getValue().add(message);
		}
	}
	
	//INITIALIZE QUEUE
	public synchronized static void initializeClientQueue(int clientId)
	{clientHashMap.put(clientId, new ConcurrentLinkedQueue<String>());}
	public synchronized static void initializeTraderQueue(int traderId)
	{traderHashMap.put(traderId, new ConcurrentLinkedQueue<String>());}
	public synchronized static void initializeRouterQueue(int routerId)
	{routerHashMap.put(routerId, new ConcurrentLinkedQueue<String>());}
	
	//DESTROY QUEUE
	public synchronized static void destroyClientQueue(int clientId)
	{clientHashMap.remove(clientId);}
	public synchronized static void destroyTraderQueue(int traderId)
	{traderHashMap.remove(traderId);}
	public synchronized static void destroyRouterQueue(int routerId)
	{routerHashMap.remove(routerId);}
	
//ORDERS
	//VARS
	private static ConcurrentHashMap<Integer, Order> orders = new ConcurrentHashMap<>();
	private static int orderCounter = 0;
	
	//ADD ORDER
	public synchronized static int addOrder(Order order)
	{
		orderCounter += 1;
		orders.put(orderCounter, order);
		order.setId(orderCounter);
		return orderCounter;
	}
	
	public synchronized static Order getOrder(int orderId)
	{
		return orders.get(orderId);
	}
	
	public synchronized static void removeOrder(int orderId)
	{
		orders.remove(orderId);
	}
}
