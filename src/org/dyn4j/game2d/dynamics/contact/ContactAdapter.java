/*
 * Copyright (c) 2010, William Bittle
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
package org.dyn4j.game2d.dynamics.contact;

/**
 * Convenience class for implementing the {@link ContactListener} interface.
 * @author William Bittle
 */
public class ContactAdapter implements ContactListener {
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.contact.ContactListener#sensed(org.dyn4j.game2d.dynamics.contact.SensedContactPoint)
	 */
	@Override
	public void sensed(SensedContactPoint point) {}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.contact.ContactListener#begin(org.dyn4j.game2d.dynamics.contact.ContactPoint)
	 */
	@Override
	public boolean begin(ContactPoint point) { return true; }
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.contact.ContactListener#end(org.dyn4j.game2d.dynamics.contact.ContactPoint)
	 */
	@Override
	public boolean end(ContactPoint point) { return true; }
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.contact.ContactListener#persist(org.dyn4j.game2d.dynamics.contact.PersistedContactPoint)
	 */
	@Override
	public boolean persist(PersistedContactPoint point) { return true; }
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.contact.ContactListener#preSolve(org.dyn4j.game2d.dynamics.contact.ContactPoint)
	 */
	@Override
	public boolean preSolve(ContactPoint point) { return true; }

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.contact.ContactListener#postSolve(org.dyn4j.game2d.dynamics.contact.SolvedContactPoint)
	 */
	@Override
	public void postSolve(SolvedContactPoint point) {}
}
