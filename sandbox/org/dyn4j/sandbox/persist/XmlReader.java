package org.dyn4j.sandbox.persist;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.RectangularBounds;
import org.dyn4j.dynamics.BodyFixture;
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
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class used to read in a saved simulation file.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class XmlReader extends DefaultHandler {
	/** A list of tags that are expected to be skipped */
	private static final String SKIP_TAGS = "World,Bounds,Rectangle,Bodies,Joints,Fixtures";
	
	// Flags; true if the tag is currently active; false otherwise
	
	// rectangle
	
	/** Flag for the Width tag */
	private boolean widthFlag;
	
	/** Flag for the Height tag */
	private boolean heightFlag;
	
	/** Flag for the LocalRotation tag */
	private boolean localRotationFlag;
	
	// transform
	
	/** Flag for the Transform tag */
	private boolean transformFlag;
	
	/** Flag for the Rotation tag */
	private boolean rotationFlag;
	
	// body
	
	/** Flag for the AngularVelocity tag */
	private boolean angularVelocityFlag;
	
	/** Flag for the AccumulatedTorque tag */
	private boolean accumulatedTorqueFlag;
	
	/** Flag for the AutoSleep tag */
	private boolean autoSleepFlag;
	
	/** Flag for the Asleep tag */
	private boolean asleepFlag;
	
	/** Flag for the Active tag */
	private boolean activeFlag;
	
	/** Flag for the Bullet tag */
	private boolean bulletFlag;
	
	/** Flag for the LinearDamping tag */
	private boolean linearDampingFlag;
	
	/** Flag for the AngularDamping tag */
	private boolean angularDampingFlag;
	
	/** Flag for the GravityScale tag */
	private boolean gravityScaleFlag;
	
	/** Flag for the Sensor tag */
	private boolean sensorFlag;
	
	/** Flag for the Density tag */
	private boolean densityFlag;
	
	/** Flag for the Friction tag */
	private boolean frictionFlag;
	
	/** Flag for the Restitution tag */
	private boolean restitutionFlag;
	
	// shape
	
	/** Flag for the Radius tag */
	private boolean radiusFlag;
	
	// filter
	
	/** Flag for the PartOfGroups tag */
	private boolean partOfGroupsFlag;
	
	/** Flag for the CollideWithGroups tag */
	private boolean collideWithGroupsFlag;
	
	// mass
	
	/** Flag for the Mass tag */
	private boolean massFlag;
	
	/** Flag for the Type tag under the Mass tag */
	private boolean massTypeFlag;
	
	/** Flag for the Mass tag under the Mass tag */
	private boolean massMassFlag;
	
	/** Flag for the Inertia tag */
	private boolean massInertiaFlag;
	
	/** Flag for the Explicit tag */
	private boolean massExplicitFlag;
	
	// joints
	
	/** Flag for the BodyId1 tag */
	private boolean bodyId1Start;
	
	/** Flag for the BodyId2 tag */
	private boolean bodyId2Start;
	
	/** Flag for the CollisionAllowed tag */
	private boolean collisionAllowedStart;
	
	/** Flag for the UpperLimit tag */
	private boolean upperLimitStart;
	
	/** Flag for the LowerLimit tag */
	private boolean lowerLimitStart;
	
	/** Flag for the LimitEnabled tag */
	private boolean limitEnabledStart;
	
	/** Flag for the ReferenceAngle tag */
	private boolean referenceAngleStart;
	
	/** Flag for the Frequency tag */
	private boolean frequencyStart;
	
	/** Flag for the DampingRatio tag */
	private boolean dampingRatioStart;
	
	/** Flag for the Distance tag */
	private boolean distanceStart;
	
	/** Flag for the MaximumForce tag */
	private boolean maximumForceStart;
	
	/** Flag for the MaximumTorque tag */
	private boolean maximumTorqueStart;
	
	/** Flag for the MotorSpeed tag */
	private boolean motorSpeedStart;
	
	/** Flag for the MaximumMotorForce tag */
	private boolean maximumMotorForceStart;
	
	/** Flag for the MotorEnabled tag */
	private boolean motorEnabledStart;
	
	/** Flag for the Ratio tag */
	private boolean ratioStart;
	
	/** Flag for the MaximumMotorTorque tag */
	private boolean maximumMotorTorqueStart;
	
	/** Flag for the LowerLimitEnabled tag */
	private boolean lowerLimitEnabledStart;
	
	/** Flag for the UpperLimitEnabled tag */
	private boolean upperLimitEnabledStart;
	
	
	// Data; storage for the final results and extra information
	
	/** The bounds object */
	private Bounds bounds;
	
	/** The list of bodies */
	private List<SandboxBody> bodies;
	
	/** The list of joints */
	private List<Joint> joints;
	
	/** A mapping of bodies to their original ids contained in the XML file */
	private Map<String, SandboxBody> idMap;
	
	// Tag Data; for temporary storage
	
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
	
	/** Storage for the AngularVelocity tag */
	private double angularVelocity;
	
	/** Storage for the Accumulated Torque tag */
	private double accumulatedTorque;
	
	/** Storage for the AutoSleep tag */
	private boolean autoSleep;
	
	/** Storage for the Asleep tag */
	private boolean asleep;
	
	/** Storage for the Active tag */
	private boolean active;
	
	/** Storage for the Bullet tag */
	private boolean bullet;
	
	/** Storage for the LinearDamping tag */
	private double linearDamping;
	
	/** Storage for the AngularDamping tag */
	private double angularDamping;
	
	/** Storage for the GravityScale tag */
	private double gravityScale;

	// fixture
	
	/** Storage for the fixture */
	private BodyFixture fixture;
	
	/** Storage for the Name attribute on the Fixture tag */
	private String fixtureName;
	
	/** Storage for the Sensor tag */
	private boolean sensor;
	
	/** Storage for the Density tag */
	private double density;
	
	/** Storage for the Friction tag */
	private double friction;
	
	/** Storage for the Restitution tag */
	private double restitution;
	
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
	
	/**
	 * Hidden constructor.
	 * @see #fromXml(File, World)
	 */
	private XmlReader() {
		this.bodies = new ArrayList<SandboxBody>();
		this.joints = new ArrayList<Joint>();
		this.idMap = new HashMap<String, SandboxBody>();
		this.vertices = new ArrayList<Vector2>();
	}
	
	/**
	 * Parses the given file and loads the bounds, bodies, and joints into the given world object.
	 * <p>
	 * The world object is cleared before loading.
	 * @param file the file to read from
	 * @param world the world object to modify
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	public static void fromXml(File file, World world) throws ParserConfigurationException, SAXException, IOException {
		XmlReader.fromXml(new InputSource(new FileReader(file)), world);
	}
	
	/**
	 * Parses the given string and loads the bounds, bodies, and joints into the given world object.
	 * <p>
	 * The world object is cleared before loading.
	 * @param xml the string containing the XML to read from
	 * @param world the world object to modify
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	public static void fromXml(String xml, World world) throws ParserConfigurationException, SAXException, IOException {
		XmlReader.fromXml(new InputSource(new StringReader(xml)), world);
	}
	
	/**
	 * Parses the given input source and loads the bounds, bodies, and joints into the given world object.
	 * <p>
	 * The world object is cleared before loading.
	 * @param source the source containing the XML
	 * @param world the world object to modify
	 * @throws ParserConfigurationException thrown if a SAX configuration error occurs
	 * @throws SAXException thrown if a parsing error occurs
	 * @throws IOException thrown if an IO error occurs
	 */
	private static void fromXml(InputSource source, World world) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		
		XmlReader reader = new XmlReader();
		
		parser.parse(source, reader);
		
		world.removeAll();
		world.setBounds(reader.bounds);
		for (SandboxBody body : reader.bodies) {
			world.add(body);
		}
		for (Joint joint : reader.joints) {
			world.add(joint);
		}	
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("LocalCenter".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.localCenter = new Vector2(x, y);
		} else if ("Width".equalsIgnoreCase(qName)) {
			this.widthFlag = true;
		} else if ("Height".equalsIgnoreCase(qName)) {
			this.heightFlag = true;
		} else if ("LocalRotation".equalsIgnoreCase(qName)) {
			this.localRotationFlag = true;
		} else if ("Transform".equalsIgnoreCase(qName)) {
			this.transformFlag = true;
		} else if ("Translation".equalsIgnoreCase(qName) && this.transformFlag) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.translation = new Vector2(x, y);
		} else if ("Rotation".equalsIgnoreCase(qName) && this.transformFlag) {
			this.rotationFlag = true;
		} else if ("Body".equalsIgnoreCase(qName)) {
			this.body = new SandboxBody();
			// save the old id for setting up joints
			this.idMap.put(attributes.getValue("Id"), this.body);
			// set the name
			this.body.setName(attributes.getValue("Name"));
		} else if ("Fixture".equalsIgnoreCase(qName)) {
			this.fixtureName = attributes.getValue("Name");
		} else if ("Shape".equalsIgnoreCase(qName)) {
			this.shapeType = attributes.getValue("xsi:type");
			this.vertices.clear();
		} else if ("Radius".equalsIgnoreCase(qName)) {
			this.radiusFlag = true;
		} else if ("Vertex".equalsIgnoreCase(qName)) {
			this.vertices.add(new Vector2(
					Double.parseDouble(attributes.getValue("x")),
					Double.parseDouble(attributes.getValue("y"))));
		} else if ("Filter".equalsIgnoreCase(qName)) {
			String type = attributes.getValue("xsi:type");
			if ("CategoryFilter".equals(type)) {
				this.category = 0;
				this.mask = 0;
				this.filter = new CategoryFilter();
			} else if ("DefaultFilter".equals(type)) {
				// otherwise always use the default filter
				this.filter = null;
			} else {
				throw new SAXException("Filter type \"" + type + "\" unknown or not implemented.");
			}
		} else if ("PartOfGroups".equalsIgnoreCase(qName)) {
			this.partOfGroupsFlag = true;
		} else if ("CollideWithGroups".equalsIgnoreCase(qName)) {
			this.collideWithGroupsFlag = true;
		} else if ("All".equals(qName) || qName.startsWith("Group")) {
			if (this.partOfGroupsFlag) {
				this.category |= Integer.parseInt(attributes.getValue("Value"));
			} else if (this.collideWithGroupsFlag) {
				this.mask |= Integer.parseInt(attributes.getValue("Value"));
			}
		} else if ("Sensor".equalsIgnoreCase(qName)) {
			this.sensorFlag = true;
		} else if ("Density".equalsIgnoreCase(qName)) {
			this.densityFlag = true;
		} else if ("Friction".equalsIgnoreCase(qName)) {
			this.frictionFlag = true;
		} else if ("Restitution".equalsIgnoreCase(qName)) {
			this.restitutionFlag = true;
		} else if ("Mass".equalsIgnoreCase(qName) && !this.massFlag) {
			this.massFlag = true;
		} else if ("Type".equalsIgnoreCase(qName) && this.massFlag) {
			this.massTypeFlag = true;
		} else if ("Mass".equalsIgnoreCase(qName) && this.massFlag) {
			this.massMassFlag = true;
		} else if ("Inertia".equalsIgnoreCase(qName) && this.massFlag) {
			this.massInertiaFlag = true;
		} else if ("Explicit".equalsIgnoreCase(qName) && this.massFlag) {
			this.massExplicitFlag = true;
		} else if ("Velocity".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.body.getVelocity().set(x, y);
		} else if ("AngularVelocity".equalsIgnoreCase(qName)) {
			this.angularVelocityFlag = true;
		} else if ("AccumulatedForce".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.body.apply(new Vector2(x, y));
		} else if ("AccumulatedTorque".equalsIgnoreCase(qName)) {
			this.accumulatedTorqueFlag = true;
		} else if ("AutoSleep".equalsIgnoreCase(qName)) {
			this.autoSleepFlag = true;
		} else if ("Asleep".equalsIgnoreCase(qName)) {
			this.asleepFlag = true;
		} else if ("Active".equalsIgnoreCase(qName)) {
			this.activeFlag = true;
		} else if ("Bullet".equalsIgnoreCase(qName)) {
			this.bulletFlag = true;
		} else if ("LinearDamping".equalsIgnoreCase(qName)) {
			this.linearDampingFlag = true;
		} else if ("AngularDamping".equalsIgnoreCase(qName)) {
			this.angularDampingFlag = true;
		} else if ("GravityScale".equalsIgnoreCase(qName)) {
			this.gravityScaleFlag = true;
		} else if ("Joint".equalsIgnoreCase(qName)) {
			this.jointName = attributes.getValue("Name");
			this.jointType = attributes.getValue("xsi:type");
		} else if ("BodyId1".equalsIgnoreCase(qName)) {
			this.bodyId1Start = true;
		} else if ("BodyId2".equalsIgnoreCase(qName)) {
			this.bodyId2Start = true;
		} else if ("CollisionAllowed".equalsIgnoreCase(qName)) {
			this.collisionAllowedStart = true;
		} else if ("LowerLimit".equalsIgnoreCase(qName)) {
			this.lowerLimitStart = true;
		} else if ("UpperLimit".equalsIgnoreCase(qName)) {
			this.upperLimitStart = true;
		} else if ("LimitEnabled".equalsIgnoreCase(qName)) {
			this.limitEnabledStart = true;
		} else if ("ReferenceAngle".equalsIgnoreCase(qName)) {
			this.referenceAngleStart = true;
		} else if ("Anchor1".equalsIgnoreCase(qName) || "BodyAnchor1".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.anchor1 = new Vector2(x, y);
		} else if ("Anchor2".equalsIgnoreCase(qName) || "BodyAnchor2".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.anchor2 = new Vector2(x, y);
		} else if ("Frequency".equalsIgnoreCase(qName)) {
			this.frequencyStart = true;
		} else if ("DampingRatio".equalsIgnoreCase(qName)) {
			this.dampingRatioStart = true;
		} else if ("Distance".equalsIgnoreCase(qName)) {
			this.distanceStart = true;
		} else if ("Anchor".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.anchor = new Vector2(x, y);
		} else if ("MaximumForce".equalsIgnoreCase(qName)) {
			this.maximumForceStart = true;
		} else if ("MaximumTorque".equalsIgnoreCase(qName)) {
			this.maximumTorqueStart = true;
		} else if ("Target".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.target = new Vector2(x, y);
		} else if ("Axis".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.axis = new Vector2(x, y);
		} else if ("MotorSpeed".equalsIgnoreCase(qName)) {
			this.motorSpeedStart = true;
		} else if ("MaximumMotorForce".equalsIgnoreCase(qName)) {
			this.maximumMotorForceStart = true;
		} else if ("MotorEnabled".equalsIgnoreCase(qName)) {
			this.motorEnabledStart = true;
		} else if ("PulleyAnchor1".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.pulleyAnchor1 = new Vector2(x, y);
		} else if ("PulleyAnchor2".equalsIgnoreCase(qName)) {
			double x = Double.parseDouble(attributes.getValue("x"));
			double y = Double.parseDouble(attributes.getValue("y"));
			this.pulleyAnchor2 = new Vector2(x, y);
		} else if ("Ratio".equalsIgnoreCase(qName)) {
			this.ratioStart = true;
		} else if ("MaximumMotorTorque".equalsIgnoreCase(qName)) {
			this.maximumMotorTorqueStart = true;
		} else if ("LowerLimitEnabled".equalsIgnoreCase(qName)) {
			this.lowerLimitEnabledStart = true;
		} else if ("UpperLimitEnabled".equalsIgnoreCase(qName)) {
			this.upperLimitEnabledStart = true;
		} else {
			if (!SKIP_TAGS.contains(qName)) {
				System.out.println("Tag \"" + qName + "\" skipped.");
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String s = new String(ch, start, length);
		if (this.widthFlag) {
			this.width = Double.parseDouble(s);
		} else if (this.heightFlag) {
			this.height = Double.parseDouble(s);
		} else if (this.localRotationFlag) {
			this.localRotation = Double.parseDouble(s);
		} else if (this.rotationFlag) {
			this.rotation = Double.parseDouble(s);
		} else if (this.radiusFlag) {
			this.radius = Double.parseDouble(s);
		} else if (this.sensorFlag) {
			this.sensor = Boolean.parseBoolean(s);
		} else if (this.densityFlag) {
			this.density = Double.parseDouble(s);
		} else if (this.frictionFlag) {
			this.friction = Double.parseDouble(s);
		} else if (this.restitutionFlag) {
			this.restitution = Double.parseDouble(s);
		} else if (this.massTypeFlag) {
			this.massType = s;
		} else if (this.massMassFlag) {
			this.massMass = Double.parseDouble(s);
		} else if (this.massInertiaFlag) {
			this.massInertia = Double.parseDouble(s);
		} else if (this.massExplicitFlag) {
			this.massExplicit = Boolean.parseBoolean(s);
		} else if (this.angularVelocityFlag) {
			this.angularVelocity = Double.parseDouble(s);
		} else if (this.accumulatedTorqueFlag) {
			this.accumulatedTorque = Double.parseDouble(s);
		} else if (this.autoSleepFlag) {
			this.autoSleep = Boolean.parseBoolean(s);
		} else if (this.asleepFlag) {
			this.asleep = Boolean.parseBoolean(s);
		} else if (this.activeFlag) {
			this.active = Boolean.parseBoolean(s);
		} else if (this.bulletFlag) {
			this.bullet = Boolean.parseBoolean(s);
		} else if (this.linearDampingFlag) {
			this.linearDamping = Double.parseDouble(s);
		} else if (this.angularDampingFlag) {
			this.angularDamping = Double.parseDouble(s);
		} else if (this.gravityScaleFlag) {
			this.gravityScale = Double.parseDouble(s);
		} else if (this.bodyId1Start) {
			this.bodyId1 = s;
		} else if (this.bodyId2Start) {
			this.bodyId2 = s;
		} else if (this.collisionAllowedStart) {
			this.collisionAllowed = Boolean.parseBoolean(s);
		} else if (this.lowerLimitStart) {
			this.lowerLimit = Double.parseDouble(s);
		} else if (this.upperLimitStart) {
			this.upperLimit = Double.parseDouble(s);
		} else if (this.limitEnabledStart) {
			this.limitsEnabled = Boolean.parseBoolean(s);
		} else if (this.referenceAngleStart) {
			this.referenceAngle = Double.parseDouble(s);
		} else if (this.frequencyStart) {
			this.frequency = Double.parseDouble(s);
		} else if (this.dampingRatioStart) {
			this.dampingRatio = Double.parseDouble(s);
		} else if (this.distanceStart) {
			this.distance = Double.parseDouble(s);
		} else if (this.maximumForceStart) {
			this.maximumForce = Double.parseDouble(s);
		} else if (this.maximumTorqueStart) {
			this.maximumTorque = Double.parseDouble(s);
		} else if (this.motorSpeedStart) {
			this.motorSpeed = Double.parseDouble(s);
		} else if (this.motorEnabledStart) {
			this.motorEnabled = Boolean.parseBoolean(s);
		} else if (this.ratioStart) {
			this.ratio = Double.parseDouble(s);
		} else if (this.maximumMotorTorqueStart) {
			this.maximumMotorTorque = Double.parseDouble(s);
		} else if (this.maximumMotorForceStart) {
			this.maximumMotorForce = Double.parseDouble(s);
		} else if (this.lowerLimitEnabledStart) {
			this.lowerLimitEnabled = Boolean.parseBoolean(s);
		} else if (this.upperLimitEnabledStart) {
			this.upperLimitEnabled = Boolean.parseBoolean(s);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("Bounds".equalsIgnoreCase(qName)) {
			Rectangle r = new Rectangle(this.width, this.height);
			r.rotate(this.localRotation);
			r.translate(this.localCenter);
			this.bounds = new RectangularBounds(r);
			this.bounds.rotate(this.rotation);
			this.bounds.translate(this.translation);
		} else if ("Width".equalsIgnoreCase(qName)) {
			this.widthFlag = false;
		} else if ("Height".equalsIgnoreCase(qName)) {
			this.heightFlag = false;
		} else if ("LocalRotation".equalsIgnoreCase(qName)) {
			this.localRotationFlag = false;
		} else if ("Transform".equalsIgnoreCase(qName)) {
			this.transformFlag = false;
			// check if we are parsing a body
			if (this.body != null) {
				// if so, then set the transform
				Transform transform = new Transform();
				transform.rotate(this.rotation);
				transform.translate(this.translation);
				this.body.setTransform(transform);
			}
		} else if ("Rotation".equalsIgnoreCase(qName) && this.transformFlag) {
			this.rotationFlag = false;
		} else if ("Body".equalsIgnoreCase(qName)) {
			this.bodies.add(this.body);
			this.body = null;
		} else if ("Fixture".equalsIgnoreCase(qName)) {
			this.body.addFixture(this.fixture);
		} else if ("Shape".equalsIgnoreCase(qName)) {
			// figure out what to create
			if ("Circle".equals(this.shapeType)) {
				this.shape = Geometry.createCircle(this.radius);
				this.shape.translate(this.localCenter);
			} else if ("Rectangle".equals(this.shapeType)) {
				this.shape = Geometry.createRectangle(this.width, this.height);
				this.shape.rotate(this.localRotation);
				this.shape.translate(this.localCenter);
			} else if ("Triangle".equals(this.shapeType)) {
				this.shape = Geometry.createTriangle(
						this.vertices.get(0),
						this.vertices.get(1),
						this.vertices.get(2));
				// no translation required because the vertices handle that
			} else if ("Polygon".equals(this.shapeType)) {
				Vector2[] verts = new Vector2[this.vertices.size()];
				this.vertices.toArray(verts);
				this.shape = Geometry.createPolygon(verts);
				// no translation required because the vertices handle that
			} else if ("Segment".equals(this.shapeType)) {
				this.shape = Geometry.createSegment(
						this.vertices.get(0),
						this.vertices.get(1));
				// no translation required because the vertices handle that
			} else {
				throw new SAXException("Shape type \"" + this.shapeType + "\" unknown or not implemented.");
			}
			
			// create the fixture
			this.fixture = new BodyFixture(this.shape);
			this.fixture.setUserData(this.fixtureName);
		} else if ("Radius".equalsIgnoreCase(qName)) {
			this.radiusFlag = false;
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
		} else if ("Sensor".equalsIgnoreCase(qName)) {
			this.sensorFlag = false;
			this.fixture.setSensor(this.sensor);
		} else if ("Density".equalsIgnoreCase(qName)) {
			this.densityFlag = false;
			this.fixture.setDensity(this.density);
		} else if ("Friction".equalsIgnoreCase(qName)) {
			this.frictionFlag = false;
			this.fixture.setFriction(this.friction);
		} else if ("Restitution".equalsIgnoreCase(qName)) {
			this.restitutionFlag = false;
			this.fixture.setRestitution(this.restitution);
		} else if ("Mass".equalsIgnoreCase(qName) && !this.massMassFlag) {
			this.massFlag = false;
			Mass mass = new Mass(this.localCenter, this.massMass, this.massInertia);
			// set the type
			if (Mass.Type.NORMAL.toString().equals(this.massType)) {
				mass.setType(Mass.Type.NORMAL);
			} else if (Mass.Type.INFINITE.toString().equals(this.massType)) {
				mass.setType(Mass.Type.INFINITE);
			} else if (Mass.Type.FIXED_LINEAR_VELOCITY.toString().equals(this.massType)) {
				mass.setType(Mass.Type.FIXED_LINEAR_VELOCITY);
			} else if (Mass.Type.FIXED_ANGULAR_VELOCITY.toString().equals(this.massType)) {
				mass.setType(Mass.Type.FIXED_ANGULAR_VELOCITY);
			} else {
				throw new SAXException("Mass type \"" + this.massType + "\" unknown or not implemented.");
			}
			this.body.setMass(mass);
			this.body.setMassExplicit(this.massExplicit);
			
			this.massExplicit = false;
			this.massInertia = 0.0;
			this.massMass = 0.0;
			this.massType = null;
		} else if ("Type".equalsIgnoreCase(qName) && this.massFlag) {
			this.massTypeFlag = false;
		} else if ("Mass".equalsIgnoreCase(qName) && this.massMassFlag) {
			this.massMassFlag = false;
		} else if ("Inertia".equalsIgnoreCase(qName) && this.massFlag) {
			this.massInertiaFlag = false;
		} else if ("Explicit".equalsIgnoreCase(qName) && this.massFlag) {
			this.massExplicitFlag = false;
		} else if ("AngularVelocity".equalsIgnoreCase(qName)) {
			this.angularVelocityFlag = false;
			this.body.setAngularVelocity(this.angularVelocity);
		} else if ("AccumulatedTorque".equalsIgnoreCase(qName)) {
			this.accumulatedTorqueFlag = false;
			this.body.apply(this.accumulatedTorque);
		} else if ("AutoSleep".equalsIgnoreCase(qName)) {
			this.autoSleepFlag = false;
			this.body.setAutoSleepingEnabled(this.autoSleep);
		} else if ("Asleep".equalsIgnoreCase(qName)) {
			this.asleepFlag = false;
			this.body.setAsleep(this.asleep);
		} else if ("Active".equalsIgnoreCase(qName)) {
			this.activeFlag = false;
			this.body.setActive(this.active);
		} else if ("Bullet".equalsIgnoreCase(qName)) {
			this.bulletFlag = false;
			this.body.setBullet(this.bullet);
		} else if ("LinearDamping".equalsIgnoreCase(qName)) {
			this.linearDampingFlag = false;
			this.body.setLinearDamping(this.linearDamping);
		} else if ("AngularDamping".equalsIgnoreCase(qName)) {
			this.angularDampingFlag = false;
			this.body.setAngularDamping(this.angularDamping);
		} else if ("GravityScale".equalsIgnoreCase(qName)) {
			this.gravityScaleFlag = false;
			this.body.setGravityScale(this.gravityScale);
		} else if ("Joint".equalsIgnoreCase(qName)) {
			Joint joint = null;
			// create the joint given the type
			if ("AngleJoint".equals(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				AngleJoint aj = new AngleJoint(b1, b2);
				aj.setLimits(this.lowerLimit, this.upperLimit);
				aj.setLimitEnabled(this.limitsEnabled);
				aj.setReferenceAngle(this.referenceAngle);
				joint = aj;
			} else if ("DistanceJoint".equals(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				DistanceJoint dj = new DistanceJoint(b1, b2, this.anchor1, this.anchor2);
				dj.setFrequency(this.frequency);
				dj.setDampingRatio(this.dampingRatio);
				// we need to set the target distance because the joint may have been saved
				// in a state in which it was compressed or stretched
				dj.setDistance(this.distance);
				joint = dj;
			} else if ("FrictionJoint".equals(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				FrictionJoint fj = new FrictionJoint(b1, b2, this.anchor);
				fj.setMaximumForce(this.maximumForce);
				fj.setMaximumTorque(this.maximumTorque);
				joint = fj;
			} else if ("MouseJoint".equals(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				MouseJoint mj = new MouseJoint(b1, this.anchor, this.frequency, this.dampingRatio, this.maximumForce);
				mj.setTarget(this.target);
				joint = mj;
			} else if ("PrismaticJoint".equals(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				PrismaticJoint pj = new PrismaticJoint(b1, b2, this.anchor, this.axis);
				pj.setLimits(this.lowerLimit, this.upperLimit);
				pj.setLimitEnabled(this.limitsEnabled);
				pj.setMaximumMotorForce(this.maximumMotorForce);
				pj.setMotorSpeed(this.motorSpeed);
				pj.setMotorEnabled(this.motorEnabled);
				pj.setReferenceAngle(this.referenceAngle);
				joint = pj;
			} else if ("PulleyJoint".equals(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				PulleyJoint pj = new PulleyJoint(b1, b2, this.pulleyAnchor1, this.pulleyAnchor2, this.anchor1, this.anchor2);
				pj.setRatio(this.ratio);
				joint = pj;
			} else if ("RevoluteJoint".equals(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				RevoluteJoint rj = new RevoluteJoint(b1, b2, this.anchor);
				rj.setLimits(this.lowerLimit, this.upperLimit);
				rj.setLimitEnabled(this.limitsEnabled);
				rj.setMaximumMotorTorque(this.maximumMotorTorque);
				rj.setMotorEnabled(this.motorEnabled);
				rj.setMotorSpeed(this.motorSpeed);
				rj.setReferenceAngle(this.referenceAngle);
				joint = rj;
			} else if ("RopeJoint".equals(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				RopeJoint rj = new RopeJoint(b1, b2, this.anchor1, this.anchor2);
				rj.setLimits(this.lowerLimit, this.upperLimit);
				rj.setLowerLimitEnabled(this.lowerLimitEnabled);
				rj.setUpperLimitEnabled(this.upperLimitEnabled);
				joint = rj;
			} else if ("WeldJoint".equals(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				WeldJoint wj = new WeldJoint(b1, b2, this.anchor);
				wj.setReferenceAngle(this.referenceAngle);
				wj.setFrequency(this.frequency);
				wj.setDampingRatio(this.dampingRatio);
				joint = wj;
			} else if ("WheelJoint".equals(this.jointType)) {
				SandboxBody b1 = this.idMap.get(this.bodyId1);
				SandboxBody b2 = this.idMap.get(this.bodyId2);
				WheelJoint wj = new WheelJoint(b1, b2, this.anchor, this.axis);
				wj.setFrequency(this.frequency);
				wj.setDampingRatio(this.dampingRatio);
				wj.setMaximumMotorTorque(this.maximumMotorTorque);
				wj.setMotorSpeed(this.motorSpeed);
				wj.setMotorEnabled(this.motorEnabled);
				joint = wj;
			} else {
				throw new SAXException("Joint type \"" + this.jointType + "\" unknown or not implemented.");
			}
			
			if (joint != null) {
				joint.setUserData(this.jointName);
				joint.setCollisionAllowed(this.collisionAllowed);
				this.joints.add(joint);
			}
			
			this.jointName = null;
			this.jointType = null;
		} else if ("BodyId1".equalsIgnoreCase(qName)) {
			this.bodyId1Start = false;
		} else if ("BodyId2".equalsIgnoreCase(qName)) {
			this.bodyId2Start = false;
		} else if ("CollisionAllowed".equalsIgnoreCase(qName)) {
			this.collisionAllowedStart = false;
		} else if ("LowerLimit".equalsIgnoreCase(qName)) {
			this.lowerLimitStart = false;
		} else if ("UpperLimit".equalsIgnoreCase(qName)) {
			this.upperLimitStart = false;
		} else if ("LimitEnabled".equalsIgnoreCase(qName)) {
			this.limitEnabledStart = false;
		} else if ("ReferenceAngle".equalsIgnoreCase(qName)) {
			this.referenceAngleStart = false;
		} else if ("Frequency".equalsIgnoreCase(qName)) {
			this.frequencyStart = false;
		} else if ("DampingRatio".equalsIgnoreCase(qName)) {
			this.dampingRatioStart = false;
		} else if ("Distance".equalsIgnoreCase(qName)) {
			this.distanceStart = false;
		} else if ("MaximumForce".equalsIgnoreCase(qName)) {
			this.maximumForceStart = false;
		} else if ("MaximumTorque".equalsIgnoreCase(qName)) {
			this.maximumTorqueStart = false;
		} else if ("MotorSpeed".equalsIgnoreCase(qName)) {
			this.motorSpeedStart = false;
		} else if ("MaximumMotorForce".equalsIgnoreCase(qName)) {
			this.maximumMotorForceStart = false;
		} else if ("MotorEnabled".equalsIgnoreCase(qName)) {
			this.motorEnabledStart = false;
		} else if ("Ratio".equalsIgnoreCase(qName)) {
			this.ratioStart = false;
		} else if ("MaximumMotorTorque".equalsIgnoreCase(qName)) {
			this.maximumMotorTorqueStart = false;
		} else if ("LowerLimitEnabled".equalsIgnoreCase(qName)) {
			this.lowerLimitEnabledStart = false;
		} else if ("UpperLimitEnabled".equalsIgnoreCase(qName)) {
			this.upperLimitEnabledStart = false;
		}
	}
}
