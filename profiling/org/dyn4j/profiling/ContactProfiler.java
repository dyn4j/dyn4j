/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.profiling;

import java.io.IOException;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;

/**
 * Profiler class used to profile a contact heavy simulation for potential
 * performance enchancements.
 * @author William Bittle
 * @version 3.1.11
 * @since 3.1.11
 */
public class ContactProfiler {
	/**
	 * The application entry point.
	 * @param args application arguments
	 * @throws IOException thrown if reading from the input stream fails
	 */
	public static void main(String[] args) throws IOException {
		// compute the running time
		final double runtime = 10; // 30 seconds
		final int iterations = (int)Math.ceil(runtime / Settings.DEFAULT_STEP_FREQUENCY);
		
		// warm-up
		World world = new World();
		setup(world);
		for (int i = 0; i < iterations; i++) {
			world.step(1);
		}
		
		// wait for profiler to attach
		System.out.println("Attach profiler and then press any key to continue...");
		//System.in.read();
		
//		long[][] metrics = new long[iterations*2][2];
		
		for (int i = 0; i < iterations * 30; i++) {
//			metrics[i][0] = System.nanoTime();
			world.step(1);
//			metrics[i][1] = System.nanoTime();
		}
		
//		for (long[] times : metrics) {
//			long diff = times[1] - times[0];
//			System.out.println((double)diff / 1.0e6); // milliseconds
//		}
	}

	/**
	 * World setup.  Exported from the Bucket test in the Sandbox.
	 * @param world the world
	 */
	public static final void setup(World world) {
		//Settings settings = world.getSettings();

		AxisAlignedBounds bounds = new AxisAlignedBounds(18.0, 25.0);
		bounds.translate(new Vector2(0.0, 8.0));
		world.setBounds(bounds);

		// Bottom
		Body body1 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(15.0, 1.0);
			BodyFixture bf = new BodyFixture(c);
			body1.addFixture(bf);
		}
		body1.setMass(Mass.Type.INFINITE);
		world.addBody(body1);

		// Left-Side
		Body body2 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.0, 15.0);
			BodyFixture bf = new BodyFixture(c);
			body2.addFixture(bf);
		}
		body2.translate(new Vector2(-7.5, 7.0));
		body2.setMass(Mass.Type.INFINITE);
		world.addBody(body2);

		// Right-Side
		Body body3 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.0, 15.0);
			BodyFixture bf = new BodyFixture(c);
			body3.addFixture(bf);
		}
		body3.translate(new Vector2(7.5, 7.0));
		body3.setMass(Mass.Type.INFINITE);
		world.addBody(body3);

		// Body4
		Body body4 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.26466451071798514,
					0.26466451071798514);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body4.addFixture(bf);
		}
		body4.translate(new Vector2(-1.1330084195675079, 5.50713143344162));
		body4.setMass(Mass.Type.NORMAL);
		world.addBody(body4);

		// Body5
		Body body5 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.33550559150792847,
					0.33550559150792847);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body5.addFixture(bf);
		}
		body5.translate(new Vector2(5.603235147719401, 13.411960949246117));
		body5.setMass(Mass.Type.NORMAL);
		world.addBody(body5);

		// Body6
		Body body6 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.5357970931824577);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body6.addFixture(bf);
		}
		body6.translate(new Vector2(-6.329934811410148, 6.927759853134262));
		body6.setMass(Mass.Type.NORMAL);
		world.addBody(body6);

		// Body7
		Body body7 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.45500206737397864,
					0.45500206737397864);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body7.addFixture(bf);
		}
		body7.translate(new Vector2(4.679834541654975, 6.438757601586879));
		body7.setMass(Mass.Type.NORMAL);
		world.addBody(body7);

		// Body8
		Body body8 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.16441954490420335);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body8.addFixture(bf);
		}
		body8.translate(new Vector2(-0.8485805483210063, 13.847950280799422));
		body8.setMass(Mass.Type.NORMAL);
		world.addBody(body8);

		// Body9
		Body body9 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.11346183622917774,
					0.11346183622917774);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body9.addFixture(bf);
		}
		body9.translate(new Vector2(4.973900551680926, 9.273285652109875));
		body9.setMass(Mass.Type.NORMAL);
		world.addBody(body9);

		// Body10
		Body body10 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.3302934524068014,
					0.3302934524068014);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body10.addFixture(bf);
		}
		body10.translate(new Vector2(-1.742686187270787, 3.490862643618847));
		body10.setMass(Mass.Type.NORMAL);
		world.addBody(body10);

		// Body11
		Body body11 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.5150530773072916,
					0.5150530773072916);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body11.addFixture(bf);
		}
		body11.translate(new Vector2(2.6941316494464393, 5.1610120488699565));
		body11.setMass(Mass.Type.NORMAL);
		world.addBody(body11);

		// Body12
		Body body12 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.5939229991296598,
					0.5939229991296598);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body12.addFixture(bf);
		}
		body12.translate(new Vector2(-0.1833092549830776, 4.682417370760636));
		body12.setMass(Mass.Type.NORMAL);
		world.addBody(body12);

		// Body13
		Body body13 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.6122826679423516,
					0.6122826679423516);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body13.addFixture(bf);
		}
		body13.translate(new Vector2(1.962512418931428, 2.4628065359233693));
		body13.setMass(Mass.Type.NORMAL);
		world.addBody(body13);

		// Body14
		Body body14 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.33116008735390884);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body14.addFixture(bf);
		}
		body14.translate(new Vector2(-4.2380766695232435, 9.34224354276551));
		body14.setMass(Mass.Type.NORMAL);
		world.addBody(body14);

		// Body15
		Body body15 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.19427909157390447,
					0.19427909157390447);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body15.addFixture(bf);
		}
		body15.translate(new Vector2(5.053142188283335, 4.644335892212074));
		body15.setMass(Mass.Type.NORMAL);
		world.addBody(body15);

		// Body16
		Body body16 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.06587836045881985);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body16.addFixture(bf);
		}
		body16.translate(new Vector2(-3.461132870705931, 7.374911939464545));
		body16.setMass(Mass.Type.NORMAL);
		world.addBody(body16);

		// Body17
		Body body17 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.3426496752710881);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body17.addFixture(bf);
		}
		body17.translate(new Vector2(4.6807372127965605, 14.72171270835167));
		body17.setMass(Mass.Type.NORMAL);
		world.addBody(body17);

		// Body18
		Body body18 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.43396718706259274,
					0.43396718706259274);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body18.addFixture(bf);
		}
		body18.translate(new Vector2(-6.177132372638269, 6.001832935956893));
		body18.setMass(Mass.Type.NORMAL);
		world.addBody(body18);

		// Body19
		Body body19 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.11441013647776184);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body19.addFixture(bf);
		}
		body19.translate(new Vector2(4.2370008533239405, 3.547601358264866));
		body19.setMass(Mass.Type.NORMAL);
		world.addBody(body19);

		// Body20
		Body body20 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.36047498620674756);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body20.addFixture(bf);
		}
		body20.translate(new Vector2(-4.76829837703779, 2.75234255450824));
		body20.setMass(Mass.Type.NORMAL);
		world.addBody(body20);

		// Body21
		Body body21 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.23494154594950922);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body21.addFixture(bf);
		}
		body21.translate(new Vector2(1.882900596069884, 8.385362338792662));
		body21.setMass(Mass.Type.NORMAL);
		world.addBody(body21);

		// Body22
		Body body22 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.29883829398233763);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body22.addFixture(bf);
		}
		body22.translate(new Vector2(-5.7760143259393315, 12.151269177101588));
		body22.setMass(Mass.Type.NORMAL);
		world.addBody(body22);

		// Body23
		Body body23 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.5759652599162056,
					0.5759652599162056);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body23.addFixture(bf);
		}
		body23.translate(new Vector2(0.6625380390614225, 7.515475929812812));
		body23.setMass(Mass.Type.NORMAL);
		world.addBody(body23);

		// Body24
		Body body24 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.17983056801217948,
					0.17983056801217948);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body24.addFixture(bf);
		}
		body24.translate(new Vector2(-4.29129333045134, 3.6046894694310776));
		body24.setMass(Mass.Type.NORMAL);
		world.addBody(body24);

		// Body25
		Body body25 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.5252698854410011,
					0.5252698854410011);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body25.addFixture(bf);
		}
		body25.translate(new Vector2(4.479341185976786, 6.18004713671508));
		body25.setMass(Mass.Type.NORMAL);
		world.addBody(body25);

		// Body26
		Body body26 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.0663322824076675,
					1.0663322824076675);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body26.addFixture(bf);
		}
		body26.translate(new Vector2(-0.9734522651974241, 12.160382324262478));
		body26.setMass(Mass.Type.NORMAL);
		world.addBody(body26);

		// Body27
		Body body27 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.5066957519079994);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body27.addFixture(bf);
		}
		body27.translate(new Vector2(0.23159523353113676, 6.229803763316303));
		body27.setMass(Mass.Type.NORMAL);
		world.addBody(body27);

		// Body28
		Body body28 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.5134348846837831);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body28.addFixture(bf);
		}
		body28.translate(new Vector2(-0.6376643479052707, 8.283928961628414));
		body28.setMass(Mass.Type.NORMAL);
		world.addBody(body28);

		// Body29
		Body body29 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.08028774401657247);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body29.addFixture(bf);
		}
		body29.translate(new Vector2(3.576063444857732, 5.757436195304736));
		body29.setMass(Mass.Type.NORMAL);
		world.addBody(body29);

		// Body30
		Body body30 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.4486315906214697);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body30.addFixture(bf);
		}
		body30.translate(new Vector2(-4.113027539339198, 1.5758158168112055));
		body30.setMass(Mass.Type.NORMAL);
		world.addBody(body30);

		// Body31
		Body body31 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.15219893660331302);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body31.addFixture(bf);
		}
		body31.translate(new Vector2(3.7404752061532904, 1.3650463467028633));
		body31.setMass(Mass.Type.NORMAL);
		world.addBody(body31);

		// Body32
		Body body32 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.2132083258298908);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body32.addFixture(bf);
		}
		body32.translate(new Vector2(-1.9975104146582772, 10.41311022405915));
		body32.setMass(Mass.Type.NORMAL);
		world.addBody(body32);

		// Body33
		Body body33 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.27845970069236153);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body33.addFixture(bf);
		}
		body33.translate(new Vector2(5.645732898794953, 12.84426714483509));
		body33.setMass(Mass.Type.NORMAL);
		world.addBody(body33);

		// Body34
		Body body34 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.4887181486282036,
					0.4887181486282036);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body34.addFixture(bf);
		}
		body34.translate(new Vector2(-6.436651121897059, 1.0845162989758197));
		body34.setMass(Mass.Type.NORMAL);
		world.addBody(body34);

		// Body35
		Body body35 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.20281941328422148);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body35.addFixture(bf);
		}
		body35.translate(new Vector2(4.94102060437015, 13.7920539244557));
		body35.setMass(Mass.Type.NORMAL);
		world.addBody(body35);

		// Body36
		Body body36 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.8502024280321234,
					0.8502024280321234);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body36.addFixture(bf);
		}
		body36.translate(new Vector2(-4.9076410653918625, 1.111386916955206));
		body36.setMass(Mass.Type.NORMAL);
		world.addBody(body36);

		// Body37
		Body body37 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.7474035327857532,
					0.7474035327857532);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body37.addFixture(bf);
		}
		body37.translate(new Vector2(0.5156337910571108, 10.364795452932713));
		body37.setMass(Mass.Type.NORMAL);
		world.addBody(body37);

		// Body38
		Body body38 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.024108182361493,
					1.024108182361493);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body38.addFixture(bf);
		}
		body38.translate(new Vector2(-1.3363180958386027, 13.913354940886506));
		body38.setMass(Mass.Type.NORMAL);
		world.addBody(body38);

		// Body39
		Body body39 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.13677002884421158);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body39.addFixture(bf);
		}
		body39.translate(new Vector2(2.2867806759809888, 11.361279564445159));
		body39.setMass(Mass.Type.NORMAL);
		world.addBody(body39);

		// Body40
		Body body40 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.11838794968691083,
					0.11838794968691083);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body40.addFixture(bf);
		}
		body40.translate(new Vector2(-6.791487795404287, 13.295707204422149));
		body40.setMass(Mass.Type.NORMAL);
		world.addBody(body40);

		// Body41
		Body body41 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.055025306069729676);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body41.addFixture(bf);
		}
		body41.translate(new Vector2(4.645147358453109, 1.2715327368569647));
		body41.setMass(Mass.Type.NORMAL);
		world.addBody(body41);

		// Body42
		Body body42 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.7301265736222848,
					0.7301265736222848);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body42.addFixture(bf);
		}
		body42.translate(new Vector2(-4.653141924310416, 14.03379116787397));
		body42.setMass(Mass.Type.NORMAL);
		world.addBody(body42);

		// Body43
		Body body43 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.29988232840384);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body43.addFixture(bf);
		}
		body43.translate(new Vector2(2.8934320398435314, 12.722664967167887));
		body43.setMass(Mass.Type.NORMAL);
		world.addBody(body43);

		// Body44
		Body body44 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.10272844748899186);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body44.addFixture(bf);
		}
		body44.translate(new Vector2(-5.607063410659674, 4.18992848948463));
		body44.setMass(Mass.Type.NORMAL);
		world.addBody(body44);

		// Body45
		Body body45 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.069695263948326);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body45.addFixture(bf);
		}
		body45.translate(new Vector2(5.1146614116851286, 14.867359297805047));
		body45.setMass(Mass.Type.NORMAL);
		world.addBody(body45);

		// Body46
		Body body46 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.8324269310635236,
					0.8324269310635236);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body46.addFixture(bf);
		}
		body46.translate(new Vector2(-5.576028900547054, 6.264671470063004));
		body46.setMass(Mass.Type.NORMAL);
		world.addBody(body46);

		// Body47
		Body body47 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.46718893775332804,
					0.46718893775332804);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body47.addFixture(bf);
		}
		body47.translate(new Vector2(2.8924423982963807, 6.1919480666560975));
		body47.setMass(Mass.Type.NORMAL);
		world.addBody(body47);

		// Body48
		Body body48 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.11792345678306618,
					0.11792345678306618);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body48.addFixture(bf);
		}
		body48.translate(new Vector2(-4.599413511874901, 10.09288807701673));
		body48.setMass(Mass.Type.NORMAL);
		world.addBody(body48);

		// Body49
		Body body49 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.10088206858466549);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body49.addFixture(bf);
		}
		body49.translate(new Vector2(3.89376828781741, 4.296977512396952));
		body49.setMass(Mass.Type.NORMAL);
		world.addBody(body49);

		// Body50
		Body body50 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.6947159899974195,
					0.6947159899974195);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body50.addFixture(bf);
		}
		body50.translate(new Vector2(-4.298064677586164, 5.795616925977458));
		body50.setMass(Mass.Type.NORMAL);
		world.addBody(body50);

		// Body51
		Body body51 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.7050983767295572,
					0.7050983767295572);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body51.addFixture(bf);
		}
		body51.translate(new Vector2(5.515393450088526, 13.095395558542654));
		body51.setMass(Mass.Type.NORMAL);
		world.addBody(body51);

		// Body52
		Body body52 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.21891145876971443,
					0.21891145876971443);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body52.addFixture(bf);
		}
		body52.translate(new Vector2(-1.3455614416097157, 14.445239223123615));
		body52.setMass(Mass.Type.NORMAL);
		world.addBody(body52);

		// Body53
		Body body53 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.11451800886408332);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body53.addFixture(bf);
		}
		body53.translate(new Vector2(6.6614538521501245, 11.71178097549447));
		body53.setMass(Mass.Type.NORMAL);
		world.addBody(body53);

		// Body54
		Body body54 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.6711279785676146,
					0.6711279785676146);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body54.addFixture(bf);
		}
		body54.translate(new Vector2(-0.5982649143457038, 9.707963633158768));
		body54.setMass(Mass.Type.NORMAL);
		world.addBody(body54);

		// Body55
		Body body55 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.1718567216508439,
					0.1718567216508439);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body55.addFixture(bf);
		}
		body55.translate(new Vector2(5.4710317609321555, 2.4165991252902006));
		body55.setMass(Mass.Type.NORMAL);
		world.addBody(body55);

		// Body56
		Body body56 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.0198378735340194,
					1.0198378735340194);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body56.addFixture(bf);
		}
		body56.translate(new Vector2(-6.036991852461182, 1.2886810020279817));
		body56.setMass(Mass.Type.NORMAL);
		world.addBody(body56);

		// Body57
		Body body57 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.38429622248288436);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body57.addFixture(bf);
		}
		body57.translate(new Vector2(6.155153441857344, 4.859882036012544));
		body57.setMass(Mass.Type.NORMAL);
		world.addBody(body57);

		// Body58
		Body body58 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.4770964009182179);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body58.addFixture(bf);
		}
		body58.translate(new Vector2(-2.381935652360842, 8.701001840366512));
		body58.setMass(Mass.Type.NORMAL);
		world.addBody(body58);

		// Body59
		Body body59 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.19407226396625948);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body59.addFixture(bf);
		}
		body59.translate(new Vector2(2.9787332494427647, 6.185809559172435));
		body59.setMass(Mass.Type.NORMAL);
		world.addBody(body59);

		// Body60
		Body body60 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.8467358249101412,
					0.8467358249101412);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body60.addFixture(bf);
		}
		body60.translate(new Vector2(-2.0709644055898475, 5.109891643137622));
		body60.setMass(Mass.Type.NORMAL);
		world.addBody(body60);

		// Body61
		Body body61 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.0594419437064406,
					1.0594419437064406);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body61.addFixture(bf);
		}
		body61.translate(new Vector2(5.9976093972769515, 13.918653529648326));
		body61.setMass(Mass.Type.NORMAL);
		world.addBody(body61);

		// Body62
		Body body62 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.3937835927369919);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body62.addFixture(bf);
		}
		body62.translate(new Vector2(-0.5272784965213353, 14.92077022389309));
		body62.setMass(Mass.Type.NORMAL);
		world.addBody(body62);

		// Body63
		Body body63 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.38194514450149886);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body63.addFixture(bf);
		}
		body63.translate(new Vector2(1.4145128559863944, 14.806462564515838));
		body63.setMass(Mass.Type.NORMAL);
		world.addBody(body63);

		// Body64
		Body body64 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.5337978145653176);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body64.addFixture(bf);
		}
		body64.translate(new Vector2(-0.8367389307960994, 12.865540326614669));
		body64.setMass(Mass.Type.NORMAL);
		world.addBody(body64);

		// Body65
		Body body65 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.3420056556814798,
					0.3420056556814798);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body65.addFixture(bf);
		}
		body65.translate(new Vector2(6.037082857507388, 3.0735217761863165));
		body65.setMass(Mass.Type.NORMAL);
		world.addBody(body65);

		// Body66
		Body body66 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.0693173258667334,
					1.0693173258667334);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body66.addFixture(bf);
		}
		body66.translate(new Vector2(-2.6324577401989595, 14.050555036015213));
		body66.setMass(Mass.Type.NORMAL);
		world.addBody(body66);

		// Body67
		Body body67 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.5169919500597395);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body67.addFixture(bf);
		}
		body67.translate(new Vector2(0.5759906628114146, 9.473409038515605));
		body67.setMass(Mass.Type.NORMAL);
		world.addBody(body67);

		// Body68
		Body body68 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.060637212537973564);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body68.addFixture(bf);
		}
		body68.translate(new Vector2(-5.6710705517767055, 6.536012187641885));
		body68.setMass(Mass.Type.NORMAL);
		world.addBody(body68);

		// Body69
		Body body69 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.050863340643350255);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body69.addFixture(bf);
		}
		body69.translate(new Vector2(0.6124645143026399, 3.3628094274699225));
		body69.setMass(Mass.Type.NORMAL);
		world.addBody(body69);

		// Body70
		Body body70 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.3463262484252299);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body70.addFixture(bf);
		}
		body70.translate(new Vector2(-6.56012999544052, 13.701447446631734));
		body70.setMass(Mass.Type.NORMAL);
		world.addBody(body70);

		// Body71
		Body body71 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.3327019129044136);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body71.addFixture(bf);
		}
		body71.translate(new Vector2(5.9509359144112635, 8.727518312280695));
		body71.setMass(Mass.Type.NORMAL);
		world.addBody(body71);

		// Body72
		Body body72 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.2861913151611636,
					0.2861913151611636);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body72.addFixture(bf);
		}
		body72.translate(new Vector2(-2.9197071199071005, 1.9769904699605134));
		body72.setMass(Mass.Type.NORMAL);
		world.addBody(body72);

		// Body73
		Body body73 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.24238165373177734);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body73.addFixture(bf);
		}
		body73.translate(new Vector2(0.07310921348839017, 5.769460173776579));
		body73.setMass(Mass.Type.NORMAL);
		world.addBody(body73);

		// Body74
		Body body74 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.16070885132156945,
					0.16070885132156945);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body74.addFixture(bf);
		}
		body74.translate(new Vector2(-1.5500981528016586, 6.713997016920705));
		body74.setMass(Mass.Type.NORMAL);
		world.addBody(body74);

		// Body75
		Body body75 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.3460059063454248);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body75.addFixture(bf);
		}
		body75.translate(new Vector2(0.9034127886465787, 1.892649576213631));
		body75.setMass(Mass.Type.NORMAL);
		world.addBody(body75);

		// Body76
		Body body76 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.5707239310391335,
					0.5707239310391335);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body76.addFixture(bf);
		}
		body76.translate(new Vector2(-3.2442791769911876, 14.034068091889033));
		body76.setMass(Mass.Type.NORMAL);
		world.addBody(body76);

		// Body77
		Body body77 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.470454856356467,
					0.470454856356467);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body77.addFixture(bf);
		}
		body77.translate(new Vector2(3.4248610298949496, 8.467916849078179));
		body77.setMass(Mass.Type.NORMAL);
		world.addBody(body77);

		// Body78
		Body body78 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.31687640368384984);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body78.addFixture(bf);
		}
		body78.translate(new Vector2(-0.7705369191762341, 3.8555899682525516));
		body78.setMass(Mass.Type.NORMAL);
		world.addBody(body78);

		// Body79
		Body body79 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.12017783477603984);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body79.addFixture(bf);
		}
		body79.translate(new Vector2(2.8985043684552627, 7.190560848735549));
		body79.setMass(Mass.Type.NORMAL);
		world.addBody(body79);

		// Body80
		Body body80 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.44193395875048436);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body80.addFixture(bf);
		}
		body80.translate(new Vector2(-0.6387730740946571, 7.04168480007445));
		body80.setMass(Mass.Type.NORMAL);
		world.addBody(body80);

		// Body81
		Body body81 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.2245321540399771,
					0.2245321540399771);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body81.addFixture(bf);
		}
		body81.translate(new Vector2(1.797902543104146, 4.956051800080884));
		body81.setMass(Mass.Type.NORMAL);
		world.addBody(body81);

		// Body82
		Body body82 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.793860677786305,
					0.793860677786305);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body82.addFixture(bf);
		}
		body82.translate(new Vector2(-5.050058480113792, 9.211341593062468));
		body82.setMass(Mass.Type.NORMAL);
		world.addBody(body82);

		// Body83
		Body body83 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.27231505227516967);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body83.addFixture(bf);
		}
		body83.translate(new Vector2(4.750581782092442, 11.420536305692208));
		body83.setMass(Mass.Type.NORMAL);
		world.addBody(body83);

		// Body84
		Body body84 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.5756020565332961,
					0.5756020565332961);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body84.addFixture(bf);
		}
		body84.translate(new Vector2(-2.2141607452347567, 13.910690592127295));
		body84.setMass(Mass.Type.NORMAL);
		world.addBody(body84);

		// Body85
		Body body85 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.24563174131300827,
					0.24563174131300827);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body85.addFixture(bf);
		}
		body85.translate(new Vector2(5.526067332403243, 2.8518116265178355));
		body85.setMass(Mass.Type.NORMAL);
		world.addBody(body85);

		// Body86
		Body body86 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.387983333334941);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body86.addFixture(bf);
		}
		body86.translate(new Vector2(-1.3632675300029597, 9.488957181758515));
		body86.setMass(Mass.Type.NORMAL);
		world.addBody(body86);

		// Body87
		Body body87 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.2113047563868844);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body87.addFixture(bf);
		}
		body87.translate(new Vector2(6.583745072696165, 2.572963137914148));
		body87.setMass(Mass.Type.NORMAL);
		world.addBody(body87);

		// Body88
		Body body88 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.4135918597026794);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body88.addFixture(bf);
		}
		body88.translate(new Vector2(-4.836826171746334, 11.266752969725065));
		body88.setMass(Mass.Type.NORMAL);
		world.addBody(body88);

		// Body89
		Body body89 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.7338845225159352,
					0.7338845225159352);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body89.addFixture(bf);
		}
		body89.translate(new Vector2(5.056529064336144, 11.152387160215692));
		body89.setMass(Mass.Type.NORMAL);
		world.addBody(body89);

		// Body90
		Body body90 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.056752361624918,
					1.056752361624918);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body90.addFixture(bf);
		}
		body90.translate(new Vector2(-5.00854236494412, 13.691338860825708));
		body90.setMass(Mass.Type.NORMAL);
		world.addBody(body90);

		// Body91
		Body body91 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.4548931969873673,
					0.4548931969873673);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body91.addFixture(bf);
		}
		body91.translate(new Vector2(0.8721174520496123, 10.125620393370214));
		body91.setMass(Mass.Type.NORMAL);
		world.addBody(body91);

		// Body92
		Body body92 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.13115959551841294);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body92.addFixture(bf);
		}
		body92.translate(new Vector2(-3.0948835846727047, 12.563988731101967));
		body92.setMass(Mass.Type.NORMAL);
		world.addBody(body92);

		// Body93
		Body body93 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.5021905553094111);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body93.addFixture(bf);
		}
		body93.translate(new Vector2(0.5668676987591313, 11.386736734712253));
		body93.setMass(Mass.Type.NORMAL);
		world.addBody(body93);

		// Body94
		Body body94 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.11515759512804827,
					0.11515759512804827);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body94.addFixture(bf);
		}
		body94.translate(new Vector2(-0.03633653795944514, 13.887145655312901));
		body94.setMass(Mass.Type.NORMAL);
		world.addBody(body94);

		// Body95
		Body body95 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.36603855099906774);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body95.addFixture(bf);
		}
		body95.translate(new Vector2(6.421004385596704, 1.2101933923308814));
		body95.setMass(Mass.Type.NORMAL);
		world.addBody(body95);

		// Body96
		Body body96 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.5652269958781965,
					0.5652269958781965);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body96.addFixture(bf);
		}
		body96.translate(new Vector2(-2.260662128356081, 10.075108586938763));
		body96.setMass(Mass.Type.NORMAL);
		world.addBody(body96);

		// Body97
		Body body97 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.3061190065687181);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body97.addFixture(bf);
		}
		body97.translate(new Vector2(4.525113822274603, 5.392919583499586));
		body97.setMass(Mass.Type.NORMAL);
		world.addBody(body97);

		// Body98
		Body body98 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.990673623647648,
					0.990673623647648);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body98.addFixture(bf);
		}
		body98.translate(new Vector2(-6.9677437609246615, 9.892688092535213));
		body98.setMass(Mass.Type.NORMAL);
		world.addBody(body98);

		// Body99
		Body body99 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.1551200930252989);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body99.addFixture(bf);
		}
		body99.translate(new Vector2(1.7495176663188992, 6.652825403380667));
		body99.setMass(Mass.Type.NORMAL);
		world.addBody(body99);

		// Body100
		Body body100 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.0898280110132321,
					1.0898280110132321);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body100.addFixture(bf);
		}
		body100.translate(new Vector2(-4.627495196184105, 12.242864914054335));
		body100.setMass(Mass.Type.NORMAL);
		world.addBody(body100);

		// Body101
		Body body101 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.09687280021122181);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body101.addFixture(bf);
		}
		body101.translate(new Vector2(6.287262763773848, 8.37889461603794));
		body101.setMass(Mass.Type.NORMAL);
		world.addBody(body101);

		// Body102
		Body body102 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.5377646881002901,
					0.5377646881002901);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body102.addFixture(bf);
		}
		body102.translate(new Vector2(-5.435408785032828, 1.8634128731412085));
		body102.setMass(Mass.Type.NORMAL);
		world.addBody(body102);

		// Body103
		Body body103 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.043047763955859,
					1.043047763955859);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body103.addFixture(bf);
		}
		body103.translate(new Vector2(6.536236476913971, 8.061011411766376));
		body103.setMass(Mass.Type.NORMAL);
		world.addBody(body103);

		// Body104
		Body body104 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.16881739884727923,
					0.16881739884727923);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body104.addFixture(bf);
		}
		body104.translate(new Vector2(-6.5500937632742575, 12.440683402922138));
		body104.setMass(Mass.Type.NORMAL);
		world.addBody(body104);

		// Body105
		Body body105 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.16274589535056067);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body105.addFixture(bf);
		}
		body105.translate(new Vector2(4.06388790018847, 11.84447402778531));
		body105.setMass(Mass.Type.NORMAL);
		world.addBody(body105);

		// Body106
		Body body106 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.14292500731004854);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body106.addFixture(bf);
		}
		body106.translate(new Vector2(-6.721753943733683, 2.405955189728253));
		body106.setMass(Mass.Type.NORMAL);
		world.addBody(body106);

		// Body107
		Body body107 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.1921643847190091);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body107.addFixture(bf);
		}
		body107.translate(new Vector2(4.04006660374961, 2.7806346117348815));
		body107.setMass(Mass.Type.NORMAL);
		world.addBody(body107);

		// Body108
		Body body108 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.559740580798365,
					0.559740580798365);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body108.addFixture(bf);
		}
		body108.translate(new Vector2(-2.201398890594816, 8.887382048643255));
		body108.setMass(Mass.Type.NORMAL);
		world.addBody(body108);

		// Body109
		Body body109 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.098119388372038,
					1.098119388372038);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body109.addFixture(bf);
		}
		body109.translate(new Vector2(6.908905202965681, 2.1244566778606684));
		body109.setMass(Mass.Type.NORMAL);
		world.addBody(body109);

		// Body110
		Body body110 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.8027486715138856,
					0.8027486715138856);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body110.addFixture(bf);
		}
		body110.translate(new Vector2(-6.043215689577052, 12.719736338826358));
		body110.setMass(Mass.Type.NORMAL);
		world.addBody(body110);

		// Body111
		Body body111 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.0205676001845905,
					1.0205676001845905);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body111.addFixture(bf);
		}
		body111.translate(new Vector2(4.860040209571233, 12.468615103400069));
		body111.setMass(Mass.Type.NORMAL);
		world.addBody(body111);

		// Body112
		Body body112 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.3296608110113598);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body112.addFixture(bf);
		}
		body112.translate(new Vector2(-6.4817532066929235, 5.727350456958197));
		body112.setMass(Mass.Type.NORMAL);
		world.addBody(body112);

		// Body113
		Body body113 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.6530604997723835,
					0.6530604997723835);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body113.addFixture(bf);
		}
		body113.translate(new Vector2(2.0734275418728543, 13.436339717603166));
		body113.setMass(Mass.Type.NORMAL);
		world.addBody(body113);

		// Body114
		Body body114 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.3731878090852402,
					0.3731878090852402);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body114.addFixture(bf);
		}
		body114.translate(new Vector2(-6.504610215074548, 1.3490456812951934));
		body114.setMass(Mass.Type.NORMAL);
		world.addBody(body114);

		// Body115
		Body body115 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.7876190442982464,
					0.7876190442982464);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body115.addFixture(bf);
		}
		body115.translate(new Vector2(1.654858977132177, 1.161786335890563));
		body115.setMass(Mass.Type.NORMAL);
		world.addBody(body115);

		// Body116
		Body body116 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.23673315277776252);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body116.addFixture(bf);
		}
		body116.translate(new Vector2(-5.950945500120648, 12.34028347978079));
		body116.setMass(Mass.Type.NORMAL);
		world.addBody(body116);

		// Body117
		Body body117 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.4353213595580068);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body117.addFixture(bf);
		}
		body117.translate(new Vector2(3.7383227773959273, 12.964402766239635));
		body117.setMass(Mass.Type.NORMAL);
		world.addBody(body117);

		// Body118
		Body body118 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.2750673984510311);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body118.addFixture(bf);
		}
		body118.translate(new Vector2(-2.2364349009417066, 11.792357117874213));
		body118.setMass(Mass.Type.NORMAL);
		world.addBody(body118);

		// Body119
		Body body119 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.26811972240774945);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body119.addFixture(bf);
		}
		body119.translate(new Vector2(1.3131438191695293, 14.670878457007444));
		body119.setMass(Mass.Type.NORMAL);
		world.addBody(body119);

		// Body120
		Body body120 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.5146250565349019);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body120.addFixture(bf);
		}
		body120.translate(new Vector2(-2.3882693819633123, 13.281602039179695));
		body120.setMass(Mass.Type.NORMAL);
		world.addBody(body120);

		// Body121
		Body body121 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.3797022043842089,
					0.3797022043842089);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body121.addFixture(bf);
		}
		body121.translate(new Vector2(0.48094681620964186, 11.675626996585168));
		body121.setMass(Mass.Type.NORMAL);
		world.addBody(body121);

		// Body122
		Body body122 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.7319316917380673,
					0.7319316917380673);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body122.addFixture(bf);
		}
		body122.translate(new Vector2(-6.0319823232422305, 4.9265132149582245));
		body122.setMass(Mass.Type.NORMAL);
		world.addBody(body122);

		// Body123
		Body body123 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.2697382106511592,
					0.2697382106511592);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body123.addFixture(bf);
		}
		body123.translate(new Vector2(2.309351972957582, 13.645733248242074));
		body123.setMass(Mass.Type.NORMAL);
		world.addBody(body123);

		// Body124
		Body body124 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.8718383415031288,
					0.8718383415031288);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body124.addFixture(bf);
		}
		body124.translate(new Vector2(-1.6739009431721397, 3.1553130599369856));
		body124.setMass(Mass.Type.NORMAL);
		world.addBody(body124);

		// Body125
		Body body125 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.4074585641431461,
					0.4074585641431461);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body125.addFixture(bf);
		}
		body125.translate(new Vector2(4.363162144475284, 2.1383004817738858));
		body125.setMass(Mass.Type.NORMAL);
		world.addBody(body125);

		// Body126
		Body body126 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.7384660624605481,
					0.7384660624605481);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body126.addFixture(bf);
		}
		body126.translate(new Vector2(-3.6633851717616692, 1.9417348565426193));
		body126.setMass(Mass.Type.NORMAL);
		world.addBody(body126);

		// Body127
		Body body127 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.30703610173029244);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body127.addFixture(bf);
		}
		body127.translate(new Vector2(3.0620302940134945, 4.156175072288958));
		body127.setMass(Mass.Type.NORMAL);
		world.addBody(body127);

		// Body128
		Body body128 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.40309068774778367,
					0.40309068774778367);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body128.addFixture(bf);
		}
		body128.translate(new Vector2(-5.6959672338035965, 7.745834510039156));
		body128.setMass(Mass.Type.NORMAL);
		world.addBody(body128);

		// Body129
		Body body129 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.9509816776242854,
					0.9509816776242854);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body129.addFixture(bf);
		}
		body129.translate(new Vector2(0.34581005283913757, 12.904049471351712));
		body129.setMass(Mass.Type.NORMAL);
		world.addBody(body129);

		// Body130
		Body body130 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.3902555365077435,
					0.3902555365077435);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body130.addFixture(bf);
		}
		body130.translate(new Vector2(-1.709512771523146, 12.26289338907026));
		body130.setMass(Mass.Type.NORMAL);
		world.addBody(body130);

		// Body131
		Body body131 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.17278992241103644,
					0.17278992241103644);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body131.addFixture(bf);
		}
		body131.translate(new Vector2(3.9474803129463845, 3.680725531299211));
		body131.setMass(Mass.Type.NORMAL);
		world.addBody(body131);

		// Body132
		Body body132 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.11168006140522625);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body132.addFixture(bf);
		}
		body132.translate(new Vector2(-6.756635102279852, 7.226029835517388));
		body132.setMass(Mass.Type.NORMAL);
		world.addBody(body132);

		// Body133
		Body body133 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.37352600082301113);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body133.addFixture(bf);
		}
		body133.translate(new Vector2(0.26411543106905533, 6.22737110335172));
		body133.setMass(Mass.Type.NORMAL);
		world.addBody(body133);

		// Body134
		Body body134 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.1800816238122285,
					0.1800816238122285);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body134.addFixture(bf);
		}
		body134.translate(new Vector2(-3.5480361892874, 2.2980080283232707));
		body134.setMass(Mass.Type.NORMAL);
		world.addBody(body134);

		// Body135
		Body body135 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.5400158627675264,
					0.5400158627675264);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body135.addFixture(bf);
		}
		body135.translate(new Vector2(3.333135419328551, 13.217145521813954));
		body135.setMass(Mass.Type.NORMAL);
		world.addBody(body135);

		// Body136
		Body body136 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.9518737320379923,
					0.9518737320379923);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body136.addFixture(bf);
		}
		body136.translate(new Vector2(-1.3279460971108892, 10.838398292083765));
		body136.setMass(Mass.Type.NORMAL);
		world.addBody(body136);

		// Body137
		Body body137 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.386406009768392,
					0.386406009768392);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body137.addFixture(bf);
		}
		body137.translate(new Vector2(2.3419858113161425, 10.648782549813207));
		body137.setMass(Mass.Type.NORMAL);
		world.addBody(body137);

		// Body138
		Body body138 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.22178310719808184,
					0.22178310719808184);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body138.addFixture(bf);
		}
		body138.translate(new Vector2(-0.9136063915967683, 12.382695964078762));
		body138.setMass(Mass.Type.NORMAL);
		world.addBody(body138);

		// Body139
		Body body139 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.2146549955751718,
					0.2146549955751718);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body139.addFixture(bf);
		}
		body139.translate(new Vector2(5.654229025018095, 14.205494340641346));
		body139.setMass(Mass.Type.NORMAL);
		world.addBody(body139);

		// Body140
		Body body140 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.2883087054728617);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body140.addFixture(bf);
		}
		body140.translate(new Vector2(-3.610211330820338, 11.519354973660365));
		body140.setMass(Mass.Type.NORMAL);
		world.addBody(body140);

		// Body141
		Body body141 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.17396277769946325,
					0.17396277769946325);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body141.addFixture(bf);
		}
		body141.translate(new Vector2(1.5118710409555787, 6.779917069102093));
		body141.setMass(Mass.Type.NORMAL);
		world.addBody(body141);

		// Body142
		Body body142 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.539243165607275);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body142.addFixture(bf);
		}
		body142.translate(new Vector2(-6.009230355380466, 8.0867305328487));
		body142.setMass(Mass.Type.NORMAL);
		world.addBody(body142);

		// Body143
		Body body143 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.6955766380346571,
					0.6955766380346571);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body143.addFixture(bf);
		}
		body143.translate(new Vector2(6.941047095306077, 12.123122772617466));
		body143.setMass(Mass.Type.NORMAL);
		world.addBody(body143);

		// Body144
		Body body144 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.2793766336437552);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body144.addFixture(bf);
		}
		body144.translate(new Vector2(-4.30435350101775, 14.188365181168525));
		body144.setMass(Mass.Type.NORMAL);
		world.addBody(body144);

		// Body145
		Body body145 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.2066073291949424);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body145.addFixture(bf);
		}
		body145.translate(new Vector2(0.5499657126309837, 7.429032350983618));
		body145.setMass(Mass.Type.NORMAL);
		world.addBody(body145);

		// Body146
		Body body146 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.3678809746115181);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body146.addFixture(bf);
		}
		body146.translate(new Vector2(-5.37448217052999, 12.254580281012185));
		body146.setMass(Mass.Type.NORMAL);
		world.addBody(body146);

		// Body147
		Body body147 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.0049629788916534,
					1.0049629788916534);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body147.addFixture(bf);
		}
		body147.translate(new Vector2(1.5416824244030356, 12.165116961024651));
		body147.setMass(Mass.Type.NORMAL);
		world.addBody(body147);

		// Body148
		Body body148 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.13028297382192083);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body148.addFixture(bf);
		}
		body148.translate(new Vector2(-6.722144568097052, 11.321142721609977));
		body148.setMass(Mass.Type.NORMAL);
		world.addBody(body148);

		// Body149
		Body body149 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.29698246924724536,
					0.29698246924724536);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body149.addFixture(bf);
		}
		body149.translate(new Vector2(1.4713621967077406, 6.827427961693318));
		body149.setMass(Mass.Type.NORMAL);
		world.addBody(body149);

		// Body150
		Body body150 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.3380899658783299);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body150.addFixture(bf);
		}
		body150.translate(new Vector2(-6.998661519056404, 6.43582127381071));
		body150.setMass(Mass.Type.NORMAL);
		world.addBody(body150);

		// Body151
		Body body151 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.22320717614793933);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body151.addFixture(bf);
		}
		body151.translate(new Vector2(3.509370724423099, 12.212789890540451));
		body151.setMass(Mass.Type.NORMAL);
		world.addBody(body151);

		// Body152
		Body body152 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.8354264585692943,
					0.8354264585692943);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body152.addFixture(bf);
		}
		body152.translate(new Vector2(-4.237883106860887, 13.717259236094263));
		body152.setMass(Mass.Type.NORMAL);
		world.addBody(body152);

		// Body153
		Body body153 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.5465016540401817,
					0.5465016540401817);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body153.addFixture(bf);
		}
		body153.translate(new Vector2(4.07173074402495, 3.401431987490366));
		body153.setMass(Mass.Type.NORMAL);
		world.addBody(body153);

		// Body154
		Body body154 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.6220907652021131,
					0.6220907652021131);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body154.addFixture(bf);
		}
		body154.translate(new Vector2(-6.346578763124829, 3.424692793870255));
		body154.setMass(Mass.Type.NORMAL);
		world.addBody(body154);

		// Body155
		Body body155 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.3818983283185477);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body155.addFixture(bf);
		}
		body155.translate(new Vector2(4.619931936483606, 8.322756420895669));
		body155.setMass(Mass.Type.NORMAL);
		world.addBody(body155);

		// Body156
		Body body156 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.3033181320617947,
					0.3033181320617947);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body156.addFixture(bf);
		}
		body156.translate(new Vector2(-2.3104304393501556, 13.586607557359706));
		body156.setMass(Mass.Type.NORMAL);
		world.addBody(body156);

		// Body157
		Body body157 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.5282586789012279);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body157.addFixture(bf);
		}
		body157.translate(new Vector2(6.094762012949048, 7.427408759618526));
		body157.setMass(Mass.Type.NORMAL);
		world.addBody(body157);

		// Body158
		Body body158 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.29416473136969323,
					0.29416473136969323);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body158.addFixture(bf);
		}
		body158.translate(new Vector2(-2.007058722360677, 6.703341380823966));
		body158.setMass(Mass.Type.NORMAL);
		world.addBody(body158);

		// Body159
		Body body159 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.5155409276219133);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body159.addFixture(bf);
		}
		body159.translate(new Vector2(6.342424345547997, 3.9020043502452384));
		body159.setMass(Mass.Type.NORMAL);
		world.addBody(body159);

		// Body160
		Body body160 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.91050405863145,
					0.91050405863145);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body160.addFixture(bf);
		}
		body160.translate(new Vector2(-2.5699880489983444, 12.026797530477335));
		body160.setMass(Mass.Type.NORMAL);
		world.addBody(body160);

		// Body161
		Body body161 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.3488961797745214);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body161.addFixture(bf);
		}
		body161.translate(new Vector2(4.96965004248245, 9.561364770146803));
		body161.setMass(Mass.Type.NORMAL);
		world.addBody(body161);

		// Body162
		Body body162 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.17231313280121202);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body162.addFixture(bf);
		}
		body162.translate(new Vector2(-5.437923951903824, 8.934040370555442));
		body162.setMass(Mass.Type.NORMAL);
		world.addBody(body162);

		// Body163
		Body body163 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.312162367692362);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body163.addFixture(bf);
		}
		body163.translate(new Vector2(1.8468419457409877, 6.388574071281916));
		body163.setMass(Mass.Type.NORMAL);
		world.addBody(body163);

		// Body164
		Body body164 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.522858362004631);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body164.addFixture(bf);
		}
		body164.translate(new Vector2(-0.7295341059221937, 2.236488160239513));
		body164.setMass(Mass.Type.NORMAL);
		world.addBody(body164);

		// Body165
		Body body165 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.8941189692784465,
					0.8941189692784465);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body165.addFixture(bf);
		}
		body165.translate(new Vector2(1.4501092572920558, 4.109214536293088));
		body165.setMass(Mass.Type.NORMAL);
		world.addBody(body165);

		// Body166
		Body body166 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.1859534290043198,
					0.1859534290043198);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body166.addFixture(bf);
		}
		body166.translate(new Vector2(-5.265163178915082, 4.066879072481729));
		body166.setMass(Mass.Type.NORMAL);
		world.addBody(body166);

		// Body167
		Body body167 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.426537136536678);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body167.addFixture(bf);
		}
		body167.translate(new Vector2(6.04166446686976, 8.107583470468315));
		body167.setMass(Mass.Type.NORMAL);
		world.addBody(body167);

		// Body168
		Body body168 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.2254912603608782);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body168.addFixture(bf);
		}
		body168.translate(new Vector2(-2.322007112508924, 3.280984017946152));
		body168.setMass(Mass.Type.NORMAL);
		world.addBody(body168);

		// Body169
		Body body169 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.8443932694161133,
					0.8443932694161133);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body169.addFixture(bf);
		}
		body169.translate(new Vector2(1.9237494491673126, 1.8638057611931909));
		body169.setMass(Mass.Type.NORMAL);
		world.addBody(body169);

		// Body170
		Body body170 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.28858650913786904);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body170.addFixture(bf);
		}
		body170.translate(new Vector2(-5.687856206050134, 5.9472139927978525));
		body170.setMass(Mass.Type.NORMAL);
		world.addBody(body170);

		// Body171
		Body body171 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.15544047042433043,
					0.15544047042433043);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body171.addFixture(bf);
		}
		body171.translate(new Vector2(2.389542247109277, 8.64922959142487));
		body171.setMass(Mass.Type.NORMAL);
		world.addBody(body171);

		// Body172
		Body body172 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.2251988498238081);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body172.addFixture(bf);
		}
		body172.translate(new Vector2(-1.2223643421629355, 13.550377058883393));
		body172.setMass(Mass.Type.NORMAL);
		world.addBody(body172);

		// Body173
		Body body173 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.1892540346067532,
					0.1892540346067532);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body173.addFixture(bf);
		}
		body173.translate(new Vector2(6.6041225766992335, 5.151906658261973));
		body173.setMass(Mass.Type.NORMAL);
		world.addBody(body173);

		// Body174
		Body body174 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.36962456955176853);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body174.addFixture(bf);
		}
		body174.translate(new Vector2(-2.7744953614182686, 13.87900419902253));
		body174.setMass(Mass.Type.NORMAL);
		world.addBody(body174);

		// Body175
		Body body175 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.44055681207846553);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body175.addFixture(bf);
		}
		body175.translate(new Vector2(4.252737053365385, 13.906202659247748));
		body175.setMass(Mass.Type.NORMAL);
		world.addBody(body175);

		// Body176
		Body body176 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.3183791360385481);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body176.addFixture(bf);
		}
		body176.translate(new Vector2(-3.845368674116036, 13.162694505980873));
		body176.setMass(Mass.Type.NORMAL);
		world.addBody(body176);

		// Body177
		Body body177 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.16788905807265653);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body177.addFixture(bf);
		}
		body177.translate(new Vector2(0.8015970251894683, 5.586248440694629));
		body177.setMass(Mass.Type.NORMAL);
		world.addBody(body177);

		// Body178
		Body body178 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.12314291743953666);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body178.addFixture(bf);
		}
		body178.translate(new Vector2(-3.731506227320013, 12.814688544465458));
		body178.setMass(Mass.Type.NORMAL);
		world.addBody(body178);

		// Body179
		Body body179 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.33911701980229236,
					0.33911701980229236);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body179.addFixture(bf);
		}
		body179.translate(new Vector2(0.6256316486142202, 10.255985273339453));
		body179.setMass(Mass.Type.NORMAL);
		world.addBody(body179);

		// Body180
		Body body180 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.35020766631232825,
					0.35020766631232825);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body180.addFixture(bf);
		}
		body180.translate(new Vector2(-1.3378410730829668, 13.181695304961817));
		body180.setMass(Mass.Type.NORMAL);
		world.addBody(body180);

		// Body181
		Body body181 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.5342065682362022,
					0.5342065682362022);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body181.addFixture(bf);
		}
		body181.translate(new Vector2(4.855695589686542, 12.988380621247606));
		body181.setMass(Mass.Type.NORMAL);
		world.addBody(body181);

		// Body182
		Body body182 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.3187861651035538);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body182.addFixture(bf);
		}
		body182.translate(new Vector2(-5.142546488327987, 7.6194106953401555));
		body182.setMass(Mass.Type.NORMAL);
		world.addBody(body182);

		// Body183
		Body body183 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.0081021831354748,
					1.0081021831354748);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body183.addFixture(bf);
		}
		body183.translate(new Vector2(4.377292016092975, 12.378419171919344));
		body183.setMass(Mass.Type.NORMAL);
		world.addBody(body183);

		// Body184
		Body body184 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.14283156472199854);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body184.addFixture(bf);
		}
		body184.translate(new Vector2(-1.5462403880895503, 12.639188887505844));
		body184.setMass(Mass.Type.NORMAL);
		world.addBody(body184);

		// Body185
		Body body185 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.27548447248050506);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body185.addFixture(bf);
		}
		body185.translate(new Vector2(4.041103017599663, 10.527772507082318));
		body185.setMass(Mass.Type.NORMAL);
		world.addBody(body185);

		// Body186
		Body body186 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.3834906649370393,
					0.3834906649370393);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body186.addFixture(bf);
		}
		body186.translate(new Vector2(-3.6697219326028634, 2.148347451583983));
		body186.setMass(Mass.Type.NORMAL);
		world.addBody(body186);

		// Body187
		Body body187 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.179572936008945,
					0.179572936008945);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body187.addFixture(bf);
		}
		body187.translate(new Vector2(0.5634431652581207, 9.116546546375307));
		body187.setMass(Mass.Type.NORMAL);
		world.addBody(body187);

		// Body188
		Body body188 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.3417564656488009,
					0.3417564656488009);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body188.addFixture(bf);
		}
		body188.translate(new Vector2(-1.3405058229292188, 2.754324247032513));
		body188.setMass(Mass.Type.NORMAL);
		world.addBody(body188);

		// Body189
		Body body189 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.2387871346714293);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body189.addFixture(bf);
		}
		body189.translate(new Vector2(6.600743136336251, 4.707752881998891));
		body189.setMass(Mass.Type.NORMAL);
		world.addBody(body189);

		// Body190
		Body body190 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.6419598482023334,
					0.6419598482023334);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body190.addFixture(bf);
		}
		body190.translate(new Vector2(-5.86793721999458, 9.163056434471903));
		body190.setMass(Mass.Type.NORMAL);
		world.addBody(body190);

		// Body191
		Body body191 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.10421781474973275);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body191.addFixture(bf);
		}
		body191.translate(new Vector2(6.688188823276665, 8.531190819579868));
		body191.setMass(Mass.Type.NORMAL);
		world.addBody(body191);

		// Body192
		Body body192 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.022789457732974,
					1.022789457732974);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body192.addFixture(bf);
		}
		body192.translate(new Vector2(-1.8324633442973748, 8.487784379000612));
		body192.setMass(Mass.Type.NORMAL);
		world.addBody(body192);

		// Body193
		Body body193 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.4261582273052383);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body193.addFixture(bf);
		}
		body193.translate(new Vector2(6.358838735481853, 1.2222934410919495));
		body193.setMass(Mass.Type.NORMAL);
		world.addBody(body193);

		// Body194
		Body body194 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.4483785365115294);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body194.addFixture(bf);
		}
		body194.translate(new Vector2(-1.5746433647685463, 3.90981268845536));
		body194.setMass(Mass.Type.NORMAL);
		world.addBody(body194);

		// Body195
		Body body195 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.40747426651826113,
					0.40747426651826113);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body195.addFixture(bf);
		}
		body195.translate(new Vector2(0.16834185188011097, 10.935374748565403));
		body195.setMass(Mass.Type.NORMAL);
		world.addBody(body195);

		// Body196
		Body body196 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.21054866124059626,
					0.21054866124059626);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body196.addFixture(bf);
		}
		body196.translate(new Vector2(-6.226706467374679, 11.498974636999522));
		body196.setMass(Mass.Type.NORMAL);
		world.addBody(body196);

		// Body197
		Body body197 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(1.0201425932526846,
					1.0201425932526846);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body197.addFixture(bf);
		}
		body197.translate(new Vector2(0.7994965012937562, 1.6182204607215396));
		body197.setMass(Mass.Type.NORMAL);
		world.addBody(body197);

		// Body198
		Body body198 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.6694637731277837,
					0.6694637731277837);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body198.addFixture(bf);
		}
		body198.translate(new Vector2(-2.4232278238798974, 8.534762865269139));
		body198.setMass(Mass.Type.NORMAL);
		world.addBody(body198);

		// Body199
		Body body199 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.16637513169661747,
					0.16637513169661747);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body199.addFixture(bf);
		}
		body199.translate(new Vector2(6.047465729130442, 3.9481275809190692));
		body199.setMass(Mass.Type.NORMAL);
		world.addBody(body199);

		// Body200
		Body body200 = new Body();
		{// Fixture1
			Convex c = Geometry.createRectangle(0.30291221743394525,
					0.30291221743394525);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body200.addFixture(bf);
		}
		body200.translate(new Vector2(-2.584309371303852, 1.8122184732521593));
		body200.setMass(Mass.Type.NORMAL);
		world.addBody(body200);

		// Body201
		Body body201 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.05313935350864547);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body201.addFixture(bf);
		}
		body201.translate(new Vector2(4.815031695708246, 12.240518386097973));
		body201.setMass(Mass.Type.NORMAL);
		world.addBody(body201);

		// Body202
		Body body202 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.3735156964338623);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body202.addFixture(bf);
		}
		body202.translate(new Vector2(-1.4039068404915347, 8.493563750151));
		body202.setMass(Mass.Type.NORMAL);
		world.addBody(body202);

		// Body203
		Body body203 = new Body();
		{// Fixture1
			Convex c = Geometry.createCircle(0.18614102874467303);
			BodyFixture bf = new BodyFixture(c);
			bf.setDensity(0.1);
			body203.addFixture(bf);
		}
		body203.translate(new Vector2(6.545890277585758, 1.3593228301677163));
		body203.setMass(Mass.Type.NORMAL);
		world.addBody(body203);

	}
}
