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

import com.socraticgrid.kmr.knowledgemodule.KmrKnowledgeModule;
import com.socraticgrid.kmr.knowledgemodule.KmrKnowledgeModuleService;
import com.socraticgrid.kmr.knowledgemodule.ObjectFactory;
import org.socraticgrid.workbench.model.ext.PagedResult;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.socraticgrid.kmr.kmtypes.ReferenceDataLookupRefTypeCode;
import org.socraticgrid.kmr.kmtypes.ReferenceDataLookupRefTypeSystem;
import org.socraticgrid.kmr.kmtypes.ReferenceDataLookupRefTypeType;
import org.socraticgrid.kmr.kmtypes.ReferenceDataLookupType;
import org.socraticgrid.kmr.kmtypes.ReferenceDataRefDataType;
import org.socraticgrid.kmr.kmtypes.ReferenceDataRefRequestType;
import org.socraticgrid.kmr.kmtypes.ReferenceDataRefResponseType;

/**
 *
 * @author esteban
 */
public class KnowledgeModuleServiceProcessor {

    private static KnowledgeModuleServiceProcessor instance = null;
    private String cname = "[KMRProcessor] ";  //debug purpose

    public static synchronized KnowledgeModuleServiceProcessor getInstance() {
        if (instance == null) {
            instance = new KnowledgeModuleServiceProcessor();
        }
        return instance;
    }

    private KnowledgeModuleServiceProcessor() {
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
    
    public synchronized PagedResult<ReferenceDataRefDataType> getReferenceData(String params, String requestId) {

        List<ReferenceDataRefDataType> results = new ArrayList<ReferenceDataRefDataType>();
        ReferenceDataRefRequestType request = new ReferenceDataRefRequestType();
        ReferenceDataLookupType lookup = new ReferenceDataLookupType();

        String reference = null;

        int maxRecordsToShow = 200;
        int startingRecNumber = 0;

        //parse params for the requested reference value.
        for (String pp : params.split("&")) {
            StringTokenizer Tok = new StringTokenizer(pp, "=");
            int n = 0;
            String element;
            while (Tok.hasMoreElements()) {
                element = (String) Tok.nextElement();
                //System.out.println("=====> " + ++n +"[getReferenceData/elem]:"+element);

                if (element.equals("reference") && Tok.hasMoreElements()) {
                    reference = (String) Tok.nextElement();
                }
                if (element.equals("MaxRecordsToShow") && Tok.hasMoreElements()) {
                    maxRecordsToShow = new Integer((String) Tok.nextElement());
                }
                if (element.equals("limit") && Tok.hasMoreElements()) {
                    //also support default Extjs paging attributes
                    maxRecordsToShow = new Integer((String) Tok.nextElement());
                }
                if (element.equals("StartingRecNumber") && Tok.hasMoreElements()) {
                    startingRecNumber = new Integer((String) Tok.nextElement());
                }
                if (element.equals("start") && Tok.hasMoreElements()) {
                    //also support default Extjs paging attributes
                    startingRecNumber = new Integer((String) Tok.nextElement());
                }
            }
        }

        System.out.println("\n" + this.cname + "REQUESTED REFERENCE=" + reference);
        System.out.println(this.cname + params);
        
        ObjectFactory objectFactory = new ObjectFactory();
        
        //----------------------------------------------------
        //		Create KMR Service Reference Lookup request 
        //		based on incoming params.
        //----------------------------------------------------
        if (reference.equalsIgnoreCase("facttype")
                || reference.equalsIgnoreCase("tasktype")
                || reference.equalsIgnoreCase("usedfacttype")
                || reference.equalsIgnoreCase("useddemogtype")
                || reference.equalsIgnoreCase("usedtasktype")) {
            lookup.setLookupType(this.getLookupType(params));
        }
        if (reference.equalsIgnoreCase("factscheme")
                || reference.equalsIgnoreCase("taskscheme")
                || reference.equalsIgnoreCase("demogscheme")
                || reference.equalsIgnoreCase("specialtyscheme")
                || reference.equalsIgnoreCase("usedfactscheme")
                || reference.equalsIgnoreCase("usedtaskscheme")
                || reference.equalsIgnoreCase("useddemogscheme")
                || reference.equalsIgnoreCase("usedspecialtyscheme")) {
            lookup.setLookupSystem(this.getLookupSystem(params));
        }
        if (reference.equalsIgnoreCase("factcode")
                || reference.equalsIgnoreCase("taskcode")
                || reference.equalsIgnoreCase("demogcode")
                || reference.equalsIgnoreCase("specialtycode")
                || reference.equalsIgnoreCase("usedfactcode")
                || reference.equalsIgnoreCase("usedtaskcode")
                || reference.equalsIgnoreCase("useddemogcode")
                || reference.equalsIgnoreCase("useddemogstatus")
                || reference.equalsIgnoreCase("useddemogacuity")
                || reference.equalsIgnoreCase("usedspecialtycode")
                || reference.equalsIgnoreCase("usedkmtype")
                || reference.equalsIgnoreCase("usedorglevel")
                || reference.equalsIgnoreCase("usedkmstatus")) {
            lookup.setLookupCode(this.getLookupCode(params));
        }

        //SWITCHING a demog GENDER request to be a demog VALUE request
        //with 6 (type value for GENDER) as a hardcoded demog type.
        if (reference.equalsIgnoreCase("useddemoggender")) {
            lookup.setLookupCode(this.getLookupCode(params));
            reference = "useddemogvalue";
        }

        //SWITCHING a demog AGERANGE request to be a demog VALUE request
        //with 7 (type value for AGERANGE) as a hardcoded demog type.
        if (reference.equalsIgnoreCase("useddemogagerange")) {
            lookup.setLookupCode(this.getLookupCode(params));
            reference = "useddemogvalue";
        }

        lookup.setReference(reference);
        lookup.setMaxRecordsToShow(maxRecordsToShow);
        lookup.setStartingRecNumber(startingRecNumber);
        request.setLookup(lookup);


        //----------------------------------------------------
        //		Send KMR Service Reference Lookup request 
        //		and retrieve the Response.
        //----------------------------------------------------


        
        
        // Call Web Service Operation
        KmrKnowledgeModuleService service = new  KmrKnowledgeModuleService();
        KmrKnowledgeModule port = service.getKmrKnowledgeModulePort();

        ReferenceDataRefResponseType response = port.getReferenceData(request);



        if (response.getResponse() != null) {
            System.out.println(this.cname + "RESPONSE LENGTH= " + response.getResponse().getTotalReferencesFound());
            
            for (ReferenceDataRefDataType referenceDataRefDataType : response.getResponse().getRefData()) {
                ReferenceDataRefDataType aRef = new ReferenceDataRefDataType();
                aRef.setId(referenceDataRefDataType.getId());
                aRef.setName(referenceDataRefDataType.getName());
                aRef.setDescr(referenceDataRefDataType.getDescr());
                results.add(aRef);
            }

        }

        return new PagedResult<ReferenceDataRefDataType>(response.getResponse().getTotalReferencesFound(), startingRecNumber, maxRecordsToShow, results);
    }

    private ReferenceDataLookupRefTypeType getLookupType(String inParams) {

        ObjectFactory objectFactory = new ObjectFactory();
        
        ReferenceDataLookupRefTypeType lookup_type = new ReferenceDataLookupRefTypeType();
        //System.out.println("=====>getLookupType: ");

        //split apart the different elements/values and set km objects as needed
        for (String p : inParams.split("&")) {
            StringTokenizer Tok = new StringTokenizer(p, "=");
            int n = 0;
            String element = null;
            while (Tok.hasMoreElements()) {
                element = (String) Tok.nextElement();
                //System.out.println("=====> " + ++n +"[elem]:"+element);

                if (element.equals("lookuptype") && Tok.hasMoreElements()) {
                    lookup_type.setCodeType((String) Tok.nextElement());
                }
            }
        }
        return lookup_type;
    }

    private ReferenceDataLookupRefTypeSystem getLookupSystem(String inParams) {

        ObjectFactory objectFactory = new ObjectFactory();
        
        ReferenceDataLookupRefTypeSystem lookup_system = new ReferenceDataLookupRefTypeSystem();
        //System.out.println("=====>getLookupSystem");

        //split apart the different elements/values and set km objects as needed
        for (String p : inParams.split("&")) {
            StringTokenizer Tok = new StringTokenizer(p, "=");
            int n = 0;
            String element;
            while (Tok.hasMoreElements()) {
                element = (String) Tok.nextElement();
                //System.out.println("=====> " + ++n +"[elem]:"+element);

                if (element.equals("lookuptype") && Tok.hasMoreElements()) {
                    lookup_system.setCodeType((String) Tok.nextElement());
                } else if (element.equals("lookupsystem") && Tok.hasMoreElements()) {
                    lookup_system.setCodeSystem((String) Tok.nextElement());
                }
            }
        }
        return lookup_system;
    }

    private ReferenceDataLookupRefTypeCode getLookupCode(String inParams) {

        ObjectFactory objectFactory = new ObjectFactory();
        
        ReferenceDataLookupRefTypeCode lookup_code = new ReferenceDataLookupRefTypeCode();

        //System.out.println("=====>getLookupCode");

        //split apart the different elements/values and set km objects as needed
        for (String p : inParams.split("&")) {
            StringTokenizer Tok = new StringTokenizer(p, "=");
            int n = 0;
            String element;
            while (Tok.hasMoreElements()) {
                element = (String) Tok.nextElement();
                //System.out.println("=====> " + ++n +"[elem]:"+element);

                if (element.equals("lookuptype") && Tok.hasMoreElements()) {
                    lookup_code.setCodeType((String) Tok.nextElement());
                } else if (element.equals("lookupsystem") && Tok.hasMoreElements()) {
                    lookup_code.setCodeSystem((String) Tok.nextElement());
                } else if ((element.equals("lookupcode") || element.equals("query")) && Tok.hasMoreElements()) {
                    lookup_code.setContentCode((String) Tok.nextElement());
                }
            }
        }
        return lookup_code;
    }

}
