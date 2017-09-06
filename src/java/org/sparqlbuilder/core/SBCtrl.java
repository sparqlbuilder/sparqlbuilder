/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sparqlbuilder.core;

import java.util.*;
import java.io.*;
import javax.servlet.http.HttpServletRequest;
import javax.json.*;

/**
 *
 * @author atsuko
 */

public class SBCtrl {
    ClassGraph cg = null;
    Map<String, ClassInfo> cl = null;  // classURI->classInfo
    Map<String, Set<String>> clrel = null; // class->classes
    Map<String, ClassGraph> epcg = null; // ep->cg
    Set<String> yum = null; // yummy ep list
    Set<String> avep = null;
    
    String clfile = "cc/cl.txt";
    String clrelfile = "cc/clrel.txt";
    
    public SBCtrl(){ // default contructer
        // cl.txt -> cl
        cl = SBIO.readCL(clfile);
        // clrel.txt -> crel
        clrel = SBIO.readCLRel(clrelfile);
    }
    
    public JsonArray getEPs(){
        JsonBuilderFactory jbfactory = Json.createBuilderFactory(null);
        JsonArrayBuilder jab = jbfactory.createArrayBuilder();
        // KOKO
        JsonArray ja = jab.build();
        return ja;
    }
    
    public JsonArray getCLs(HttpServletRequest request){
        String c1 = request.getParameter("c1");
        JsonBuilderFactory jbfactory = Json.createBuilderFactory(null);
        JsonArrayBuilder jab = jbfactory.createArrayBuilder();
        if (c1 == null){
            Iterator<String> cit = cl.keySet().iterator();
            JsonObjectBuilder job = jbfactory.createObjectBuilder();
            while( cit.hasNext() ){
                String classuri = cit.next();
                ClassInfo classinfo = cl.get(classuri);
                ListIterator<String> eit = classinfo.endpoints.listIterator();
                while ( eit.hasNext() ){
                    String ep = eit.next();
                    job.add("ep", ep);
                    job.add("uri", classuri);
                    job.add("label", classinfo.prlabel);
                    job.add("number", classinfo.instances4e.get(ep));
                    jab.add(job);
                }
            }
        }else{
            //
            Set<String> classes = clrel.get(c1);
            Iterator<String> cit = classes.iterator();
            JsonObjectBuilder job = jbfactory.createObjectBuilder();
            while( cit.hasNext() ){
                String classuri = cit.next();
                ClassInfo classinfo = cl.get(classuri);
                ListIterator<String> eit = classinfo.endpoints.listIterator();
                while ( eit.hasNext() ){
                    String ep = eit.next();
                    job.add("ep", ep);
                    job.add("uri", classuri);
                    job.add("label", classinfo.prlabel);
                    job.add("number", classinfo.instances4e.get(ep));
                    jab.add(job);
                }
            }
        }       
        JsonArray ja = jab.build();
        return ja;
    }
    
    public String getPath(HttpServletRequest request){
        // get parameters
        String ep = request.getParameter("ep");
        String st = request.getParameter("startClass");
        String en = request.getParameter("endClass");
        Path[] paths = null;
        cg = new ClassGraph();
        // cg.setNodes();
        // cg.setEdges(start, end);
        paths = cg.getPaths(null, null);
        String jsonstr = "";
        if ( paths == null ){
        }else{
            jsonstr += "[";
  	    for(int i = 0; i< paths.length; i++){
	        if (i > 0 && paths[i] != null){
	            jsonstr += ",";
	        }
                if ( paths[i] == null ){
                    continue;
                }
                jsonstr += paths[i].toJSONString(cl);
	    }
	    jsonstr += "]";
        }
        return jsonstr;
    }
}
