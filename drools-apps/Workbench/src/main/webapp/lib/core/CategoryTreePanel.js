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

org.socraticgrid.client.component.CategoryTreePanel = function(configData){
    
    var id = configData && configData.id != undefined?configData.id:'_category_panel_tree_' + new Date().getTime();
    
    var showAssets = configData && configData.showAssets != undefined?configData.showAssets:true;
    
    /*              Panel Configuration     */
    var defaultConfig = {
        id: id,
        defaults:{
            style:'font-weight: bold;'
        },
        autoScroll: true,
        split: true,
        root: new Ext.tree.AsyncTreeNode({
            id: '/',
            text: 'Categories',
            expanded: true,
            leaf: false,
            loader: new Ext.tree.TreeLoader({
                dataUrl: "./CategoryTreePanelServlet",
                baseParams: {
                    'action':'listNodes',
                    'showAssets': showAssets                    
                }
            })
        }),
        rootVisible: true,
        listeners:{
            click: function(node){
                if (node.isLeaf()){
                    APPLICATION.EVENTS.raiseEvent({
                        type: EVENTS.EVENT_PROCESS_SELECTED,
                        processId: node.id,
                        processName: node.text
                    });
                }
            }
        }
    }
    
    //merge provided config with default
    if (configData){
        Ext.applyIf(configData, defaultConfig);
    }else{
        configData = defaultConfig;
    }
    
    //Event Listeners
    APPLICATION.EVENTS.registerOnEvent(EVENTS.EVENT_PROCESS_CREATED, 
        function(event){
           refreshExpandedNodes();
        }.bind(this)
    );
        
    APPLICATION.EVENTS.registerOnEvent(EVENTS.EVENT_PROCESS_SAVED, 
        function(event){
           refreshExpandedNodes();
        }.bind(this)
    );
    
    //Methods
    function refreshExpandedNodes(rootNode){
        if (!rootNode){
             rootNode = Ext.getCmp(id).root;
        }

        var tree = Ext.getCmp(id);
        
        var selectedNodes = [];
        rootNode.cascade(function(node) {
            if (node.isExpanded()) {
                selectedNodes.push(node.id);
            }
        });
        
        rootNode.reload();
        
        var i = 0;
        if (selectedNodes.length > 1){
            expandNodesInOrder(tree, 0,selectedNodes)
        }
    }
    
    function expandNodesInOrder(tree, number, nodesId){
        if (number == nodesId.length){
            return;
        }
        var node = tree.getNodeById(nodesId[number]);
        if (node) {
            node.expand(false, false, function(){
                expandNodesInOrder(tree, number+1, nodesId)
            });
        }
        
    }
    
    function refreshNode(path){
        var categoryNode = Ext.getCmp(id).getNodeById(path);
        if (categoryNode && categoryNode.isExpanded()){
            categoryNode.reload();
        }
    }
    
    org.socraticgrid.client.component.CategoryTreePanel.superclass.constructor.call(this,configData);
}

Ext.extend(org.socraticgrid.client.component.CategoryTreePanel,Ext.tree.TreePanel,{});