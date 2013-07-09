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
package org.socraticgrid.guvnorassetsscanner.abdera;

import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.drools.builder.ResourceType;
import org.drools.mas.util.CompositeResourceDescriptorImpl;
import org.drools.mas.util.ResourceDescriptor;
import org.drools.mas.util.ResourceDescriptorImpl;
/**
 *
 * @author esteban
 */
public class ResourceDescriptorTranslator {
    
    public static final String NS = "";
    public static final QName METADATA = new QName(NS, "metadata");
    public static final QName VALUE = new QName(NS, "value");
    public static final QName ARCHIVED = new QName(NS, "archived");
    public static final QName UUID = new QName(NS, "uuid");
    public static final QName STATE = new QName(NS, "state");
    public static final QName FORMAT = new QName(NS, "format");
    public static final QName CATEGORIES = new QName(NS, "categories");
    public static final QName CREATED = new QName(NS, "created");
    public static final QName LAST_MODIFIED = new QName(NS, "lastModified");
    public static final QName TITLE = new QName(NS, "title");
    public static final QName VERSION = new QName(NS, "version");
    
    public static final String CHANGE_SET_FORMAT = "changeset";
    
    public static ResourceDescriptor toResourceDescriptor(Entry entry){
        ExtensibleElement metadataExtension = entry.getExtension(METADATA);
        
        //Format
        String format = ((ExtensibleElement) metadataExtension.getExtension(FORMAT)).getSimpleExtension(VALUE);

        ResourceDescriptorImpl descriptor = null;
        if (format.equals(CHANGE_SET_FORMAT)){
            descriptor = new CompositeResourceDescriptorImpl();
        }else{
            descriptor = new ResourceDescriptorImpl();
        }
        if(format.equals(CHANGE_SET_FORMAT)){
            descriptor.setType(ResourceType.CHANGE_SET);
        }
        if(format.equals("DRL")){
            descriptor.setType(ResourceType.DRL);
        }
        
        //UUID
        String uuid = ((ExtensibleElement) metadataExtension.getExtension(UUID)).getSimpleExtension(VALUE);
        descriptor.setId(uuid);
        
        //Author
        String author = entry.getAuthor().getName();
        descriptor.setAuthor(author);
        
        //Creation Time
        String creationTime = ((ExtensibleElement) metadataExtension.getExtension(CREATED)).getSimpleExtension(VALUE);
        descriptor.setCreationTime(AtomDate.parse(creationTime));
        
        
        //Description
        String description = entry.getSummary();
        descriptor.setDescription(description);
        
        //No documentation
        descriptor.setDocumentation(null);
        
        //Last Modification Time
        Date lastModificationTime = entry.getPublished();
        descriptor.setLastModificationTime(lastModificationTime);
        
        
        //Name
        String name = entry.getTitle();
        descriptor.setName(name);

        //Resource URL
        try {
            descriptor.setResourceURL(new URL(URLDecoder.decode(entry.getContentSrc().toASCIIString(), "UTF-8")));
        } catch (Exception ex) {
            throw new IllegalStateException("Invalid format for \"sourceLink\":"+entry.getContentSrc().toASCIIString());
        }
        
        //Status
        String status = ((ExtensibleElement) metadataExtension.getExtension(STATE)).getSimpleExtension(VALUE);
        descriptor.setStatus(status);
        
        //Version
        String version = ((ExtensibleElement) metadataExtension.getExtension(VERSION)).getSimpleExtension(VALUE);
        descriptor.setVersion(version);
        
        //Categories
        ExtensibleElement categoriesExtension = metadataExtension.getExtension(CATEGORIES);
        if (categoriesExtension != null){
            Set<String> categories = new HashSet<String>();
            List<Element> categoriesValues = categoriesExtension.getExtensions(VALUE);
            for (Element category : categoriesValues) {
                categories.add(category.getText());
            }
            descriptor.setCategories(categories);
        }
        
        return descriptor;
    }
}
