/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Codigo;

import Apontamento.CRUDOTK;
import static Apontamento.CRUDOTK.APIsPermitidas;
import Telas.TelaPrincipal;
import static Telas.TelaPrincipal.jsonTxtArea;
import static Telas.TelaPrincipal.juntarAPIsPermitidas;
import static Telas.TelaPrincipal.setarJsonTxtArea;
import com.cedarsoftware.util.io.JsonWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.json.JSONObject;

/**
 *
 * @author Mawluis
 */
public class ConexaoBD {

    public static String insertURL = "";
    public static String insertMetodo = "";
    public static int insertCota = 0;
    public static String insertAPI = "";
    public static int INSERCAO = 0;
    public static String insertSistema = "";
    public static String insertUser = "";
    public static String insertClient_key = "";
    public static String insertType = "";
    public static String insertDescription = "";
    public static String insertOrg = "";
    private static String tableBD = "";

    public static String getTableBD() {
        return tableBD;
    }

    public static void setTableBD(String tableBD) {
        ConexaoBD.tableBD = tableBD;
    }

    public static String getInsertType() {
        return insertType;
    }

    public static void setInsertType(String insertType) {
        ConexaoBD.insertType = insertType;
    }

    public static String getInsertDescription() {
        return insertDescription;
    }

    public static void setInsertDescription(String insertDescription) {
        ConexaoBD.insertDescription = insertDescription;
    }

    public static String getInsertOrg() {
        return insertOrg;
    }

    public static void setInsertOrg(String insertOrg) {
        ConexaoBD.insertOrg = insertOrg;
    }
    public static boolean insertApagar = false;

    public static boolean isInsertApagar() {
        return insertApagar;
    }

    public static void setInsertApagar(boolean insertApagar) {
        ConexaoBD.insertApagar = insertApagar;
    }

    public static String getInsertClient_key() {
        return insertClient_key;
    }

    public static void setInsertClient_key(String insertClient_key) {
        ConexaoBD.insertClient_key = insertClient_key;
    }

    public static String getInsertUser() {
        return insertUser;
    }

    public static void setInsertUser(String insertUser) {
        ConexaoBD.insertUser = insertUser;
    }

    public static String getInsertSistema() {
        return insertSistema;
    }

    public static void setInsertSistema(String insertSistema) {
        ConexaoBD.insertSistema = insertSistema;
    }

    public static int getINSERCAO() {
        return INSERCAO;
    }

    public static void setINSERCAO(int INSERCAO) {
        ConexaoBD.INSERCAO = INSERCAO;
    }

    public String getClassForName() {
        return ClassForName;
    }

    public void setClassForName(String ClassForName) {
        this.ClassForName = ClassForName;
    }

    public static String getInsertAPI() {
        return insertAPI;
    }

    public static void setInsertAPI(String insertAPI) {
        ConexaoBD.insertAPI = insertAPI;
    }

    public static String getInsertURL() {
        return insertURL;
    }

    public static void setInsertURL(String insertURL) {
        ConexaoBD.insertURL = insertURL;
    }

    public static String getInsertMetodo() {
        return insertMetodo;
    }

    public static void setInsertMetodo(String insertMetodo) {
        ConexaoBD.insertMetodo = insertMetodo;
    }

    public static int getInsertCota() {
        return insertCota;
    }

    public static void setInsertCota(int insertCota) {
        ConexaoBD.insertCota = insertCota;
    }

    String UrlBD = "jdbc:postgresql://ec2-52-203-118-49.compute-1.amazonaws.com:5432/dffsh0qmr4rjur?sslmode=require"; //verificar sslmode=require
    String userBD = "hbnfgvanlkunmq";
    String PassBD = "854534ef7de1b5e3a8f2b358ed972072fe34e0fc33cf4f7329eb9cff7eb497ca";
    String ClassForName = "org.postgresql.Driver"; //String obrigatória no POSTGRESQL

    MatrizDinamica<String> matriz = new MatrizDinamica<String>();
    String ultimoUrl = "";
    int matrizURL = -1;
    int matrizMetodo = 0;

    public boolean buscarClient_key(String sisOuClientId, String tabela, boolean ativo) throws ClassNotFoundException, Exception {
        boolean notFound = true;
        try {
            //TODO verificar meio de buscar quando não há URLs no cliente. No caso por exemplo de ter apagado todos os URLs ou só tiver o URL de cadastro(dump)

            ResultSet rs = null;

            Class.forName(ClassForName);//verificar
            Connection con = DriverManager.getConnection(UrlBD, userBD, PassBD);
            PreparedStatement pst1 = con.prepareStatement("SELECT * FROM \"" + tabela + "\" WHERE LOWER(\"" + sisOuClientId + "\") =LOWER(?) AND \"ATIVO\" = ? ORDER BY \"URL\"");
            pst1.setString(1, getInsertClient_key());
            pst1.setBoolean(2, ativo);
            rs = pst1.executeQuery();

            if (rs.next()) {
                System.out.println(sisOuClientId + " encontrado.");
                notFound = false;
                setInsertSistema(rs.getObject(5).toString());//atribuindo nome do cliente caso a query tenha sido por client_id
                setInsertClient_key(rs.getObject(6).toString());//atribuindo client key
                setInsertType(rs.getObject(11).toString());//atribuindo confidential type
                setInsertDescription(rs.getObject(12).toString());//atribuindo Descrição do OTK
                setInsertOrg(rs.getObject(13).toString());//atribuindo org (APPID)
                try {
                    CRUDOTK.getOTK(getInsertClient_key());//pegar APIsPermitidas legado. Da erro qdo client_key n for o client_ident. TODO API que retorna client_key vigente e dar UPDATE no sistema
                } catch (Exception ex) {
                    APIsPermitidas = "";
                    JOptionPane.showMessageDialog(null, "client_key diferente do client_ident\n" + (ex), "Erro! IOException:", JOptionPane.ERROR_MESSAGE);
                }
                preencherAPIsPermitidasLegado();//TODO verificar melhoria utilizando a TelaPrincipal.juntarAPIsPermitidas que é similar a essa.
                if (ativo) {
                    do {
                        populaArray(rs);
                    } while (rs.next());
                    suprirRecursos();
                } else {
                    JOptionPane.showMessageDialog(null, "O " + getInsertSistema() + " não possui permissões ativas, digite um URL para conceder", "Sem permissões", JOptionPane.INFORMATION_MESSAGE);
                    if (APIsPermitidas.isEmpty()){
                        setarJsonTxtArea("{\"APIsPermitidas_v2\":{}}");
                    }                    
                }
            }

            rs.close();
            pst1.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConexaoBD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return notFound;
    }

    public void preencherAPIsPermitidasLegado() {//TODO verificar melhoria utilizando a TelaPrincipal.juntarAPIsPermitidas que é similar a essa.
        String custom_key = "";

        if (CRUDOTK.APIsPermitidas.isEmpty()) {
            System.out.println("sem apispermitidas legado");
        } else {

            JSONObject APIsPermitidas = new JSONObject(CRUDOTK.APIsPermitidas);

            JSONObject custom_keyObj = new JSONObject();

            custom_keyObj.put("APIsPermitidas", APIsPermitidas);

            custom_key = custom_keyObj.toString();
            custom_key = JsonWriter.formatJson(custom_key); //indentando   
            System.out.println("custom_key:" + custom_key);
            TelaPrincipal.setarJsonTxtArea(custom_key);
        }
    }

    public void suprirRecursos() {
        for (int i = 0; i < matriz.getSizeX(); i++) {
            System.out.println(matriz.get(i, 0));
            System.out.println("matriz.getSizeX:" + matriz.getSizeX());
            for (int j = 0; j < matriz.getSizeY(i); j++) {
                String elemento = matriz.get(i, j); // obtendo o elementos
                System.out.println("elemento:" + elemento);
                System.out.println("matriz.getSizeY:" + matriz.getSizeY(i));
            }
        }

        String json2 = TelaPrincipal.gerarJson2(matriz);
        json2 = TelaPrincipal.juntarAPIsPermitidas(json2);
        TelaPrincipal.setarJsonTxtArea(json2);
        TelaPrincipal.alimentaJtable2(matriz);
    }

    public void populaArray(ResultSet rs1) throws SQLException {
        int ColunaAPI = 1;
        int ColunaURL = 2;
        int ColunaMetodo = 3;
        int ColunaQuota = 4;
        int ColunaSistema = 5;
        int ColunaClient_key = 6;
        int ColunaType = 11;
        int ColunaDescription = 12;
        int ColunaOrg = 13;

        String rsAPI = rs1.getObject(ColunaAPI).toString();
        String rsURL = rs1.getObject(ColunaURL).toString();
        String rsMetodo = rs1.getObject(ColunaMetodo).toString();
        String rsQuota = rs1.getObject(ColunaQuota).toString();
        String rsSistema = rs1.getObject(ColunaSistema).toString();
        String rsClient_key = rs1.getObject(ColunaClient_key).toString();
        String rsType = rs1.getObject(ColunaType).toString();
        String rsDescription = rs1.getObject(ColunaDescription).toString();
        String rsOrg = rs1.getObject(ColunaOrg).toString();

        setInsertType(rsType);
        setInsertDescription(rsDescription);
        setInsertOrg(rsOrg);
        setInsertSistema(rsSistema);
        setInsertClient_key(rsClient_key);

        System.out.println("API:" + rsAPI + " ||| URL:" + rsURL + " ||| Metodo:" + rsMetodo + " ||| Quota:" + rsQuota);

        if (!ultimoUrl.equals(rsURL)) {//senão tem esse URL no vetor.
            matrizURL++;
            matrizMetodo = 0;
            matriz.set(matrizURL, matrizMetodo, rsURL); // inserindo o elemento
            matriz.set(matrizURL, ++matrizMetodo, "  {\"" + rsMetodo + "\":{\"quota\":" + rsQuota + "}   }  "); // inserindo o elemento
            ultimoUrl = rsURL;
        } else {//já tem URL no vetor. Adicionar apenas y: [x,y]
            matriz.set(matrizURL, ++matrizMetodo, "  {\"" + rsMetodo + "\":{\"quota\":" + rsQuota + "}   }  "); // inserindo o elemento
        }
    }

    public void insertBD(String API, String URL, String METODO, int QUOTA, String SISTEMA, String CLIENT_KEY, String USER, int INSERCAO, boolean ATIVO, String TYPE, String DESCRIPTION, String ORG, String tabela, String logMessage) throws ClassNotFoundException, SQLException {//TODO insert update ATIVO para 0.
        Class.forName(ClassForName);
        Connection con = DriverManager.getConnection(UrlBD, userBD, PassBD);
        String insert = "INSERT INTO \"public\".\"" + tabela + "\" (\"API\", \"URL\", \"METODO\", \"QUOTA\", \"SISTEMA\", \"CLIENT_KEY\", \"USER\", \"INSERCAO\", \"ATIVO\", \"TYPE\", \"DESCRIPTION\", \"ORG\", \"LOG\") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING *";
        PreparedStatement pst1 = con.prepareStatement(insert, PreparedStatement.RETURN_GENERATED_KEYS);

        pst1.setString(1, API); //API
        pst1.setString(2, URL); //URL
        pst1.setString(3, METODO); //METODO
        pst1.setInt(4, QUOTA); //QUOTA
        pst1.setString(5, SISTEMA); //SISTEMA
        pst1.setString(6, CLIENT_KEY); //CLIENT_KEY
        pst1.setString(7, USER); //USER
        pst1.setInt(8, INSERCAO); //INSERCAO
        pst1.setBoolean(9, ATIVO); //ATIVO
        pst1.setString(10, TYPE); //TYPE
        pst1.setString(11, DESCRIPTION); //DESCRIPTION
        pst1.setString(12, ORG); //ORG
        pst1.setString(13, logMessage); //LOG
        pst1.executeUpdate();

        ResultSet rs = pst1.getGeneratedKeys();
        int i = 0;
        while (rs.next()) {
            i++;
            System.out.println(i + ".1:" + (rs.getObject(1).toString()));//coluna:API
            System.out.println(i + ".2:" + (rs.getObject(2).toString()));//coluna:URL
            System.out.println(i + ".3:" + (rs.getObject(3).toString()));//coluna:METODO
            System.out.println(i + ".4:" + (rs.getObject(4).toString()));//coluna:QUOTA
            System.out.println(i + ".5:" + (rs.getObject(5).toString()));//coluna:SISTEMA
            System.out.println(i + ".6:" + (rs.getObject(6).toString()));//coluna:CLIENT_KEY
            System.out.println(i + ".7:" + (rs.getObject(7).toString()));//coluna:USER
            System.out.println(i + ".8:" + (rs.getObject(8).toString()));//coluna:DATE  - timestamp gerar automaticamente pelo banco.
            System.out.println(i + ".9:" + (rs.getObject(9).toString()));//coluna:INSERCAO
            System.out.println(i + ".10:" + (rs.getObject(10).toString()));//coluna:ATIVO
        }

        System.out.println("Linhas inseridas:" + i);

        pst1.close();
        rs.close();
        con.close();

    }

    public String buscarAPI(String URL, String tabela) throws ClassNotFoundException, SQLException {//responsável por verificar se já existe o nome da URL da API exemplo ConsultarTopOfertas. 
        try {
            Class.forName(ClassForName);
            Connection con = DriverManager.getConnection(UrlBD, userBD, PassBD);
            PreparedStatement pst1 = con.prepareStatement("SELECT \"" + tabela + "\".\"API\" from \"" + tabela + "\" WHERE LOWER(\"URL\")=LOWER(?)");

            pst1.setString(1, URL);

            ResultSet rs1 = pst1.executeQuery();

            if (rs1.next()) {
                setInsertAPI(rs1.getObject(1).toString()); //Populando o nome do API encontrado a partir do URL
            } else {
                setInsertAPI(JOptionPane.showInputDialog("Novo URL identificado. " + "Favor cadastrar respectivo nome do URL " + URL + "."));
            }
            rs1.close();
            pst1.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getInsertAPI();

    }

    public ArrayList<String> retornarSistemas(String URL, String tabela) throws ClassNotFoundException, SQLException {
        ArrayList<String> sistemas = new ArrayList<String>();
        try {
            Class.forName(ClassForName);
            Connection con = DriverManager.getConnection(UrlBD, userBD, PassBD);
            PreparedStatement pst1 = con.prepareStatement("SELECT DISTINCT \"SISTEMA\" from \"" + tabela + "\" WHERE LOWER(\"URL\") = LOWER(?)");

            pst1.setString(1, URL);

            ResultSet rs1 = pst1.executeQuery();

            while (rs1.next()) {
                sistemas.add(rs1.getObject(1).toString());
            }
            rs1.close();
            pst1.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sistemas;

    }

    public int maxInsercao(String tabela) throws ClassNotFoundException {//Responsável pela coluna INSERCAO

        try {
            Class.forName(ClassForName);//verificar
            Connection con = DriverManager.getConnection(UrlBD, userBD, PassBD);
            PreparedStatement pst1 = con.prepareStatement("Select Max(\"INSERCAO\") from \"" + tabela + "\"");

            ResultSet rs1 = pst1.executeQuery();

            if (rs1.next()) {
                int insercao = 0;
                insercao = Integer.parseInt(rs1.getObject(1).toString());
                insercao++; //colocando o próximo insercao na coluna
                setINSERCAO(insercao);
            }

            rs1.close();
            pst1.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getINSERCAO();
    }

    public String getAPI(String url, String tabela) throws ClassNotFoundException {//Captura API a partir do URL

        try {
            Class.forName(ClassForName);//verificar
            Connection con = DriverManager.getConnection(UrlBD, userBD, PassBD);
            PreparedStatement pst1 = con.prepareStatement("select * from \"public\".\"" + tabela + "\" where \"lower\"(\"URL\") = lower(?) ORDER BY \"DATE\" desc");//pega o registro mais recente deste URL

            pst1.setString(1, url);

            ResultSet rs1 = pst1.executeQuery();

            if (rs1.next()) {
                setInsertAPI(rs1.getObject(1).toString());
            }

            rs1.close();
            pst1.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getInsertAPI();
    }

    public void inativador(String URL, String CLIENT_KEY, String tabela, String logMessage) throws ClassNotFoundException, SQLException {//query para desativar tudo que tem no banco com esse URL e client_key antes de colocar os métodos novos. 
        Class.forName(ClassForName);
        Connection con = DriverManager.getConnection(UrlBD, userBD, PassBD);
        String update = "UPDATE \"public\".\"" + tabela + "\" SET \"ATIVO\" = '0' WHERE LOWER(\"URL\") = LOWER(?) AND LOWER(\"CLIENT_KEY\") = LOWER(?)";
        PreparedStatement pst1 = con.prepareStatement(update);

        if (isInsertApagar()) {//Se não tem mais métodos, dar update no ATIVO = f em todos métodos desse URL, inserir linha com registro da exclusão
            String insert = "INSERT INTO \"public\".\"" + tabela + "\" (\"API\", \"URL\", \"METODO\", \"QUOTA\", \"SISTEMA\", \"CLIENT_KEY\", \"USER\", \"INSERCAO\", \"ATIVO\", \"TYPE\", \"DESCRIPTION\", \"ORG\", \"LOG\") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING *";
            PreparedStatement pst2 = con.prepareStatement(insert, PreparedStatement.RETURN_GENERATED_KEYS);

            pst2.setString(1, getInsertAPI()); //API
            pst2.setString(2, getInsertURL()); //URL
            pst2.setString(3, "URL deletado"); //METODO
            pst2.setInt(4, 0); //QUOTA
            pst2.setString(5, getInsertSistema()); //SISTEMA
            pst2.setString(6, CLIENT_KEY); //CLIENT_KEY
            pst2.setString(7, getInsertUser()); //USER
            pst2.setInt(8, getINSERCAO()); //INSERCAO
            pst2.setBoolean(9, false); //ATIVO
            pst2.setString(10, getInsertType()); //TYPE
            pst2.setString(11, getInsertDescription()); //DESCRIPTION
            pst2.setString(12, getInsertOrg()); //ORG
            pst2.setString(13, logMessage); //LOG
            pst2.executeUpdate();

            ResultSet rs = pst2.getGeneratedKeys();
            int i = 0;
            while (rs.next()) {
                i++;
                System.out.println(i + ".1:" + (rs.getObject(1).toString()));//coluna:API
                System.out.println(i + ".2:" + (rs.getObject(2).toString()));//coluna:URL
                System.out.println(i + ".3:" + (rs.getObject(3).toString()));//coluna:METODO
                System.out.println(i + ".4:" + (rs.getObject(4).toString()));//coluna:QUOTA
                System.out.println(i + ".5:" + (rs.getObject(5).toString()));//coluna:SISTEMA
                System.out.println(i + ".6:" + (rs.getObject(6).toString()));//coluna:CLIENT_KEY
                System.out.println(i + ".7:" + (rs.getObject(7).toString()));//coluna:USER
                System.out.println(i + ".8:" + (rs.getObject(8).toString()));//coluna:DATE  - timestamp gerar automaticamente pelo banco.
                System.out.println(i + ".9:" + (rs.getObject(9).toString()));//coluna:INSERCAO
                System.out.println(i + ".10:" + (rs.getObject(10).toString()));//coluna:ATIVO
                System.out.println(i + ".11:" + (rs.getObject(11).toString()));//coluna:TYPE
                System.out.println(i + ".12:" + (rs.getObject(12).toString()));//coluna:DESCRIPTION
                System.out.println(i + ".13:" + (rs.getObject(13).toString()));//coluna:ORG
                System.out.println(i + ".14:" + (rs.getObject(14).toString()));//coluna:LOG
            }
            rs.close();
            System.out.println("Linhas inseridas:" + i);

            pst2.close();
            setInsertApagar(false);
        }

        pst1.setString(1, URL);
        pst1.setString(2, CLIENT_KEY);
        pst1.executeUpdate();

        pst1.close();
        con.close();

    }
}
