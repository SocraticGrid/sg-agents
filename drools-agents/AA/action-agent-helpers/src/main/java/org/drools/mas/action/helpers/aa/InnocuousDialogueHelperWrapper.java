/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.action.helpers.aa;

import java.util.LinkedHashMap;
import java.util.List;
import org.drools.mas.ACLMessage;
import org.drools.mas.body.content.Query;
import org.drools.mas.helpers.DialogueHelperCallback;

/**
 *
 * @author esteban
 */
public class InnocuousDialogueHelperWrapper implements DialogueHelperWrapper{

    public InnocuousDialogueHelperWrapper() {
    }

    public Object extractReturn(ACLMessage msg, boolean decode) throws UnsupportedOperationException {
        return null;
    }

    public List<ACLMessage> getAgentAnswers(String reqId) {
        return null;
    }

    public String invokeConfirm(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        return null;
    }

    public String invokeConfirm(String sender, String receiver, Object proposition) {
        return null;
    }

    public String invokeDisconfirm(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        return null;
    }

    public String invokeDisconfirm(String sender, String receiver, Object proposition) {
        return null;
    }

    public String invokeInform(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        System.out.println("InnocuousDialogueHelperWrapper.invokeInform() invoked");
        return null;
    }

    public String invokeInform(String sender, String receiver, Object proposition) {
        return this.invokeInform(sender, receiver, proposition, null);
    }

    public String invokeQueryIf(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        return null;
    }

    public String invokeQueryIf(String sender, String receiver, Object proposition) {
        return null;
    }

    public String invokeQueryRef(String sender, String receiver, Query query, DialogueHelperCallback callback) {
        return null;
    }

    public String invokeRequest(String sender, String receiver, String methodName, LinkedHashMap<String, Object> args, DialogueHelperCallback callback) throws UnsupportedOperationException {
        return null;
    }

    public String invokeRequest(String methodName, LinkedHashMap<String, Object> args, DialogueHelperCallback callback) throws UnsupportedOperationException {
        return null;
    }

    public String invokeRequest(String sender, String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        return null;
    }

    public String invokeRequest(String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        System.out.println("InnocuousDialogueHelperWrapper.invokeRequest() invoked");
        return null;
    }

    public String invokeRequest(String sender, String receiver, String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        return null;
    }

    public void setConnectionTimeout(int connectionTimeout) {
    }

    public void setReceiveTimeout(int receiveTimeout) {
    }

    public boolean validateRequestResponses(List<ACLMessage> answers) {
        return false;
    }

    
}
