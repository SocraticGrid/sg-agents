/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.action.helpers.aa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.drools.mas.ACLMessage;
import org.drools.mas.action.communication.CommunicationHandlerConfiguration;
import org.drools.mas.action.helpers.aa.listener.ActionAgentDialogueHelperListener;
import org.drools.mas.helpers.DialogueHelperCallbackImpl;
import org.drools.mas.util.EndPointHelper;

/**
 * Specialized DialogueHelper to deal with Action-Agent
 * @author esteban
 */
public class ActionAgentDialogueHelper {
    /**
     * This value can be used in META-INF/service.endpoint.properties 
     * in order to use a mocked action-agent
     */
    public final static String MOCK_ENDPOINT = "MOCK"; 
    public final static String AA_ENDPOINT_KEY = "AAEndpoint"; 
    
    public final static String SENDER_NAME = "ActionAgentDialogueHelper"; 
    public final static String RECEIVER_NAME = "Action-Agent"; 
    
    
    private DialogueHelperWrapper dialogueHelperWrapper;
    
    private List<ActionAgentDialogueHelperListener> listeners = Collections.synchronizedList(new ArrayList<ActionAgentDialogueHelperListener>());
    
    public ActionAgentDialogueHelper() {
        String aaEndPoint = EndPointHelper.getEndPoint(AA_ENDPOINT_KEY);
        
        if (aaEndPoint == null || aaEndPoint.equals(MOCK_ENDPOINT)){
            dialogueHelperWrapper = new InnocuousDialogueHelperWrapper();
        } else{
            dialogueHelperWrapper = new DialogueHelperWrapperImpl(aaEndPoint);
        }
    }

    public void invokeActionAgent(CommunicationHandlerConfiguration data){
        
        this.dialogueHelperWrapper.invokeInform(SENDER_NAME, RECEIVER_NAME, data, new DialogueHelperCallbackImpl(){

            @Override
            public void onSuccess(List<ACLMessage> messages) {
                for (ActionAgentDialogueHelperListener listener : listeners) {
                    listener.onSuccess(messages);
                }
            }

            @Override
            public void onError(Throwable t) {
                for (ActionAgentDialogueHelperListener listener : listeners) {
                    listener.onError(t);
                }
            }

            @Override
            public int getExpectedResponsesNumber() {
                return 0;
            }
            
        });
        
    }

    public boolean addListener(ActionAgentDialogueHelperListener e) {
        return listeners.add(e);
    }

    public boolean removeListener(ActionAgentDialogueHelperListener o) {
        return listeners.remove(o);
    }
    
    
    
    
}
