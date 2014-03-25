package remote.server;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;

public class ServerGui {
	enum GuiState {
		ACTIVE, PAUSED, WAITING
	};

	private final Image activeImage;
	private final Image pausedImage;
	private final Image waitingImage;
	private final SystemTray tray = SystemTray.getSystemTray();
	private final TrayIcon trayIcon;

	private GuiState state = GuiState.WAITING;

	public ServerGui() {
		activeImage = find("res/active.png", "Active").getImage();
		pausedImage = find("res/paused.png", "Paused").getImage();
		waitingImage = find("res/waiting.png", "Waiting").getImage();
		trayIcon = new TrayIcon(waitingImage, "Remote control");
	}

	private ImageIcon find(String path, String description) {
		URL url = getClass().getResource('/' + path);
		if (url == null) {
			// Fall back to local path
			return new ImageIcon(path, description);
		}
		return new ImageIcon(url, description);
	}

	public void show() {
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
					state = GuiState.PAUSED;
					break;
				case PAUSED:
					trayIcon.setImage(waitingImage);
					state = GuiState.WAITING;
					break;
				case WAITING:
					trayIcon.setImage(activeImage);
					state = GuiState.ACTIVE;
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

	public void exit() {
		tray.remove(trayIcon);
		System.exit(0);
	}
}
