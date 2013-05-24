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
package org.socraticgrid.workitemhandlers;

import org.socraticgrid.workitemhandlers.BaseCommunicationServiceTaskHandler;
import org.drools.mas.action.communication.CommunicationHandlerConfiguration;
import java.util.Iterator;
import org.drools.ClockType;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.AssertBehaviorOption;
import org.drools.conf.EventProcessingOption;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class BaseCommunicationServiceTaskHandlerTest {
    
    protected StatefulKnowledgeSession ksession;
    
    @Before
    public void setUp(){
        ksession = this.createDefaultSession();
    }
    
    @Test
    public void testCommunication() {

        CommunicationHandlerConfiguration globalConfiguration = new CommunicationHandlerConfiguration();
        
        ksession.getWorkItemManager().registerWorkItemHandler("CommunicationTask", new BaseCommunicationServiceTaskHandler(ksession));
        
        ksession.setGlobal("globalConfiguration", globalConfiguration);
        
        ksession.startProcess("com.sample.bpmm2");
        
        ksession.fireAllRules();
        
        Assert.assertArrayEquals(new String[]{"actor1", "actor2"}, globalConfiguration.getReceivers().toArray());
        Assert.assertArrayEquals(new String[]{"ALERT", "ALERT"}, globalConfiguration.getChannels().toArray());
        Assert.assertArrayEquals(new String[]{"template1", "template2"}, globalConfiguration.getTemplates().toArray());
        Assert.assertArrayEquals( new String[]{"0s", "120s"}, globalConfiguration.getTimeouts().toArray());
    }
    
    private StatefulKnowledgeSession createDefaultSession(){
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        //mandatory DRL files
        kbuilder.add(ResourceFactory.newClassPathResource("org/drools/informer/informer-changeset.xml"), ResourceType.CHANGE_SET);
        kbuilder.add(ResourceFactory.newClassPathResource("org/drools/informer/interaction/interaction-changeset.xml"), ResourceType.CHANGE_SET);
        kbuilder.add(ResourceFactory.newClassPathResource("rules/WorkItemHandlers.drl"), ResourceType.DRL);
        
        //test process and rules
        kbuilder.add(ResourceFactory.newClassPathResource("rules/WorkItemHandlersTest.drl"), ResourceType.DRL);
        kbuilder.add(ResourceFactory.newClassPathResource("processes/communicationAlertProcess.bpmn2"), ResourceType.BPMN2);

        if (kbuilder.hasErrors()){
            Iterator<KnowledgeBuilderError> iterator = kbuilder.getErrors().iterator();
            while (iterator.hasNext()) {
                KnowledgeBuilderError knowledgeBuilderError = iterator.next();
                System.out.println("Error: "+knowledgeBuilderError.getMessage());
            }
            throw new IllegalStateException("Compilation Errors");
        }
        
        KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( AssertBehaviorOption.EQUALITY );
        kbconf.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase( kbconf );
        kBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setProperty(ClockTypeOption.PROPERTY_NAME, ClockType.REALTIME_CLOCK.toExternalForm());
        KnowledgeSessionConfiguration ksessionConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksession = kBase.newStatefulKnowledgeSession( ksessionConf, null );
        
        ksession.addEventListener(new DebugAgendaEventListener(System.out));
        
        return ksession;
    }
}
