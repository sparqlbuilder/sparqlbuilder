/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sparqlbuilder.www;

import org.sparqlbuilder.core.OWLLabelReader;
import java.io.*;
//import java.io.PrintWriter;
import java.util.*;
import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author atsuko
 */
@WebServlet(name = "CLServlet", urlPatterns = {"/clist"})
public class CLServlet extends HttpServlet {

    private static final String FILENAME = "ddata/";
    private static final String WORKDIR = "cc/";
    private Set<String> yumep = null;
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
            out.println("<title>Servlet CLServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CLServlet at " + request.getContextPath() + "</h1>");
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
        String yum = request.getParameter("yum");
        
        //HttpSession session = request.getSession();
        //QueryPathGenerator qpg = (QueryPathGenerator)session.getAttribute("qpg");
        //SortedSet<String> sortedClasses = null;
        String classURI = request.getParameter("class");
     
        Set<String> yumep = null;
        if ( yum !=null ){
            yumep = YummyEP.getYummyEP(yum);
        }
        
        TreeSet<String> classes = new TreeSet<String>();
        Map<String, String> cinfo = new HashMap<String, String>();
        //Map<String, String> owllabels = OWLLabelReader.readLabels("owldata");
        Map<String, String> owllabels = OWLLabelReader.readLabelsFromOWL("owldata");
        try{
            BufferedReader br = new BufferedReader(new FileReader("cc/cl.txt"));
            BufferedWriter bw = new BufferedWriter(new FileWriter("cctmp/cl2.txt"));
            String w_buf;
            while ( (w_buf = br.readLine()) != null ){
                String data[] = w_buf.split("\t"); // 0: url, 1: label, 2: ep, 3: #ins
                if ( yum != null ){
                    if ( ! yumep.contains(data[2])){
                        continue;
                    }
                }
                String label = null;
                if (data.length == 1 ){
                    System.out.println("here");
                }
                if (data[1].length() == 0){
                    if ( owllabels.containsKey(data[0]) ){
                        label = owllabels.get(data[0]);
                    }else{
                        String data2[] = data[0].split("/");
                        String data3[] = data2[data2.length - 1].split("#");
                        label = data3[data3.length -1];
                    }
                }else{
                    label = data[1];
                }     
                String info = label+"\t"+data[0]+"\t"+data[3]+"\t"+data[2]; //label, url, ins, ep
                classes.add(info);
                bw.write(info);
                bw.newLine();
                cinfo.put(data[0], info);
            }
            br.close();
            bw.close();
        }catch(IOException e){
            e.printStackTrace();
        }   
        
/*        if ( ep == null ){
            JsonBuilderFactory jbfactory = Json.createBuilderFactory(null);
            if ( classURI != null ){
                Map<String, String> epclist = new HashMap<String, String>();
                // KOKO TODO TODO 
            }
        }
*/
        if ( classURI != null ){
            if ( ep == null ){
                System.out.println("EP is not determined");
                return;
            }
            try{
                classes = new TreeSet<String>();
                String epc = ep.split("//")[1].replace("/", "_").replace("#", "-");;
                BufferedReader br = new BufferedReader(new FileReader("cc/".concat(epc).concat(".cr")));
                String w_buf;
                while ( (w_buf = br.readLine()) != null ){
                    String data[] = w_buf.split("\t");
                    if ( classURI.equals(data[0])){                        
                        //String info = cinfo.get(data[2]);
                        String cls[] = data[1].split(",");
                        for (int i = 0; i < cls.length; i++ ){
                            String info = cinfo.get(cls[i]);
                            classes.add(info);            
                        }
                        break;
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }
            //qpg.setOWLClassGraph(classURI); 
            //qpg.setPartClassGraph(classURI);
            //classes = qpg.getClasses(null);
            //classes = qpg.getReachableClasses(classURI);            
        }else{
            //classes = qpg.getClasses(null);
            // KOKO TODO
        }
        //sortedClasses = qpg.getSortedClasses(classes);
        
        JsonBuilderFactory jbfactory = Json.createBuilderFactory(null);
        JsonArray ja = getJsonArrayFromClasses(jbfactory, classes);
        out.print(ja);
        //JsonArray ja = getJsonArrayFromSortedClasses(jbfactory, sortedClasses, ep);
        //out.print(ja);
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

    
    private JsonArray getJsonArrayFromSortedClasses(JsonBuilderFactory jbfactory, 
             SortedSet<String> sortedClasses, String ep){
        JsonArrayBuilder jab = jbfactory.createArrayBuilder();
        Iterator<String> cit = sortedClasses.iterator();
        List<String> tmpclasses = new LinkedList<String>();
        JsonObjectBuilder job = jbfactory.createObjectBuilder();
        while( cit.hasNext() ){
            String classinfo = cit.next();
            String[] data = classinfo.split("\t"); 
            if (data.length != 3 ){
                System.out.println("data is wrong?");
            }
            if (data[0].matches("^[0-9]*$")){
                tmpclasses.add(classinfo);
            }else{
                job.add("ep", ep);
                job.add("uri", data[2]);
                job.add("label", data[0]);
                job.add("number", data[1]);
                jab.add(job);
            }
        }
        cit = tmpclasses.iterator();
        while( cit.hasNext() ){
            String classinfo = cit.next();
            String[] data = classinfo.split("\t"); 
            if (data.length != 3 ){
                System.out.println("data is wrong?");
            }
            job.add("ep", ep);
            job.add("uri", data[2]);
            job.add("label", data[0]);
            job.add("number", data[1]);
            jab.add(job);
        }       
        JsonArray ja = jab.build();
        return ja;
    }

    private JsonArray getJsonArrayFromClasses(JsonBuilderFactory jbfactory, TreeSet<String> cl){
        JsonArrayBuilder jab = jbfactory.createArrayBuilder();
        JsonObjectBuilder job = jbfactory.createObjectBuilder();
        Iterator<String> cit = cl.iterator();
        
        List<String> ncl = new LinkedList<String>();
        
        while( cit.hasNext() ){
            String classinfo = cit.next();
            String[] data = classinfo.split("\t"); 
            if (data.length != 4 ){
                System.out.println("data is wrong?"); // KOKO
            }else if( data[0].matches("^[0-9].*") ){
                ncl.add(classinfo); // number should be last
            }else{
                job.add("ep", data[3]);
                job.add("uri", data[1]);
                job.add("label", data[0]);
                job.add("number", data[2]);
                jab.add(job);
            }
        }
        /*
        ListIterator<String> nit = ncl.listIterator();
        while(nit.hasNext()){
            String classinfo = nit.next();
            String[] data = classinfo.split("\t"); 
            job.add("ep", data[3]);
            job.add("uri", data[1]);
            job.add("label", data[0]);
            job.add("number", data[2]);
            jab.add(job);           
        }
        */
        JsonArray ja = jab.build();
        return ja;
    }
}

