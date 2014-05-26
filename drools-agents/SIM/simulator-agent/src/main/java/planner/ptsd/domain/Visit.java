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


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import planner.ptsd.domain.actors.Patient;
import planner.ptsd.domain.actors.Provider;
import planner.ptsd.domain.solver.ProviderStrengthComparator;
import planner.ptsd.domain.solver.VisitDateStrengthComparator;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity()
public class Visit implements Cloneable {


    private Patient patient;
    private int seqNum;
    private VisitType type;

    private int expectedWeek;
    private int heurExpectedWeek;


    private Provider provider;
    private VisitDate date;

    public Visit( Patient pat, int seq, int expectedWeek, VisitType type ) {
        this.patient = pat;
        this.seqNum = seq;
        this.expectedWeek = expectedWeek;
        this.heurExpectedWeek = expectedWeek > 0 ? expectedWeek : predict( seq, pat.getOnActiveCareFromWeek(), type );
        this.type = type;
    }

    private int predict( int seq, int from, VisitType type) {
        switch ( type ) {
            case THERAPY   : return from + seq;
            case GROUP     : return from + seq;
            case MEDICATION: return from + 4*seq;
            case COUNSELING: return from + 4*seq;
            default: return from + seq;
        }
    }


    public static List<VisitDate> temporalHorizon;


    public List<VisitDate> getHeurTemporalHorizon() {
        List<VisitDate> heur = new ArrayList<VisitDate>();
        for ( VisitDate v : temporalHorizon ) {
            if ( expectedWeek > 0 ) {
                if ( v.getWeekIndex() == expectedWeek ) {
                    heur.add( v );
                }
            } else {
                if ( Math.abs( v.getWeekIndex() - heurExpectedWeek ) < 5 ) {
                    heur.add( v );
                }
            }
        }
        return heur;
    }

    public Patient getPatient() {
        return patient;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public int getExpectedWeek() {
        return expectedWeek;
    }

    public VisitType getType() {
        return type;
    }

    @PlanningVariable( strengthComparatorClass = ProviderStrengthComparator.class )
    @ValueRange( type= ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "providers" )
    public Provider getProvider() {
        return provider;
    }

    @PlanningVariable( strengthComparatorClass = VisitDateStrengthComparator.class )
    @ValueRange( type= ValueRangeType.FROM_PLANNING_ENTITY_PROPERTY,  planningEntityProperty = "heurTemporalHorizon" )
    public VisitDate getDate() {
        return date;
    }


    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public void setDate(VisitDate date) {
        this.date = date;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public void setType(VisitType type) {
        this.type = type;
    }

    public void setExpectedWeek(int expectedWeek) {
        this.expectedWeek = expectedWeek;
    }

    public Visit clone() {
        Visit clone = new Visit( patient, seqNum, expectedWeek, type );
        clone.provider = provider;
        clone.date = date;
        return clone;
    }

    public boolean solutionEquals( Object other ) {
        if ( this == other ) {
            return true;
        } else if ( other instanceof Visit) {
            Visit otherVisit = (Visit) other;
            return new EqualsBuilder()
                    .append( patient, otherVisit.patient )
                    .append(seqNum, otherVisit.seqNum)
                    .append( expectedWeek, otherVisit.expectedWeek )
                    .append( type, otherVisit.type )
                    .append( provider, otherVisit.provider )
                    .append( date, otherVisit.date )
                    .isEquals();
        } else {
            return false;
        }
    }

    public int solutionHashCode() {
        return new HashCodeBuilder()
                .append(patient)
                .append(seqNum)
                .append(expectedWeek)
                .append(type)
                .append(provider)
                .append(date)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Visit{" +
                "patient=" + patient.getName() +
                ", seqNum=" + seqNum +
                ( type == VisitType.THERAPY  ? ", expWeek=" + expectedWeek : "" ) +
                ", type=" + type +
                ", provider=" + ( provider != null ? provider.getName() : null ) +
                ", date=" + date +
                '}';
    }
}
