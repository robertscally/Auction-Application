import java.net.*;
import java.io.*;

public class AuctionClient implements Runnable
{  
   private Socket socket               = null;
   private Thread thread               = null;
   private BufferedReader  console     = null;
   private DataOutputStream streamOut  = null;
   private AuctionClientThread client  = null;
   private String clientName;

   public AuctionClient(String serverName, int serverPort)
   {
	  System.out.println("Establishing connection. Please wait ...");
	  
	  // try to connect to server using a socket
      try
	  {
		 socket = new Socket(serverName, serverPort);
         System.out.println("Connected: " + socket);
         start();
      }
      catch(UnknownHostException uhe)
	  {
		  System.out.println("Host unknown: " + uhe.getMessage());
	  }
      catch(IOException ioe)
	  {
		  System.out.println("Unexpected exception: " + ioe.getMessage());
	  }
   }
   
   // ROBERT SCALLY: Get the clients name function
   public String getClientName()
   {
	   return clientName;
   }

   public void run()
   {
	   while (thread != null)
	   {
		 try 
		 {
		    // read input from client
			String message = console.readLine();
			streamOut.writeUTF(message); // write the message entered by the client to the output stream
            streamOut.flush();
         }
         catch(IOException ioe)
         {  
			System.out.println("Sending error: " + ioe.getMessage());
            stop();
         }
      }
   }

   /* Function to handle messages entered by user */
   public void handle(String msg)
   {  
      // if message is ".bye" stop the client thread
	  if (msg.equals(".bye"))
      {  
		 System.out.println("Good bye. Press RETURN to exit ...");
         stop();
      }
      else
         System.out.println(msg); // otherwise print the message to the clients window
   }

   public void start() throws IOException
   {
	  // read input from the user
	  console = new BufferedReader(new InputStreamReader(System.in));

      streamOut = new DataOutputStream(socket.getOutputStream());
      
	  // create a new thread 
	  if (thread == null)
      {  
		 client = new AuctionClientThread(this, socket, clientName);
         thread = new Thread(this);
         thread.start();
      }
   }

   /* Stop the thread */
   public void stop()
   {
      try
      {  if (console   != null)  console.close();
         if (streamOut != null)  streamOut.close();
         if (socket    != null)  socket.close();
      }
      catch(IOException ioe)
      {
		  System.out.println("Error closing ...");
      }
	  
      client.close();
      
	  thread = null;
   }

   /* Main function */
   public static void main(String args[])
   {  
		AuctionClient client = null;
		
		// if the number of arguments provided is not equal to 2
		// print a message to user to explain usage 
		if (args.length != 2)
		{
			System.out.println("Usage: java AuctionClient host port");
		}
		else
		{
			client = new AuctionClient(args[0], Integer.parseInt(args[1]));
		}
   }
}
