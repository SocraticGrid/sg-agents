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

import org.socraticgrid.workbench.model.guvnor.Asset;
import org.socraticgrid.workbench.model.guvnor.ChangeSetResource;
import org.socraticgrid.workbench.model.guvnor.Package;
import org.socraticgrid.workbench.servlet.GuvnorUtils;
import org.socraticgrid.workbench.setting.UserSettings;
import org.socraticgrid.workbench.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.commons.io.IOUtils;

/**
 * Facade for Guvnor REST API
 *
 * @author esteban
 */
public class GuvnorService {

    private final String guvnorURL;

    private String changeSetTemplateFile;
    
    private final Translator translator;

    private UserSettings userSettings;
    /**
     * Default constructor that uses GuvnorUtils to get Guvnor's URL
     * @param guvnorURL 
     */
    public GuvnorService(UserSettings userSettings) {
        this(GuvnorUtils.getGuvnorBaseURL(), userSettings);
    }
    
    public GuvnorService(String guvnorURL, UserSettings userSettings) {
        try {
            this.guvnorURL = guvnorURL;
            this.translator = new Translator();
            this.userSettings = userSettings;
            
            this.changeSetTemplateFile = IOUtils.toString(this.getClass().getResourceAsStream("/templates/changeSet.tpl"));
        } catch (IOException ex) {
            throw new IllegalStateException("Error reading change-set template",ex);
        }
    }

    public static enum ASSET_TYPE {

        BPMN2("bpmn2"),
        WORKING_SET("workingset");
        private String format;

        private ASSET_TYPE(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }
    }

    public List<org.socraticgrid.workbench.model.guvnor.Package> getPackages() {
        
        RequestOptions options = this.createDefaultOptions();

        ClientResponse resp = this.invokeGETGuvnor("/rest/packages", options);
        
        List<org.socraticgrid.workbench.model.guvnor.Package> packages = new ArrayList<org.socraticgrid.workbench.model.guvnor.Package>();

        Document<Feed> document = resp.getDocument();
        for (Entry entry : document.getRoot().getEntries()) {
            packages.add(translator.toPackage(entry));
        }

        return packages;
    }

    public List<org.socraticgrid.workbench.model.guvnor.Category> getCategoriesTree(){
        String url = "/rest/categories";
        RequestOptions options = this.createDefaultOptions( "application/xml");
        ClientResponse resp = this.invokeGETGuvnor(url, options);

        Document<Feed> document = resp.getDocument();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            document.writeTo(outputStream);
        } catch (IOException ex) {
            //this should never happen
            Logger.getLogger(GuvnorService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return translator.fromXMLToCategoriesTree(outputStream.toString());
    }
    
    public List<org.socraticgrid.workbench.model.guvnor.Category> getCategoryChildren(String categoryPath) {

        String url;
        boolean getChildren = false;
        if (categoryPath == null || categoryPath.trim().equals("") || categoryPath.trim().equals("/")){
            //get top-level categories
            url = "/rest/categories";
            getChildren = false;
        }else{
            if(!categoryPath.startsWith("/")){
                categoryPath = "/"+categoryPath;
            }
            //get category children
            url = "/rest/categories"+categoryPath+"/children";
            getChildren = true;
        }
        
        RequestOptions options = this.createDefaultOptions( "application/xml");
        ClientResponse resp = this.invokeGETGuvnor(url, options);

        Document<Feed> document = resp.getDocument();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            document.writeTo(outputStream);
        } catch (IOException ex) {
            //this should never happen
            Logger.getLogger(GuvnorService.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (getChildren){
            return translator.fromXMLCategories(outputStream.toString());
        }else{
            return translator.fromXMLToTopCategories(outputStream.toString());
        }
        

    }
    
    public List<org.socraticgrid.workbench.model.guvnor.Asset> getCategoryAssets(String categoryPath, String fromPackage) {
        
        //assets can't belong to root category
        if (categoryPath == null || categoryPath.trim().equals("") || categoryPath.trim().equals("/")){
            return new ArrayList<Asset>();
        }
        
        //make sure categoryPath starts with '/'
        if(!categoryPath.startsWith("/")){
            categoryPath = "/"+categoryPath;
        }
        
        //get category's assets
        RequestOptions options = this.createDefaultOptions();
        ClientResponse resp = this.invokeGETGuvnor("/rest/categories"+categoryPath+"/assets", options);

        Document<Feed> document = resp.getDocument();
        
        List<Asset> assets = new ArrayList<Asset>();

        for (Entry entry : document.getRoot().getEntries()){ 
            //TODO: add this filter in Guvnor's side
            Asset asset = translator.toAsset(entry);
            if (asset.getMetadata().getFormat().equals(ASSET_TYPE.BPMN2.getFormat())){
                if (fromPackage == null || (fromPackage.equals(asset.getPkg()))){
                    assets.add(asset);
                }
            }
        }

        return assets;
    }

    public List<Asset> getProcesses(String packageName) {
        return getAssets(packageName, ASSET_TYPE.BPMN2);
    }

    public List<Asset> getWorkingSets(String packageName) {
        return getAssets(packageName, ASSET_TYPE.WORKING_SET);
    }

    public List<Asset> getAssets(String packageName, ASSET_TYPE assetType) {
        
        RequestOptions options = this.createDefaultOptions();
        ClientResponse resp = this.invokeGETGuvnor("/rest/packages/" + packageName + "/assets?format=" + assetType.getFormat(), options);

        List<Asset> assets = new ArrayList<Asset>();

        Document<Feed> document = resp.getDocument();
        for (Entry entry : document.getRoot().getEntries()) {
            assets.add(translator.toAsset(entry));
        }

        return assets;
    }

    /**
     * Creates a process in Guvnor and a corresponding Change-Set.
     *
     * @param packageName the package name where the process is going to be
     * created
     * @param title the title (name) of the process
     * @param summary the process summary
     * @param categories the categories (names) of the process
     */
    public Asset createNewProcess(String packageName, String title, String summary, List<String> categories) {
        Asset newProcess = this.createProcess(packageName, title, summary, categories);
        
        ChangeSetResource resource = new ChangeSetResource(guvnorURL+"/rest/packages/" + GuvnorService.encodeURIComponent(packageName) + "/assets/"+GuvnorService.encodeURIComponent(title), "BPMN2");
        
        this.createChangeSet(packageName, title+"-change-set", resource);
        
        return newProcess;
    }
    
    public Asset createProcess(String packageName, String title, String summary, List<String> categories) {

        try {
            Abdera abdera = Abdera.getInstance();
            AbderaClient client = new AbderaClient(abdera);

            //convert the arguments into a process Entry
            Entry processEntry = translator.createProcessEntry(title, summary, categories);

            //invoke Guvnor REST API to store the process
            RequestOptions options = this.createDefaultOptions("application/atom+xml", "application/atom+xml");

            ClientResponse resp = client.post(guvnorURL + "/rest/packages/" + packageName + "/assets", processEntry, options);

            if (resp.getType() != ResponseType.SUCCESS) {
                throw new RuntimeException(resp.getStatusText());
            }

            Document<Entry> document = resp.getDocument();
            return translator.toAsset(document.getRoot());

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }
    
    public void createChangeSet(String packageName, String title, ChangeSetResource... resources) {

        try {
            Abdera abdera = Abdera.getInstance();
            AbderaClient client = new AbderaClient(abdera);

            //convert the arguments into a ChangeSet Entry
            Entry changeSetEntry = translator.createChangeSetEntry(title);

            //invoke Guvnor REST API to create an empty change-set
            RequestOptions options = this.createDefaultOptions("application/atom+xml", "application/atom+xml");

            ClientResponse resp = client.post(guvnorURL + "/rest/packages/" + packageName + "/assets", changeSetEntry, options);

            if (resp.getType() != ResponseType.SUCCESS) {
                throw new RuntimeException(resp.getStatusText());
            }
            
            //create the content of the change-set
            StringTemplate changeSetTemplate = new StringTemplate(this.changeSetTemplateFile);
            changeSetTemplate.setAttribute("sources", resources);
            String content = changeSetTemplate.toString();

            //invoke Guvnor REST API to update the content of the change-set
            options = this.createDefaultOptions("application/xml", "application/xml");
            client.put(guvnorURL + "/rest/packages/" + packageName + "/assets/"+title+"/source", new ByteArrayInputStream(content.getBytes()), options);
            
            if (resp.getType() != ResponseType.SUCCESS) {
                throw new RuntimeException(resp.getStatusText());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    public Package getPackage(String packageName) {
        
        RequestOptions options = this.createDefaultOptions();
        ClientResponse resp = this.invokeGETGuvnor("/rest/packages/" + packageName, options);

        Document<Entry> document = resp.getDocument();
        return translator.toPackage(document.getRoot());
    }

    public Asset getAsset(String packageName, String assetName) {
        return translator.toAsset(this.getAssetEntry(packageName, assetName));
    }
    
    public Entry getAssetEntry(String packageName, String assetName) {
        RequestOptions options = this.createDefaultOptions();
        ClientResponse resp = this.invokeGETGuvnor("/rest/packages/" + packageName + "/assets/" + assetName, options);

        Document<Entry> document = resp.getDocument();
        return document.getRoot();
    }
    
    public String getAssetSource(String packageName, String assetName) {
        RequestOptions options = this.createDefaultOptions("text/plain");
        ClientResponse resp = this.invokeGETGuvnor("/rest/packages/" + packageName + "/assets/" + assetName+"/source", options);

        return resp.getDocument().getRoot().toString();
    }
    
    public void updateAssetCategories(String packageName, String processName, List<String> categories) {
        //get the asset
        Entry assetEntry = this.getAssetEntry(packageName, processName);
        
        //update its categories
        translator.updateEntryCategories(assetEntry, categories);
       
        //serialize the Entry
        ByteArrayOutputStream serializedEntry = new ByteArrayOutputStream();
        try {
            assetEntry.writeTo(serializedEntry);
        } catch (IOException ex) {
            throw new IllegalStateException("Error serializing the Asset Entry",ex);
        }
        
        //invoke guvnor to save the changes
        RequestOptions options = this.createDefaultOptions();
        options.setUsePostOverride(true);
        this.invokePUTGuvnor("/rest/packages/"+packageName+"/assets/"+processName, options, new ByteArrayInputStream(serializedEntry.toByteArray()));
    }

    private RequestOptions createDefaultOptions() {
        return this.createDefaultOptions("application/atom+xml");
    }

    private RequestOptions createDefaultOptions(String acceptType) {
        return this.createDefaultOptions(acceptType, null);
    }

    private RequestOptions createDefaultOptions(String acceptType, String contentType) {
        Abdera abdera = Abdera.getInstance();
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions options = client.getDefaultRequestOptions();
        options.setAuthorization("Basic " + new Base64().encodeToString(("admin:admin".getBytes())));
        
        if (this.userSettings != null && this.userSettings.getSetting(UserSettings.USER_SETTINGS.SECURITY_TOKEN_NAME) != null){
            String securityAttributeName = this.userSettings.getSetting(UserSettings.USER_SETTINGS.SECURITY_TOKEN_NAME);
            String securityAttributeValue = this.userSettings.getSetting(UserSettings.USER_SETTINGS.SECURITY_TOKEN);
            options.setHeader(securityAttributeName, securityAttributeValue);
            options.setHeader("SAMLResponseEncoded", "true");
        }
        

        options.setAccept(acceptType);

        if (contentType != null) {
            options.setContentType(contentType);
        }

        return options;
    }
    
    private ClientResponse invokeGETGuvnor(String path, RequestOptions options){
        Abdera abdera = Abdera.getInstance();
        AbderaClient client = new AbderaClient(abdera);

        path = GuvnorService.encodeURIComponent(path);
        
        ClientResponse resp = client.get(guvnorURL + path, options);

        if (resp.getType() == ResponseType.SUCCESS) {
            return resp;
        } else {
            throw new RuntimeException(resp.getStatusText());
        }
    }
    
    private ClientResponse invokePUTGuvnor(String path, RequestOptions options, InputStream inputStream){
        Abdera abdera = Abdera.getInstance();
        AbderaClient client = new AbderaClient(abdera);

        path = GuvnorService.encodeURIComponent(path);
        
        ClientResponse resp = client.put(guvnorURL + path, inputStream, options);

        if (resp.getType() == ResponseType.SUCCESS) {
            return resp;
        } else {
            throw new RuntimeException(resp.getStatusText());
        }
    }
    
    public static String encodeURIComponent(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8").
                    replaceAll("\\+", "%20").
                    replaceAll("\\%21", "!").
                    replaceAll("\\%27", "'").
                    replaceAll("\\%28", "(").
                    replaceAll("\\%29", ")").
                    replaceAll("\\%7E", "~").
                    replaceAll("\\%3F", "?").
                    replaceAll("\\%3D", "=").
                    replaceAll("\\%2F", "/");
        } // This exception should never occur.
        catch (UnsupportedEncodingException e) {
        }
        return s;
    } 
}
