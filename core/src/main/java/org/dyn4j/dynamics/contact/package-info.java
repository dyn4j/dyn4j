/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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

/**
 * Sub package of the Dynamics package handling contacts.
 * <p>
 * Contacts are solved using an iterative constraint based approach.
 * <p>
 * The {@link org.dyn4j.dynamics.World} object will determine all collisions between the 
 * {@link org.dyn4j.dynamics.Body}s and will turn each 
 * {@link org.dyn4j.collision.manifold.Manifold} into a 
 * {@link org.dyn4j.dynamics.contact.ContactConstraint}.  Once all the 
 * {@link org.dyn4j.dynamics.contact.ContactConstraint}s have been gathered the 
 * {@link org.dyn4j.dynamics.World} object will perform a depth first search on the 
 * {@link org.dyn4j.dynamics.contact.ContactConstraint} graph to yield 
 * islands.  The islands will 
 * use the {@link org.dyn4j.dynamics.contact.WarmStartingContactManager} to warm start the 
 * {@link org.dyn4j.dynamics.contact.ContactConstraint}s and then use the 
 * {@link org.dyn4j.dynamics.contact.SequentialImpulses} to solve them.
 * <p>
 * The {@link org.dyn4j.dynamics.contact.ContactListener} can be used to receive 
 * notifications of sensed, begin, end, persisted, and pre/post solve events.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
package org.dyn4j.dynamics.contact;