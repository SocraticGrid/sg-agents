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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Configuration implements Serializable {

    private String modelId;
    
    private String configId;
    private String name;
    private String description;
    
    private Date createdDate;
    private Date lastRunDate;
    
    private String author;
    private String goal;
    
    private Date startDate;
    private Date endDate;
    private List<String> durations;
    private String duration;
    
    private Integer stopIteration;
    private Long stopTime;
    private String timeUnit;
    
    private Double stopScore;
    private Double stopImprovement;
    
    private List<Agent> agents = new ArrayList<Agent>();
    private List<ConstraintPackage> constraints = new ArrayList<ConstraintPackage>();


    public Configuration() {
    }

    public Configuration(String configId) {
        this.configId = configId;
    }

    public Configuration( String configId, String modelId) {
        this.modelId = modelId;
        this.configId = configId;
    }

    public Configuration(String configId, String modelId, String name, String description, Date createdDate, Date lastRunDate, String author, String goal, Date startDate, Date endDate, List<String> durations, String duration, Integer stopIteration, Long stopTime, String timeUnit, Double stopScore, Double stopImprovement, List<Agent> agents, List<ConstraintPackage> constraints) {
        this.configId = configId;
        this.modelId = modelId;
        this.name = name;
        this.description = description;
        this.createdDate = createdDate;
        this.lastRunDate = lastRunDate;
        this.author = author;
        this.goal = goal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durations = durations;
        this.duration = duration;
        this.stopIteration = stopIteration;
        this.stopTime = stopTime;
        this.timeUnit = timeUnit;
        this.stopScore = stopScore;
        this.stopImprovement = stopImprovement;
        this.agents = agents;
        this.constraints = constraints;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastRunDate() {
        return lastRunDate;
    }

    public void setLastRunDate(Date lastRunDate) {
        this.lastRunDate = lastRunDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<String> getDurations() {
        return durations;
    }

    public void setDurations(List<String> durations) {
        this.durations = durations;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getStopIteration() {
        return stopIteration;
    }

    public void setStopIteration(Integer stopIteration) {
        this.stopIteration = stopIteration;
    }

    public Long getStopTime() {
        return stopTime;
    }

    public void setStopTime(Long stopTime) {
        this.stopTime = stopTime;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Double getStopScore() {
        return stopScore;
    }

    public void setStopScore(Double stopScore) {
        this.stopScore = stopScore;
    }

    public Double getStopImprovement() {
        return stopImprovement;
    }

    public void setStopImprovement(Double stopImprovement) {
        this.stopImprovement = stopImprovement;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }

    public List<ConstraintPackage> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<ConstraintPackage> constraints) {
        this.constraints = constraints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Configuration)) return false;

        Configuration that = (Configuration) o;

        if (configId != null ? !configId.equals(that.configId) : that.configId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return configId != null ? configId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "modelId='" + modelId + '\'' +
                ", configId='" + configId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdDate=" + createdDate +
                ", lastRunDate=" + lastRunDate +
                ", author='" + author + '\'' +
                ", goal='" + goal + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", durations=" + durations +
                ", duration='" + duration + '\'' +
                ", stopIteration=" + stopIteration +
                ", stopTime=" + stopTime +
                ", timeUnit='" + timeUnit + '\'' +
                ", stopScore=" + stopScore +
                ", stopImprovement=" + stopImprovement +
                ", agents=" + agents +
                ", constraints=" + constraints +
                '}';
    }
}
