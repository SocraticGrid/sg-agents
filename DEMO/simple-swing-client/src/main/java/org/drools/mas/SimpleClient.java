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
package org.drools.mas;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.IOUtils;
import org.drools.mas.body.acts.InformIf;
import org.drools.mas.event.EventDispatcher;
import org.drools.mas.event.EventListener;
import org.drools.mas.facteditor.FactEditorPanel;
import org.drools.mas.facteditor.event.FactEditorPanelCloseEvent;
import org.drools.mas.facteditor.event.FactModifiedEvent;
import org.drools.mas.facteditor.event.NewFactCreatedEvent;
import org.drools.mas.helpers.DialogueHelper;
import org.drools.mas.helpers.DialogueHelperCallbackImpl;
import org.drools.mas.objectserializer.ObjectSerializerFactory;
import org.drools.mas.util.MessageContentEncoder;

/**
 *
 * @author salaboy
 */
public class SimpleClient extends javax.swing.JFrame {

    private String endpoint = "http://192.168.1.90:8083/action-agent/services/AsyncAgentService?wsdl";
    private DialogueHelper helper;
    //private FactTableModel factTableModel = new FactTableModel();
    private FactTableModel factTableModel = new MockFactTableModel();
    private JDialog factEditorPanelDialog;
    private FactEditorPanel factEditorPanel;
    private LogHelper logHelper;

    /**
     * Creates new form SimpleClient
     */
    public SimpleClient() {
        Logger.getLogger(SimpleClient.class.getName()).log(Level.INFO, "Iitializing components");
        initComponents();
        this.logHelper = new LogHelper(notificationTextArea);
        factsjTable.setModel(factTableModel);

        String predefinedEndpoint = System.getProperty("agent.endpoint");
        if (predefinedEndpoint != null){
            //if there is a predefined endpoint, use it!
            this.endpoint = predefinedEndpoint;
            this.helper = new DialogueHelper(endpoint);
        }
        
        this.endpointTextField.setText(endpoint);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        jMenuItem1 = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        messageIdTextField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        informButton = new javax.swing.JButton();
        disconfirmButton = new javax.swing.JButton();
        informButton2 = new javax.swing.JButton();
        addFactButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        factsjTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        notificationTextArea = new javax.swing.JTextArea();
        lastMessageIdTextField = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        endpointTextField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        menuSaveAs = new javax.swing.JMenuItem();

        fileChooser.setDialogTitle("Choose an Ontology File (OWL)");

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("AAD Client V1.3");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Interaction Status"));

        jLabel3.setText("Last MessageId: ");

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(messageIdTextField)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(messageIdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        informButton.setText("Assert");
        informButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                informButtonActionPerformed(evt);
            }
        });

        disconfirmButton.setText("Retract");
        disconfirmButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconfirmButtonActionPerformed(evt);
            }
        });

        informButton2.setText("Refresh Facts");
        informButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                informButton2ActionPerformed(evt);
            }
        });

        addFactButton.setText("Add Fact");
        addFactButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFactButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(6, 6, 6)
                .add(informButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(disconfirmButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(addFactButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(informButton2)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(addFactButton)
                    .add(informButton2)
                    .add(disconfirmButton)
                    .add(informButton))
                .add(0, 4, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Facts"));

        factsjTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        factsjTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                factsjTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(factsjTable);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Interaction", jPanel5);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Agent Answers"));

        jLabel4.setText("Get Answers for MessageId:");

        jButton5.setText("Get Answers!");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        notificationTextArea.setColumns(20);
        notificationTextArea.setRows(5);
        jScrollPane1.setViewportView(notificationTextArea);

        jButton6.setText("Clear");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lastMessageIdTextField))
                    .add(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 675, Short.MAX_VALUE)))
                .addContainerGap())
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jButton5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton6)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(lastMessageIdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton5)
                    .add(jButton6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Debug", jPanel2);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Configuration"));

        jLabel1.setText("Endpoint:");

        endpointTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endpointTextFieldActionPerformed(evt);
            }
        });

        jButton1.setText("Apply");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(17, 17, 17)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(endpointTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButton1)
                .add(24, 24, 24))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(67, 67, 67)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(endpointTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1))
                .addContainerGap(530, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Configuration", jPanel3);

        jMenu1.setMnemonic('f');
        jMenu1.setText("File");

        jMenuItem2.setMnemonic('o');
        jMenuItem2.setText("Open...");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        menuSaveAs.setMnemonic('s');
        menuSaveAs.setText("Save As...");
        menuSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveAsActionPerformed(evt);
            }
        });
        jMenu1.add(menuSaveAs);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        this.endpoint = endpointTextField.getText();
        if (this.endpoint != null && !this.endpoint.equals("")) {
            this.helper = new DialogueHelper(endpoint);
        } else {
            this.logHelper.log(" ### ERROR: helper cannot be created with endpoint: " + this.endpoint);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void informButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_informButtonActionPerformed
        // TODO add your handling code here:
        if (this.helper != null) {
            final int selectedRow = factsjTable.getSelectedRow();
            if (!((FactTableModel) factsjTable.getModel()).isInformed(selectedRow)) {
                Object fact = ((FactTableModel) factsjTable.getModel()).getFacts().get(selectedRow);
                Logger.getLogger(SimpleClient.class.getName()).log(Level.INFO, " >>> INFORMING FACT: {0}",fact);
                String messageId = helper.invokeInform("", "", fact, new DialogueHelperCallbackImpl(){

                    @Override
                    public int getExpectedResponsesNumber() {
                        return 0;
                    }
                    
                    @Override
                    public void onSuccess(List<ACLMessage> messages) {
                        syncFact(selectedRow);
                    }

                    @Override
                    public void onError(Throwable t) {
                        logHelper.log(" ### ERROR: Error informing Fact:"+t.getMessage());
                        Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, " Error Informing Fact",t);
                    }
                    
                });
                this.messageIdTextField.setText(messageId);
                this.lastMessageIdTextField.setText(messageId);
            } else {
                this.logHelper.log(" ### WARN: this fact was already informed, not informing again");
            }

        } else {
            this.logHelper.log(" ### ERROR: DialogueHelper null check the endpoint: " + this.endpoint);
        }
    }//GEN-LAST:event_informButtonActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        String text = this.lastMessageIdTextField.getText();
        if (this.helper != null) {
            List<ACLMessage> agentAnswers = this.helper.getAgentAnswers(text);
            for (ACLMessage msg : agentAnswers) {
                this.logHelper.log(" >>> " + msg);
            }
        } else {
            this.logHelper.log(" ### ERROR: No Answers Could Be retreived check the endpoint: " + this.endpoint);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        this.logHelper.clear();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void disconfirmButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconfirmButtonActionPerformed
        if (this.helper != null) {
            final int selectedRow = factsjTable.getSelectedRow();
            if (((FactTableModel) factsjTable.getModel()).isInformed(selectedRow)) {
                Object fact = ((FactTableModel) factsjTable.getModel()).getFacts().get(selectedRow);
                Logger.getLogger(SimpleClient.class.getName()).log(Level.INFO, " >>> DISCONFIRM FACT: {0}",fact);
                String messageId = helper.invokeDisconfirm("", "", fact, new DialogueHelperCallbackImpl(){

                    @Override
                    public int getExpectedResponsesNumber() {
                        return 0;
                    }
                    
                    @Override
                    public void onSuccess(List<ACLMessage> messages) {
                        syncFact(selectedRow);
                    }

                    @Override
                    public void onError(Throwable t) {
                        logHelper.log(" ### ERROR: Error disconfirming Fact:"+t.getMessage());
                        Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, " Error Disconfirming Fact",t);
                    }
                    
                });
                this.messageIdTextField.setText(messageId);
                this.lastMessageIdTextField.setText(messageId);
            } else {
                this.logHelper.log(" ### WARN: this fact was already disconfirmed, not disconfirming again");
            }

        } else {
            this.logHelper.log(" ### ERROR: DialogueHelper null check the endpoint: " + this.endpoint);
        }
    }//GEN-LAST:event_disconfirmButtonActionPerformed

    private void informButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_informButton2ActionPerformed

        int[] selectedRows = this.factsjTable.getSelectedRows();

        if (selectedRows.length == 0) {
            logHelper.log(" ### INFO: No Facts selected");
            return;
        }

        //synchronize the existing facts
        for (int i = 0; i < selectedRows.length; i++) {
            this.syncFact(selectedRows[i]);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_informButton2ActionPerformed

    private void syncFact(final int index) {
        final Object fact = this.factTableModel.getFacts().get(index);

        logHelper.log(" ### INFO: Synching fact " + fact);

        this.helper.invokeQueryIf("", "", fact, new DialogueHelperCallbackImpl() {
            @Override
            public int getExpectedResponsesNumber() {
                //QueryIf expects only 1 response
                return 1;
            }

            @Override
            public long getTimeoutForResponses() {
                return 4000;
            }

            @Override
            public long getMinimumWaitTimeForResponses() {
                return 200;
            }

            @Override
            public void onSuccess(List<ACLMessage> messages) {
                try {
                    if (messages.size() > 0) {
                        ACLMessage response = messages.get(0);
                        MessageContentEncoder.decodeBody(response.getBody(), response.getEncoding());
                        if (((InformIf) response.getBody()).getProposition().getData().equals(fact)) {
                            factTableModel.informFact(index);
                            return;
                        }
                    } else {
                        logHelper.log(" ### INFO: " + fact + " is not asserted in the agent");
                    }

                    factTableModel.disconfirmFact(index);
                } finally {
                    factTableModel.fireTableDataChanged();
                }

            }

            @Override
            public void onError(Throwable t) {
                logHelper.log(" ### ERROR: DialogueHelper Error in Response: " + t.getMessage());
                Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, "Error synchronizing fact " + fact, t);
                factTableModel.disconfirmFact(index);
                factTableModel.fireTableDataChanged();
            }
        });
    }

    private void addFactButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFactButtonActionPerformed
        this.openFactEditorPanel();

    }//GEN-LAST:event_addFactButtonActionPerformed

    private void menuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveAsActionPerformed
        JFileChooser saveFileChooser = new JFileChooser();
        saveFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Model File", "mod"));
        int showDialog = saveFileChooser.showDialog(this, "Save");
        if (showDialog == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = saveFileChooser.getSelectedFile();
                if (selectedFile.exists()) {
                    int showConfirmDialog = JOptionPane.showConfirmDialog(this, "The file '" + selectedFile.getCanonicalPath() + "' already exstits. Do you want to continue?");
                    if (showConfirmDialog != JOptionPane.OK_OPTION) {
                        return;
                    }
                }

                if (!selectedFile.getName().endsWith(".mod")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".mod");
                }

                String content = ObjectSerializerFactory.getObjectSerializerInstance().serialize(this.factTableModel.getFacts());
                IOUtils.copy(new ByteArrayInputStream(content.getBytes()), new FileOutputStream(selectedFile));
                JOptionPane.showMessageDialog(this, "File Saved!");

                this.setTitle(selectedFile.getName());

            } catch (Exception ex) {
                Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_menuSaveAsActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        JFileChooser openFileChooser = new JFileChooser();
        openFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Model File", "mod"));

        int showDialog = openFileChooser.showDialog(this, "Open");
        if (showDialog == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = openFileChooser.getSelectedFile();
                if (!selectedFile.exists()) {
                    JOptionPane.showMessageDialog(this, "File: " + selectedFile.getCanonicalPath(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String toString = IOUtils.toString(new FileInputStream(selectedFile));

                List facts = (List) ObjectSerializerFactory.getObjectSerializerInstance().deserialize(toString);

                this.factTableModel.clear();
                for (Object fact : facts) {
                    this.factTableModel.addFact(fact);
                }

                this.factTableModel.fireTableDataChanged();

                this.setTitle(selectedFile.getName());
            } catch (Exception ex) {
                Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void factsjTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_factsjTableMouseClicked
        if (evt.getClickCount() > 1) {
            int i = factsjTable.getSelectedRow();
            Object fact = factTableModel.getFacts().get(i);
            this.openFactEditorPanelForEdition(fact);
        }
    }//GEN-LAST:event_factsjTableMouseClicked

    private void endpointTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endpointTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_endpointTextFieldActionPerformed

    private void openFactEditorPanel() {
        this.openFactEditorPanelForEdition(null);
    }

    private void openFactEditorPanelForEdition(Object fact) {
        int pad = 100;
        if (this.factEditorPanel == null) {
            this.factEditorPanel = new FactEditorPanel();
            this.factEditorPanel.setBounds(0, 0, this.getWidth() - pad, this.getHeight() - pad);
            factEditorPanelDialog = new JDialog(this, "Fact Editor");
            factEditorPanelDialog.setSize(this.factEditorPanel.getWidth(), this.factEditorPanel.getHeight());
            factEditorPanelDialog.setLocationRelativeTo(null);
            factEditorPanelDialog.add(this.factEditorPanel);

            EventDispatcher.getInstance().registerEventListener(new EventListener<NewFactCreatedEvent>() {
                public void onEvent(NewFactCreatedEvent event) {
                    SimpleClient.this.factTableModel.addFact(event.getFact());
                    SimpleClient.this.factTableModel.fireTableDataChanged();
                    factEditorPanelDialog.setVisible(false);
                }
            });

            EventDispatcher.getInstance().registerEventListener(new EventListener<FactModifiedEvent>() {
                public void onEvent(FactModifiedEvent event) {
                    SimpleClient.this.factTableModel.replaceFact(event.getOriginalFact(), event.getNewFact());
                    SimpleClient.this.factTableModel.fireTableDataChanged();
                    factEditorPanelDialog.setVisible(false);
                }
            });

            EventDispatcher.getInstance().registerEventListener(new EventListener<FactEditorPanelCloseEvent>() {
                public void onEvent(FactEditorPanelCloseEvent event) {
                    factEditorPanelDialog.setVisible(false);
                }
            });

        }
        factEditorPanel.reset();
        if (fact != null) {
            factEditorPanel.editObject(fact);
        }
        factEditorPanelDialog.setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SimpleClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SimpleClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SimpleClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SimpleClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SimpleClient().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFactButton;
    private javax.swing.JButton disconfirmButton;
    private javax.swing.JTextField endpointTextField;
    private javax.swing.JTable factsjTable;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JButton informButton;
    private javax.swing.JButton informButton2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField lastMessageIdTextField;
    private javax.swing.JMenuItem menuSaveAs;
    private javax.swing.JTextField messageIdTextField;
    private javax.swing.JTextArea notificationTextArea;
    // End of variables declaration//GEN-END:variables
}
