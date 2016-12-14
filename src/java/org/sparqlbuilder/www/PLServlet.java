/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sparqlbuilder.www;

import org.sparqlbuilder.core.Path;
import org.sparqlbuilder.core.ClassLink;
import org.sparqlbuilder.core.ClassGraph;
import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
//import javax.json.*;
import org.json.JSONObject;

/**
 *
 * @author atsuko
 */
@WebServlet(name = "PLServlet", urlPatterns = {"/plist"})
public class PLServlet extends HttpServlet {

    private static final String FILENAME = "ddata/";    
    //private static final String FILENAME = "cdata/";    
    
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
            out.println("<title>Servlet PLServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PLServlet at " + request.getContextPath() + "</h1>");
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        response.setHeader("Access-Control-Max-Age", "-1");
	PrintWriter out = response.getWriter();
        String ep = request.getParameter("ep");
        String st = request.getParameter("startclass");
        String en = request.getParameter("endclass");
        
        // String
        String uri = request.getQueryString();
        System.out.println(uri);
        
        HttpSession session = request.getSession();
                
        //QueryPathGenerator qpg = new QueryPathGenerator(ep);
        //SClass[] classes = qpg.getClasses(null);
        //qpg.setClassLabels(classes);
        if ( ep == null || st == null || en == null){
            return;
        }
        
        String epcr = ep.split("//")[1].replace("/", "_").replace("#", "-").concat(".cl");
        File epcrfile = new File("./cc/".concat(epcr));
        File cl2file = new File("./cctmp/cl2.txt");
        Set<String> cl = new HashSet<String>();
        Set<String> cr = new HashSet<String>();
        Map<String, String> clabels = new HashMap<String, String>();
        Map<String, Integer> cent = new HashMap<String, Integer>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(epcrfile));
            String buf;
            while( (buf = br.readLine()) != null){
                String data[] = buf.split("\t");
                cl.add(data[0]);
                cl.add(data[1]);
                cr.add(buf);
            }
            br.close();
            br = new BufferedReader(new FileReader(cl2file));
            while( (buf = br.readLine()) != null){
                String data[] = buf.split("\t");
                clabels.put(data[1], data[0]);
                cent.put(data[1], Integer.parseInt(data[2]));
            }
            br.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        ClassGraph cg = new ClassGraph(ep, cl, cr, cent);
        Path[] paths = null;
        paths = cg.getPaths(st, en); 
        if ( paths == null ){
            out.print(" ");
        }else{
            String jsonstr = "[";
  	    for(int i = 0; i< paths.length; i++){
	        if (i > 0 && paths[i] != null){
	            jsonstr += ",";
	        }
                if ( paths[i] == null ){
                    continue;
                }
                jsonstr += this.getJsonPathString(paths[i], clabels);
	    }
	    jsonstr += "]";
        // For debug
        /*
        System.out.println("JSON:");
        System.out.println(jsonstr);
        */
            out.print(jsonstr);
        }
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

    public String getJsonPathString(Path p, Map<String, String> clabels){
        String json_str="";
	json_str+="{\"startClass\":\""+ p.getStartClass()+"\",";
                // label
                json_str+="\"label\":\""+clabels.get(p.getStartClass())+"\",";
                List<ClassLink> clinks = p.getClassLinks();
                if ( clinks != null && clinks.size() != 0) {
			json_str+="\"classLinks\":[";
			
			JSONObject[] classLinkObjs = new JSONObject[clinks.size()];
			for (int i = 0; i < clinks.size(); i++) {
				if(i>0){json_str += "," ;}
				json_str+= getJsonClinkString(clinks.get(i), clabels); //koko .toJSONString4(qpg);
			}
			json_str+="]";                        
		}
                json_str += ",";
                json_str +="\"score\":\""+""+"\"";
		json_str +="}";
		
		return json_str;
    }
    
    public String getJsonClinkString(ClassLink c, Map<String, String> clabels){
        String json_str ="{";
        String curl = c.getLinkedClassURI();
        if( c.getPropertyURI() != null ) {
            json_str+="\"predicate\":"+"\""+c.getPropertyURI()+"\",";
        }else{
	   json_str+="\"predicate\":"+"\"propertyURI\",";			
	}
	if( curl != null ){
            json_str+="\"linkedClass\":"+"\""+c.getLinkedClassURI()+"\",";
	}else{
            json_str+="\"linkedClass\":"+"\""+"linkedClassURI"+"\",";
	}
        if( c.getDirection() != null ){
	    json_str+="\"direction\":"+"\""+c.getDirection().toString()+"\",";
	}else{
	    json_str+="\"direction\":"+"\"direction\",";			
	}
        if ( curl != null ){
            String label = clabels.get(curl);
            json_str+="\"label\":"+"\""+label+"\"";
        }else{
            json_str+="\"label\":"+"\""+"\"No Label\""+"\"";               
        }
	json_str+="}";
	return json_str;
    } 
}
