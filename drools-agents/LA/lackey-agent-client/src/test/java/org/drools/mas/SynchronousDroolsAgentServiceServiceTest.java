/**
 * *****************************************************************************
 *
 * Copyright (C) 2012 by Cognitive Medical Systems, Inc
 * (http://www.cognitivemedciine.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *****************************************************************************
 */
/**
 * ****************************************************************************
 * Socratic Grid contains components to which third party terms apply. To comply
 * with these terms, the following notice is provided:
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION Copyright (c)
 * 2008, Nationwide Health Information Network (NHIN) Connect. All rights
 * reserved. Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the NHIN Connect Project nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
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
 *****************************************************************************
 */
package org.drools.mas;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.builder.ResourceType;
import org.drools.mas.mock.MockFact;
import org.drools.mas.helpers.DialogueHelper;
import org.drools.mas.util.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.drools.grid.api.impl.*;
import org.drools.mas.helpers.DialogueHelperCallbackImpl;

/**
 *
 * @author salaboy
 */
public class SynchronousDroolsAgentServiceServiceTest {
    
    private static final Logger LOG = Logger.getLogger(SynchronousDroolsAgentServiceServiceTest.class.getName());

    public SynchronousDroolsAgentServiceServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSimpleInformWithHelper() throws InterruptedException {
        DialogueHelper agentHelper = new DialogueHelper("http://localhost:8080/lackey-agent/services/AsyncAgentService?wsdl");

        MockFact fact = new MockFact("patient1", 18);



        final CountDownLatch latch = new CountDownLatch(1);
        final List<ACLMessage> results = new ArrayList<ACLMessage>();
        agentHelper.invokeInform("me", "you", fact, new DialogueHelperCallbackImpl() {
            
            @Override
            public void onSuccess(List<ACLMessage> list) {
                results.addAll(list);
                latch.countDown();
            }

            public void onError(Throwable thrwbl) {
                LOG.log(Level.SEVERE, "Error during agent invocation", thrwbl);
                latch.countDown();
            }

            @Override
            public long getTimeoutForResponses() {
                return 5000;
            }
            
            
            
        });

        latch.await();

        assertEquals(1, results.size());

    }

    @Test
    public void testLackeyAndActionAgentInAction() throws InterruptedException, MalformedURLException {
        
        DialogueHelper agentHelper = new DialogueHelper("http://localhost:8080/lackey-agent/services/AsyncAgentService?wsdl");




        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
        ResourceDescriptorImpl resourceDescriptorImpl = new ResourceDescriptorImpl();
        resourceDescriptorImpl.setId("1");
        resourceDescriptorImpl.setAuthor("salaboy");
        resourceDescriptorImpl.setName("my resource");
        resourceDescriptorImpl.setStatus("draft");
        resourceDescriptorImpl.setVersion("1");
        resourceDescriptorImpl.setCreationTime(new Date());
        resourceDescriptorImpl.setType(ResourceType.DRL);
        resourceDescriptorImpl.setDescription("this is my resource description");
        resourceDescriptorImpl.setResourceURL(new URL("file:/Users/salaboy/myports.txt"));
        LinkedHashMap<String, Object> args = new LinkedHashMap<String, Object>();

        args.put("descriptor", resourceDescriptorImpl.getId());

        AgentID agentID = new AgentID();
        agentID.setName("otherAgent");
        args.put("agentID", agentID.getName());



        agentHelper.invokeInform("", "", resourceDescriptorImpl, null);


        agentHelper.invokeInform("", "", agentID, null);


        final CountDownLatch latch = new CountDownLatch(1);
        final List<ACLMessage> agentAnswers = new ArrayList<ACLMessage>();
        String reqId = agentHelper.invokeRequest("forceInform", args, new DialogueHelperCallbackImpl() {
            
            @Override
            public void onSuccess(List<ACLMessage> list) {
                agentAnswers.addAll(list);
                latch.countDown();
            }

            public void onError(Throwable thrwbl) {
                LOG.log(Level.SEVERE, "Error during agent invocation", thrwbl);
                latch.countDown();
            }

            @Override
            public int getExpectedResponsesNumber() {
                return 2;
            }
            
            
            
        });

        latch.await();
        
        System.out.println(" >>>>>>>>>>>>> Answers = " + agentAnswers);
        assertEquals(2, agentAnswers.size());
        assertEquals(Act.AGREE, agentAnswers.get(0).getPerformative());
        assertEquals(Act.INFORM, agentAnswers.get(1).getPerformative());

        System.out.println(" #### agentAnswers: " + agentAnswers);



    }
}
