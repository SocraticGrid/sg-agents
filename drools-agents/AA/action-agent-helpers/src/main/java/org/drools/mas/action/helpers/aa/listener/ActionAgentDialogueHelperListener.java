/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.action.helpers.aa.listener;

import java.util.List;
import org.drools.mas.ACLMessage;

/**
 *
 * @author esteban
 */
public interface ActionAgentDialogueHelperListener {

    public void onSuccess(List<ACLMessage> messages);

    public void onError(Throwable t);
}
