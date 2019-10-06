import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Test {
	
	//initialize socket and input stream 
	private int port = 9999; /////////////////////////////////////////
    private Socket socket = null; 
    private ServerSocket server = null; 
    private DataInputStream input = null; 
    
    public static void main(String[] args) {
    	new Test();
    }
  
    // constructor with port 
    public Test() { 
        // starts server and waits for a connection 
        try { 
            server = new ServerSocket(port); 
            System.out.println("Server started"); 
  
            System.out.println("Waiting for a client ..."); 
  
            socket = server.accept(); 
            System.out.println("Client accepted"); 
  
            // takes input from the client socket 
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            
//            InputThread it = new InputThread();
//            it.start();
  
            String line = ""; 
  
            // reads message from client until "Over" is sent 
            while (!line.equals("Over")) { 
                try { 
                    line = input.readUTF(); 
                    System.out.println(line); 
  
                } catch(IOException i) { 
                    System.out.println(i); 
                } 
            } 
            System.out.println("Closing connection"); 
  
//            // close connection 
//            socket.close(); 
//            in.close(); 
        } catch(IOException i) { 
            System.out.println(i); 
        } 
    }
    
    private class InputThread extends Thread{
		public void run() {
			// string to read message from input 
			String line = ""; 
			
			while (!line.equals("Over")) { 
				try { 
					line = input.readLine();
					// output.writeUTF(line); 
				} catch(IOException i) { 
					System.out.println(i); 
				} 
			}
			
			System.out.println("client read " + line);
		}
	}

}
