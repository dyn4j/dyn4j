![alt tag](https://raw.githubusercontent.com/wnbittle/dyn4j/master/dyn4j.png)

![Actions Status](https://github.com/dyn4j/dyn4j/workflows/Maven%20CI/badge.svg)
![License](https://img.shields.io/github/license/dyn4j/dyn4j)
![Language](https://img.shields.io/github/languages/top/dyn4j/dyn4j)
![Code Coverage](https://img.shields.io/badge/coverage-71.1%25-brightgreen)

## Java Collision Detection and Physics Engine

A 100% Java 2D collision detection and physics engine.  Designed to be fast, stable, extensible, and easy to use.  dyn4j is free for use in commercial and non-commercial applications.

The project is comprised of the main project and tests managed here and two others:
- [dyn4j-samples](https://github.com/dyn4j/dyn4j-samples) A collection of samples to help get started
- [dyn4j-sandbox](https://github.com/dyn4j/dyn4j-sandbox) A non-trivial desktop application that allows users to build scenes, run, save, and load them - all built with dyn4j as the simulation engine.

### Requirements
* Java 1.6+

### Getting Started
dyn4j comes with a lot of features and extensibility, but getting started is easy.  If you are looking for a quick start, take a look at the following video.

[![Getting started with dyn4j](https://img.youtube.com/vi/OqOcT8z-m_w/0.jpg)](https://www.youtube.com/watch?v=OqOcT8z-m_w)

#### Step 1: Add dyn4j to Your Project
Add dyn4j to your classpath by adding a Maven dependency from 
[Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.dyn4j%22%20AND%20a%3A%22dyn4j%22) or 
[GitHub Packages](https://github.com/dyn4j/dyn4j/packages)
```xml
<dependency>
    <groupId>org.dyn4j</groupId>
    <artifactId>dyn4j</artifactId>
    <version>3.4.0</version>
</dependency>
```

If you are not using Maven you can download the jar from either of the links above.  
**NOTE:** [Releases](https://github.com/wnbittle/dyn4j/releases) are no longer being created as of 3.4.0.

#### Step 2: Create a World
```java
World world = new World();
```
This creates a new simulation environment with default settings.  The default settings use the meter-kilogram-seconds system and include the default pipeline classes.

#### Step 3: Add Some Bodies
```java
Body body = new Body();
body.addFixture(Geometry.createCircle(1.0));
body.translate(1.0, 0.0);
body.setMass(MassType.Normal);
world.addBody(body);
```
A body is the primary unit of simulation and completely rigid.  A body is comprised of many fixtures or shapes.  While the shapes of dyn4j are all convex (and must be), a collection of these shapes can be used to create a body that is not.  A body can be initially placed in a scene by translating or rotating it.  Once the shape(s) of a body is defined, it must be given a mass by calling a setMass method.  The mass type is typically MassType.NORMAL or MassType.INFINITE.  When set to NORMAL, the mass will be calculated based on the shapes.  An INFINITE mass body might represent a floor, ground, or something unmovable.

#### Step 4: Add Some Joints
```java
PinJoint joint = new PinJoint(body, new Vector2(0, 0), 4, 0.7, 1000);
world.addJoint(joint);
```
A joint is a constraint on the motion of one or more bodies.  There are many joint types that serve different purposes.  Generally, joints are used to link bodies together in a specified way.  Bodies can have multiple joints attached to them making for some interesting combinations.

#### Step 5: Run the Simulation
```java
for (int i = 0; i < 100; i++) {
    world.step(1);
}
```
Unlike this example, a GUI based application you would call the World.update(elapsedTime) method in it's render loop.  Either way, each time the world is advanced forward in time (which may or may not occur when using the World.update(elapsedTime) methods) the bodies added to it will be moved based on the world gravity (if any) and will interact with other bodies placed in the world.

#### Next Steps
From here you should take a look at the [dyn4j-samples](https://github.com/dyn4j/dyn4j-samples) sub project to get a jump start with a simple Java2D framework. You can also check out the documentation [here](http://www.dyn4j.org/documentation/).

### Links
* [www.dyn4j.org](http://www.dyn4j.org)
* [Latest Release Notes](https://github.com/wnbittle/dyn4j/blob/master/release-notes.txt)
* [Latest Javadocs](http://docs.dyn4j.org)
* [Forum](http://forum.dyn4j.org) - disabled but still a good resource
* [Blog](http://www.dyn4j.org/category/blog/)

### Building
* Maven build goals: clean package
* Check artifact class version: 
    * javap -verbose -classpath /path/to/jar/dyn4j.jar org.dyn4j.Version 50
    * javap -verbose -classpath /path/to/jar/dyn4j.jar module-info 53+
