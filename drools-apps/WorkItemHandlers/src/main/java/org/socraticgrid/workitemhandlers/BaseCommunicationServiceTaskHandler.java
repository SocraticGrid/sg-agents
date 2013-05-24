/***********************************************************************************************************************
 *
 * Copyright (C) 2012 by Cognitive Medical Systems, Inc (http://www.cognitivemedciine.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/
 
 /***********************************************************************************************************************
 * Socratic Grid contains components to which third party terms apply. To comply with these terms, the following notice is provided:
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 * Copyright (c) 2008, Nationwide Health Information Network (NHIN) Connect. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of the NHIN Connect Project nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * END OF TERMS AND CONDITIONS
 *
 **********************************************************************************************************************/
package org.socraticgrid.workitemhandlers;

import com.google.gson.Gson;
import org.drools.mas.action.communication.CommunicationHandlerConfiguration;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.socraticgrid.workitemhandlers.communication.CommunicationHandlerConfigurationParser;

/**
 *
 * @author esteban
 */
public class BaseCommunicationServiceTaskHandler implements WorkItemHandler  {
    
    private KnowledgeRuntime kruntime;

    public BaseCommunicationServiceTaskHandler(StatefulKnowledgeSession ksession) {
        this.kruntime = ksession;
    }
    
    public BaseCommunicationServiceTaskHandler(KnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
    }
    
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        
        String configurationString = (String) workItem.getParameter("Configuration");
        
        CommunicationHandlerConfiguration configuration = new CommunicationHandlerConfigurationParser().parseString(configurationString);
        
        kruntime.insert(configuration);
        
        manager.completeWorkItem(workItem.getId(), null);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    }

    private String[] parameterAsCollection(String parameter){
        if (parameter == null || parameter.trim().isEmpty()){
            return new String[0];
        }
        
        Gson gson = new Gson();
        return gson.fromJson(parameter, String[].class); 
        
    }
}
