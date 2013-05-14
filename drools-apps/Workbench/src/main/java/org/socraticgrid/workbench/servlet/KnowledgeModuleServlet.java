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
package org.socraticgrid.workbench.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.socraticgrid.workbench.model.ext.PagedResult;
import org.socraticgrid.workbench.service.KnowledgeModuleServiceProcessor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.socraticgrid.kmr.kmtypes.ReferenceDataRefDataType;

/**
 *
 * @author esteban
 */
public class KnowledgeModuleServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 490725496946388102L;

    /**
     * parameter example:
     * ?reference=factcode&lookuptype=LAB&lookupsystem=LOINC&lookupcode=bromo&MaxRecordsToShow=10&StartingRecNumber=0
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    @SuppressWarnings("unchecked")
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("\n====> ENTERING KmReferencesHandler.doGet");

        String myparams = request.getQueryString();
        //------------------------------------------------
        //Init call to KMR Search Ref Data service
        //------------------------------------------------		
        PagedResult<ReferenceDataRefDataType> results;

        long time1 = System.currentTimeMillis();
	
        try {
            results = this.kmGetReferences(myparams);
        } catch (Exception ex) {
            throw new ServletException(ex);
            //TODO: Remove this! This is only for testing when the WS is down
            //or unreachable
//            String limit = request.getParameter("limit");
//            if (limit == null){
//                limit = "10";
//            }
//            
//            results = this.createMockData(Integer.parseInt(limit));
        }
        long time2 = System.currentTimeMillis();

        System.out.println("Total Query time= " + (time2 - time1) / 1000 + " seg");

        JsonArray list = new JsonArray();

        //------------------------------------------------
        //Build a JsonObject of each reference data 
        //(each reference data contains three element data)
        //and push into list.
        //------------------------------------------------
        for (int i = 0; i < results.getResults().size(); i++) {
            JsonObject referenceObject = new JsonObject();

            referenceObject.addProperty("id", results.getResults().get(i).getDescr());
            referenceObject.addProperty("name", results.getResults().get(i).getName());
            referenceObject.addProperty("descr", results.getResults().get(i).getDescr()+ "->" + results.getResults().get(i).getName());

            list.add(referenceObject);
        }
        //------------------------------------------------
        //Add JSONArray of all references to a JSONObject tagged as "refdata".
        //------------------------------------------------

        JsonObject out = new JsonObject();
        out.addProperty("totalrecsfound", results.getTotal());
        out.add("refdata", list);

        //------------------------------------------------
        //Write out response to HttpServletResponse ("response")
        //------------------------------------------------	
        Gson gson = new Gson();
        response.getWriter().write(gson.toJson(out));
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("\n====> ENTERING KmReferencesHandler.doPost ... NOT IMPLEMENTED");
    }

    /*
     * Returns all references that match requested type
     */
    private PagedResult<ReferenceDataRefDataType> kmGetReferences(String params) {

        //generating a random for now, cause not really needed yet. 
        String reqId = "REF-9999";
        
        PagedResult<ReferenceDataRefDataType> results = KnowledgeModuleServiceProcessor.getInstance().getReferenceData(params, reqId);

        /*
         * .........DEBUG ONLY ......................
         * System.out.println("\n====> TotalSize="+results.size()); if
         * (results.size() > 0) { for (int i=0; i < results.size(); i++) {
         * System.out.println("[kmGetReferences]result("+i+")="+results.get(i).getName());
         * } } else { System.out.println("\n====> result is empty"); }
         * .............................................
         */

        /*
         *
         */

        return results;
    }

    private PagedResult<ReferenceDataRefDataType> createMockData(int ammount) {

        String[] terms = new String[]{
            "Medical",
            "Medication",
            "Allergy",
            "Clinic",
            "Ventricular",
            "DCIS",
            "DDH (developmental dislocation of the hip)",
            "De Quervain's tenosynovitis",
            "DEA",
            "Deafness",
            "Deafness with goiter",
            "Achondroplasia",
            "Achoo syndrome",
            "Achromatopsia",
            "Achromycin",
            "Acid deposition",
            "Acid indigestion",
            "Acid phosphatase",
            "Acid rain",
            "Acid reflux",
            "Acid, amino",
            "Acid, bile",
            "Acid, fatty",
            "Acid, folic",
            "Acid, nucleic",
            "Acid, pantothenic",
            "Acid, trans fatty",
            "Acid-base balance",
            "Acidophilus",
            "Acidosis",
            "Acinetobacter",
            "Acinus, pulmonary"
        };

        Random rnd = new Random();
        List<ReferenceDataRefDataType> results = new ArrayList<ReferenceDataRefDataType>();
        for (int i = 0; i < ammount; i++) {
            ReferenceDataRefDataType r = new ReferenceDataRefDataType();
            r.setName(terms[rnd.nextInt(terms.length)] + " " + terms[rnd.nextInt(terms.length)]);
            r.setDescr(System.nanoTime() + "");

            results.add(r);
        }

        return new PagedResult<ReferenceDataRefDataType>(ammount, 0, ammount, results);
    }
}
