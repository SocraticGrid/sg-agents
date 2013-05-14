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

import org.socraticgrid.workbench.setting.ApplicationSettings.SettingKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Class containing an in-memory Map of settings for a particular User.
 * When looking for a particular setting, if it is not present in the Map, it 
 * is searched in {@link ApplicationSettings}
 * @author esteban
 */
public class UserSettings {

    private Map<String,String> settings;
    
    public static enum USER_SETTINGS implements SettingKey {
        USERNAME("username"),
        PACKAGE_UUID("package_uuid"),
        WORKING_SET_UUID("working_set_uuid"),
        SECURITY_TOKEN_NAME("security_token_name"),
        SECURITY_TOKEN("security_token");
        
        USER_SETTINGS (String key){
            this.key = key;
        }
        
        private String key;
        
        public String getKey(){
            return key;
        }
    }
    
    public UserSettings(){
        this.settings = new HashMap<String, String>();
    }
    
    /**
     * Returns a setting given a key. If the User doesn't have any value for
     * a particular key, the setting is searched in {@link ApplicationSettings}.
     * If the setting is not there, defaultValue is returned.
     * @param key
     * @param defaultValue
     * @return 
     */
    public String getSetting(SettingKey key, String defaultValue){
        return this.getSetting(key.getKey(), defaultValue);
    }
    
    /**
     * Same as {@link #getSetting(org.socraticgrid.workbench.setting.ApplicationSettings.SettingKey, java.lang.String) getSetting(key, null)}
     * @param key
     * @return 
     */
    public String getSetting(SettingKey key){
        return this.getSetting(key, null);
    }
    
    public String getSetting(String key){
        return this.getSetting(key, null);
    }
    
    public String getSetting(String key, String defaultValue){
        if (this.settings.containsKey(key)){
            return this.settings.get(key);
        }else{
            return ApplicationSettings.getInstance().getSetting(key, defaultValue);
        }
    }
    
    public void setSetting(SettingKey key, String value){
        this.settings.put(key.getKey(), value);
    }
    
}
