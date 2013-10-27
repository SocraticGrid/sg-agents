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

import org.drools.ClassObjectFilter;
import org.drools.definition.type.FactType;
import org.drools.mas.core.DroolsAgent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.junit.Assert;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.socraticgrid.cdsa.dialoguehelper.DialogueHelperFactory;
import org.socraticgrid.cdsa.dialoguehelper.TestDialogueHelperWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/applicationContext.xml"})
public class TestModels extends BaseTest {

    @Autowired
    private DroolsAgent agent;

    private static Logger logger = LoggerFactory.getLogger( TestModels.class );

    @BeforeClass
    public static void setUp() {
        //set a mocked DialogueHelper
        DialogueHelperFactory.dialogueHelperClass = TestDialogueHelperWrapper.class;
    }


    @AfterClass
    public static void cleanUp() {
    }

    private List<String> getModelsFromXML( String xml ) {
        return getElements( xml, "//modelId" );
    }

    @Test
    public void testAgentCreation() {
        // nothing, just see that the agent is loaded
        assertNotNull (agent );

        for ( Object  o : agent.getMind().getObjects() ) {
            logger.debug(o.toString());
        }
    }



    @Test
    public void testGetModels() {

        String diagModels = getModels(agent, "drX", "patient33", Arrays.asList("Diagnostic") );

        logger.info(diagModels);

        List<String> diagList = getModelsFromXML( diagModels );
        assertEquals( 1, diagList.size() );
        assertTrue( diagList.containsAll( Arrays.asList("MockDiag" ) ) );



        setRiskThreshold(agent, "drX", "patient33", "MockPTSD", "Display", 35 );

        String riskModels = getRiskModels(agent, "drX", "patient33");

        logger.info(riskModels);

        List<String> riskList = getModelsFromXML( riskModels );
        assertEquals( 4, riskList.size());
        assertTrue( riskList.containsAll( Arrays.asList( "MockPTSD", "MockCold", "MockDiabetes" ) ) );

        assertEquals( "35", getValue( riskModels, "//modelId[.='MockPTSD']/../displayThreshold" ) );


        String enterpriseRiskModels = getModels(agent, "drX", "patient33", Arrays.asList("E", "Risk") );

        logger.info(enterpriseRiskModels);

        List<String> enterpriseRiskModelsList = getModelsFromXML( enterpriseRiskModels );
        assertEquals( 3, enterpriseRiskModelsList.size() );
        assertTrue( enterpriseRiskModelsList.containsAll( Arrays.asList( "MockPTSD", "MockCold", "MockBC" ) ) );


    }



    @Test
    public void testGetRiskModelsDetail() {

        List<String> modelsIds = getElements(getRiskModels(agent, "docX", "patient33"), "//modelId");
        logger.debug(modelsIds.toString());

        String modelStats = getRiskModelsDetail(agent, "docX", "patient33", modelsIds.toArray(new String[modelsIds.size()]));
        logger.info(modelStats);

        String sid1 = getValue( modelStats, "//modelId[.='MockPTSD']/../surveyId" );
        String sid2 = getValue( modelStats, "//modelId[.='MockCold']/../surveyId" );

        assertNotNull( sid1 );
        assertNotNull( sid2 );
        assertTrue(sid1.length() > 1);
        assertTrue(sid2.length() > 1);



        logger.debug(sid1 + " .............................. " + sid2);
        String ptsdSurvey = getSurvey(agent, "docX", "patient33", sid1);
        String coldSurvey = getSurvey(agent, "docX", "patient33", sid2);


        try {
            logger.debug(probe(agent, "patient33"));
        } catch (InterruptedException e) {
            logger.error("", e);
        }

        logger.debug("@#*" + coldSurvey);

        String gender = getValue(ptsdSurvey, "//questionName[.='MockPTSD_Gender']/../itemId");
        String deployments = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Deployments']/../itemId" );
        String alcohol = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Alcohol']/../itemId" );
        String age = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Age']/../itemId" );

        String temperature = getValue( coldSurvey, "//questionName[.='MockCold_Temp']/../itemId" );

        assertNotNull( gender );
        assertNotNull( deployments );
        assertNotNull( age );
        assertNotNull( temperature);

        setSurvey(agent, "drX", "patient33", sid1, deployments, "1");

        setSurvey(agent, "drX", "patient33", sid1, gender, "female");
        setSurvey(agent, "drX", "patient33", sid1, alcohol, "yes" );
        setSurvey(agent, "drX", "patient33", sid1, age, "30" );

        setSurvey(agent, "drX", "patient33", sid2, temperature, "39" );

        setRiskThreshold(agent, "drX", "patient33", "MockPTSD", "Alert", 35);



        modelStats = getRiskModelsDetail(agent, "docX", "patient33", modelsIds.toArray(new String[modelsIds.size()]));

        String sid3 = getValue( modelStats, "//modelId[.='MockCold']/../surveyId" );
        assertEquals( sid2, sid3 );


        logger.info(modelStats);
        assertEquals( "30", getValue( modelStats, "//modelId[.='MockPTSD']/../relativeRisk" ) );
        assertEquals( "Average", getValue( modelStats, "//modelId[.='MockPTSD']/../severity" ) );
        assertEquals( "35", getValue( modelStats, "//modelId[.='MockPTSD']/../alertThreshold" ) );
        assertFalse( "Started".equals( getValue( modelStats, "//modelId[.='MockPTSD']/../dxProcessStatus" ) ) );


        assertEquals( "50", getValue( modelStats, "//modelId[.='MockCold']/../alertThreshold" ) );
        assertEquals( "22", getValue( modelStats, "//modelId[.='MockCold']/../relativeRisk" ) );
        assertEquals( "Low", getValue( modelStats, "//modelId[.='MockCold']/../severity" ) );


        setSurvey(agent, "drX", "patient33", sid1, deployments, "null" );
        setSurvey(agent, "drX", "patient33", sid1, gender, "null" );
        setSurvey(agent, "drX", "patient33", sid1, alcohol, "null" );
        setSurvey(agent, "drX", "patient33", sid1, age, "null" );

        setSurvey(agent, "drX", "patient33", sid2, temperature, "null" );
        logger.debug("@#*" + sid2);
    }




    @Test
    public void testResourceCompilation() {
        Resource resource = ResourceFactory.newClassPathResource("config/subsession_default.xml");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        kbuilder.add(resource, ResourceType.CHANGE_SET);
        
        if (kbuilder.hasErrors()){
            Iterator<KnowledgeBuilderError> iterator = kbuilder.getErrors().iterator();
            while (iterator.hasNext()) {
                KnowledgeBuilderError knowledgeBuilderError = iterator.next();
                System.out.println(knowledgeBuilderError);
            }
            Assert.fail("Compilation errors!");
        }
    }
    

    @Test
    public void testExceedAndReset() {

        List<String> modelsIds = getElements(getRiskModels(agent, "docX", "patient33"), "//modelId");
        String modelStats = getRiskModelsDetail(agent, "docX", "patient33", modelsIds.toArray(new String[modelsIds.size()]));

        FactType alertType = agent.getInnerSession("patient33").getKnowledgeBase().getFactType("org.drools.informer.interaction", "Alert");
        Class alertClass = alertType.getFactClass();
        FactType xType = agent.getInnerSession("patient33").getKnowledgeBase().getFactType("org.drools.informer.interaction", "TicketActor");


        Collection alerts = agent.getInnerSession("patient33").getObjects(new ClassObjectFilter(alertClass));
        assertEquals( 0, alerts.size());


        String sid1 = getValue(modelStats, "//modelId[.='MockPTSD']/../surveyId");
        assertNotNull(sid1);
        assertFalse("".equalsIgnoreCase(sid1.trim()));

        String ptsdSurvey = getSurvey(agent, "docX", "patient33", sid1);

        String gender = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Gender']/../itemId" );
        String deployments = getValue(ptsdSurvey, "//questionName[.='MockPTSD_Deployments']/../itemId");
        String alcohol = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Alcohol']/../itemId" );
        String age = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Age']/../itemId" );

        assertNotNull(gender);
        assertNotNull(deployments );
        assertNotNull( age );

        setSurvey(agent, "drX", "patient33", sid1, deployments, "1");
        setSurvey(agent, "drX", "patient33", sid1, gender, "female");
        setSurvey(agent, "drX", "patient33", sid1, alcohol, "yes");
        setSurvey(agent, "drX", "patient33", sid1, age, "30" );

        modelStats = getRiskModelsDetail(agent, "docX", "patient33", modelsIds.toArray(new String[modelsIds.size()]));
        assertEquals("30", getValue(modelStats, "//modelId[.='MockPTSD']/../relativeRisk"));


        setRiskThreshold(agent, "drX", "patient33", "MockPTSD", "Alert", 25 );


        alertType = agent.getInnerSession("patient33").getKnowledgeBase().getFactType("org.drools.informer.interaction", "Alert");
        alertClass = alertType.getFactClass();

        alerts = agent.getInnerSession("patient33").getObjects( new ClassObjectFilter(alertClass) );
        assertEquals( 2, alerts.size() );
        sleep(12000);
        alerts = agent.getInnerSession("patient33").getObjects( new ClassObjectFilter(alertClass) );
        assertEquals( 0, alerts.size() );
        Collection zalerts = agent.getInnerSession("patient33").getObjects( new ClassObjectFilter(xType.getFactClass()) );
        assertEquals( 0, zalerts.size() );




        for ( Object o :  agent.getInnerSession("patient33").getObjects() ){
            logger.debug(o.toString());
        }


        setSurvey(agent, "drX", "patient33", sid1, age, "1" );

        modelStats = getRiskModelsDetail(agent, "docX", "patient33", modelsIds.toArray(new String[modelsIds.size()]));
        assertEquals( "16", getValue( modelStats, "//modelId[.='MockPTSD']/../relativeRisk" ) );


        setSurvey(agent, "drX", "patient33", sid1, age, "40" );

        modelStats = getRiskModelsDetail(agent, "docX", "patient33", modelsIds.toArray(new String[modelsIds.size()]));
        assertEquals( "35", getValue( modelStats, "//modelId[.='MockPTSD']/../relativeRisk" ) );



        zalerts = agent.getInnerSession("patient33").getObjects( new ClassObjectFilter(xType.getFactClass()) );
        for (Object o : zalerts) {
            logger.debug(o.toString());
        }



        alerts = agent.getInnerSession("patient33").getObjects( new ClassObjectFilter(alertClass) );
        for (Object o : alerts) {
            logger.debug(o.toString());
        }
        assertEquals( 2, alerts.size() );


        setRiskThreshold(agent, "drX", "patient33", "MockPTSD", "Alert", 55 );

    }









    @Test
    public void testSetAndResetAndSet() {

        String[] mids = new String[] { "MockPTSD" };

        String modelStats = getRiskModelsDetail(agent, "docX", "patient33", mids);

        String sid1 = getValue( modelStats, "//modelId[.='MockPTSD']/../surveyId" );
        assertNotNull( sid1 );
        assertFalse("".equalsIgnoreCase(sid1.trim()));

        logger.debug("SurveyId= "+sid1);
        
        String ptsdSurvey = getSurvey(agent, "docX", "patient33", sid1);

        logger.debug(ptsdSurvey);
        
        String gender = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Gender']/../itemId" );
        String deployments = getValue(ptsdSurvey, "//questionName[.='MockPTSD_Deployments']/../itemId");
        String alcohol = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Alcohol']/../itemId" );
        String age = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Age']/../itemId" );

        assertNotNull( gender );
        assertNotNull( deployments );
        assertNotNull( age );

        String set;
        set = setSurvey(agent, "drX", "patient33", sid1, deployments, "1" );
        logger.debug(set);
        set = setSurvey(agent, "drX", "patient33", sid1, gender, "female" );
        logger.debug(set);
        set = setSurvey(agent, "drX", "patient33", sid1, alcohol, "yes" );
        logger.debug(set);
        set = setSurvey(agent, "drX", "patient33", sid1, age, "30" );
        logger.debug(set);

        modelStats = getRiskModelsDetail(agent, "docX", "patient33", mids);
        assertEquals("30", getValue(modelStats, "//modelId[.='MockPTSD']/../relativeRisk"));
        assertEquals( "Average", getValue( modelStats, "//modelId[.='MockPTSD']/../severity" ) );




        set = setSurvey(agent, "drX", "patient33", sid1, age, null );
        logger.debug(set);
        set = setSurvey(agent, "drX", "patient33", sid1, gender, null );
        logger.debug(set);

        modelStats = getRiskModelsDetail(agent, "docX", "patient33", mids);
        assertEquals( "-1", getValue( modelStats, "//modelId[.='MockPTSD']/../relativeRisk" ) );
        assertEquals("n/a", getValue(modelStats, "//modelId[.='MockPTSD']/../severity"));



        setSurvey(agent, "drX", "patient33", sid1, age, "25" );
        modelStats = getRiskModelsDetail(agent, "docX", "patient33", mids);
        assertEquals( "-1", getValue( modelStats, "//modelId[.='MockPTSD']/../relativeRisk"));
        assertEquals( "n/a", getValue( modelStats, "//modelId[.='MockPTSD']/../severity" ) );



        setSurvey(agent, "drX", "patient33", sid1, gender, "male" );
        modelStats = getRiskModelsDetail(agent, "docX", "patient33", mids);
        assertEquals( "38", getValue( modelStats, "//modelId[.='MockPTSD']/../relativeRisk" ) );
        assertEquals( "Average", getValue( modelStats, "//modelId[.='MockPTSD']/../severity" ) );



        setSurvey(agent, "drX", "patient33", sid1, age, "30" );
        modelStats = getRiskModelsDetail(agent, "docX", "patient33", mids);
        assertEquals( "40", getValue( modelStats, "//modelId[.='MockPTSD']/../relativeRisk" ) );
        assertEquals( "Average", getValue( modelStats, "//modelId[.='MockPTSD']/../severity" ) );


    }












    @Test
    public void testClearRiskModelsSurvey() {

        String[] modelsIds = new String[] {"MockCold"};
        String modelStats = getRiskModelsDetail(agent, "docX", "patient33", modelsIds);

        String sid2 = getValue( modelStats, "//modelId[.='MockCold']/../surveyId" );


        assertNotNull(sid2);
        assertFalse("".equalsIgnoreCase(sid2.trim()));

        String coldSurvey = getSurvey(agent, "docX", "patient33", sid2);
        logger.debug("@#*" + coldSurvey);

        String temperature = getValue(coldSurvey, "//questionName[.='MockCold_Temp']/../itemId");
        assertNotNull(temperature);

        for ( int j = 0; j < 5; j++ ) {
            logger.debug("");
        }
        logger.debug(getSurvey(agent, "docX", "patient33", sid2));
        for ( int j = 0; j < 5; j++ ) {
            logger.debug("");
        }

        setSurvey(agent, "drX", "patient33", sid2, temperature, "null");

        setSurvey(agent, "drX", "patient33", sid2, temperature, "39" );


        modelStats = getRiskModelsDetail(agent, "docX", "patient33", modelsIds);



        assertEquals("22", getValue(modelStats, "//modelId[.='MockCold']/../relativeRisk"));


        setSurvey(agent, "drX", "patient33", sid2, temperature, "null" );

        modelStats = getRiskModelsDetail(agent, "docX", "patient33", modelsIds);
        assertEquals( "-1", getValue( modelStats, "//modelId[.='MockCold']/../relativeRisk" ) );




        setSurvey(agent, "drX", "patient33", sid2, temperature, "35" );
        modelStats = getRiskModelsDetail(agent, "docX", "patient33", modelsIds);

        assertEquals( "30", getValue( modelStats, "//modelId[.='MockCold']/../relativeRisk" ) );


    }






    @Test
    public void testExceedRiskThreshold() {
        
        List<String> modelsIds = getElements(getRiskModels(agent, "docX", "patient33"), "//modelId");
        String modelStats = getRiskModelsDetail(agent, "docX", "patient33", modelsIds.toArray(new String[modelsIds.size()]));

        System.out.println("\n\n\n");
        System.out.println(modelStats);
        System.out.println("\n\n\n");
        
        String sid1 = getValue( modelStats, "//modelId[.='MockPTSD']/../surveyId" );
        assertNotNull( sid1 );
        assertFalse("".equalsIgnoreCase(sid1.trim()));

        String ptsdSurvey = getSurvey(agent, "docX", "patient33", sid1);

        logger.debug(ptsdSurvey);

        String gender = getValue(ptsdSurvey, "//questionName[.='MockPTSD_Gender']/../itemId");
        String deployments = getValue(ptsdSurvey, "//questionName[.='MockPTSD_Deployments']/../itemId" );
        String alcohol = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Alcohol']/../itemId" );
        String age = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Age']/../itemId" );

        assertNotNull( gender );
        assertNotNull( deployments );
        assertNotNull( age );

        setSurvey(agent, "drX", "patient33", sid1, deployments, "1");
        setSurvey(agent, "drX", "patient33", sid1, gender, "female");
        setSurvey(agent, "drX", "patient33", sid1, alcohol, "yes" );
        setSurvey(agent, "drX", "patient33", sid1, age, "30" );

        modelStats = getRiskModelsDetail(agent, "docX", "patient33", modelsIds.toArray(new String[modelsIds.size()]));
        logger.info(modelStats);
        assertEquals( "30", getValue( modelStats, "//modelId[.='MockPTSD']/../relativeRisk" ) );


        setRiskThreshold(agent, "drX", "patient33", "MockPTSD", "Alert", 05 );






        FactType alertType = agent.getInnerSession("patient33").getKnowledgeBase().getFactType("org.drools.informer.interaction", "Alert");
        Class alertClass = alertType.getFactClass();

        Collection content = agent.getInnerSession("patient33").getObjects( );
        Collection alerts = new ArrayList();
        for ( Object o : content ) {
            if ( o.getClass().getName().equals( "org.drools.informer.interaction.Alert" ) ) {
                alerts.add( o );
                logger.debug( o.getClass().hashCode() + " vs " + alertClass.hashCode() );
            }
        }
//        assertEquals( 2, alerts.size() );


        String ackQuestionId;
        for ( Object alert : alerts ) {

            logger.debug("°°°° Now processing the alerts..... ");
            logger.debug(alert.toString());
            
            String formId = (String) alertType.get( alert, "formId" );
            String dest = (String) alertType.get( alert, "destination" );

            String form = getSurvey(agent, dest, "patient33", formId );
            assertNotNull( form );

            ackQuestionId = getValue( form, "//org.drools.informer.presentation.QuestionGUIAdapter/questionName[.='transition']/../itemId" );

            setSurvey(agent, "patient33", "patient33", formId, ackQuestionId, "COMPLETE" );
        }

        alerts = agent.getInnerSession("patient33").getObjects( new ClassObjectFilter(alertClass) );
        assertEquals( 0, alerts.size() );


        setRiskThreshold(agent, "drX", "patient33", "MockPTSD", "Alert", 50 );

    }






    @Test
    public void testProbe() throws InterruptedException {
        logger.debug( probe(agent, "patient33" ) );
    }


}





