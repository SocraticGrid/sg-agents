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
package org.socraticgrid.workbench.security;

import org.socraticgrid.workbench.servlet.HttpSessionObjectManager;
import org.socraticgrid.workbench.setting.UserSettings;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author esteban
 */
public class InternalAuthenticator {
    
    public boolean isSecureRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return request.getSession(true).getAttribute("securityToken") != null && HttpSessionObjectManager.getUserSettings(request.getSession(true)) != null ;
    }
    
    public void authenticate(String user, HttpServletRequest request, HttpServletResponse response, String authenticationTokenName, String authenticationToken) throws AuthenticationFailedException{
        //TODO: add credentials to user
        request.getSession(true).setAttribute("securityToken", UUID.randomUUID().toString());
        
        //creates default User Settings
        UserSettings userSettings = new UserSettings();
        userSettings.setSetting(UserSettings.USER_SETTINGS.USERNAME, user);
        userSettings.setSetting(UserSettings.USER_SETTINGS.SECURITY_TOKEN_NAME, authenticationTokenName);
        userSettings.setSetting(UserSettings.USER_SETTINGS.SECURITY_TOKEN, authenticationToken);
        HttpSessionObjectManager.setUserSettings(userSettings, request.getSession(true));
    }
    
    public void logout(HttpServletRequest request){
        HttpSession session = request.getSession(true);
        
        session.removeAttribute("securityToken");
        HttpSessionObjectManager.invalidate(session);
        
        session.invalidate();
    }
}
