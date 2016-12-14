/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sparqlbuilder.www;

import java.io.*;
//import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.json.*;
import java.util.*;

//import java.io.*;

/**
 *
 * @author atsuko
 */
@WebServlet(name = "EPServlet", urlPatterns = {"/eplist"})
public class EPServlet extends HttpServlet {

    private static final String FILENAME = "ddata/";
    
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
            out.println("<title>Servlet EPServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EPServlet at " + request.getContextPath() + "</h1>");
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
        HttpSession session = request.getSession();
        // Initialize the session
        //session.removeAttribute("qpg");
        //session.removeAttribute("graph");
        //QueryPathGenerator qpg = new QueryPathGenerator();

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        response.setHeader("Access-Control-Max-Age", "-1");        
	PrintWriter out = response.getWriter();
     
        String ds = request.getParameter("ds");
        String yum = request.getParameter("yum");
           
        //String[] elist = qpg.getFactory().getEndpointURIList();
        List<String> elist = getEndpointURIList();

        // with YummyData
        Set<String> yumep = null;
        if ( yum != null ){
            yumep = YummyEP.getYummyEP(yum);
        }
       
        SortedSet<String> sortedelist = new TreeSet<String>();
        ListIterator<String> eit = elist.listIterator();
        //for (int i = 0; i < elist.length; i++ ){
        while( eit.hasNext()){
            String ep = eit.next();
            if ( yumep != null ){
                if ( !yumep.contains(ep)){
                    continue;
                }
            }
            sortedelist.add(ep);
        }
        JsonBuilderFactory jbfactory = Json.createBuilderFactory(null);
        JsonArrayBuilder jab = jbfactory.createArrayBuilder();
        if ( ds == null ){
            Iterator<String> eit2 = sortedelist.iterator();
            while( eit2.hasNext() ){
                jab.add(eit2.next());            
            }
            JsonArray ja = jab.build();
	    out.print(ja);
        }else{
            File dsf = new File("dstable.txt");
            HashMap<String, String> demap = new HashMap<String, String>();
            try{
                BufferedReader br = new BufferedReader(new FileReader(dsf));
                String buf;
                while ( (buf = br.readLine()) != null ){
                    String[] data = buf.split(",");
                    if (data.length != 2 ){ continue; }
                    demap.put(data[0],data[1]);
                }
            }catch(IOException e){
                e.printStackTrace();
            }
            Iterator<String> eit3 = sortedelist.iterator();
            while( eit3.hasNext() ){
                JsonObjectBuilder job = jbfactory.createObjectBuilder();
                String duri = eit3.next();
                String euri = duri;
                String ep = demap.get(duri);
                if ( ep != null ){
                    euri = ep;
                }
                job.add("label", duri);
                job.add("uri", euri);
                jab.add(job);
            }
            JsonArray ja = jab.build();
	    out.print(ja);           
        }
        //session.setAttribute("qpg", qpg);
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

    public List<String> getEndpointURIList(){
        List<String> ep = new LinkedList<String>();
        try{
            BufferedReader br = new BufferedReader(new FileReader("cc/ep.txt"));
            String buf;
            while ((buf = br.readLine()) != null ){
                ep.add(buf);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return ep;
    }
    
}
