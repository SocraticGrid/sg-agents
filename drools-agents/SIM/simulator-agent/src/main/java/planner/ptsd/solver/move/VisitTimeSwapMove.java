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
import org.drools.planner.examples.nurserostering.domain.Employee;
import org.drools.planner.examples.nurserostering.domain.ShiftAssignment;
import org.drools.planner.examples.nurserostering.solver.move.NurseRosteringMoveHelper;
import planner.ptsd.domain.Visit;
import planner.ptsd.domain.VisitDate;

import java.util.Arrays;
import java.util.Collection;

public class VisitTimeSwapMove implements Move {

    private Visit leftVisit;
    private Visit rightVisit;

    public VisitTimeSwapMove( Visit l, Visit r ) {
        this.leftVisit = l;
        this.rightVisit = r;
    }

    public boolean isMoveDoable( ScoreDirector scoreDirector ) {
        return leftVisit.getProvider().getId().equals( rightVisit.getProvider().getId() )
               && leftVisit.getDate().getWeekIndex() == rightVisit.getDate().getWeekIndex();
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new VisitTimeSwapMove( rightVisit, leftVisit );
    }

    public void doMove(ScoreDirector scoreDirector) {
        VisitDate oldLeft = leftVisit.getDate();
        VisitDate oldRight = rightVisit.getDate();
        scoreDirector.beforeVariableChanged( leftVisit, "date" );
        scoreDirector.beforeVariableChanged( rightVisit, "date" );
        leftVisit.setDate( oldRight );
        rightVisit.setDate( oldLeft );
        scoreDirector.afterVariableChanged( leftVisit, "date" );
        scoreDirector.afterVariableChanged( rightVisit, "date" );
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList( leftVisit, rightVisit );
    }

    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList( leftVisit.getDate(), rightVisit.getDate() );
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof VisitTimeSwapMove) {
            VisitTimeSwapMove other = (VisitTimeSwapMove) o;
            return new EqualsBuilder()
                    .append(leftVisit, other.leftVisit)
                    .append(rightVisit, other.rightVisit)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftVisit)
                .append(rightVisit)
                .toHashCode();
    }

    public String toString() {
        return leftVisit + " <=> " + rightVisit;
    }

}
