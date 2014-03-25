package remote.server;

import java.awt.AWTException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;

import javax.swing.SwingUtilities;

import remote.api.Packet;

public class ServerMain {

	public static void main(String[] args) {
		// try {
		// PrivateKeyGenerator.generate();
		// } catch (NoSuchAlgorithmException e) {
		// e.printStackTrace();
		// }
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ServerGui gui = new ServerGui();
				try {
					KeyFactory keyFactory = KeyFactory
							.getInstance(Packet.SECURE_ALGORITHM_NAME);
					PrivateKey privateKey = keyFactory
							.generatePrivate(new RSAPrivateKeySpec(
									new BigInteger(Config.MODULUS),
									new BigInteger(Config.PRIVATE_EXPONENT)));
					new Server(gui, privateKey, 9456).start();
					gui.show();
				} catch (IOException | GeneralSecurityException | AWTException e) {
					e.printStackTrace();
					gui.exit();
				}
			}
		});
	}
}
