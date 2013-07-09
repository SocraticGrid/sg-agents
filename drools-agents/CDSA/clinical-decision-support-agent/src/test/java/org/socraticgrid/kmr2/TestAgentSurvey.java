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

package org.socraticgrid.kmr2;

import org.drools.mas.ACLMessage;
import org.drools.mas.Act;
import org.drools.mas.Encodings;
import org.drools.mas.body.acts.Failure;
import org.drools.mas.body.acts.Inform;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.util.ACLMessageFactory;
import org.drools.mas.util.MessageContentEncoder;
import org.drools.mas.util.MessageContentFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/test-survey-applicationContext.xml"})
public class TestAgentSurvey {

    @Autowired
    private DroolsAgent agent;

    private ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );

    private static Logger logger = LoggerFactory.getLogger( TestAgentSurvey.class );

    @BeforeClass
    public static void setUp() {

    }


    @AfterClass
    public static void cleanUp() {
    }

    private void waitForResponse( String id ) {
        do {
            try {
                Thread.sleep( 1000 );
                System.out.println( "Waiting for messages, now : " + agent.peekAgentAnswers( id ).size() );
            } catch (InterruptedException e) {
                fail( e.getMessage() );
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } while ( agent.peekAgentAnswers( id ).size() < 2);

    }

    private static class FailureException extends Exception {
        private FailureException(String message) {
            super( "FAILURE:" + message );
        }
    }

    private String ret(ACLMessage ans) throws FailureException {

        if ( ! ans.getPerformative().equals( Act.INFORM ) ) {
            System.err.println( ans );
        }

        if ( ans.getPerformative().equals( Act.FAILURE ) ) {
            throw new FailureException( ((Failure) ans.getBody()).getCause().getData().toString() );
        }
        assertEquals( Act.INFORM, ans.getPerformative() );
        MessageContentEncoder.decodeBody( ans.getBody(), Encodings.XML );
        return (String) ((Inform) ans.getBody()).getProposition().getData();

    }


    private String getSurvey( String userId, String patientId, String surveyId ) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId",userId);
        args.put("patientId",patientId);
        args.put("surveyId",surveyId);

        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getSurvey", args));
        agent.tell(req);

        waitForResponse( req.getId() );

        ACLMessage ans = agent.getAgentAnswers(req.getId()).get(1);
        try {
            return ret(ans);
        } catch (FailureException e) {
            return e.getMessage();
        }
    }

    private String setSurvey( String userId, String patientId, String surveyId, String questionId, String value ) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId",userId);
        args.put("patientId",patientId);
        args.put("surveyId",surveyId);
        args.put("questionId", questionId);
        args.put("answer",value);

        ACLMessage set = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setSurvey", args) );
        agent.tell(set);

        waitForResponse( set.getId() );

        ACLMessage ans = agent.getAgentAnswers(set.getId()).get(1);
        try {
            return ret(ans);
        } catch (FailureException e) {
            return e.getMessage();
        }
    }


    public String getValue(String xml, String xpath) {
        try {
            Document dox = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream( xml.getBytes()) );
            XPath finder = XPathFactory.newInstance().newXPath();

            return (String) finder.evaluate( xpath, dox, XPathConstants.STRING );
        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        return null;
    }
    
    @Test
    public void testAgentCreation() {
        // nothing, just see that the agent is loaded
        assertNotNull (agent );
    }

    @Test
    public void testSetSurvey() {
        String[] qid = new String[8];
        String[] values = new String[8];


        String xmlSurv = getSurvey( "drX", "99990070", "123456UNIQUESURVEYID" );

        for ( int j = 1; j < 8; j++ ) {
            qid[j] = getValue( xmlSurv, "//org.drools.informer.presentation.QuestionGUIAdapter/questionName[.='question"+j+"']/../itemId" );
            System.out.println("ID OF Q" + j + " >> " + qid[j] );
        }


        for ( int j = 1; j < 8; j++ ) {
            String val = getValue( xmlSurv, "//org.drools.informer.presentation.QuestionGUIAdapter/itemId[.='"+qid[j]+"']/../successType" );
            System.out.println( j + " : " + val );
            switch (j) {
                case 1  :
                case 2  :
                case 4  : assertEquals( "valid", val ); break;
                case 7  : assertEquals( "missing", val ); break;
                default : assertEquals( "invalid", val ); break;
            }
        }


        values[1] = new Integer((int) Math.ceil( 6 * Math.random() )).toString();
        values[2] = Math.random() > 0.5 ? "Urban" : "Rural";
        List v3 = new ArrayList();
        if ( Math.random() > 0.5 ) { v3.add( "CMP" ); }
        if ( Math.random() > 0.5 ) { v3.add( "FSH" ); }
        if ( Math.random() > 0.5 ) { v3.add( "GLF" ); }
        if ( Math.random() > 0.5 ) { v3.add( "HGL" ); }
        if ( v3.size() == 0 ) {
            v3.add( "HGL" );
        }
        values[3] = v3.toString().replace("[","").replace("]","").replace(" ","");
        values[4] = new Integer((int) Math.round( 100 * Math.random() )).toString();
        values[5] = UUID.randomUUID().toString();
        values[6] = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        values[7] = new Integer((int) Math.round( 100 * Math.random() )).toString();


        for ( int j = 1; j < 8; j++ ) {
            setSurvey( "drx", "99990070", "123456UNIQUESURVEYID", qid[j], values[j].toString());
        }


        xmlSurv = getSurvey( "drX", "99990070", "123456UNIQUESURVEYID" );
        for ( int j = 1; j < 8; j++ ) {
            String val = getValue( xmlSurv, "//org.drools.informer.presentation.QuestionGUIAdapter/itemId[.='"+qid[j]+"']/../successType" );
            switch (j) {
                default : assertEquals( "valid", val ); break;
            }
        }



        for ( int j = 1; j < 8; j++ ) {
            String val = getValue( xmlSurv, "//org.drools.informer.presentation.QuestionGUIAdapter/itemId[.='"+qid[j]+"']/../currentAnswer" );
            assertEquals( val, values[j].toString() );
        }


        for ( int j = 1; j < 8; j++ ) {
            setSurvey( "drx", "99990070", "123456UNIQUESURVEYID", qid[j], null);
        }

        xmlSurv = getSurvey( "drX", "99990070", "123456UNIQUESURVEYID" );
        for ( int j = 1; j < 8; j++ ) {
            String val = getValue( xmlSurv, "//org.drools.informer.presentation.QuestionGUIAdapter/itemId[.='"+qid[j]+"']/../successType" );
            switch (j) {
                case 4  :
                case 7  : assertEquals( "missing", val ); break;
                default : assertEquals( "invalid", val ); break;
            }
        }


    }

    @Test
    public void testUnroutableMessage() {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId", "uid");
        args.put("surveyId","123");

        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getSurvey", args));
        agent.tell(req);

        for ( Object o : agent.getMind().getObjects() ) {
            System.out.println( "MIND OBEJT " +o );
        }

        List<ACLMessage> resp =  agent.getAgentAnswers(req.getId());
        assertEquals( 1, resp.size() );
        assertEquals( Act.NOT_UNDERSTOOD, resp.get( 0 ).getPerformative() );

    }

}





