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
package org.drools.mas;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.drools.mas.body.content.Action;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.util.ACLMessageFactory;
import org.drools.mas.util.MessageContentFactory;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.*;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.drools.builder.ResourceType;
import org.drools.mas.body.acts.Failure;
import org.drools.mas.body.acts.Inform;
import org.drools.mas.util.MessageContentEncoder;
import org.drools.mas.util.ResourceDescriptorImpl;
import org.socraticgrid.lackey.fact.EndpointsConfiguration;
import org.w3c.dom.Document;

/**
 *
 * @author salaboy
 */
public class KnowledgeResourcesCompilationTest {
    private static Logger logger = LoggerFactory.getLogger(KnowledgeResourcesCompilationTest.class);
    private Server server;
    
    public static class FailureException extends Exception {

        public FailureException(String message) {
            super("FAILURE:" + message);
        }
    }
    
    public KnowledgeResourcesCompilationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
         DeleteDbFiles.execute("~", "mydb", false);

        logger.info("Staring DB for white pages ...");
        try {
            server = Server.createTcpServer(null).start();
        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        }
        logger.info("DB for white pages started! ");
    }

    @After
    public void tearDown() {
        
        logger.info("Stopping DB ...");
        server.stop();
        logger.info("DB Stopped!");
    }
    /*
     * Test for check that the resources provided inside this agent 
     * at least compile without errors. To ensure that the agent can be 
     * initialized correctly
     */
    @Test
    public void compilationTest() {
        
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        DroolsAgent agent = (DroolsAgent) context.getBean("agent");
        
        assertNotNull(agent);
        
        agent.dispose();
        
    }
    
    
    @Test
    public void lackeyAgentInformsOtherAgent() throws MalformedURLException, InterruptedException, FailureException {
        
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        DroolsAgent agent = (DroolsAgent) context.getBean("agent");
        
        assertNotNull(agent);
        
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
        ACLMessage inf = factory.newInformMessage("", "", resourceDescriptorImpl);
        agent.tell(inf);
        
        AgentID agentID = new AgentID("otherAgent");
        ACLMessage inf2 = factory.newInformMessage("", "", agentID);
        agent.tell(inf2);

        EndpointsConfiguration endpointsConfiguration = new EndpointsConfiguration();
        //we don't want to actually pull anything from guvnor. We are going
        //to provide the ResourceDescriptor by hand.
        endpointsConfiguration.setDataSource(null);
        //we are not going to configure agent's endpoint either because we
        //don't have any other agent running.
        ACLMessage inf3 = factory.newInformMessage("", "", endpointsConfiguration);
        agent.tell(inf3);
        
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("descriptor", resourceDescriptorImpl.getId());
        args.put("agentID", agentID.getName());
        Action action = MessageContentFactory.newActionContent("forceInform", args);
        ACLMessage req = factory.newRequestMessage("", "", action);

        agent.tell(req);

        waitForAnswers(agent, req.getId(), 2, 250, 50);
        
        List<ACLMessage> agentAnswers = agent.getAgentAnswers(req.getId());
        assertEquals(2, agentAnswers.size());
        assertEquals(Act.AGREE, agentAnswers.get(0).getPerformative() );
        assertEquals(Act.INFORM, agentAnswers.get(1).getPerformative() );
        
        System.out.println(" #### agentAnswers: "+agentAnswers);
        
        
        String xml = ret(agentAnswers.get(1));
        
        String aid = this.getValue(xml, "//aid");
        String resourceId = this.getValue(xml, "//resourceId");
        
        Assert.assertEquals(agentID.name, aid);
        Assert.assertEquals(resourceDescriptorImpl.getId(), resourceId);
        
        agent.dispose();
        
    }
    
    
    protected void waitForAnswers(DroolsAgent agent, String id, int expectedSize, long sleep, int maxIters) {
        int counter = 0;
        do {
            System.out.println("Answer for " + id + " is not ready, wait... ");
            try {
                Thread.sleep(sleep);
                counter++;
            } catch (InterruptedException e) {
            }
        } while (agent.peekAgentAnswers(id).size() < expectedSize && counter < maxIters);
        if (counter == maxIters) {
            fail("Timeout waiting for an answer to msg " + id);
        }

    }
    
    protected String ret(ACLMessage ans) throws FailureException {

        if (!ans.getPerformative().equals(Act.INFORM)) {
            logger.error("We were waiting for an INFORM but got this insted: \n " + ans);
        }

        if (ans.getPerformative().equals(Act.FAILURE)) {
            throw new FailureException(((Failure) ans.getBody()).getCause().getData().toString());
        }
        assertEquals(Act.INFORM, ans.getPerformative());
        MessageContentEncoder.decodeBody(ans.getBody(), Encodings.XML);
        return (String) ((Inform) ans.getBody()).getProposition().getData();

    }
    
    protected String getValue(String xml, String xpath) {
        try {
            Document dox = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
            XPath finder = XPathFactory.newInstance().newXPath();

            return (String) finder.evaluate(xpath, dox, XPathConstants.STRING);
        } catch (Exception e) {
            logger.error("Error while getting xml element's value", e);
            fail(e.getMessage());
        }
        return null;
    }
}
