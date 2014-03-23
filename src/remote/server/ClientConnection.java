package remote.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import remote.api.Packet;
import remote.api.ServerProtocol;
import remote.api.Protocol.PingCallback;
import remote.api.exceptions.PacketException;
import remote.api.exceptions.ProtocolException;

public class ClientConnection extends Thread implements
		ServerProtocol.ConnectionHandler, PingCallback {
	private ServerProtocol protocol;
	private Socket socket;
	private InputStream input;
	private OutputStream output;
	private boolean pingDone = true;

	public ClientConnection(ServerHandler handler, PrivateKey key, Socket socket)
			throws IOException, GeneralSecurityException, ProtocolException,
			PacketException {
		this.socket = socket;
		input = socket.getInputStream();
		output = socket.getOutputStream();
		protocol = new ServerProtocol(handler, this, key, input, output);
	}

	@Override
	public void run() {
		try {
			Packet p;
			while ((p = protocol.nextPacket()) != null) {
				protocol.process(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
		System.out.println("ClientConnection done");
	}

	public void close() {
		try {
			if (socket != null) {
				input.close();
				output.close();
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			input = null;
			output = null;
			socket = null;
		}
	}

	@Override
	public void onAuthenticated() {
		// Start to ping client
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						Thread.sleep(1000);
						if (pingDone) {
							protocol.ping(ClientConnection.this);
							pingDone = false;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				close();
			}
		}).start();
	}

	@Override
	public void run(long diff) {
		// System.out.println("Ping: " + Math.round(diff / 10000.0) / 100.0);
		pingDone = true;
	}
}
