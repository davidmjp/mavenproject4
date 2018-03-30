/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject4;

import com.mycompany.mavenproject4.Employeedetails;
import com.mycompany.mavenproject4.NewHibernateUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Formation
 */
public class MaServletAfficher extends HttpServlet {

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
        
        
        String nom = "", numero = "";
        int id;
        if (request.getParameter("id") != null) id = new Integer(request.getParameter("id"));
        else id = 0;
        
        try (PrintWriter out = response.getWriter()) {
             
             
            String ename = request.getParameter("ename");
            String enumber = request.getParameter("enumber");
            String bouton = "rien";
            if (request.getParameter("bouton") != null) bouton = request.getParameter("bouton");
            
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MaServletAfficher</title>");           
            out.println("</head>");
            out.println("<body>");
            out.println("<a href='jspSaisie.jsp'>Ajouter un(e) employé(e)</a><br>");
            out.println("<h1>Voici les données entrées : </h1>");
  
           
            Session session = NewHibernateUtil.getSessionFactory().openSession();
            List<Employeedetails> employeeList = new ArrayList<Employeedetails>();
            Query query = session.createQuery("from Employeedetails");
            out.println("Nombre d'employés : " + query.list().size() + "<br><br>");
            employeeList = query.list(); // Je peux faire un size là-dessus si je ne veux pas réutiliser le query
            session.close();
            
            
            for (Employeedetails oJ : employeeList) {
                out.println("*** " + oJ.getId() + " ***<br>");
                out.println("Nom de l'employé(e) : " + oJ.getEname() + "<br>");
                out.println("Numéro de l'employé(e) : " + oJ.getEnumber() + "<br><br>");
            }
            
            session = NewHibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            Employeedetails employee = new Employeedetails();
            
            
            
            /*
            out.println(request.getParameter("bouton"));
            // nom = ((Employeedetails) session.get(Employeedetails.class, new Integer("2"))).getEname();
            nom = ((Employeedetails) session.get(Employeedetails.class, id)).getEname();
                     out.println("Le nom : " + nom);
              
            if (request.getParameter("bouton") == null) out.println("NULL");
            else out.println(request.getParameter("bouton"));
            */   
            
            
            
            if (bouton.equals("update")) {
                if ((ename != null && ename !="") || (enumber != null && enumber !="")) { // Mettre à jour la BDD si quelque chose a été entré   
                    employee.setId(id);
                    employee.setEname(ename);
                    employee.setEnumber(enumber);
                    session.update(employee);
                    transaction.commit();
                    
                }
                else { // si les champs sont vides, on les préremplit
                    nom = ((Employeedetails) session.get(Employeedetails.class, id)).getEname();
                    numero = ((Employeedetails) session.get(Employeedetails.class, id)).getEnumber();         
                }
            }
            else if (bouton.equals("delete")) {
                employee.setId(id);
                employee.setEname("");
                employee.setEnumber("");
                session.delete(employee);
                transaction.commit();
            }
            
            session.close();
            
            out.println("<form action='MaServletAfficher' method='post'><select name='id'>");
            for (int i = 1; i <= employeeList.size(); i++) {
                out.print("<option");
                if (i == id) out.print(" selected>");
                else out.print(">");
                out.println(i + "</option>");
            }
            
            
            out.println("</select>");
            out.print("<label>Update</label><input type='radio' name='bouton' value='update' ");
            if (bouton.equals("update")) out.println("checked/>"); 
            else out.println("/>");
            out.print("<label>Delete</label><input type='radio' name='bouton' value='delete' ");
            if (bouton.equals("delete")) out.println("checked/>"); 
            else out.println("/>");
            out.println("<input type='submit' value='Modifier'/><br>");
            out.println("<label>Nom&emsp;&emsp;</label><input type='text' name='ename' value='" + nom + "'/><br>");
            out.println("<label>Numéro&emsp;</label><input type='text' name='enumber' value='" + numero + "'/>");
            
            
            
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
            
            //session.close();
            
            
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
        processRequest(request, response);
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

}
