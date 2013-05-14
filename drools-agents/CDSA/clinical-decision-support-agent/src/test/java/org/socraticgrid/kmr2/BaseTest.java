/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.socraticgrid.kmr2;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.drools.mas.ACLMessage;
import org.drools.mas.Act;
import org.drools.mas.Encodings;
import org.drools.mas.body.acts.Failure;
import org.drools.mas.body.acts.Inform;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.util.ACLMessageFactory;
import org.drools.mas.util.MessageContentEncoder;
import org.drools.mas.util.MessageContentFactory;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author esteban
 */
public class BaseTest {

    public static class FailureException extends Exception {

        public FailureException(String message) {
            super("FAILURE:" + message);
        }
    }
    private static Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected static final String testDecModel = "org.socraticgrid.kmr2.clinicalAgent.models.decision";
    protected static ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
    protected static Server server;

    @BeforeClass
    public static void startDB() {
        DeleteDbFiles.execute("~", "mydb", false);

        logger.info("Staring DB for white pages ...");
        try {
            server = Server.createTcpServer(new String[]{"-tcp", "-tcpAllowOthers", "-tcpDaemon", "-trace"}).start();
        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        }
        logger.info("DB for white pages started! ");
    }

    @AfterClass
    public static void stopDB() {

        logger.info("Stopping DB ...");
        try {
            Server.shutdownTcpServer(server.getURL(), "", false, false);
        } catch (SQLException e) {
            logger.error("Error while stopping DB", e);
            fail(e.getMessage());
        }
        logger.info("DB Stopped!");

    }

    protected void waitForAnswers(DroolsAgent agent, String id, int expectedSize) {
        this.waitForAnswers(agent, id, expectedSize, 250, 50);
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

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("Error while sleeping", e);
        }
    }

    protected String getSurvey(DroolsAgent agent, String userId, String patientId, String surveyId) {
        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("userId", userId);
        args.put("patientId", patientId);
        args.put("surveyId", surveyId);

        ACLMessage req = factory.newRequestMessage("me", "you", MessageContentFactory.newActionContent("getSurvey", args));

        List<ACLMessage> answers = syncTell(agent, req, 2);

        ACLMessage ans = answers.get(1);
        try {
            return ret(ans);
        } catch (FailureException e) {
            return e.getMessage();
        }
    }

    protected String setSurvey(DroolsAgent agent, String userId, String patientId, String surveyId, String questionId, String value) {
        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("userId", userId);
        args.put("patientId", patientId);
        args.put("surveyId", surveyId);
        args.put("questionId", questionId);
        args.put("answer", value);

        ACLMessage set = factory.newRequestMessage("me", "you", MessageContentFactory.newActionContent("setSurvey", args));
        List<ACLMessage> answers = syncTell(agent, set, 2);

        ACLMessage ans = answers.get(1);
        try {
            return ret(ans);
        } catch (FailureException e) {
            return e.getMessage();
        }
    }

    protected String startDiagnosticGuideProcess(DroolsAgent agent, String userId, String patientId, String disease) {
        Map<String, Object> args = new LinkedHashMap<String, Object>();

        args.put("userId", userId);
        args.put("patientId", patientId);
        args.put("disease", disease);
        ACLMessage start = factory.newRequestMessage("me", "you", MessageContentFactory.newActionContent("startDiagnosticGuideProcess", args));

        List<ACLMessage> answers = syncTell(agent, start, 2);

        ACLMessage ans = answers.get(1);
//        MessageContentEncoder.decodeBody( ans.getBody(), Encodings.XML );
//        String dxProcessId = (String) ((Inform) ans.getBody()).getProposition().getData();
//        return dxProcessId;
        try {
            return ret(ans);
        } catch (FailureException e) {
            return e.getMessage();
        }
    }

    protected String getDiagnosticProcessStatus(DroolsAgent agent, String userId, String patientId, String dxProcessId, boolean forceRefresh) {
        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("userId", userId);
        args.put("patientId", patientId);
        args.put("dxProcessId", dxProcessId);
        args.put("forceRefresh", forceRefresh);
        ACLMessage reqStatus = factory.newRequestMessage("me", "you", MessageContentFactory.newActionContent("getDiagnosticProcessStatus", args));

        List<ACLMessage> answers = syncTell(agent, reqStatus, 2);

        ACLMessage ans = answers.get(1);
        try {
            return ret(ans);
        } catch (FailureException e) {
            return e.getMessage();
        }
    }

    protected String getDiagnosticActionStatus(DroolsAgent agent, String userId, String patientId, String dxProcessId, String actionId) {
        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("userId", userId);
        args.put("patientId", patientId);
        args.put("dxProcessId", dxProcessId);
        args.put("dxActionId", actionId);
        ACLMessage reqStatus = factory.newRequestMessage("me", "you", MessageContentFactory.newActionContent("getDiagnosticActionStatus", args));

        List<ACLMessage> answers = syncTell(agent, reqStatus, 2);

        ACLMessage ans = answers.get(1);
        try {
            return ret(ans);
        } catch (FailureException e) {
            return e.getMessage();
        }
    }

    protected String setDiagnosticActionStatus(DroolsAgent agent, String userId, String patientId, String dxProcessId, String actionCtrlQuestId, String status) {

        System.out.println("Setting action status " + status + " for action ctrl quest id " + actionCtrlQuestId);

        String ctrl = getSurvey(agent, userId, patientId, actionCtrlQuestId);
        String txQid = getValue(ctrl, "//questionName[.='transition']/../itemId");

        logger.debug("Looking up " + actionCtrlQuestId + " vs " + txQid);


        if ("Started".equals(status)) {
            setSurvey(agent, userId, patientId, actionCtrlQuestId, txQid, "START");
        } else if ("Complete".equals(status)) {
            setSurvey(agent, userId, patientId, actionCtrlQuestId, txQid, "COMPLETE");
        }




        String statusXML = getDiagnosticProcessStatus(agent, userId, patientId, dxProcessId, true);

//        if ( status.contains( "Complete" ) ) {
        logger.debug(statusXML);
//        }

        String ans = getValue(statusXML, "//questionnaireId[.='" + actionCtrlQuestId + "']/../status");


        System.out.println("ACTION STATE " + ans);
        return ans;

    }

    protected void advanceDiagnosticProcessStatus(DroolsAgent agent, String userId, String patientId, String dxProcessId) {
        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("userId", userId);
        args.put("patientId", patientId);
        args.put("dxProcessId", dxProcessId);
        ACLMessage reqStatus = factory.newRequestMessage("me", "you", MessageContentFactory.newActionContent("advanceDiagnosticGuideProcess", args));

        syncTell(agent, reqStatus, 2);
    }

    protected void completeDiagnosticGuideProcess(DroolsAgent agent,String userId, String patientId, String dxProcessId, String status) {
        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("userId", userId);
        args.put("patientId", patientId);
        args.put("dxProcessId", dxProcessId);
        args.put("status", status);
        ACLMessage reqStatus = factory.newRequestMessage("me", "you", MessageContentFactory.newActionContent("completeDiagnosticGuideProcess", args));

        syncTell(agent, reqStatus, 2);
    }

    protected String probe(DroolsAgent agent, String patientId) throws InterruptedException {
        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("patientId", patientId);

        ACLMessage req = factory.newRequestMessage("me", "you", MessageContentFactory.newActionContent("probe", args));

        List<ACLMessage> answers = this.syncTell(agent, req, 2);

        ACLMessage ans = answers.get(1);
        try {
            return ret(ans);
        } catch (FailureException e) {
            return e.getMessage();
        }
    }
    
    protected void setRiskThreshold(DroolsAgent agent, String userId, String patientId, String modelId, String type, Integer value) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.clear();
        args.put("userId",userId);
        args.put("patientId",patientId);
        args.put("modelId",modelId);
        args.put("type",type);
        args.put("threshold",value);

        ACLMessage setThold = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setRiskThreshold", args) );
        syncTell(agent, setThold, 2);
    }

    protected String getModels(DroolsAgent agent, String userId, String patientId, List tags ) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId",userId);
        args.put("patientId",patientId);
        args.put("types", tags);
        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getModels", args));
        
        List<ACLMessage> ans = syncTell(agent, req, 2);

        try {
            return ret(ans.get(1));
        } catch (FailureException e) {
            return e.getMessage();
        }
    }

    protected String getRiskModels(DroolsAgent agent, String userId, String patientId ) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId",userId);
        args.put("patientId",patientId);
        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getRiskModels", args));
        
        List<ACLMessage> ans = syncTell(agent, req, 2);

        try {
            return ret(ans.get(1));
        } catch (FailureException e) {
            return e.getMessage();
        }
    }

    protected String getRiskModelsDetail(DroolsAgent agent, String userId, String patientId, String[] modelsIds) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();


        args.put("userId", userId );
        args.put("patientId", patientId );
        args.put("modelIds",modelsIds );

        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getRiskModelsDetail", args) );

        List<ACLMessage> ans = syncTell(agent, req, 2);
        
        try {
            return ret(ans.get(1));
        } catch (FailureException e) {
            return e.getMessage();
        }
    }


    protected List<ACLMessage> syncTell(DroolsAgent agent, ACLMessage message, int expectedSize) {
        agent.tell(message);
        this.waitForAnswers(agent, message.getId(), expectedSize);
        return agent.getAgentAnswers(message.getId());
    }

    protected ACLMessage syncTellWithSingleAnswer(DroolsAgent agent, ACLMessage message) {
        agent.tell(message);
        this.waitForAnswers(agent, message.getId(), 1);
        return agent.getAgentAnswers(message.getId()).get(0);
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

    protected List<String> getElements(String xml, String xpath) {
        try {
            Document dox = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
            XPath finder = XPathFactory.newInstance().newXPath();

            NodeList nodes = (NodeList) finder.evaluate(xpath, dox, XPathConstants.NODESET);
            List<String> list = new ArrayList<String>();
            for (int j = 0; j < nodes.getLength(); j++) {
                list.add(((Element) nodes.item(j)).getTextContent());
            }

            return list;
        } catch (Exception e) {
            logger.error("Error while getting xml element", e);
            fail(e.getMessage());
        }
        return Collections.emptyList();
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

    protected String lookupInBody(String statusXML, String actionName) {
        String body = getValue(statusXML, "//" + testDecModel + "." + actionName + "/body");

        System.err.println(body);

        int start = body.indexOf("id=");
        if (start > 0) {
            int end = body.indexOf("\'", start + 4);

            return body.substring(start + 4, end);
        }
        return null;
    }
}
