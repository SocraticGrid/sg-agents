/*******************************************************************************
 *
 * Copyright (C) 2012 by Cognitive Medical Systems, Inc (http://www.cognitivemedciine.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy of 
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ******************************************************************************/
 
 /******************************************************************************
 * Socratic Grid contains components to which third party terms apply. To comply 
 * with these terms, the following notice is provided:
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 * Copyright (c) 2008, Nationwide Health Information Network (NHIN) Connect. All 
 * rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this 
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the name of the NHIN Connect Project nor the names of its 
 *   contributors may be used to endorse or promote products derived from this 
 *   software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * END OF TERMS AND CONDITIONS
 *
 ******************************************************************************/
package org.socraticgrid.guvnorassetsscanner;

import org.socraticgrid.guvnorassetsscanner.abdera.AbderaResourceReaderStrategy;
import org.socraticgrid.guvnorassetsscanner.authenticator.GuvnorAuthenticatorProvider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.drools.ChangeSet;
import org.drools.grid.api.CompositeResourceDescriptor;
import org.drools.grid.api.ResourceDescriptor;
import org.drools.grid.api.impl.CompositeResourceDescriptorImpl;
import org.drools.io.Resource;
import org.drools.io.impl.BaseResource;
import org.drools.xml.ChangeSetSemanticModule;
import org.drools.xml.SemanticModules;
import org.drools.xml.XmlChangeSetReader;

/**
 *
 * @author esteban
 */
public class ScannerTask implements Runnable {

    private final List<AssetScannerListener> listeners = Collections.synchronizedList(new ArrayList<AssetScannerListener>());
    private final ScheduledExecutorService scheduler;
    
    private SemanticModules changeSetSemanticModules;
    
    private ResourceReaderStrategy resourceReaderStrategy;

    protected ScannerTask(){
        this.scheduler = Executors.newScheduledThreadPool(1);
        
        this.changeSetSemanticModules = new SemanticModules();
        this.changeSetSemanticModules.addSemanticModule(new ChangeSetSemanticModule());
    }
    
    public ScannerTask(ResourceReaderStrategy resourceReaderStrategy) {
        this();
        this.resourceReaderStrategy = resourceReaderStrategy;
    }
    
    public ScannerTask(String guvnorURL, GuvnorAuthenticatorProvider authenticatorProvider) {
        this();
        
        //default reader streategy: Abdera
        this.resourceReaderStrategy = new AbderaResourceReaderStrategy(guvnorURL, authenticatorProvider);
    }
    
    public void run() {
        //get the resources
        try{
            final List<ResourceDescriptor> resources = Collections.unmodifiableList(this.getResourceDescriptors());
        
            //notify the listeners in a separate thread
            scheduler.schedule(new Runnable(){

                public void run() {
                    synchronized(listeners) {
                        for (AssetScannerListener assetScannerListener : listeners) {
                            assetScannerListener.onAssetsScanned(resources);
                        }
                    }
                }

            }, 0, TimeUnit.MILLISECONDS);
        } catch (RuntimeException ex){
            System.out.println("Error getting Resource Descriptors:");
            ex.printStackTrace();
            throw ex;
        }
    }
    
    public void hydrateInternalResources(CompositeResourceDescriptor resourceDescriptor){
        try {
            //get the change-set content
            String changeSetContent = this.getAssetContent(resourceDescriptor.getResourceURL().toString());
            
            //parse the content
            XmlChangeSetReader reader = new XmlChangeSetReader( this.changeSetSemanticModules,
                                                            null,
                                                            0 );
            ChangeSet changeSet = reader.read(new ByteArrayInputStream(changeSetContent.getBytes()));

            List<ResourceDescriptor> childrenResources = new ArrayList<ResourceDescriptor>();
            
            //TODO: only added resources are supported?
            Collection<Resource> resourcesAdded = changeSet.getResourcesAdded();
            for (Resource resource : resourcesAdded) {
                String resourceURL = ((BaseResource)resource).getURL().toString();
                if (resourceURL.endsWith("/source") || resourceURL.endsWith("/source/")){
                    resourceURL = resourceURL.substring(0, resourceURL.lastIndexOf("/source"));
                }
                childrenResources.add(this.getResourceDescriptor(resourceURL));
            }
            
            ((CompositeResourceDescriptorImpl)resourceDescriptor).setInternalResources(childrenResources);
            
        } catch (Exception ex) {
            throw new IllegalStateException("Error hydrating the internal resources of "+resourceDescriptor, ex);
        }
    }
    
    
    public List<ResourceDescriptor> getResourceDescriptors(){
        return this.resourceReaderStrategy.getResourceDescriptors();
    }
    
    private ResourceDescriptor getResourceDescriptor(String url){
        return this.resourceReaderStrategy.getResourceDescriptor(url);
    }
    
    private String getAssetContent(String sourceURL) throws IOException{
        return this.resourceReaderStrategy.getAssetContent(sourceURL);
    }

    public void setResourceReaderStrategy(ResourceReaderStrategy resourceReaderStrategy) {
        this.resourceReaderStrategy = resourceReaderStrategy;
    }

    public void addListener(AssetScannerListener listener){
        this.listeners.add(listener);
    }
    
    public void removeListener(AssetScannerListener listener){
        this.listeners.remove(listener);
    }
    
    public void addListeners(List<AssetScannerListener> listeners){
        this.listeners.addAll(listeners);
    }
    
}
