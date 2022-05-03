/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Codigo;

import com.cedarsoftware.util.io.JsonWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mawluis
 */
public class DumpCA {

    private static String jsonOTK = "";

    public static String getJsonOTK() {
        return jsonOTK;
    }

    public static void setJsonOTK(String jsonOTK) {
        DumpCA.jsonOTK = jsonOTK;
    }

    public static void dumpCAOTK() throws ClassNotFoundException, SQLException, IOException {

        jsonOTK = new String(Files.readAllBytes(Paths.get("src/DumpArquivos/------otk.json")));

        JSONObject obj = new JSONObject(jsonOTK);
        obj = obj.getJSONObject("values");

        JSONArray cliente = obj.getJSONArray("value");

        for (int i = 0; i < cliente.length(); i++) {

            JSONObject Iteracao = cliente.getJSONObject(i);//método de pegar json array

            String organization = Iteracao.getString("organization");
            String client_ident = Iteracao.getString("client_ident");
            String name = Iteracao.getString("name"); //sistema
            String description = Iteracao.getString("description");
            String type = Iteracao.getString("type");

            System.out.println(Iteracao.get("client_custom"));

            JSONObject client_customObj = new JSONObject(Iteracao.getString("client_custom"));//método de pegar json object

            JSONObject apisPermitidas = null;
            boolean semAPI = false;
            ConexaoBD conexaobd = new ConexaoBD();
            try {
                apisPermitidas = client_customObj.getJSONObject("APIsPermitidas");
            } catch (Exception ex) {
                semAPI = true;
            }
            if (semAPI) {
                conexaobd.insertBD("no_name", "Cadastro", "Cadastro", 0, name, client_ident, "dev", 1, type, description, organization);
                continue;
            } else {
                Iterator<String> keys = apisPermitidas.keys();

                while (keys.hasNext()) {
                    String URL = keys.next(); //URL da iteração
                    System.out.println("URLDaVez:" + URL);
String metodo = null;
                    if (URL.length()<3){
                        metodo = "ALL";
                    }else{
                       metodo = URL.substring(0, 3); 
                    }
                    

                    int cota = apisPermitidas.getJSONObject(URL).getInt("quota");
                    System.out.println("cota:" + cota);

                    switch (metodo) {
                        case "GET":
                            metodo = "GET";
                            URL = URL.replace(metodo, "");
                            break;
                        case "POS":
                            metodo = "POST";
                            URL = URL.replace(metodo, "");
                            break;
                        case "PUT":
                            metodo = "PUT";
                            URL = URL.replace(metodo, "");
                            break;
                        case "DEL":
                            metodo = "DELETE";
                            URL = URL.replace(metodo, "");
                            break;
                        case "ALL":
                            metodo = "ALL";
                            URL = URL.replace(metodo, "");
                            break;
                        case "PAT":
                            metodo = "PATCH";
                            URL = URL.replace(metodo, "");
                            break;
                        default:
                            metodo = "ALL";
                            break;
                    }

                    conexaobd.insertBD("no_name", URL, metodo, cota, name, client_ident, "dev", 1, type, description, organization);
                }

            }
        }

    }

}
