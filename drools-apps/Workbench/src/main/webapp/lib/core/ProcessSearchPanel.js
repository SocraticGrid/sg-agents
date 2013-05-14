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

org.socraticgrid.client.component.ProcessSearchPanel = function(configData){
    
    var id = '_process_search_panel_' + new Date().getTime();
    var txtNameId = id+"_name";  
    var cboStartId = id+"_startType";  
    
    
    //form elements width
    var formElementsWidth = configData && configData.formElementsWidth != undefined?configData.formElementsWidth:140;
    
    
    //field-sets
    var processSearchFactFieldSet = new org.socraticgrid.client.component.ProcessSearchFactFieldSet({
        collapsed: true,
        cls: 'processSearchFieldSet'
    });
        
    var processSearchCohortFieldSet = new org.socraticgrid.client.component.ProcessSearchCohortFieldSet({
        collapsed: true,
        cls: 'processSearchFieldSet'
    });
        
    var processSearchServiceTaskFieldSet = new org.socraticgrid.client.component.ProcessSearchServiceTaskFieldSet({
        collapsed: true,
        cls: 'processSearchFieldSet'
    });
    
    /*              Panel Configuration     */
    var defaultConfig = {
        id: id,
        defaultType: 'textfield',
        labelWidth: 50,
        autoScroll: true,        
        defaults:{
            style:'font-weight: bold;'
        }, 
        tbar :[
        {
            text: 'Search',
            cls:'x-btn-text-icon',
            icon:'resources/png/view.png',
            handler: doSearch.bind(this)
        },
        {
            text: 'Clear',
            cls:'x-btn-text-icon',
            icon:'resources/png/cross.png',
            handler: clearFilter
        }
        ],
        items : [
        {
            id: txtNameId,
            fieldLabel: 'Name',
            name: 'name',
            maxLength : 100,
            width: formElementsWidth
        },
        {
            id: cboStartId,
            fieldLabel: 'Start',
            name: 'startType',
            xtype: 'combo',
            store: new Ext.data.SimpleStore({
                fields: ['dataFieldName', 'displayFieldName'],
                data: [['', 'Any'], ['simple', 'Simple'], ['conditional', 'Conditional'], 
                ['signal', 'Signal'], ['timer', 'Timer']],
                autoLoad: true
            }),
            displayField: 'displayFieldName', 
            valueField: 'dataFieldName', 
            mode          : 'local',
            hiddenName : 'startType',
            triggerAction: 'all',
            selectOnFocus: true,
            width: formElementsWidth,
            forceSelection: true,
            editable: false,
            listeners: {
                render: function(_this){
                    Ext.getCmp(cboStartId).setValue("");
                }
            } 
        },
        processSearchFactFieldSet,
        processSearchCohortFieldSet,
        processSearchServiceTaskFieldSet
        ]
    }
    
    //merge provided config with default
    if (configData){
        Ext.applyIf(configData, defaultConfig);
    }else{
        configData = defaultConfig;
    }
    
    //Event Listeners
    
    
    //Methods
    function clearFilter(){
        Ext.getCmp(txtNameId).setValue("");
        Ext.getCmp(cboStartId).setValue("");
        
        processSearchFactFieldSet.clearData();
        processSearchCohortFieldSet.clearData();
        processSearchServiceTaskFieldSet.clearData();
    }
    
    function doSearch(){
        var searchRequest = new Object();
        
        searchRequest.processName = Ext.getCmp(txtNameId).getValue();
        searchRequest.startType = Ext.getCmp(cboStartId).getValue();
        
        searchRequest.factTypes = processSearchFactFieldSet.createSearchObject();
        searchRequest.cohortTypes = processSearchCohortFieldSet.createSearchObject();
        searchRequest.serviceTasks = processSearchServiceTaskFieldSet.createSearchObject();
        
        APPLICATION.EVENTS.raiseEvent({
            type: EVENTS.EVENT_PROCESS_SEARCH_PANEL_NEW_SEARCH,
            source: this,
            searchQuery: Object.toJSON(searchRequest)
        });
        
    }
    
    //Apply config data to superclass
    org.socraticgrid.client.component.ProcessSearchPanel.superclass.constructor.call(this,configData);
    
    
}

Ext.extend(org.socraticgrid.client.component.ProcessSearchPanel,Ext.form.FormPanel,{});



/**
 * ProcessSearchFactFieldSet
 */
org.socraticgrid.client.component.ProcessSearchFactFieldSet = function(configData){
    
    var id = '_process_search_fact_field_set' + new Date().getTime();
    
    /*  Data Grid */
    var gridId = id+'_grid';
    
    var RecordDef= Ext.data.Record.create([{
        name: 'name'
    }, {
        name: 'initial'
    }]);

    var gridProxy = new Ext.data.MemoryProxy({
        root: []
    });

    var gridStore = new Ext.data.Store({
        autoDestroy: true,
        reader: new Ext.data.JsonReader({
            root: "root"
        }, RecordDef),
        proxy: gridProxy
    });
    gridStore.load();
    
    var itemDeleter = new Extensive.grid.ItemDeleter();
    
    var grid = new Ext.grid.EditorGridPanel({
        store: gridStore,
        id: gridId,
        stripeRows: true,
        width: 185,
        cm: new Ext.grid.ColumnModel([
        {
            id: 'name',
            header: 'Name',
            width: 115,
            dataIndex: 'name',
            sortable: false,
            menuDisabled: true,
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        }, {
            id: 'initial',
            header: 'Initial',
            width: 40,
            dataIndex: 'initial',
            sortable: false,
            menuDisabled: true,
            editor: new Ext.form.Checkbox({
                })
        }, 
        itemDeleter
        ]),
        selModel: itemDeleter,
        autoHeight: true,
        tbar: [{
            text: 'Add Filter',
            cls:'x-btn-text-icon',
            icon:'resources/png/filters.png',
            handler : function(){
                gridStore.add(new RecordDef({
                    name: '',
                    initial: 'false'
                }));
                grid.fireEvent('cellclick', grid, gridStore.getCount()-1, 0, null);
            }
        }],
        clicksToEdit: 1
    });
    
    /*              Panel Configuration     */
    var defaultConfig = {
        id: id,
        collapsible: true,
        title:'Fact Types',
        layout: 'form',
        //height: 200,
        autoHeight: true,
        defaults:{
            style:'font-weight: bold;'
        },
        items:[
            grid
        ],
        
        clearData: function(){
            gridStore.removeAll();
            grid.getView().refresh();
        },
        
        createSearchObject: function(){
            var result = [];
            gridStore.each(function(record){
                if (record.data.name != ""){
                        result.push({
                        name: record.data.name,
                        initial: record.data.initial
                    })
                }
            });
            
            return result;
        }
    }
    
    //merge provided config with default
    if (configData){
        Ext.applyIf(configData, defaultConfig);
    }else{
        configData = defaultConfig;
    }
    
    org.socraticgrid.client.component.ProcessSearchFactFieldSet.superclass.constructor.call(this,configData);
}

Ext.extend(org.socraticgrid.client.component.ProcessSearchFactFieldSet,Ext.form.FieldSet,{});




/**
 * ProcessSearchCohortFieldSet
 */
org.socraticgrid.client.component.ProcessSearchCohortFieldSet = function(configData){
    
    var id = '_process_search_cohort_field_set' + new Date().getTime();
    
    /*  Data Grid */
    var gridId = id+'_grid';
    
    var RecordDef= Ext.data.Record.create([{
        name: 'name'
    }]);

    var gridProxy = new Ext.data.MemoryProxy({
        root: []
    });

    var gridStore = new Ext.data.Store({
        autoDestroy: true,
        reader: new Ext.data.JsonReader({
            root: "root"
        }, RecordDef),
        proxy: gridProxy
    });
    gridStore.load();
    
    var itemDeleter = new Extensive.grid.ItemDeleter();
    
    var grid = new Ext.grid.EditorGridPanel({
        store: gridStore,
        id: gridId,
        stripeRows: true,
        width: 185,
        cm: new Ext.grid.ColumnModel([
        {
            id: 'name',
            header: 'Name',
            width: 155,
            dataIndex: 'name',
            sortable: false,
            menuDisabled: true,
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        }, 
        itemDeleter
        ]),
        selModel: itemDeleter,
        autoHeight: true,
        tbar: [{
            text: 'Add Filter',
            cls:'x-btn-text-icon',
            icon:'resources/png/filters.png',
            handler : function(){
                gridStore.add(new RecordDef({
                    name: ''
                }));
                
                grid.fireEvent('cellclick', grid, gridStore.getCount()-1, 0, null);
            }
        }],
        clicksToEdit: 1
    });
    
    /*              Panel Configuration     */
    var defaultConfig = {
        id: id,
        collapsible: true,
        title:'Cohort Types',
        layout: 'form',
        //height: 200,
        autoHeight: true,
        defaults:{
            style:'font-weight: bold;'
        },
        items:[
            grid
        ],
        
        clearData: function(){
            gridStore.removeAll();
            grid.getView().refresh();
        },
        
        createSearchObject: function(){
            var result = [];
            gridStore.each(function(record){
                if (record.data.name != ""){
                        result.push({
                        name: record.data.name
                    })
                }
            });
            
            return result;
        }
    }
    
    //merge provided config with default
    if (configData){
        Ext.applyIf(configData, defaultConfig);
    }else{
        configData = defaultConfig;
    }
 
    //Methods
 
    org.socraticgrid.client.component.ProcessSearchCohortFieldSet.superclass.constructor.call(this,configData);
}

Ext.extend(org.socraticgrid.client.component.ProcessSearchCohortFieldSet,Ext.form.FieldSet,{});


/**
 * ProcessSearchServiceTaskFieldSet
 */
org.socraticgrid.client.component.ProcessSearchServiceTaskFieldSet = function(configData){
    
    var id = '_process_search_service_task_field_set' + new Date().getTime();
    
    /*  Data Grid */
    var gridId = id+'_grid';
    
    var RecordDef= Ext.data.Record.create([{
        name: 'type'
    }, {
        name: 'value'
    }]);

    var gridProxy = new Ext.data.MemoryProxy({
        root: []
    });

    var gridStore = new Ext.data.Store({
        autoDestroy: true,
        reader: new Ext.data.JsonReader({
            root: "root"
        }, RecordDef),
        proxy: gridProxy
    });
    gridStore.load();
    
    var itemDeleter = new Extensive.grid.ItemDeleter();
    
    var grid = new Ext.grid.EditorGridPanel({
        store: gridStore,
        id: gridId,
        stripeRows: true,
        width: 185,
        cm: new Ext.grid.ColumnModel([
        {
            id: 'type',
            header: 'Type',
            width: 70,
            dataIndex: 'type',
            sortable: false,
            menuDisabled: true,
            editor: new Ext.form.ComboBox({
                allowBlank: false,
                store: new Ext.data.SimpleStore({
                    fields: ['dataFieldName', 'displayFieldName'],
                    data: [['Type', 'Type'], ['Name', 'Name']],
                    autoLoad: true
                }),
                displayField: 'displayFieldName', 
                valueField: 'dataFieldName', 
                mode          : 'local',
                editable: false,
                triggerAction: 'all',
                selectOnFocus: true,
                forceSelection: true
            })
        }, {
            id: 'value',
            header: 'Value',
            width: 85,
            dataIndex: 'value',
            sortable: false,
            menuDisabled: true,
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },
        itemDeleter
        ]),
        selModel: itemDeleter,
        autoHeight: true,
        tbar: [{
            text: 'Add Filter',
            cls:'x-btn-text-icon',
            icon:'resources/png/filters.png',
            handler : function(){
                gridStore.add(new RecordDef({
                    type: 'Type',
                    value: ''
                }));
                grid.fireEvent('cellclick', grid, gridStore.getCount()-1, 0, null);
            }
        }],
        clicksToEdit: 1
    });
    
    /*              Panel Configuration     */
    var defaultConfig = {
        id: id,
        collapsible: true,
        title:'Service Tasks',
        layout: 'form',
        //height: 200,
        autoHeight: true,
        defaults:{
            style:'font-weight: bold;'
        },
        items:[
            grid
        ],
        
        clearData: function(){
            gridStore.removeAll();
            grid.getView().refresh();
        },
        
        createSearchObject: function(){
            var result = [];
            gridStore.each(function(record){
                if (record.data.type != "" && record.data.value != ""){
                    result.push({
                        type: record.data.type,
                        value: record.data.value
                    })
                }
            });
            
            return result;
        }
    }
    
    //merge provided config with default
    if (configData){
        Ext.applyIf(configData, defaultConfig);
    }else{
        configData = defaultConfig;
    }
 
    org.socraticgrid.client.component.ProcessSearchFactFieldSet.superclass.constructor.call(this,configData);
}

Ext.extend(org.socraticgrid.client.component.ProcessSearchServiceTaskFieldSet,Ext.form.FieldSet,{});