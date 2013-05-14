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

import org.socraticgrid.workbench.model.persistence.ProcessEntity;
import org.socraticgrid.workbench.persistence.PersistentServiceFactory;
import org.socraticgrid.workbench.service.GuvnorService;
import org.socraticgrid.workbench.setting.UserSettings;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author esteban
 */
public class ProcessClassifierPersister {
    
    private GuvnorService guvnorService;
    private UserSettings userSettings; 

    public ProcessClassifierPersister(UserSettings userSettings) {
        this.userSettings = userSettings;
        guvnorService = new GuvnorService(userSettings);
    }
    
    public void persist(ProcessClassifierResult data){
        //update categories in Guvnor
        this.updateGuvnorCategories(data.getPackageName(), data.getProcessName(), data.getGuvnorCategories());
        
        boolean localPU = true;
        
        EntityManager entityManager = null;
        try{
            entityManager = PersistentServiceFactory.getInstance("WorkbenchPU").getEntityManager();
        } catch(Exception ex){
            ex.printStackTrace();
            //try with the localhost PU (TODO: remove this in production)
            entityManager = PersistentServiceFactory.getInstance("WorkbenchPU-Local").getEntityManager();
        }
        
        if (localPU){
            entityManager.getTransaction().begin();
        }
        try{

            //create the new ProcessEntity
            ProcessEntity processEntity = new ProcessEntity();
            processEntity.setId(data.getProcessId());
            processEntity.setName(data.getProcessName());
            processEntity.setPackageName(data.getPackageName());
            processEntity.setCohortEntities(data.getCohortFacts());
            processEntity.setFactTypeEntities(data.getFacts());
            processEntity.setStartEventType(data.getStartEventType());
            processEntity.setWorkItemEntities(data.getWorkItems());

            //save the new ProcessEntry
            entityManager.merge(processEntity);

            if (localPU){
                entityManager.getTransaction().commit();
            }
        } catch (RuntimeException e){
            if (localPU){
                try{
                    entityManager.getTransaction().rollback();
                } catch (RuntimeException e2){
                    e.printStackTrace();
                    throw e2;
                }
            }
            throw e;
        }finally{
            try{
                entityManager.close();
            } catch (RuntimeException e){
                e.printStackTrace();
                throw e;
            }
        }
    }
    
    private void updateGuvnorCategories(String pkg, String processName, List<String> guvnorCategories) {
        guvnorService.updateAssetCategories(pkg, processName, guvnorCategories);
    }
}
