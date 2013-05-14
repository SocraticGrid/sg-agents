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
import java.util.Date;

public class ResultInfo implements Serializable {

    private String resutlId;
    private String modelId;
    private String configId;
    
    private Date runDate;
    private Date resultDate;
    
    private Integer cases;

    private Long simulationDuration;
    private String timeUnit;
    private Double score;
    private String stopCondition;

    private String author;
    private String name;

    private Date planStartDate;
    private Long planScope;
    private String description;

    public ResultInfo() {
    }

    public ResultInfo(String resutlId) {
        this.resutlId = resutlId;
    }

    public ResultInfo(String resutlId, String modelId) {
        this.resutlId = resutlId;
        this.modelId = modelId;
    }

    public ResultInfo(String resutlId, String modelId, String configId) {
        this.resutlId = resutlId;
        this.modelId = modelId;
        this.configId = configId;
    }

    public ResultInfo(String resutlId, String modelId, String configId, Date runDate, Date resultDate, Integer cases, Long simulationDuration, String timeUnit, Double score, String stopCondition, String author, String name, Date planStartDate, Long planScope, String description) {
        this.resutlId = resutlId;
        this.modelId = modelId;
        this.configId = configId;
        this.runDate = runDate;
        this.resultDate = resultDate;
        this.cases = cases;
        this.simulationDuration = simulationDuration;
        this.timeUnit = timeUnit;
        this.score = score;
        this.stopCondition = stopCondition;
        this.author = author;
        this.name = name;
        this.planStartDate = planStartDate;
        this.planScope = planScope;
        this.description = description;
    }

    public String getResutlId() {
        return resutlId;
    }

    public void setResutlId(String resutlId) {
        this.resutlId = resutlId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public Date getRunDate() {
        return runDate;
    }

    public void setRunDate(Date runDate) {
        this.runDate = runDate;
    }

    public Date getResultDate() {
        return resultDate;
    }

    public void setResultDate(Date resultDate) {
        this.resultDate = resultDate;
    }

    public Integer getCases() {
        return cases;
    }

    public void setCases(Integer cases) {
        this.cases = cases;
    }

    public Long getSimulationDuration() {
        return simulationDuration;
    }

    public void setSimulationDuration(Long simulationDuration) {
        this.simulationDuration = simulationDuration;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getStopCondition() {
        return stopCondition;
    }

    public void setStopCondition(String stopCondition) {
        this.stopCondition = stopCondition;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getPlanStartDate() {
        return planStartDate;
    }

    public void setPlanStartDate(Date planStartDate) {
        this.planStartDate = planStartDate;
    }

    public Long getPlanScope() {
        return planScope;
    }

    public void setPlanScope(Long planScope) {
        this.planScope = planScope;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultInfo)) return false;

        ResultInfo that = (ResultInfo) o;

        if (resutlId != null ? !resutlId.equals(that.resutlId) : that.resutlId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return resutlId != null ? resutlId.hashCode() : 0;
    }


    @Override
    public String toString() {
        return "ResultInfo{" +
                "resutlId='" + resutlId + '\'' +
                ", modelId='" + modelId + '\'' +
                ", configId='" + configId + '\'' +
                ", runDate=" + runDate +
                ", resultDate=" + resultDate +
                ", cases=" + cases +
                ", simulationDuration=" + simulationDuration +
                ", timeUnit='" + timeUnit + '\'' +
                ", score=" + score +
                ", stopCondition='" + stopCondition + '\'' +
                ", author='" + author + '\'' +
                ", name='" + name + '\'' +
                ", planStartDate=" + planStartDate +
                ", planScope=" + planScope +
                ", description='" + description + '\'' +
                '}';
    }
}
