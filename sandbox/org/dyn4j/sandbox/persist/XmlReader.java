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
package org.dyn4j.sandbox.persist;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.broadphase.Sap;
import org.dyn4j.collision.continuous.ConservativeAdvancement;
import org.dyn4j.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.Sat;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Capacity;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.MouseJoint;
import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.Camera;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.SandboxRay;
import org.dyn4j.sandbox.Simulation;
import org.dyn4j.sandbox.resources.Messages;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class used to read in a saved simulation file.
 * @author William Bittle
 * @version 1.0.7
 * @since 1.0.0
 */
public class XmlReader extends DefaultHandler {
	// Flags; true if the tag is currently active; false otherwise
	
	/** The current tag name */
	private String tagName;
	
	/** Flag used to determine whether tags were skipped */
	private boolean handled = false;
	
	// system
	
	/** Flag for the System tag */
	private boolean systemFlag;
	
	// settings
	
	/** Flag for the Settings tag */
	private boolean settingsFlag;
	
	// filter
	
	/** Flag for the PartOfGroups tag */
	private boolean partOfGroupsFlag;
	
	/** Flag for the CollideWithGroups tag */
	private boolean collideWithGroupsFlag;
	
	// mass
	
	/** Flag for the Mass tag */
	private boolean massFlag;
	
	/** Flag for the Mass tag under the Mass tag */
	private boolean massMassFlag;
	
	// ray
	
	/** Flag for the ray tag */
	private boolean rayFlag;
	
	// Data; storage for the final results and extra information
	
	/** Storage for the BroadphaseDetector tag */
	private BroadphaseDetector<Body, BodyFixture> broadphase;
	
	/** Storage for the NarrowphaseDetector tag */
	private NarrowphaseDetector narrowphase;
	
	/** Storage for the ManifoldSolver tag */
	private ManifoldSolver manifoldSolver;
	
	/** Storage for the TimeOfImpactDetector tag */
	private TimeOfImpactDetector timeOfImpact;
	
	/** The Gravity tag */
	private Vector2 gravity;
	
	/** The settings */
	private Settings settings;
	
	/** The bounds object */
	private Bounds bounds;
	
	/** The list of bodies */
	private List<SandboxBody> bodies;
	
	/** The list of joints */
	private List<Joint> joints;
	
	/** A mapping of bodies to their original ids contained in the XML file */
	private Map<String, SandboxBody> idMap;
	
	// Tag Data; for temporary storage
	
	/** the version number */
	private String version;
	
	/** The camera */
	private Camera camera;
	
	// world
	
	/** The world name */
	private String worldName;
	
	/** Storage for the LocalCenter tag */
	private Vector2 localCenter;
	
	// rectangle
	
	/** Storage for the Width tag */
	private double width;
	
	/** Storage for the Height tag */
	private double height;
	
	/** Storage for the LocalRotation tag */
	private double localRotation;
	
	// transform
	
	/** Storage for the Translation tag */
	private Vector2 translation;
	
	/** Storage for the Rotation tag */
	private double rotation;
	
	// body
	
	/** Storage for the Body tag */
	private SandboxBody body;
	
	// fixture
	
	/** Storage for the fixture */
	private BodyFixture fixture;
	
	/** Storage for the Name attribute on the Fixture tag */
	private String fixtureName;
	
	// shape
	
	/** Storage for the shape */
	private Convex shape;

	/** Storage for the xsi:type attribute on a Shape tag */
	private String shapeType;
	
	/** Storage for the Radius tag */
	private double radius;
	
	/** Storage for the Vertex tags */
	private List<Vector2> vertices;
	
	// filter
	
	/** Storage for the filter */
	private CategoryFilter filter;
	
	/** Storage for the PartOfGroup GroupX/All tags tag */
	private int category;
	
	/** Storage for the CollidesWithGroups GroupX/All tags tag */
	private int mask;
	
	// mass
	
	/** Storage for the Type tag under the Mass tag */
	private String massType;
	
	/** Storage for the Mass tag under the Mass tag */
	private double massMass;
	
	/** Storage for the Inertia tag */
	private double massInertia;
	
	/** Storage for the Explicit tag */
	private boolean massExplicit;
	
	// joints
	
	/** Storage for the Name attribute of the Joint tag */
	private String jointName;
	
	/** Storage for the xsi:type attribute of the Joint tag */
	private String jointType;
	
	/** Storage for the BodyId1 tag */
	private String bodyId1;
	
	/** Storage for the BodyId2 tag */
	private String bodyId2;
	
	/** Storage for the Collision Allowed tag */
	private boolean collisionAllowed;
	
	/** Storage for the UpperLimit tag */
	private double upperLimit;
	
	/** Storage for the LowerLimit tag */
	private double lowerLimit;
	
	/** Storage for the LimitEnabled tag */
	private boolean limitsEnabled;
	
	/** Storage for the ReferenceAngle tag */
	private double referenceAngle;
	
	/** Storage for the Anchor1 and BodyAnchor1 tags */
	private Vector2 anchor1;
	
	/** Storage for the Anchor2 and BodyAnchor2 tags */
	private Vector2 anchor2;
	
	/** Storage for the Frequency tag */
	private double frequency;
	
	/** Storage for the DampingRatio tag */
	private double dampingRatio;
	
	/** Storage for the Distance tag */
	private double distance;
	
	/** Storage for the Anchor tag */
	private Vector2 anchor;
	
	/** Storage for the MaximumForce tag */
	private double maximumForce;
	
	/** Storage for the MaximumTorque tag */
	private double maximumTorque;
	
	/** Storage for the Target tag */
	private Vector2 target;
	
	/** Storage for the Axis tag */
	private Vector2 axis;
	
	/** Storage for the MotorSpeed tag */
	private double motorSpeed;
	
	/** Storage for the MaximumForce tag */
	private double maximumMotorForce;
	
	/** Storage for the MotorEnabled tag */
	private boolean motorEnabled;
	
	/** Storage for the PulleyAnchor1 tag */
	private Vector2 pulleyAnchor1;
	
	/** Storage for the PulleyAnchor2 tag */
	private Vector2 pulleyAnchor2;
	
	/** Storage for the Ratio tag */
	private double ratio;
	
	/** Storage for the MaximumMotorTorque tag */
	private double maximumMotorTorque;
	
	/** Storage for the LowerLimitEnabled tag */
	private boolean lowerLimitEnabled;
	
	/** Storage for the UpperLimitEnabled tag */
	private boolean upperLimitEnabled;
	
	// rays
	
	/** Storage for the Ray tag */
	private SandboxRay ray;
	
	/** The ray direction */
	private double rayDirection;
	
	/** The start */
	private Vector2 rayStart;
	
	/** Storage for the Rays tag */
	private List<SandboxRay> rays;
	
	/**
	 * Hidden constructor.
	 */
	private XmlReader() {
		this.bodies = new ArrayList<SandboxBody>();
		this.joints = new ArrayList<Joint>();
		this.idMap = new HashMap<String, SandboxBody>();
		this.vertices = new ArrayList<Vector2>();
		this.rays = new ArrayList<SandboxRay>();
		this.settings = new Settings();
	}
	
	/**
	 * Returns a new simulation object from the given file.
	 * @param file the file to read from
	 * @return Simulation the simulation object
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	public static Simulation fromXml(File file) throws ParserConfigurationException, SAXException, IOException {
		return XmlReader.fromXml(new InputSource(new FileReader(file)));
	}
	
	/**
	 * Returns a new simulation object from the given string.
	 * @param xml the string containing the XML to read from
	 * @return Simulation the simulation object
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	public static Simulation fromXml(String xml) throws ParserConfigurationException, SAXException, IOException {
		return XmlReader.fromXml(new InputSource(new StringReader(xml)));
	}
	
	/**
	 * Returns a new simulation object from the given stream.
	 * @param stream the input stream containing the xml
	 * @return Simulation the simulation object
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	public static Simulation fromXml(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
		return XmlReader.fromXml(new InputSource(stream));
	}
	
	/**
	 * Returns a new simulation object from the given input source.
	 * @param source the source containing the XML
	 * @return Simulation the simulation object
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	private static Simulation fromXml(InputSource source) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		
		XmlReader reader = new XmlReader();
		
		parser.parse(source, reader);
		
		// create an initial capacity
		Capacity capacity = new Capacity(
				reader.bodies.size(),
				reader.joints.size(),
				10);
		
		World world = new World(capacity);
		List<SandboxRay> rays = new ArrayList<SandboxRay>();
		Camera camera = new Camera();
		
		// set the settings
		world.setSettings(reader.settings);
		
		// these can be null
		if (reader.broadphase != null) world.setBroadphaseDetector(reader.broadphase);
		if (reader.narrowphase != null) world.setNarrowphaseDetector(reader.narrowphase);
		if (reader.manifoldSolver != null) world.setManifoldSolver(reader.manifoldSolver);
		if (reader.timeOfImpact != null) world.setTimeOfImpactDetector(reader.timeOfImpact);
		if (reader.gravity != null) world.setGravity(reader.gravity);
		if (reader.bounds != null) world.setBounds(reader.bounds);
		if (reader.worldName != null) {
			world.setUserData(reader.worldName);
		} else {
			world.setUserData(Messages.getString("world.name.default"));
		}
		
		for (SandboxBody body : reader.bodies) {
			world.addBody(body);
		}
		for (Joint joint : reader.joints) {
			world.addJoint(joint);
		}
		
		// loadup the rays list
		for (SandboxRay ray : reader.rays) {
			rays.add(ray);
		}
		
		// set the camera
		if (reader.camera != null) {
			camera.setScale(reader.camera.getScale());
			camera.setTranslation(reader.camera.getTranslation());
		}
		
		return new Simulation(camera, rays, world);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// set the tag name
		this.tagName = qName;
		this.handled = true;
		// look for tags that have attributes
		if ("Simulation".equalsIgnoreCase(qName)) {
			// check the version
			this.version = attributes.getValue("version");
			if (this.version == null) {
				// this is the version in which we first needed
				// to check for version numbers
				this.version = "1.0.2";
			}
		} else if ("System".equalsIgnoreCase(qName)) {
			this.systemFlag = true;
		} else if ("Camera".equalsIgnoreCase(qName)) {
			this.camera = new Camera(32, new Vector2());
		} else if ("Gravity".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.gravity = new Vector2(x, y);
		} else if ("Settings".equalsIgnoreCase(qName)) {
			this.settingsFlag = true;
		} else if ("LocalCenter".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.localCenter = new Vector2(x, y);
		} else if ("Translation".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.translation = new Vector2(x, y);
		} else if ("Body".equalsIgnoreCase(qName)) {
			this.body = new SandboxBody();
			// save the old id for setting up joints
			this.idMap.put(attributes.getValue("Id"), this.body);
			// set the name
			this.body.setName(attributes.getValue("Name"));
		} else if ("Mass".equalsIgnoreCase(qName) && !this.massFlag) {
			this.massFlag = true;
		} else if ("Mass".equalsIgnoreCase(qName) && this.massFlag) {
			this.massMassFlag = true;
		} else if ("OutlineColor".equalsIgnoreCase(qName)) {
			float[] color = new float[] {
				Float.parseFloat(attributes.getValue("r")),
				Float.parseFloat(attributes.getValue("g")),
				Float.parseFloat(attributes.getValue("b")),
				1.0f
			};
			this.body.setOutlineColor(color);
		} else if ("FillColor".equalsIgnoreCase(qName)) {
			float[] color = new float[] {
				Float.parseFloat(attributes.getValue("r")),
				Float.parseFloat(attributes.getValue("g")),
				Float.parseFloat(attributes.getValue("b")),
				1.0f
			};
			this.body.setFillColor(color);
		} else if ("Fixture".equalsIgnoreCase(qName)) {
			this.fixtureName = attributes.getValue("Name");
		} else if ("Shape".equalsIgnoreCase(qName)) {
			this.shapeType = attributes.getValue("xsi:type");
			this.vertices.clear();
		} else if ("Vertex".equalsIgnoreCase(qName)) {
			this.vertices.add(new Vector2(
					Double.parseDouble(attributes.getValue("x")),
					Double.parseDouble(attributes.getValue("y"))));
		} else if ("Filter".equalsIgnoreCase(qName)) {
			String type = attributes.getValue("xsi:type");
			if ("CategoryFilter".equalsIgnoreCase(type)) {
				this.category = 0;
				this.mask = 0;
				this.filter = new CategoryFilter();
			} else if ("DefaultFilter".equalsIgnoreCase(type)) {
				// otherwise always use the default filter
				this.filter = null;
			} else {
				throw new SAXException(MessageFormat.format(Messages.getString("exception.persist.unknownFilterType"), type));
			}
		} else if ("PartOfGroups".equalsIgnoreCase(qName)) {
			this.partOfGroupsFlag = true;
		} else if ("CollideWithGroups".equalsIgnoreCase(qName)) {
			this.collideWithGroupsFlag = true;
		} else if ("All".equalsIgnoreCase(qName) || qName.startsWith("Group")) {
			if (this.partOfGroupsFlag) {
				this.category |= Integer.parseInt(attributes.getValue("Value"));
			} else if (this.collideWithGroupsFlag) {
				this.mask |= Integer.parseInt(attributes.getValue("Value"));
			}
		} else if ("Velocity".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.body.getLinearVelocity().set(x, y);
		} else if ("AccumulatedForce".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.body.applyForce(new Vector2(x, y));
		} else if ("Joint".equalsIgnoreCase(qName)) {
			this.jointName = attributes.getValue("Name");
			this.jointType = attributes.getValue("xsi:type");
		} else if ("Anchor1".equalsIgnoreCase(qName) || "BodyAnchor1".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.anchor1 = new Vector2(x, y);
		} else if ("Anchor2".equalsIgnoreCase(qName) || "BodyAnchor2".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.anchor2 = new Vector2(x, y);
		} else if ("Anchor".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.anchor = new Vector2(x, y);
		} else if ("Target".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.target = new Vector2(x, y);
		} else if ("Axis".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.axis = new Vector2(x, y);
		} else if ("PulleyAnchor1".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.pulleyAnchor1 = new Vector2(x, y);
		} else if ("PulleyAnchor2".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.pulleyAnchor2 = new Vector2(x, y);
		} else if ("Ray".equalsIgnoreCase(qName)) {
			this.ray = new SandboxRay(attributes.getValue("Name"), 0.0);
			this.rayFlag = true;
		} else if ("Start".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.rayStart = new Vector2(x, y);
		} else {
			this.handled = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String s = new String(ch, start, length).trim();
		if (s.isEmpty()) return;
		boolean handled = true;
		if ("Width".equalsIgnoreCase(this.tagName)) {
			this.width = Double.parseDouble(s);
		} else if ("Name".equalsIgnoreCase(this.tagName)) {
			this.worldName = s;
		} else if ("Scale".equalsIgnoreCase(this.tagName)) {
			this.camera.setScale(Double.parseDouble(s));
		} else if ("BroadphaseDetector".equalsIgnoreCase(this.tagName)) {
			if (s.equalsIgnoreCase(Sap.class.getSimpleName())) {
				this.broadphase = new Sap<Body, BodyFixture>();
			} else if (s.equalsIgnoreCase(DynamicAABBTree.class.getSimpleName())) { 
				this.broadphase = new DynamicAABBTree<Body, BodyFixture>();
			} else {
				throw new SAXException(MessageFormat.format(Messages.getString("exception.persist.unknownBroadphaseAlgorithm"), s));
			}
		} else if ("NarrowphaseDetector".equalsIgnoreCase(this.tagName)) {
			if (s.equalsIgnoreCase(Sat.class.getSimpleName())) {
				this.narrowphase = new Sat();
			} else if (s.equalsIgnoreCase(Gjk.class.getSimpleName())) {
				this.narrowphase = new Gjk();
			} else {
				throw new SAXException(MessageFormat.format(Messages.getString("exception.persist.unknownNarrowphaseAlgorithm"), s));
			}
		} else if ("ManifoldSolver".equalsIgnoreCase(this.tagName)) {
			if (s.equalsIgnoreCase(ClippingManifoldSolver.class.getSimpleName())) {
				this.manifoldSolver = new ClippingManifoldSolver();
			} else {
				throw new SAXException(MessageFormat.format(Messages.getString("exception.persist.unknownManifoldSolverAlgorithm"), s));
			}
		} else if ("TimeOfImpactDetector".equalsIgnoreCase(this.tagName)) {
			if (s.equalsIgnoreCase(ConservativeAdvancement.class.getSimpleName())) {
				this.timeOfImpact = new ConservativeAdvancement();
			} else {
				throw new SAXException(MessageFormat.format(Messages.getString("exception.persist.unknownTimeOfImpactAlgorithm"), s));
			}
		} else if ("Height".equalsIgnoreCase(this.tagName)) {
			this.height = Double.parseDouble(s);
		} else if ("LocalRotation".equalsIgnoreCase(this.tagName)) {
			this.localRotation = Double.parseDouble(s);
		} else if ("Rotation".equalsIgnoreCase(this.tagName)) {
			this.rotation = Double.parseDouble(s);
		} else if ("Radius".equalsIgnoreCase(this.tagName)) {
			this.radius = Double.parseDouble(s);
		} else if ("Sensor".equalsIgnoreCase(this.tagName)) {
			this.fixture.setSensor(Boolean.parseBoolean(s));
		} else if ("Density".equalsIgnoreCase(this.tagName)) {
			this.fixture.setDensity(Double.parseDouble(s));
		} else if ("Friction".equalsIgnoreCase(this.tagName)) {
			this.fixture.setFriction(Double.parseDouble(s));
		} else if ("Restitution".equalsIgnoreCase(this.tagName)) {
			this.fixture.setRestitution(Double.parseDouble(s));
		} else if ("Type".equalsIgnoreCase(this.tagName) && this.massFlag) {
			this.massType = s;
		} else if ("Mass".equalsIgnoreCase(this.tagName) && this.massFlag && this.massMassFlag) {
			this.massMass = Double.parseDouble(s);
		} else if ("Inertia".equalsIgnoreCase(this.tagName)) {
			this.massInertia = Double.parseDouble(s);
		} else if ("Explicit".equalsIgnoreCase(this.tagName)) {
			this.massExplicit = Boolean.parseBoolean(s);
		} else if ("AngularVelocity".equalsIgnoreCase(this.tagName)) {
			this.body.setAngularVelocity(Math.toRadians(Double.parseDouble(s)));
		} else if ("AccumulatedTorque".equalsIgnoreCase(this.tagName)) {
			this.body.applyTorque(Double.parseDouble(s));
		} else if ("AutoSleep".equalsIgnoreCase(this.tagName) && !this.settingsFlag) {
			this.body.setAutoSleepingEnabled(Boolean.parseBoolean(s));
		} else if ("Asleep".equalsIgnoreCase(this.tagName)) {
			this.body.setAsleep(Boolean.parseBoolean(s));
		} else if ("Active".equalsIgnoreCase(this.tagName)) {
			this.body.setActive(Boolean.parseBoolean(s));
		} else if ("Bullet".equalsIgnoreCase(this.tagName)) {
			this.body.setBullet(Boolean.parseBoolean(s));
		} else if ("LinearDamping".equalsIgnoreCase(this.tagName)) {
			this.body.setLinearDamping(Double.parseDouble(s));
		} else if ("AngularDamping".equalsIgnoreCase(this.tagName)) {
			this.body.setAngularDamping(Double.parseDouble(s));
		} else if ("GravityScale".equalsIgnoreCase(this.tagName)) {
			this.body.setGravityScale(Double.parseDouble(s));
		} else if ("BodyId1".equalsIgnoreCase(this.tagName)) {
			this.bodyId1 = s;
		} else if ("BodyId2".equalsIgnoreCase(this.tagName)) {
			this.bodyId2 = s;
		} else if ("CollisionAllowed".equalsIgnoreCase(this.tagName)) {
			this.collisionAllowed = Boolean.parseBoolean(s);
		} else if ("LowerLimit".equalsIgnoreCase(this.tagName)) {
			this.lowerLimit = Double.parseDouble(s);
		} else if ("UpperLimit".equalsIgnoreCase(this.tagName)) {
			this.upperLimit = Double.parseDouble(s);
		} else if ("LimitEnabled".equalsIgnoreCase(this.tagName)) {
			this.limitsEnabled = Boolean.parseBoolean(s);
		} else if ("ReferenceAngle".equalsIgnoreCase(this.tagName)) {
			this.referenceAngle = Double.parseDouble(s);
		} else if ("Frequency".equalsIgnoreCase(this.tagName)) {
			this.frequency = Double.parseDouble(s);
		} else if ("DampingRatio".equalsIgnoreCase(this.tagName)) {
			this.dampingRatio = Double.parseDouble(s);
		} else if ("Distance".equalsIgnoreCase(this.tagName)) {
			this.distance = Double.parseDouble(s);
		} else if ("MaximumForce".equalsIgnoreCase(this.tagName)) {
			this.maximumForce = Double.parseDouble(s);
		} else if ("MaximumTorque".equalsIgnoreCase(this.tagName)) {
			this.maximumTorque = Double.parseDouble(s);
		} else if ("MotorSpeed".equalsIgnoreCase(this.tagName)) {
			this.motorSpeed = Double.parseDouble(s);
		} else if ("MotorEnabled".equalsIgnoreCase(this.tagName)) {
			this.motorEnabled = Boolean.parseBoolean(s);
		} else if ("Ratio".equalsIgnoreCase(this.tagName)) {
			this.ratio = Double.parseDouble(s);
		} else if ("MaximumMotorTorque".equalsIgnoreCase(this.tagName)) {
			this.maximumMotorTorque = Double.parseDouble(s);
		} else if ("MaximumMotorForce".equalsIgnoreCase(this.tagName)) {
			this.maximumMotorForce = Double.parseDouble(s);
		} else if ("LowerLimitEnabled".equalsIgnoreCase(this.tagName)) {
			this.lowerLimitEnabled = Boolean.parseBoolean(s);
		} else if ("UpperLimitEnabled".equalsIgnoreCase(this.tagName)) {
			this.upperLimitEnabled = Boolean.parseBoolean(s);
		} else if ("StepFrequency".equalsIgnoreCase(this.tagName)) {
			this.settings.setStepFrequency(1.0 / Double.parseDouble(s));
		} else if ("MaximumTranslation".equalsIgnoreCase(this.tagName)) {
			this.settings.setMaximumTranslation(Double.parseDouble(s));
		} else if ("MaximumRotation".equalsIgnoreCase(this.tagName)) {
			this.settings.setMaximumRotation(Math.toRadians(Double.parseDouble(s)));
		} else if ("ContinuousCollisionDetectionMode".equalsIgnoreCase(this.tagName)) {
			if (Settings.ContinuousDetectionMode.ALL.toString().equalsIgnoreCase(s)) {
				this.settings.setContinuousDetectionMode(Settings.ContinuousDetectionMode.ALL);
			} else if (Settings.ContinuousDetectionMode.BULLETS_ONLY.toString().equalsIgnoreCase(s)) {
				this.settings.setContinuousDetectionMode(Settings.ContinuousDetectionMode.BULLETS_ONLY);
			} else if (Settings.ContinuousDetectionMode.NONE.toString().equalsIgnoreCase(s)) {
				this.settings.setContinuousDetectionMode(Settings.ContinuousDetectionMode.NONE);
			} else {
				throw new SAXException(MessageFormat.format(Messages.getString("exception.persist.unknownCCDMode"), s));
			}
		} else if ("AutoSleep".equalsIgnoreCase(this.tagName) && this.settingsFlag) {
			this.settings.setAutoSleepingEnabled(Boolean.parseBoolean(s));
		} else if ("SleepTime".equalsIgnoreCase(this.tagName)) {
			this.settings.setSleepTime(Double.parseDouble(s));
		} else if ("SleepLinearVelocity".equalsIgnoreCase(this.tagName)) {
			this.settings.setSleepLinearVelocity(Double.parseDouble(s));
		} else if ("SleepAngularVelocity".equalsIgnoreCase(this.tagName)) {
			this.settings.setSleepAngularVelocity(Math.toRadians(Double.parseDouble(s)));
		} else if ("VelocitySolverIterations".equalsIgnoreCase(this.tagName)) {
			this.settings.setVelocityConstraintSolverIterations(Integer.parseInt(s));
		} else if ("PositionSolverIterations".equalsIgnoreCase(this.tagName)) {
			 this.settings.setPositionConstraintSolverIterations(Integer.parseInt(s));
		} else if ("WarmStartDistance".equalsIgnoreCase(this.tagName)) {
			this.settings.setWarmStartDistance(Double.parseDouble(s));
		} else if ("RestitutionVelocity".equalsIgnoreCase(this.tagName)) {
			this.settings.setRestitutionVelocity(Double.parseDouble(s));
		} else if ("LinearTolerance".equalsIgnoreCase(this.tagName)) {
			this.settings.setLinearTolerance(Double.parseDouble(s));
		} else if ("AngularTolerance".equalsIgnoreCase(this.tagName)) {
			this.settings.setAngularTolerance(Math.toRadians(Double.parseDouble(s)));
		} else if ("MaximumLinearCorrection".equalsIgnoreCase(this.tagName)) {
			this.settings.setMaximumLinearCorrection(Double.parseDouble(s));
		} else if ("MaximumAngularCorrection".equalsIgnoreCase(this.tagName)) {
			this.settings.setMaximumAngularCorrection(Math.toRadians(Double.parseDouble(s)));
		} else if ("Baumgarte".equalsIgnoreCase(this.tagName)) {
			this.settings.setBaumgarte(Double.parseDouble(s));
		} else if ("Direction".equalsIgnoreCase(this.tagName) && this.rayFlag) {
			this.rayDirection = Double.parseDouble(s);
		} else if ("Length".equalsIgnoreCase(this.tagName) && this.rayFlag) {
			this.ray.setLength(Double.parseDouble(s));
		} else if ("IgnoreSensors".equalsIgnoreCase(this.tagName) && this.rayFlag) {
			this.ray.setIgnoreSensors(Boolean.parseBoolean(s));
		} else if ("TestAll".equalsIgnoreCase(this.tagName) && this.rayFlag) {
			this.ray.setAll(Boolean.parseBoolean(s));
		} else {
			handled = false;
		}
		
		if (!handled && !this.handled && !this.systemFlag) {
			// output unhandled tags excluding the system tags
			System.out.println(this.tagName);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		this.tagName = null;
		if ("System".equalsIgnoreCase(qName)) {
			this.systemFlag = false;
		} else if ("Camera".equalsIgnoreCase(qName)) {
			if (this.translation != null) {
				this.camera.setTranslation(this.translation);
			}
			this.translation = null;
		} else if ("Bounds".equalsIgnoreCase(qName)) {
			if (compareVersions(this.version, "1.0.4") < 0) {
				// versions before 1.0.4 used the RectangularBounds class
				// which is now deprecated
				// so we will convert what information the old xml format
				// had into the new AxisAlignedBounds class and ignore any
				// other information
				this.bounds = new AxisAlignedBounds(this.width, this.height);
				this.bounds.translate(this.translation);
			} else {
				// versions 1.0.4 and above should be using the new
				// AxisAlignedBounds class
				this.bounds = new AxisAlignedBounds(this.width, this.height);
				this.bounds.translate(this.translation);
			}
		} else if ("Settings".equalsIgnoreCase(qName)) {
			this.settingsFlag = false;
		} else if ("Transform".equalsIgnoreCase(qName)) {
			// check if we are parsing a body
			if (this.body != null) {
				// if so, then set the transform
				Transform transform = new Transform();
				transform.setRotation(Math.toRadians(this.rotation));
				transform.setTranslation(this.translation);
				this.body.setTransform(transform);
			}
		} else if ("Body".equalsIgnoreCase(qName)) {
			this.bodies.add(this.body);
			this.body = null;
		} else if ("Fixture".equalsIgnoreCase(qName)) {
			this.body.addFixture(this.fixture);
		} else if ("Shape".equalsIgnoreCase(qName)) {
			// figure out what to create
			if ("Circle".equalsIgnoreCase(this.shapeType)) {
				this.shape = Geometry.createCircle(this.radius);
				this.shape.translate(this.localCenter);
			} else if ("Rectangle".equalsIgnoreCase(this.shapeType)) {
				this.shape = Geometry.createRectangle(this.width, this.height);
				// we can perform normal rotation since the shape's center
				// is the origin
				this.shape.rotate(Math.toRadians(this.localRotation));
				this.shape.translate(this.localCenter);
			} else if ("Triangle".equalsIgnoreCase(this.shapeType)) {
				this.shape = Geometry.createTriangle(
						this.vertices.get(0),
						this.vertices.get(1),
						this.vertices.get(2));
				// no translation required because the vertices handle that
			} else if ("Polygon".equalsIgnoreCase(this.shapeType)) {
				Vector2[] verts = new Vector2[this.vertices.size()];
				this.vertices.toArray(verts);
				this.shape = Geometry.createPolygon(verts);
				// no translation required because the vertices handle that
			} else if ("Segment".equalsIgnoreCase(this.shapeType)) {
				this.shape = Geometry.createSegment(
						this.vertices.get(0),
						this.vertices.get(1));
				// no translation required because the vertices handle that
			} else {
				throw new SAXException(MessageFormat.format(Messages.getString("exception.persist.unknownShapeType"), this.shapeType));
			}
			
			// create the fixture
			this.fixture = new BodyFixture(this.shape);
			this.fixture.setUserData(this.fixtureName);
		} else if ("Filter".equalsIgnoreCase(qName)) {
			if (this.filter != null) {
				this.filter.setCategory(this.category);
				this.filter.setMask(this.mask);
				this.fixture.setFilter(this.filter);
			}
		} else if ("PartOfGroups".equalsIgnoreCase(qName)) {
			this.partOfGroupsFlag = false;
		} else if ("CollideWithGroups".equalsIgnoreCase(qName)) {
			this.collideWithGroupsFlag = false;
		} else if ("Mass".equalsIgnoreCase(qName) && !this.massMassFlag) {
			this.massFlag = false;
			Mass mass = new Mass(this.localCenter, this.massMass, this.massInertia);
			// set the type
			if (Mass.Type.NORMAL.toString().equalsIgnoreCase(this.massType)) {
				mass.setType(Mass.Type.NORMAL);
			} else if (Mass.Type.INFINITE.toString().equalsIgnoreCase(this.massType)) {
				mass.setType(Mass.Type.INFINITE);
			} else if (Mass.Type.FIXED_LINEAR_VELOCITY.toString().equalsIgnoreCase(this.massType)) {
				mass.setType(Mass.Type.FIXED_LINEAR_VELOCITY);
			} else if (Mass.Type.FIXED_ANGULAR_VELOCITY.toString().equalsIgnoreCase(this.massType)) {
				mass.setType(Mass.Type.FIXED_ANGULAR_VELOCITY);
			} else {
				throw new SAXException(MessageFormat.format(Messages.getString("exception.persist.unknownMassType"), this.massType));
			}
			this.body.update(mass);
			this.body.setMassExplicit(this.massExplicit);
			
			this.massExplicit = false;
			this.massInertia = 0.0;
			this.massMass = 0.0;
			this.massType = null;
		} else if ("Mass".equalsIgnoreCase(qName) && this.massFlag && this.massMassFlag) {
			this.massMassFlag = false;
		} else if ("Joint".equalsIgnoreCase(qName)) {
			Joint joint = null;
			// create the joint given the type
			if ("AngleJoint".equalsIgnoreCase(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				AngleJoint aj = new AngleJoint(b1, b2);
				aj.setLimits(Math.toRadians(this.lowerLimit), Math.toRadians(this.upperLimit));
				aj.setLimitEnabled(this.limitsEnabled);
				aj.setRatio(this.ratio);
				aj.setReferenceAngle(Math.toRadians(this.referenceAngle));
				joint = aj;
			} else if ("DistanceJoint".equalsIgnoreCase(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				DistanceJoint dj = new DistanceJoint(b1, b2, this.anchor1, this.anchor2);
				dj.setFrequency(this.frequency);
				dj.setDampingRatio(this.dampingRatio);
				// we need to set the target distance because the joint may have been saved
				// in a state in which it was compressed or stretched
				dj.setDistance(this.distance);
				joint = dj;
			} else if ("FrictionJoint".equalsIgnoreCase(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				FrictionJoint fj = new FrictionJoint(b1, b2, this.anchor);
				fj.setMaximumForce(this.maximumForce);
				fj.setMaximumTorque(this.maximumTorque);
				joint = fj;
			} else if ("MouseJoint".equalsIgnoreCase(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				MouseJoint mj = new MouseJoint(b1, this.anchor, this.frequency, this.dampingRatio, this.maximumForce);
				mj.setTarget(this.target);
				joint = mj;
			} else if ("PrismaticJoint".equalsIgnoreCase(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				PrismaticJoint pj = new PrismaticJoint(b1, b2, this.anchor, this.axis);
				pj.setLimits(this.lowerLimit, this.upperLimit);
				pj.setLimitEnabled(this.limitsEnabled);
				pj.setMaximumMotorForce(this.maximumMotorForce);
				pj.setMotorSpeed(this.motorSpeed);
				pj.setMotorEnabled(this.motorEnabled);
				pj.setReferenceAngle(Math.toRadians(this.referenceAngle));
				joint = pj;
			} else if ("PulleyJoint".equals(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				PulleyJoint pj = new PulleyJoint(b1, b2, this.pulleyAnchor1, this.pulleyAnchor2, this.anchor1, this.anchor2);
				pj.setRatio(this.ratio);
				joint = pj;
			} else if ("RevoluteJoint".equalsIgnoreCase(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				RevoluteJoint rj = new RevoluteJoint(b1, b2, this.anchor);
				rj.setLimits(Math.toRadians(this.lowerLimit), Math.toRadians(this.upperLimit));
				rj.setLimitEnabled(this.limitsEnabled);
				rj.setMaximumMotorTorque(this.maximumMotorTorque);
				rj.setMotorEnabled(this.motorEnabled);
				rj.setMotorSpeed(Math.toRadians(this.motorSpeed));
				rj.setReferenceAngle(Math.toRadians(this.referenceAngle));
				joint = rj;
			} else if ("RopeJoint".equalsIgnoreCase(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				RopeJoint rj = new RopeJoint(b1, b2, this.anchor1, this.anchor2);
				rj.setLimits(this.lowerLimit, this.upperLimit);
				rj.setLowerLimitEnabled(this.lowerLimitEnabled);
				rj.setUpperLimitEnabled(this.upperLimitEnabled);
				joint = rj;
			} else if ("WeldJoint".equalsIgnoreCase(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				WeldJoint wj = new WeldJoint(b1, b2, this.anchor);
				wj.setReferenceAngle(Math.toRadians(this.referenceAngle));
				wj.setFrequency(this.frequency);
				wj.setDampingRatio(this.dampingRatio);
				joint = wj;
			} else if ("WheelJoint".equalsIgnoreCase(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				WheelJoint wj = new WheelJoint(b1, b2, this.anchor, this.axis);
				wj.setFrequency(this.frequency);
				wj.setDampingRatio(this.dampingRatio);
				wj.setMaximumMotorTorque(this.maximumMotorTorque);
				wj.setMotorSpeed(Math.toRadians(this.motorSpeed));
				wj.setMotorEnabled(this.motorEnabled);
				joint = wj;
			} else {
				throw new SAXException(MessageFormat.format(Messages.getString("exception.persist.unknownJointType"), this.jointType));
			}
			
			if (joint != null) {
				joint.setUserData(this.jointName);
				joint.setCollisionAllowed(this.collisionAllowed);
				this.joints.add(joint);
			}
			
			this.jointName = null;
			this.jointType = null;
		} else if ("Ray".equalsIgnoreCase(qName)) {
			SandboxRay ray = new SandboxRay(this.ray.getName(), this.rayStart, this.rayDirection);
			ray.setLength(this.ray.getLength());
			ray.setAll(this.ray.isAll());
			ray.setIgnoreSensors(this.ray.isIgnoreSensors());
			this.rays.add(ray);
			this.ray = null;
			this.rayFlag = false;
		}
	}
	
	/**
	 * Compares the given version numbers.
	 * @param v1 the first version number
	 * @param v2 the second version number
	 * @return 0 if they are equal; -1 if v1 < v2; 1 if v1 > v2
	 * @throws SAXException if either version number is invalid
	 */
	private int compareVersions(String v1, String v2) throws SAXException {
		String[] p1 = v1.split("\\.");
		String[] p2 = v2.split("\\.");
		try {
			int n = p1.length;
			for (int i = 0; i < n; i++) {
				int n1 = Integer.parseInt(p1[i]);
				int n2 = Integer.parseInt(p2[i]);
				if (n1 < n2) {
					return -1;
				} else if (n1 > n2) {
					return 1;
				}
				// if they are equal, continue to the
				// next version number
			}
		} catch (NumberFormatException e) {
			// this indicates the version number is botched up in
			// the xml document
			throw new SAXException(MessageFormat.format(Messages.getString("exception.persist.invalidVersionNumber"), v1, v2), e);
		}
		return 0;
	}
}
