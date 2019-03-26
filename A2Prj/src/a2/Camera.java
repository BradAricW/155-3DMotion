package a2;

import graphicslib3D.Matrix3D;

public class Camera {
	private double xCam, yCam, zCam; 
	private float pitch, pan;
	private Matrix3D viewMat;
	private double[] setup;
	private double amt;
	
	public Camera() {
		xCam = 0.0;
		yCam = 0.0;
		zCam = 12.0;
		pitch = 0.0f;
		pan = 0.0f;
		amt = 0.5;
		setup = new double[] {1.0, 0.0, 0.0, 0.0,
		                   	  0.0, 1.0, 0.0, 0.0,
		                   	  0.0, 0.0, 1.0, 0.0,
		                   	  0.0, 0.0, 0.0, 1.0};
		viewMat.setValues(setup);
	}
	
	public void updateView() {
		
	}

	public Matrix3D getView() {
		return viewMat;
	}

	public void forwardMove() {
		zCam += amt;
		updateView();
	}

	public void backwardMove() {
		zCam -= amt;
		updateView();		
	}

	public void pitchUp() {
		pitch += amt ;
		updateView();
	}

	public void pitchDown() {
		pitch -= amt;
		updateView();		
	}

	public void panRight() {
		pan += amt;
		updateView();		
	}

	public void panLeft() {
		pan -= amt;
		updateView();		
	}

	public void upMove() {
		yCam += amt;
		updateView();
	}

	public void downMove() {
		yCam -= amt;
		updateView();		
	}

	public void rightStrafe() {
		xCam += amt;
		updateView();
	}

	public void leftStrafe() {
		xCam -= amt;
		updateView();		
	}

}
