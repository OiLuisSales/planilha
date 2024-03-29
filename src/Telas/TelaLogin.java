/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Telas;

import Apontamento.CRUDOTK;
import Apontamento.GetCaPermissao;
import Codigo.ConexaoBD;
import static Telas.TelaPrincipal.versao;
import static Codigo.ConexaoBD.*;
import Codigo.DumpCA;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Mawluis
 */
public class TelaLogin extends javax.swing.JFrame {

    String login;
    String password;
    public static int responseCode = 171;

    public static int getResponseCode() {
        return responseCode;
    }

    public static void setResponseCode(int responseCode) {
        TelaLogin.responseCode = responseCode;
    }

    /**
     * Creates new form TelaLogin
     */
    public TelaLogin() {
        initComponents();
        setTitle("Ferramenta de credenciais do API. Versão:" + versao);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();
        campoLogin = new javax.swing.JTextField();
        campoPassword = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        listServer = new javax.swing.JComboBox<>();
        btnDumpOTKtoSQL = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Login de rede");

        jLabel2.setText("Password + OTP");

        btnLogin.setText("Go!");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        jLabel3.setText("Ambiente");

        listServer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "apimdev", "apimhml.oi.net.br", "apim.oi.net.br", "apimbss.oi.net.br", "apimhmllocal.intranet", "apimlocal.intranet", "apimbsslocal.intranet", "apimoiplay.oi.net.br", "apimoiplaylocal.intranet" }));
        listServer.setSelectedItem("apimhml.oi.net.br");

        btnDumpOTKtoSQL.setText("DumpOTKtoSQL");
        btnDumpOTKtoSQL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDumpOTKtoSQLActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDumpOTKtoSQL)
                .addGap(21, 21, 21))
            .addGroup(layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLogin)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(campoLogin)
                                .addComponent(campoPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(listServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(83, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(listServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(campoLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(campoPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addComponent(btnLogin)
                .addGap(35, 35, 35)
                .addComponent(btnDumpOTKtoSQL)
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        login = campoLogin.getText();
        setInsertUser(login);
        password = new String(campoPassword.getPassword());
        String server = pegarAmbiente();
        server = "https://" + server;
        GetCaPermissao gt = new GetCaPermissao();
        gt.setServer(server);

        try {
            gt.VerificarCredenciais(login, password);
        } catch (Exception ex) {
            Logger.getLogger(TelaLogin.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (responseCode == 200) {
            new TelaPrincipal().main();
            this.setVisible(false);
        } else {
            System.out.println("dados errados ou servidores indisponíveis");
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private String pegarAmbiente(){
        String server = listServer.getSelectedItem().toString().toLowerCase();

        switch (server) {//configurando tabela
            case "apimdev":
                ConexaoBD.setTableBD("Table_DEV_INT");
                server = "amgdx01:8443";
                break;
            case "apimhml.oi.net.br":
                ConexaoBD.setTableBD("Table_HML_EXT");
                server = "amghx01:8443";
                break;
            case "apim.oi.net.br":
                ConexaoBD.setTableBD("Table_PRD_EXT");
                server = "amgrx01a:8443";
                break;
            case "apimbss.oi.net.br":
                ConexaoBD.setTableBD("Table_BSS_EXT");
                server = "amgpx08a:8443";
                break;
            case "apimhmllocal.intranet":
                ConexaoBD.setTableBD("Table_HML_INT");
                server = "amghx03:8443";
                break;
            case "apimlocal.intranet":
                ConexaoBD.setTableBD("Table_PRD_INT");
                server = "amgpx03a:8443";
                break;
            case "apimbsslocal.intranet":
                ConexaoBD.setTableBD("Table_BSS_INT");
                server = "amgpx09a:8443";
                break;
            case "apimoiplaylocal.intranet":
                ConexaoBD.setTableBD("Table_PLAY_INT");
                server = "amgpx20a:8443";
                break;
            case "apimlocaloiplay.oi.net.br":
                ConexaoBD.setTableBD("Table_PLAY_EXT");
                server = "amgpx18a:8443";
                break;
        }
        return server;
    }
    private void btnDumpOTKtoSQLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDumpOTKtoSQLActionPerformed
       
        if(campoLogin.getText().equals("mawdmin")){
            
        try {
            pegarAmbiente();
            DumpCA.dumpCAOTK();
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            Logger.getLogger(TelaLogin.class.getName()).log(Level.SEVERE, null, ex);
        }
        }else{
            JOptionPane.showMessageDialog(rootPane, "Usuário não permitido a fazer dump", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_btnDumpOTKtoSQLActionPerformed

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
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaLogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDumpOTKtoSQL;
    private javax.swing.JButton btnLogin;
    private javax.swing.JTextField campoLogin;
    private javax.swing.JPasswordField campoPassword;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox<String> listServer;
    // End of variables declaration//GEN-END:variables
}
