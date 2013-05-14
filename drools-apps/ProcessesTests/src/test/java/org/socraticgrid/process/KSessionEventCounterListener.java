/***********************************************************************************************************************
 *
 * Copyright (C) 2012 by Cognitive Medical Systems, Inc (http://www.cognitivemedciine.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/
 
 /***********************************************************************************************************************
 * Socratic Grid contains components to which third party terms apply. To comply with these terms, the following notice is provided:
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 * Copyright (c) 2008, Nationwide Health Information Network (NHIN) Connect. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of the NHIN Connect Project nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * END OF TERMS AND CONDITIONS
 *
 **********************************************************************************************************************/
package org.socraticgrid.process;

import java.util.HashMap;
import java.util.Map;
import org.drools.event.KnowledgeRuntimeEvent;
import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.AgendaGroupPoppedEvent;
import org.drools.event.rule.AgendaGroupPushedEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.drools.event.rule.RuleFlowGroupActivatedEvent;
import org.drools.event.rule.RuleFlowGroupDeactivatedEvent;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 *
 * @author esteban
 */
public class KSessionEventCounterListener implements ProcessEventListener, AgendaEventListener, WorkingMemoryEventListener{
    
    private Map<String, Integer> eventCounter = new HashMap<String, Integer>();
    private boolean debugEnabled;
    
    public KSessionEventCounterListener(StatefulKnowledgeSession session){
        session.addEventListener((ProcessEventListener)this);
        session.addEventListener((AgendaEventListener)this);
        session.addEventListener((WorkingMemoryEventListener)this);
    }
    
    public synchronized int getEventCount(String eventName){
        Integer eventCount = eventCounter.get(eventName);
        return (eventCount == null)?0:eventCount;
    }
    
    public synchronized void reset(String eventName){
        this.eventCounter.put(eventName, 0);
    }
    
    public synchronized void reset(){
        this.eventCounter = new HashMap<String, Integer>();
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }
    
    private synchronized void addEvent(String eventName, KnowledgeRuntimeEvent event){
        Integer eventCount = eventCounter.get(eventName);
        if (eventCount == null){
            eventCount = 0;
        }
        eventCount++;
        if (debugEnabled){
            System.out.println(eventName+"("+event+")= "+eventCount);
        }
        eventCounter.put(eventName, eventCount);
        
    }

    public void beforeProcessStarted(ProcessStartedEvent event) {
        this.addEvent("BeforeProcessStartedEvent", event);
    }

    public void afterProcessStarted(ProcessStartedEvent event) {
        this.addEvent("AfterProcessStartedEvent", event);
    }

    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        this.addEvent("BeforeProcessCompletedEvent", event);
    }

    public void afterProcessCompleted(ProcessCompletedEvent event) {
        this.addEvent("AfterProcessCompletedEvent", event);
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        this.addEvent("BeforeProcessNodeTriggeredEvent", event);
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        this.addEvent("AfterProcessNodeTriggeredEvent", event);
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        this.addEvent("BeforeProcessNodeLeftEvent", event);
    }

    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        this.addEvent("AfterProcessNodeLeftEvent", event);
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        this.addEvent("BeforeProcessVariableChangedEvent", event);
    }

    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        this.addEvent("AfterProcessVariableChangedEvent", event);
    }

    public void activationCreated(ActivationCreatedEvent event) {
        this.addEvent("ActivationCreatedEvent", event);
    }

    public void activationCancelled(ActivationCancelledEvent event) {
        this.addEvent("ActivationCancelledEvent", event);
    }

    public void beforeActivationFired(BeforeActivationFiredEvent event) {
        this.addEvent("BeforeActivationFiredEvent", event);
    }

    public void afterActivationFired(AfterActivationFiredEvent event) {
        this.addEvent("AfterActivationFiredEvent", event);
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        this.addEvent("AgendaGroupPoppedEvent", event);
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        this.addEvent("AgendaGroupPushedEvent", event);
    }

    public void objectInserted(ObjectInsertedEvent event) {
        this.addEvent("ObjectInsertedEvent", event);
    }

    public void objectUpdated(ObjectUpdatedEvent event) {
        this.addEvent("ObjectUpdatedEvent", event);
    }

    public void objectRetracted(ObjectRetractedEvent event) {
        this.addEvent("ObjectRetractedEvent", event);
    }

    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent arg0) {
    }

    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent arg0) {
    }

    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent arg0) {
    }

    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent arg0) {
    }
    
}
