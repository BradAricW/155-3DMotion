package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class StrafeAction extends AbstractAction {

	private Camera cam;
	private int val;
	
	public StrafeAction(Camera c, int value) {
		cam = c;
		val = value;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(val >= 1) //cam.rightStrafe();
			System.out.println("Strafe Right Test");
		else //cam.leftStrafe();
			System.out.println("Strafe Left Test");
	}

}
