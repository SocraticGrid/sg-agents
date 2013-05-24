/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.action.helpers.aa;

import java.util.LinkedHashMap;
import java.util.List;
import org.drools.mas.ACLMessage;
import org.drools.mas.Encodings;
import org.drools.mas.body.content.Query;
import org.drools.mas.helpers.DialogueHelper;
import org.drools.mas.helpers.DialogueHelperCallback;

/**
 *
 * @author esteban
 */
public class DialogueHelperWrapperImpl implements DialogueHelperWrapper {

    private DialogueHelper helper;
    
    public DialogueHelperWrapperImpl(String url) {
        helper = new DialogueHelper(url);
    }

    public DialogueHelperWrapperImpl(String url, Encodings enc) {
        helper = new DialogueHelper(url, enc);
    }

    public DialogueHelperWrapperImpl(String url, int wSDLRetrievalTimeout) {
        helper = new DialogueHelper(url, wSDLRetrievalTimeout);
    }

    public DialogueHelperWrapperImpl(String url, Encodings enc, int wSDLRetrievalTimeout) {
        helper = new DialogueHelper(url, enc, wSDLRetrievalTimeout);
    }

    @Override
    public String invokeRequest(String sender, String receiver, String methodName, LinkedHashMap<String, Object> args, DialogueHelperCallback callback) throws UnsupportedOperationException {
        return helper.invokeRequest(sender, receiver, methodName, args, callback);
    }

    @Override
    public String invokeRequest(String methodName, LinkedHashMap<String, Object> args, DialogueHelperCallback callback) throws UnsupportedOperationException {
        return helper.invokeRequest(methodName, args, callback);
    }

    @Override
    public String invokeRequest(String sender, String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        return helper.invokeRequest(sender, methodName, args);
    }

    @Override
    public String invokeRequest(String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        return helper.invokeRequest(methodName, args);
    }

    @Override
    public String invokeRequest(String sender, String receiver, String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        return helper.invokeRequest(sender, receiver, methodName, args);
    }

    @Override
    public String invokeQueryIf(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        return helper.invokeQueryIf(sender, receiver, proposition, callback);
    }

    @Override
    public String invokeQueryIf(String sender, String receiver, Object proposition) {
        return helper.invokeQueryIf(sender, receiver, proposition);
    }

    @Override
    public String invokeQueryRef(String sender, String receiver, Query query, DialogueHelperCallback callback) {
        return helper.invokeQueryRef(sender, receiver, query, callback);
    }

    @Override
    public String invokeInform(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        return helper.invokeInform(sender, receiver, proposition, callback);
    }

    @Override
    public String invokeInform(String sender, String receiver, Object proposition) {
        return helper.invokeInform(sender, receiver, proposition);
    }

    @Override
    public String invokeConfirm(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        return helper.invokeConfirm(sender, receiver, proposition, callback);
    }

    @Override
    public String invokeConfirm(String sender, String receiver, Object proposition) {
        return helper.invokeConfirm(sender, receiver, proposition);
    }

    @Override
    public String invokeDisconfirm(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        return helper.invokeDisconfirm(sender, receiver, proposition, callback);
    }

    @Override
    public String invokeDisconfirm(String sender, String receiver, Object proposition) {
        return helper.invokeDisconfirm(sender, receiver, proposition);
    }

    @Override
    public boolean validateRequestResponses(List<ACLMessage> answers) {
        return helper.validateRequestResponses(answers);
    }

    @Override
    public Object extractReturn(ACLMessage msg, boolean decode) throws UnsupportedOperationException {
        return helper.extractReturn(msg, decode);
    }

    @Override
    public List<ACLMessage> getAgentAnswers(String reqId) {
        return helper.getAgentAnswers(reqId);
    }

    @Override
    public void setConnectionTimeout(int connectionTimeout) {
        helper.setConnectionTimeout(connectionTimeout);
    }

    @Override
    public void setReceiveTimeout(int receiveTimeout) {
        helper.setReceiveTimeout(receiveTimeout);
    }
    
    
}
