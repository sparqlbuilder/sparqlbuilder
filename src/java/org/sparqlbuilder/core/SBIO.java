package org.sparqlbuilder.core;

import java.util.*;
import java.io.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author atsuko
 */
public class SBIO {
    static Map<String, ClassInfo> readCL(String filename){
        Map<String, ClassInfo> cl = new HashMap<String, ClassInfo>();
        File clfile = new File(filename);
        try{
            BufferedReader br = new BufferedReader(new FileReader(clfile));
            String buf;
            while ( (buf = br.readLine()) != null ){
                String[] data = buf.split("\t");// cl url \tab cl label \tab ep
                ClassInfo cli = cl.get(data[0]);
                if (cli == null){
                    cli = new ClassInfo(data[2], data[0], data[1], Integer.parseInt(data[3]));
                }else{
                    // KOKO
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return cl;
    }
    
    static Map<String, Set<String>> readCLRel(String filename){
        Map<String, Set<String>> clrel = new HashMap<String, Set<String>>();
        
        return clrel;
    }
}
