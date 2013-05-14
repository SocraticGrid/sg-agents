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
package org.socraticgrid.workbench.setting;

import java.io.IOException;
import java.util.Properties;

/**
 * Class in charge of externalize Application's settings defined in
 * settings.properties file.
 * This object is immutable in the sense that the values of its properties
 * can't be modified. If you want to modify some of its values for a particular
 * user, then use {@link UserSettings} instead.
 * @author esteban
 */
public class ApplicationSettings {

    private static ApplicationSettings INSTANCE;
    
    private Properties settings;
    
    public static interface SettingKey{
        String getKey();
    }
    
    public static enum APPLICATION_SETTINGS implements SettingKey{
        PACKAGE("package"),
        WORKING_SET("workingSet");
        
        APPLICATION_SETTINGS (String key){
            this.key = key;
        }
        
        private String key;
        
        public String getKey(){
            return key;
        }
    }
    
    private ApplicationSettings() throws IOException {
        this.settings = new Properties();
        this.settings.load(ApplicationSettings.class.getResourceAsStream("/settings.properties"));
    }
    
    public synchronized static ApplicationSettings getInstance(){
        if (INSTANCE == null){
            try {
                INSTANCE = new ApplicationSettings();
            } catch (IOException ex) {
                throw new IllegalStateException("Unable to load settings.properties", ex);
            }
        }
        return INSTANCE;
    }
    
    public String getSetting(SettingKey key, String defaultValue){
        return this.getSetting(key.getKey(), defaultValue);
    }
    
    public String getSetting(SettingKey key){
        return this.getSetting(key.getKey(), null);
    }
    
    public String getSetting(String key){
        return this.getSetting(key, null);
    }
    
    public String getSetting(String key, String defaultValue){
        return this.settings.getProperty(key, defaultValue);
    }
    
}
