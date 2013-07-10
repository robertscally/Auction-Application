import java.net.*;
import java.io.*;

public class AuctionClientThread extends Thread
{  
   private Socket           socket   = null;
   private AuctionClient    client   = null;
   private DataInputStream  streamIn = null;

   public AuctionClientThread(AuctionClient _client, Socket _socket, String clientName)
   {  
	  client   = _client;
      socket   = _socket;
      
      open();
      start();
   }
   
   /* Function to get input from the client */
   public void open()
   {  try
      {
		  streamIn  = new DataInputStream(socket.getInputStream());
      }
      catch(IOException ioe)
      {
		 System.out.println("Error getting input stream: " + ioe);
         client.stop();
      }
   }
   
   /* Function to close input stream from client */
   public void close()
   {  try
      {  
		if (streamIn != null) streamIn.close();
      }
      catch(IOException ioe)
      {  System.out.println("Error closing input stream: " + ioe);
      }
   }

   public void run()
   {
	   // listen for incoming messages from the client
	   while (true && client!= null)
	   {
		  try 
		  {
			  client.handle(streamIn.readUTF());
          }
          catch(IOException ioe)
          {
			  client = null;
			  System.out.println("Listening error: " + ioe.getMessage());

         }
      }
   }
}



