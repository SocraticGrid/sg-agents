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

org.socraticgrid.client.component.ProcessSearchResultPanel = function(configData){
    
    var id = '_process_search_resut_panel_' + new Date().getTime();
    var gridPagingId = id + "_grid_paging";

    var processSearchPanel = configData.processSearchPanel;
    
    var currentSearchRequest;
    
    /*  Data Grid */
    var gridId = id+'_grid';
    
    var RecordDef= Ext.data.Record.create([{
        name: 'id'
    }, {
        name: 'name'
    }, {
        name: 'packageName'
    }, {
        name: 'startEventType'
    }]);

    var gridStore = new Ext.data.JsonStore({
        url: './ProcessSearchServlet',
        remoteSort:true,
        totalProperty: "total",
        root: 'results',
        id: "id",
        fields: RecordDef
    });

    gridStore.on('loadexception', function(proxy, options, response, error){
        try{
            var object = Ext.util.JSON.decode(response.responseText);
            Ext.MessageBox.alert('Error', object.error);
        } catch (e){
            Ext.MessageBox.alert('Error', "Error getting search results: "+error);
        }

    });

    gridStore.on({
        'beforeload' : function (t,options ){
            gridStore.baseParams = {
                'searchRequest': currentSearchRequest
            };
        }
    });
    
    var gridPagingToolbar = new org.socraticgrid.client.component.CustomPagingToolbar({
        id: gridPagingId,
        store: gridStore,
        emptyMsg :'No Results!',
        pageSize:20,
        displayInfo:false, 
        displayMsg:'Showing {0} - {1} of {2}'
    });


    var grid = new Ext.grid.EditorGridPanel({
        store: gridStore,
        id: gridId,
        stripeRows: true,
        viewConfig:{
            forceFit:true
        },
        cm: new Ext.grid.ColumnModel([
        {
            id: 'name',
            header: 'Process',
            dataIndex: 'name',
            sortable: false,
            menuDisabled: true
        } 
        ]),
        autoHeight: true,
        bbar: gridPagingToolbar,
        listeners:{
            'rowdblclick':function(tbl,rowIndex,event){
                var record = tbl.getStore().getAt(rowIndex);
                
                APPLICATION.EVENTS.raiseEvent({
                    type: EVENTS.EVENT_PROCESS_SELECTED,
                    processId: record.data.id,
                    processName: record.data.name
                });
            }
        }

    });
    
    /*              Panel Configuration     */
    var defaultConfig = {
        id: id,
        autoScroll: true,        
        defaults:{
            style:'font-weight: bold;'
        },
        items: [
        grid
        ]
        
    }
    
    //merge provided config with default
    if (configData){
        Ext.applyIf(configData, defaultConfig);
    }else{
        configData = defaultConfig;
    }
    
    //Event Listeners
    APPLICATION.EVENTS.registerOnEvent(EVENTS.EVENT_PROCESS_SEARCH_PANEL_NEW_SEARCH, 
        function(event){
            if (event.source == processSearchPanel){
                currentSearchRequest = event.searchQuery;
                Ext.getCmp(gridPagingId).changePage(0);
            }
        }.bind(this) 
        );
    
    //Methods
    
    
    //Apply config data to superclass
    org.socraticgrid.client.component.ProcessSearchResultPanel.superclass.constructor.call(this,configData);
    
    
}

Ext.extend(org.socraticgrid.client.component.ProcessSearchResultPanel,Ext.Panel,{});






org.socraticgrid.client.component.CustomPagingToolbar = function(configData){
    
    var defaultConfig = {
        doLoad : function(start){
            var o = {}, pn = this.paramNames;
            o[pn.start] = start;
            o[pn.limit] = this.pageSize;
            if(this.fireEvent('beforechange', this, o) !== false){
                this.store.load({
                    params:o
                });
            }
        },


        changePage: function(page){
            this.doLoad(((page-1) * this.pageSize).constrain(0, this.store.getTotalCount()));
        }
    };
    
    //merge provided config with default
    if (configData){
        Ext.applyIf(configData, defaultConfig);
    }else{
        configData = defaultConfig;
    }
    
    org.socraticgrid.client.component.CustomPagingToolbar.superclass.constructor.call(this,configData);
}

Ext.extend(org.socraticgrid.client.component.CustomPagingToolbar,Ext.PagingToolbar,{});
