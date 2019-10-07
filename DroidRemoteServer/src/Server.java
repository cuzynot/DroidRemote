import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	// initialize socket and input stream 
	private int port = 9997; /////////////////////////////////////////
	private Socket socket; 
	private ServerSocket server; 
	private DataInputStream input;

	// position vars
	private double x, y, z;

	// other vars
	private boolean isActive;

	public static void main(String[] args) {
		new Server();
	}

	// constructor with port 
	public Server() { 
		// starts server and waits for a connection 
		try { 
			isActive = true;

			server = new ServerSocket(port); 
			System.out.println("Server started\nWaiting for a client ..."); 

			// accept client
			socket = server.accept(); 
			System.out.println("Client accepted"); 

			// takes input from the client socket 
			input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

			InputThread it = new InputThread();
			it.start();

			ScrollThread st = new ScrollThread();
			st.start();


		} catch(IOException i) { 
			System.out.println(i); 
		} 
	}

	private void closeConnections() {
		try {
			socket.close();
			input.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private class ScrollThread extends Thread {

		private Robot robot;
		private int scrollDelay = 15;
		private final double X_FACTOR = 5;

		public ScrollThread() {
			try {
				robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			while (isActive) {
//				scrollDelay = (int)(10 / Math.abs(x));
//
//				System.out.println(scrollDelay);
//				if (scrollDelay > 30 && scrollDelay < 200) {
//					// System.out.println("rather neutral");
//					//					if (x < 0) {
//					//						robot.mouseWheel(-1);
//					//					} else {
//					//						robot.mouseWheel(1);
//					//					}
//					robot.mouseWheel((int)(Math.round(x * X_FACTOR)));
//					try {
//						Thread.sleep(scrollDelay);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}

				robot.mouseWheel((int)(Math.round(x * X_FACTOR)));
				try {
					Thread.sleep(scrollDelay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}

		}
	}

	private class InputThread extends Thread{
		public void run() {
			while (isActive) { 
				try { 
					String line = input.readUTF();

					char c = line.charAt(0);

					if (c == 'x') {
						x = Double.parseDouble(line.substring(2));
					} else if (c == 'y') {
						y = Double.parseDouble(line.substring(2));
					} else if (c == 'z') {
						z = Double.parseDouble(line.substring(2));
					}
					//					System.out.println(x + " " + y + " " + z);
				} catch(IOException e) { 
					System.out.println(e); 
					break;
				}
			}
			System.out.println("end of reading");
			closeConnections();
		}
	}

}
