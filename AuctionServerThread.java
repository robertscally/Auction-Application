import java.net.*;
import java.io.*;

public class AuctionServerThread extends Thread
{  
   // variables
   private AuctionServer    server    = null;
   private Socket           socket    = null;
   private int              ID        = -1;
   private String           name        = "";
   private DataInputStream  streamIn  =  null;
   private DataOutputStream streamOut = null;
   private Thread thread;

   public AuctionServerThread(AuctionServer _server, Socket _socket)
   {
	  super();
      server = _server;
      socket = _socket;
      ID     = socket.getPort();

   }
   
   public void send(String msg)
   {
	   // try to write message to the output stream
	   try
	   {
		  streamOut.writeUTF(msg);
          streamOut.flush();
       }
       catch(IOException ioe)
       {
		  System.out.println(ID + " ERROR sending: " + ioe.getMessage());
          server.remove(ID);
          thread=null;
       }
   }
   
   /* ROBERT SCALLY: Get the clients name function */
   public String getClientName()
   {
	   return name;
   }
   
   /* ROBERT SCALLY: Set the clients name function */
   public String setClientName(String name)
   {
	   return name;
   }
   
   /* Function to get the ID of the thread */
   public int getID()
   {
	   return ID;
   }

   public void run()
   {
	  System.out.println("Server Thread " + ID + " running.");
	  thread = new Thread(this);
      while (true)
	  {
		 try
		 {
			 server.broadcast(ID, streamIn.readUTF()); // broadcast all messages recieved by client to all other clients

			 // pause the thread for a random number of secs
         	 int pause = (int)(Math.random()*3000); 
		 	 Thread.sleep(pause);
		 }
		 catch (InterruptedException e)
		 {
		 	System.out.println(e);
		 }
         catch(IOException ioe)
		 {
            server.remove(ID);
            thread = null;
         }
      }
   }

   public void open() throws IOException
   {
	  streamIn = new DataInputStream(new
                        BufferedInputStream(socket.getInputStream()));
      streamOut = new DataOutputStream(new
                        BufferedOutputStream(socket.getOutputStream()));
   }

   /* Function to close the socket and input and output data streams */
   public void close() throws IOException
   {
	   if (socket != null)
	   	socket.close();

      if (streamIn != null)
      	streamIn.close();

      if (streamOut != null)
      	streamOut.close();
   }
}