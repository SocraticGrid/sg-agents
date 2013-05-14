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
package org.drools.mas.objectserializer;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.socraticgrid.adapter.fact.FactImpl;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.representer.Representer;

/**
 *
 * @author esteban
 */
public class YamlObjectSerializer implements ObjectSerializer {

    private class FactRepresenter extends Representer {

        private List<Class> bannedClasses = new ArrayList<Class>() {
            {
                add(FactImpl.class);
            }
        };
        
        private List<String> bannedLiteralProperties = new ArrayList<String>() {
            {
                add("accountTypeStrings");
                add("attendingMD");
                add("birthOrder");
                add("collectedDuringEncounterEncounter");
                add("communicatedByPerson");
            }
        };
        private List<String> bannedRegexProperties = new ArrayList<String>() {
            {
                add("^.*Agent[s]?$");
                add("^.*Range[s]?$");
                add("^.*Integer?$");
                add("^.*String?$");
            }
        };
        
        private Set<Property> bannedClassesPropertiesCache = new HashSet<Property>();

        protected Set<Property> getProperties(Class<? extends Object> type)
                throws IntrospectionException {
            Set<Property> set = super.getProperties(type);
            Set<Property> filtered = new TreeSet<Property>();
            
            if (type.getName().equals("urn.gov.hhs.fha.nhinc.adapter.fact.FactImpl")){
                return filtered;
            }
            
            if (!bannedClasses.isEmpty() && bannedClassesPropertiesCache.isEmpty()){
                for (Class type1 : bannedClasses) {
                    bannedClassesPropertiesCache.addAll(super.getProperties(type1)); 
                }
            }
            
            // filter properties
            for (Property property : set) {

                if(bannedClassesPropertiesCache.contains(property)){
                    continue;
                }
                
                if (bannedLiteralProperties.contains(property.getName())) {
                    continue;
                } 
                
                boolean banned = false;
                for (String regex : bannedRegexProperties) {
                    if (property.getName().matches(regex)) {
                        System.out.println("property " + property.getName() + " BANNED");
                        banned = true;
                        continue;
                    }
                }
               
                if(banned){
                    continue;
                }
                
                
                filtered.add(property);

            }


            return filtered;
        }

    }

    @Override
    public String serialize(Object obj) {

        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(new FactRepresenter(), options);

        return yaml.dump(obj);
    }

    @Override
    public Object deserialize(String serializedObj) {
        Yaml yaml = new Yaml();
        return yaml.load(serializedObj);
    }
}
