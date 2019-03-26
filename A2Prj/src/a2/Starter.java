package a2;

import graphicslib3D.*;
import graphicslib3D.GLSLUtils.*;
import graphicslib3D.shape.Sphere;

import java.io.*;
import java.nio.*;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLContext;

public class Starter extends JFrame implements GLEventListener
{	private GLCanvas myCanvas;
	private int rendering_program;
	private int vao[] = new int[1];
	//vbo[0] = custom object position 
	//vbo[1] = sphere object position
	//vbo[2] = sphere object texture
	//vbo[3] = x axis position
	//vbo[4] = y axis position
	//vbo[5] = z axis position
	private int vbo[] = new int[6];
	private float sphereLocX, sphereLocY, sphereLocZ;
	private GLSLUtils util = new GLSLUtils();
	
	//texture initializations
	private int sunTexture, plan1Texture, plan2Texture;
	private int moon1Texture, moon2Texture;
	private int xAxisTexture, yAxisTexture, zAxisTexture;
	private Texture joglSun, joglPlan1, joglPlan2;
	private Texture joglMoon1, joglMoon2;
	private Texture joglX, joglY, joglZ;
	
	private float cameraX, cameraY, cameraZ;
	
	
	private Camera cam;
	
	boolean axisView;
	
	//sphere initializations
	private Sphere sphereObj = new Sphere(24);
	
	private	MatrixStack mvStack = new MatrixStack(20);

	public Starter()
	{	setTitle("Brad Waechter - A2");
		setSize(600, 600);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		getContentPane().add(myCanvas);
		this.setVisible(true);
		axisView = true;
		//cam = new Camera();
		
		//actions setup
		AxisAction axisAction = new AxisAction(this);
		ForwardBackwardAction forwardAction = new ForwardBackwardAction(cam, 1);
		ForwardBackwardAction backwardAction = new ForwardBackwardAction(cam, -1);
		PitchAction pitchUAction = new PitchAction(cam, 1);
		PitchAction pitchDAction = new PitchAction(cam, -1);
		StrafeAction strafeRAction = new StrafeAction(cam, 1);
		StrafeAction strafeLAction = new StrafeAction(cam, -1);
		UpDownAction upAction = new UpDownAction(cam, 1);
		UpDownAction downAction = new UpDownAction(cam, -1);
		PanAction panRAction = new PanAction(cam, 1);
		PanAction panLAction = new PanAction(cam, -1);
		
		//key mapping---------------------------------------------------------------------------
		
		//W
		JComponent contentPane = (JComponent) this.getContentPane();
		int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap imap = contentPane.getInputMap(mapName);
		KeyStroke wKey = KeyStroke.getKeyStroke('w');
		imap.put(wKey, "forward");
		ActionMap amap = contentPane.getActionMap();
		amap.put("forward", forwardAction);
		this.requestFocus();
		
		//S
		KeyStroke sKey = KeyStroke.getKeyStroke('s');
		imap.put(sKey, "backward");
		amap.put("backward", backwardAction);
		this.requestFocus();
		
		//A
		KeyStroke aKey = KeyStroke.getKeyStroke('a');
		imap.put(aKey, "strafe left");
		amap.put("strafe left", strafeLAction);
		this.requestFocus();
		
		//D
		KeyStroke dKey = KeyStroke.getKeyStroke('d');
		imap.put(dKey, "strafe right");
		amap.put("strafe right", strafeRAction);
		this.requestFocus();
		
		//Q
		KeyStroke qKey = KeyStroke.getKeyStroke('q');
		imap.put(qKey, "up");
		amap.put("up", upAction);
		this.requestFocus();
		
		//E
		KeyStroke eKey = KeyStroke.getKeyStroke('e');
		imap.put(eKey, "down");
		amap.put("down", downAction);
		this.requestFocus();
		
		//UP
		KeyStroke upArrow = KeyStroke.getKeyStroke("UP");
		imap.put(upArrow, "pitch up");
		amap.put("pitch up", pitchUAction);
		this.requestFocus();
		
		//DOWN
		KeyStroke downArrow = KeyStroke.getKeyStroke("DOWN");
		imap.put(downArrow, "pitch down");
		amap.put("pitch down", pitchDAction);
		this.requestFocus();
		
		//LEFT
		KeyStroke leftArrow = KeyStroke.getKeyStroke("LEFT");
		imap.put(leftArrow, "pan left");
		amap.put("pan left", panLAction);
		this.requestFocus();
		
		//RIGHT
		KeyStroke rightArrow = KeyStroke.getKeyStroke("RIGHT");
		imap.put(rightArrow, "pan right");
		amap.put("pan right", panRAction);
		this.requestFocus();
		
		//SPACE
		KeyStroke spaceBar = KeyStroke.getKeyStroke("SPACE");
		imap.put(spaceBar, "axis");
		amap.put("axis", axisAction);
		this.requestFocus();
		//------------------------------------------------------------------------------------
		
		FPSAnimator animator = new FPSAnimator(myCanvas, 50);
		animator.start();
		
		
	}

	public void display(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		gl.glClear(GL_DEPTH_BUFFER_BIT);
		float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);

		gl.glClear(GL_DEPTH_BUFFER_BIT);

		gl.glUseProgram(rendering_program);

		int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
		int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");

		float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		Matrix3D pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);

		// push view matrix onto the stack
		mvStack.pushMatrix();
		mvStack.translate(-cameraX, -cameraY, -cameraZ);
		//mvStack.multMatrix(cam.getView());
		double amt = (double)(System.currentTimeMillis())/1000.0;

		gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
		
		int numVerts = sphereObj.getIndices().length;
		
		// ----------------------  sun  
		mvStack.pushMatrix();
		mvStack.translate(sphereLocX, sphereLocY, sphereLocZ);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/10.0,1.0,1.0,1.0);
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		//position
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		//texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, sunTexture);
		
		gl.glEnable(GL_DEPTH_TEST);
		gl.glFrontFace(GL_CCW);
		//end texture
		
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, 72); 
		mvStack.popMatrix();
		
		//-----------------------  planet1  
		mvStack.pushMatrix();
		mvStack.translate(Math.sin(amt/2)*4.0f, 0.0f, Math.cos(amt/2)*4.0f);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,1.0,0.0);
		mvStack.scale(0.75, 0.75, 0.75);
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		//position
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		//texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, plan1Texture);	
		
		gl.glEnable(GL_DEPTH_TEST);
		gl.glFrontFace(GL_CCW);
		//end texture
		gl.glDrawArrays(GL_TRIANGLES, 0, numVerts);
		mvStack.popMatrix();

		//-----------------------  moon1
		mvStack.pushMatrix();
		mvStack.translate(Math.sin(amt*2)*2.0f, Math.sin(amt*2)*2.0f, Math.cos(amt*2)*2.0f);
		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,0.0,-1.0);
		mvStack.scale(0.25, 0.25, 0.25);
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		//position
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		//texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, moon1Texture);
		
		gl.glEnable(GL_DEPTH_TEST);
		gl.glFrontFace(GL_CCW);
		//end texture
		gl.glDrawArrays(GL_TRIANGLES, 0, numVerts);
		mvStack.popMatrix();  mvStack.popMatrix();  
		
		//-----------------------  planet2  
		mvStack.pushMatrix();
		mvStack.translate(Math.sin(amt)*8.0f, 0.0f, Math.cos(amt)*8.0f);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/10.0,1.0,1.0,0.0);
		mvStack.scale(0.5, 0.5, 0.5);
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		//position
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		//texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, plan2Texture);
		
		gl.glEnable(GL_DEPTH_TEST);
		gl.glFrontFace(GL_CCW);
		//end texture
		gl.glDrawArrays(GL_TRIANGLES, 0, numVerts);
		mvStack.popMatrix();

		//-----------------------  moon2
		mvStack.pushMatrix();
		mvStack.translate(Math.sin(amt*(-2))*2.0f, Math.cos(amt*(-2))*2.0f, Math.cos(amt*(-2))*2.0f);
		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,1.0,1.0);
		mvStack.scale(0.15, 0.15, 0.15);
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		//position
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		//texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, moon2Texture);
		
		gl.glEnable(GL_DEPTH_TEST);
		gl.glFrontFace(GL_CCW);
		//end texture
		gl.glDrawArrays(GL_TRIANGLES, 0, numVerts);
		mvStack.popMatrix();  mvStack.popMatrix();  		
		
		mvStack.popMatrix();
		mvStack.popMatrix();
		
		
		if(axisView) {
			// ----------------------  x axis  
			mvStack.pushMatrix();
			mvStack.translate(sphereLocX, sphereLocY, sphereLocZ);
			gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
			//position
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
			gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(0);
			//texture
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
			gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(1);
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(GL_TEXTURE_2D, xAxisTexture);
			
			gl.glEnable(GL_DEPTH_TEST);
			gl.glFrontFace(GL_CCW);
			//end texture
			
			gl.glEnable(GL_DEPTH_TEST);
			gl.glDrawArrays(GL_LINES, 0, 2); 
			mvStack.popMatrix();
			
			// ----------------------  y axis  
			mvStack.pushMatrix();
			mvStack.translate(sphereLocX, sphereLocY, sphereLocZ);
			gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
			//position
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
			gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(0);
			//texture
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
			gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(1);
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(GL_TEXTURE_2D, yAxisTexture);
			
			gl.glEnable(GL_DEPTH_TEST);
			gl.glFrontFace(GL_CCW);
			//end texture
			
			gl.glEnable(GL_DEPTH_TEST);
			gl.glDrawArrays(GL_LINES, 0, 2); 
			mvStack.popMatrix();
			
			// ----------------------  z axis  
			mvStack.pushMatrix();
			mvStack.translate(sphereLocX, sphereLocY, sphereLocZ);
			gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
			//position
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
			gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(0);
			//texture
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
			gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(1);
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(GL_TEXTURE_2D, zAxisTexture);
			
			gl.glEnable(GL_DEPTH_TEST);
			gl.glFrontFace(GL_CCW);
			//end texture
			
			gl.glEnable(GL_DEPTH_TEST);
			gl.glDrawArrays(GL_LINES, 0, 2); 
			mvStack.popMatrix();
		}
	}

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		rendering_program = createShaderProgram();
		setupVertices();
		sphereLocX = 0.0f; sphereLocY = 0.0f; sphereLocZ = 0.0f;
		
		cameraX = 0.0f; cameraY = 0.0f; cameraZ = 12.0f;
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		//textures
		joglSun = loadTexture("src/a2/sun.jpg");
		sunTexture = joglSun.getTextureObject();
		
		joglPlan1 = loadTexture("src/a2/earth.jpg");
		plan1Texture = joglPlan1.getTextureObject();
		
		joglMoon1 = loadTexture("src/a2/moon.jpg");
		moon1Texture = joglMoon1.getTextureObject();
		
		joglPlan2 = loadTexture("src/a2/earth.jpg");
		plan2Texture = joglPlan2.getTextureObject();
		
		joglMoon2 = loadTexture("src/a2/moon.jpg");
		moon2Texture = joglMoon2.getTextureObject();
		
		joglX = loadTexture("src/a2/x.jpg");
		xAxisTexture = joglX.getTextureObject();
		
		joglY = loadTexture("src/a2/y.jpg");
		yAxisTexture = joglY.getTextureObject();
		
		joglZ = loadTexture("src/a2/z.jpg");
		zAxisTexture = joglZ.getTextureObject();
	}

	private Texture loadTexture(String textureFileName) {
		Texture tex = null;
		try {
			tex = TextureIO.newTexture(new File(textureFileName), false);
		}
		catch (Exception e) { e.printStackTrace(); }
		return tex;
	}

	private void setupVertices()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		
		Vertex3D[] vertices = sphereObj.getVertices();
		int[] indices = sphereObj.getIndices();
		
		float[] pvalues = new float[indices.length*3];
		float[] tvalues = new float[indices.length*2];
		
		for (int i=0; i<indices.length; i++)
		{	pvalues[i*3] = (float) (vertices[indices[i]]).getX();
			pvalues[i*3+1] = (float) (vertices[indices[i]]).getY();
			pvalues[i*3+2] = (float) (vertices[indices[i]]).getZ();
			tvalues[i*2] = (float) (vertices[indices[i]]).getS();
			tvalues[i*2+1] = (float) (vertices[indices[i]]).getT();
		}
		
		float[] custom_positions =
		{		//front pyramid
			0.0f, 0.0f, -2.0f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f,//bottom
			0.0f, 0.0f, -2.0f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f,//top
			0.0f, 0.0f, -2.0f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f,//left
			0.0f, 0.0f, -2.0f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f,//right
				//back pyramid
			0.0f, 0.0f, 2.0f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f,//bottom
			0.0f, 0.0f, 2.0f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,//top
			0.0f, 0.0f, 2.0f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f,//left
			0.0f, 0.0f, 2.0f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f,//right
			
				//left pyramid
			-2.0f, 0.0f, 0.0f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f,//bottom
			-2.0f, 0.0f, 0.0f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f,//top
			-2.0f, 0.0f, 0.0f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f,//back
			-2.0f, 0.0f, 0.0f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f,//front		
				//right pyramid
			2.0f, 0.0f, 0.0f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f,//bottom
			2.0f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f,//top
			2.0f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f,//back
			2.0f, 0.0f, 0.0f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f,//front			
			
				//top pyramid
			0.0f, 2.0f, 0.0f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f,//left
			0.0f, 2.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f,//right
			0.0f, 2.0f, 0.0f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,//back
			0.0f, 2.0f, 0.0f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f,//front	
				//bottom pyramid
			0.0f, -2.0f, 0.0f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f,//left
			0.0f, -2.0f, 0.0f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f,//right
			0.0f, -2.0f, 0.0f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f,//back
			0.0f, -2.0f, 0.0f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f,//front
			
		};
		
		float[] xAxis_positions =
			{
			10.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,		
			};
		float[] yAxis_positions =
			{
			0.0f, 10.0f, 0.0f, 0.0f, 0.0f, 0.0f,		
			};
		float[] zAxis_positions =
			{
			0.0f, 0.0f, 10.0f, 0.0f, 0.0f, 0.0f,		
			};
		

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer sphereBuf = Buffers.newDirectFloatBuffer(custom_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, sphereBuf.limit()*4, sphereBuf, GL_STATIC_DRAW);
		
		
		//axis buffers
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer xBuf = Buffers.newDirectFloatBuffer(xAxis_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, xBuf.limit()*4, xBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer yBuf = Buffers.newDirectFloatBuffer(yAxis_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, yBuf.limit()*4, yBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer zBuf = Buffers.newDirectFloatBuffer(zAxis_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, zBuf.limit()*4, zBuf, GL_STATIC_DRAW);
	}

	private Matrix3D perspective(float fovy, float aspect, float n, float f)
	{	float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
		float A = q / aspect;
		float B = (n + f) / (n - f);
		float C = (2.0f * n * f) / (n - f);
		Matrix3D r = new Matrix3D();
		r.setElementAt(0,0,A);
		r.setElementAt(1,1,q);
		r.setElementAt(2,2,B);
		r.setElementAt(3,2,-1.0f);
		r.setElementAt(2,3,C);
		return r;
	}

	public static void main(String[] args) { new Starter(); }
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose(GLAutoDrawable drawable) {}

	private int createShaderProgram()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		String vshaderSource[] = util.readShaderSource("src/a2/vert.shader");
		String fshaderSource[] = util.readShaderSource("src/a2/frag.shader");

		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);

		gl.glCompileShader(vShader);
		gl.glCompileShader(fShader);

		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		return vfprogram;
	}

	public void axisViewSet() {
		if(axisView) axisView = false;
		else axisView = true;
	}
}
