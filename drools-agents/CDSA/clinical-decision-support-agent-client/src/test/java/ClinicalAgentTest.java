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

import java.io.File;
import org.drools.mas.ACLMessage;
import org.drools.mas.Act;
import org.drools.mas.Encodings;
import org.drools.mas.body.acts.Inform;
import org.drools.mas.helpers.DialogueHelper;
import org.drools.mas.helpers.SyncDialogueHelper;
import org.drools.mas.util.MessageContentEncoder;
import org.drools.mas.util.MessageContentFactory;
import org.junit.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ClinicalAgentTest {

    private final String endpoint = "http://localhost:8082/clinical-decision-support-agent/services/AsyncAgentService?WSDL";
//private final String endpoint = "http://184.191.173.235:8082/clinical-decision-support-agent/services/AsyncAgentService?WSDL";

    public ClinicalAgentTest() {
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
    @Ignore
    public void testGetRiskModelsMessage() throws InterruptedException {
        DialogueHelper agentHelper = new DialogueHelper( endpoint );

        LinkedHashMap<String,Object> args = new LinkedHashMap<String,Object>();
        args.put( "userId", "drX" );
        args.put( "patientId", "patient33" );

        String reqId = agentHelper.invokeRequest( "getRiskModels", args );

        List<ACLMessage> agentAnswers = new ArrayList<ACLMessage>();
        
        do {
            agentAnswers.addAll( agentHelper.getAgentAnswers( reqId ) );
            Thread.sleep( 250 );
        } while ( ! agentHelper.validateRequestResponses( agentAnswers ) );

        assertEquals( Act.AGREE, agentAnswers.get(0).getPerformative() );
        assertEquals( Act.INFORM, agentAnswers.get(1).getPerformative() );

        System.out.println( "<<<<<< GET RISK MODELS RESULT : >>>>>>" );
        System.out.println( ret( agentAnswers.get( 1 ) ) );

    }




    @Test
    @Ignore
    public void testGetRiskModelsMessageSynch() {
        SyncDialogueHelper agentHelper = new SyncDialogueHelper( endpoint );

        LinkedHashMap<String,Object> args = new LinkedHashMap<String,Object>();
        args.put( "userId", "drX" );
        args.put( "patientId", "patient35" );

        String reqId = agentHelper.invokeRequest( "getRiskModels", args );
        Object ans = agentHelper.getReturn( true );

        System.out.println( "<<<<<< GET RISK MODELS RESULT 1 : >>>>>>" );
        System.out.println( ans );


        String reqId2 = agentHelper.invokeRequest( "getRiskModels", args );
        Object ans2 = agentHelper.getReturn( true );

        System.out.println( "<<<<<< GET RISK MODELS RESULT 2 : >>>>>>" );
        System.out.println( ans2 );

    }



    @Test
    @Ignore
    public void testGetRiskModelsDetails() {
        SyncDialogueHelper agentHelper = new SyncDialogueHelper( endpoint );

        LinkedHashMap<String,Object> args = new LinkedHashMap<String,Object>();

        args.put( "userId", "drX" );
        args.put( "patientId", "patient35" );
        args.put( "modelIds", new String[] { "MockPTSD" } );

        String reqId = agentHelper.invokeRequest( "getRiskModelsDetail", args );
        Object ans = agentHelper.getReturn( true );

        System.out.println( "<<<<<< GET RISK MODELS DETAIL RESULT 2 : >>>>>>" );
        System.out.println( ans );
    }



    private String ret( ACLMessage ans ) {
        
        assertEquals( Act.INFORM, ans.getPerformative() );
        MessageContentEncoder.decodeBody( ans.getBody(), Encodings.XML );
        return (String) ( (Inform) ans.getBody() ).getProposition().getData();

    }
}