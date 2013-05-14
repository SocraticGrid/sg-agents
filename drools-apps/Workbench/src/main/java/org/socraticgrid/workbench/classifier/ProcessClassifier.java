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
package org.socraticgrid.workbench.classifier;

import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.socraticgrid.workbench.model.persistence.CohortTypeEntity;
import org.socraticgrid.workbench.model.persistence.FactTypeEntity;
import org.socraticgrid.workbench.model.persistence.WorkItemEntity;
import org.socraticgrid.workbench.ontomodel.service.OntoModelService;
import org.socraticgrid.workbench.ontomodel.service.OntoModelServiceException;
import org.socraticgrid.workbench.ontomodel.service.SyntacticalOntoModelServiceImpl;
import org.socraticgrid.workbench.setting.UserSettings;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author esteban
 */
public class ProcessClassifier {

    private UserSettings userSettings;

    public ProcessClassifier(UserSettings userSettings) {
        this.userSettings = userSettings;
    }
    
    public ProcessClassifierResult classifyProcess(String pkg, String processId, String processName, String xmlDefinition) throws OntoModelServiceException {
        try {
            
            //Categorize the xml and create the result
            ProcessClassifierResult result = this.parseAndClassifyProcess(xmlDefinition);
            
            //Use OntoModelService to get all the Guvnor categories that matches
            //the process definition
            OntoModelService ontoModelService = new SyntacticalOntoModelServiceImpl(userSettings);
            List<String> guvnorCategories = ontoModelService.categorizeProcess(xmlDefinition);
            
            //add guvnor categories to result
            result.setGuvnorCategories(guvnorCategories);
            
            //add packageName and processName to result
            result.setPackageName(pkg);
            result.setProcessId(processId);
            result.setProcessName(processName);
            
            
            return result;
        } catch (Exception ex) {
            throw new IllegalStateException("Error categorizing the process", ex);
        }
    }

    protected ProcessClassifierResult parseAndClassifyProcess(String xmlDefinition) throws Exception {
        
        ProcessClassifierResult result = new ProcessClassifierResult();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xmlDefinition.getBytes()));

        JsonParser jsonParser = new JsonParser();

        boolean parsingTextAnnotation = false;
        boolean parsingStartEvent = false;

        String currentTextAnnotationId = null;
        
        String startEventType = "Simple";

        String startEventNodeId = null;
        
        //Map of <textAnnotation's id, Fact Type name>
        Map<String, String> factTypeNodesIds = new HashMap<String, String>();
        
        //A List of <association's targetRef and association's sourceRef>
        List<Map<String, String>> associations = new ArrayList<Map<String, String>>();
        
        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    if ("bpmn2".equals(reader.getName().getPrefix()) && "textAnnotation".equals(reader.getName().getLocalPart())) {
                        parsingTextAnnotation = true;
                        currentTextAnnotationId = reader.getAttributeValue("", "id");
                    } else if ("bpmn2".equals(reader.getName().getPrefix()) && "startEvent".equals(reader.getName().getLocalPart())){
                        parsingStartEvent = true;
                        startEventNodeId = reader.getAttributeValue("", "id");
                    } else if ("bpmn2".equals(reader.getName().getPrefix()) && "association".equals(reader.getName().getLocalPart())){
                        String sourceRef = reader.getAttributeValue("", "sourceRef");
                        String targetRef = reader.getAttributeValue("", "targetRef");
                        
                        Map<String,String> references = new HashMap<String, String>();
                        references.put(targetRef, sourceRef);
                        
                        associations.add(references);
                    } else if ("bpmn2".equals(reader.getName().getPrefix()) && "task".equals(reader.getName().getLocalPart())){
                        //<bpmn2:task id="_FDBDEFA7-D789-4440-BC5E-4295A39153B3" drools:bgcolor="#b1c2d6" drools:taskName="Appointment" name="Appointment">
                        String taskType = reader.getAttributeValue("http://www.jboss.org/drools", "taskName");
                        String name = reader.getAttributeValue("", "name");
                        if (taskType != null && !taskType.trim().isEmpty()){
                            WorkItemEntity workItemEntity = new WorkItemEntity();
                            workItemEntity.setType(taskType);
                            workItemEntity.setName(name);
                            result.addWorkItemEntity(workItemEntity);
                        }
                    } else if (parsingStartEvent && "bpmn2".equals(reader.getName().getPrefix()) && reader.getName().getLocalPart().endsWith("EventDefinition")){
                        startEventType = reader.getName().getLocalPart().substring(0, reader.getName().getLocalPart().indexOf("EventDefinition"));
                    }else if(parsingTextAnnotation && "bpmn2".equals(reader.getName().getPrefix()) && "text".equals(reader.getName().getLocalPart())) {
                        String text = reader.getElementText();
                        if (text.startsWith("KMRCustom--")) {
                            //<bpmn2:text>KMRCustom--Diagnosis--code--49320</bpmn2:text>
                            String[] parts = text.split("--");
                            
                            FactTypeEntity factTypeEntity = new FactTypeEntity();
                            factTypeEntity.setName(parts[1]);
                            
                            //store the id of the annotation for further analysis
                            factTypeNodesIds.put(currentTextAnnotationId, factTypeEntity.getName());
                            
                            result.addFactTypeEntity(factTypeEntity);
                        } else if (text.startsWith("KMRCustomCohort--")) {
                            //<bpmn2:text>KMRCustomCohort--{"cohortproperty_fieldb":"","cohortproperty_fielda":"123","resourceId":"_BEF705C5-FA86-404F-A253-24140C56BED1","fieldb_name":"fieldB","name":"","fielda_name":"fieldA","documentation":"","cohortentity":"ClassA"}</bpmn2:text>
                            String[] parts = text.split("--");
                            JsonPrimitive cohortEntity = jsonParser.parse(parts[1]).getAsJsonObject().getAsJsonPrimitive("cohortentity");
                            if (cohortEntity != null){
                                CohortTypeEntity cohortTypeEntity = new CohortTypeEntity();
                                cohortTypeEntity.setName(cohortEntity.getAsString());
                                result.addCohortTypeEntity(cohortTypeEntity);
                            }
                        }
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if ("bpmn2".equals(reader.getName().getPrefix()) && "textAnnotation".equals(reader.getName().getLocalPart())) {
                        parsingTextAnnotation = false;
                        currentTextAnnotationId = null;
                    } else if ("bpmn2".equals(reader.getName().getPrefix()) && "startEvent".equals(reader.getName().getLocalPart())) {
                        parsingStartEvent = false;
                    }
            }
        }
        
        //process associations
        for (Map<String, String> associationRefs : associations) {
            String sourceRef = associationRefs.get(startEventNodeId);
            //has this association StartEvent as target?
            if ( sourceRef != null){
                //get the Fact Type
                String factTypeName = factTypeNodesIds.get(sourceRef);
                //mark sourceRef as starting Fact Type
                result.getFactsMap().get(factTypeName).setStarting(true);
            }
        }
        
        result.setStartEventType(startEventType);

        return result;
    }
}
