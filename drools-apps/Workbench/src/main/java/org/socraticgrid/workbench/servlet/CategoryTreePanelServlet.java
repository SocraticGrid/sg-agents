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
package org.socraticgrid.workbench.servlet;

import com.google.gson.Gson;
import org.socraticgrid.workbench.model.ext.TreeNode;
import org.socraticgrid.workbench.model.guvnor.Asset;
import org.socraticgrid.workbench.model.guvnor.Category;
import org.socraticgrid.workbench.service.GuvnorService;
import org.socraticgrid.workbench.setting.ApplicationSettings;
import org.socraticgrid.workbench.setting.UserSettings;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author esteban
 */
public class CategoryTreePanelServlet extends HttpServlet {

    private Comparator treeNodeComparator = new Comparator<TreeNode>() {

        public int compare(TreeNode o1, TreeNode o2) {
            return o1.getText().compareTo(o2.getText());
        }
    };
    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String action = request.getParameter("action");

            if ("listNodes".equals(action)) {
                UserSettings userSettings = HttpSessionObjectManager.getUserSettings(request.getSession(true));
                String pkg = userSettings.getSetting(ApplicationSettings.APPLICATION_SETTINGS.PACKAGE);
                
                String categoryPath = request.getParameter("node");
                
                String showAssets = request.getParameter("showAssets");
                
                GuvnorService guvnorService = new GuvnorService(userSettings);
                
                //get category's children
                List<org.socraticgrid.workbench.model.guvnor.Category> categories = guvnorService.getCategoryChildren(categoryPath);
                List<TreeNode> childrenCategories = convertToTreeNodes(categories);
                
                //get category's assets
                List<TreeNode> childrenAssets = new ArrayList<TreeNode>();
                
                if (showAssets.equalsIgnoreCase("true")){
                    List<Asset> categoryAssets = guvnorService.getCategoryAssets(categoryPath, pkg);
                    for (Asset asset : categoryAssets) {
                        TreeNode treeNode = new TreeNode();
                        treeNode.setId(asset.getMetadata().getUuid());
                        treeNode.setText(asset.getMetadata().getTitle());
                        treeNode.setIconCls("processTreeNode");
                        childrenAssets.add(treeNode);
                    }
                }
                
                //sort children
                Collections.sort(childrenCategories, treeNodeComparator );
                Collections.sort(childrenAssets, treeNodeComparator);
                
                //add children to unique list
                List<TreeNode> children = new ArrayList<TreeNode>();
                children.addAll(childrenCategories);
                children.addAll(childrenAssets);
                
                //serialize the list
                Gson gson = new Gson();
                out.write(gson.toJson(children));

            } 
        } finally {
            out.close();
        }
    }
    
    private List<TreeNode> convertToTreeNodes(List<org.socraticgrid.workbench.model.guvnor.Category> categories){
        if (categories == null || categories.isEmpty()){
            return new ArrayList<TreeNode>();
        }
        
        List<TreeNode> nodes = new ArrayList<TreeNode>();
        for (Category category : categories) {
            TreeNode node = new TreeNode();
            node.setText(category.getName());
            node.setId(category.getPath());
            //node.setChildren(this.convertToTreeNodes(category.getChildren()));
            node.setLeaf(false);
            nodes.add(node);
        }
        
        return nodes;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
