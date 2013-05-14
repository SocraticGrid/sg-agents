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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.drools.mas.body.acts.Inform;
import org.drools.mas.body.content.Action;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.test.MockFact;
import org.drools.mas.util.ACLMessageFactory;
import org.drools.mas.util.MessageContentEncoder;
import org.drools.mas.util.MessageContentFactory;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.*;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author salaboy
 */
public class KnowledgeResourcesCompilationTest {
    private static Logger logger = LoggerFactory.getLogger(KnowledgeResourcesCompilationTest.class);
    private Server server;
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
    public void tearDown() throws InterruptedException {

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

        ApplicationContext context = new ClassPathXmlApplicationContext("test-applicationContext.xml");
        DroolsAgent agent = (DroolsAgent) context.getBean("agent");

        assertNotNull(agent);

        agent.dispose();

    }
    
    
    private List<ACLMessage> waitForResponse( DroolsAgent agent, String id, int numAns ) {
        List<ACLMessage> responses = new ArrayList<ACLMessage>( numAns );
        do {
            try {
                System.out.println( "Waiting for messages, now : " + responses.size() );
                Thread.sleep( 1000 );
                responses.addAll( agent.getAgentAnswers( id ) );
            } catch (InterruptedException e) {
                fail( e.getMessage() );
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } while ( responses.size() < numAns );
        return responses;
    }


    @Test
    public void testSimpleRequestToDeliverMessage() throws InterruptedException {

        ApplicationContext context = new ClassPathXmlApplicationContext("test-applicationContext.xml");
        DroolsAgent agent = (DroolsAgent) context.getBean("agent");
        assertNotNull(agent);

        LinkedHashMap<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("refId", "284d7e8d-6853-46cb-bef2-3c71e565f90d");
        args.put("conversationId", "502ed27e-682d-43b3-ac2a-8bba3b597d13");
        args.put("subjectAbout", new String[] { "patient1", "docx", "id1", "id2", "id3" } );
        args.put("sender", "docx");
        args.put("mainRecipients", new String[] {"id1"} );
        args.put("secondaryRecipients", new String[] {"id2"});
        args.put("hiddenRecipients", new String[] {"id3"});
        args.put("type", "ALERT");
        args.put("header", "Risk threshold exceeded : MockPTSD (30%)");
        args.put("body", "<h2>MockPTSD</h2><br/>(This is free form HTML with an optionally embedded survey)<br/>Dear @{recipient.displayName},<br/>"
                + "<p>Your patient @{patient.displayName} has a high risk of developing the disease known as MockPTSD. <br/>"
                + "     The estimated rate is around 30.000000000000004%."
                + "</p>"
                + "<p>"
                + "They will contact you shortly."
                + "</p>"
                + "MockPTSD:<p class='agentEmbed' type='survey' id='01f0bb3d-7f67-450a-ad8d-be29c5611055' /p>"
                + "<br/>Thank you very much, <br/><p>Your Friendly Clinical Decision Support Agent, on behalf of @{provider.displayName}</p>");
        args.put("priority", "Critical");
        args.put("deliveryDate", "Tue Oct 11 23:46:36 CEST 2011");
        args.put("status", "New");



        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );

        Action action = MessageContentFactory.newActionContent( "deliverMessage", args );
        ACLMessage req = factory.newRequestMessage( "", "", action );


        agent.tell( req );

        List<ACLMessage> ans = waitForResponse( agent, req.getId(), 2 );

        assertEquals( 2, ans.size() );
        assertEquals( Act.AGREE, ans.get( 0 ).getPerformative() );
        assertEquals( Act.INFORM, ans.get( 1 ).getPerformative() );

        Object result = ((Inform) ans.get( 1 ).getBody()).getProposition().getEncodedContent();
        assertNotNull( result );
        assertTrue( result.toString().contains( "refId" ) );
        assertTrue( result.toString().contains( "convoId" ) );




        action = MessageContentFactory.newActionContent( "deliverMessage", args );
        ACLMessage req2 = factory.newRequestMessage( "", "", action );

        agent.tell( req2 );

        ans.clear();
        ans = waitForResponse( agent, req2.getId(), 2 );
        assertEquals( 2, ans.size() );
        assertEquals( Act.AGREE, ans.get( 0 ).getPerformative() );
        assertEquals( Act.INFORM, ans.get( 1 ).getPerformative() );

        agent.dispose();
    }

    @Test
    public void testEncoder(){
        MockFact fact = new MockFact( "salaboy", Integer.SIZE );
        String exp = "<org.drools.mas.test.MockFact>\n" +
                "  <name>salaboy</name>\n" +
                "  <age>32</age>\n" +
                "</org.drools.mas.test.MockFact>";

        assertEquals(exp, MessageContentEncoder.encode(fact, Encodings.XML));

    }
}
