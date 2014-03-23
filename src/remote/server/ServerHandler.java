package remote.server;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.nio.charset.StandardCharsets;

import remote.api.ServerProtocol.Handler;
import remote.api.commands.Command;
import remote.api.commands.KeyPress;
import remote.api.commands.KeyRelease;
import remote.api.commands.MouseMove;
import remote.api.commands.MousePress;
import remote.api.commands.MouseRelease;
import remote.api.commands.MouseWheel;
import remote.api.commands.TextInput;

public class ServerHandler implements Handler {
	private Robot robot;
	private Keyboard keyboard;

	public ServerHandler() throws AWTException {
		robot = new Robot();
		keyboard = new Keyboard(robot);
	}

	@Override
	public boolean authentication(byte[] user, byte[] password) {
		String userString = new String(user, StandardCharsets.UTF_8);
		System.out.println("Authentication: " + userString);
		String passwordString = new String(password, StandardCharsets.UTF_8);

		return userString.equals(Config.USER)
				&& passwordString.equals(Config.PASSWORD);
	}

	@Override
	public synchronized void command(Command command) {
		switch (command.getType()) {
		case Command.MOUSE_MOVE:
			MouseMove mm = (MouseMove) command;
			Point p = MouseInfo.getPointerInfo().getLocation();
			p.translate(mm.getDx(), mm.getDy());
			robot.mouseMove(p.x, p.y);
			break;
		case Command.MOUSE_PRESS:
			MousePress mp = (MousePress) command;
			robot.mousePress(mp.getButtons());
			break;
		case Command.MOUSE_RELEASE:
			MouseRelease mr = (MouseRelease) command;
			robot.mouseRelease(mr.getButtons());
			break;
		case Command.MOUSE_WHEEL:
			MouseWheel mw = (MouseWheel) command;
			robot.mouseWheel(mw.getWheelAmt());
			break;
		case Command.KEY_PRESS:
			KeyPress kp = (KeyPress) command;
			robot.keyPress(kp.getKeycode());
			break;
		case Command.KEY_RELEASE:
			KeyRelease kr = (KeyRelease) command;
			robot.keyRelease(kr.getKeycode());
			break;
		case Command.TEXT_INPUT:
			TextInput ti = (TextInput) command;
			String text = new String(ti.getText(), StandardCharsets.UTF_8);
			keyboard.type(text);
			break;
		default:
			System.out.println("Unknown command: " + command);
		}
	}

	@Override
	public synchronized void terminate(boolean shutdown) {
		System.out.println("Terminate: " + shutdown);
		ServerMain.exit();
	}
}
