/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sparqlbuilder.core;

/**
 *
 * @author atsuko
 */

import com.hp.hpl.jena.rdf.model.*;
import java.util.*;
import java.io.*;

public class OWLLabelReader { 
    public static Map<String, String> readLabels(String dir){
        Map<String, String> labels = new HashMap<String, String> ();
        File fdir = new File(dir);
        if ( fdir.exists() ){
            File[] files = fdir.listFiles();
            for (int i = 0 ; i < files.length; i++ ){
                try{
                    BufferedReader in = new BufferedReader(new FileReader(files[i]));
                    String buf;
                    while( (buf = in.readLine()) != null){
                        String[] data1 = buf.split("[\\s]+");
                        String[] data2 = buf.split("\"");
                        labels.put(data1[0].substring(1,data1[0].length() - 1 ), data2[1]);
                    }
                    in.close();
                }catch(IOException e){
                    System.err.println(e);
                }
            }
        }
        return labels;
    }
    
    public static Map<String, String> readLabelsFromOWL(String dir){
        Map<String, String> labels = new HashMap<String, String> ();
        File fdir = new File(dir);
        if ( fdir.exists() ){
            File[] files = fdir.listFiles();
            for (int i = 0 ; i < files.length; i++ ){
                Model m = getModel(files[i]);
                Property p = m.getProperty("http://www.w3.org/2000/01/rdf-schema#label");
                ResIterator rit = m.listSubjectsWithProperty(p);
                while(rit.hasNext()){
                    Resource r = rit.next();
                    String url = r.getURI();
                    NodeIterator nit = m.listObjectsOfProperty(r,p);
                    while(nit.hasNext()){
                        RDFNode node = nit.next();
                        String lan = node.asLiteral().getLanguage();
                        if ( lan.equals("") || lan.equals("en")){
                            String label = node.asLiteral().getString();
                            labels.put(url, label); 
                            break;
                        }
                    }
                }
            }
        }
        return labels;
    }

    public static Model getModel(File file){
        Model model = ModelFactory.createDefaultModel();
        try{
          InputStream in = new FileInputStream(file);
          model.read(in, null);
        }catch(IOException e){
          e.printStackTrace();
        }
        return model;
    }
    
}
