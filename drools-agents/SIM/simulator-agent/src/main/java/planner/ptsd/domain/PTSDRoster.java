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

package planner.ptsd.domain;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.api.domain.solution.PlanningEntityCollectionProperty;
import org.drools.planner.core.score.buildin.hardandsoft.HardAndSoftScore;
import org.drools.planner.core.solution.Solution;
import planner.ptsd.domain.actors.Patient;
import planner.ptsd.domain.actors.Provider;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class PTSDRoster implements Solution<HardAndSoftScore> {

    private String id;

    private List<Patient> patients;
    private List<Provider> providers;
    private List<VisitDate> temporalHorizon;

    private List<Visit> visits;

    private HardAndSoftScore score;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public void setProviders(List<Provider> providers) {
        this.providers = providers;
    }

    @PlanningEntityCollectionProperty
    public List<Visit> getVisits() {
        return visits;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }

    public HardAndSoftScore getScore() {
        return score;
    }

    public void setScore(HardAndSoftScore score) {
        this.score = score;
    }

    public List<VisitDate> getTemporalHorizon() {
        return temporalHorizon;
    }

    public void setTemporalHorizon(List<VisitDate> temporalHorizon) {
        this.temporalHorizon = temporalHorizon;
    }

    // ************************************************************************
    // ToString
    // ************************************************************************

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( " PTSD Care plan " + id + "  >>> " + score ).append( "\n\n" );
        builder.append( "--- Patient list : -------------------------------------------------------------" ).append( "\n\n" );
        for ( Patient p : patients ) {
            builder.append( p.toString() ).append( "\n" );
        }
        builder.append( "--- Provider list : -------------------------------------------------------------" ).append( "\n\n" );
        for ( Provider p : providers ) {
            builder.append( p.toString() ).append( "\n" );
        }
        builder.append( "--- Schedules ------------------------------------------------------------------" ).append( "\n\n" );
        for ( Visit v : visits ) {
            builder.append( v.toString() ).append( "\n" );
        }
//        builder.append( "--- Dates ------------------------------------------------------------------" ).append( "\n\n" );
//        for ( VisitDate d : temporalHorizon ) {
//            builder.append( d.toString() ).append( "\n" );
//        }
        builder.append( "--------------------------------------------------------------------------------" ).append( "\n\n" );
        return builder.toString();
    }


    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();

        facts.addAll(patients);
        facts.addAll(providers);
        facts.addAll(temporalHorizon);
        // Do not add the planning entity's (shiftAssignmentList) because that will be done automatically
        return facts;
    }

    /**
     * Clone will only deep copy the {@link #visits}.
     */
    public PTSDRoster cloneSolution() {
        PTSDRoster clone = new PTSDRoster();
        clone.id = id;
        clone.patients = patients;
        clone.providers = providers;
        clone.temporalHorizon = temporalHorizon;

        List<Visit> clonedVisitList = new ArrayList<Visit>( visits.size() );
        for ( Visit visit : visits ) {
            Visit clonedVisit = visit.clone();
            clonedVisitList.add(clonedVisit);
        }
        clone.visits = clonedVisitList;
        clone.score = score;
        return clone;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ( id == null || !( o instanceof PTSDRoster ) ) {
            return false;
        } else {
            PTSDRoster other = (PTSDRoster) o;
            if ( visits.size() != other.visits.size() ) {
                return false;
            }
            for ( Iterator<Visit> it = visits.iterator(), otherIt = other.visits.iterator(); it.hasNext(); ) {
                Visit visit = it.next();
                Visit otherVisit = otherIt.next();
                // Notice: we don't use equals()
                if ( ! visit.solutionEquals( otherVisit ) ) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for ( Visit visit : visits ) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append( visit.solutionHashCode() );
        }
        return hashCodeBuilder.toHashCode();
    }

}
