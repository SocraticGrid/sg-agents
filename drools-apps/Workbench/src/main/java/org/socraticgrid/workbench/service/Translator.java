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
import org.socraticgrid.workbench.model.guvnor.AssetMetadata;
import org.socraticgrid.workbench.model.guvnor.Category;
import org.socraticgrid.workbench.model.guvnor.PackageMetadata;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.logging.Level;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Translation from entities returned from Guvnor (Feed, JSON, etc.) to real
 * entities (Package, Asset, etc)
 *
 * @author esteban
 */
public class Translator {
    
    public static final String NS = "";
    public static final QName METADATA = new QName(NS, "metadata");
    public static final QName VALUE = new QName(NS, "value");
    public static final QName ARCHIVED = new QName(NS, "archived");
    public static final QName UUID = new QName(NS, "uuid");
    public static final QName STATE = new QName(NS, "state");
    public static final QName FORMAT = new QName(NS, "format");
    public static final QName CATEGORIES = new QName(NS, "categories");
    
    private CategoryParser allCategoriesParser = new CategoryParser() {

        public List<Category> parseCategories(Map<String, Category> categories) {
            
            List<Category> allCategories = new ArrayList<Category>();
            for (Map.Entry<String, Category> entry : categories.entrySet()) {
                Category category = entry.getValue();
                allCategories.add(category);
            }
            
            return allCategories;
        }
    };
    
    private CategoryParser topCategoyParser = new CategoryParser() {

        public List<Category> parseCategories(Map<String, Category> categories) {
            
            List<Category> topCategories = new ArrayList<Category>();
            for (Map.Entry<String, Category> entry : categories.entrySet()) {
                String path = entry.getKey();
                Category category = entry.getValue();
                
                if (!path.contains("/")){
                    //is a first-level category -> add it to the result list
                    topCategories.add(category);
                }
            }
            
            return topCategories;
        }
    };
    
    private CategoryParser categoyTreeParser = new CategoryParser() {

        public List<Category> parseCategories(Map<String, Category> categories) {
            //create the tree structure of categories.
            List<Category> categoryTree = new ArrayList<Category>();
            for (Map.Entry<String, Category> entry : categories.entrySet()) {
                String path = entry.getKey();
                Category category = entry.getValue();
                
                if (path.contains("/")){
                    //this is not a first-level category. We need to get the parent 
                    //category and add this category as a child.
                    String parentPath = path.substring(0,path.lastIndexOf("/"));
                    Category parent = categories.get(parentPath);
                    
                    if (parent == null){
                        throw new IllegalStateException("No parent category for "+category);
                    }
                    
                    parent.getChildren().add(category);
                    
                }else{
                    //is a first-level category -> add it to the result list
                    categoryTree.add(category);
                }
            }
            
            
            return categoryTree;
        }
    };
    
    public org.socraticgrid.workbench.model.guvnor.Package toPackage(Entry entry) {
        org.socraticgrid.workbench.model.guvnor.Package pkg = new org.socraticgrid.workbench.model.guvnor.Package();
        pkg.setTitle(entry.getTitle());

        PackageMetadata metadata = new PackageMetadata();

        //Use title instad of uuid
        metadata.setUuid(entry.getTitle());

        //TODO return complete packages!

        pkg.setMetadata(metadata);

        return pkg;
    }

    public Asset toAsset(Entry entry) {
        ExtensibleElement metadataExtension = entry.getExtension(METADATA);

        AssetMetadata metadata = new AssetMetadata();
        metadata.setTitle(entry.getTitle());

        //UUID
        String uuid = ((ExtensibleElement) metadataExtension.getExtension(UUID)).getSimpleExtension(VALUE);
        metadata.setUuid(uuid);

        //Format
        String format = ((ExtensibleElement) metadataExtension.getExtension(FORMAT)).getSimpleExtension(VALUE);
        metadata.setFormat(format);
        
        //Package. i.e: http://localhost:8080/drools-guvnor/rest/packages/package1/assets/P2
        String pkg = entry.getContentSrc().toASCIIString();
        pkg = pkg.substring(pkg.indexOf("/rest/packages/")+"/rest/packages/".length(), pkg.indexOf("/assets/"));
        
        Asset asset = new Asset();
        asset.setPkg(pkg);
        asset.setMetadata(metadata);

        //TODO return a complete asset

        return asset;

    }
    
    public void updateEntryCategories(Entry entry, List<String> categories){
        ExtensibleElement metadataExtension  = entry.getExtension(Translator.METADATA); 
        
        //overwite CATEGORIES extension
        ExtensibleElement categoryExtension = metadataExtension.getExtension(Translator.CATEGORIES);
        categoryExtension.discard();
        
        //set the new categories
        metadataExtension.addExtension(Translator.CATEGORIES);
        categoryExtension = metadataExtension.getExtension(Translator.CATEGORIES);
        
        for (String category : categories) {
            categoryExtension.addSimpleExtension(Translator.VALUE, category);
        }
    }
    
    public Entry createProcessEntry(String title, String summary, List<String> categories) {

        Abdera abdera = Abdera.getInstance();

        Entry processEntry = abdera.newEntry();

        processEntry.setTitle(title);
        processEntry.setSummary(summary);

        //create metadata element
        ExtensibleElement metadataExtension = processEntry.addExtension(METADATA);

        //add format element to metadata
        ExtensibleElement formatExtension = metadataExtension.addExtension(FORMAT);
        formatExtension.addSimpleExtension(VALUE, "bpmn2");

        //add categories element to metadata
        ExtensibleElement categoriesExtension = metadataExtension.addExtension(CATEGORIES);
        
        for (String category : categories) {
            categoriesExtension.addSimpleExtension(VALUE, category);
        }

        return processEntry;


    }
    
    public Entry createChangeSetEntry(String title) {

        Abdera abdera = Abdera.getInstance();

        Entry changeSetEntry = abdera.newEntry();

        changeSetEntry.setTitle(title);
        changeSetEntry.setSummary("");

        //create metadata element
        ExtensibleElement metadataExtension = changeSetEntry.addExtension(METADATA);

        //add format element to metadata
        ExtensibleElement formatExtension = metadataExtension.addExtension(FORMAT);
        formatExtension.addSimpleExtension(VALUE, "changeset");

        return changeSetEntry;

    }

    public List<Category> fromXMLToTopCategories(String xml) {
        return this.fromXMLCategories(xml, topCategoyParser);
    }
    
    public List<Category> fromXMLToCategoriesTree(String xml) {
        return this.fromXMLCategories(xml, categoyTreeParser);
    }
    
    public  List<Category> fromXMLCategories(String xml) {
        return this.fromXMLCategories(xml, allCategoriesParser);
    }
        
    private  List<Category> fromXMLCategories(String xml, CategoryParser parserStrategy) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            
            
            final List<String> errors = new ArrayList<String>();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            builder.setErrorHandler(new ErrorHandler() {

                public void warning(SAXParseException exception) throws SAXException {
                    java.util.logging.Logger.getLogger(Translator.class.getName()).log(Level.WARNING, "Warning parsing categories from Guvnor", exception);
                }

                public void error(SAXParseException exception) throws SAXException {
                    java.util.logging.Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, "Error parsing categories from Guvnor", exception);
                    errors.add(exception.getMessage());
                }

                public void fatalError(SAXParseException exception) throws SAXException {
                    java.util.logging.Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, "Error parsing categories from Guvnor", exception);
                    errors.add(exception.getMessage());
                }
            });
            
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
            
            if (!errors.isEmpty()){
                throw new IllegalStateException("Error parsing categories from guvnor. Check the log for errors' details.");
            }

            Map<String, Category> categories = new LinkedHashMap<String, Category>();
            
            //convert all catergories and add them to the map
            NodeList categoriesList = doc.getElementsByTagName("category");
            for (int i = 0; i < categoriesList.getLength(); i++) {
                Element element = (Element) categoriesList.item(i);
                
                NodeList paths = element.getElementsByTagName("path");
                if (paths.getLength() != 1){
                    throw new IllegalStateException("Malfromed category. Expected 1 <path> tag, but found "+paths.getLength());
                }
                
                String path = paths.item(0).getTextContent();
                
                String name = path;
                if (path.contains("/")){
                    name = path.substring(path.lastIndexOf("/")+1);
                }
                
                Category category = new Category();
                category.setName(name);
                category.setPath(path);
                category.setChildren(new ArrayList<Category>());
                
                categories.put(path, category);
            }
            
            return parserStrategy.parseCategories(categories);
            
        } catch (SAXException ex) {
            throw new RuntimeException("Error parsing categories' xml",ex);
        } catch (IOException ex) {
            throw new RuntimeException("Error parsing categories' xml",ex);
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException("Error parsing categories' xml",ex);
        }
    }
}

interface CategoryParser{
    public List<Category> parseCategories(Map<String, Category> categories);
}