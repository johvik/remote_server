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
		ACTIVE, PAUSED
	};

	private final Image activeImage;
	private final Image pausedImage;
	private final SystemTray tray = SystemTray.getSystemTray();
	private final TrayIcon trayIcon;

	private GuiState state = GuiState.ACTIVE;

	public ServerGui() {
		activeImage = find("res/active.png", "Active").getImage();
		pausedImage = find("res/paused.png", "Paused").getImage();
		trayIcon = new TrayIcon(activeImage, "Remote control");
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
				// Pause/unpause
				if (state == GuiState.PAUSED) {
					state = GuiState.ACTIVE;
					trayIcon.setImage(activeImage);
				} else {
					state = GuiState.PAUSED;
					trayIcon.setImage(pausedImage);
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

	public boolean isActive() {
		return state == GuiState.ACTIVE;
	}

	public void exit() {
		tray.remove(trayIcon);
		System.exit(0);
	}
}
