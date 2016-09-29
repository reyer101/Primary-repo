/**
*	Blue Client
*	Connects to a UDP Server
*	Receives a line of input from the keyboard and sends it to the server
*	If the client is the first to connect, it waits for a response.
* 	If the client is the second to connect or a response has been received, the client then sends messages to the sever until "Goodbye"
*
*	@author: Alec Reyerson
*	Partner: Sean Keelan
*	@version: 2.1
*/

import java.io.*;
import java.net.*;


class Blue {
	public static void main(String args[]) throws Exception
	{
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		int state = 0;
		String message = "HELLO Blue";
		String response = "";
		DatagramPacket sendPacket = null;
		DatagramPacket receivePacket = null;
		//Boolean isFirstClient = false;	
		//Main loop:

		while(state<3)
		{		
			sendData = new byte[1024];
			receiveData = new byte[1024];
			switch (state)
			{
				case 0: // send initial message to server and wait for response

				sendData = message.getBytes();
				sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
				clientSocket.send(sendPacket);

				receivePacket = new DatagramPacket(receiveData, receiveData.length);

	      		clientSocket.receive(receivePacket);
	      		response = new String(receivePacket.getData());
	      		System.out.println("FROM SERVER: " + response);


				if (response.substring(0,3).equals("100")) 
				{					
					state = 1; //You are first client. wait for second client to connect
				}
				else if (response.substring(0,3).equals("200"))
				{					
					state = 2; //you are second client. Wait for message from first client
				}
				break;

				case 1: // Waiting for notification that the second client is ready
					System.out.println("In case 1:");
					while(!response.substring(0,3).equals("200"))
					{
						System.out.println("In loop case 1");
						receivePacket = new DatagramPacket(receiveData, receiveData.length);

	      				clientSocket.receive(receivePacket);
	      				response = new String(receivePacket.getData());
	      				System.out.println("FROM SERVER: " + response);
					}	
					//get message from user and send it to server
					//System.out.println("Enter the first message");
					message = inFromUser.readLine();
					sendData = message.getBytes();
					sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
					clientSocket.send(sendPacket);

					//System.out.println("Out of while case 1");
					state = 2; //transition to state 2: chat mode
					break;
				case 2:
					System.out.println("In case 2:");
					//Chat mode					
					//Chat mode //receive message from other client
					response = "";					
					while(response.equals(""))
					{
						receivePacket = new DatagramPacket(receiveData, receiveData.length);
	      				clientSocket.receive(receivePacket);
	      				response = new String(receivePacket.getData());	      					
					}
					System.out.println("FROM SERVER: " + response);					
					
					//check for Goodbye message
					if (response.length()>=7 && response.substring(0,7).equals("Goodbye"))
					{
						state = 3; //prepare to exit the while loop
						break;
					}
					//if not Goodbye, get next message from user and send it;
					message = inFromUser.readLine();
					sendData = message.getBytes();
					sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
					clientSocket.send(sendPacket);
					//stay in state 2
					break;
			} //end switch
		} // end while
		//close the socket
		clientSocket.close();
	}
}