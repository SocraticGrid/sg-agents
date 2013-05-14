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

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Select OID</title>
        <link rel="stylesheet" type="text/css" href="./resources/css/sirona.css" />
        <link rel="stylesheet" type="text/css" href="./lib/ext-2.0.2/resources/css/ext-all.css" />
        

        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/prototype-1.5.1.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/ext-2.0.2/adapter/ext/ext-base.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/ext-2.0.2/ext-all.js"></SCRIPT>
        
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/clazz.js"></SCRIPT>
        
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/events.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/Application.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/config.js"></SCRIPT>
        
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/OIDLookupPanel.js"></SCRIPT>
    </head>
    <body>
        <h1>${param['factType']} -> ${param['fieldName']} : [ ${param['cf_id']} -> ${param['cf_value']} ]</h1>
        ID:&nbsp;<input type="hidden" name="cf_id" id="cf_id" value="${param['cf_id']}"/><br/>
        VALUE:&nbsp;<input type="hidden" name="cf_value" id="cf_value" value="${param['cf_value']}"/>
        
        <script type="text/javascript">
        var vp = new Ext.Viewport({
                id: 'oidFormViewPort',
                layout: 'border',
                items: [
                    new org.socraticgrid.client.component.OIDLookupPanel({
                        region:'center', 
                        factType: '${param['factType']}',
                        value: '${param['cf_value']}',
                        labelAlign: 'top'
                        })
                ]
            });
            
            
            Ext.onReady(function(){
                Ext.QuickTips.init();
        
        
                APPLICATION.EVENTS.registerOnEvent(EVENTS.EVENT_OID_PANEL_ELEMENT_SELECTED, 
                    function(event){
                        window.document.getElementById('cf_id').value = event.elementId;
                        window.document.getElementById('cf_value').value = event.elementName;
                    }.bind(this) 
                );
        
            });
            </script>
    </body>
</html>
