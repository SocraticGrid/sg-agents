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
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class TestAgentFactLoading {

    private static DroolsAgent mainAgent;

    private ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );

    private static Logger logger = LoggerFactory.getLogger( TestAgentFactLoading.class );
    private static Server server;

    private static PrintStream testout;

    @BeforeClass
    public static void createAgents() {

        DeleteDbFiles.execute("~", "mydb", false);

        logger.info("Staring DB for white pages ...");
        try {
            server = Server.createTcpServer(new String[] {"-tcp","-tcpAllowOthers","-tcpDaemon","-trace"}).start();
        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        }
        logger.info("DB for white pages started! ");

        //GridHelper.reset();

        testout = new EavesdroppingPrintStream( System.out );
        System.setOut( testout );


        ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/test-fact-applicationContext.xml");


        mainAgent = (DroolsAgent) context.getBean( "agent" );

    }


    @AfterClass
    public static void cleanUp() {
        if (mainAgent != null) {
            mainAgent.dispose();
        }

        logger.info("Stopping DB ...");
        try {
            Server.shutdownTcpServer( server.getURL(), "", false, false);
        } catch (SQLException e) {
            e.printStackTrace();
            fail ( e.getMessage() );
        }
        logger.info("DB Stopped!");

    }


    private void sleep( long millis ) {
        try {
            Thread.sleep( millis );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitForResponse( String id ) {
        do {
            try {
                Thread.sleep( 1000 );
                System.out.println( "Waiting for messages, now : " + mainAgent.peekAgentAnswers( id ).size() );
            } catch (InterruptedException e) {
                fail( e.getMessage() );
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } while ( mainAgent.peekAgentAnswers( id ).size() < 2);

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
        assertEquals(Act.INFORM, ans.getPerformative());
        MessageContentEncoder.decodeBody(ans.getBody(), Encodings.XML);
        return (String) ((Inform) ans.getBody()).getProposition().getData();

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
        assertNotNull(mainAgent);
        assertEquals( 2, ((EavesdroppingPrintStream) testout).getCounter() );
    }



    public static class EavesdroppingPrintStream extends PrintStream {

        private int counter = 0;

        public EavesdroppingPrintStream(OutputStream out) {
            super(out);
        }

        public int getCounter() {
            return counter;
        }

        @Override
        public void println( String x ) {
            super.println(x);
            if ( x != null && x.contains( "Logging information" ) ) {
                counter++;
            }
        }
    }
}





