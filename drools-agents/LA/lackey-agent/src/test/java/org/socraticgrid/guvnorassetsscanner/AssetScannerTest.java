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

import org.socraticgrid.guvnorassetsscanner.authenticator.BasicAuthenticatorProvider;
import org.socraticgrid.guvnorassetsscanner.authenticator.GuvnorAuthenticatorProvider;
import org.socraticgrid.guvnorassetsscanner.io.URLResourceReaderStrategy;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import junit.framework.Assert;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.IOUtils;
import org.drools.mas.util.CompositeResourceDescriptor;
import org.drools.mas.util.ResourceDescriptor;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class AssetScannerTest {
    
    @Ignore("make sure you have a running instance of Guvnor!")
    public void testScannerAgainstRunningGuvnor() throws Exception{
    
        String url = "http://localhost:8080/drools-guvnor/rest/packages/urn.org.socraticgrid.adapter.fact/assets/";
        GuvnorAuthenticatorProvider authenticatorProvider = new BasicAuthenticatorProvider("admin", "admin");
        AssetScannerListener listener = new AssetScannerListener() {

            public void onAssetsScanned(List<ResourceDescriptor> assets) {
                System.out.println("\n\n\n");
                System.out.println("Resource:");
                for (ResourceDescriptor resourceDescriptor : assets) {
                    System.out.println("\t"+resourceDescriptor.getName()+" ["+resourceDescriptor.getId()+"]");
                }
                System.out.println("\n\n\n");
            }
        };
        
        AssetsScanner scanner = new AssetsScanner(url, authenticatorProvider, 10);
        
        scanner.addListener(listener);
        
        scanner.start();
        
        Thread.sleep(60000);
        
        scanner.stop();
    }
    
    @Ignore("make sure you have a running instance of Guvnor!")
    public void testInternalResourceScannerAgainstRunningGuvnor() throws Exception{
    
        String url = "http://localhost:8080/drools-guvnor/rest/packages/urn.org.socraticgrid.adapter.fact/assets/";
        GuvnorAuthenticatorProvider authenticatorProvider = new BasicAuthenticatorProvider("admin", "admin");
        
        final AssetsScanner scanner = new AssetsScanner(url, authenticatorProvider, 10);
        
        AssetScannerListener listener = new AssetScannerListener() {

            public void onAssetsScanned(List<ResourceDescriptor> assets) {
                System.out.println("\n\n\n");
                try{
                    for (ResourceDescriptor resourceDescriptor : assets) {
                        if (resourceDescriptor instanceof CompositeResourceDescriptor){
                            scanner.hydrateInternalResources((CompositeResourceDescriptor)resourceDescriptor);
                            System.out.println("Composite Resource ["+resourceDescriptor.getName()+"]:");
                            for (ResourceDescriptor internalResourceDescriptor : ((CompositeResourceDescriptor)resourceDescriptor).getInternalResources()) {
                                System.out.println("\t"+internalResourceDescriptor.getName()+" ["+internalResourceDescriptor.getId()+"]");
                            }
                            System.out.println("\n\n");
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("\n\n\n");
            }
        };
        
        scanner.addListener(listener);
        
        scanner.start();
        
        Thread.sleep(60000);
        
        scanner.stop();
    }
    
    @Test
    public void testFileResource() throws IOException{
        URL fullAssetsTemplateURL = this.getClass().getResource("/fullAssets.xml");
        URL changeSetTemplateURL = this.getClass().getResource("/changeSet.xml");
        URL process1EntryURL = this.getClass().getResource("/process1Entry.xml");
        URL process2EntryURL = this.getClass().getResource("/process2Entry.xml");
        
        //create change-set file
        StringTemplate changeSetTemplate = new StringTemplate(IOUtils.toString(changeSetTemplateURL));
        changeSetTemplate.setAttribute("process1URL", process1EntryURL.toString());
        changeSetTemplate.setAttribute("process2URL", process2EntryURL.toString());
        File changeSetTempFile = File.createTempFile("changeSet", "xml");
        IOUtils.copy(new ByteArrayInputStream(changeSetTemplate.toString().getBytes()), new FileOutputStream(changeSetTempFile));
        
        //create full assets file
        StringTemplate assetsTemplate = new StringTemplate(IOUtils.toString(fullAssetsTemplateURL));
        assetsTemplate.setAttribute("changeSetURL", changeSetTempFile.toURI().toURL().toString());
        File fullAssetsTempFile = File.createTempFile("fullAssets", "xml");
        IOUtils.copy(new ByteArrayInputStream(assetsTemplate.toString().getBytes()), new FileOutputStream(fullAssetsTempFile));
        
        //create the scanner task
        ResourceReaderStrategy readerStrategy = new URLResourceReaderStrategy(fullAssetsTempFile.toURI().toURL());
        ScannerTask scannerTask = new ScannerTask(readerStrategy);
        
        //get all the resources( 1 change-set and 2 processes)
        List<ResourceDescriptor> resourceDescriptors = scannerTask.getResourceDescriptors();

        Assert.assertEquals(3, resourceDescriptors.size());
        
        //get the change-set
        CompositeResourceDescriptor changeSet = null;
        for (ResourceDescriptor resourceDescriptor : resourceDescriptors) {
            if (resourceDescriptor instanceof CompositeResourceDescriptor){
                changeSet = (CompositeResourceDescriptor) resourceDescriptor;
            }
        }
        Assert.assertNotNull(changeSet);
        
        System.out.println(changeSet.getResourceURL());
        
        //Hydrate the change-set
        scannerTask.hydrateInternalResources(changeSet);
        
        Assert.assertEquals(2, changeSet.getInternalResources().size());
        
        for (ResourceDescriptor resourceDescriptor : changeSet.getInternalResources()) {
            System.out.println("\t"+resourceDescriptor.getName());
        }
    }
}
