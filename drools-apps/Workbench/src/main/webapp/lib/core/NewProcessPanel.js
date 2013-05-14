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

org.socraticgrid.client.component.NewProcessPanel = function(configData){

    var serviceURL = APPLICATION.CONFIG.WORKBENCH_URL+"/GuvnorPackageCRUDServlet";
    var serviceParams = {'action':'save'};

    var packageId = configData && configData.packageId?configData.packageId:undefined;
    
    if (!packageId){
        Ext.MessageBox.show({
            title: 'Error',
            msg: 'No Package Selected',
            buttons: Ext.MessageBox.OK,
            icon: Ext.MessageBox.ERROR
        });
        return;
    }
    
    var onSuccessCallback = configData && configData.onSuccessCallback?configData.onSuccessCallback:undefined;
    var onCancelCallback = configData && configData.onCancelCallback?configData.onCancelCallback:undefined;
    
    var defaultConfig = {
        id: '_New_Process_Panel_',
        baseCls: 'x-plain',
        defaultType: 'textfield',
        items : [
            {
                xtype: 'hidden',
                fieldLabel: 'Package',
                name: 'package',
                value: packageId
            },
            {
                xtype: 'hidden',
                id: '_New_Process_Panel_Categories',
                name: 'categories',
                value: packageId
            },
            {
                fieldLabel: 'Package',
                value: packageId,
                disabled: true
            },{
                fieldLabel: 'Name',
                name: 'name',
                allowBlank: false,
                blankText: 'You must provide a Name for the process',
                maxLength : 50
            },{
                xtype: 'textarea',
                fieldLabel: 'Summary',
                name: 'summary'
            },
            new org.socraticgrid.client.component.CategoryTreePanel({
                id: '_New_Process_Cat_Tree', 
                showAssets: false,
                selModel: new Ext.tree.MultiSelectionModel(),
                height: 150
            })
        ],
        buttons: [{
            text: "Create",
            cls: 'x-btn-text-icon',
            icon: './resources/png/disk.png',
            tooltip: "Create the Process",
            handler : doCreate
        },{
            text: 'Cancel',
            cls: 'x-btn-text-icon',
            icon: './resources/png/cross.png',
            tooltip: 'Cancel',
            handler : doCancel
        }]
    }
    
    function doCreate(){
        var panel = Ext.getCmp('_New_Process_Panel_');
        var categoriesTxt = Ext.getCmp('_New_Process_Panel_Categories');
        var catTree = Ext.getCmp('_New_Process_Cat_Tree');
        
        if (catTree.getSelectionModel().getSelectedNodes().length == 0){
            alert ("Please Select a Category.");
            return;
        }
        
        var separator = "";
        var categories = "";
        var categoriesArray = new Array();
        
        catTree.getSelectionModel().getSelectedNodes().each(function(cat){
           categoriesArray.push(cat.id);
           categories+= separator + cat.id;
           if (separator == ""){
               separator = ',';
           }
        });
        
        categoriesTxt.setValue(categories);
        
        if (panel.getForm().isValid()){
            panel.getEl().mask( panel.getEl().mask());
            panel.getForm().submit({
                url : serviceURL,
                params: serviceParams,
                failure: function (form, action) {
                    var object = Ext.util.JSON.decode(action.response.responseText);
                    
                    panel.getEl().unmask();

                    Ext.MessageBox.show({
                        title: 'Error',
                        msg: object.error,
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.ERROR
                    });
                },
                success: function (form, request) {
                    
                    panel.getEl().unmask();

                    if (onSuccessCallback){
                        var processId = request.result.data.metadata.Uuid;
                        var processName = request.result.data.metadata.title;
                        onSuccessCallback(processId, processName, categoriesArray);
                    }

                }
            })
        }
    }

    function doCancel(){
        if (onCancelCallback){
            onCancelCallback();
        }
    }
       
    //merge provided config with default
    if (configData){
        Ext.applyIf(configData, defaultConfig);
    }else{
        configData = defaultConfig;
    }

    org.socraticgrid.client.component.NewProcessPanel.superclass.constructor.call(this,configData);

}

Ext.extend(org.socraticgrid.client.component.NewProcessPanel,Ext.form.FormPanel,{});

