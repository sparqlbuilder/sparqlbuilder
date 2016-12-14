/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sparqlbuilder.core;

import java.util.*;

/**
 *
 * @author atsuko
 */
public class ClassInfo {
    List<String> endpoints;
    String prlabel;
    List<String> labels;
    String url;
    Map<String, Integer> instances4e;
    int instances;
    
    public ClassInfo(String endpoint, String clURL, String label, int ent){
        url = clURL;
                
        endpoints = new LinkedList<String>();
        endpoints.add(endpoint);
        
        labels = new  LinkedList<String>();
        labels.add(label);
        
        instances4e = new HashMap<String, Integer>();
        instances4e.put(endpoint, ent);
        
        instances = ent;
    }
    
    public void addInfo(){
        
    }
    
}
