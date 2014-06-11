/*******************************************************************************
 *
 * Copyright (C) 2012 by Cognitive Medical Systems, Inc (http://www.cognitivemedciine.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy of 
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ******************************************************************************/
 
 /******************************************************************************
 * Socratic Grid contains components to which third party terms apply. To comply 
 * with these terms, the following notice is provided:
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 * Copyright (c) 2008, Nationwide Health Information Network (NHIN) Connect. All 
 * rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this 
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the name of the NHIN Connect Project nor the names of its 
 *   contributors may be used to endorse or promote products derived from this 
 *   software without specific prior written permission.
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
 ******************************************************************************/
package org.drools.mas.action.helpers;

import java.util.*;
import javax.xml.ws.BindingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.dsa.DSAIntegration;
import org.socraticgrid.dsa.DSAIntegrationService;
import org.socraticgrid.dsa.DeliverMessageRequestType;
import org.socraticgrid.dsa.DeliverMessageResponseType;

/**
 *
 * @author salaboy
 */
public class DeliverMessageHelper {

    /**
     * This value can be used in META-INF/service.endpoint.properties in order
     * to use a mocked endpoint
     */
    public final static String MOCK_ENDPOINT = "MOCK";
    
    private static Logger logger = LoggerFactory.getLogger(DeliverMessageHelper.class);
    
    public static String deliverMessage(String endpoint, Map<String, Object> params) {
        
        DeliverMessageRequestType request = new DeliverMessageRequestType();
        request.setRefId((String) ((params.get("refId") != null)?params.get("refId"):"") );
        request.setPriority((String) ((params.get("priority") != null)?params.get("priority"):""));
        request.setTitle((String) ((params.get("title") != null)?params.get("title"):""));
        request.setHeader((String) ((params.get("header") != null)?params.get("header"):""));
        request.setBody((String) ((params.get("body") != null)?params.get("body"):""));
        request.setDeliveryDate((String) ((params.get("deliveryDate") != null)?params.get("deliveryDate"):""));
        request.setSender((String) ((params.get("sender") != null)?params.get("sender"):""));
        request.setSource((String) ((params.get("source") != null)?params.get("source"):""));
        request.setOriginator((String) ((params.get("originator") != null)?params.get("originator"):""));
        request.setStatus((String) ((params.get("status") != null)?params.get("status"):""));

        request.getSubject().addAll(getParameterAsList("subjectAbout", params));
        request.getMainRecipients().addAll(getParameterAsList("mainRecipients", params));
        request.getSecondaryRecipients().addAll(getParameterAsList("secondaryRecipients", params));
        request.getHiddenRecipients().addAll(getParameterAsList("hiddenRecipients", params));
        request.getType().addAll(getParameterAsList("type", params));
        
        if(logger.isInfoEnabled()){
            logger.info(" >>> DeliveryMessageHelper: Trying to Delivere Message: "+request);
        }
        
        if (endpoint == null || endpoint.equals(MOCK_ENDPOINT)) {
            String response = "OK";
            logger.debug("No endpoint provided, using MOCKED endpoint!");
            logger.info(" >>> DeliveryMessageHelper: Message Delivered to MOCKED endpoint, with response =  " + response);
            return response;
        }
        
        DSAIntegrationService port;
        try{
            port = getPort(endpoint);
        }catch(Exception e){
            logger.error(" ??? DeliveryMessageHelper: Delivering Message Failed: "+e);
            return null;
        }
        DeliverMessageResponseType response = port.deliverMessage(request);
        if(logger.isInfoEnabled()){
            logger.info(" >>> DeliveryMessageHelper: Message Delivered, with response =  " + response.getStatus());
        }
        

        return response.getStatus();

    }

    public static void sendSMS(String endpoint, String mobile, String text) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("body", text);
        params.put("header", text);
        params.put("sender", mobile);
        
        List<String> recipients = new ArrayList<String>();
        recipients.add(mobile);
        params.put("mainRecipients", recipients);
        List<String> types = new ArrayList<String>();
        types.add("SMS");
        params.put("type", types);
        if(logger.isInfoEnabled()){
            logger.info(" >>> DeliveryMessageHelper: Sending a SMS: "+mobile+ " - text: "+text);
        }
        deliverMessage(endpoint, params);
    }
    
    public static void sendEmail(String endpoint, String toEmail, String title, String body) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("body", body);
        params.put("title", body);
        
        List<String> recipients = new ArrayList<String>();
        recipients.add(toEmail);
        params.put("mainRecipients", recipients);
        
        List<String> types = new ArrayList<String>();
        types.add("EMAIL");
        params.put("type", types);
        if(logger.isInfoEnabled()){
            logger.info(" >>> DeliveryMessageHelper: Sending an EMAIL: title: '"+title+ "' - body: '"+body+"' to email: '"+toEmail+"'");
        }
        deliverMessage(endpoint, params);
    }
    
    public static void sendMobile(String endpoint, String sender, String subject, String text) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("body", text);
        params.put("header", text);
        params.put("sender", sender);
        
        List<String> recipients = new ArrayList<String>();
        recipients.add(subject);
        params.put("mainRecipients", recipients);
        List<String> types = new ArrayList<String>();
        types.add("MOBILE");
        params.put("type", types);
        if(logger.isInfoEnabled()){
            logger.info(" >>> DeliveryMessageHelper: Sending a MOBILE ALERT: "+subject+ " - text: "+text);
        }
        deliverMessage(endpoint, params);
    }
    
    public static void sendAlert(String endpoint, String refId, String title, String header, String body, String sender, String recipient, String[] subjectAbout, String source, String originator ) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("refId", refId);
        params.put("title", title);
        params.put("header", header);
        params.put("body", body);
        params.put("sender", sender);
        params.put("source", source);
        params.put("originator", originator);
        params.put("priority", "HIGH");
        params.put("deliveryDate", new Date().toString());
        
        List<String> recipients = new ArrayList<String>();
        recipients.add(recipient);
        params.put("mainRecipients", recipients);
        params.put("subjectAbout", subjectAbout);
        List<String> types = new ArrayList<String>();
        types.add("ALERT");
        params.put("type", types);
        if(logger.isInfoEnabled()){
            logger.info(" >>> DeliveryMessageHelper: Sending a new Alert ("+sender+"): "+header+ " - body: "+body + " --");
        }
        deliverMessage(endpoint, params);
    }
    
    private static Collection getParameterAsList(String paramName, Map<String, Object> params){
        Object paramValue = params.get(paramName);
        if (paramValue == null){
            return Collections.EMPTY_LIST;
        }
        
        if (paramValue instanceof Collection){
            return (Collection) paramValue;
        }
        
        List result = new ArrayList();
        
        if (paramValue instanceof String[]){
            for (int i = 0; i < ((String[])paramValue).length; i++) {
                result.add(((String[])paramValue)[i]);
            }
            return result;
        }

        
        result.add(paramValue);
        return result;
    }
    
   
    private static DSAIntegrationService getPort(String endpoint) {
        DSAIntegration service = new DSAIntegration();
        DSAIntegrationService port = service.getDSAIntegrationPortSoap11();
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                endpoint);
        return port;
    }
}
