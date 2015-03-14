package com.corpus.client;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.JFrame;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.corpus.client.Joint.JointType;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * Draws the body model using jogl.
 * 
 * @See <a href="http://www.land-of-kain.de/docs/jogl">JOGL (Java OpenGL)
 *      Tutorial</a>
 * 
 * @author Scopus
 * 
 */
public class ModelFrame extends GLCanvas implements GLEventListener {

	/** Serial version UID. */
	private static final long serialVersionUID = 1L;

	/** The GL unit (helper class). */
	private GLU glu;

	/** The frames per second setting. */
	private int fps = 60;

	/** The OpenGL animator. */
	private FPSAnimator animator;

	public Joint[] model;

	/**
	 * A new mini starter.
	 * 
	 * @param capabilities
	 *            The GL capabilities.
	 * @param width
	 *            The window width.
	 * @param height
	 *            The window height.
	 */
	public ModelFrame(int width, int height) {
		addGLEventListener(this);
	}

	/**
	 * Sets up the screen.
	 * 
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void init(GLAutoDrawable drawable) {
		final GL2 gl2 = drawable.getGL().getGL2();

		// Enable z- (depth) buffer for hidden surface removal.
		gl2.glEnable(GL2.GL_DEPTH_TEST);
		gl2.glDepthFunc(GL2.GL_LEQUAL);

		// Enable smooth shading.
		gl2.glShadeModel(GL2.GL_SMOOTH);

		// Define "clear" color.
		gl2.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		// We want a nice perspective.
		gl2.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

		gl2.glEnable(GL2.GL_BLEND);
		gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl2.glEnable(GL2.GL_MULTISAMPLE);

		// Create GLU.
		glu = new GLU();

		// Start animator.
		animator = new FPSAnimator(this, fps);
		animator.start();
	}

	/**
	 * The only method that you should implement by yourself.
	 * 
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	public void display(GLAutoDrawable drawable) {
		if (!animator.isAnimating()) {
			return;
		}
		final GL2 gl2 = drawable.getGL().getGL2();

		// Clear screen.
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		// Set camera.
		setCamera(gl2, glu, -1);

		// Prepare light parameters.
		float SHINE_ALL_DIRECTIONS = 1;
		float[] lightPos = { -30, 0, 0, SHINE_ALL_DIRECTIONS };
		float[] lightColorAmbient = { 0.2f, 0.2f, 0.2f, 1f };
		float[] lightColorSpecular = { 0.8f, 0.8f, 0.8f, 1f };

		// Set light parameters.
		gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
		gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
		gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);

		// Enable lighting in GL.
		gl2.glEnable(GL2.GL_LIGHT1);
		gl2.glEnable(GL2.GL_LIGHTING);

		// Set material properties.
		float[] rgba = { 1f, 1f, 1f };
		gl2.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
		gl2.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
		gl2.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 0.5f);

		// Draw sphere (possible styles: FILL, LINE, POINT).
		for (Joint j : model)
			drawChilds(gl2, j.absolutePosition, j, j.children, 0.025f, 0.01f);
	}

	/**
	 * Resizes the screen.
	 * 
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable,
	 *      int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		final GL2 gl2 = drawable.getGL().getGL2();
		gl2.glViewport(0, 0, width, height);
	}

	/**
	 * Changing devices is not supported.
	 * 
	 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable,
	 *      boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		throw new UnsupportedOperationException("Changing display is not supported.");
	}

	/**
	 * @param gl
	 *            The GL context.
	 * @param glu
	 *            The GL unit.
	 * @param distance
	 *            The distance from the screen.
	 */
	private void setCamera(GL2 gl2, GLU glu, float distance) {
		// Change to projection matrix.
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();

		// Perspective.
		float widthHeightRatio = (float) getWidth() / (float) getHeight();
		glu.gluPerspective(45, widthHeightRatio, 1, 1000);
		glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);

		// Change back to model view matrix.
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
	}

	public final static void main(String[] args) {
		ModelFrame canvas = new ModelFrame(800, 500);
		JFrame frame = new JFrame("Mini JOGL Demo (breed)");
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		frame.setSize(800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		canvas.requestFocus();
	}

	public void dispose(GLAutoDrawable arg0) {

	}

	private void drawChilds(GL2 gl2, Vector3D absParentPos, Joint parent, ArrayList<Joint> childs, float jointRadius, float boneRadius) {
		if (parent.jointType == JointType.WRIST_LEFT || parent.jointType == JointType.WRIST_RIGHT)
			drawJoint(absParentPos, gl2, 0.01f);
		else
			drawJoint(absParentPos, gl2, jointRadius);
		for (Joint child : childs) {
			Joint jointChild = child;
			if (parent.jointType == JointType.WRIST_LEFT || parent.jointType == JointType.WRIST_RIGHT
					|| parent.jointType == JointType.ANKLE_LEFT || parent.jointType == JointType.ANKLE_RIGHT) {
				drawChilds(gl2, child.absolutePosition, child, jointChild.children, 0.01f, 0.005f);
				drawBone(absParentPos, child.absolutePosition, 0.005f, 32, gl2);
			} else {
				drawChilds(gl2, child.absolutePosition, child, jointChild.children, jointRadius, boneRadius);
				drawBone(absParentPos, child.absolutePosition, boneRadius, 32, gl2);
			}
		}
	}

	private void drawJoint(Vector3D position, GL2 gl2, float radius) {
		// Draw sphere (possible styles: FILL, LINE, POINT).
		// gl2.glColor3f(0.3f, 0.5f, 1f);
		gl2.glColor3f(1f, 0f, 0f);
		gl2.glTranslatef((float) -position.getX() / 100f, (float) position.getY() / 100f, (float) position.getZ() / 100f);
		GLUquadric earth = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
		glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
		final int slices = 32;
		final int stacks = 16;
		glu.gluSphere(earth, radius, slices, stacks);
		glu.gluDeleteQuadric(earth);
		gl2.glLoadIdentity();
	}

	private void renderCylinder(double x1, double y1, double z1, double x2, double y2, double z2, float radius, int subdivisions,
			GLUquadric quadric, GL2 gl2) {
		double vx = x2 - x1;
		double vy = y2 - y1;
		double vz = z2 - z1;

		// handle the degenerate case of z1 == z2 with an approximation
		if (vz == 0)
			vz = 0.0001f;

		double v = Math.sqrt(vx * vx + vy * vy + vz * vz);
		double ax = 57.2957795 * Math.acos(vz / v);
		if (vz < 0.0)
			ax = -ax;
		double rx = -vy * vz;
		double ry = vx * vz;
		gl2.glPushMatrix();

		// draw the cylinder body
		gl2.glTranslatef((float) x1, (float) y1, (float) z1);
		gl2.glRotatef((float) ax, (float) rx, (float) ry, 0.0f);
		glu.gluQuadricOrientation(quadric, GLU.GLU_OUTSIDE);
		glu.gluCylinder(quadric, radius, radius, v, subdivisions, 1);
		gl2.glPopMatrix();
	}

	private void drawBone(Vector3D pos1, Vector3D pos2, float radius, int subdivisions, GL2 gl2) {
		// the same quadric can be re-used for drawing many cylinders
		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		renderCylinder(-pos1.getX() / 100f, pos1.getY() / 100f, pos1.getZ() / 100f, -pos2.getX() / 100f, pos2.getY() / 100f,
				pos2.getZ() / 100f, radius, subdivisions, quadric, gl2);
		glu.gluDeleteQuadric(quadric);
	}
}