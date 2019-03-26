package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class PanAction extends AbstractAction {

	private Camera cam;
	private int val;
	
	public PanAction(Camera c, int value) {
		// TODO Auto-generated constructor stub
		cam = c;
		val = value;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(val>=1) //cam.panRight();
			System.out.println("Pan Right Test");
		else //cam.panLeft();
			System.out.println("Pan Left Test");
		
	}

}
