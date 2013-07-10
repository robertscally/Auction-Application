import java.io.*;

/******************************************
*
*	Programmer: Robert Scally
*
*	Description: Models a sale item which 
*				 is placed on an auction.
*
*
******************************************/

class Item implements Serializable
{
	// variables
	private String item_name;
	private int highestBid;
	private int reservePrice;
	private int bidderID;

	/* default constructor */
	public Item()
	{
		item_name = "test";
		highestBid = 0;
		reservePrice = 0;
		bidderID = 0;
	}
	
	/* constructor */
	public Item(String aItemName, int aHighBid, int aReservePrice, int aBidderID)
	{
		item_name = aItemName;
		highestBid = aHighBid;
		reservePrice = aReservePrice;
		bidderID = aBidderID;
	}

	/* Function to get name of sale item */
	public String getName()
	{
		return item_name;
	}
	
	/* Function to set the name of the sale item */
	public void setName(String name)
	{
		item_name = name;
	}
	
	/* Function to get the highest bid for a sale item */
	public int getHighBid()
	{
		return highestBid;
	}
	
	/* Function to set the highest bid for a sale item */
	public void setHighBid(String bid)
	{
		// parse the sale item's highest bid and store as an integer
		highestBid = Integer.parseInt(bid);
	}
	
	/* Function to get the highest bidder ID for a sale item */
	public int getBidderID()
	{
		return bidderID;
	}
	
	/* Function to set the highest bidder ID for a sale item */
	public void setBidderID(int aBidderID)
	{
		bidderID = aBidderID;
	}
	
	/* Function to get the reserve price for a sale item */
	public int getReservePrice()
	{
		return reservePrice;
	}
	
	/* Function to set the reserve price for a sale item */
	public void setReservePrice(String price)
	{
		// parse the sale item price and store as an integer
		reservePrice = Integer.parseInt(price);
	}
}
