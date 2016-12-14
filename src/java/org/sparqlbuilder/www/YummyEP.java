/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sparqlbuilder.www;


import java.util.*;
import java.io.*;
import java.net.*;
import javax.json.*;

/**
 *
 * @author atsuko
 */

public class YummyEP {
    static Set<String> getYummyEP(String alive){
        Set<String> yumep = new HashSet<String>();
        StringBuilder api = new StringBuilder("http://d.umaka.dbcls.jp/api/endpoints/search?");
        api.append("alive_rate_lower=");
        api.append(alive);
        api.append("&");
        api.append("execution_time_upper=");
        api.append("10");
        StringBuilder out = new StringBuilder("");
        try{
            URL url = new URL(api.toString());
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader isr = new InputStreamReader(c.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String buf;
                while ((buf = br.readLine()) != null) {
                    out.append(buf);                    
                }
                br.close();
            }
            c.disconnect();

            JsonReader jsonReader = Json.createReader(new StringReader(out.toString()));
            JsonArray ja = jsonReader.readArray();
            int an = ja.size();
            for (int i = 0 ; i < an; i++ ){
                JsonObject jo = ja.getJsonObject(i);
                JsonString jurl = jo.getJsonString("url");
                yumep.add(jurl.getString());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return yumep;
    }
}
