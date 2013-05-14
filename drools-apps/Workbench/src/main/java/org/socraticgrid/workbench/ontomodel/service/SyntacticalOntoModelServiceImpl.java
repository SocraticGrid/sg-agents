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
package org.socraticgrid.workbench.ontomodel.service;

import org.socraticgrid.workbench.model.guvnor.Category;
import org.socraticgrid.workbench.service.GuvnorService;
import org.socraticgrid.workbench.setting.UserSettings;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author esteban
 */
public class SyntacticalOntoModelServiceImpl implements OntoModelService {

    private List<Category> categories;
    private boolean useCache;
    private UserSettings userSettings;

    public SyntacticalOntoModelServiceImpl(UserSettings userSettings) {
        this.userSettings = userSettings;
    }
    
    /**
     * Returns all the applicable categories for a process definition
     *
     * @param processDefinition
     * @return
     */
    public List<String> categorizeProcess(String processDefinition) throws OntoModelServiceException{
        
        try{
        //get the fact types used in the process
        Set<String> processFactTypes = this.getProcessFactTypesFromXml(processDefinition);
        
        //get all the categories in Guvnor that matches against each process Fact Type
        List<String> matchingCategories = this.getMatchingCategories(processFactTypes);
        
        //cleanup the category list
        this.removeSuperCategories(matchingCategories);
        
        return matchingCategories;
        } catch (Exception e){
            throw new OntoModelServiceException("Error while categorizing the process",e);
        }
    }

    /**
     * Converts a plain search text into a list of categories.
     *
     * @param search
     * @return
     */
    public List<String> convertSearchTextToCategories(String search) {
        return new ArrayList<String>();
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }
    

    private List<Category> getCategories() {
        if (this.useCache && this.categories != null) {
            return this.categories;
        }
        this.categories = this.getCategoriesFromGuvnor();

        return this.categories;

    }

    /**
     * Retrieves the category tree from Guvnor.
     * Do not use this method directly. Use {@link #getCategories()} instead.
     * @return 
     */
    private List<Category> getCategoriesFromGuvnor() {
        GuvnorService service = new GuvnorService(this.userSettings);
        return service.getCategoriesTree();
    }

    /**
     * Extracts all the Fact Types used in a process definition.
     * Each Fact Type is declared as:
     * 
     *   <bpmn2:textAnnotation id="_92807518-95EC-447B-A292-D46C9D5958E9">
     *       <bpmn2:text>KMRCustom--Diagnosis--code--49320</bpmn2:text>
     *   </bpmn2:textAnnotation>
     * 
     * In the previous example, Diagnosis is the Fact Type
     * 
     * @param processXml
     * @return
     * @throws XMLStreamException 
     */
    private Set<String> getProcessFactTypesFromXml(String processXml) throws XMLStreamException {

        Set<String> facts = new HashSet<String>();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(processXml.getBytes()));

        boolean parsingTextAnnotation = false;
        
        //<bpmn2:text>KMRCustom--Diagnosis--code--49320</bpmn2:text>
        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    if ("bpmn2".equals(reader.getName().getPrefix()) && "textAnnotation".equals(reader.getName().getLocalPart())) {
                        parsingTextAnnotation = true;
                    }
                    if (parsingTextAnnotation && "bpmn2".equals(reader.getName().getPrefix()) && "text".equals(reader.getName().getLocalPart())) {
                        String text = reader.getElementText();
                        if (text.startsWith("KMRCustom--")) {
                            String[] parts = text.split("--");
                            facts.add(parts[1]);
                        }
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if ("bpmn2".equals(reader.getName().getPrefix()) && "textAnnotation".equals(reader.getName().getLocalPart())) {
                        parsingTextAnnotation = false;
                    }
            }
        }

        return facts;
    }
    
    /**
     * Returns a plain list (not a tree) of all the Guvnor's categories that matches with
     * the name of each Fact Type.
     * The returned list contains the full path of each category.
     * @param factTypes
     * @return 
     */
    private List<String> getMatchingCategories(Set<String> factTypes){
        List<Category> matchedCategories = new ArrayList<Category>();
        List<String> matchedCategoriesPaths = new ArrayList<String>();
        List<Category> guvnorCategories = this.getCategories();
        
        for (String factType : factTypes) {
            matchedCategories.addAll(this.searchCategoryInTree(factType, guvnorCategories));
        }
        
        //convert from List<Category> to List<String>
        for (Category category : matchedCategories) {
            matchedCategoriesPaths.add(category.getPath());
        }
        
        return matchedCategoriesPaths;
    }
    
    /**
     * Recursively searches in categoryLista (tree) for all the categories
     * named categoryName
     * @param categoryName
     * @param categoryList
     * @return 
     */
    private List<Category> searchCategoryInTree(String categoryName, List<Category> categoryList){
        List<Category> result = new ArrayList<Category>();
        for (Category categoryInTree : categoryList) {
            if (categoryInTree.getName().equals(categoryName)){
                result.add(categoryInTree);
            }
            if (categoryInTree.getChildren() != null && !categoryInTree.getChildren().isEmpty()){
                result.addAll(this.searchCategoryInTree(categoryName, categoryInTree.getChildren()));
            }
        }
        
        return result;
    }
    
    /**
     * Removes super-categories from the list. 
     * If the list contains:
     *  - /A
     *  - /A/B
     *  - /A/B/C
     *  - /D
     *  - /E
     *  - /E/F
     * 
     * After the invocation of this method, the list will contain:
     *  - /A/B/C
     *  - /D
     *  - /E/F
     * 
     * @param categories 
     */
    private void removeSuperCategories(List<String> categories){
        //order the list according to the depth of each element
        Collections.sort(categories, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return new Integer(StringUtils.countMatches(o1, "/")).compareTo(StringUtils.countMatches(o2, "/"));
            }
        });
        
        String[] categoriesArray = categories.toArray(new String[categories.size()]);
        
        for (int i = 0; i < categoriesArray.length; i++) {
            boolean subcategoryFound = false;
            for (int j = i+1; j < categoriesArray.length; j++) {
                if (categoriesArray[j].startsWith(categoriesArray[i])){
                    //then categoriesArray[j] is a subcategory of categoriesArray[i]
                    //we don't need categoriesArray[i] in the result
                    subcategoryFound = true;
                    break;
                }
            }
            if (subcategoryFound){
                categories.remove(categoriesArray[i]);
            }
        }
        
    }
}
