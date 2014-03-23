package remote.server;

import java.awt.AWTException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;

public class Server extends Thread {
	private PrivateKey privateKey;
	private ServerSocket serverSocket;
	private Socket oldSocket;
	private ServerHandler handler;

	public Server(PrivateKey privateKey, int port) throws IOException,
			AWTException {
		this.privateKey = privateKey;
		serverSocket = new ServerSocket(port);
		handler = new ServerHandler();
	}

	@Override
	public void run() {
		try {
			ClientConnection cc = null;
			while (true) {
				// Accept client connections
				Socket socket = serverSocket.accept();
				// Clean up old connection
				if (oldSocket != null) {
					try {
						oldSocket.close();
					} catch (IOException e) {
						// Ignore
					}
				}
				if (cc != null) {
					// Close old worker
					cc.close();
				}
				oldSocket = socket;
				cc = new ClientConnection(handler, privateKey, socket);
				cc.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ServerMain.exit();
	}
}
