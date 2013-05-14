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
Ext.namespace('app', 'org.socraticgrid.client.component');

org.socraticgrid.client.component.Editor = function(configData){
    
    window.addEventListener("resize", function(){
        Ext.getCmp("_main_editor_").resizeProcessPanel();
    });
    
    var defaultConfig = {
        id: '_main_editor_',
        xtype: 'panel',
        //html: '<iframe id="_main_editor_processFrame" name="processFrame" width="100%" height="100%" src="http://www.google.com"></iframe>',
        //html: '<iframe id="_main_editor_processFrame" name="processFrame" width="100%" height="100%" src="http://localhost:8080/drools-guvnor/"></iframe>',
        //html: '<iframe id="_main_editor_processFrame" name="processFrame" width="100%" height="100%" src="http://localhost:8080/designer/editor/?uuid=4c57622d-c13b-4d8a-964d-953959c100e6&profile=kmr&wsUuid=80a55a1f-1e31-4ebd-9a64-8634faf20c8a"></iframe>',
        html: '<iframe id="_main_editor_processFrame" name="processFrame" width="100%" height="100%" src="blank.html"></iframe>',

        currentProcessId: undefined,
        currentProcessName: undefined,
        
        _changeIframeURL: function(url, attachGuvnorOnloadEvent){
            var ifrm = window.document.getElementById('_main_editor_processFrame');
            if (attachGuvnorOnloadEvent){
                ifrm.onload = this.attachOnloadEventListener.bind(this);
            }
            ifrm.src = url;
        },
        
        reloadLastProcess: function(){
            if (!currentProcessId){
                alert ("No process selected");
                return;
            }
            
            this.onProcessSelected(currentProcessId, currentProcessName);
        },
        
        onProcessSelected: function(processId, processName){
            currentProcessId = processId;
            currentProcessName = processName;
            
            var url ='./WapamaServlet?action=openProcessEditor&uuid='+processId;
            
            this._changeIframeURL(url, true);
        },
        
        onProcessSaved: function(){
            //categorize the process
            this.categorizeCurrentProcess();
            
            //reloads the process
            this.reloadLastProcess();
        },
    
        onProcessClosed: function(){
            currentProcessId = undefined;
            currentProcessName = undefined;
            
            var url ='./blank.html';
            
            this._changeIframeURL(url, false);
            
            APPLICATION.EVENTS.raiseEvent({
                type: EVENTS.EVENT_PROCESS_CLOSED
            });
        },
    
        attachOnloadEventListener: function(){
            var ifrm = window.document.getElementById('_main_editor_processFrame');
            if (!ifrm.contentWindow.guvnorEditorObject){
                setTimeout('Ext.getCmp("_main_editor_").attachOnloadEventListener()', 1000);
                return;
            } 
            
            
            ifrm.contentWindow.guvnorEditorObject.registerAfterCloseButtonCallbackFunction(function(){
                this.onProcessClosed();
            }.bind(this));
            
            ifrm.contentWindow.guvnorEditorObject.registerBeforeSaveAllButtonCallbackFunction(function(){
            });
            
            ifrm.contentWindow.guvnorEditorObject.registerAfterSaveAllButtonCallbackFunction(function(){
                this.onProcessSaved();
            }.bind(this));
            
            ifrm.onload = undefined;
            
            Ext.getCmp("_main_editor_").tryToResizeProcessPanel();
        },
        
        tryToResizeProcessPanel:function(){
            if ($$('iframe')[0].contentWindow.document.getElementsByTagName('iframe').length < 3){
                setTimeout('Ext.getCmp("_main_editor_").tryToResizeProcessPanel()', 500);
                return;
            }
            
            Ext.getCmp("_main_editor_").resizeProcessPanel();
            
        },
        
        resizeProcessPanel:function(){
            if ($$('iframe')[0].contentWindow.document.getElementsByTagName('iframe').length < 3){
                return;
            }
            $$('iframe')[0].contentWindow.document.getElementsByTagName('iframe')[2].style.height = ($$('iframe')[0].contentWindow.document.body.scrollHeight-72)+"px";
        },
        
        categorizeCurrentProcess: function(){
            var mainViewPortEL = Ext.getCmp('mainViewPort').getEl();
            mainViewPortEL.mask("Adding Categories..."); 
            
            new Ajax.Request("./GuvnorPackageCRUDServlet", {
                asynchronous: false,
                method: 'POST',
                parameters: {
                    "action": "categorizeProcess",
                    "processId": currentProcessId,
                    "processName": currentProcessName
                },
                onSuccess: function(transport){
                    mainViewPortEL.unmask();
                    var response = Ext.util.JSON.decode(transport.responseText);
                            
                    if (response.success != undefined && !response.success){
                        throw { 
                            name:        "Error", 
                            message:     response.error+": "+response.detail
                        };
                    }
                    
                    APPLICATION.EVENTS.raiseEvent({
                        type: EVENTS.EVENT_PROCESS_SAVED,
                        processId: currentProcessId,
                        processName: currentProcessName,
                        categories: response.categories
                    });
                    
                    return response.value;
                },
                onFailure: (function(transport){
                    mainViewPortEL.unmask();
                    throw { 
                        name:        "Error", 
                        message:     "Communication Error: "+transport 
                    };
                })
            });
            
        }
    }
    
    //event listeners
    APPLICATION.EVENTS.registerOnEvent(EVENTS.EVENT_PROCESS_SELECTED, 
        function(event){
            this.onProcessSelected(event.processId, event.processName);
        }.bind(this) 
    );

    //merge provided config with default
    if (configData){
        Ext.applyIf(configData, defaultConfig);
    }else{
        configData = defaultConfig;
    }

    org.socraticgrid.client.component.Editor.superclass.constructor.call(this,configData);

}

Ext.extend(org.socraticgrid.client.component.Editor,Ext.Panel,{});

