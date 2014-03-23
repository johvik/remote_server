package remote.server;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import remote.api.Packet;

public class ServerMain {
	enum UiState {
		ACTIVE, PAUSED, WAITING
	};

	private static UiState state = UiState.WAITING;

	private static final Image activeImage = new ImageIcon("res/active.png",
			"Active").getImage();
	private static final Image pausedImage = new ImageIcon("res/paused.png",
			"Paused").getImage();
	private static final Image waitingImage = new ImageIcon("res/waiting.png",
			"Waiting").getImage();
	private static final SystemTray tray = SystemTray.getSystemTray();
	private static final TrayIcon trayIcon = new TrayIcon(waitingImage,
			"Remote control");

	public static void main(String[] args) {
		// try {
		// PrivateKeyGenerator.generate();
		// } catch (NoSuchAlgorithmException e) {
		// e.printStackTrace();
		// }
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					KeyFactory keyFactory = KeyFactory
							.getInstance(Packet.SECURE_ALGORITHM_NAME);
					PrivateKey privateKey = keyFactory
							.generatePrivate(new RSAPrivateKeySpec(
									new BigInteger(Config.MODULUS),
									new BigInteger(Config.PRIVATE_EXPONENT)));
					new Server(privateKey, 9456).start();
					createAndShowGUI();
				} catch (IOException | GeneralSecurityException | AWTException e) {
					e.printStackTrace();
					exit();
				}
			}
		});
	}

	private static void createAndShowGUI() {
		// Check the SystemTray support
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}

		final PopupMenu popup = new PopupMenu();

		// Create a popup menu components
		MenuItem exitItem = new MenuItem("Exit");

		// Add components to popup menu
		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
			return;
		}

		trayIcon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Dummy state transition
				switch (state) {
				case ACTIVE:
					trayIcon.setImage(pausedImage);
					state = UiState.PAUSED;
					break;
				case PAUSED:
					trayIcon.setImage(waitingImage);
					state = UiState.WAITING;
					break;
				case WAITING:
					trayIcon.setImage(activeImage);
					state = UiState.ACTIVE;
					break;
				}
			}
		});

		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
	}

	public static void exit() {
		tray.remove(trayIcon);
		System.exit(0);
	}
}
