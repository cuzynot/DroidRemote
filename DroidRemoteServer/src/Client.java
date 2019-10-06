import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

	private String address = "127.0.0.1";//172.18.55.249";
	private int port = 5005;
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	

	public static void main(String[] args) {
		new Client();
	}

	// constructor
	public Client() {
		
		try {
            socket = new Socket(address, port);
           	System.out.println("rand" + "Connected");

            // takes input from terminal
            input = new DataInputStream(System.in);

            // sends output to the socket
            output = new DataOutputStream(socket.getOutputStream());

            String line = "";

            // keep reading until "Over" is input
            while (!line.equals("Over")){
                try {
                    Thread.sleep(1000);
                    // line = input.readLine();
                    output.writeUTF("yeet");
                } catch(IOException i) {
                    System.out.println(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch(IOException i) {
            System.out.println(i);
        }
		
//		try { 
//			socket = new Socket(address, port); 
//			System.out.println("Connected"); 
//
//			// takes input from terminal 
//			input = new DataInputStream(System.in); 
//
//			// sends output to the socket 
//			output = new DataOutputStream(socket.getOutputStream()); 
//			
//
//			String line = "";
//			// reads message from client until "Over" is sent 
//            while (!line.equals("Over")) { 
//                try { 
//                    line = input.readUTF(); 
//                    System.out.println(line); 
//  
//                } catch(IOException i) { 
//                    System.out.println(i); 
//                } 
//            } 
//		} catch(UnknownHostException u) { 
//			System.out.println(u); 
//		} catch(IOException i) { 
//			System.out.println(i); 
//		} 

		
        
//        Scanner sc = new Scanner(System.in);
//        
//        for (int i = 0; i < 100; i++) {
//        	try {
//				output.writeUTF(sc.nextLine());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        }

		// close the connection 
//		try { 
//			input.close(); 
//			output.close(); 
//			socket.close(); 
//		} catch(IOException i) { 
//			System.out.println(i); 
//		} 
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
