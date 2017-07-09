package DataStore;

public class Slice
{
	private int volume;
	private int id;
	private boolean filled;
	private float price;
	private static final String delimiter = "*";
	
	public Slice(int id, int volume, float price)
	{
		this.volume = volume;
		this.id = id;
		this.price = price;
		filled = false;
	}
	
	public Slice(String stringSlice)
	{
		String[] parts = stringSlice.split(delimiter);
		id = Integer.parseInt(parts[0]);
		volume = Integer.parseInt(parts[1]);
		price = Float.parseFloat(parts[2]);
		filled = Boolean.parseBoolean(parts[3]);
	}
	
	public int getVolume()
	{
		return volume;
	}
	
	public int getId()
	{
		return id;
	}
	
	public boolean isFilled()
	{
		return filled;
	}
	
	public void setFilled(boolean filled)
	{
		this.filled = filled;
	}
	
	@Override
	public String toString()
	{
		String stringSlice = "";
		
		stringSlice += id		+ delimiter;
		stringSlice += volume	+ delimiter;
		stringSlice += price	+ delimiter;
		stringSlice += filled;
				
		return stringSlice;
	}
}
