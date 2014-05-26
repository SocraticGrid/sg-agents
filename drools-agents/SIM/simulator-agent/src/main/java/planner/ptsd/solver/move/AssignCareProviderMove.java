/*******************************************************************************
 *
 * Copyright (C) 2012 by Cognitive Medical Systems, Inc (http://www.cognitivemedciine.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy of 
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ******************************************************************************/
 
 /******************************************************************************
 * Socratic Grid contains components to which third party terms apply. To comply 
 * with these terms, the following notice is provided:
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 * Copyright (c) 2008, Nationwide Health Information Network (NHIN) Connect. All 
 * rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this 
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the name of the NHIN Connect Project nor the names of its 
 *   contributors may be used to endorse or promote products derived from this 
 *   software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * END OF TERMS AND CONDITIONS
 *
 ******************************************************************************/

package planner.ptsd.solver.move;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;
import planner.ptsd.domain.Visit;
import planner.ptsd.domain.VisitType;
import planner.ptsd.domain.actors.Patient;
import planner.ptsd.domain.actors.Provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AssignCareProviderMove implements Move {

    private List<Visit> visits;
    private Provider oldProvider;
    private Provider toProvider;
    private Patient patient;
    private VisitType type;

    public AssignCareProviderMove( List<Visit> visits, Provider oldProvider, Provider toProvider, Patient pat, VisitType type ) {
        this.visits = visits;
        this.toProvider = toProvider;
        this.oldProvider = oldProvider;
        this.patient = pat;
        this.type = type;

    }

    public boolean isMoveDoable( ScoreDirector scoreDirector ) {
        if ( visits.size() == 0 || oldProvider == null ) {
            return false;
        }
        for ( Visit v : visits ) {
            if ( ! v.getProvider().equals( oldProvider ) ) {
                return false;
            }
        }
        return true;
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new AssignCareProviderMove( visits, toProvider, oldProvider, patient, type );
    }

    public void doMove(ScoreDirector scoreDirector) {
        boolean fail = false;
        for ( Visit visit : visits ) {
            if ( ! visit.getProvider().equals( oldProvider ) ) {
//                System.out.println( "Trying to move from " + oldProvider + " to " + toProvider );
//                System.out.println( "But visit " + visit + " ...... " );
                fail = true;
            }
        }
        if ( fail ) {
            throw new RuntimeException( "WILL NOT BE ABLE TO UNDO THIS MOVE FOR" + visits);
        }


        for ( Visit visit : visits ) {
            scoreDirector.beforeVariableChanged(visit, "provider");
            visit.setProvider(toProvider);
            scoreDirector.afterVariableChanged(visit, "provider");
        }
    }

    public Collection<? extends Object> getPlanningEntities() {
        return new ArrayList( visits );
    }

    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList( toProvider );
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof AssignCareProviderMove) {
            AssignCareProviderMove other = (AssignCareProviderMove) o;
            return new EqualsBuilder()
                    .append(visits, other.visits)
                    .append(toProvider, other.toProvider)
                    .append(patient, other.patient)
                    .append(type, other.type)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(visits)
                .append(patient)
                .append(type)
                .append(toProvider)
                .toHashCode();
    }

    public String toString() {
        return visits + " => " + toProvider;
    }

}
