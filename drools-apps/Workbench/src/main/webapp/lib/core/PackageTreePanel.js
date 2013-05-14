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

org.socraticgrid.client.component.PackageTreePanel = function(configData){
    
    /*              Panel Configuration     */
    var defaultConfig = {
        //TODO: make this id dynamic
        id: '_package_tree_panel_',
        defaults:{
            style:'font-weight: bold;'
        },
        autoScroll: true,
        split: true,
        root: new Ext.tree.AsyncTreeNode({
            id: '_package_tree_panel_root_node_',
            text: 'Packages',
            expanded: true,
            leaf: false,
            loader: new Ext.tree.TreeLoader({
                dataUrl: "./PackageTreePanelServlet",
                baseParams: {'action':'listNodes'}
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
    
    //event listeners
    APPLICATION.EVENTS.registerOnEvent(EVENTS.EVENT_PROCESS_CREATED, 
        function(event){
            refreshPackageNode(APPLICATION.CONTEXT_INFO.getCurrentPackage())
        }.bind(this) 
    );
    
    //Methods
    function refreshPackageNode(packageId){
        var packageNode = Ext.getCmp('_package_tree_panel_').getNodeById('_package_tree_panel_pkg_'+packageId);
        if (packageNode && packageNode.isExpanded()){
            packageNode.reload();
        }
    }
    
    //merge provided config with default
    if (configData){
        Ext.applyIf(configData, defaultConfig);
    }else{
        configData = defaultConfig;
    }
    
    org.socraticgrid.client.component.PackageTreePanel.superclass.constructor.call(this,configData);
}

Ext.extend(org.socraticgrid.client.component.PackageTreePanel,Ext.tree.TreePanel,{});