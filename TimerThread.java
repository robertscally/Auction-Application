import java.net.*;
import java.io.*;

/******************************************
*
*	Programmer: Robert Scally
*
*	Description: Thread class used as a 
*				 timer to count down an
*				 auction bid time
*
*
******************************************/

public class TimerThread extends Thread
{  
   // variable used to store the server object
   private AuctionServer	server = null;

   // constructor
   public TimerThread(AuctionServer _server)
   {  
	  server = _server;
	  
      start();
   }
   
   // thread run function
   public void run()
   {
	   // the MAX time allowed
	   int theTime = 60;
	   
	   // loop until the time has reached 0
	   while(theTime > 0)
	   {  
		   // send a message to all clients to inform them
		   // about the remaining bid time
		   server.broadcastServerMessage("Bid Time Remaining: " + theTime + " secs");
				
		   try
		   {
			   // make thread sleep for 10 seconds
			   Thread.sleep(10000);
		   }
		   catch (InterruptedException e) // thread interrupt exception
		   { 
				// if the thread is interupted by the AuctionServer class
				// then break out of the loop
				break; 
		   }
			   
		   // decrement the time by 10 secs
		   theTime = theTime - 10;
		   
		   // if the time is 0 secs, start a new auction
		   if(theTime == 0)
		   {
			   server.startNewAuction(); // start new auction on the server
		   }
	   }
   }
}



