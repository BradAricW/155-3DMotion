package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ForwardBackwardAction extends AbstractAction {

	private Camera cam;
	private int val;
	
	public ForwardBackwardAction(Camera c, int value) {
		// TODO Auto-generated constructor stub
		cam = c;
		val = value;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(val>=1) //cam.forwardMove();
		System.out.println("Forward Move Test");
		else //cam.backwardMove();
		System.out.println("Backward Move Test");
		
	}

}
