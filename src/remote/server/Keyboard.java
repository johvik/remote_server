package remote.server;

import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Keyboard {
	private Robot robot;

	public Keyboard(Robot robot) {
		this.robot = robot;
	}

	public void type(String text) {
		int length = text.length();
		for (int i = 0; i < length; i++) {
			char c = text.charAt(i);
			type(c);
		}
	}

	private void type(char c) {
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_NUMPAD0);
		robot.keyRelease(KeyEvent.VK_NUMPAD0);
		String altCode = Integer.toString(c);
		for (int i = 0; i < altCode.length(); i++) {
			c = (char) (altCode.charAt(i) + '0');
			robot.keyPress(c);
			robot.keyRelease(c);
		}
		robot.keyRelease(KeyEvent.VK_ALT);
	}
}