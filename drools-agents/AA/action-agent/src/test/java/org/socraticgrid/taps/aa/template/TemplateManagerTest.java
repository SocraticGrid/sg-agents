/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.socraticgrid.taps.aa.template;

import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author esteban
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:META-INF/test-templateManagerContext.xml"})
public class TemplateManagerTest {
    
    @Autowired
    private TemplateManager templateManager;
    
    @Before
    public void getContext(){
        assertNotNull(templateManager);
    }
    
    @Test
    public void basicTest(){
        
        TemplateEntry template = templateManager.getTemplate("test-template-1");
        Assert.assertNotNull(template);
        Assert.assertEquals("Test title for @{person}", template.getTitle());
        Assert.assertEquals("@{greetingMessage}", template.getHeader());
    }
    
    @Test
    public void testNonExistingTemplate(){
        
        try{
            TemplateEntry template = templateManager.getTemplate("templateXXX");
            Assert.fail("Excepton expected!");
        } catch (IllegalArgumentException ex){
            //OK
        }
    }
    
}
