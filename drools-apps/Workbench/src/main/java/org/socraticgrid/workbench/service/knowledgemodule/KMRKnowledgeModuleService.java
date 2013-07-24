/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.socraticgrid.workbench.service.knowledgemodule;

import com.socraticgrid.kmr.knowledgemodule.KmrKnowledgeModule;
import com.socraticgrid.kmr.knowledgemodule.KmrKnowledgeModuleService;
import java.util.ArrayList;
import java.util.List;
import org.socraticgrid.kmr.kmtypes.ReferenceDataLookupRefTypeCode;
import org.socraticgrid.kmr.kmtypes.ReferenceDataLookupRefTypeSystem;
import org.socraticgrid.kmr.kmtypes.ReferenceDataLookupRefTypeType;
import org.socraticgrid.kmr.kmtypes.ReferenceDataLookupType;
import org.socraticgrid.kmr.kmtypes.ReferenceDataRefDataType;
import org.socraticgrid.kmr.kmtypes.ReferenceDataRefRequestType;
import org.socraticgrid.kmr.kmtypes.ReferenceDataRefResponseType;
import org.socraticgrid.workbench.model.ext.PagedResult;

public class KMRKnowledgeModuleService implements KnowledgeModuleService {

    public PagedResult<ReferenceData> getReferenceData(ReferenceDataRequest request) {

        ReferenceDataRefRequestType dataRequest = new ReferenceDataRefRequestType();
        ReferenceDataLookupType lookup = new ReferenceDataLookupType();
        
        String reference = request.getReference();
        
        
        //----------------------------------------------------
        //		Create KMR Service Reference Lookup request 
        //		based on incoming params.
        //----------------------------------------------------
        if (reference.equalsIgnoreCase("facttype")
                || reference.equalsIgnoreCase("tasktype")
                || reference.equalsIgnoreCase("usedfacttype")
                || reference.equalsIgnoreCase("useddemogtype")
                || reference.equalsIgnoreCase("usedtasktype")) {
            
            lookup.setLookupType(this.getLookupType(request));
        }
        if (reference.equalsIgnoreCase("factscheme")
                || reference.equalsIgnoreCase("taskscheme")
                || reference.equalsIgnoreCase("demogscheme")
                || reference.equalsIgnoreCase("specialtyscheme")
                || reference.equalsIgnoreCase("usedfactscheme")
                || reference.equalsIgnoreCase("usedtaskscheme")
                || reference.equalsIgnoreCase("useddemogscheme")
                || reference.equalsIgnoreCase("usedspecialtyscheme")) {
            
            lookup.setLookupSystem(this.getLookupSystem(request));
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
            
            lookup.setLookupCode(this.getLookupCode(request));
        }
        
        
        //SWITCHING a demog GENDER request to be a demog VALUE request
        //with 6 (type value for GENDER) as a hardcoded demog type.
        if (reference.equalsIgnoreCase("useddemoggender")) {
            lookup.setLookupCode(this.getLookupCode(request));
            reference = "useddemogvalue";
        }

        //SWITCHING a demog AGERANGE request to be a demog VALUE request
        //with 7 (type value for AGERANGE) as a hardcoded demog type.
        if (reference.equalsIgnoreCase("useddemogagerange")) {
            lookup.setLookupCode(this.getLookupCode(request));
            reference = "useddemogvalue";
        }

        lookup.setReference(reference);
        lookup.setMaxRecordsToShow(request.getMaxRecordsToShow());
        lookup.setStartingRecNumber(request.getStartingRecNumber());
        dataRequest.setLookup(lookup);
        
        //----------------------------------------------------
        //		Send KMR Service Reference Lookup request 
        //		and retrieve the Response.
        //----------------------------------------------------
        
        // Call Web Service Operation
        KmrKnowledgeModuleService service = new  KmrKnowledgeModuleService();
        KmrKnowledgeModule port = service.getKmrKnowledgeModulePort();

        ReferenceDataRefResponseType response = port.getReferenceData(dataRequest);

        if (response.getResponse() != null) {
            return new PagedResult<ReferenceData>(response.getResponse().getTotalReferencesFound(), request.getStartingRecNumber(), request.getMaxRecordsToShow(), this.convertResults(response.getResponse().getRefData()));
        }

        return new PagedResult<ReferenceData>(0, request.getStartingRecNumber(), request.getMaxRecordsToShow(), new ArrayList<ReferenceData>());
        
    }
    
    private ReferenceDataLookupRefTypeType getLookupType(ReferenceDataRequest request){
        ReferenceDataLookupRefTypeType lookupType = new ReferenceDataLookupRefTypeType();
        lookupType.setCodeType(request.getLookupType());
        
        return lookupType;
    }
    
    private ReferenceDataLookupRefTypeSystem getLookupSystem(ReferenceDataRequest request){
        ReferenceDataLookupRefTypeSystem lookupSystem = new ReferenceDataLookupRefTypeSystem();
        lookupSystem.setCodeType(request.getLookupType());
        lookupSystem.setCodeSystem(request.getLookupSystem());
        
        return lookupSystem;
    }
    
    private ReferenceDataLookupRefTypeCode getLookupCode(ReferenceDataRequest request){
        ReferenceDataLookupRefTypeCode lookupCode = new ReferenceDataLookupRefTypeCode();
        lookupCode.setCodeType(request.getLookupType());
        lookupCode.setCodeSystem(request.getLookupSystem());
        lookupCode.setContentCode(request.getLookupCode());
        
        return lookupCode;
    }

    private List<ReferenceData> convertResults(List<ReferenceDataRefDataType> originalResults){
        List<ReferenceData> results = new ArrayList<ReferenceData>();
        if (originalResults != null){
            for (ReferenceDataRefDataType referenceDataRefDataType : originalResults) {
                ReferenceData data = new ReferenceData(referenceDataRefDataType.getId(), referenceDataRefDataType.getName(), referenceDataRefDataType.getDescr());
                results.add(data);
            }
        }
        return results;
    }
}
