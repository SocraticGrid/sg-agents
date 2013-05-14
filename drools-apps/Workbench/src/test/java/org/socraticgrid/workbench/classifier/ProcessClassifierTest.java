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

import org.socraticgrid.workbench.model.persistence.CohortTypeEntity;
import org.socraticgrid.workbench.model.persistence.FactTypeEntity;
import org.socraticgrid.workbench.model.persistence.WorkItemEntity;
import org.socraticgrid.workbench.servlet.GuvnorUtils;
import org.socraticgrid.workbench.util.MockServletRequestForGuvnorUtils;
import java.io.InputStream;
import java.util.Map;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class ProcessClassifierTest {
    
    public ProcessClassifierTest() {
    }

    @BeforeClass
    public static void beforeClass(){
        GuvnorUtils.init(new MockServletRequestForGuvnorUtils());
    }
    
    @Test
    public void testProcessClassification() throws Exception{
        InputStream processStream = this.getClass().getResourceAsStream("/processes/ClassificationTestProcess.bpmn");
        Assert.assertNotNull(processStream);
        
        String processDefinition = IOUtils.toString(processStream);
        
        ProcessClassifier classifier = new ProcessClassifier(null);
        
        ProcessClassifierResult result = classifier.parseAndClassifyProcess(processDefinition);
        
        //Start Event Type
        Assert.assertEquals("conditional", result.getStartEventType());
        
        //Facts:
        Map<String, FactTypeEntity> facts = result.getFactsMap();
        Assert.assertEquals(4, facts.size());
        Assert.assertTrue(facts.keySet().contains("Allergy"));
        Assert.assertTrue(facts.keySet().contains("VitalSign"));
        Assert.assertTrue(facts.keySet().contains("Patient"));
        Assert.assertTrue(facts.keySet().contains("Laboratory"));
        
        //Starting Facts:
        Assert.assertTrue(facts.get("Allergy").isStarting());
        Assert.assertTrue(facts.get("VitalSign").isStarting());
        Assert.assertFalse(facts.get("Patient").isStarting());
        Assert.assertFalse(facts.get("Laboratory").isStarting());
        
        //Cohort Type:
        Map<String, CohortTypeEntity> cohortTypes = result.getCohortFactsMap();
        Assert.assertEquals(2, cohortTypes.size());
        Assert.assertTrue(cohortTypes.keySet().contains("ClassA"));
        Assert.assertTrue(cohortTypes.keySet().contains("ClassB"));
        
        //Service Tasks
        Map<String, WorkItemEntity> workItems = result.getWorkItemsMap();
        Assert.assertEquals(4, workItems.size());
        Assert.assertTrue(workItems.keySet().contains("Registration"));
        Assert.assertTrue(workItems.keySet().contains("Order"));
        Assert.assertTrue(workItems.keySet().contains("Email"));
        Assert.assertTrue(workItems.keySet().contains("SMS"));
        
        Assert.assertEquals("Send Email", workItems.get("Email").getName());
        Assert.assertEquals("SMS", workItems.get("SMS").getName());
        
        
    }
}
