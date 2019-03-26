package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class AxisAction extends AbstractAction {

	Starter s;
	
	public AxisAction(Starter start) {
		s = start;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		s.axisViewSet();
		System.out.println("Axis Test");
		
	}

}
