/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
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
 * Contact between bodies are tracked and solved via 
 * {@link org.dyn4j.dynamics.contact.ContactConstraint}s. A contact constraint can
 * have one or more {@link org.dyn4j.dynamics.contact.Contact}s.  The
 * {@link org.dyn4j.dynamics.contact.ContactConstraint}s are then used in a
 * {@link org.dyn4j.dynamics.contact.ContactConstraintSolver} to resolve the
 * collisions in a physical way. 
 * <p>
 * {@link org.dyn4j.dynamics.contact.SequentialImpulses} is the only implementation
 * of the {@link org.dyn4j.dynamics.contact.ContactConstraintSolver}.
 * <p>
 * In addition to solving the {@link org.dyn4j.dynamics.contact.ContactConstraint}s, 
 * they can be updated to facilitate warm starting the solver.  Warm starting provides
 * a way to jump start the solving with last frame's solution to decrease the time
 * required to achieve solution convergence.
 * <p> 
 * During the update process, notifications of begin, end, persisted 
 * {@link org.dyn4j.dynamics.contact.Contact}s
 * are generated through the {@link org.dyn4j.dynamics.contact.ContactUpdateHandler}.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 */
package org.dyn4j.dynamics.contact;