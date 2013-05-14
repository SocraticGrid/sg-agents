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
package org.socraticgrid.kmr2.simulatorAgent;


import java.io.Serializable;

public class ScoreDetails implements Serializable {

    private String constraintId;
    private String type;
    private String description;

    private Integer violations;
    private Integer violationLimit;

    private String penalties;
    private String relevance;

    private String aggregateScore;
    private String constraintScore;

    public ScoreDetails() {
    }

    public ScoreDetails(String constraintId) {
        this.constraintId = constraintId;
    }

    public ScoreDetails(String constraintId, String type, String description, Integer violations, Integer violationLimit, String penalties, String relevance, String aggregateScore, String constraintScore) {
        this.constraintId = constraintId;
        this.type = type;
        this.description = description;
        this.violations = violations;
        this.violationLimit = violationLimit;
        this.penalties = penalties;
        this.relevance = relevance;
        this.aggregateScore = aggregateScore;
        this.constraintScore = constraintScore;
    }

    public String getConstraintId() {
        return constraintId;
    }

    public void setConstraintId(String constraintId) {
        this.constraintId = constraintId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getViolations() {
        return violations;
    }

    public void setViolations(Integer violations) {
        this.violations = violations;
    }

    public Integer getViolationLimit() {
        return violationLimit;
    }

    public void setViolationLimit(Integer violationLimit) {
        this.violationLimit = violationLimit;
    }

    public String getPenalties() {
        return penalties;
    }

    public void setPenalties(String penalties) {
        this.penalties = penalties;
    }

    public String getRelevance() {
        return relevance;
    }

    public void setRelevance(String relevance) {
        this.relevance = relevance;
    }

    public String getAggregateScore() {
        return aggregateScore;
    }

    public void setAggregateScore(String aggregateScore) {
        this.aggregateScore = aggregateScore;
    }

    public String getConstraintScore() {
        return constraintScore;
    }

    public void setConstraintScore(String constraintScore) {
        this.constraintScore = constraintScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScoreDetails)) return false;

        ScoreDetails that = (ScoreDetails) o;

        if (constraintId != null ? !constraintId.equals(that.constraintId) : that.constraintId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return constraintId != null ? constraintId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ScoreDetails{" +
                "constraintId='" + constraintId + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", violations=" + violations +
                ", violationLimit=" + violationLimit +
                ", penalties='" + penalties + '\'' +
                ", relevance='" + relevance + '\'' +
                ", aggregateScore='" + aggregateScore + '\'' +
                ", constraintScore='" + constraintScore + '\'' +
                '}';
    }
}

