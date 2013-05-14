package org.socraticgrid.kmr;

import org.socraticgrid.kmr.knowledgemodule.KmrKnowledgeModule;
import org.socraticgrid.kmr.knowledgemodule.KmrKnowledgeModuleImpl;
import java.io.InputStream;
import java.net.URL;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class ServiceTest{
    
    private static Server server;
    
    @BeforeClass
    public static void setUp() throws Exception {
        System.out.println("Starting Server");
        KmrKnowledgeModuleImpl implementor = new KmrKnowledgeModuleImpl();
        JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
        svrFactory.setServiceClass(KmrKnowledgeModule.class);
        svrFactory.setAddress("http://localhost:9000/KmrKnowledgeModule");
        svrFactory.setServiceBean(implementor);
        svrFactory.getInInterceptors().add(new LoggingInInterceptor());
        svrFactory.getOutInterceptors().add(new LoggingOutInterceptor());
        server = svrFactory.create();
        server.start();
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        server.stop();
    }
    
    @Test
    public void testConnection() throws Exception{
        System.out.println("testing");
        
        InputStream in = new URL("http://localhost:9000/KmrKnowledgeModule?wsdl").openStream();
        
        String wsdl = IOUtils.toString(in);
        
        Assert.assertFalse(wsdl.trim().isEmpty());
        
        System.out.println(wsdl);
        
    }
    
}
