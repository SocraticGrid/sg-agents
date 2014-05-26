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
package planner.ptsd.domain.actors;


import planner.ptsd.PTSDProblemFactory;
import planner.ptsd.domain.PTSDRoster;
import planner.ptsd.domain.Severity;

public class Patient {

    private String id;
    private String name;

    private boolean onTheraphy = false;
    private boolean onMedication = false;
    private boolean onGroup = false;
    private boolean onSocial = false;

    private int onActiveCareFromWeek;

    private Therapist personalTherapist;
    private Psychiatrist personalMedic;
    private SocialWorker personalAssistant;
    private Therapist personalTeamLeader;

    private Severity severity = Severity.NONE;

    public Patient( String id, String name, Severity severity, int careWeek ) {
        this.id = id;
        this.name = name;
        this.severity = severity;

        onTheraphy = severity != Severity.NONE;
        onActiveCareFromWeek = careWeek;

        if ( onTheraphy ) {
            onSocial = true;
            onGroup = PTSDProblemFactory.onGroup( severity );
            onMedication = PTSDProblemFactory.onMedication( severity );
        }
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", onTheraphy=" + onTheraphy +
                ", onMedication=" + onMedication +
                ", onGroup=" + onGroup +
                ", onSocial=" + onSocial +
                ", onActiveCareFromWeek=" + onActiveCareFromWeek +
                ", personalTherapist=" + personalTherapist +
                ", personalMedic=" + personalMedic +
                ", personalAssistant=" + personalAssistant +
                ", personalTeamLeader=" + personalTeamLeader +
                ", severity=" + severity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Patient patient = (Patient) o;

        if (id != null ? !id.equals(patient.id) : patient.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }








    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnTheraphy() {
        return onTheraphy;
    }

    public void setOnTheraphy(boolean onTheraphy) {
        this.onTheraphy = onTheraphy;
    }

    public boolean isOnMedication() {
        return onMedication;
    }

    public void setOnMedication(boolean onMedication) {
        this.onMedication = onMedication;
    }

    public boolean isOnGroup() {
        return onGroup;
    }

    public void setOnGroup(boolean onGroup) {
        this.onGroup = onGroup;
    }

    public boolean isOnSocial() {
        return onSocial;
    }

    public void setOnSocial(boolean onSocial) {
        this.onSocial = onSocial;
    }

    public int getOnActiveCareFromWeek() {
        return onActiveCareFromWeek;
    }

    public void setOnActiveCareFromWeek(int onActiveCareFromWeek) {
        this.onActiveCareFromWeek = onActiveCareFromWeek;
    }

    public int getPlanCompletionWeekIndex() {
        switch ( severity ) {
            case MILD   : return onActiveCareFromWeek + 14;
            case MEDIUM : return onActiveCareFromWeek + 25;
            case SEVERE : return onActiveCareFromWeek + 29;
            default     : return onActiveCareFromWeek;
        }
    }

    public Therapist getPersonalTherapist() {
        return personalTherapist;
    }

    public void setPersonalTherapist(Therapist personalTherapist) {
        this.personalTherapist = personalTherapist;
    }

    public Psychiatrist getPersonalMedic() {
        return personalMedic;
    }

    public void setPersonalMedic(Psychiatrist personalMedic) {
        this.personalMedic = personalMedic;
    }

    public SocialWorker getPersonalAssistant() {
        return personalAssistant;
    }

    public void setPersonalAssistant(SocialWorker personalAssistant) {
        this.personalAssistant = personalAssistant;
    }

    public Therapist getPersonalTeamLeader() {
        return personalTeamLeader;
    }

    public void setPersonalTeamLeader(Therapist personalTeamLeader) {
        this.personalTeamLeader = personalTeamLeader;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
}
