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
package org.socraticgrid.workbench.security.wso2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import org.apache.commons.httpclient.HttpClient;

import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.impl.*;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.security.MetadataCredentialResolver;
import org.opensaml.security.MetadataCriteria;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.criteria.UsageCriteria;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.signature.SignatureTrustEngine;
import org.opensaml.xml.signature.impl.ExplicitKeySignatureTrustEngine;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.opensaml.xml.validation.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("restriction")
public class SamlConsumer {

    /*
     * @auther suresh@wso2.com
     *
     * @This calls basically does two things: generating the saml2 AuthnRequest
     * object and processing the saml2 Response object which is sent by the
     * identity provider.
     */
    // The url of the identity provider
    String redirectionUrl = null;
    // The url of the service provider which send the AuthnRequest object
    String issuerUrl = null;
    // Identity provider sends the Response object to this url
    String consumerUrl = null;
    // ID of the AuthRequest object
    String randomId = Integer.toHexString(new Double(Math.random()).intValue());
    // The redirectUrl with the message
    String compUrl = null;
    String relayState = null;

    public SamlConsumer(String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
    }

    public String buildRequestMessage(String pIssuerUrl, String pUrl) {
        // AuthnRequest message creation
        AuthnRequest authnRequest;
        String encodedRequestMessage = null;
        issuerUrl = pIssuerUrl;
        consumerUrl = pUrl;

        try {
            DefaultBootstrap.bootstrap();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (FactoryConfigurationError exc) {
            exc.printStackTrace();
        }

        authnRequest = this.buildAuthnRequestObject();
        // Encoding the object
        try {
            encodedRequestMessage = encodeAuthnRequest(authnRequest);
        } catch (MarshallingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // The complete URL
        compUrl = redirectionUrl + "?SAMLRequest=" + encodedRequestMessage
                + "&RelayState=" + relayState;
        return compUrl;
    }

    private AuthnRequest buildAuthnRequestObject() {
        // AuthnRequest object creation

        // Issuer object
        IssuerBuilder issuerBuilder = new IssuerBuilder();
        Issuer issuer = issuerBuilder.buildObject(
                "urn:oasis:names:tc:SAML:2.0:assertion", "Issuer", "samlp");
        issuer.setValue(issuerUrl);

        // NameIDPolicy
        NameIDPolicyBuilder nameIdPolicyBuilder = new NameIDPolicyBuilder();
        NameIDPolicy nameIdPolicy = nameIdPolicyBuilder.buildObject();
        nameIdPolicy.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
        nameIdPolicy.setSPNameQualifier("Issuer");
        nameIdPolicy.setAllowCreate(true);

        // AuthnContextClass
        AuthnContextClassRefBuilder authnContextClassRefBuilder = new AuthnContextClassRefBuilder();
        AuthnContextClassRef authnContextClassRef = authnContextClassRefBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion",
                "AuthnContextClassRef", "saml");
        authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");

        // AuthnContex
        RequestedAuthnContextBuilder requestedAuthnContextBuilder = new RequestedAuthnContextBuilder();
        RequestedAuthnContext requestedAuthnContext = requestedAuthnContextBuilder.buildObject();
        requestedAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.EXACT);
        requestedAuthnContext.getAuthnContextClassRefs().add(
                authnContextClassRef);

        // Creation of AuthRequestObject
        DateTime issueInstant = new DateTime();
        AuthnRequestBuilder authRequestBuilder = new AuthnRequestBuilder();
        AuthnRequest authRequest = authRequestBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:protocol",
                "AuthnRequest", "samlp");
        authRequest.setForceAuthn(false);
        authRequest.setIsPassive(false);
        authRequest.setIssueInstant(issueInstant);
        authRequest.setProtocolBinding("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
        authRequest.setAssertionConsumerServiceURL(consumerUrl);
        authRequest.setIssuer(issuer);
        authRequest.setNameIDPolicy(nameIdPolicy);
        authRequest.setRequestedAuthnContext(requestedAuthnContext);
        authRequest.setID(randomId);
        authRequest.setVersion(SAMLVersion.VERSION_20);
        return authRequest;

    }

    @SuppressWarnings("deprecation")
    private String encodeAuthnRequest(AuthnRequest authnRequest)
            throws MarshallingException, IOException {
        String requestMessage;
        // Pass authnRequest object to a DOM element
        Marshaller marshaller = org.opensaml.Configuration.getMarshallerFactory().getMarshaller(authnRequest);
        org.w3c.dom.Element authDOM = null;

        authDOM = marshaller.marshall(authnRequest);

        // Get the string
        StringWriter rspWrt = new StringWriter();
        XMLHelper.writeNode(authDOM, rspWrt);
        requestMessage = rspWrt.toString();

        // DEFLATE compression of the message, byteArrayOutputStream will holds
        // the compressed bytes
        Deflater deflater = new Deflater(Deflater.DEFLATED, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(
                byteArrayOutputStream, deflater);

        deflaterOutputStream.write(requestMessage.getBytes());
        deflaterOutputStream.close();

        // Encoding the compressed message
        String encodedRequestMessage = Base64.encodeBytes(byteArrayOutputStream.toByteArray(), Base64.DONT_BREAK_LINES);
        encodedRequestMessage = URLEncoder.encode(encodedRequestMessage).trim();

        return encodedRequestMessage;
    }

    public String processResponseMessage(String responseMessage) {
        // Process and retrieve results of the ResponseMessage
        XMLObject responseObject = null;
        try {
            responseObject = this.unmarshall(responseMessage);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnmarshallingException e) {
            e.printStackTrace();
        }
        
//        try {
//            this.foo(responseObject);
//        } catch (org.opensaml.xml.security.SecurityException ex) {
//            ex.printStackTrace();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        
        return this.getResult(responseObject);
    }

    private XMLObject unmarshall(String responseMessage)
            throws ConfigurationException, ParserConfigurationException,
            SAXException, IOException, UnmarshallingException {
        // Create a XMLObject using unmarshaller 

        DefaultBootstrap.bootstrap();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        documentBuilderFactory.setNamespaceAware(true);

        DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();

        Document document = docBuilder.parse(new ByteArrayInputStream(
                responseMessage.trim().getBytes()));

        Element element = document.getDocumentElement();
        
        UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);

        return unmarshaller.unmarshall(element);

    }

    private String getResult(XMLObject responseObject) {
        // Retrieve results by converting the response to DOM
        Element ele = responseObject.getDOM();
        NodeList statusNodeList = ele.getElementsByTagName("samlp:StatusCode");
        Node statusNode = statusNodeList.item(0);
        NamedNodeMap statusAttr = statusNode.getAttributes();
        Node valueAtt = statusAttr.item(0);
        String statusValue = valueAtt.getNodeValue();
        String[] word = statusValue.split(":");
        String result = word[word.length - 1];

        NodeList nameIDNodeList = ele.getElementsByTagNameNS(
                "urn:oasis:names:tc:SAML:2.0:assertion", "NameID");
        Node nameIDNode = nameIDNodeList.item(0);
        String nameID = nameIDNode.getFirstChild().getNodeValue();

        result = nameID + ":" + result;
        return result;
    }

    private void foo(XMLObject element) throws org.opensaml.xml.security.SecurityException, Exception {
        //One-time init code here...
        MetadataProvider mdProvider = new HTTPMetadataProvider(new Timer(), new HttpClient(), "http://google.com");
        MetadataCredentialResolver mdCredResolver = new MetadataCredentialResolver(mdProvider);
        KeyInfoCredentialResolver keyInfoCredResolver =
                Configuration.getGlobalSecurityConfiguration().getDefaultKeyInfoCredentialResolver();
        ExplicitKeySignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(mdCredResolver, keyInfoCredResolver);
        //storeSignatureTrustEngine(trustEngine);

        // Individual message handling code here..
        Response response = (Response) element;

        SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();
        try {
            profileValidator.validate(response.getSignature());
        } catch (ValidationException e) {
            // Indicates signature did not conform to SAML Signature profile
            e.printStackTrace();
        }

        SignatureTrustEngine sigTrustEngine = trustEngine;
        CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.add(new EntityIDCriteria(response.getIssuer().getValue()));
        criteriaSet.add(new MetadataCriteria(IDPSSODescriptor.DEFAULT_ELEMENT_NAME, SAMLConstants.SAML20P_NS));
        criteriaSet.add(new UsageCriteria(UsageType.SIGNING));

        try {
            if (!sigTrustEngine.validate(response.getSignature(), criteriaSet)) {
                throw new Exception("Signature was either invalid or signing key could not be established as trusted");
            }
        } catch (SecurityException e) {
            // Indicates processing error evaluating the signature
            e.printStackTrace();
        }
    }
}
