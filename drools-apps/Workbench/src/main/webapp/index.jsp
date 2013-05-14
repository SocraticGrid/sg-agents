<%-- 
    Document   : index
    Created on : Oct 29, 2010, 3:02:11 PM
    Author     : esteban
    <!--
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
-->
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <link rel="stylesheet" type="text/css" href="./resources/css/sirona.css" />
        <link rel="stylesheet" type="text/css" href="./lib/external/Extensive.css" />
        <link rel="stylesheet" type="text/css" href="./lib/ext-2.0.2/resources/css/ext-all.css" />
        

        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/prototype-1.5.1.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/ext-2.0.2/adapter/ext/ext-base.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/ext-2.0.2/ext-all.js"></SCRIPT>
        
        
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/external/Extensive.js"></SCRIPT>
        
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/clazz.js"></SCRIPT>
        
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/events.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/Application.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/config.js"></SCRIPT>

        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/StoreCombo.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/Editor.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/Menu.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/PackageTreePanel.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/CategoryTreePanel.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/NewProcessPanel.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/ProcessSearchPanel.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/ProcessSearchResultPanel.js"></SCRIPT>
        
        <SCRIPT LANGUAGE="JavaScript" SRC="./lib/core/setting/ServerSideSetting.js"></SCRIPT>


    </head>
    <body>
        <script type="text/javascript">
            
            var _window_title = "CDS Workbench";
            
            window.document.title = _window_title;
            
            var searchPanel = new org.socraticgrid.client.component.ProcessSearchPanel({
                region:'center', 
                collapsible : true,
                titleCollapse: true, 
                title: "Search",
                border	:false,
                width: "200"
            });
            
            var searchResultPanel = new org.socraticgrid.client.component.ProcessSearchResultPanel({
                region:'south', 
                collapsible : true,
                titleCollapse: true, 
                title: "Search Results",
                border	:false,
                width: "200",
                processSearchPanel: searchPanel 
            });
            
            var vp = new Ext.Viewport({
                id: 'mainViewPort',
                layout: 'border',
                items: [
                    new org.socraticgrid.client.component.menu.Menu({region:'north', border:false})  , 
                    {
                        id: '_west_main_panel_',
                        xtype: 'panel',
                        title: ' ',
                        region:'west', 
                        width: "200",
                        collapsible : true,
                        titleCollapse: true, 
                        border: false,
                        autoScroll: true,
                        items: [
                            new org.socraticgrid.client.component.CategoryTreePanel({
                                id: '_center_category_tree_',
                                region:'center', 
                                rootVisible: false,
                                split	: true,
                                animate: true,
                                collapsible : true,
                                titleCollapse: true, 
                                title: "Categories",
                                border	:false,
                                width: "200"
                            }),
                            searchPanel,
                            searchResultPanel
                        ]
                    },
                    new org.socraticgrid.client.component.Editor({region: 'center', border:false})
                ]
            });
            
            
            Ext.onReady(function(){
                Ext.QuickTips.init();
        
                try{
                    vp.getEl().mask("Loading User Settings"); 
                    serverSideSetting.init();
                    
                    var keys = ['package', 'package_uuid', 'workingSet', 'working_set_uuid','username'];
                    var data = serverSideSetting.getSettingValues(keys);
                    
                    APPLICATION.EVENTS.raiseEvent({
                        type: EVENTS.EVENT_USER_CREDENTIALS_SET,
                        username: data[4]
                    });
                    
                    APPLICATION.EVENTS.raiseEvent({
                        type: EVENTS.EVENT_MAIN_PACKAGE_CHANGED,
                        packageId: data[1],
                        packageName: data[0]
                    });
                    
                    APPLICATION.EVENTS.raiseEvent({
                        type: EVENTS.EVENT_MAIN_WORKING_SET_CHANGED,
                        workingSetId: data[3],
                        workingSetName: data[2]
                    });
                    
                    //when a process is opened we must change the title and collapse the west panel
                    APPLICATION.EVENTS.registerOnEvent(EVENTS.EVENT_PROCESS_SELECTED, 
                        function(event){
                            window.document.title = _window_title +" - "+event.processName;
                            Ext.getCmp('_west_main_panel_').collapse();
                        }.bind(this) 
                    );
                        
                    //when a process is closed we must change the title and expand the west panel
                    APPLICATION.EVENTS.registerOnEvent(EVENTS.EVENT_PROCESS_CLOSED, 
                        function(event){
                            window.document.title = _window_title;
                            Ext.getCmp('_west_main_panel_').expand();
                        }.bind(this) 
                    );
                        
                    //when a process is created -> open it
                    APPLICATION.EVENTS.registerOnEvent(EVENTS.EVENT_PROCESS_CREATED, 
                        function(event){
                            APPLICATION.EVENTS.raiseEvent({
                                type: EVENTS.EVENT_PROCESS_SELECTED,
                                processId: event.processId,
                                processName: event.processName
                            });
                        }.bind(this) 
                    );
                        
                } catch (ex){
                    console.log(ex);
                    Ext.MessageBox.show({
                        title: ex.name,
                        msg: ex.message,
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.ERROR
                    });
                }
                vp.getEl().unmask();
            });
        </script>
    </body>
</html>
