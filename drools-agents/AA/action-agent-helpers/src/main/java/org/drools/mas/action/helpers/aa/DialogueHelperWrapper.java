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
public interface DialogueHelperWrapper {

    Object extractReturn(ACLMessage msg, boolean decode) throws UnsupportedOperationException;

    List<ACLMessage> getAgentAnswers(String reqId);

    String invokeConfirm(String sender, String receiver, Object proposition, DialogueHelperCallback callback);

    String invokeConfirm(String sender, String receiver, Object proposition);

    String invokeDisconfirm(String sender, String receiver, Object proposition, DialogueHelperCallback callback);

    String invokeDisconfirm(String sender, String receiver, Object proposition);

    String invokeInform(String sender, String receiver, Object proposition, DialogueHelperCallback callback);

    String invokeInform(String sender, String receiver, Object proposition);

    String invokeQueryIf(String sender, String receiver, Object proposition, DialogueHelperCallback callback);

    String invokeQueryIf(String sender, String receiver, Object proposition);

    String invokeQueryRef(String sender, String receiver, Query query, DialogueHelperCallback callback);

    String invokeRequest(String sender, String receiver, String methodName, LinkedHashMap<String, Object> args, DialogueHelperCallback callback) throws UnsupportedOperationException;

    String invokeRequest(String methodName, LinkedHashMap<String, Object> args, DialogueHelperCallback callback) throws UnsupportedOperationException;

    String invokeRequest(String sender, String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException;

    String invokeRequest(String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException;

    String invokeRequest(String sender, String receiver, String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException;

    void setConnectionTimeout(int connectionTimeout);

    void setReceiveTimeout(int receiveTimeout);

    boolean validateRequestResponses(List<ACLMessage> answers);
    
}
