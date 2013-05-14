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
package org.socraticgrid.workbench.security.opensso;

import org.socraticgrid.workbench.security.AuthenticationFailedException;
import org.socraticgrid.workbench.security.HttpAuthenticationProvider;
import org.socraticgrid.workbench.security.InternalAuthenticator;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author esteban
 */
public class OpenSSOAuthenticationProvider implements HttpAuthenticationProvider {

    
    @Override
    public void configure(String securityCallbackURL, Map<String, String> configurationParameters) {
    }

    @Override
    public void doLoginRedirect(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            //TODO: this is just a test
            String user = request.getParameter("user");
            String pass = request.getParameter("pass");
            
//            try { // Call Web Service Operation
//                com.sun.identity.idsvcs.opensso.IdentityServicesImplService service = new com.sun.identity.idsvcs.opensso.IdentityServicesImplService();
//                com.sun.identity.idsvcs.opensso.IdentityServicesImpl port = service.getIdentityServicesImplPort();
//                // TODO initialize WS operation arguments here
//                java.lang.String username = user;
//                java.lang.String password = pass;
//                java.lang.String uri = "";
//
//                com.sun.identity.idsvcs.opensso.Token result = port.authenticate(username, password, uri);
//                
//                request.getSession(true).setAttribute("securityToken", result);
//                
//                response.sendRedirect(request.getContextPath());
//                
//            } catch (Exception ex) {
//            // TODO handle custom exceptions here
//            }

        } finally {
            out.close();
        }
    }

    

//    public boolean isSecureRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        com.sun.identity.idsvcs.opensso.Token token = (com.sun.identity.idsvcs.opensso.Token) request.getSession(true).getAttribute("securityToken");
//        
//        try { // Call Web Service Operation
//            com.sun.identity.idsvcs.opensso.IdentityServicesImplService service = new com.sun.identity.idsvcs.opensso.IdentityServicesImplService();
//            com.sun.identity.idsvcs.opensso.IdentityServicesImpl port = service.getIdentityServicesImplPort();
//            return port.isTokenValid(token);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return false;
//        
//    }

    public String processSecurityCallback(HttpServletRequest request, HttpServletResponse response, InternalAuthenticator internalAuthenticator) throws AuthenticationFailedException {
        //request.getSession(true).setAttribute("securityToken", relayState);
        return null;
    }

}
