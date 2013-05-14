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

import org.socraticgrid.workbench.model.guvnor.Asset;
import org.socraticgrid.workbench.model.guvnor.Package;
import org.socraticgrid.workbench.service.GuvnorService;
import org.socraticgrid.workbench.setting.ApplicationSettings;
import org.socraticgrid.workbench.setting.UserSettings;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author esteban
 */
public class UserSettingsServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            
            String init = request.getParameter("init");
            
            if (init != null){
                this.initSettings(request);
                out.write("{success:true}");
                return;
            }
            
            String[] keys = request.getParameterValues("key");
            
            if (keys == null || keys.length == 0){
                throw new IllegalArgumentException("No key was specified!");
            }
            
            if (keys.length == 1){
                this.processSimpleSettingRequest(keys[0], out, request.getSession(true));
            }else{
                this.processMultipleSettingsRequest(keys, out, request.getSession(true));
            }
            
            
            
        } catch (Exception ex){
            ex.printStackTrace();
            out.write("{success:false, error: \"Unable to retrieve Property\", detail: \""+ex.getMessage()+"\" }");
        } finally {            
            out.close();
        }
    }
    
    /**
     * Instantiates all the default User Settings.
     */
    private void initSettings(HttpServletRequest request){
        
        HttpSession session = request.getSession(true);
        
        UserSettings userSettings = HttpSessionObjectManager.getUserSettings(request.getSession(true));
        GuvnorService guvnorService = new GuvnorService(userSettings);
        
        
        //load package uuid
        String pkgName = userSettings.getSetting(ApplicationSettings.APPLICATION_SETTINGS.PACKAGE);
        Package pkg = guvnorService.getPackage(pkgName);
        
        //load working-set uuid
        String wsName = userSettings.getSetting(ApplicationSettings.APPLICATION_SETTINGS.WORKING_SET);
        Asset ws = guvnorService.getAsset(pkgName, wsName);
        
        
        //update user settings
        userSettings.setSetting(UserSettings.USER_SETTINGS.PACKAGE_UUID, pkg.getMetadata().getUuid());
        userSettings.setSetting(UserSettings.USER_SETTINGS.WORKING_SET_UUID, ws.getMetadata().getUuid());
    }

    private void processSimpleSettingRequest(String key, PrintWriter out, HttpSession session){
        UserSettings userSettings = HttpSessionObjectManager.getUserSettings(session);
        String value = userSettings.getSetting(key);

        out.write("{'key':'"+key+"', 'value':'"+value+"'}");
    }
    
    private void processMultipleSettingsRequest(String[] keys, PrintWriter out, HttpSession session){
        String separator = "";
        
        out.write("[");
        for (String key : keys) {
            out.write(separator);
            this.processSimpleSettingRequest(key, out, session);
            if (separator.equals("")){
                separator = ",";
            }
        }
        out.write("]");
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
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
     * Handles the HTTP <code>POST</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
