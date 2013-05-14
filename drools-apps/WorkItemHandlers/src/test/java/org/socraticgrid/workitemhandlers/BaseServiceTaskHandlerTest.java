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

import org.socraticgrid.workitemhandlers.BaseServiceTaskHandler;
import org.socraticgrid.workitemhandlers.mock.SynchronousDialogueHelperMock;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.builder.conf.DeclarativeAgendaOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class BaseServiceTaskHandlerTest {
    
    public BaseServiceTaskHandlerTest() {
    
    }
    
    public final static String ACTION_AGENT_END_POINT_URL = "http://localhost:8081/action-agent/services/AsyncAgentService?WSDL";
    
    protected StatefulKnowledgeSession ksession;
    
    
    private SynchronousDialogueHelperMock synchronousRequestHelperMock;
    
    @Before
    public void setUp(){
        ksession = this.createDefaultSession();
    }
    

    @Test
    public void testBaseServiceTaskHandler() {
    
        synchronousRequestHelperMock = new SynchronousDialogueHelperMock(ACTION_AGENT_END_POINT_URL);
        ksession.getWorkItemManager().registerWorkItemHandler("Email", new BaseServiceTaskHandler(synchronousRequestHelperMock){

            @Override
            protected String getChannel(Map map) {
                return "Email";
            }

            @Override
            protected String buildBody(WorkItem workItem, Map map) {
                return "This is the body";
            }
            
        });
        
        ksession.startProcess("emailTestProcess");
        
        //1 request
        List<LinkedHashMap<String, Object>> requests = synchronousRequestHelperMock.getRequests("deliverMessage");
        
        Assert.assertEquals(1, requests.size());
        
        Assert.assertEquals("This is the header", requests.get(0).get("Header"));
        
    }
    
    private StatefulKnowledgeSession createDefaultSession(){
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add(ResourceFactory.newClassPathResource("processes/emailTestProcess.bpmn2"), ResourceType.BPMN2);

        if (kbuilder.hasErrors()){
            Iterator<KnowledgeBuilderError> iterator = kbuilder.getErrors().iterator();
            while (iterator.hasNext()) {
                KnowledgeBuilderError knowledgeBuilderError = iterator.next();
                System.out.println("Error: "+knowledgeBuilderError.getMessage());
            }
            throw new IllegalStateException("Compilation Errors");
        }
        
        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( DeclarativeAgendaOption.ENABLED );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        return ksession;
    }
}
