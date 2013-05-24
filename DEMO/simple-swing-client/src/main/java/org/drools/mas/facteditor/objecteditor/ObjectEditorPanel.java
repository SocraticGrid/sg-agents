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
package org.drools.mas.facteditor.objecteditor;

import org.drools.mas.objectserializer.ObjectSerializerFactory;

/**
 *
 * @author esteban
 */
public class ObjectEditorPanel extends javax.swing.JPanel {

    private Class currentClass;
    /**
     * Creates new form ObjectEditorPanel
     */
    public ObjectEditorPanel() {
        initComponents();
    }
    
    public void reset(){
        this.currentClass = null;
        this.txtObject.setText("");
    }
    
    public void showEmptyObject(String fqn) throws Exception{
        currentClass = Class.forName(fqn);
        Object newInstance = currentClass.newInstance();
        
        /*
        newInstance = new Fact();
        ((Fact)newInstance).setIntProperty(123);
        ((Fact)newInstance).setStringProperty("hola");
        
        Fact f1 = new Fact();
        f1.setIntProperty(456);
        f1.setStringProperty("chau");
        
        Fact f2 = new Fact();
        f2.setIntProperty(789);
        f2.setStringProperty("hello");
        
        List<Fact> facts = new ArrayList<Fact>();
        facts.add(f2);
        
        f1.setListProperty(facts);
        
        List<Fact> facts2 = new ArrayList<Fact>();
        facts2.add(f1);
        facts2.add(f2);
        ((Fact)newInstance).setListProperty(facts2);
        */
        
        String dump = ObjectSerializerFactory.getObjectSerializerInstance().serialize(newInstance);
        
        this.txtObject.setText(dump);
    }
    
    public Object getCurrentObject(){
        return ObjectSerializerFactory.getObjectSerializerInstance().deserialize(this.txtObject.getText());
    }
    
    public void setCurrentObject(Object obj){
        this.txtObject.setText(ObjectSerializerFactory.getObjectSerializerInstance().serialize(obj));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtObject = new javax.swing.JTextArea();

        txtObject.setColumns(20);
        txtObject.setRows(5);
        jScrollPane1.setViewportView(txtObject);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtObject;
    // End of variables declaration//GEN-END:variables
}