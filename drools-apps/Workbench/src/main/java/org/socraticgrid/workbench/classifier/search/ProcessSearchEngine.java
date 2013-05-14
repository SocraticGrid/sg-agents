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
package org.socraticgrid.workbench.classifier.search;

import org.socraticgrid.workbench.model.persistence.CohortTypeEntity;
import org.socraticgrid.workbench.model.persistence.FactTypeEntity;
import org.socraticgrid.workbench.model.persistence.ProcessEntity;
import org.socraticgrid.workbench.model.persistence.WorkItemEntity;
import org.socraticgrid.workbench.persistence.PersistentServiceFactory;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.ManagedType;

/**
 *
 * @author esteban
 */
public class ProcessSearchEngine {

    public ProcessSearchEngineResult search(ProcessSearchRequest request, Integer start, Integer limit) {
        EntityManager entityManager;
        try {
            entityManager = PersistentServiceFactory.getInstance("WorkbenchPU").getEntityManager();
        } catch (Exception ex) {
            ex.printStackTrace();
            //try with the localhost PU (TODO: remove this in production)
            entityManager = PersistentServiceFactory.getInstance("WorkbenchPU-Local").getEntityManager();
        }
        
        try{
            CriteriaQuery<ProcessEntity> query = this.createQuery(entityManager, request);

            TypedQuery<ProcessEntity> typedQuery = entityManager.createQuery(query);
            //TODO: change this to a select count(*)
            int total = typedQuery.getResultList().size();

            if (start != null){
                typedQuery.setFirstResult(start);
            }
            if (limit != null){
                typedQuery.setMaxResults(limit);
            }

            List<ProcessEntity> entities = typedQuery.getResultList();

            ProcessSearchEngineResult result = new ProcessSearchEngineResult();
            result.setResults(entities);
            result.setTotal(total);

            return result;
        } finally {
            try{
                entityManager.close();
            } catch (RuntimeException e){
                e.printStackTrace();
                throw e;
            }
        }
    }

    private CriteriaQuery<ProcessEntity> createQuery(EntityManager entityManager, ProcessSearchRequest request) {

        ManagedType<ProcessEntity> processEntityManagedType = entityManager.getMetamodel().managedType(ProcessEntity.class);
        ManagedType<FactTypeEntity> factTypeEntityManagedType = entityManager.getMetamodel().managedType(FactTypeEntity.class);
        ManagedType<CohortTypeEntity> cohortTypeEntityManagedType = entityManager.getMetamodel().managedType(CohortTypeEntity.class);
        ManagedType<WorkItemEntity> workItemEntityManagedType = entityManager.getMetamodel().managedType(WorkItemEntity.class);


        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ProcessEntity> query = criteriaBuilder.createQuery(ProcessEntity.class);
        Root<ProcessEntity> p = query.from(ProcessEntity.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        
        //processName filter
        if (request.getProcessName() != null && !request.getProcessName().trim().isEmpty()) {
            Predicate nameCondition = criteriaBuilder.like(
                criteriaBuilder.lower(
                    p.get(
                        processEntityManagedType.getDeclaredSingularAttribute("name", String.class)
                    )
                ), "%" + request.getProcessName() + "%"
            );
            
            predicates.add(nameCondition);
        }
        
        //startType filter
        if (request.getStartType() != null && !request.getStartType().trim().isEmpty()) {
            Predicate startTypeCondition = criteriaBuilder.equal(
                criteriaBuilder.lower(
                    p.get(
                        processEntityManagedType.getDeclaredSingularAttribute("startEventType", String.class)
                    )
                ), request.getStartType()
            );
        
            predicates.add(startTypeCondition);
        }
 
        //fact types filter
        if (request.getFactTypes() != null && !request.getFactTypes().isEmpty()){
            
            for (FactTypeSearch factTypeSearch : request.getFactTypes()) {
                Join<ProcessEntity,FactTypeEntity> processFactType = p.join( processEntityManagedType.getDeclaredCollection("factTypeEntities", FactTypeEntity.class) );
                Predicate factTypesPredicate = criteriaBuilder.and(
                        criteriaBuilder.equal(
                            processFactType.get(
                                factTypeEntityManagedType.getDeclaredSingularAttribute("name", String.class)), factTypeSearch.getName()
                        ),
                        criteriaBuilder.equal(
                            processFactType.get(
                                factTypeEntityManagedType.getDeclaredSingularAttribute("starting", Boolean.class)), factTypeSearch.isInitial()
                        )
                );
                predicates.add(factTypesPredicate);
            }
        }
        
        //Cohort types filter
        if (request.getCohortTypes() != null && !request.getCohortTypes().isEmpty()){
            
            for (CohortTypeSearch cohortTypeSearch : request.getCohortTypes()) {
                Join<ProcessEntity,CohortTypeEntity> processCohortType = p.join( processEntityManagedType.getDeclaredCollection("cohortEntities", CohortTypeEntity.class) );
                Predicate cohortTypesPredicate = criteriaBuilder.equal(
                            processCohortType.get(
                                cohortTypeEntityManagedType.getDeclaredSingularAttribute("name", String.class)), cohortTypeSearch.getName()
                        );
                predicates.add(cohortTypesPredicate);
            }
        }
        
        
        //WorkItem types filter
        if (request.getServiceTasks() != null && !request.getServiceTasks().isEmpty()){
            
            for (ServiceTaskSearch serviceTaskSearch : request.getServiceTasks()) {
                Join<ProcessEntity,WorkItemEntity> processCohortType = p.join( processEntityManagedType.getDeclaredCollection("workItemEntities", WorkItemEntity.class) );
                Predicate serviceTasksPredicate = criteriaBuilder.like(
                            processCohortType.get(
                                workItemEntityManagedType.getDeclaredSingularAttribute(serviceTaskSearch.getType().toLowerCase(), String.class)), 
                                "%"+serviceTaskSearch.getValue()+"%"
                        );
                predicates.add(serviceTasksPredicate);
            }
        }
        
        if (!predicates.isEmpty()){
            query.where(predicates.toArray(new Predicate[predicates.size()]));
        }
        
        
        return query;
    }
}
