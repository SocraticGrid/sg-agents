/**
 * *********************************************************************************************************************
 *
 * Copyright (C) 2012 by Cognitive Medical Systems, Inc
 * (http://www.cognitivemedciine.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *********************************************************************************************************************
 */
/**
 * *********************************************************************************************************************
 * Socratic Grid contains components to which third party terms apply. To comply
 * with these terms, the following notice is provided:
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION Copyright (c)
 * 2008, Nationwide Health Information Network (NHIN) Connect. All rights
 * reserved. Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the NHIN Connect Project nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * END OF TERMS AND CONDITIONS
 *
 *********************************************************************************************************************
 */
package org.socraticgrid.workbench.filter;

import org.socraticgrid.workbench.security.AuthenticationFailedException;
import org.socraticgrid.workbench.security.HttpAuthenticationProvider;
import org.socraticgrid.workbench.security.InternalAuthenticator;
import org.socraticgrid.workbench.security.wso2.WSO2AuthenticationProvider;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author esteban
 */
public class SecurityFilter implements Filter {

    private static final boolean debug = false;
    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;
    private String securityCallbackURL = null;
    private boolean enabled = true;
    private InternalAuthenticator internalAuthenticator;
    private HttpAuthenticationProvider authenticationProvider;

    public SecurityFilter() {
    }

    private void doBeforeProcessing(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("SecurityFilter:DoBeforeProcessing");
        }

        if (!this.enabled) {
            if (!internalAuthenticator.isSecureRequest(request, response)) {
                try {
                    internalAuthenticator.authenticate("test", request, response, "SAMLResponse", "TEST");
                } catch (AuthenticationFailedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return;
        }

        //is security callback -> process it
        if (request.getServletPath().equals(this.securityCallbackURL)) {
            try {
                authenticationProvider.processSecurityCallback(request, response, internalAuthenticator);
                response.sendRedirect(request.getContextPath());
                return;
            } catch (AuthenticationFailedException ex) {

                request.setAttribute("errorMessage", ex.getMessage());

                RequestDispatcher requestDispatcher = request.getRequestDispatcher("/error.jsp");
                requestDispatcher.forward(request, response);
                return;
            }
        }

        //is this secured?
        if (!internalAuthenticator.isSecureRequest(request, response)) {
            //invalid? -> do login please
            authenticationProvider.doLoginRedirect(request, response);
            return;
        }


    }

    private void doAfterProcessing(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("SecurityFilter:DoAfterProcessing");
        }


    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        if (debug) {
            log("SecurityFilter:doFilter()");
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        doBeforeProcessing(httpRequest, httpResponse);

        Throwable problem = null;

        try {
            chain.doFilter(httpRequest, httpResponse);
        } catch (Throwable t) {
            // If an exception is thrown somewhere down the filter chain,
            // we still want to execute our after processing, and then
            // rethrow the problem after that.
            problem = t;
            t.printStackTrace();
        }

        doAfterProcessing(httpRequest, httpResponse);

        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        if (problem != null) {
            if (problem instanceof ServletException) {
                throw (ServletException) problem;
            }
            if (problem instanceof IOException) {
                throw (IOException) problem;
            }
            sendProcessingError(problem, response);
        }
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {
                log("SecurityFilter: Initializing filter");
            }

            this.securityCallbackURL = filterConfig.getInitParameter("securityCallbackURL");


            String isEnabled = filterConfig.getInitParameter("enabled");
            if (isEnabled != null) {
                this.enabled = Boolean.getBoolean(isEnabled);
            }

        }

        internalAuthenticator = new InternalAuthenticator();

        if (!this.enabled) {
            return;
        }

        if (this.securityCallbackURL == null) {
            throw new IllegalStateException("securityCallbackURL was not specified!");
        }

        //authenticationProvider = new OpenSSOAuthenticationProvider();
        authenticationProvider = new WSO2AuthenticationProvider();

        authenticationProvider.configure(this.securityCallbackURL, this.getConfigurationParameters(filterConfig));
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("SecurityFilter()");
        }
        StringBuffer sb = new StringBuffer("SecurityFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());

    }

    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

    private Map<String, String> getConfigurationParameters(FilterConfig filterConfig) {
        Map config = new HashMap();
        if (filterConfig != null) {
            Enumeration initParameterNames = filterConfig.getInitParameterNames();
            while (initParameterNames.hasMoreElements()) {
                String name = (String) initParameterNames.nextElement();
                String value = filterConfig.getInitParameter(name);

                config.put(name, value);
            }
        }
        return config;
    }
}
