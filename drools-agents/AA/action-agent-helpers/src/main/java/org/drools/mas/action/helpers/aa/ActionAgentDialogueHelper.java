/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.action.helpers.aa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.mas.ACLMessage;
import org.drools.mas.action.communication.CommunicationHandlerConfiguration;
import org.drools.mas.action.helpers.aa.listener.ActionAgentDialogueHelperListener;
import org.drools.mas.helpers.DialogueHelperCallbackImpl;
import org.drools.mas.util.EndPointHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specialized DialogueHelper to deal with Action-Agent
 *
 * @author esteban
 */
public class ActionAgentDialogueHelper {

    private static Logger logger = LoggerFactory.getLogger(ActionAgentDialogueHelper.class);
    
    /**
     * This value can be used in META-INF/service.endpoint.properties in order
     * to use a mocked action-agent
     */
    public final static String MOCK_ENDPOINT = "MOCK";
    public final static String AA_ENDPOINT_KEY = "AAEndpoint";
    public final static String SENDER_NAME = "ActionAgentDialogueHelper";
    public final static String RECEIVER_NAME = "Action-Agent";
    private DialogueHelperWrapper dialogueHelperWrapper;
    private final List<ActionAgentDialogueHelperListener> listeners = Collections.synchronizedList(new ArrayList<ActionAgentDialogueHelperListener>());

    /**
     * Creates an instance of ActionAgentDialogueHelper pointing to the provided
     * endpoint. ActionAgentDialogueHelper.MOCK_ENDPOINT can be used as parameter
     * ins order to use a mocked endpoint.
     * @param aaEndPoint 
     */
    public ActionAgentDialogueHelper(String aaEndPoint) {
        this.configureDialogueHelperWrapper(aaEndPoint);
    }
    
    /**
     * Creates an instance of ActionAgentDialogueHelper taking the configuration
     * from /META-INF/service.endpoint.properties
     */
    public ActionAgentDialogueHelper() {
        String aaEndPoint = EndPointHelper.getEndPoint(AA_ENDPOINT_KEY);
        this.configureDialogueHelperWrapper(aaEndPoint);
    }
    
    private void configureDialogueHelperWrapper(String endpoint){
        if (endpoint == null || endpoint.equals(MOCK_ENDPOINT)) {
            logger.debug("No Agent endpoint provided, using MOCKED endpoint!");
            dialogueHelperWrapper = new InnocuousDialogueHelperWrapper();
        } else {
            dialogueHelperWrapper = new DialogueHelperWrapperImpl(endpoint);
        }
    }

    public void invokeActionAgent(List<String> receivers, List<String> subjects, List<String> channels, List<String> templates, List<String> timeouts) {
        this.invokeActionAgent(receivers, subjects, channels, templates, timeouts, new HashMap<String, Object>());
    }
    
    public void invokeActionAgent(List<String> receivers, List<String> subjects, List<String> channels, List<String> templates, List<String> timeouts, Map<String, Object> templateVariables) {
        CommunicationHandlerConfiguration configuration = new CommunicationHandlerConfiguration();
        configuration.setReceivers(receivers);
        configuration.setSubjects(subjects);
        configuration.setChannels(channels);
        configuration.setTemplates(templates);
        configuration.setTimeouts(timeouts);
        configuration.setTemplateVariables(templateVariables);
        
        this.invokeActionAgent(configuration);
    }

    public void invokeActionAgent(CommunicationHandlerConfiguration data) {

        this.dialogueHelperWrapper.invokeInform(SENDER_NAME, RECEIVER_NAME, data, new DialogueHelperCallbackImpl() {
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
