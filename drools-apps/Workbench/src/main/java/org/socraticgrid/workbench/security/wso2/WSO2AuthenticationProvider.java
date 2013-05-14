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
package org.socraticgrid.workbench.security.wso2;

import org.socraticgrid.workbench.security.AuthenticationFailedException;
import org.socraticgrid.workbench.security.HttpAuthenticationProvider;
import org.socraticgrid.workbench.security.InternalAuthenticator;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.opensaml.xml.XMLObject;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Subject;

/**
 *
 * @author esteban
 */
public class WSO2AuthenticationProvider implements HttpAuthenticationProvider {
    
    private String securityCallbackURL;
    private SamlConsumer samlConsumer;

    @Override
    public void configure(String securityCallbackURL, Map<String, String> configurationParameters) {
        this.securityCallbackURL = securityCallbackURL;
        
        Saml2Util.addProperty(SSOConstants.IDP_URL, configurationParameters.get(SSOConstants.IDP_URL));

        if (Saml2Util.getProperty(SSOConstants.IDP_URL) == null){
            throw new IllegalStateException("You need to provide an Identity Provider URL ('"+SSOConstants.IDP_URL+"')");
        }
        
        Saml2Util.addProperty(SSOConstants.ISSUER_ID, configurationParameters.get(SSOConstants.ISSUER_ID));
        if (Saml2Util.getProperty(SSOConstants.ISSUER_ID) == null){
            throw new IllegalStateException("You need to provide a Service Provider Id ('"+SSOConstants.ISSUER_ID+"')");
        }
        
        this.samlConsumer = new SamlConsumer(Saml2Util.getProperty(SSOConstants.IDP_URL));
        
        Saml2Util.addProperty(SSOConstants.KEY_STORE_NAME, configurationParameters.get(SSOConstants.KEY_STORE_NAME));
        Saml2Util.addProperty(SSOConstants.KEY_STORE_PASSWORD, configurationParameters.get(SSOConstants.KEY_STORE_PASSWORD));
        Saml2Util.addProperty(SSOConstants.IDP_ALIAS, configurationParameters.get(SSOConstants.IDP_ALIAS));
        
    }

    @Override
    public void doLoginRedirect(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();
        try {
            String requestMessage = samlConsumer.buildRequestMessage(Saml2Util.getProperty(SSOConstants.ISSUER_ID), request.getRequestURL().toString()+"/"+this.securityCallbackURL);
            System.out.println("----------------------------------------");
            System.out.println(requestMessage);
            System.out.println("----------------------------------------");
            response.sendRedirect(requestMessage);

        } finally {
            out.close();
        }
    }

    public String processSecurityCallback(HttpServletRequest request, HttpServletResponse response, InternalAuthenticator internalAuthenticator) throws AuthenticationFailedException {
        try {
            String resp = request.getParameter("SAMLResponse");
            String relayState = request.getParameter("RelayState");
            
            System.out.println("\n\n");
            System.out.println(resp);
            System.out.println("\n\n");
            
            XMLObject samlObject = Saml2Util.unmarshall(resp);
            
            // if this is a logout Response, then send the user to logout_complete.jsp
            // after removing the session attributes.
            if (samlObject instanceof LogoutResponse) {
                internalAuthenticator.logout(request);
                return request.getContextPath();
            }
            
            // Assuming it a Response
            Response samlResponse = (Response) samlObject;
            List<Assertion> assertions = samlResponse.getAssertions();
            String username = "anonymous user";
            
            // extract the username
            if(assertions != null && assertions.size() > 0){
                Subject subject = assertions.get(0).getSubject();
                if(subject != null){
                    if(subject.getNameID() != null){
                        username = subject.getNameID().getValue();
                    }
                }
            }

            // validating the signature
            if(!Saml2Util.validateSignature(samlResponse)){
                throw new AuthenticationFailedException("Invalid username/password!");
            }
            
            
            internalAuthenticator.authenticate(username, request, response, "SAMLResponse", Saml2Util.encode(resp) );
            
            return request.getContextPath();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new AuthenticationFailedException("Invalid username/password!");
        }
    }

}
