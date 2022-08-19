/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Telas;

import Apontamento.CRUDOTK;
import static Apontamento.CRUDOTK.APIsPermitidas;
import static Apontamento.GetCaPermissao.getServer;
import static Apontamento.GetCaPermissao.setServer;
import Codigo.ConexaoBD;
import static Codigo.ConexaoBD.*;
import Codigo.MatrizDinamica;
import com.cedarsoftware.util.io.JsonWriter;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Mawluis
 */
public class TelaPrincipal extends javax.swing.JFrame {

    public static String versao = "0.9009b";
    public static String queryRunning = "";
    public static int apagar = 0;
    public static int JJ = 0;
    public static long agora = System.currentTimeMillis();
    public static boolean espera = false;

    /**
     * Creates new form TelaPrincipal
     */
    public TelaPrincipal() {
        initComponents();
        tblUrl.getSelectionModel().addListSelectionListener(new RowListener());
        spinCotaGet.setVisible(false);
        spinCotaPost.setVisible(false);
        spinCotaPut.setVisible(false);
        spinCotaDelete.setVisible(false);
        spinCotaAll.setVisible(false);
        spinCotaPatch.setVisible(false);
        lblCota.setVisible(false);
        jsonTxtArea.setText("");
        setTitle("Ferramenta de credenciais do API. Versão:" + versao);

        //listeners do checkers:
        chkGet.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    spinCotaGet.setVisible(true);
                } else {//checkbox has been deselected
                    spinCotaGet.setVisible(false);
                };
            }
        });

        chkPost.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    spinCotaPost.setVisible(true);
                } else {//checkbox has been deselected
                    spinCotaPost.setVisible(false);
                };
            }
        });

        chkPut.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    spinCotaPut.setVisible(true);
                } else {//checkbox has been deselected
                    spinCotaPut.setVisible(false);
                };
            }
        });

        chkDelete.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    spinCotaDelete.setVisible(true);
                } else {//checkbox has been deselected
                    spinCotaDelete.setVisible(false);
                };
            }
        });

        chkAll.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    spinCotaAll.setVisible(true);
                } else {//checkbox has been deselected
                    spinCotaAll.setVisible(false);
                };
            }
        });

        chkPatch.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    spinCotaPatch.setVisible(true);
                } else {//checkbox has been deselected
                    spinCotaPatch.setVisible(false);
                };
            }
        });

        txtURLAPI.getDocument().addDocumentListener(new DocumentListener() {//listener do campo de URL
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    System.out.println("listener insertUpdate");
                    acao();
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    System.out.println("listener removeUpdate");
                    acao();
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    System.out.println("listener changedUpdate");
                    acao();
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            public void acao() throws ClassNotFoundException, SQLException {
                if (queryRunning.equals(txtURLAPI.getText()) || txtURLAPI.getText().isEmpty()) { //url igual da var na memoria ou vazio
                } else {
                    queryRunning = txtURLAPI.getText();//padroniza o listener do URL com novo valor
                    new Thread() {
                        @Override
                        public void run() {
                            DefaultTableModel model = (DefaultTableModel) tblSistemas.getModel();
                            model.setNumRows(0);//limpeza enquanto roda query
                            int loop = 0;
                            String queryRunningLocal = txtURLAPI.getText();//var para não mexer com a global
                            while (queryRunningLocal.equals(txtURLAPI.getText()) && loop < 4) {
                                try {
                                    new Thread().sleep(1000);//aguardando um segundo.
                                    loop++;
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                if (loop > 1) {//depois de 2 iterações rodar select no banco
                                    atualizarMetodos(txtURLAPI.getText());
                                    setInsertURL(txtURLAPI.getText());
                                    ConexaoBD conexaobd = new ConexaoBD();
                                    try {
                                        ArrayList<String> sistemas = conexaobd.retornarSistemas(getInsertURL(), pegarAmbiente());
                                        model.setNumRows(0);//limpeza reduntante de segurança, pois houveram casos de duplicidade
                                        for (int i = 0; i < sistemas.size(); i++) {
                                            Vector row = new Vector();
                                            row.add(sistemas.get(i));
                                            model.addRow(row);
                                        }
                                    } catch (ClassNotFoundException | SQLException ex) {
                                        Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    loop = loop + 10;
                                }
                            }
                        }
                    }.start();
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnConcederPermissao = new javax.swing.JButton();
        campoCliente = new javax.swing.JTextField();
        lblSistema = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblCota = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtURLAPI = new javax.swing.JTextField();
        lblClient_key = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jsonTxtArea = new javax.swing.JTextArea();
        btnBuscarCliente = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblUrl = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        chkGet = new javax.swing.JCheckBox();
        spinCotaGet = new javax.swing.JSpinner();
        spinCotaPost = new javax.swing.JSpinner();
        spinCotaPut = new javax.swing.JSpinner();
        spinCotaDelete = new javax.swing.JSpinner();
        chkPost = new javax.swing.JCheckBox();
        chkPut = new javax.swing.JCheckBox();
        chkDelete = new javax.swing.JCheckBox();
        spinCotaAll = new javax.swing.JSpinner();
        chkAll = new javax.swing.JCheckBox();
        chkPatch = new javax.swing.JCheckBox();
        spinCotaPatch = new javax.swing.JSpinner();
        btnHelp = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        comboBoxServer = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        txtAPI = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblSistemas = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnConcederPermissao.setText("<html><font color='green'>Conceder acesso</font></html>");
        btnConcederPermissao.setVisible(false);
        btnConcederPermissao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConcederPermissaoActionPerformed(evt);
            }
        });

        campoCliente.setText("MinhaOi123");
        campoCliente.addCaretListener(e -> {
            String currentVal = txtURLAPI.getText();
            String campoClienteVal = campoCliente.getText();
            if(!currentVal.isEmpty()&&!campoClienteVal.isEmpty()) {
                btnConcederPermissao.setVisible(true);
            }else{
                btnConcederPermissao.setVisible(false);
            }
        });

        lblSistema.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblSistema.setText("Sistema");
        lblSistema.setVisible(false);

        jLabel2.setText("Método");

        lblCota.setText("Cota");

        jLabel4.setText("URL");

        txtURLAPI.addCaretListener(e -> {
            String currentVal = txtURLAPI.getText();
            String campoClienteVal = campoCliente.getText();
            if(!currentVal.isEmpty()&&!campoClienteVal.isEmpty()) {
                btnConcederPermissao.setVisible(true);
            }else{
                btnConcederPermissao.setVisible(false);
            }
        });

        lblClient_key.setText("Cliente");

        jsonTxtArea.setEditable(false);
        jsonTxtArea.setColumns(20);
        jsonTxtArea.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jsonTxtArea.setRows(5);
        jsonTxtArea.setText("{\n\t\"APIsPermitidas\": {\n    \"POST/api/novafibra/v1/antifraude/fechamentocaso\": {\n      \"quota\": 1\n    },\n    \"POST/trg/api/novafibra/v1/antifraude/fechamentocaso\": {\n      \"quota\": 1\n    }\n  },\n  \"APIsPermitidas_v2\": {\n    \"/api/customermanagement/v3/creditanalysis\": {\n      \"GET\": \n      {\n        \"quota\": 3\n      },\n      \"POST\": \n      {\n        \"quota\": 2\n      }\n    },\n    \"/api/customermanagement/v3/revenueStatus\": {\n      \"ALL\": {\n        \"quota\": 5\n      }\n    },\n    \"/api/customermanagement/v3/xpto\": {\n      \"PATCH\": {\n        \"quota\": 3\n      },\n      \"ALL\": {\n        \"quota\": 4\n      }\n    },\n    \"/api/customermanagement/v3/*\": {\n      \"ALL\": {\n        \"quota\": 7\n      }\n    }\n  }\n}");
        jScrollPane1.setViewportView(jsonTxtArea);

        btnBuscarCliente.setText("Buscar Cliente");
        btnBuscarCliente.setVisible(true);
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        tblUrl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "URL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblUrl);

        chkGet.setText("GET");

        spinCotaGet.setValue(1);

        spinCotaPost.setValue(1);

        spinCotaPut.setValue(1);

        spinCotaDelete.setValue(1);

        chkPost.setText("POST");

        chkPut.setText("PUT");

        chkDelete.setText("DELETE");

        spinCotaAll.setValue(1);

        chkAll.setText("ALL");

        chkPatch.setText("PATCH");

        spinCotaPatch.setValue(1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkGet)
                            .addComponent(chkPost)
                            .addComponent(chkPut)
                            .addComponent(chkDelete)
                            .addComponent(chkAll))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spinCotaDelete, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinCotaPut, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinCotaPost, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinCotaGet, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinCotaAll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(chkPatch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(spinCotaPatch, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkGet)
                    .addComponent(spinCotaGet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinCotaPost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPost))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinCotaPut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPut))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinCotaDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkDelete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinCotaAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPatch)
                    .addComponent(spinCotaPatch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        btnHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/questionMark.png"))); // NOI18N
        btnHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHelpActionPerformed(evt);
            }
        });

        jLabel3.setText("Json OTK retornado");

        comboBoxServer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "apimdev", "apimhml.oi.net.br", "apim.oi.net.br", "apimbss.oi.net.br", "apimhmllocal.intranet", "apimlocal.intranet", "apimbsslocal.intranet", "apimoiplay.oi.net.br", "apimoiplaylocal.intranet" }));

        txtAPI.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        txtAPI.setText("API");
        txtAPI.setVisible(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(234, 234, 234)
                .addComponent(txtAPI)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(txtAPI)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        tblSistemas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Sistemas que usam este API"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblSistemas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2))
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(137, 137, 137)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(comboBoxServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(97, 97, 97)
                                        .addComponent(lblSistema))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(lblClient_key)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(campoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(27, 27, 27)
                                        .addComponent(btnBuscarCliente))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(94, 94, 94)
                                        .addComponent(lblCota))
                                    .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(btnConcederPermissao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtURLAPI, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(212, 212, 212)
                                            .addComponent(jLabel4))))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(238, 238, 238)
                                        .addComponent(btnHelp)
                                        .addGap(90, 90, 90))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(168, 168, 168)
                                        .addComponent(jLabel3))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 6, Short.MAX_VALUE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(comboBoxServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblSistema)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnBuscarCliente)
                            .addComponent(campoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblClient_key))
                        .addGap(36, 36, 36)
                        .addComponent(btnConcederPermissao, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(lblCota))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnHelp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txtURLAPI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(85, 85, 85)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void atualizarMetodos(String url) {
        if (queryRunning != url) {
            queryRunning = url;
            txtAPI.setVisible(false);

            new Thread() {//pegando nome do API pelo URL no SSG
                @Override
                public void run() {
                    try {
                        String api_outdoor = CRUDOTK.getAPI(url);
                        api_outdoor = "<html><font color='green'>" + api_outdoor + "</font></html>";
                        txtAPI.setText(api_outdoor);
                        txtAPI.setVisible(true);
                    } catch (Exception ex) {
                        Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();

            new Thread() {//pegando nome do API pelo banco heroku
                @Override
                public void run() {

                    ConexaoBD conexaobd = new ConexaoBD();
                    try {
                        String api_outdoor = conexaobd.getAPI(url, pegarAmbiente());
                        if (!api_outdoor.equals("no_name")) {
                            api_outdoor = "<html><font color='blue'>" + api_outdoor + "</font></html>";
                            txtAPI.setText(api_outdoor);
                            txtAPI.setVisible(true);
                        }
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }.start();

            String matrix[][] = parseJsonToMatrix(jsonTxtArea.getText());

            chkGet.setSelected(false);
            chkPut.setSelected(false);
            chkPost.setSelected(false);
            chkDelete.setSelected(false);
            chkAll.setSelected(false);
            chkPatch.setSelected(false);

            for (int i = 0; i < matrix.length; i++) {
                if (matrix[i][0].equals(txtURLAPI.getText())) {
                    for (int j = 0; j < matrix[i].length; j++) {

                        if ((j != 0) && matrix[i][j] != null) {
                            JSONObject metodo = new JSONObject(matrix[i][j]);
                            String keyMetodo = metodo.keys().next();
                            int quota = (int) metodo.getJSONObject(keyMetodo).getInt("quota");
                            switch (keyMetodo) {
                                case "GET":
                                    chkGet.setSelected(true);
                                    spinCotaGet.setValue(quota);
                                    break;
                                case "POST":
                                    chkPost.setSelected(true);
                                    spinCotaPost.setValue(quota);
                                    break;
                                case "PUT":
                                    chkPut.setSelected(true);
                                    spinCotaPut.setValue(quota);
                                    break;
                                case "DELETE":
                                    chkDelete.setSelected(true);
                                    spinCotaDelete.setValue(quota);
                                    break;
                                case "ALL":
                                    chkAll.setSelected(true);
                                    spinCotaAll.setValue(quota);
                                    break;
                                case "PATCH":
                                    chkPatch.setSelected(true);
                                    spinCotaPatch.setValue(quota);
                                    break;
                            }

                        }
                    }
                }

            }
        } else {
            System.out.println("texto igual, saindo do método");
        }
    }

    public static void setarJsonTxtArea(String json) {
        jsonTxtArea.setText(json);
    }

    public String pegarAmbiente() {
        String tabela = comboBoxServer.getSelectedItem().toString();

        switch (tabela) {//configurando tabela
            case "apimdev":
                ConexaoBD.setTableBD("Table_DEV_INT");
                tabela = "amgdx01:8443";
                break;
            case "apimhml.oi.net.br":
                ConexaoBD.setTableBD("Table_HML_EXT");
                tabela = "amghx01:8443";
                break;
            case "apim.oi.net.br":
                ConexaoBD.setTableBD("Table_PRD_EXT");
                tabela = "amgrx01a:8443";
                break;
            case "apimbss.oi.net.br":
                ConexaoBD.setTableBD("Table_BSS_EXT");
                tabela = "amgpx08a:8443";
                break;
            case "apimhmllocal.intranet":
                ConexaoBD.setTableBD("Table_HML_INT");
                tabela = "amghx03:8443";
                break;
            case "apimlocal.intranet":
                ConexaoBD.setTableBD("Table_PRD_INT");
                tabela = "amgpx03a:8443";
                break;
            case "apimbsslocal.intranet":
                ConexaoBD.setTableBD("Table_BSS_INT");
                tabela = "amgpx09a:8443";
                break;
            case "apimoiplaylocal.intranet":
                ConexaoBD.setTableBD("Table_PLAY_INT");
                tabela = "amgpx20a:8443";
                break;
            case "apimlocaloiplay.oi.net.br":
                ConexaoBD.setTableBD("Table_PLAY_EXT");
                tabela = "amgpx18a:8443";
                break;
        }
        setServer("https://" + tabela);

        return getTableBD();
    }
    private void btnConcederPermissaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConcederPermissaoActionPerformed
        do {
            if (getInsertClient_key().toLowerCase().equals(campoCliente.getText().toLowerCase()) || getInsertSistema().toLowerCase().equals(campoCliente.getText().toLowerCase())) {//proibir conceder acesso sem antes buscar
                String logMessage = JOptionPane.showInputDialog("Descrição. " + "Favor inserir comentário desta ação ");
                if (logMessage == null) {
                    System.out.println("abortado");
                    break;
                }

                setInsertURL(txtURLAPI.getText());
                String custom_key = "";//armazena APIsPermitidas_v2
                ConexaoBD conexaobd = new ConexaoBD();
                boolean pelomenos1 = false;
                if (chkGet.isSelected()) {
                    pelomenos1 = true;
                }
                if (chkPost.isSelected()) {
                    pelomenos1 = true;
                }
                if (chkPut.isSelected()) {
                    pelomenos1 = true;
                }
                if (chkDelete.isSelected()) {
                    pelomenos1 = true;
                }
                if (chkAll.isSelected()) {
                    pelomenos1 = true;
                }
                if (chkPatch.isSelected()) {
                    pelomenos1 = true;
                }
                if (!pelomenos1) {//cai nesse enlace se não marcou nenhum método
                    String typedJson = jsonTxtArea.getText();

                    if (typedJson.isEmpty()) {
                        typedJson = "{\"APIsPermitidas_v2\":{}}";
                    }

                    JSONObject APIPermitidasv2 = new JSONObject(typedJson);
                    String urlAPI = txtURLAPI.getText();

                    String message = "Não foi encontrado nenhum método. Deseja apagar o URL" + urlAPI + "?";
                    String title = "Confirmação";

                    int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);//Exibe caixa de dialogo solicitando confirmação ou não. 
                    if (reply == JOptionPane.YES_OPTION) {//Se o usuário clicar em "Sim" retorna 0 pra variavel reply, se informado não retorna 1
                        setInsertApagar(true);//Dar update no ATIVO = f em todos métodos desse URL, inserir linha com registro da exclusão
                        APIPermitidasv2.getJSONObject("APIsPermitidas_v2").remove(urlAPI);
                        String json = APIPermitidasv2.toString();
                        System.out.println("reply:" + reply);
                        json = JsonWriter.formatJson(json); //indentando   
                        jsonTxtArea.setText(json);
                        if (!CRUDOTK.APIsPermitidas.isEmpty()) {
                            jsonTxtArea.setText(juntarAPIsPermitidas(jsonTxtArea.getText()));
                        }
                        alimentaJtable(json);

                        try {
                            conexaobd.maxInsercao(pegarAmbiente());
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        try {//conseguindo API TODO melhorar para não ter que buscar o nome do API no banco, e sim, num array quando apertar o buscarCliente
                            setInsertAPI(conexaobd.buscarAPI(getInsertURL(), pegarAmbiente()));
                        } catch (ClassNotFoundException | SQLException ex) {
                            Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        try {
                            conexaobd.inativador(getInsertURL(), getInsertClient_key(), pegarAmbiente(), logMessage);//método chama query para desativar tudo que tem no banco com esse URL e client_key antes de colocar os métodos novos. 
                        } catch (ClassNotFoundException | SQLException ex) {
                            Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        System.out.println("PUT no otk, de apagar URL:");

                        try {//pegar APIsPermitidas legado. Da erro qdo client_key n for o client_ident. TODO API que retorna client_key vigente e dar UPDATE no sistema
                            CRUDOTK.getOTK(getInsertClient_key());
                            custom_key = juntarAPIsPermitidas(jsonTxtArea.getText());
                            jsonTxtArea.setText(custom_key);
                        } catch (Exception ex) {
                            APIsPermitidas = "";
                            JOptionPane.showMessageDialog(null, "client_key diferente do client_ident\n" + (ex), "Erro! IOException:", JOptionPane.ERROR_MESSAGE);
                        }

                        try {
                            CRUDOTK.putOTK(getInsertClient_key(), getInsertSistema(), getInsertOrg(), getInsertDescription(), getInsertType(), custom_key, getInsertUser(), getServer());
                        } catch (IOException | InterruptedException ex) {
                            Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println(jsonTxtArea.getText());
                        JOptionPane.showMessageDialog(rootPane, "URL " + urlAPI + " deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {

                    try {
                        conexaobd.maxInsercao(pegarAmbiente());
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {//conseguindo API TODO melhorar para não ter que buscar o nome do API no banco, e sim, num array quando apertar o buscarCliente
                        setInsertAPI(conexaobd.buscarAPI(getInsertURL(), pegarAmbiente()));
                    } catch (ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        conexaobd.inativador(getInsertURL(), getInsertClient_key(), pegarAmbiente(), logMessage);//método chama query para desativar tudo que tem no banco com esse URL e client_key antes de colocar os métodos novos. 
                    } catch (ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    String[][] matrix = parseJsonToMatrix(jsonTxtArea.getText());
                    int posUrl = -1;

                    // leitura do vetor
                    for (int i = 0; i < matrix.length; i++) {

                        // se existir no vetor o número digitado
                        if (matrix[i][0].equals(txtURLAPI.getText())) {

                            posUrl = i; //armazenando posição do vetor que o URL foi encontrado
                        }
                    }

                    if (posUrl != -1) {//verifica se já tem o URL no vetor

                        matrix[posUrl][0] = txtURLAPI.getText();

                        setInsertURL(txtURLAPI.getText());

                        pelomenos1 = false;
                        if (chkGet.isSelected()) {
                            matrix[posUrl][1] = "  {\"GET\":{\"quota\":" + spinCotaGet.getValue() + "} }     ";
                            pelomenos1 = true;

                            setInsertMetodo("GET");
                            setInsertCota(Integer.parseInt(spinCotaGet.getValue().toString()));

                            try {//INSERT NO BANCO
                                conexaobd.insertBD(insertAPI, getInsertURL(), getInsertMetodo(), getInsertCota(), getInsertSistema(), getInsertClient_key(), getInsertUser(), getINSERCAO(), true, getInsertType(), getInsertDescription(), getInsertOrg(), pegarAmbiente(), logMessage);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            matrix[posUrl][1] = null;
                        }
                        if (chkPost.isSelected()) {
                            matrix[posUrl][2] = "  {\"POST\":{\"quota\":" + spinCotaPost.getValue() + "}}     ";
                            pelomenos1 = true;

                            setInsertMetodo("POST");
                            setInsertCota(Integer.parseInt(spinCotaPost.getValue().toString()));

                            try {//INSERT NO BANCO
                                conexaobd.insertBD(insertAPI, getInsertURL(), getInsertMetodo(), getInsertCota(), getInsertSistema(), getInsertClient_key(), getInsertUser(), getINSERCAO(), true, getInsertType(), getInsertDescription(), getInsertOrg(), pegarAmbiente(), logMessage);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            matrix[posUrl][2] = null;
                        }
                        if (chkPut.isSelected()) {
                            matrix[posUrl][3] = "  {\"PUT\":{\"quota\":" + spinCotaPut.getValue() + "}  }   ";
                            pelomenos1 = true;

                            setInsertMetodo("PUT");
                            setInsertCota(Integer.parseInt(spinCotaPut.getValue().toString()));

                            try {//INSERT NO BANCO
                                conexaobd.insertBD(insertAPI, getInsertURL(), getInsertMetodo(), getInsertCota(), getInsertSistema(), getInsertClient_key(), getInsertUser(), getINSERCAO(), true, getInsertType(), getInsertDescription(), getInsertOrg(), pegarAmbiente(), logMessage);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            matrix[posUrl][3] = null;
                        }
                        if (chkDelete.isSelected()) {
                            matrix[posUrl][4] = "  {\"DELETE\":{\"quota\":" + spinCotaDelete.getValue() + "} }    ";
                            pelomenos1 = true;

                            setInsertMetodo("DELETE");
                            setInsertCota(Integer.parseInt(spinCotaDelete.getValue().toString()));

                            try {//INSERT NO BANCO
                                conexaobd.insertBD(insertAPI, getInsertURL(), getInsertMetodo(), getInsertCota(), getInsertSistema(), getInsertClient_key(), getInsertUser(), getINSERCAO(), true, getInsertType(), getInsertDescription(), getInsertOrg(), pegarAmbiente(), logMessage);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            matrix[posUrl][4] = null;
                        }
                        if (chkAll.isSelected()) {
                            matrix[posUrl][5] = "  {\"ALL\":{\"quota\":" + spinCotaAll.getValue() + "}   }  ";
                            pelomenos1 = true;

                            setInsertMetodo("ALL");
                            setInsertCota(Integer.parseInt(spinCotaAll.getValue().toString()));

                            try {//INSERT NO BANCO
                                conexaobd.insertBD(insertAPI, getInsertURL(), getInsertMetodo(), getInsertCota(), getInsertSistema(), getInsertClient_key(), getInsertUser(), getINSERCAO(), true, getInsertType(), getInsertDescription(), getInsertOrg(), pegarAmbiente(), logMessage);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            matrix[posUrl][5] = null;
                        }
                        if (chkPatch.isSelected()) {
                            matrix[posUrl][6] = "  {\"PATCH\":{\"quota\":" + spinCotaPatch.getValue() + "}   }  ";
                            pelomenos1 = true;

                            setInsertMetodo("PATCH");
                            setInsertCota(Integer.parseInt(spinCotaPatch.getValue().toString()));

                            try {//INSERT NO BANCO
                                conexaobd.insertBD(insertAPI, getInsertURL(), getInsertMetodo(), getInsertCota(), getInsertSistema(), getInsertClient_key(), getInsertUser(), getINSERCAO(), true, getInsertType(), getInsertDescription(), getInsertOrg(), pegarAmbiente(), logMessage);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            matrix[posUrl][6] = null;
                        }
                        if (!pelomenos1) {
                            //já tratado
                        } else {
                            gerarJson(matrix);
                            System.out.println("PUT no otk de overwrite:");

                            try {//pegar APIsPermitidas legado. Da erro qdo client_key n for o client_ident. TODO API que retorna client_key vigente e dar UPDATE no sistema
                                CRUDOTK.getOTK(getInsertClient_key());
                                custom_key = juntarAPIsPermitidas(jsonTxtArea.getText());
                                jsonTxtArea.setText(custom_key);
                            } catch (Exception ex) {
                                APIsPermitidas = "";
                                JOptionPane.showMessageDialog(null, "client_key diferente do client_ident\n" + (ex), "Erro! IOException:", JOptionPane.ERROR_MESSAGE);
                            }

                            try {
                                CRUDOTK.putOTK(getInsertClient_key(), getInsertSistema(), getInsertOrg(), getInsertDescription(), getInsertType(), custom_key, getInsertUser(), getServer());
                            } catch (Exception ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println(jsonTxtArea.getText());
                        }

                    } else {//não possui dentro do vetor, acrescentar novo URL
                        String[][] newMatrix = new String[matrix.length + 1][7];

                        for (int i = 0; i < matrix.length; i++) {
                            for (int j = 0; j < matrix[i].length; j++) {
                                newMatrix[i][j] = matrix[i][j];
                            }
                        }

                        boolean peloMenos1 = false;
                        newMatrix[newMatrix.length - 1][0] = getInsertURL();
                        if (chkGet.isSelected()) {
                            newMatrix[newMatrix.length - 1][1] = "  {\"GET\":{\"quota\":" + spinCotaGet.getValue() + "} }     ";
                            peloMenos1 = true;
                            setInsertMetodo("GET");
                            setInsertCota(Integer.parseInt(spinCotaGet.getValue().toString()));

                            try {//INSERT NO BANCO
                                conexaobd.insertBD(insertAPI, getInsertURL(), getInsertMetodo(), getInsertCota(), getInsertSistema(), getInsertClient_key(), getInsertUser(), getINSERCAO(), true, getInsertType(), getInsertDescription(), getInsertOrg(), pegarAmbiente(), logMessage);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (chkPost.isSelected()) {
                            newMatrix[newMatrix.length - 1][2] = "  {\"POST\":{\"quota\":" + spinCotaPost.getValue() + "}}     ";
                            peloMenos1 = true;

                            setInsertMetodo("POST");
                            setInsertCota(Integer.parseInt(spinCotaPost.getValue().toString()));

                            try {//INSERT NO BANCO
                                conexaobd.insertBD(insertAPI, getInsertURL(), getInsertMetodo(), getInsertCota(), getInsertSistema(), getInsertClient_key(), getInsertUser(), getINSERCAO(), true, getInsertType(), getInsertDescription(), getInsertOrg(), pegarAmbiente(), logMessage);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (chkPut.isSelected()) {
                            newMatrix[newMatrix.length - 1][3] = "  {\"PUT\":{\"quota\":" + spinCotaPut.getValue() + "}  }   ";
                            peloMenos1 = true;

                            setInsertMetodo("PUT");
                            setInsertCota(Integer.parseInt(spinCotaPut.getValue().toString()));

                            try {//INSERT NO BANCO
                                conexaobd.insertBD(insertAPI, getInsertURL(), getInsertMetodo(), getInsertCota(), getInsertSistema(), getInsertClient_key(), getInsertUser(), getINSERCAO(), true, getInsertType(), getInsertDescription(), getInsertOrg(), pegarAmbiente(), logMessage);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (chkDelete.isSelected()) {
                            newMatrix[newMatrix.length - 1][4] = "  {\"DELETE\":{\"quota\":" + spinCotaDelete.getValue() + "} }    ";
                            peloMenos1 = true;

                            setInsertMetodo("DELETE");
                            setInsertCota(Integer.parseInt(spinCotaDelete.getValue().toString()));

                            try {//INSERT NO BANCO
                                conexaobd.insertBD(insertAPI, getInsertURL(), getInsertMetodo(), getInsertCota(), getInsertSistema(), getInsertClient_key(), getInsertUser(), getINSERCAO(), true, getInsertType(), getInsertDescription(), getInsertOrg(), pegarAmbiente(), logMessage);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (chkAll.isSelected()) {
                            newMatrix[newMatrix.length - 1][5] = "  {\"ALL\":{\"quota\":" + spinCotaAll.getValue() + "}   }  ";
                            peloMenos1 = true;

                            setInsertMetodo("ALL");
                            setInsertCota(Integer.parseInt(spinCotaAll.getValue().toString()));

                            try {//INSERT NO BANCO
                                conexaobd.insertBD(insertAPI, getInsertURL(), getInsertMetodo(), getInsertCota(), getInsertSistema(), getInsertClient_key(), getInsertUser(), getINSERCAO(), true, getInsertType(), getInsertDescription(), getInsertOrg(), pegarAmbiente(), logMessage);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (chkPatch.isSelected()) {
                            newMatrix[newMatrix.length - 1][6] = "  {\"PATCH\":{\"quota\":" + spinCotaPatch.getValue() + "}   }  ";
                            peloMenos1 = true;

                            setInsertMetodo("PATCH");
                            setInsertCota(Integer.parseInt(spinCotaPatch.getValue().toString()));

                            try {//INSERT NO BANCO
                                conexaobd.insertBD(insertAPI, getInsertURL(), getInsertMetodo(), getInsertCota(), getInsertSistema(), getInsertClient_key(), getInsertUser(), getINSERCAO(), true, getInsertType(), getInsertDescription(), getInsertOrg(), pegarAmbiente(), logMessage);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        if (!peloMenos1) {
                            JOptionPane.showMessageDialog(rootPane, "Favor escolher ao menos um método", "Erro", JOptionPane.ERROR_MESSAGE);
                        } else {
                            gerarJson(newMatrix);
                            System.out.println("PUT no otk:");

                            try {//pegar APIsPermitidas legado. Da erro qdo client_key n for o client_ident. TODO API que retorna client_key vigente e dar UPDATE no sistema
                                CRUDOTK.getOTK(getInsertClient_key());
                                custom_key = juntarAPIsPermitidas(jsonTxtArea.getText());
                                jsonTxtArea.setText(custom_key);
                            } catch (Exception ex) {
                                APIsPermitidas = "";
                                JOptionPane.showMessageDialog(null, "client_key diferente do client_ident\n" + (ex), "Erro! IOException:", JOptionPane.ERROR_MESSAGE);
                            }

                            try {
                                CRUDOTK.putOTK(getInsertClient_key(), getInsertSistema(), getInsertOrg(), getInsertDescription(), getInsertType(), custom_key, getInsertUser(), getServer());
                            } catch (Exception ex) {
                                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println(jsonTxtArea.getText());
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Favor buscar antes de conceder acesso", "Aviso", JOptionPane.ERROR_MESSAGE);
            }
        } while (false);//do-while para poder usar break;
    }//GEN-LAST:event_btnConcederPermissaoActionPerformed

    public static String juntarAPIsPermitidas(String custom_key) {

        if (CRUDOTK.APIsPermitidas.isEmpty()) {
            System.out.println("custom_key:" + custom_key);
        } else {
            JSONObject APIsPermitidas = new JSONObject(CRUDOTK.APIsPermitidas);
            JSONObject APIsPermitidas_v2 = new JSONObject(jsonTxtArea.getText()).getJSONObject("APIsPermitidas_v2");
            JSONObject custom_keyObj = new JSONObject();

            custom_keyObj.put("APIsPermitidas", APIsPermitidas);
            custom_keyObj.put("APIsPermitidas_v2", APIsPermitidas_v2);

            custom_key = custom_keyObj.toString();
            custom_key = JsonWriter.formatJson(custom_key); //indentando   
            System.out.println("custom_key:" + custom_key);
        }
        return custom_key;
    }

    public static String gerarJson(String newMatrix[][]) {

        JSONObject cota = new JSONObject();
        JSONObject metodo = new JSONObject();
        JSONObject urlAPIObj = new JSONObject();
        JSONObject apiPermitidasObj = new JSONObject();

        for (int i = 0; i < newMatrix.length; i++) {
            for (int j = 0; j < newMatrix[i].length; j++) {
                if ((j != 0) && newMatrix[i][j] != null) {
                    JSONObject cotaFor = new JSONObject(newMatrix[i][j]);
                    String keyMetodo = cotaFor.keys().next();
                    int quota = (int) cotaFor.getJSONObject(keyMetodo).getInt("quota");
                    cota.put("quota", quota); //inserindo inteiro na cota
                    metodo.put(keyMetodo, cota);          //inserindo cota no método
                    cota = new JSONObject();
                }
            }
            urlAPIObj.put(newMatrix[i][0], metodo); //inserindo método no URL
            metodo = new JSONObject();
            apiPermitidasObj.put("APIsPermitidas_v2", urlAPIObj);

        }

        try {//apagar Cadastro (alguns clientes não tem API então são registrados no banco com a linha cadastro)
            apiPermitidasObj.getJSONObject("APIsPermitidas_v2").remove("Cadastro");//mesmo n tendo Cadastro, n cai em exceção
        } catch (JSONException je) {
            System.out.println("Catch apagar Cadastro");
        }

        String json = apiPermitidasObj.toString();

        json = JsonWriter.formatJson(json); //indentando   
        jsonTxtArea.setText(json);

        alimentaJtable(json);

        return json;
    }

    public static String gerarJson2(MatrizDinamica<String> matriz) {

        JSONObject cota = new JSONObject();
        JSONObject metodo = new JSONObject();
        JSONObject urlAPIObj = new JSONObject();
        JSONObject apiPermitidasObj = new JSONObject();

        for (int i = 0; i < matriz.getSizeX(); i++) {
            System.out.println(matriz.get(i, 0));
            System.out.println("matriz.getSizeX:" + matriz.getSizeX());
            for (int j = 0; j < matriz.getSizeY(i); j++) {
                String elemento = matriz.get(i, j); // obtendo o elementos
                System.out.println("elemento:" + elemento);
                System.out.println("matriz.getSizeY:" + matriz.getSizeY(i));

                if (j != 0) {//não quero URL, só vetor de métodos com a cota
                    JSONObject cotaFor = new JSONObject(matriz.get(i, j));
                    String keyMetodo = cotaFor.keys().next();
                    int quota = (int) cotaFor.getJSONObject(keyMetodo).getInt("quota");
                    cota.put("quota", quota); //inserindo inteiro na cota
                    metodo.put(keyMetodo, cota);          //inserindo cota no método
                    cota = new JSONObject();
                }

            }

            urlAPIObj.put(matriz.get(i, 0), metodo); //inserindo método no URL
            metodo = new JSONObject();
            apiPermitidasObj.put("APIsPermitidas_v2", urlAPIObj);
        }

        try {//apagar Cadastro (alguns clientes não tem API então são registrados no banco com a linha cadastro)
            apiPermitidasObj.getJSONObject("APIsPermitidas_v2").remove("Cadastro");//mesmo n tendo Cadastro, n cai em exceção
        } catch (JSONException je) {
            System.out.println("Catch apagar Cadastro");
        }

        String json = apiPermitidasObj.toString();

        json = JsonWriter.formatJson(json); //indentando   
        jsonTxtArea.setText(json);

        alimentaJtable(json);

        return json;
    }

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteActionPerformed
        lblSistema.setVisible(false);
        setInsertSistema("");
        jsonTxtArea.setText("");
        DefaultTableModel model = (DefaultTableModel) tblSistemas.getModel();//limpeza da tabela
        model.setNumRows(0);//limpeza da tabela
        alimentaJtable("{\"APIsPermitidas_v2\":{}}");
        txtURLAPI.setText("");
        txtAPI.setVisible(false);
        setInsertClient_key(campoCliente.getText());
        ConexaoBD conexaobd = new ConexaoBD();
        boolean notfound = true;

        try {
            notfound = conexaobd.buscarClient_key("SISTEMA", pegarAmbiente(), true);//procura por sistema e registro ativo;após rodar, também alimentará setInsertSistema()

            if (notfound) {
                notfound = conexaobd.buscarClient_key("SISTEMA", pegarAmbiente(), false);//procura por sistema e registro inativo
            }

            if (notfound) {
                JOptionPane.showMessageDialog(null, "Sistema " + getInsertClient_key() + " não encontrado, buscando como client_id", "Não encontrado", JOptionPane.INFORMATION_MESSAGE);
                setInsertClient_key(getInsertClient_key().replaceAll("	", "").replaceAll(" ", ""));
                notfound = conexaobd.buscarClient_key("CLIENT_KEY", pegarAmbiente(), true);//procura por client_id e registro ativo
            }

            if (notfound) {
                notfound = conexaobd.buscarClient_key("CLIENT_KEY", pegarAmbiente(), false);//procura por client_id e registro inativo
            }

            if (notfound) {
                JOptionPane.showMessageDialog(null, "O " + getInsertClient_key() + " não existe no banco", "Não encontrado", JOptionPane.ERROR_MESSAGE);
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }

        lblSistema.setText(getInsertSistema());
        lblSistema.setVisible(true);
    }//GEN-LAST:event_btnBuscarClienteActionPerformed

    private void btnHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHelpActionPerformed
        JOptionPane.showMessageDialog(null, "Versão " + versao + ". (JDK 18).\nDesenvolvido por Luis Sales (luis.sales@oi.br.net).\n");
    }//GEN-LAST:event_btnHelpActionPerformed

    public static void alimentaJtable(String Json) {
        String[][] matrix = parseJsonToMatrix(Json);

        Vector<String> columnNames = new Vector<>();
        columnNames.add("URL");

        DefaultTableModel model = (DefaultTableModel) tblUrl.getModel();
        model.setNumRows(0);

        for (int i = 0; i < matrix.length; i++) {
            Vector row = new Vector();
            row.add(matrix[i][0].toString());
            model.addRow(row);
        }
    }

    public static void alimentaJtable2(MatrizDinamica<String> matriz) {

        Vector<String> columnNames = new Vector<>();
        columnNames.add("URL");

        DefaultTableModel model = (DefaultTableModel) tblUrl.getModel();
        model.setNumRows(0);

        for (int i = 0; i < matriz.getSizeX(); i++) {
            System.out.println(matriz.get(i, 0));
            Vector row = new Vector();
            row.add(matriz.get(i, 0));
            model.addRow(row);

        }
    }

    private class RowListener implements ListSelectionListener {//método chamado quando é selecionado linha da tabela 2

        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            int linhaSelecionada = tblUrl.getSelectedRow();

            try {
                String urlApi = (String) tblUrl.getValueAt(linhaSelecionada, 0);
                txtURLAPI.setText(urlApi);//listerner spotará esse setText fazendo com que chame o atualizarMetodos
            } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                System.out.println("exceção:índice -1");
            }

        }
    }

    public static String[][] parseJsonToMatrix(String typedJson) {//TODO inserir inteligência de armazenar o CRUD.APIsPermitidas, se tiver, aqui
        JSONObject jsonTypedObj = new JSONObject(typedJson);
        try {
            jsonTypedObj = jsonTypedObj.getJSONObject("APIsPermitidas_v2");
        } catch (Exception ex) {
            typedJson = "{\"APIsPermitidas_v2\":{}}";
            jsonTypedObj = new JSONObject(typedJson);
        }

        Iterator<String> keys = jsonTypedObj.keys();
        int matrizUrl = 0;
        int matrizMetodo = 0;
        String[][] matriz = new String[jsonTypedObj.length()][7]; //tabela de URLs por métodos (suporta 5 métodos)
        while (keys.hasNext()) {
            String key = keys.next();
            if (jsonTypedObj.get(key) instanceof JSONObject) {//saber se o URL da iteração é um JSONObject
                matriz[matrizUrl][matrizMetodo] = key;
                JSONObject jsonTypedObjMetodo = new JSONObject(typedJson);
                jsonTypedObjMetodo = jsonTypedObj.getJSONObject(key);
                Iterator<String> keysMetodo = jsonTypedObjMetodo.keys();

                while (keysMetodo.hasNext()) {
                    String keyMetodo = keysMetodo.next();
                    if (jsonTypedObjMetodo.get(keyMetodo) instanceof JSONObject) {
                        String json = "    {\"" + keyMetodo + "\": " + jsonTypedObjMetodo.get(keyMetodo).toString() + "}         ";

                        // json = JsonWriter.formatJson(json); //indentando   
                        matrizMetodo++;
                        matriz[matrizUrl][matrizMetodo] = json;
                    }

                }
                matrizUrl++;
                matrizMetodo = 0;
            }
        }
        return matriz;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws UnsupportedLookAndFeelException {
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaPrincipal().setVisible(true);
            }
        });
    }

    public void main() {
        new TelaPrincipal().setVisible(true);
        switch (getTableBD()) {//configurando tabela
            case "Table_DEV_INT" ->
                comboBoxServer.setSelectedItem("apimdev");
            case "Table_HML_EXT" ->
                comboBoxServer.setSelectedItem("apimhml.oi.net.br");
            case "Table_PRD_EXT" ->
                comboBoxServer.setSelectedItem("apim.oi.net.br");
            case "Table_BSS_EXT" ->
                comboBoxServer.setSelectedItem("apimbss.oi.net.br");
            case "Table_HML_INT" ->
                comboBoxServer.setSelectedItem("apimhmllocal.intranet");
            case "Table_PRD_INT" ->
                comboBoxServer.setSelectedItem("apimlocal.intranet");
            case "Table_BSS_INT" ->
                comboBoxServer.setSelectedItem("apimbsslocal.intranet");
            case "Table_PLAY_INT" ->
                comboBoxServer.setSelectedItem("apimoiplaylocal.intranet");
            case "Table_PLAY_EXT" ->
                comboBoxServer.setSelectedItem("apimlocaloiplay.oi.net.br");
        }
        //configurando tabela
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnConcederPermissao;
    private javax.swing.JButton btnHelp;
    private javax.swing.JTextField campoCliente;
    private javax.swing.JCheckBox chkAll;
    private javax.swing.JCheckBox chkDelete;
    private javax.swing.JCheckBox chkGet;
    private javax.swing.JCheckBox chkPatch;
    private javax.swing.JCheckBox chkPost;
    private javax.swing.JCheckBox chkPut;
    private static javax.swing.JComboBox<String> comboBoxServer;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    public static javax.swing.JTextArea jsonTxtArea;
    private javax.swing.JLabel lblClient_key;
    private javax.swing.JLabel lblCota;
    private javax.swing.JLabel lblSistema;
    private javax.swing.JSpinner spinCotaAll;
    private javax.swing.JSpinner spinCotaDelete;
    private javax.swing.JSpinner spinCotaGet;
    private javax.swing.JSpinner spinCotaPatch;
    private javax.swing.JSpinner spinCotaPost;
    private javax.swing.JSpinner spinCotaPut;
    private javax.swing.JTable tblSistemas;
    private static javax.swing.JTable tblUrl;
    private javax.swing.JLabel txtAPI;
    private javax.swing.JTextField txtURLAPI;
    // End of variables declaration//GEN-END:variables
}
