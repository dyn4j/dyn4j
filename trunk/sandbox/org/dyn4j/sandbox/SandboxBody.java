package org.dyn4j.sandbox;

import java.awt.Color;

import javax.media.opengl.GL2;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Transform;
import org.dyn4j.sandbox.utilities.ColorUtilities;
import org.dyn4j.sandbox.utilities.RenderState;
import org.dyn4j.sandbox.utilities.RenderUtilities;

/**
 * Custom body class used to store colors and a name.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SandboxBody extends Body {
	/** The body name */
	protected String name;
	
	/** The body outline color */
	protected float[] outlineColor = ColorUtilities.convertColor(new Color(113, 149, 70));
	
	/** The body fill color */
	protected float[] fillColor = ColorUtilities.convertColor(new Color(196, 213, 150));
	
	/**
	 * Renders the body normally.
	 * <p>
	 * Uses the fill and outline colors to fill and outline each fixture in sequence.
	 * @param gl the OpenGL context
	 */
	public void render(GL2 gl) {
		Transform transform = this.getTransform();
		
		// apply the body transform
		RenderUtilities.pushTransform(gl);
		RenderUtilities.applyTransform(gl, transform);
		
		// loop over all the fixtures
		int fSize = this.fixtures.size();
		for (int i = 0; i < fSize; i++) {
			BodyFixture bodyFixture = this.getFixture(i);
			Convex convex = bodyFixture.getShape();
			
			// render the fill
			gl.glColor4fv(this.fillColor, 0);
			RenderUtilities.fillShape(gl, convex);
			// render the outline
			gl.glColor4fv(this.outlineColor, 0);
			RenderUtilities.drawShape(gl, convex, false);
		}
		
		// remove the body transform
		RenderUtilities.popTransform(gl);
	}
	
	/**
	 * Outlines the body only.
	 * <p>
	 * Uses the outline color to outline each fixture in sequence.
	 * <p>
	 * The outline is not an outline of the entire body but instead
	 * an outline of each fixture.  If you need an outline of the entire
	 * body see the {@link RenderUtilities#outlineBody(GL2, SandboxBody, RenderState)} methods.
	 * @param gl the OpenGL context
	 */
	public void draw(GL2 gl) {
		RenderUtilities.pushTransform(gl);
		RenderUtilities.applyTransform(gl, transform);
		
		int fSize = this.fixtures.size();
		for (int i = 0; i < fSize; i++) {
			BodyFixture bodyFixture = this.getFixture(i);
			Convex convex = bodyFixture.getShape();
			
			// render
			RenderUtilities.drawShape(gl, convex, false);
		}
		
		RenderUtilities.popTransform(gl);
	}
	
	/**
	 * Fills the body only.
	 * <p>
	 * Uses the fill color to fill each fixture in sequence.
	 * @param gl the OpenGL context
	 */
	public void fill(GL2 gl) {
		RenderUtilities.pushTransform(gl);
		RenderUtilities.applyTransform(gl, transform);
		
		int fSize = this.fixtures.size();
		for (int i = 0; i < fSize; i++) {
			BodyFixture bodyFixture = this.getFixture(i);
			Convex convex = bodyFixture.getShape();
			
			// render
			RenderUtilities.fillShape(gl, convex);
		}
		
		RenderUtilities.popTransform(gl);
	}
	
	/**
	 * Returns the outline color.
	 * @return float[]
	 */
	public float[] getOutlineColor() {
		return this.outlineColor;
	}
	
	/**
	 * Sets the outline color.
	 * <p>
	 * An array of 4 floats is expected.
	 * @param color the outline color
	 */
	public void setOutlineColor(float[] color) {
		this.outlineColor = color;
	}
	
	/**
	 * Returns the fill color.
	 * @return float[]
	 */
	public float[] getFillColor() {
		return this.fillColor;
	}
	
	/**
	 * Sets the fill color.
	 * <p>
	 * An array of 4 floats is expected.
	 * @param color the fill color
	 */
	public void setFillColor(float[] color) {
		this.fillColor = color;
	}

	/**
	 * Returns the name of the body.
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the body.
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.Body#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
}
