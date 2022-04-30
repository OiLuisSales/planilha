/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Apontamento;


import static Codigo.ConexaoBD.getInsertSistema;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
public class PostCaPermissao {

    public static void post(String usuario, String senha, String server, String permissao) {
        try {
            PostCaPermissao.call_me(usuario, senha, server, permissao);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erro! Exceção na chamada do Post", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void call_me(String usuario, String senha, String server, String permissao) throws Exception {
        //desativando a validacao de certificado.
        SSLTool ssltool = new SSLTool();
        ssltool.disableCertificateValidation();
        server = server.replace("list","update");
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

        byte[] postDataBytes = permissao.getBytes("UTF-8");

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
}
