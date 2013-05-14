/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.socraticgrid.cdsa.dialoguehelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import org.drools.mas.ACLMessage;
import org.drools.mas.Encodings;
import org.drools.mas.body.content.Query;
import org.drools.mas.helpers.DialogueHelperCallback;

/**
 *
 * @author esteban
 */
public class TestDialogueHelperWrapper implements DialogueHelperWrapper{

    public TestDialogueHelperWrapper(String url) {
    }

    public TestDialogueHelperWrapper(String url, Encodings enc) {
    }

    public TestDialogueHelperWrapper(String url, int wSDLRetrievalTimeout) {
    }

    public TestDialogueHelperWrapper(String url, Encodings enc, int wSDLRetrievalTimeout) {
    }

    public Object extractReturn(ACLMessage msg, boolean decode) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<ACLMessage> getAgentAnswers(String reqId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String invokeConfirm(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String invokeConfirm(String sender, String receiver, Object proposition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String invokeDisconfirm(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String invokeDisconfirm(String sender, String receiver, Object proposition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String invokeInform(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String invokeInform(String sender, String receiver, Object proposition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String invokeQueryIf(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String invokeQueryIf(String sender, String receiver, Object proposition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String invokeQueryRef(String sender, String receiver, Query query, DialogueHelperCallback callback) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String invokeRequest(String sender, String receiver, String methodName, LinkedHashMap<String, Object> args, DialogueHelperCallback callback) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String invokeRequest(String methodName, LinkedHashMap<String, Object> args, DialogueHelperCallback callback) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String invokeRequest(String sender, String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String invokeRequest(String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        System.out.println("TestDialogueHelperWrapper.invokeRequest() invoked");
        return UUID.randomUUID().toString();
    }

    public String invokeRequest(String sender, String receiver, String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setConnectionTimeout(int connectionTimeout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setReceiveTimeout(int receiveTimeout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean validateRequestResponses(List<ACLMessage> answers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

    
}
