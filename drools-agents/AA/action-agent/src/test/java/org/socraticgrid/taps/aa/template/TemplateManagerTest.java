/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.socraticgrid.taps.aa.template;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class TemplateManagerTest {
    
    @Test
    public void basicTest(){
        
        TemplateManager templateManager = TemplateManager.getInstance();
        
        TemplateEntry template = templateManager.getTemplate("template1");
        Assert.assertNotNull(template);
        Assert.assertEquals("Recommendation 1", template.getHeader());
    }
    
    @Test
    public void testNonExistingTemplate(){
        
        TemplateManager templateManager = TemplateManager.getInstance();
        
        try{
            TemplateEntry template = templateManager.getTemplate("templateXXX");
            Assert.fail("Excepton expected!");
        } catch (IllegalArgumentException ex){
            //OK
        }
    }
    
}
