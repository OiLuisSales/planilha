/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Apontamento;

import static Apontamento.GetCaPermissao.*;
import static Codigo.ConexaoBD.getInsertSistema;
import static Telas.TelaLogin.*;
import com.cedarsoftware.util.io.JsonWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.Base64;
import javax.swing.JOptionPane;
//import javax.xml.bind.DatatypeConverter;
//import java.util.Base64;
import org.json.JSONObject;

/**
 *
 * @author OI324558
 */
public class CRUDOTK {

    /*public static void post(String usuario, String senha, String server, String permissao) {
        try {
            CRUDOTK.call_me(usuario, senha, server, permissao);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erro! Exceção na chamada do Post", JOptionPane.ERROR_MESSAGE);
        }
    }*/
    public static void postOTK(String usuario, String senha, String server, String body) throws Exception {
        //desativando a validacao de certificado.
        SSLTool ssltool = new SSLTool();
        ssltool.disableCertificateValidation();
        server = server.replace("list", "update");
        URL obj = new URL(server);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        String loginPassword = usuario + ":" + senha;

        //String encoded = new sun.misc.BASE64Encoder().encode(loginPassword.getBytes()); //deprecated (BASE64Encoder is internal proprietary API and may be removed in a future release)
        String encoded = Base64.getEncoder().encodeToString(loginPassword.getBytes("utf-8")); //java 8
        //String encoded = DatatypeConverter.printBase64Binary(loginPassword.getBytes()); //encode String
        //byte[] bytearray = DatatypeConverter.parseBase64Binary(encoded); //encode bytearray

        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Basic " + encoded);
        con.setRequestProperty("clientName", getInsertSistema());
        con.setRequestProperty("clientAttr", "custom");

        byte[] postDataBytes = body.getBytes("UTF-8");

        con.setDoOutput(true);
        con.getOutputStream().write(postDataBytes);
        //con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        System.out.println("\nEnviando 'PUT' request para o URL: " + server);
        System.out.println("Response Code : " + responseCode);//esperado 200
        BufferedReader in = null;
        StringBuffer response = null;
        try {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        System.out.println("Resultado após ler o Json response:");
        System.out.println(response);
    }

    public static void getOTK(String client_key) throws Exception {
        //desativando a validacao de certificado.
        SSLTool ssltool = new SSLTool();
        ssltool.disableCertificateValidation();
        client_key="?client_key="+client_key;
        URL obj = new URL(getServer()+getUrlOTK()+client_key);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        BufferedReader inB = null;
        StringBuffer response = null;
        try {
            con.setRequestProperty("token", getToken());
            con.setRequestMethod("GET");
            //con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int respCode = con.getResponseCode();
            setResponseCode(respCode);

            System.out.println("\nEnviando 'GET' request para o URL: " + getServer()+getUrlOTK()+client_key);
            System.out.println("Response Code : " + responseCode);//esperado 2xx

            inB = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            response = new StringBuffer();
            while ((inputLine = inB.readLine()) != null) {
                response.append(inputLine);
            }
            System.out.println("result inputLine:");//Printando a string crua.
            System.out.println(inputLine);
            System.out.println("----------====================-----------------------");
        } catch (IOException ex) {
          //JOptionPane.showMessageDialog(null, "Problemas com os parâmetros enviados!\n" + ex.getMessage(), "Erro! IOException", JOptionPane.ERROR_MESSAGE);//com URL
            JOptionPane.showMessageDialog(null, "Problemas com os parâmetros enviados!\n", "Erro! IOException", JOptionPane.ERROR_MESSAGE);//sem URL
            return;
        } finally {
            if (inB != null) {
                inB.close();
            }
        }
        System.out.println("result response:");//Printando a string crua.
        String responseSTR = JsonWriter.formatJson(response.toString()); //indentando   
        System.out.println(responseSTR);
        System.out.println("----------====================-----------------------");
    }
}
