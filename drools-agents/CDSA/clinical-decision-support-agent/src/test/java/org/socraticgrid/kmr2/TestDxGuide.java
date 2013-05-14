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
package org.socraticgrid.kmr2;

import org.drools.mas.core.DroolsAgent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

public class TestDxGuide extends BaseTest {

    private static DroolsAgent mainAgent;
    private static Logger logger = LoggerFactory.getLogger(TestDxGuide.class);

    @BeforeClass
    public static void createAgents() {
        ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/test-dxGuide-applicationContext.xml");
        mainAgent = (DroolsAgent) context.getBean("agent");
    }

    @AfterClass
    public static void destroyAgents() {
        if (mainAgent != null) {
            mainAgent.dispose();
        }
    }

    
    @Test
    public void testDifferentialSetSurvey() {

        String dxProcessReturn = startDiagnosticGuideProcess(mainAgent, "docX", "123456", "Post Traumatic Stress Disorder");
        String dxProcessId = getValue(dxProcessReturn, "//dxProcessId");

        assertNotNull(dxProcessId);

        String statusXML = getDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId, true);


        System.err.println(statusXML);


        String testActionId = getValue(statusXML, "//" + testDecModel + ".DoExcruciatinglyPainfulTest/actionId");
        String testActionQuestId = getValue(statusXML, "//" + testDecModel + ".DoExcruciatinglyPainfulTest/questionnaireId");
//        String testQuestId = getValue( statusXML, "//" + testDecModel + ".DoExcruciatinglyPainfulTest/questionnaireId" );
        assertNotNull(testActionId);
//        assertNotNull( testQuestId );

        String stat1 = setDiagnosticActionStatus(mainAgent, "drX", "123456", dxProcessId, testActionQuestId, "Started");
        assertEquals("Started", stat1);

        String testQuestId = lookupInBody(statusXML, "DoExcruciatinglyPainfulTest");

        String survXML = getSurvey(mainAgent, "drX", "123456", testQuestId);
        String confirmQid = getValue(survXML, "//questionName[.='confirm']/../itemId");


        String set;
        set = setSurvey(mainAgent, "drx", "123456", testQuestId, confirmQid, "invalidAnswerForBoolean");
        System.err.println(set);
        assertEquals("invalid", getValue(set, "//updatedQuestions//itemId[.='" + confirmQid + "']/../successType"));


        set = setSurvey(mainAgent, "drx", "123456", testQuestId, confirmQid, "true");

        System.err.println(set);


        statusXML = getDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId, true);
//        assertEquals( "true", getValue( statusXML, "//" + testDecModel + ".DoExcruciatinglyPainfulTest/confirm" ) );
        assertEquals("Started", getValue(statusXML, "//" + testDecModel + ".DoExcruciatinglyPainfulTest/status"));

        sleep(2500);

        set = setSurvey(mainAgent, "drx", "123456", testQuestId, confirmQid, "false");
        System.out.println(set);

        // no longer holds since we don't have autocompleting forms
        //assertEquals( "disable", getValue( set, "//updatedQuestions//itemId[.='"+ confirmQid+"']/../action" ) );


        completeDiagnosticGuideProcess(mainAgent, "docX", "123456", dxProcessId, "Complete");

        statusXML = getDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId, true);

    }

    @Test
    public void testSetDiagnostic() {
        System.out.println("STARtING testSetDiagnostic ");


        String dxProcessReturn = startDiagnosticGuideProcess(mainAgent, "docX", "123456", "Post Traumatic Stress Disorder");
        String dxProcessId = getValue(dxProcessReturn, "//dxProcessId");
        System.out.println("THE NEW DX PROCESS FOR testSetDiagnostic IS " + dxProcessId);

        assertNotNull(dxProcessId);

        String statusXML = getDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId, true);

        String actionId = getValue(statusXML, "//" + testDecModel + ".AskAlcohol/actionId");
        String actionQuestId = getValue(statusXML, "//" + testDecModel + ".AskAlcohol/questionnaireId");
        assertNotNull(actionId);


        System.err.println(statusXML);

        String stat1 = setDiagnosticActionStatus(mainAgent, "drX", "123456", dxProcessId, actionQuestId, "Started");
        

        assertEquals("Started", stat1);


        String stat3 = setDiagnosticActionStatus(mainAgent, "drX", "123456", dxProcessId, actionQuestId, "Complete");
        assertEquals("Completed", stat3);

        statusXML = getDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId, true);
        System.out.println(statusXML);

        completeDiagnosticGuideProcess(mainAgent, "drX", "123456", dxProcessId, "Complete");

    }

    @Test
    public void testDiagnostic() {
        System.out.println("STARtING testDiagnostic ");

        String dxProcessReturn = startDiagnosticGuideProcess(mainAgent, "docX", "123456", "Post Traumatic Stress Disorder");
        String dxProcessId = getValue(dxProcessReturn, "//dxProcessId");

        assertNotNull(dxProcessId);
        System.out.println("THE NEW DX PROCESS FOR testDiagnostic IS " + dxProcessId);

        String statusXML = getDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId, true);

        System.err.println(statusXML);


        String actionId = getValue(statusXML, "//" + testDecModel + ".AskAlcohol/actionId");
        String actionQuestId = getValue(statusXML, "//" + testDecModel + ".AskAlcohol/questionnaireId");
        assertNotNull(actionId);

        String stat1 = setDiagnosticActionStatus(mainAgent, "drX", "123456", dxProcessId, actionQuestId, "Started");

        assertEquals("Started", stat1);



        String actionBodyId = lookupInBody(statusXML, "AskAlcohol");

        String survXML = getSurvey(mainAgent, "drX", "123456", actionBodyId);
        String alcoholQid = getValue(survXML, "//questionName[.='question']/../itemId");

        System.err.println(alcoholQid);



        statusXML = getDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId, true);
        assertEquals("false", getValue(statusXML, "//canAdvance"));



        setSurvey(mainAgent, "drX", "123456", actionBodyId, alcoholQid, "true");



        survXML = getSurvey(mainAgent, "drX", "123456", actionBodyId);


        System.err.println(survXML);



        String stat = setDiagnosticActionStatus(mainAgent, "drX", "123456", dxProcessId, actionQuestId, "Complete");
        assertEquals("Completed", stat);




        statusXML = getDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId, true);



        assertEquals("Completed", getValue(statusXML, "//actionId[.='" + actionId + "']/../status"));
        assertEquals("Not Started", getValue(statusXML, "//" + testDecModel + ".AskDeployment/status"));



        assertEquals("true", getValue(statusXML, "//canAdvance"));
        assertEquals("false", getValue(statusXML, "//canCancel"));



        advanceDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId);





        statusXML = getDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId, true);




        assertEquals("true", getValue(statusXML, "//canAdvance"));


        completeDiagnosticGuideProcess(mainAgent, "rdrX", "123456", dxProcessId, "Complete");

        statusXML = getDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId, true);


        System.err.println(statusXML);
//
        assertEquals("1", getValue(statusXML, "//org.socraticgrid.kmr2.clinicalAgent.models.decision.DxDecision/diseaseProbability[.='10']/../stage"));


    }

    @Test
    public void testProbe() throws InterruptedException {
        System.out.println(probe(mainAgent, "123456"));
    }

    @Test
    public void testGetDiagnosticActionStatus() {


        String dxProcessReturn = startDiagnosticGuideProcess(mainAgent, "docX", "123456", "Post Traumatic Stress Disorder");
        String dxProcessId = getValue(dxProcessReturn, "//dxProcessId");

        assertNotNull(dxProcessId);

        String statusXML = getDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId, true);

        String actionId = getValue(statusXML, "//" + testDecModel + ".AskAlcohol/actionId");

        String actStatusXML = getDiagnosticActionStatus(mainAgent, "drX", "123456", dxProcessId, actionId);

        System.err.println(actStatusXML);

        assertEquals("true", getValue(statusXML, "//canAdvance"));
        assertEquals("true", getValue(statusXML, "//canCancel"));

        advanceDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId);

        String actStatusXML2 = getDiagnosticActionStatus(mainAgent, "drX", "123456", dxProcessId, actionId);

        System.err.println(actStatusXML);

        statusXML = getDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId, true);
        System.out.println(statusXML);



        assertEquals(actStatusXML, actStatusXML2);


    }

    @Test
    public void testEmptyDiagnostic() {

        String dxProcessReturn = startDiagnosticGuideProcess(mainAgent, "docX", "123456", "MockCold");
        String dxProcessId = getValue(dxProcessReturn, "//dxProcessId");
        assertNotNull(dxProcessId);

        String statusXML = getDiagnosticProcessStatus(mainAgent, "drX", "123456", dxProcessId, true);

        System.err.println(statusXML);

    }

    @Test
    public void testNonExistingDiagnostic() {

        String dxProcessReturn = startDiagnosticGuideProcess(mainAgent, "docX", "123456", "Imaginary Disease");
        assertTrue(dxProcessReturn.startsWith("FAILURE:"));
        System.err.println(dxProcessReturn);

    }
}
