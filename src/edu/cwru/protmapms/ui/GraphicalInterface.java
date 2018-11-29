/*

 Copyright (C) Case Western Reserve University, 2014. All rights reserved. 
 This source code and documentation constitute proprietary information
 belonging to Case Western Reserve University. None of the foregoing
 material may be copied, duplicated or disclosed without the express
 written permission of Case Western Reserve University.

 CASE WESTERN RESERVE UNIVERSITY EXPRESSLY DISCLAIMS ANY
 AND ALL WARRANTIES CONCERNING THIS SOURCE CODE AND DOCUMENTATION,
 INCLUDING ANY WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 FOR ANY PARTICULAR PURPOSE, AND WARRANTIES OF PERFORMANCE,
 AND ANY WARRANTY THAT MIGHT OTHERWISE ARISE FROM COURSE OF
 DEALING OR USAGE OF TRADE. NO WARRANTY IS EITHER EXPRESS OR
 IMPLIED WITH RESPECT TO THE USE OF THE SOFTWARE OR
 DOCUMENTATION.
 
 Under no circumstances shall University be liable for incidental, special,
 indirect, direct or consequential damages or loss of profits, interruption
 of business, or related expenses which may arise from use of source code or 
 documentation, including but not limited to those resulting from defects in
 source code and/or documentation, or loss or inaccuracy of data of any kind.

 */
package edu.cwru.protmapms.ui;

import java.io.File;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import edu.cwru.protmapms.IdentificationFactory;
import edu.cwru.protmapms.IdentificationFactoryConfig;
import edu.cwru.protmapms.Fasta;
import edu.cwru.protmapms.modifications.ModificationTableLoader;
import edu.cwru.protmapms.util.FileToString;
import edu.cwru.sb4j.http.Sb4jHttpClient;

/**
 *
 * @author sean-m
 */
public class GraphicalInterface extends javax.swing.JFrame {
    private String lastDir = ".";
    
    private class ProteinSequenceDatabaseFileChooserFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".fasta") || f.getName().endsWith(".txt");
        }

        @Override
        public String getDescription() {
            return("Protein sequence files");
        }
        
    }
    
    private class ModificationSiteDatabaseFileChooserFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".txt");
        }

        @Override
        public String getDescription() {
            return("Modification site files");
        }
        
    }
    
    private class SpectrumFileChooserFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().matches(".*(\\.mzXML)");
        }

        @Override
        public String getDescription() {
            return("Spectrum files");
        }
        
    }
    
    private void putFileToServer(Sb4jHttpClient client, ServerConfig sc, String dir, String file) {
        try {
            String putURL=sc.getLocation()+"?project="+dir+"&file="+file+"&pin="+sc.getPin();
            client.put(putURL,FileToString.read("results/"+dir+"/"+file));
        }
        catch(Exception e) {
            e.printStackTrace(System.out);
        }
    }
    
    private void transferToServer(String dir) {
        ServerConfig sc = ServerConfig.loadServerConfig("server.config");
        if(sc == null)
            return;
        
        Sb4jHttpClient client = new Sb4jHttpClient();
        putFileToServer(client,sc,dir,"peak-areas.json");
        putFileToServer(client,sc,dir,"chromatograms.json");
        putFileToServer(client,sc,dir,"identifications.json");
    }
    
    private class BgIdentificationFactoryWorker extends SwingWorker<String,Object> {
        private final String title;
        private final IdentificationFactoryConfig config;
        BgIdentificationFactoryWorker(String s, IdentificationFactoryConfig c) {
            title = s;
            config = c;
        }
        
        @Override
        protected String doInBackground() throws Exception {
            String result = "ok";
            progressBar.setVisible(true);
            statusLabel.setText("Processing...");
            try {
                IdentificationFactory factory = new IdentificationFactory();
                factory.configure(config);
                factory.identify();
                transferToServer(title);
                
                progressBar.setVisible(false);
                startButton.setEnabled(true);
                statusLabel.setText("Processing complete");
                return(result);
            }
            catch(Exception e) {
                e.printStackTrace(System.out);
                progressBar.setVisible(false);
                startButton.setEnabled(true);
                return "error";
            }
            
        }
        
        @Override
        protected void done() {
            
        }
    }
    
    private String validateProjectName(String title) {
        title = title.replace(" ","_");
        if(!title.matches("([A-Za-z0-9]|\\-|_)+")) {
            statusLabel.setText("Title can only contain letters, numbers, spaces and -");
            return null;
        }
        else {
            File f = new File("./results/"+title);
            if(f.exists()) {
                statusLabel.setText("A project with tha name already exists");
                return null;
            }
        }
        return title;
    }
    
    /**
     * Creates new form GraphicalInterface
     */
    public GraphicalInterface() {
        initComponents();
        progressBar.setVisible(false);
        spectrumFileList.setModel(new DefaultListModel());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fastaChooseButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        spectrumFileList = new javax.swing.JList();
        fastaFilePathLabel = new javax.swing.JLabel();
        modificationsChooseButton = new javax.swing.JButton();
        modificationFilePathLabel = new javax.swing.JLabel();
        proteaseNameCombo = new javax.swing.JComboBox();
        massLowInput = new javax.swing.JTextField();
        massHighInput = new javax.swing.JTextField();
        rtLowInput = new javax.swing.JTextField();
        rtHighInput = new javax.swing.JTextField();
        zLowInput = new javax.swing.JTextField();
        zHighInput = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        ms1ErrorPPMInput = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        ms2ErrorDaInput = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        maxMissedCleavagesInput = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        maxConcurrentModsInput = new javax.swing.JTextField();
        titleInput = new javax.swing.JTextField();
        spectrumRemoveButton = new javax.swing.JButton();
        spectrumAddButton = new javax.swing.JButton();
        startButton = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        statusLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        configureServerMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ProtMapMS v3");

        fastaChooseButton.setText("Choose");
        fastaChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fastaChooseButtonActionPerformed(evt);
            }
        });

        spectrumFileList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(spectrumFileList);

        fastaFilePathLabel.setText("--none--");

        modificationsChooseButton.setText("Choose");
        modificationsChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificationsChooseButtonActionPerformed(evt);
            }
        });

        modificationFilePathLabel.setText("--none--");

        proteaseNameCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AspN", "AspN/N->D", "Chymotrypsin", "GluC", "LysC", "Pepsin, pH=1.3", "Pepsin, pH=2.0", "Trypsin", "Non-specific" }));
        proteaseNameCombo.setSelectedIndex(7);

        massLowInput.setText("500.0");

        massHighInput.setText("4000.0");

        rtLowInput.setText("20.0");

        rtHighInput.setText("170.0");

        zLowInput.setText("2");

        zHighInput.setText("4");

        jLabel10.setText("MS1 Error PPM");

        ms1ErrorPPMInput.setText("10");

        jLabel11.setText("MS2 Error Da");

        ms2ErrorDaInput.setText("0.25");

        jLabel3.setText("Max Missed Cleavages");

        maxMissedCleavagesInput.setText("1");

        jLabel5.setText("Max Concurrent Mods");

        maxConcurrentModsInput.setText("2");

        spectrumRemoveButton.setText("   -   ");
        spectrumRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spectrumRemoveButtonActionPerformed(evt);
            }
        });

        spectrumAddButton.setText("   +   ");
        spectrumAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spectrumAddButtonActionPerformed(evt);
            }
        });

        startButton.setBackground(new java.awt.Color(254, 254, 254));
        startButton.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        startButton.setForeground(new java.awt.Color(33, 112, 31));
        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        jLabel13.setText("Protease");

        jLabel14.setText("Mass Range");

        jLabel15.setText("RT Range");

        jLabel16.setText("Charge Range");

        jLabel17.setText("Title");

        jLabel18.setText("Spectrum FIles");

        jLabel19.setText("FASTA");

        jLabel20.setText("Modifications");

        progressBar.setIndeterminate(true);

        statusLabel.setText(" ");
        statusLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jMenu1.setText("File");

        configureServerMenuItem.setText("Configure Server");
        configureServerMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configureServerMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(configureServerMenuItem);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(titleInput)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(zLowInput, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(zHighInput))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rtLowInput, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(rtHighInput))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(massLowInput, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(massHighInput))
                            .addComponent(proteaseNameCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(87, 87, 87)
                                        .addComponent(jLabel10))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel11)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ms1ErrorPPMInput)
                                    .addComponent(ms2ErrorDaInput, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(maxMissedCleavagesInput, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(maxConcurrentModsInput, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 194, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel19)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(spectrumAddButton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(spectrumRemoveButton))
                                .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(modificationsChooseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(modificationFilePathLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(fastaChooseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fastaFilePathLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane1)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fastaChooseButton)
                    .addComponent(fastaFilePathLabel)
                    .addComponent(jLabel19))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spectrumAddButton)
                            .addComponent(spectrumRemoveButton))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modificationsChooseButton)
                    .addComponent(modificationFilePathLabel)
                    .addComponent(jLabel20))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(proteaseNameCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(ms1ErrorPPMInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(massLowInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(massHighInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ms2ErrorDaInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rtLowInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rtHighInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(maxMissedCleavagesInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(maxConcurrentModsInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zLowInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zHighInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(startButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)))
                .addGap(18, 18, 18)
                .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void spectrumRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spectrumRemoveButtonActionPerformed
        DefaultListModel model = (DefaultListModel)spectrumFileList.getModel();
        List<String> selections = spectrumFileList.getSelectedValuesList();
        for(String value : selections) {
            model.removeElement(value);
        }
    }//GEN-LAST:event_spectrumRemoveButtonActionPerformed

    private void spectrumAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spectrumAddButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new SpectrumFileChooserFilter());
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setCurrentDirectory(new File(lastDir));
        fc.setMultiSelectionEnabled(true);
        int result = fc.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION) {
            lastDir = fc.getSelectedFiles()[0].getParent();
            for(File file : fc.getSelectedFiles()) {
                String path = file.getPath();
                DefaultListModel model = (DefaultListModel)spectrumFileList.getModel();
                if(!model.contains(path)) {
                    model.addElement(path);
                }
            }
        }
    }//GEN-LAST:event_spectrumAddButtonActionPerformed

    private void fastaChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fastaChooseButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ProteinSequenceDatabaseFileChooserFilter());
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setCurrentDirectory(new File(lastDir));
        
        int result = fc.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION) {
            File selected = fc.getSelectedFile();
            lastDir = selected.getParent();
            fastaFilePathLabel.setText(selected.getPath());
        }
    }//GEN-LAST:event_fastaChooseButtonActionPerformed

    private void modificationsChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificationsChooseButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ModificationSiteDatabaseFileChooserFilter());
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setCurrentDirectory(new File(lastDir));
        
        int result = fc.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION) {
            File selected = fc.getSelectedFile();
            lastDir = selected.getParent();
            modificationFilePathLabel.setText(selected.getPath());
        }
    }//GEN-LAST:event_modificationsChooseButtonActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        startButton.setEnabled(false);
        IdentificationFactoryConfig ifc = new IdentificationFactoryConfig();
                
        String projectName = validateProjectName(titleInput.getText());
        if(projectName == null) {
            startButton.setEnabled(true);
            return;
        }
        
        
        try {
            ifc.setChargeRange(Integer.parseInt(zLowInput.getText()), Integer.parseInt(zHighInput.getText()));
            ifc.setMassRange(Double.parseDouble(massLowInput.getText()), Double.parseDouble(massHighInput.getText()));
            ifc.setRTRange(Double.parseDouble(rtLowInput.getText()), Double.parseDouble(rtHighInput.getText()));
            ifc.setFasta(new Fasta(fastaFilePathLabel.getText()));
            ifc.setModifications(ModificationTableLoader.load(modificationFilePathLabel.getText()));
            ifc.setMS1ErrorPPM(Integer.parseInt(ms1ErrorPPMInput.getText()));
            ifc.setMS2ErrorDa(Double.parseDouble(ms2ErrorDaInput.getText()));
            ifc.setMaxMissedCleavages(Integer.parseInt(maxMissedCleavagesInput.getText()));
            ifc.setMaxConcurrentModifications(Integer.parseInt(maxConcurrentModsInput.getText()));
            ifc.setProteaseName(proteaseNameCombo.getSelectedItem().toString());
            ifc.setOutputDirectory("results/"+projectName);
            
            DefaultListModel model = (DefaultListModel)spectrumFileList.getModel();
            Double key = 0.0;
            for(Object item : model.toArray()) {
                ifc.addSpectrum(item.toString(), key);
                key += 1.0;
            }
            
            (new BgIdentificationFactoryWorker(projectName,ifc)).execute();
        }
        catch(Exception e) {
            e.printStackTrace(System.out);
        }
    }//GEN-LAST:event_startButtonActionPerformed

    private void configureServerMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configureServerMenuItemActionPerformed
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConfigureServer().setVisible(true);
            }
        });
    }//GEN-LAST:event_configureServerMenuItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GraphicalInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GraphicalInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GraphicalInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GraphicalInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GraphicalInterface().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem configureServerMenuItem;
    private javax.swing.JButton fastaChooseButton;
    private javax.swing.JLabel fastaFilePathLabel;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField massHighInput;
    private javax.swing.JTextField massLowInput;
    private javax.swing.JTextField maxConcurrentModsInput;
    private javax.swing.JTextField maxMissedCleavagesInput;
    private javax.swing.JLabel modificationFilePathLabel;
    private javax.swing.JButton modificationsChooseButton;
    private javax.swing.JTextField ms1ErrorPPMInput;
    private javax.swing.JTextField ms2ErrorDaInput;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JComboBox proteaseNameCombo;
    private javax.swing.JTextField rtHighInput;
    private javax.swing.JTextField rtLowInput;
    private javax.swing.JButton spectrumAddButton;
    private javax.swing.JList spectrumFileList;
    private javax.swing.JButton spectrumRemoveButton;
    private javax.swing.JButton startButton;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JTextField titleInput;
    private javax.swing.JTextField zHighInput;
    private javax.swing.JTextField zLowInput;
    // End of variables declaration//GEN-END:variables
}
