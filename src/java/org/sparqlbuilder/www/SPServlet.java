/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sparqlbuilder.www;

import org.sparqlbuilder.core.Path;
import org.sparqlbuilder.core.ClassLink;
import org.sparqlbuilder.core.Direction;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import javax.json.Json;

/**
 *
 * @author atsuko
 */
@WebServlet(name = "SPServlet", urlPatterns = {"/sparql"})
public class SPServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SPServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SPServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
/*    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }*/
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        response.setHeader("Access-Control-Max-Age", "-1");
        PrintWriter out = response.getWriter();
        String jpath = request.getParameter("path");
        System.out.println("Path");
        System.out.println(jpath);
        Path path = null;
        try {
            path = convertJ2Path(jpath);
        } catch (JSONException ex) {
            Logger.getLogger(SPServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        String query = null;
        try {
           query = convertPath2SPARQL(path, 100);
        } catch (Exception ex) {
            Logger.getLogger(SPServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.print(query);
    }
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private Path convertJ2Path(String jpath) throws JSONException{
  
        JSONObject object = new JSONObject(jpath);
   
    //   int width = Integer.parseInt(object.getJSONObject("width").toString());
        JSONArray classLinks = object.getJSONArray("classLinks");
        JSONObject jsonObject;
        List <ClassLink> list = new ArrayList<ClassLink>();
       
        for (int i=0;i<classLinks.length();i++){
            jsonObject = classLinks.getJSONObject(i);  
               
            String direction=jsonObject.getString("direction"); 
            Direction myDirection = null;
            if (direction.equals(Direction.forward.toString())){
                myDirection=Direction.forward;
            }else if (direction.equals(Direction.reverse.toString())){
                myDirection=Direction.reverse;
            }else if (direction.equals(Direction.both.toString()))                  
                myDirection=Direction.both;
           
                 
            String linkedLiteralDatatypeURI=null;
            //   linkedLiteralDatatypeURI = jsonObject.getString("linkedLiteralDatatypeURI");
            String linkedClassURI = jsonObject.getString("linkedClass"); 
            String propertyURI = jsonObject.getString("predicate");
              
        //    int numOfLinks = Integer.parseInt(jsonObject.getJSONObject("numOfLinks").toString());
        //    int numOfLinkedInstances = Integer.parseInt(jsonObject.getJSONObject("numOfLinkedInstances").toString()); 
        //    int numOfOriginInstances = Integer.parseInt(jsonObject.getJSONObject("numOfOriginInstances").toString());
        //    int numOfOriginClassInstances = Integer.parseInt(jsonObject.getJSONObject("numOfOriginInstances").toString());            
        //    int numOfLinkedClassInstances = Integer.parseInt(jsonObject.getJSONObject("numOfLinkedClassInstances").toString()); 
              
        //    ClassLink classLink =new ClassLink(propertyURI, linkedClassURI, linkedLiteralDatatypeURI, null, 
	//			 numOfLinks,  numOfOriginInstances,  numOfLinkedInstances,
	//			 numOfOriginClassInstances,  numOfLinkedClassInstances,
	//			false, false);
            ClassLink classLink =new ClassLink(propertyURI, linkedClassURI, linkedLiteralDatatypeURI, myDirection, 
				 0,  0,  0,
				 0,  0,
				false, false);
            System.out.println(classLink.getDirection().toString());  
            list.add(classLink);
        }
        String startClass = object.getString("startClass");
        Path path = new Path(startClass,  list,  0);
         
        return path;
    }
    
    private String convertPath2SPARQL(Path path, int limit) throws Exception{
        ArrayList<String> classname =new ArrayList<String>() ;
        if( path == null ){
	    throw new Exception("Path is null.");
        }
	String startClass = path.getStartClass();
	List<ClassLink> classLinks = path.getClassLinks();
        
	StringBuffer queryStr = new StringBuffer();
	StringBuffer selStr = new StringBuffer();
	StringBuffer whereStr = new StringBuffer();
	//	if(num==0){
	int num = classLinks.size();
	//	}
			
	queryStr.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n");
	queryStr.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
				
	selStr.append("SELECT ");
	whereStr.append("WHERE { \n");

	String properties = null;
	String objectClasses = null;
	String subjectClasses = null;
	Direction direction = null;
	int i = 0;
	int k = 0;
	for (ClassLink link :classLinks ){
	    properties = link.getPropertyURI();
	    objectClasses = link.getLinkedClassURI();
	    direction = link.getDirection();
			
	    if (i==0){
		subjectClasses = startClass;
            }           
	    classname.add(subjectClasses);
	    selStr.append("?c").append(i).append(" ");
	    selStr.append("?l").append(i).append(" ");
			
	    if(i == classLinks.size()){
		selStr.append("\n");
            }

            //if ( ){
	        whereStr.append("?c").append(i).
	        append(" rdf:type ").
	        append("<").
	        append(subjectClasses).
	        append(">").
	        append(".\n");
            //}	
	    whereStr.append("OPTIONAL{\n?c"+i+" rdfs:label ?l"+i+".}\n");

						
	    if(direction == Direction.forward){
		whereStr.append("?c").append(i).append(" ");
		whereStr.append("<").append(properties).append("> ");			
		whereStr.append("?c").append(i+1).append(".\n");			
	    }else{
		whereStr.append("?c").append(i+1).append(" ");
		whereStr.append("<").append(properties).append("> ");
		whereStr.append("?c").append(i).append(".\n");
	    }
			
	    subjectClasses = objectClasses;
	    i++;
	    k++;
	    if(k>=num){
		break;
	    }
	}
		
	selStr.append("?c").append(i).append(" \n");
	selStr.append("?l").append(i).append(" \n");
	whereStr.append("?c").append(i).append(" rdf:type ").
        append("<").
        append(subjectClasses).
	append(">").
	append(".\n");
	whereStr.append("OPTIONAL{\n?c"+i+" rdfs:label ?l"+i+".}\n");
        classname.add(subjectClasses);
	
					
	queryStr.append(selStr).append(whereStr);
		
	queryStr.append("}");
	//OPTIONAL
	queryStr.append("LIMIT ").append(Integer.toString(limit)).append("\n");
                        
        System.out.println(queryStr); 
               
        //rewrite Sparql                
	                 
        ArrayList<String> classname2 = new ArrayList<String>();
        for(int index=0;index<classname.size();index++){
            String  tmp=classname.get(index);
                  /*
                  int mark;
                  if((mark=tmp.indexOf("#"))!=-1)
                     classname2.add(tmp.substring(mark+1));
                  else classname2.add(tmp.substring(tmp.indexOf("/")+1));
                  */
                  // changed by Atsuko
            String[] sname1 = tmp.split("#");
            String[] sname2 = sname1[sname1.length -1].split("/");
            String[] sname3 = sname2[sname2.length -1].split(":");
            String cname = sname3[sname3.length -1].replaceAll("-", "");
            classname2.add(cname);
        }
        String query=queryStr.toString();
        for(int index=0;index<classname2.size();index++){
            String  original="c"+index;
            query= query.replaceAll(original, classname2.get(index));
        }
            query= query.replaceAll("\\?l","\\?label");
            System.out.println(query);
	return query;
    }
    
    private static String rewriteSparql(String query){
        StringBuffer tmp = new StringBuffer(query);
       int index= tmp.indexOf("WHERE");
       int begin=0,cnt=0;
       while(begin<index)
       {           
          begin= tmp.indexOf("?c", begin);
          cnt++;       
     
       }
        return null;
    }
    
/*
    private static List<String> convertJ2Path2(String jpath) throws JSONException{
         List <String> list = null;
           String temp =(String) jpath.subSequence(2, jpath.length()-2);
      //  if (temp.contains(","")) 
             list =Arrays.asList(temp.split("\",\""));
             return list;
  
       JSONArray classLinks=new JSONArray(jpath); 
       String string;
       
       for (int i=0;i<classLinks.length();i++) 
       {
            string = classLinks.getJSONObject(i).toString(); 
            if (string.contains(",")) {
             list =Arrays.asList(string.split(","));
              
       } else 
          throw new IllegalArgumentException("path error");
               
         }
       
       //   return list;    
    }
*/
    
    /* 
    private static String convertPath2SPARQL2(List<String> path) throws Exception{
        if( path == null ){
			throw new Exception("Path is null.");
		}
        
		              
		//List<String> classLinks = path.getClassLinks();
		
		StringBuffer queryStr = new StringBuffer();
		StringBuffer selStr = new StringBuffer();
		StringBuffer whereStr = new StringBuffer();
	//	if(num==0){
		//	int num = classLinks.size();
	//	}
			
		queryStr.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n");
		queryStr.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
				
		selStr.append("SELECT ");
		whereStr.append("WHERE { \n");

		String properties = null;
		String objectClasses = null;
		String subjectClasses = null;
		Direction direction = null;
		int i = 0;
		int k = 0;
                
                String startClass = path.get(path.size()-1);
                List<String> classLinks = null;
         //      ArrayList<String> array= (String[])path.toArray();
              
            //    classLinks.add(path.get(i));
               
                int num =(path.size()-1)/2;
                
		 for(int j=path.size()-2;j>0;j=j-2) 
		{
		//	properties = link.getPropertyURI();
		//	objectClasses = link.getLinkedClassURI();
		//	direction = link.getDirection();
                   properties =  path.get(j);
                   objectClasses = path.get(j-1);
                   direction = Direction.forward;
			
			if (i==0)
		    subjectClasses = startClass;
			
			selStr.append("?c").append(i).append(" ");
			selStr.append("?l").append(i).append(" ");
			
		//	if(i == path.size())
		//		selStr.append("\n");
			
			
			whereStr.append("?c").append(i).
			append(" rdf:type ").
			append("<").
			append(subjectClasses).
			append(">").
			append(".\n");
			
			whereStr.append("OPTIONAL{\n?c"+i+" rdfs:label ?l"+i+".}\n");

						
			if(direction == Direction.forward)
			{
			whereStr.append("?c").append(i).append(" ");
			whereStr.append("<").append(properties).append("> ");			
			whereStr.append("?c").append(i+1).append(".\n");			
			}
			else
			{
				whereStr.append("?c").append(i+1).append(" ");
				whereStr.append("<").append(properties).append("> ");
				whereStr.append("?c").append(i).append(".\n");
			}
			
			subjectClasses = objectClasses;
			i++;
			k++;
			if(k>=num){
				break;
			}
		}
		
		selStr.append("?c").append(i).append(" \n");
		selStr.append("?l").append(i).append(" \n");
		whereStr.append("?c").append(i).append(" rdf:type ").
		    append("<").
		    append(subjectClasses).
		    append(">").
			append(".\n");
		whereStr.append("OPTIONAL{\n?c"+i+" rdfs:label ?l"+i+".}\n");
	
					
		queryStr.append(selStr).append(whereStr);
		
		queryStr.append("}");
		//OPTIONAL
		queryStr.append("LIMIT 100\n");;
		
		System.out.println(queryStr);
		return queryStr.toString();
        
    
    }
    */
}
