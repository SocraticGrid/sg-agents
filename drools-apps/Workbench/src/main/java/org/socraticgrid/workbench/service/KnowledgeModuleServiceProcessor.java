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
package org.socraticgrid.workbench.service;

import java.util.StringTokenizer;
import org.socraticgrid.workbench.model.ext.PagedResult;
import org.socraticgrid.workbench.service.knowledgemodule.KnowledgeModuleService;
import org.socraticgrid.workbench.service.knowledgemodule.ReferenceData;
import org.socraticgrid.workbench.service.knowledgemodule.ReferenceDataRequest;
import org.socraticgrid.workbench.setting.ApplicationSettings;

/**
 *
 * @author esteban
 */
public class KnowledgeModuleServiceProcessor {

    private static KnowledgeModuleServiceProcessor instance = null;

    public static synchronized KnowledgeModuleServiceProcessor getInstance() {
        if (instance == null) {
            instance = new KnowledgeModuleServiceProcessor();
        }
        return instance;
    }
    
    private KnowledgeModuleService knowledgeModuleService;

    private KnowledgeModuleServiceProcessor() {
        try {
            String implName = ApplicationSettings.getInstance().getSetting("knowledge.module.service.impl");
            this.knowledgeModuleService = (KnowledgeModuleService) Class.forName(implName).newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 
     * @param params
    ---------------------------------
    Incoming PARAMS
    Reference can be (...for now..)
    reference=aaaa
    
    ====================================================
    Possible References who have TYPE association
    ====================================================
    facttype     OR factscheme          OR factcode
    tasktype     OR taskscheme          OR taskcode
    usedfacttype OR usedfactscheme      OR usedfactcode
    usedtasktype OR usedtaskscheme      OR usedtaskcode
    demogtype    OR demogscheme         OR demogcode    OR demogvalue
    useddemogtype OR useddemogscheme    OR useddemogcode OR useddemogvalue
    ====================================================
    Possible References who do NOT have TYPE association
    ====================================================
    specialtyscheme     OR specialtycode
    usedspecialtyscheme OR usedspecialtycode
    ====================================================
    
    Lookup_Type can be:
    lookuptype=xxx
    
    Lookup_System can be:
    lookuptype=xxx&lookupsystem=yyyy
    
    Lookup_Code can be:
    lookuptype=xxx&lookupsystem=yyyy&lookupcode=zzzz
    ---------------------------------
    
    Full example showing a request for a Fact Code based on a 
    particular Fact Type and Scheme:
    
    reference=factcode&lookuptype=LAB&lookupsystem=LOINC				
    ---------------------------------
    
    //----------------- TESTER ---------------------------
    //this.getReferenceData(
    //		"ext-comp-1055=Options% loaded% using% Ajax...&reference=factcode&MaxRecordsToShow=500&lookuptype=LAB&lookupsystem=LOINC"		
    //		,"abdc99");
    //this.getReferenceData(
    //		"ext-comp-1055=Options% loaded% using% Ajax...&reference=facttype&MaxRecordsToShow=500"
    //		,"abdc99");
    //this.getReferenceData(
    //		"ext-comp-1055=Options% loaded% using% Ajax...&reference=factscheme&MaxRecordsToShow=500&lookuptype=LAB"
    //		,"abdc99");
    //----------------- TESTER ----------------------------
     * @param requestId
     * @return
     */
    
    public synchronized PagedResult<ReferenceData> getReferenceData(String params, String requestId) {
        return knowledgeModuleService.getReferenceData(this.parseParams(params));
    }
    
    private ReferenceDataRequest parseParams(String params){
        String reference = null;
        String lookupType = null;
        String lookupSystem = null;
        String lookupCode = null;

        int maxRecordsToShow = 200;
        int startingRecNumber = 0;

        //parse params for the requested reference value.
        for (String pp : params.split("&")) {
            StringTokenizer tok = new StringTokenizer(pp, "=");
            int n = 0;
            String element;
            while (tok.hasMoreElements()) {
                element = (String) tok.nextElement();
                //System.out.println("=====> " + ++n +"[getReferenceData/elem]:"+element);

                if (element.equals("reference") && tok.hasMoreElements()) {
                    reference = (String) tok.nextElement();
                }
                if (element.equals("MaxRecordsToShow") && tok.hasMoreElements()) {
                    maxRecordsToShow = new Integer((String) tok.nextElement());
                }
                if (element.equals("limit") && tok.hasMoreElements()) {
                    //also support default Extjs paging attributes
                    maxRecordsToShow = new Integer((String) tok.nextElement());
                }
                if (element.equals("StartingRecNumber") && tok.hasMoreElements()) {
                    startingRecNumber = new Integer((String) tok.nextElement());
                }
                if (element.equals("start") && tok.hasMoreElements()) {
                    //also support default Extjs paging attributes
                    startingRecNumber = new Integer((String) tok.nextElement());
                }
                if (element.equals("lookuptype") && tok.hasMoreElements()) {
                    lookupType = (String) tok.nextElement();
                } else if (element.equals("lookupsystem") && tok.hasMoreElements()) {
                    lookupSystem = (String) tok.nextElement();
                } else if ((element.equals("lookupcode") || element.equals("query")) && tok.hasMoreElements()) {
                    lookupCode = (String) tok.nextElement();
                }
            }
        }
        
        return new ReferenceDataRequest(reference, lookupType, lookupSystem, lookupCode, startingRecNumber, maxRecordsToShow);
        
    }

}
