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
Ext.namespace('app', 'org.socraticgrid.client.component.menu');

org.socraticgrid.client.component.menu.Menu = function(configData){

    var storePackages = new StoreCombo(APPLICATION.CONFIG.LOV_URL,{
        'action':'getPackages'
    });
    
    var storeWorkingSets = new StoreCombo(APPLICATION.CONFIG.LOV_URL,{
        'action':'getWorkingSets'
    });

    var defaultConfig = {
        items : [
            {
                text: "New Process",
                cls:'x-btn-text-icon',
                icon:'resources/png/add.png',
                handler: newProcess
            },
            /*{
                text: "Resize",
                cls:'x-btn-text-icon',
                icon:'resources/png/add.png',
                handler: function(){
                    $$('iframe')[0].contentWindow.document.getElementsByTagName('iframe')[2].style.height = ($$('iframe')[0].contentWindow.document.body.scrollHeight-72)+"px";
                }
            },*/
            '->',
            {
                id: '_menu_username_label_',
                text: '',
                disabled: true,
                disabledClass:''
            },
            {
                text: "Logout",
                cls:'x-btn-text-icon',
                icon:'resources/png/cross.png',
                handler: logout
            }
            /*,'-','Package:',{
                xtype:'combo',
                width:167,
                allowBlank:false,
                store: storePackages,
                displayField:'value',
                valueField:'key',
                typeAhead: false,
                mode: 'remote',
                forceSelection: true,
                triggerAction: 'all',
                emptyText:'Package...',
                selectOnFocus:true,
                loadingText: 'Loading...',
                hiddenName: 'package',
                hiddenId: 'package',
                id: '_menu_Package_combo',
                listeners: {
                    select: function(cbo, rec, index){
                        APPLICATION.EVENTS.raiseEvent({
                            type: EVENTS.EVENT_MAIN_PACKAGE_CHANGED,
                            packageId: rec.data.key,
                            packageName: rec.data.value
                        });
                    }
                }
            },'Working Set:',{
                xtype:'combo',
                width:167,
                allowBlank:false,
                store: storeWorkingSets,
                displayField:'value',
                valueField:'key',
                typeAhead: false,
                mode: 'remote',
                forceSelection: true,
                triggerAction: 'all',
                emptyText:'Working-Set...',
                selectOnFocus:true,
                loadingText: 'Loading...',
                hiddenName: 'workingSet',
                hiddenId: 'workingSet',
                id: '_menu_WorkingSet_combo',
                disabled: true,
                listeners: {
                    select: function(cbo, rec, index){
                        APPLICATION.EVENTS.raiseEvent({
                            type: EVENTS.EVENT_MAIN_WORKING_SET_CHANGED,
                            workingSetId: rec.data.key,
                            workingSetName: rec.data.value
                        });
                    }
                }
            }*/
        ]
    }
    
    
    //event listeners
    
    APPLICATION.EVENTS.registerOnEvent(
        EVENTS.EVENT_USER_CREDENTIALS_SET, 
        function(event){
            Ext.getCmp('_menu_username_label_').setText(event.username);
        }.bind(this) 
    );
    
    
//    APPLICATION.EVENTS.registerOnEvent(
//        EVENTS.EVENT_MAIN_PACKAGE_CHANGED, 
//        function(event){
//            onMainPackageChanged(event.packageName);
//        }.bind(this) 
//    );
//        
//    APPLICATION.EVENTS.registerOnEvent(
//        EVENTS.EVENT_MAIN_WORKING_SET_CHANGED, 
//        function(event){
//            //alert(event.newWorkingSetId+" -> "+event.workingSetName);
//        }.bind(this) 
//    );
        
    
    //methods
    function onMainPackageChanged(newPackageName){
        var wsCombo = Ext.getCmp('_menu_WorkingSet_combo');
        var pkgCombo = Ext.getCmp('_menu_Package_combo');
        wsCombo.enable();
        storeWorkingSets.on({
            'beforeload' : function (t,options ){
                storeWorkingSets.baseParams = {
                    'action':'getWorkingSets',
                    'packageName':newPackageName
                };
            }
        });
        
        //select the first WS if any
        storeWorkingSets.on({
            'load' : function (store, records,options ){
                var selectedValue = "---"; //invalid initial value
                if (records.length > 0){
                    selectedValue = records[0].data.value;
                }else{
                    Ext.MessageBox.show({
                        title: 'Error',
                        msg: "The package '"+pkgCombo.getValue()+"' doesn't have any Working Set defined!",
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.ERROR
                    });
                    wsCombo.disable();
                }
                wsCombo.setValue(selectedValue); //if invalid, 'valueNotFoundText' is displayed
            }
        });
        
        storeWorkingSets.reload();
    }
    
    
    function newProcess(){
        var window = new Ext.Window({
            id:'winABM',
            layout:'fit',
            modal: false,
            plain:true,
            width: 350,
            height: 370,
            closeAction:'close',
            title:"Create a New Process",
            items:[
                new org.socraticgrid.client.component.NewProcessPanel({
                    packageId: APPLICATION.CONTEXT_INFO.getCurrentPackage(),
                    onSuccessCallback: function (processId, processName, categories){
                        APPLICATION.EVENTS.raiseEvent({
                            type: EVENTS.EVENT_PROCESS_CREATED,
                            processId: processId,
                            processName: processName,
                            categories: categories
                        });
                        
                        window.close();
                    },
                    onCancelCallback: function (){
                        window.close();
                    }
                })
            ]
        });
        
        window.show();
    }
    
    function logout(){
        window.location.href = "./logout.jsp"
    }
    
    //merge provided config with default
    if (configData){
        Ext.applyIf(configData, defaultConfig);
    }else{
        configData = defaultConfig;
    }

    org.socraticgrid.client.component.menu.Menu.superclass.constructor.call(this,configData);

}

Ext.extend(org.socraticgrid.client.component.menu.Menu,Ext.Toolbar,{});

