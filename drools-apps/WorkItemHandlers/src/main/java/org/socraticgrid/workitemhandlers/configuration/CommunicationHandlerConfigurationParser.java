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
package org.socraticgrid.workitemhandlers.configuration;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parses from a String sent as WorkItem parameter to a CommunicationHandlerConfiguration object.
 * @author esteban
 */
public class CommunicationHandlerConfigurationParser {
    
    public static final String CONFIGURATION_SEPARATOR_STRING = "_U_U_";
    
    //"[{"receiver": "actor1"_U_U_ "channel": "ALERT"_U_U_ "template": "template1"_U_U_ "timeout": "0ms"}_U_U_ {"receiver": "actor2"_U_U_ "channel": "ALERT"_U_U_ "template": "template2"_U_U_ "timeout": "12000ms"}]"
    public CommunicationHandlerConfiguration parseString(String configString){
        
        if (configString == null || !configString.contains(CONFIGURATION_SEPARATOR_STRING)){
            throw new IllegalArgumentException("Malformed 'configuration' parameter: "+configString);
        }
        
        //replace separator string
        //"[{"receiver": "actor1", "channel": "ALERT", "template": "template1", "timeout": "0s"}, {"receiver": "actor2", "channel": "ALERT", "template": "template2", "timeout": "120s"}]"
        configString = configString.replaceAll(CONFIGURATION_SEPARATOR_STRING, ",");
        
        CommunicationHandlerConfiguration configuration = new CommunicationHandlerConfiguration();
        List<String> receivers = new ArrayList<String>();
        List<String> channels = new ArrayList<String>();
        List<String> templates = new ArrayList<String>();
        List<String> timeouts = new ArrayList<String>();
        
        //parse the string
        JsonParser parser = new JsonParser();
        JsonElement parsedContent = parser.parse(configString);
        
        JsonArray array = parsedContent.getAsJsonArray();
        Iterator<JsonElement> iterator = array.iterator();
        while (iterator.hasNext()) {
            JsonObject jsonObject = (JsonObject) iterator.next();
            receivers.add(jsonObject.get("receiver").getAsString());
            channels.add(jsonObject.get("channel").getAsString());
            templates.add(jsonObject.get("template").getAsString());
            timeouts.add(jsonObject.get("timeout").getAsString());
        }
        
        configuration.setReceivers(receivers);
        configuration.setChannels(channels);
        configuration.setTemplates(templates);
        configuration.setTimeouts(timeouts);
        
        return configuration;
    }
}
