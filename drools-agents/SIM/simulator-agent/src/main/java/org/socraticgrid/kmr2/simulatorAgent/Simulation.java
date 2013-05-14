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

public class Simulation implements Serializable {

    private String simulationId;
    private String status = "Running";

    private Date createdDate;
    private Long averageIterationTime;
    private String elapsedTime;
    private String timeUnit;

    private Double currentRelativeOevrallScore;
    private Integer currentIteration;
    private Double currentImprovement;
    private Double currentAbsoluteScore;

    private Configuration configuration;

    private List<ScoreDetails> scoreDetails = new ArrayList<ScoreDetails>();

    private List<ScoreStep> chartingData = new ArrayList<ScoreStep>();



    public Simulation() {
    }

    public Simulation(String simulationId) {
        this.simulationId = simulationId;
    }

    public String getSimulationId() {
        return simulationId;
    }

    public void setSimulationId(String simulationId) {
        this.simulationId = simulationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getAverageIterationTime() {
        return averageIterationTime;
    }

    public void setAverageIterationTime(Long averageIterationTime) {
        this.averageIterationTime = averageIterationTime;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Double getCurrentRelativeOevrallScore() {
        return currentRelativeOevrallScore;
    }

    public void setCurrentRelativeOevrallScore(Double currentRelativeOevrallScore) {
        this.currentRelativeOevrallScore = currentRelativeOevrallScore;
    }

    public Integer getCurrentIteration() {
        return currentIteration;
    }

    public void setCurrentIteration(Integer currentIteration) {
        this.currentIteration = currentIteration;
    }

    public Double getCurrentImprovement() {
        return currentImprovement;
    }

    public void setCurrentImprovement(Double currentImprovement) {
        this.currentImprovement = currentImprovement;
    }

    public Double getCurrentAbsoluteScore() {
        return currentAbsoluteScore;
    }

    public void setCurrentAbsoluteScore(Double currentAbsoluteScore) {
        this.currentAbsoluteScore = currentAbsoluteScore;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public List<ScoreDetails> getScoreDetails() {
        return scoreDetails;
    }

    public void setScoreDetails(List<ScoreDetails> scoreDetails) {
        this.scoreDetails = scoreDetails;
    }

    public List<ScoreStep> getChartingData() {
        return chartingData;
    }

    public void setChartingData(List<ScoreStep> chartingData) {
        this.chartingData = chartingData;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Simulation)) return false;

        Simulation that = (Simulation) o;

        if (simulationId != null ? !simulationId.equals(that.simulationId) : that.simulationId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return simulationId != null ? simulationId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Simulation{" +
                "simulationId='" + simulationId + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                ", averageIterationTime=" + averageIterationTime +
                ", elapsedTime='" + elapsedTime + '\'' +
                ", timeUnit='" + timeUnit + '\'' +
                ", currentRelativeOevrallScore=" + currentRelativeOevrallScore +
                ", currentIteration=" + currentIteration +
                ", currentImprovement=" + currentImprovement +
                ", currentAbsoluteScore=" + currentAbsoluteScore +
                ", configuration=" + configuration +
                ", scoreDetails=" + scoreDetails +
                ", chartingData=" + chartingData +
                '}';
    }




    public void start() {
        setStatus( "Running" );
        System.out.println( ">> STARTING SIM " + getSimulationId() );
    }

    public void pause() {
        setStatus( "Paused" );
        System.out.println( ">> PAUSING SIM " + getSimulationId() );
    }

    public void stop() {
        setStatus( "Stopped" );
        System.out.println( ">> STOPPING SIM " + getSimulationId() );
    }




    public static class ScoreStep implements Comparable<ScoreStep>, Serializable {
        private Integer step;
        private Double score;

        public ScoreStep(Integer step, Double score) {
            this.step = step;
            this.score = score;
        }

        public Integer getStep() {
            return step;
        }

        public void setStep(Integer step) {
            this.step = step;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public int compareTo(ScoreStep o) {
            return this.step - o.step;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ScoreStep)) return false;

            ScoreStep scoreStep = (ScoreStep) o;

            if (score != null ? !score.equals(scoreStep.score) : scoreStep.score != null) return false;
            if (step != null ? !step.equals(scoreStep.step) : scoreStep.step != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = step != null ? step.hashCode() : 0;
            result = 31 * result + (score != null ? score.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "ScoreStep{" +
                    "step=" + step +
                    ", score=" + score +
                    '}';
        }
    }


}

