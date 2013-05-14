<%--
 ~
 ~ Copyright (C) 2012 by Cognitive Medical Systems, Inc (http://www.cognitivemedciine.com)
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 ~ the License. You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 ~ an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 ~ specific language governing permissions and limitations under the License.
 ~
-->
 
<!--
 ~ Socratic Grid contains components to which third party terms apply. To comply with these terms, the following notice is provided:
 ~
 ~ TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 ~ Copyright (c) 2008, Nationwide Health Information Network (NHIN) Connect. All rights reserved.
 ~ Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 ~ 
 ~ - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 ~ - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 ~ - Neither the name of the NHIN Connect Project nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 ~ 
 ~ THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ~ 
 ~ END OF TERMS AND CONDITIONS
 ~
--%>
<%@page import="org.socraticgrid.workbench.setting.UserSettings"%>
<%@page import="org.socraticgrid.workbench.servlet.HttpSessionObjectManager"%>
<%@page import="org.socraticgrid.workbench.security.wso2.Saml2Util"%>
<%@page import="org.socraticgrid.workbench.security.wso2.SSOConstants"%>
<%@page import="org.socraticgrid.workbench.security.wso2.LogoutRequestBuilder"%>
<%@page import="java.util.UUID" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head></head>
<%
    String username = "Anonymous";
    
    if (!session.isNew()){
        UserSettings settings = HttpSessionObjectManager.getUserSettings(session);
        if (settings != null){
            username = settings.getSetting(UserSettings.USER_SETTINGS.USERNAME);
        }
    }

    String samlRequest = Saml2Util.marshall(
            new LogoutRequestBuilder().buildLogoutRequest(
                    username, SSOConstants.LOGOUT_USER));
    String encodedRequest = Saml2Util.encode(samlRequest);
    String relayState = java.util.UUID.randomUUID().toString();
%>
<body>
<p>You are now redirected to Stratos Identity. If the
   redirection fails, please click the post button.</p>

<form method="post" action="<%=Saml2Util.getProperty(SSOConstants.IDP_URL)%>">
    <p><input type="hidden" name="SAMLRequest"
              value="<%= encodedRequest %>"/>
        <input type="hidden" name="RelayState" value="<%= relayState %>"/>
        <button type="submit">POST</button>
    </p>
</form>

<script type="text/javascript">
    document.forms[0].submit();
</script>
</body>
</html>