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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.swing.JOptionPane;
//import javax.xml.bind.DatatypeConverter;
//import java.util.Base64;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONException;

/**
 *
 * @author OI324558
 */
public class CRUDOTK {

    public static String APIsPermitidas = "";

    /*public static void post(String usuario, String senha, String server, String permissao) {
        try {
            CRUDOTK.call_me(usuario, senha, server, permissao);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erro! Exceção na chamada do Post", JOptionPane.ERROR_MESSAGE);
        }
    }*/
    public static void putOTK(String client_key, String sistema, String org, String description, String type, String client_custom, String usuario, String server) throws IOException, InterruptedException {

        /*o ignorador de SSL parou de ignorar esse API na nova versão do JDK 18.
        client_key = "?client_ident=" + client_key;//mudando custom key, não a credencial. Para mudar credencial (callback, scope, custom_key_info, etc.) implementar query param ?client_key=
        String URL = server + getUrlOTK() + client_key + "&name=" + sistema + "&org=" + org + "&description=" + description + "&type=" + type;
        URL = URL.replaceAll(" ", "+");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .PUT(HttpRequest.BodyPublishers.ofString(client_custom))
                .header("token", getToken())
                .header("ambiente", server)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
         */
        //desativando a validacao de certificado.
        SSLTool ssltool = new SSLTool();
        ssltool.disableCertificateValidation();
        client_key = "?client_ident=" + client_key;
        String url = server + getUrlOTK() + client_key + "&name=" + sistema + "&org=" + org + "&description=" + description + "&type=" + type;
        url = url.replaceAll(" ", "+");
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setDoOutput(true);//parâmetro necessário para conseguir fazer stream no body

        BufferedReader inB = null;
        StringBuffer response = null;
        try {
            con.setRequestProperty("token", getToken());
            con.setRequestProperty("ambiente", getServer());
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("PUT");

            DataOutputStream out = new DataOutputStream(con.getOutputStream());//stream no body
            out.writeBytes(client_custom);
            out.flush();
            out.close();

            int respCode = con.getResponseCode();
            setResponseCode(respCode);

            System.out.println("\nEnviando 'PUT' request para o URL: " + url);
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
            JOptionPane.showMessageDialog(null, "Problemas com os parâmetros enviados!\n" + (ex), "Erro! IOException:", JOptionPane.ERROR_MESSAGE);//sem URL
            throw new IOException(ex);
            //return;
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

    public static void getOTK(String client_key) throws Exception {
        //desativando a validacao de certificado.
        SSLTool ssltool = new SSLTool();
        ssltool.disableCertificateValidation();
        client_key = "?client_key=" + client_key;
        URL obj = new URL(getServer() + getUrlOTK() + client_key);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        BufferedReader inB = null;
        StringBuffer response = null;
        try {
            con.setRequestProperty("token", getToken());
            con.setRequestProperty("ambiente", getServer());
            con.setRequestMethod("GET");
            //con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int respCode = con.getResponseCode();
            setResponseCode(respCode);

            System.out.println("\nEnviando 'GET' (getOTK) request para o URL: " + getServer() + getUrlOTK() + client_key);
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
            JOptionPane.showMessageDialog(null, "Problemas com os parâmetros enviados no getOTK!\n", "Erro! IOException:", JOptionPane.ERROR_MESSAGE);//sem URL
            System.out.println("exceção no getOTK"+ex);
            throw new IOException(ex);
            //return;
        } finally {
            if (inB != null) {
                inB.close();
            }
        }
        System.out.println("result response:");//Printando a string crua.
        String responseSTR = JsonWriter.formatJson(response.toString()); //indentando   
        System.out.println(responseSTR);
        System.out.println("----------====================-----------------------");
        pegarAPIsPermitidas(responseSTR);
    }

    public static void pegarAPIsPermitidas(String json) {
        APIsPermitidas = "";
        JSONObject client_custom = new JSONObject(json);
        //client_custom = client_custom.getJSONObject("values");.getJSONObject("value").getJSONObject("client_custom");
        client_custom = client_custom.getJSONObject("values");
        client_custom = client_custom.getJSONObject("value");
        json = client_custom.get("client_custom").toString();
        client_custom = new JSONObject(json);
        try {
            APIsPermitidas = client_custom.getJSONObject("APIsPermitidas").toString();
            System.out.println("APIsPermitidas:" + APIsPermitidas);
        } catch (JSONException ex) {
            System.out.println("Não possui APIsPermitidas legado");
            APIsPermitidas = "";
        }
    }

    public static String getAPI(String UrlApi) throws Exception {
        //desativando a validacao de certificado.
        SSLTool ssltool = new SSLTool();
        ssltool.disableCertificateValidation();
        URL obj = new URL(getServer() + getUrlOTK());
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        BufferedReader inB = null;
        StringBuffer response = null;
        try {
            con.setRequestProperty("token", getToken());
            con.setRequestProperty("ambiente", getServer());
            con.setRequestProperty("url", UrlApi);
            con.setRequestMethod("GET");
            //con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int respCode = con.getResponseCode();
            setResponseCode(respCode);

            System.out.println("\nEnviando 'GET' request para o URL: " + getServer() + getUrlOTK());
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
            System.out.println("Deu ruim no método getAPI");
            //JOptionPane.showMessageDialog(null, "Problemas com os parâmetros enviados!\n" + ex.getMessage(), "Erro! IOException", JOptionPane.ERROR_MESSAGE);//com URL
            JOptionPane.showMessageDialog(null, "Problemas com os parâmetros enviados no getAPI!\n" + (ex), "Erro! IOException:", JOptionPane.ERROR_MESSAGE);//sem URL
            throw new IOException(ex);
            //return;
        } finally {
            if (inB != null) {
                inB.close();
            }
        }
        System.out.println("result response:");//Printando a string crua.
        System.out.println(response.toString());
        System.out.println("----------====================-----------------------");
        return response.toString();
    }
}
