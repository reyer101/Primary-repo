/**
*	UDP Server Program
*	Listens on a UDP port
*	Receives a line of input from a UDP client
*	Returns an upper case version of the line to the client
*
*	@author: Sean Keelan
@	version: 2.0
*/

import java.io.*;
import java.net.*;

class ChatServer {
  public static void main(String args[]) throws Exception
  {
    DatagramSocket serverSocket = null;

    try
		{
			serverSocket = new DatagramSocket(9876);
		}

    catch(Exception e)
		{
			System.out.println("Failed to open UDP socket");
			System.exit(0);
		}

    /* A basic outline for ChatServer.java can be found on the Day7.pdf file
    provided by Prof. Michael Fahy. */

    String receiveMessage = "";  // Message received from client
    String sendMessage = ""; // Sentence to be sent to a client

    String clientName = ""; // Switches between clientName1 and clientName2
    String clientName1 = "";
    String clientName2 = "";

    InetAddress IPAddress = null;  // Switches between IPAddress1 and IPAddress 2
    InetAddress IPAddress1 = null;
    InetAddress IPAddress2 = null;

    int port = 0;  // Switcehs b/w port1 and port2
    int port1 = 0;
    int port2 = 0;

    byte[] receiveData = new byte[1024];
    byte[] sendData  = new byte[1024];

    int state = 0;

    DatagramPacket receivePacket = null;
    DatagramPacket sendPacket = null;

    while (state < 3)
    {
      receiveData = new byte[1024];
      sendData = new byte[1024];

      switch(state)
      {
        case 0:
          receivePacket = new DatagramPacket(receiveData, receiveData.length);
          serverSocket.receive(receivePacket);

          receiveMessage = new String(receivePacket.getData());
          if(receiveMessage.substring(0,5) == "HELLO")
          {
            if(receiveMessage.substring(6,9) == "Red")
            {
              clientName1 = new String("Red");
            }
            else if(receiveMessage.substring(6,10) == "Blue")
            {
              clientName1 = new String("Blue");
            }
            else
            {
              IPAddress1 = receivePacket.getAddress();
              port1 = receivePacket.getPort();
              sendMessage = new String("Invalid username. Choose between Red or Blue.");
              sendData = sendMessage.getBytes();
              sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress1,
                                              port1);
              serverSocket.send(sendPacket);
              break;
            }
            IPAddress1 = receivePacket.getAddress();
            port1 = receivePacket.getPort();
          }
          sendMessage = new String("100");
          sendData = sendMessage.getBytes();
          sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress1,
                                          port1);
          serverSocket.send(sendPacket);
          state = 1;
          break;

        case 1:
          receivePacket = new DatagramPacket(receiveData, receiveData.length);
          serverSocket.receive(receivePacket);

          receiveMessage = new String(receivePacket.getData());
          if(receiveMessage.substring(0,5) == "HELLO")
          {
            if(receiveMessage.substring(6,9) == "Red" && clientName1 == "Blue")
            {
              clientName2 = new String("Red");
            }
            else if(receiveMessage.substring(6,10) == "Blue" && clientName1 == "Red")
            {
              clientName2 = new String("Blue");
            }
            else
            {
              IPAddress2 = receivePacket.getAddress();
              port2 = receivePacket.getPort();
              sendMessage = new String("Invalid username. Choose between Red or Blue. "
                                    + clientName1 + " is already taken.");
              sendData = sendMessage.getBytes();
              sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress2,
                                              port2);
              serverSocket.send(sendPacket);
              break;
            }

            IPAddress2 = receivePacket.getAddress();
            port2 = receivePacket.getPort();
          }
          sendMessage = new String("200");
          sendData = sendMessage.getBytes();

          // Preparing packet for Client 1
          sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress1,
                                        port1);
          serverSocket.send(sendPacket);  // Sending packet to Client 1

          // Preparing packet for Client 2
          sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress2,
                                        port2);
          serverSocket.send(sendPacket); // Sending packet to Client 2

          state = 2;
          break;

        case 2:
          receivePacket = new DatagramPacket(receiveData, receiveData.length);
          serverSocket.receive(receivePacket);

          receiveMessage = new String(receivePacket.getData());

          /* If a client sends a "Goodbye" message, server/client communication
          closes. */
          if(receiveMessage.length() >= 7 && receiveMessage.substring(0,7) == "Goodbye")
          {
            state = 3;
            break;
          }

          IPAddress = receivePacket.getAddress();
          port = receivePacket.getPort();

          if(port == port1 && (IPAddress.equals(IPAddress1)))
          {
            clientName = clientName1;
            IPAddress = IPAddress2;
            port = port2;
          }
          else
          {
            clientName = clientName2;
            IPAddress = IPAddress1;
            port = port1;
          }
          receiveMessage = new String(receivePacket.getData());
          sendMessage = new String(receiveMessage);
          sendData = sendMessage.getBytes();
          sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,
                                          port);
          serverSocket.send(sendPacket);

          break;
      }
    }
    sendMessage = new String("Goodbye");
    sendData = sendMessage.getBytes();

    //Sending "Goodbye" to Client 1
    sendPacket = new DatagramPacket(sendData, sendData.length,
                                    IPAddress1, port);
    serverSocket.send(sendPacket);

    // Sending "Goodbye" to Client 2
    sendPacket = new DatagramPacket(sendData, sendData.length,
                                    IPAddress2, port);
    serverSocket.send(sendPacket);

    serverSocket.close();
  }
}
