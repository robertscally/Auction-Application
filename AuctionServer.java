import java.net.*;
import java.io.*;
import java.util.Timer;

public class AuctionServer implements Runnable
{  	
   private AuctionServerThread clients[] = new AuctionServerThread[50]; // Array of clients
   private Item          saleItemArray[] = new Item[50]; // Array of items on sale
   private BufferedReader        console = null; // console used for user input from the command line
   private ServerSocket           server = null;
   private Thread                 thread = null;
   
   // count used to store number of clients connected to the auction server
   private static int clientCount = 0;
   
   // ROBERT SCALLY CODE: Added following variables
   private TimerThread timerThread = null;
   private static Item    saleItem = new Item();
   private static int auctionCount = 0;
   private static int   timerCount = 0;
   
   public AuctionServer(int port)
   {
	  try 
	  {
		 // open new server socket with provided port
		 System.out.println("Binding to port " + port + ", please wait  ...");
         server = new ServerSocket(port);
         System.out.println("Server started: " + server.getInetAddress());
         
         // array of items for sale
         saleItemArray[0] = new Item("BMW", 120, 120, 0); 
         saleItemArray[1] = new Item("Painting", 50, 50, 0); 
         saleItemArray[2] = new Item("Truck", 40, 40, 0); 
         saleItemArray[3] = new Item("Shoes", 10, 10, 0); 
         
		 // store first sale item in an array of sale items
         saleItem = saleItemArray[0];
         
		 // start the thread
         start();
      }
      catch(IOException ioe)
      {
		  System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());

      }
   }

   /* Function to run the thread and check for incoming client connection requests */
   public void run()
   {
	  while (thread != null)
      {
		 try{

			System.out.println("Waiting for a client ...");
            addThread(server.accept());
			
			// pause the thread for a random number of seconds
			int pause = (int)(Math.random()*3000);
			Thread.sleep(pause);

         }
         catch(IOException ioe)
		 {
			System.out.println("Server accept error: " + ioe);
			stop();
         }
         catch (InterruptedException e)
		 {
		 	System.out.println(e);
		 }
      }
   }

  public void start()
    {
		// if the thread is null, start a new thread
		if (thread == null) 
		{
		  thread = new Thread(this);
          thread.start();
       }
    }


   public void stop()
   {
	   // stops the thread
	   thread = null;
   }

   /* Function to find a client when provided with an ID */
   private int findClient(int ID)
   {
	   // loop through the array of client threads to find the client
	   // with the same ID as that provided
	   for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;
   }
   
   /* ROBERT SCALLY: Function to broadcast a message to all the clients */
   public synchronized void broadcastServerMessage(String message)
   {
	   // loop through all the clients and send them the message which
	   // is passed into the function 
	   for (int i = 0; i < clientCount; i++)
	   {
		   clients[i].send(message); // sends messages to clients
       }
   }

   public synchronized void broadcast(int ID, String theInput)
   {	
       // ROBERT SCALLY: boolean variable to store true or false state of bid
	   //                entered by the user
	   boolean validBid = true;
	   String input = theInput;
   
	   
   
	   // if the input from the client is ".bye" then send back the
	   // same message to that client and remove their thread
       if(input.equals(".bye"))
	   {
		  clients[findClient(ID)].send(".bye");
          remove(ID);
	   }
	   else
       {   
		   // ROBERT SCALLY: check the client bid to verify if it is valid
		   validBid = checkClientBid(input);
		   
		    // if the bid is valid, i.e. higher than current bid
			if(validBid)
			{
				// set the new high bid and bidder id for the item
				saleItem.setHighBid(input);
				saleItem.setBidderID(ID);
				
				// loop through all clients
				for (int i = 0; i < clientCount; i++)
				{
					//
					if(clients[i].getID() != ID)
					{
						clients[i].send(ID + ": " + "has entered a NEW High Bid of " + input + "\nEnter new bid:"); // sends messages to clients
					}
				}
			
				// send message to all connected clients
				notifyAll();
			}
			
			// tell client they have entered an invalid bid
			else if(!validBid)
			{
				clients[findClient(ID)].send("Invalid Bid. Try Again!");
				clients[findClient(ID)].send("Enter new bid: ");
			}
	   }
   }
   
   /* Function to remove a client thread  */
   public synchronized void remove(int ID)
   {
	  int pos = findClient(ID);
      
	  if (pos >= 0)
	  {
		 AuctionServerThread toTerminate = clients[pos];
         System.out.println("Removing client thread " + ID + " at " + pos);

         if (pos < clientCount-1)
            for (int i = pos+1; i < clientCount; i++)
               clients[i-1] = clients[i];
         clientCount--;

         try
         {
			 toTerminate.close();
	     }
         catch(IOException ioe)
         {
			 System.out.println("Error closing thread: " + ioe);
		 }
         
		 toTerminate = null;
		 System.out.println("Client " + pos + " removed");
		 notifyAll();
      }
   }

   /* Function to add a new thread */
   private void addThread(Socket socket)
   {
	  // if the clients connected have not reached the maximum allowed (50)
	  if (clientCount < clients.length)
	  {  
		 System.out.println("Client accepted: " + socket);
         clients[clientCount] = new AuctionServerThread(this, socket);

         try
         {
			clients[clientCount].open();
            clients[clientCount].start();
			            
			// ROBERT SCALLY: Send item for sale and highest bid to newly connected client
			clients[clientCount].send("Item: " + saleItem.getName() + "\nHighest Bid: " + saleItem.getHighBid()
									   + "\nEnter a bid:");
			
			// increment the client counter
            clientCount++;
         }
         catch(IOException ioe)
		 {
			 System.out.println("Error opening thread: " + ioe);
		 }
	  }
      else
      {
         System.out.println("Client refused: maximum " + clients.length + " reached.");
      }
   }

   // ROBERT SCALLY: Function to check if clients bid is valid
   private boolean checkClientBid(String input)
   {   
		boolean validBid;
		
		// get the highest bid of the item on sale
		int highestBid = saleItem.getHighBid();
		
		// declare a bid variable and initialize it
		int bid = 0;
		
		// try to parse an integer from the string input provided
		// by the user
		try
	    {
			bid = Integer.parseInt(input);
	    }
	    catch (NumberFormatException e) 
	    {
			// if non-numeric values are entered by the user
			// set the bid to be -1
            bid = -1;
        }
		
		// check if clients bid is greater than current highest bid
		if(bid <= highestBid)
		{
			validBid = false;
		}
		else
		{
			// if the timer thread is null, create a new timer thread
			if(timerThread == null)
			{
				timerThread = new TimerThread(this);
			}
			// else if the timer thread is already running
			else
			{
				timerThread.interrupt(); // interrupt the thread
				timerThread = null; // set the thread to be null i.e. stop the thread
				timerThread = new TimerThread(this); // create a new timer thread
			}
			
			// set valid bid to be true as the client has successfully
			// bid higher than the current highest bid
			validBid = true;
		}
		
		return validBid;
   }
   
   // ROBERT SCALLY: Function to start a new auction 
   public void startNewAuction()
   {   
	   // increment auction count
	   auctionCount++;
	   
	   // if the sale item array is not null
	   if(saleItemArray[auctionCount] != null)
	   {
		   
	   
		   // loop through the clients to send them all a message
		   // about the item which was sold and the ID of the user.
		   // then begin to 
		   for (int i = 0; i < clientCount; i++)
		   {
			   clients[i].send("------------ ITEM SOLD TO " + saleItem.getBidderID() + " ------------"); // sends messages to clients
			   clients[i].send("------------ NEW AUCTION HAS BEGUN ------------"); // sends messages to clients
		   }
			
			   // set the next sale item in the array of sale items to be 
			   // the current sale item
			   saleItem = saleItemArray[auctionCount];
		   
		   // loop through all clients and send them message about new sale item
		   for (int i = 0; i < clientCount; i++)
		   {
			   clients[i].send("Item: " + saleItem.getName() + "\nHighest Bid: " + saleItem.getHighBid() + "\nEnter a bid:");
		   }
	   }
	   else
	   {
		   for (int i = 0; i < clientCount; i++)
		   {
			   clients[i].send("------------ ITEM SOLD TO " + saleItem.getBidderID() + " ------------"); // sends messages to clients
			   clients[i].send("------------ AUCTION HAS ENDED. NO MORE ITEMS FOR SALE ------------"); 
		   }
	   }
   }

   public static void main(String args[]) 
   {
	   AuctionServer server = null;
	   
      if (args.length != 1)
      {
         System.out.println("Usage: java AuctionServer port");
      }
      else
      {
         server = new AuctionServer(Integer.parseInt(args[0]));
      }
   }

}