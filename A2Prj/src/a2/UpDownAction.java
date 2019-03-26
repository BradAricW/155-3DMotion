package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class UpDownAction extends AbstractAction {
	
	private Camera cam;
	private int val;
	
	public UpDownAction(Camera c, int value) {
		// TODO Auto-generated constructor stub
		cam = c;
		val = value;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(val>=1) //cam.upMove();
			System.out.println("Up Move Test");
		else //cam.downMove();
			System.out.println("Down Move Test");
	}


}
