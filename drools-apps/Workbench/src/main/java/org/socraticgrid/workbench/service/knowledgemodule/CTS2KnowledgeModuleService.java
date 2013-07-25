/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.socraticgrid.workbench.service.knowledgemodule;

import edu.mayo.cts2.framework.core.client.Cts2RestClient;
import edu.mayo.cts2.framework.model.core.EntitySynopsis;
import edu.mayo.cts2.framework.model.valuesetdefinition.IteratableResolvedValueSet;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.socraticgrid.workbench.model.ext.PagedResult;
import org.socraticgrid.workbench.setting.ApplicationSettings;

/**
 * KnowledgeModuleService implementation using a configurable CTS2 as its data source.
 * @author esteban
 */
public class CTS2KnowledgeModuleService implements KnowledgeModuleService {

    private final String cts2EndpointURL;
    private static final String valueSetUrlPart="valueset/workbench-{valueSetName}/resolution?matchalgorithm=contains&filtercomponent=resourceSynopsis&matchvalue={query}&page={start}&maxtoreturn={resultsNumber}";

    public CTS2KnowledgeModuleService() {
        String localCts2EndpointURL = ApplicationSettings.getInstance().getSetting("knowledge.module.service.impl.cts2.url");
        if (!localCts2EndpointURL.endsWith("/")){
            localCts2EndpointURL+="/";
        }
        
        //Check that the url is valid
        try {
            new URL(localCts2EndpointURL);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("knowledge.module.service.impl.cts2.url configuration property does not contain a valid URL! ("+this.cts2EndpointURL+")");
        }
        
        this.cts2EndpointURL = localCts2EndpointURL+valueSetUrlPart;
    }
    
    

    public PagedResult<ReferenceData> getReferenceData(ReferenceDataRequest request) {
        String url = this.cts2EndpointURL
                .replace("{valueSetName}", request.getLookupType() )
                .replace("{query}", request.getLookupCode() )
                .replace("{start}", String.valueOf(request.getStartingRecNumber()))
                .replace("{resultsNumber}", String.valueOf(request.getMaxRecordsToShow()));
        
        Cts2RestClient client = Cts2RestClient.instance();
        IteratableResolvedValueSet resolution = client.getCts2Resource(url, IteratableResolvedValueSet.class);
        
        EntitySynopsis[] entries = resolution.getEntry();
        
        List<ReferenceData> data = new ArrayList<ReferenceData>();
        int total = 0;
        if (entries != null){
            total = entries.length;
            
            for (EntitySynopsis entitySynopsis : entries) {
                String id = entitySynopsis.getUri();
                String name = entitySynopsis.getDesignation();
                String descr = entitySynopsis.getUri();
                data.add(new ReferenceData(id, name, descr));
            }
        }
        
        //TODO: fix the 'total' part
        return new PagedResult<ReferenceData>(total+1, request.getStartingRecNumber(), total, data);
        
    }
    
}
