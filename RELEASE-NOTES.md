## v4.2.2 - October 1st, 2022

[Milestone](https://github.com/dyn4j/dyn4j/milestone/14?closed=1) |
[Tag](https://github.com/dyn4j/dyn4j/tree/4.2.2) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/4.2.2/bundle) |
[GitHub Release](https://github.com/dyn4j/dyn4j/milestone/14)

Added the ability to set zero density on fixtures. Updated/added more JUnit tests and in doing so found and fixed a few bugs.

**New Features:**
- [#229](https://github.com/dyn4j/dyn4j/issues/229) Allow zero density `BodyFixture`s to allow fixtures to participate in collision detection/resolution, but not contribute to the mass or inertia of the body.

**Bug Fixes**
- [#230](https://github.com/dyn4j/dyn4j/issues/230) Fixed a bug in the `Geometry.minkowskiSum` method where it would compute the wrong sum or fail entirely.
- [#231](https://github.com/dyn4j/dyn4j/issues/231) Fixed a bug in the `Link.translate` method where it wouldn't recompute the neighbor links normals.


## v4.2.1 - January 7th, 2022

[Milestone](https://github.com/dyn4j/dyn4j/milestone/13?closed=1) |
[Tag](https://github.com/dyn4j/dyn4j/tree/4.2.1) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/4.2.1/bundle) |
[GitHub Release](https://github.com/dyn4j/dyn4j/packages/93466?version=4.2.1)

This update contains a few requested features (see below) and a few minor bug fixes.

**New Features:**
- [#201](https://github.com/dyn4j/dyn4j/issues/201) Added a new `collision(T, T, TimeOfImpact)` method to the `TimeOfImpactListener` interface that can be used to perform logic (or ignore the collision) when the minimum time of impact event has been detected.  Before, the `TimeOfImpactListener`s would notify of potential collisions to allow you to filter out detections that weren't necessary (for performance).  There was also a notification that was triggered when a valid time of impact event was detected.  What was missing is the _minimum_ time of impact and the opportunity to change or halt the solving after the minimum was detected.
- [#211](https://github.com/dyn4j/dyn4j/issues/211) Added a new `isOutside(AABB, Transform, Fixture)` method to the `Bounds` interface to better support extension to arbitrary bounding shapes.  The given `AABB` is in world space whereas the `Fixture` is in local coordinates.  This is the **only** method called from the `World` class and super classes.
- [#212](https://github.com/dyn4j/dyn4j/issues/212) Added a new `getIntersection(Polygon, Polygon)` method to the `Geometry` class to return the intersection `Polygon`.  Because the input `Polygon`s are `Convex` this method operates in O(n+m) time.  It will return null in the case where the `Polygon`s do not overlap or if they overlap only by a vertex or edge.
- [#213](https://github.com/dyn4j/dyn4j/issues/213) Added a new `ContainmentDetector` interface that the `Sat` class implements to detect full containment of a `Convex` in another `Convex`.  NOTE: The `Sat` class does not support all `Shape`s and the same holds true for this new containment detection feature.
- [#218](https://github.com/dyn4j/dyn4j/issues/218) Added a new `getArea` method to the `Shape` class to return the area.

**Bug Fixes**
- [#214](https://github.com/dyn4j/dyn4j/issues/214) Fixed an issue where only the `Sat` class would clear the `Penetration` / `Separation` objects.  Now all detectors require the caller to clear these objects before calling the methods that use them.
- [#215](https://github.com/dyn4j/dyn4j/issues/215) Fixed an issue where the `VertexClusterReduction` class would fail with an exception in certain degenerate cases
- [#216](https://github.com/dyn4j/dyn4j/issues/216) Fixed an issue where the `Simplifier` implementations would behave differently when passing in null/empty lists vs arrays.
- [#217](https://github.com/dyn4j/dyn4j/issues/217) Fixed an issue where the `Slice` class would not return the correct inertia when using a density other than 1


## v4.2.0 - May 13th, 2021

[Milestone](https://github.com/dyn4j/dyn4j/milestone/8?closed=1) |
[Tag](https://github.com/dyn4j/dyn4j/tree/4.2.0) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/4.2.0/bundle) |
[GitHub Release](https://github.com/dyn4j/dyn4j/packages/93466?version=4.2.0)

The primary goal of this release was to fix an issue with the way smooth sliding was implemented for the `Link` shape.  This shape is used to avoid the ghost collision problem caused by the selection of collision points during collision detection.  The issue occurred when a long shape would lean over a bend in the `Link` chain causing the algorithm to choose the wrong normal.  To fix the issue, a breaking change was necessary which requires that the `Link` shape be one-sided.  You can read more about the problem and solution on [Erin Catto's blog](https://box2d.org/posts/2020/06/ghost-collisions/).

A secondary goal of this release was to merge the `RopeJoint` into the `DistanceJoint` and to improve automated unit testing for all joints.  The automated tests found a few issues, mostly inconsistencies in usage, that were also fixed in this release.  Some of these fixes were breaking changes.  Some effort was put into normalizing the API surface between the joints also - for example, those joints with limits should have the same methods to get/set those limits (with some exceptions for those who can't control which are enabled).

One _unplanned_ feature in this release is simple-polygon (without holes) simplification algorithms in the new `org.dyn4j.geometry.simplify` package.  The `decompose` package has been around for a while now and does a great job.  The problem is that the decomposition process is exact, the output must include all vertices from the input.  For example, take a simple polygon with 300 vertices, the decomposition algorithms may convert that into 100 convex shapes, but some of those shapes will be thin and long which are particularly bad for collision detection/resolution.  This isn't a problem with the decomposition algorithms, but rather a problem with the input.  The problem shapes emitted in the decomposition process are typically caused by features of little visual significance.  The goal was to evaluate methods to simplify the polygon before decomposition.  Using this new package, a 300 vertex polygon can be reduced to 40 vertices (a reduction of 86%) which is further decomposed into 20 convex shapes - all with minimal loss in fidelity!

Finally, this release also sees the total code coverage go up to 93% with 2100+ automated tests.  Coverage isn't the only metric for code quality, but a lot of time was spent on making the tests meaningful.  For example, simulation based tests were introduced to test different configurations of joints over time.

**New Features:**
- [#200](https://github.com/dyn4j/dyn4j/pull/200) Added simple polygon simplification algorithms that can be used to optimize geometry before being sent to the decomposition algorithms.
- [#200](https://github.com/dyn4j/dyn4j/pull/200) Added new methods to the `Decomposer` and `HullGenerator` interfaces that accept `List<Vector2>` for ease of use.
- [#150](https://github.com/dyn4j/dyn4j/issues/150) Moved the restitution velocity from a global setting to a setting on the `BodyFixture` to allow better control of restitution.
- [#151](https://github.com/dyn4j/dyn4j/issues/151) The `AngleJoint` now supports negative ratios to allow for opposite angular velocities.
- [#151](https://github.com/dyn4j/dyn4j/issues/151) The `WheelJoint` now supports the absence of a spring-damper by setting the frequency to zero.
- [#151](https://github.com/dyn4j/dyn4j/issues/151) The `RopeJoint` has been merged into the `DistanceJoint` allowing toggling between the two behaviors.

**Bug Fixes:**
- [#105](https://github.com/dyn4j/dyn4j/issues/105) Fixed issue with `Link` shape where collision normals would be adjusted incorrectly in some scenarios.
- [#151](https://github.com/dyn4j/dyn4j/issues/151) Fixed a few minor issues in the `AngleJoint`, `PrismaticJoint`, and `RevoluteJoint` classes in the context of warm starting and changing limits and motor speed.

**Breaking Changes:**
- [#105](https://github.com/dyn4j/dyn4j/issues/105) All `Link` shape chains are now one-sided. The direction of the normal is now dependent on the winding.  Counter-clockwise winding will produce normals pointing to the right of the `Link`s and clockwise winding will produce normals pointing to the left of the `Link`s.
- [#151](https://github.com/dyn4j/dyn4j/issues/151) The `AngleJoint` now throws an exception when a ratio of zero is given.
- [#151](https://github.com/dyn4j/dyn4j/issues/151) The behavior of the `PulleyJoint` when using the ratio option has changed to be more consistent when changing the total length or ratio.
  
**Other:**
- [#151](https://github.com/dyn4j/dyn4j/issues/151) Minor performance improvement in the `PinJoint`.

## v4.1.4 - February 26th, 2021

[Milestone](https://github.com/dyn4j/dyn4j/milestone/12?closed=1) |
[Tag](https://github.com/dyn4j/dyn4j/tree/4.1.4) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/4.1.4/bundle) |
[GitHub Release](https://github.com/dyn4j/dyn4j/packages/93466?version=4.1.4)

**Bug Fixes:**
- [#186](https://github.com/dyn4j/dyn4j/issues/186) Fixed the removal of sensor fixtures so the atRest state of colliding bodies is unchanged

## v4.1.3 - February 13th, 2021

[Milestone](https://github.com/dyn4j/dyn4j/milestone/11?closed=1) |
[Tag](https://github.com/dyn4j/dyn4j/tree/4.1.3) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/4.1.3/bundle) |
[GitHub Release](https://github.com/dyn4j/dyn4j/packages/93466?version=4.1.3)

**Bug Fixes:**
- [#181](https://github.com/dyn4j/dyn4j/issues/181) Fixed an issue when using the PinJoint and removing a body.

## v4.1.2 - February 9th, 2021

[Milestone](https://github.com/dyn4j/dyn4j/milestone/10?closed=1) |
[Tag](https://github.com/dyn4j/dyn4j/tree/4.1.2) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/4.1.2/bundle) |
[GitHub Release](https://github.com/dyn4j/dyn4j/packages/93466?version=4.1.2)

**Bug Fixes:**
- [#179](https://github.com/dyn4j/dyn4j/issues/179) Fixed a bug introduced in 4.0.0 that would cause a body to be added to Islands more than once

## v4.1.1 - February 8th, 2021

[Milestone](https://github.com/dyn4j/dyn4j/milestone/9?closed=1) |
[Tag](https://github.com/dyn4j/dyn4j/tree/4.1.1) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/4.1.1/bundle) |
[GitHub Release](https://github.com/dyn4j/dyn4j/packages/93466?version=4.1.1)

**Bug Fixes:**
- [#177](https://github.com/dyn4j/dyn4j/issues/177) Fixed a bug introduced in 4.1.0 that would throw an IllegalStateException when removing a body from a world

## v4.1.0 - February 4th, 2021

[Milestone](https://github.com/dyn4j/dyn4j/milestone/5?closed=1) |
[Tag](https://github.com/dyn4j/dyn4j/tree/4.1.0) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/4.1.0/bundle) |
[GitHub Release](https://github.com/dyn4j/dyn4j/packages/93466?version=4.1.0)

This version sees massive performance improvement for large worlds, on the order of 10x, when using Continuous Collision Detection (CCD).
It also includes a number of bug fixes and enhancements to the CCD detection and resolution process.  These changes required a number of 
breaking changes to the org.dyn4j.broadphase APIs and related classes.  In general, if you aren't interacting with the broadphase classes 
directly, then you'll see minimal changes (a few listeners have updated and the CollisionData class is slightly different).

**New Features:**
- [#122](https://github.com/dyn4j/dyn4j/issues/122) Major performance enhancements for CCD (10x in large worlds)
- [#63](https://github.com/dyn4j/dyn4j/issues/63) Enhanced the broadphase API to allow for additional use-cases (like CCD)

**Bug Fixes:**
- Fixed some issues with the SegmentDetector.raycast method in coincident scenarios
- ConservativeAdvancement now returns false if the bodies are near collision in their previous transform
- ConservativeAdvancement now exits immediately if it advances to a collision to improve resolution
- Gjk now returns better separating points, normal and depth in some cases
- Fixed issue where Gjk would return the detect epsilon in the getRaycastEpsilon method
- Fixed an issue where CCD would re-test & re-solve the same pair in some cases
- Fixed an issue with Geometry.createLinks when the list/array includes a null element

**Breaking Changes:**
- CollisionPair was changed from CollisionPair<CollisionBody, Fixture> to CollisionPair<T>
- AbstractCollisionPair was changed from AbstractCollisionPair<CollisionBody, Fixture> to AbstractCollisionPair<T>
- BasicCollisionPair was changed from BasicCollisionPair<CollisionBody, Fixture> to BasicCollisionPair<T>
- BroadphaseDetector was changed from BroadphaseDetector<CollisionBody, Fixture> to BroadphaseDetector<T>
- AbstractBroadphaseDetector was changed from AbstractBroadphaseDetector<CollisionBody, Fixture> to AbstractBroadphaseDetector<T>
- BruteForceDetector, DynamicAABBTree, and SAP broadphase detector constructors were changed to accept BroadphaseFilter, AABProducer, and AABBExpansionMethod arguments
- AABB.createAABBFromPoints was renamed to AABB.createFromPoints
- The org.dyn4j.world.BroadphaseFilter was renamed to org.dyn4j.world.BroadphaseCollisionDataFilter to differentiate it with the org.dyn4j.broadphase.BroadphaseFilter
- ForceCollisionTimeOfImpactSolver now only translates the bodies being resolved
- FallbackConditions no longer implements equals/hashcode methods
  
**Other:**
- [#125](https://github.com/dyn4j/dyn4j/issues/125) All deprecated API as of 4.0.2 has been removed.
- [#124](https://github.com/dyn4j/dyn4j/issues/124) Continued improvement and coverage of JUnit test cases.
- [#156](https://github.com/dyn4j/dyn4j/issues/156) Javadoc includes protected methods again.
- Small numerical improvement to the Transform.lerp method and variants.

## v4.0.2 - October 15th, 2020

[Milestone](https://github.com/dyn4j/dyn4j/milestone/7?closed=1) |
[Tag](https://github.com/dyn4j/dyn4j/tree/4.0.2) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/4.0.2/bundle) |
[GitHub Release](https://github.com/dyn4j/dyn4j/packages/93466?version=4.0.2)

**Bug Fixes:**
- [#149](https://github.com/dyn4j/dyn4j/issues/149) Fixed the RevoluteJoint.getReactionTorque method to include motor impulse

## v4.0.1 - September 26th, 2020

[Milestone](https://github.com/dyn4j/dyn4j/milestone/6?closed=1) |
[Tag](https://github.com/dyn4j/dyn4j/tree/4.0.1) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/4.0.1/bundle) |
[GitHub Release](https://github.com/dyn4j/dyn4j/packages/93466?version=4.0.1)

**New Features:**
- [#136](https://github.com/dyn4j/dyn4j/issues/136) Added back a listener method to allow setting the sensor/enabled flags on Contact Constraints during collision detection

**Bug Fixes:**
- [#130](https://github.com/dyn4j/dyn4j/issues/130) Fixed the getReactionForce/Torque methods to return the proper values
- [#135](https://github.com/dyn4j/dyn4j/issues/135) Fixed issue with Geometry.createLinks when using closed = true would not produce the correct end linkage
  
**Other:**
- [#137](https://github.com/dyn4j/dyn4j/issues/137) Clean up of test import warnings

## v4.0.0 - August 29th, 2020

[Milestone](https://github.com/dyn4j/dyn4j/milestone/4?closed=1) |
[Tag](https://github.com/dyn4j/dyn4j/tree/4.0.0) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/4.0.0/bundle) |
[GitHub Release](https://github.com/dyn4j/dyn4j/packages/93466?version=4.0.0)

This version sees massive performance improvements for large worlds, on the order of 30%-40% improvement. In addition, this version sees a big
change in the API to better segment code for maintainability, testability, and extensibility. Along with those improvements comes lower object
allocation per iteration and higher visibility into the output of the collision detection/resolution pipelines.

__IMPORTANT!__ This version of dyn4j keeps a number of deprecated APIs in place, but makes no attempt to allow the deprecated API to work with 
the new APIs. The recommendation is that if you want to take advantage of the new features and performance enhancements, you will need to remove
all references to deprecated API.

__ALL__ deprecated APIs will be removed in the next version. The deprecated APIs should be used for upgrading only.

**New Features:**
- [#99](https://github.com/dyn4j/dyn4j/issues/99) Major performance enhancements for large worlds (30-40%) and some small enhancements for small-medium worlds
- [#99](https://github.com/dyn4j/dyn4j/issues/99) Added new objects to track collision over time to reduce allocation per frame.
- [#99](https://github.com/dyn4j/dyn4j/issues/99) Added new API to easily see the entire output of the collision detection/resolution pipelines
- New predictive joint limits
- New linear limits on the Wheel Joint
- [#102](https://github.com/dyn4j/dyn4j/issues/102) Added guards on Joint setX methods to ensure that the joined bodies are not awakened unless necessary
- [#103](https://github.com/dyn4j/dyn4j/issues/103) Updated the FrictionJoint default max force/torque to non-zero values to better illustrate it's use to new users
- Increased test coverage by 10%+ and greatly improved test quality
- Parameterized World and Joint classes for better support for extension of the Body class

**Bug Fixes:**
- [#106](https://github.com/dyn4j/dyn4j/issues/106) PulleyJoint no longer allows negative length
- [#100](https://github.com/dyn4j/dyn4j/issues/100) Fixed unrealistic falling of bodies under gravity when bound together by a MotorJoint
- Fix for overflow in the Rotation/Transform classes where it would generate values outside the valid range [1.0, -1.0] of cos(t)/sin(t) which produced NaNs
- Fix for the ContactListener.end method not being called in some cases

**Deprecated:**
- A lot of the components in the dynamics packages have been deprecated and replaced with components in the world packages. The vast majority of deprecated APIs have replacement APIs

**Breaking Changes:**
- [#71](https://github.com/dyn4j/dyn4j/issues/71) Removed all references to UUID in the project as promised in the previous version's deprecation
- Adding a Joint to a world now requires the bodies to be added first
- The BroadphaseDetector.getAABB methods will now always return an expanded AABB if that feature is supported
- The Body.getChangeInOrientation method now returns the minimum angular change when the angular velocity is zero
- The BodyFixture class now enforces the friction/restitution coefficients to be non-negative
- The ContactConstraint class now enforces the friction/restitution coefficients to be non-negative
- The TimeStep (formerly Step) class now enforces the delta time to be greater than zero
- Some listener methods have been removed, renamed, or moved.
  
**Other:**
- [#98](https://github.com/dyn4j/dyn4j/issues/98) Replaced release-notes.txt with RELEASE-NOTES.md for better formatting and maintenance.
- [#101](https://github.com/dyn4j/dyn4j/issues/101) Added unit documentation for damping values on the Body class

## v3.4.0 - January 10th, 2020

[Milestone](https://github.com/dyn4j/dyn4j/milestone/3?closed=1) |
[Tag](https://github.com/dyn4j/dyn4j/tree/3.4.0) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.4.0/bundle) |
[GitHub Release](https://github.com/dyn4j/dyn4j/packages/93466?version=3.4.0)

**New Features:**
- Arithmetic optimizations [@mtsamis](https://github.com/mtsamis)
- Small performance enhancement in the Island class [@mtsamis](https://github.com/mtsamis)
- Memory footprint improvements [@mtsamis](https://github.com/mtsamis)
- AABB unpack and performance enhancements [@mtsamis](https://github.com/mtsamis)
- New LazyAABBTree broadphase detector [@mtsamis](https://github.com/mtsamis)
- New BruteForceBroadphase detector [@mtsamis](https://github.com/mtsamis)
- Geometry optimizations [@mtsamis](https://github.com/mtsamis)
- Dynamics optimizations [@mtsamis](https://github.com/mtsamis)
- New Rotation class for more efficient rotations [@mtsamis](https://github.com/mtsamis)
- New divide, quotient, and inverseRotate methods for Vector2 [@mtsamis](https://github.com/mtsamis)
- New RobustGeometry class for dealing with error in floating point computations (specifically for geometry at the moment) [@mtsamis](https://github.com/mtsamis)

**Bug Fixes:**
- [#45](https://github.com/dyn4j/dyn4j/issues/45) Bug fix for HalfEllipse.getRadius(Vector2) when the half width < half height and the given point is below the evolute.
- Bug fix for the initial transform not updating if CCD is turned off
- [#53](https://github.com/dyn4j/dyn4j/issues/53) Bug fix for strange code in position solver
- [#60](https://github.com/dyn4j/dyn4j/issues/60) Bug fix for wrong raycast results when using vertical segment/link shapes
- [#61](https://github.com/dyn4j/dyn4j/issues/61) Bug fix for Polygon.contains when the given point is coincident with an edge of the polygon
- [#88](https://github.com/dyn4j/dyn4j/issues/88) Bug fixes for the SweepLine algorithm where it would produce incorrect results
- [#64](https://github.com/dyn4j/dyn4j/issues/64) Fix for a small bug in the BroadphaseKey equals method
- [#69](https://github.com/dyn4j/dyn4j/issues/69) Fix for Wound.getVertexIterator and Wound.getNormalIterator methods to ensure all array elements are returned
- [#75](https://github.com/dyn4j/dyn4j/issues/75) Fix for NPE in SweepLine w/ detection of crossing edges and degenerate simple polygons throwing IllegalArgumentException
- [#76](https://github.com/dyn4j/dyn4j/issues/76) [#80](https://github.com/dyn4j/dyn4j/issues/80) [#83](https://github.com/dyn4j/dyn4j/issues/83) Fixes for the DivideAndConquer, GiftWrap, and GrahamScan convex hull generation algorithms for colinear and coincident points and for precision loss (by mtsamis)
    
**Deprecated:**
- [#47](https://github.com/dyn4j/dyn4j/issues/47) getId methods in all classes are going away in the next version. If you need an id, please use the setUserData method.
- [#79](https://github.com/dyn4j/dyn4j/issues/79) Transform.IDENTITY - please use new Transform() instead of this static final property.
  
**Breaking Changes:**
- [#75](https://github.com/dyn4j/dyn4j/issues/75) double getRotation() methods were replaced with Rotation getRotation() methods. Use the getRotationAngle() method instead.
- The Polygon class now checks for degenerate (zero area) polygons and throws an IllegalArgumentException when detected
  
**Other:**
- Updated broadphase tests (by mtsamis)
- Many more (and improved) unit tests for the geometry package (by mtsamis)
- [#94](https://github.com/dyn4j/dyn4j/issues/94) Updated the Ray class constructors to normalize the input direction to help avoid common usage issues.

## v3.3.0 - April 14th, 2018

[Tag](https://github.com/dyn4j/dyn4j/tree/3.3.0) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.3.0/bundle)

**New Features:**
- [#30](https://github.com/dyn4j/dyn4j/issues/30) Performance improvements for Android
- [#29](https://github.com/dyn4j/dyn4j/issues/29) Java 9 modules support [@io7m](https://github.com/io7m)
- [#29](https://github.com/dyn4j/dyn4j/issues/29) OSGi support [@io7m](https://github.com/io7m)
- More control of iteration count and epsilon values in the GJK algorithm
- [#29](https://github.com/dyn4j/dyn4j/issues/29) Proper Maven-ization of the project with automated building, testing, and packaging [@io7m](https://github.com/io7m)
- Added an enabled flag for the ContactConstraint class to allow contacts to be temporarily turned off, but still warm started and tracked.

**Bug Fixes:**
- The GJK algorithm hanging in rare cases
    
**Deprecated:**
- ContactListener.sensed method has been deprecated.  Sensor events are sent to the ContactListener.begin, ContactListener.persist, and ContactListener.end methods instead with a sensor flag. NOTE: The ContactListener.sensed method still exists, but is no longer called.
- SimpleContactManager was combined with WarmStartingContactManager into DefaultContactManager. You can turn off warm starting via the setWarmStartingEnabled(boolean) method.
  
**Breaking Changes:**
- The ContactListener.sensed method is no longer called.
  
**Other:**
- Some other small performance enhancements

## v3.2.4 - May 1st, 2017

[Tag](https://github.com/dyn4j/dyn4j/tree/3.2.4) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.2.4/bundle)

**Bug Fixes:**
  - [#26](https://github.com/dyn4j/dyn4j/issues/26) Joint.world property not set to null when the joint is removed
    
**Other:**
- Small javadoc updates
- Changed the DefaultBroadphaseFilter to allow non-dynamic vs. non-dynamic bodies to continue in the collision detection pipeline as long as one of them is a sensor
- Changed the Sap broadphase detector to use a self-balancing binary tree by default.

## v3.2.3 - September 4th, 2016

[Tag](https://github.com/dyn4j/dyn4j/tree/3.2.3) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.2.3/bundle)

**Bug Fixes:**
- [#20](https://github.com/dyn4j/dyn4j/issues/20) Sap.detect(aabb, filter) returns false when it should return true
- [#21](https://github.com/dyn4j/dyn4j/issues/21) Wrong values returned from getRadius(Vector2) methods for Slice, Capsule, Ellipse, and HalfEllipse
    

## v3.2.2 - June 19th, 2016

[Tag](https://github.com/dyn4j/dyn4j/tree/3.2.2) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.2.2/bundle)

**New Features:**
- [#19](https://github.com/dyn4j/dyn4j/issues/19) Added the Link shape class to fix the internal edge problem
- Exposed a set/get method for the BroadphaseFilter that is being used in the World.detect method to give more control over the filtering during the broadphase.

**Bug Fixes:**
- Fixed a small bug in the Segment class where the second normal was incorrect, but was changed to the correct one during collision detection.
    
**Other:**
- Small code clean up here and there.
- Updated copyright dates.

## v3.2.1 - November 23rd, 2015

[Tag](https://github.com/dyn4j/dyn4j/tree/3.2.1) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.2.1/bundle)

**New Features:**
- Added getAngularTranslation and getLinearSpeed methods to the WheelJoint class.
- Added setLength method to the PulleyJoint class.

**Bug Fixes:**
- Fixed a bug where the normals were rotated improperly for Polygon and Segment shapes for local rotations.
- Fixed a bug with the Rectangle.getRotation method where it would report the angle negative.
    
**Deprecated:**
- getJointTranslation and getJointSpeed on the WheelJoint class for getLinearTranslation and getAngularSpeed.

**Other:**
- Updated samples to use a common base class for rendering and other stuff to make the samples contain mostly dyn4j usage code.
- The WheelJoint's default frequency is now set to 8.0 by default for easier use.
- The RevoluteJoint's default limits are set to the initial angle between the bodies (the limit is still disabled by default).
- More javadoc updates.

## v3.2.0 - September 30th, 2015

[Tag](https://github.com/dyn4j/dyn4j/tree/3.2.0) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.2.0/bundle)

**New Features:**
- Performance improvements.

**Deprecated:**
- Body.setMass() it was odd that it set the mass type to Mass.Type.NORMAL. Use the Body.setMass(MassType) method instead.
  
**Breaking Changes:**
- The Mass.Type enumeration has been renamed to MassType.
- The BroadphaseDetector interface has changed drastically.
- The three SAP algorithms have been replaced by one.
- The CollisionListener.collision(Body,Body) method has been removed due to changes made to the broad-phase.
- The Body.getFixtures() method now returns an unmodifiable list.
- The World.getBodies() and World.getJoints() methods now return unmodifiable lists.
- The Bounds interface changed to only include the Translatable interface due to the some bounds types not supporting rotation.
- Many member variables and methods have been made final, package private and private.
- Made a few internal classes package private.
- Many deprecated methods and classes have been removed.
- The Settings.ContinuousDetectionMode enumeration has been moved to its own class file with the same name.
- Renamed the MouseJoint to PinJoint.
  
**Other:**
- Cleaner API with less classes, methods and members to sift through and more interface options for extensibility.
- More & revised Javadoc comments.

## v3.1.11 - January 31st, 2015

[Tag](https://github.com/dyn4j/dyn4j/tree/3.1.11) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.1.11/bundle)

**New Features:**
- A few minor performance improvements.
    
**Bug Fixes:**
- Fixed a bug in the raycast(Ray,double,boolean,boolean,List<RaycastResult>) method where it was calling itself, causing a StackOverflowException. 
    
**Other:**
- Changed the Math.hypot calls to Math.sqrt since the former is much slower.  It's slower due to the overflow/underflow handling, which dyn4j doesn't need.
- Added code to export Rays for the Java exporter.

## v3.1.10 - July 20th, 2014

[Tag](https://github.com/dyn4j/dyn4j/tree/3.1.10) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.1.10/bundle)

**New Features:**
- Added a new update(double, int) method to the World class that will execute any number of steps given the elapsed time.
- Added get/set methods for the time accumulator on the World class.
    
**Bug Fixes:**
- Fixed a bug in the WeldJoint class when using two fixed angular velocity bodies.
- Fixed a NPE bug in the World.detect methods.
- Fixed a bug in the EarClipping class where the decomposition would lead to a colinear vertex situation.
- Fixed a bug in Bayazit class where it would select a closest vertex that was not visible by the current vertex.  Sadly, this has made it much slower.

**Other:**
- Updated license dates.

## v3.1.9 - March 29th, 2014

[Tag](https://github.com/dyn4j/dyn4j/tree/3.1.9) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.1.9/bundle)

**New Features:**
- Added a new interface, Triangulator, that both the EarClipping and SweepLine algorithms implement that allows you to get a triangulation of a simple polygon.
- Added a bunch of new detect methods to the World class to allow filtering of sensor bodies, inactive bodies, or by a Filter object and some other features.
- Added a new DetectListener interface specifically for the World.detect methods that allow arbitrary filtering of collision tests.
- Added new raycast and convexCast methods that include a Filter and ignoreInactive parameters.
    
**Bug Fixes:**
- Fixed a bug in the SweepLine class where it would get a NPE when there were vertices who had close to the same y value (were sorted incorrectly).
- Fixed a bug in the EarClipping class where extra half edges were added to the DCEL at termination that didn't need to be.
- Fixed a bug in the Interval.intersection(Interval) class where it was performing the union rather than the intersection.
- Fixed a bug in the SweepLine class where it would enter an infinite loop due to a poor sort condition during the decompose to y-monotone phase.
- Fixed a bug in the convexCast methods where it would exit early instead of continuing to test other fixtures when false was returned from a particular ConvexCastListener method.
- Fixed a bug in the Sandbox app where null would be output instead of the correct body instance.
    
**Deprecated:**
- World.detect(AABB), World.detect(Convex), and World.detect(Convex, Transform) have been replaced by with new World.detect methods that have more input and output options.

**Breaking Changes:**
- The World.detect, World.raycast, and World.convexCast methods now filter out inactive bodies by default (they were included in versions before).
    
**Other:**
- Regression JUnit tests for bugs listed above.
- Made a few small changes to some classes (added hashcode methods and changes like this) that have zero effect on usage, output or performance.

## v3.1.8 - December 21st, 2013

[Tag](https://github.com/dyn4j/dyn4j/tree/3.1.8) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.1.8/bundle)

**New Features:**
- Added the methods Body.getFixture(Vector2), Body.getFixtures(Vector2), Body.removeFixture(Vector2), and Body.removeFixtures(Vector2) which allow you to get/remove fixtures given a world space point.
    
**Bug Fixes:**
- Fixed a bug in the Vector2.distance(double, double) method (wrong since 1.1.0, yikes!).  Thankfully its not used anywhere in the engine so no regression.
- Fixed a bug in Body.applyImpulse(Vector2, Vector2) where the body would not be awakened.
    
**Other:**
- Regression JUnit test for Vector2.distance(double, double) method.
- Added documentation to the World.convexCast methods to explain the limitation of the start of the cast (its a similar limitation as the raycast methods).
- The Body.setLinearVelocity(Vector2) method now transfers the x,y values of the given velocity to the body's velocity vector.  This has no effect on anything, except this way you can reuse the vector passed to this method now (earlier it was doing a reference assignment).

## v3.1.7 - October 12th, 2013

[Tag](https://github.com/dyn4j/dyn4j/tree/3.1.7) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.1.7/bundle)

**Bug Fixes:**
- Fixed a bug in the Ellipse.getHalfHeight method where it was returning the half width instead.
- Fixed a bug in the Ellipse.contains method where it would fail if there was a local translation and a local rotation.
- Fixed a bug in the Graphics2DRenderer that would incorrectly render the Ellipse shapes (this was why the above bug existed).
- Fixed a bug in the HalfEllipse.contains method where it would fail if there was a local translation and a local rotation.

**Breaking Changes:**
- Had to remove the Ellipse.getPointClosestToPoint method due to an incorrect assumption resulting in incorrect return values.  The real solution to this problem involves solving a quartic equation which is typically done by a root finding algorithm.
    
**Other:**
- Regression JUnit test for Ellipse.getHalfHeight, Ellipse.contains, and HalfEllipse.contains methods

## v3.1.6 - October 6th, 2013

[Tag](https://github.com/dyn4j/dyn4j/tree/3.1.6) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.1.6/bundle)

**New Features:**
- Added a "slack" flag to the PulleyJoint to allow the constraint to be applied only when the current length is greater than the total length. To turn this feature on, use the setSlackEnabled method.
    
**Bug Fixes:**
- Fixed a bug in the Segment.getSegmentIntersection methods where it would return an intersection point falsely.
    
**Other:**
- Regression JUnit test for Segment.getSegmentIntersection

## v3.1.5 - September 14th, 2013

[Tag](https://github.com/dyn4j/dyn4j/tree/3.1.5) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.1.5/bundle)

**New Features:**
- Added a new Capsule shape primitive.
- Added a new Slice shape primitive.
- Updated the createUnitCirclePolygon method to be more efficient.
- Added methods to generate polygonal capsules, slices, ellipses, and half ellipses to the Geometry class.
- Added a new Ellipse shape primitive (GJK support only).
- Added a new HalfEllipse shape primitive (GJK support only).
- Added a Graphics2DRenderer class to the examples directory to show how to render the shapes using Java2D.  Some of the new shapes are difficult to render properly due to local rotation and curved features.
- Added a new FallbackNarrowphaseDetector which uses FallbackConditions to determine when to use a fallback NarrowphaseDetector rather than the primary. This is useful when you want to use the SAT algorithm with the ellipse or half ellipse shapes (fallback to GJK on these types). See the SingleTypedFallbackCondition and PairwiseTypedFallbackCondition classes.
- Added equivalent instance methods for common Segment and line operations to the Segment class.
- Added a couple of methods to do a fast radial expansion of a polygon.
- Added scaling operations to the Geometry class.
- Added Convex Casting to the World class.  This also included a rework of the Time of Impact and CCD code.
    
**Bug Fixes:**
- Fixed a bug in the Vector2.getAngleBetween method where it would return an angle outside the range of [-pi, pi]
- Fixed a bug in the Gjk algorithm where a near zero vector was not being caught, causing a false negative during collision and a false positive in the distance method.
- Fixed some bugs in the JUnit tests (no real effect but still important so that future releases do not impact existing functionality)
    
**Deprecated:**
- Body.setVelocity and Body.getVelocity have been replaced by Body.setLinearVelocity and Body.getLinearVelocity respectively.

**Breaking Changes:**
- The addition of convex casting caused an overhaul of the Time of Impact and CCD code.  This could have breaking changes if you were hooking directly into any of this functionality.
    
**Other:**
- Many more JUnit tests.
- Updated license dates to be consistent.
- Removed the TestBed code.  You can always go get it from any version tagged 3.1.4 or lower under the tags folder in SVN.

## v3.1.4 - June 12th, 2013

[Tag](https://github.com/dyn4j/dyn4j/tree/3.1.4) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.1.4/bundle)

**New Features:**
- Added a new feature to the Geometry class for flipping shapes about an arbitrary line (defined as an axis and point).
- Not really a feature, but a createAABB accepting no arguments was added to the shape interface for convenience.
    
**Bug Fixes:**
- Fixed a bug that was introduced in 3.1.3 where the average center was not computed correctly since the vertex indexer was a 1 instead of an i.

**Breaking Changes:**
- The shape, fixture, body, etc ids were updated from string to their UUID counter parts for better flexibility.  However, if you were saving or doing anything with the ids, that will need to change to something like getId().toString().

## v3.1.3

[Tag](https://github.com/dyn4j/dyn4j/tree/3.1.3)

**Bug Fixes:**
- Fixed a major bug in the Polygon inertia calculation where it did not take into account the local center.  This in turn made multi-fixture masses incorrect as well.

## v3.1.2 - November 6th, 2012

[Tag](https://github.com/dyn4j/dyn4j/tree/3.1.2) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.1.2/bundle)

**New Features:**
- Added ids to ContactPoint objects to help track them.

**Breaking Changes:**
- The ContactPoint and subclasses now must take in a ContactPointId object
- The IndexedManifoldPointId class no longer has the setXXX methods

## v3.1.1 - August 11th, 2012

[Tag](https://github.com/dyn4j/dyn4j/tree/3.1.1) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.1.1/bundle)

**New Features:**
- Added detect methods to the World class to do collision detection on a area bounded by an AABB or Convex.
- Added contains methods for bodies, joints and listeners to the World class.
- Added the createSweptAABB method to the Swept interface. Used in the CCD code to help cull collision tests.
- Added contains(Vector2) and contains(double,double) to the AABB class to test for point in AABB.
- Added a addFixture method to the Body class that accepts density, friction and restitution values for convenience.
- Added a contains(BodyFixture) method to the Body class.
- Added applyImpulse(Vector2), applyImpulse(double) and applyImpulse(Vector2, Vector2) methods to the Body class.
- Added a getExpanded methods to the AABB and Interval classes.  Added intersection and getIntersection methods to the AABB class.  Added a getLength method to the Interval class.
- Moved getUserData to the Constraint class so that you can now assign a user object to contact constraints.
- Added initial capacity parameters to the constructors of relevant classes to help size internal structures appropriately for better performance.
- Added line and segment intersection methods to the Segment class

**Bug Fixes:**
- Fixed a bug in the ContactConstraintSolver where if there were two contacts and just one was disabled, it was still possible that they would both be solved.
- The expand methods in the Interval and AABB classes would accept a negative expansion amount.  If the expansion was larger than the size, the interval or AABB would be invalid.  This has been fixed to return degenerate versions (about the mid point) instead.

**Deprecated:**
- World class methods remove(Body,boolean), remove(Body), remove(Joint), removeAll, removeAll(boolean), add(Body), add(Joint), and removeListeners replaced with removeBody(Body,boolean), removeBody(Body), removeJoint(Joint), removeAllBodiesAndJoints, removeAllBodiesAndJoints(boolean), addBody(Body), addJoint(Joint), and removeAllListeners respectively.
- RectangularBounds replaced with AxisAlignedBounds.
- Body class methods apply(double), apply(Force), apply(Torque), apply(Vector2), and apply(Vector2, Vector2) replaced with applyTorque(double), applyForce(Force), applyTorque(Torque), applyForce(Vector2), and applyForce(Vector2, Vector2) respectively.
- ContactManager.isEmpty() replaced with ContactManager.isCacheEmpty()

**Breaking Changes:**
- The Step.update(double) method has been changed from public to protected.
- Changed the ContactConstraint to have a List of Contacts instead of the Contact array.
- The Settings.setStepFrequency method has been changed to accept 1/frequency (1/60) instead of frequency (60) as before. In versions before 3.1.1 one you could pass in 60 and it would be automatically inverted.
- The Shape.rotate(double) method's purpose has been changed to rotate the shape about the origin.  A new method, rotateAboutCenter has been created to replace the old functionality.
- The Broadphase detectors were returning a locally stored list that was cleared and re-populated on the detect method.  These methods now return a new list each time.  They now also return an empty list when there are no collisions via the Collections.emptyList() method.
    
**Other:**
- Small performance enhancements via manual inlining of methods.
- Small performance enhancements via appropriate collection sizing.
- Small performance hit from returning a new list from the broadphase detect methods.
- Small performance enhancement to DynamicAABBTree.detect methods by changing the recursive search to a stackless iterative search.
- Small performance hit from returning new Penetration and Manifold objects from the CollisionListener methods.
- The Settings class now allows any values one or greater for the velocity and position constraint solver iterations.  In addition any value greater than zero can be used for the step frequency.
- Updated Javadocs for the convex shape classes with documentation on where the center of the shape will be and general restrictions on construction.
- Updated Javadocs for all listener types to help aid understanding of their use and pitfalls.
- Replaced the usage of the synchronized Stack class with ArrayDeque or ArrayList to improve performance.
- Many more JUnit tests.

## v3.1.0 - July 2nd, 2012

[Tag](https://github.com/dyn4j/dyn4j/tree/3.1.0) |
[Maven Release](https://search.maven.org/artifact/org.dyn4j/dyn4j/3.1.0/bundle)

**New Features:**
- MotorJoint - Best for character movement.
- Coordinate Shifting - For large worlds.
- AngleJoint ratio - For creating gears.
- Multiple listeners
- Runtime version checking
  
**Bug Fixes:**
- Added checks for bodies and joints already added to another world.
- Fixed a bug in the World.removeAll method where it would still report a destoryed body even if notify was false.
- Fixed a bug in the Transform.setRotation method where it was set incorrectly.
- Fixed a bug in the FrictionJoint class where the linear constraint was not being satisfied.
- Fixed a bug in the MouseJoint class where it wouldn't work if the body was FIXED_LINEAR_VELOCITY mass type.
- Fixed a bug in the World.getJoinedBodies and World.getInContactBodies methods to only return one instance of a joined/contact body if it was joined or in contact multiple times.

**Breaking Changes:**
- The World.remove(Body) no longer automatically notifies of destroyed contacts or joints. Use the World.remove(Body,boolean) method to receive notifications.
- The entire listener scheme has been revamped to allow multiple listeners of all types. There are new methods that replace all the old methods on the World class: World.addListener, World.removeListener, World.getListeners(class), etc.
- The Version class no longer has the version numbers public. Instead use the getXXX methods. This allows runtime version checking of the dyn4j jar.
- The Force.isComplete and Torque.isComplete methods have changed to add an elapsed time parameter. This allows better tracking of when a force/torque is completed.
