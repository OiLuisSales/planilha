/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Apontamento;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.Base64;
//import java.util.Base64;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
//import javax.xml.bind.DatatypeConverter;
import org.json.JSONObject;
import static Telas.TelaLogin.*;
import com.cedarsoftware.util.io.JsonWriter;


/**
 *
 * @author OI324558
 */
public class GetCaPermissao {

    public static String server = "https://apimhml.oi.net.br";
    public static String api = "/teste/getclient";
    
    
    
    
    
    public void VerificarCredenciais(String usuario, String senha) {
        try {
            GetCaPermissao.call_me(usuario, senha);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro no método GET. " + e.getMessage(), "Erro! Exception!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void call_me(String usuario, String senha) throws Exception {
        //desativando a validacao de certificado.
        SSLTool ssltool = new SSLTool();
        ssltool.disableCertificateValidation();

        String url = (server+api);
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        String loginPassword = usuario + ":" + senha;
        //String encoded = new sun.misc.BASE64Encoder().encode(loginPassword.getBytes()); //deprecated (BASE64Encoder is internal proprietary API and may be removed in a future release)
        String encoded = Base64.getEncoder().encodeToString(loginPassword.getBytes("utf-8")); //java 8
        //String encoded = DatatypeConverter.printBase64Binary(loginPassword.getBytes()); //encode String
        //byte[] bytearray = DatatypeConverter.parseBase64Binary(encoded); //encode bytearray
        BufferedReader inB = null;
        StringBuffer response = null;
        try {
            con.setRequestProperty("Authorization", "Basic " + encoded);
            con.setRequestMethod("GET");
            //con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int respCode = con.getResponseCode();
            setResponseCode(respCode);

            System.out.println("\nEnviando 'GET' request para o URL: " + url);
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
            Logger.getLogger(GetCaPermissao.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Problemas com os parâmetros enviados!\n" + ex.getMessage(), "Erro! IOException", JOptionPane.ERROR_MESSAGE);
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