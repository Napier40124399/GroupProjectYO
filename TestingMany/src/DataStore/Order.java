package DataStore;

import java.util.ArrayList;

public class Order
{
	private static final String delimiter = "&";
	private static final String sliceDelimiter = "#";

	private int volume;
	private int volumeRemaining;
	private String instrument;
	private int clientId;
	private int orderId;
	private String status;
	private float price;
	private ArrayList<Slice> slices;
	private int pendingReplies = 0;
	
	public Order(int clientId, int volume, String instrument)
	{
		slices = new ArrayList<>();
		this.volume = volume;
		this.volumeRemaining = volume;
		this.instrument = instrument;
		this.clientId = clientId;
		this.status = "N";
		price = 0f;
	}

	public Order(String stringOrder)
	{
		String[] parts = stringOrder.split(delimiter);
		stageAll(parts);
//		switch (parts.length)
//		{
//		case 5:
//			stageOne(parts);
//			break;
//		case 6:
//			stageTwo(parts);
//			break;
//		default:
//			System.err.println("BAD ORDER: " + stringOrder);
//		}
	}

	public void stageOne(String[] parts)
	{
		this.clientId = Integer.parseInt(parts[0]);
		this.orderId = Integer.parseInt(parts[1]);
		this.volume = Integer.parseInt(parts[2]);
		this.instrument = parts[3];
		this.status = parts[4];
		volumeRemaining = volume;
	}

	public void stageTwo(String[] parts)
	{
		this.clientId = Integer.parseInt(parts[0]);
		this.orderId = Integer.parseInt(parts[1]);
		this.volume = Integer.parseInt(parts[2]);
		this.instrument = parts[3];
		this.status = parts[4];
		this.price = Float.parseFloat(parts[5]);
		volumeRemaining = volume;
	}
	
	public void stageAll(String[] parts)
	{
		try
		{
			this.clientId = Integer.parseInt(parts[0]);
		}
		catch(Exception e){System.err.println("FATAL ERROR IN ORDER BUILD -> clientID");}
		try
		{
			this.orderId = Integer.parseInt(parts[1]);
		}
		catch(Exception e){System.err.println("FATAL ERROR IN ORDER BUILD -> orderID");}
		try
		{
			this.volume = Integer.parseInt(parts[2]);
			volumeRemaining = volume;
		}
		catch(Exception e){System.err.println("FATAL ERROR IN ORDER BUILD -> volume");}
		try
		{
			this.instrument = parts[3];
		}
		catch(Exception e){System.err.println("FATAL ERROR IN ORDER BUILD -> instrument");}
		try
		{
			this.status = parts[4];
		}
		catch(Exception e){System.err.println("FATAL ERROR IN ORDER BUILD -> status");}
		try
		{
			this.price = Float.parseFloat(parts[5]);
		}
		catch(Exception e){price = 0f;}
		try
		{
			slices = new ArrayList<Slice>();
			rebuildSlices(parts[6]);
		}
		catch(Exception e){}
	}

	@Override
	public String toString()
	{
		String stringOrder = "";
		try
		{
			stringOrder += clientId + delimiter;
			stringOrder += orderId + delimiter;
			stringOrder += volume + delimiter;
			stringOrder += instrument + delimiter;
			stringOrder += status + delimiter;
			stringOrder += price + delimiter;
			stringOrder += makeSliceString();
		} catch (Exception e)
		{

		}
		return stringOrder;
	}
	
	private void rebuildSlices(String sliceString)
	{
		String[] parts = sliceString.split(sliceDelimiter);
		for(String s : parts)
		{
			Slice slice = new Slice(s);
			slices.add(slice);
		}
	}

	private String makeSliceString()
	{
		String sliceString = "";

		for (Slice slice : slices)
		{
			sliceString += slice.toString() + sliceDelimiter;
		}
		try
		{
			sliceString = sliceString.substring(0, sliceString.length() - 1);
		} catch (Exception e)
		{
		}

		return sliceString;
	}

	// Getters and Setters

	public int getVolume()
	{
		return volume;
	}

	public void setVolume(int volume)
	{
		this.volume = volume;
	}

	public String getInstrument()
	{
		return instrument;
	}

	public void setInstrument(String instrument)
	{
		this.instrument = instrument;
	}

	public int getClientId()
	{
		return clientId;
	}

	public void setClientId(int clientId)
	{
		this.clientId = clientId;
	}

	public int getId()
	{
		return orderId;
	}

	public void setId(int orderId)
	{
		this.orderId = orderId;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getStatus()
	{
		return status;
	}

	public void setPrice(float price)
	{
		this.price = price;
	}

	public float getPrice()
	{
		return price;
	}

	public void newSlice(int sliceVolume, float slicePrice)
	{
		slices.add(new Slice(slices.size(), ((sliceVolume < volumeRemaining) ? sliceVolume : volumeRemaining),
				slicePrice));
		volumeRemaining -= ((sliceVolume < volumeRemaining) ? sliceVolume : volumeRemaining);
	}

	public void newSlice(int sliceVolume)
	{
		slices.add(new Slice(slices.size(), ((sliceVolume < volumeRemaining) ? sliceVolume : volumeRemaining), price));
		volumeRemaining -= ((sliceVolume < volumeRemaining) ? sliceVolume : volumeRemaining);
	}
	
	public synchronized void incrementPendingReply()
	{
		price = 100;
		pendingReplies += 1;
	}
	
	public synchronized boolean decrementPendingReply(float newPrice)
	{
		if(newPrice < price){price = newPrice;}
		pendingReplies -= 1;
		return pendingReplies == 0;
	}
}