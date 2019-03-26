package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class PitchAction extends AbstractAction {

	
	private Camera cam;
	private int val;
	
	public PitchAction(Camera c, int value) {
		// TODO Auto-generated constructor stub
		cam = c;
		val = value;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(val>=1) //cam.pitchUp();
			System.out.println("Pitch Up Test");
		else //cam.pitchDown();
			System.out.println("Pitch Down Test");
		
	}

}
