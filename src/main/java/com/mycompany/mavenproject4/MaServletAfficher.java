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
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
        
        // Déclaration des variables
        String nom = "", numero = "", nomPrec = "", numeroPrec = "";
        int id, exId = -1;
        String boutonradio = "rien";
        Session session = null;
        String ename = "ENAME", enumber = "ENUMBER"; // pour récupérer les données envoyées dans le formulaire
        
        // Tester obligatoirement si le request.getParameter n'est pas null pour éviter un plantage
        if (request.getParameter("id") != null) id = new Integer(request.getParameter("id"));
            else id = 0;
        if (request.getParameter("boutonradio") != null) boutonradio = request.getParameter("boutonradio");
        if (request.getParameter("ename") != null) ename = request.getParameter("ename");
        if (request.getParameter("enumber") != null) enumber = request.getParameter("enumber");
        
        
        try (PrintWriter out = response.getWriter()) {
            
            // Session pour récupérer l'exId s'il existe, ou pour l'initialiser ou le réinitialiser s'il est différent de l'id.
            HttpSession sessionId = request.getSession(true);
            if(sessionId.getAttribute("exId") != null) {
                exId = (int) sessionId.getAttribute("exId");
            }
            if(exId == -1 || exId != id) {
                if (exId != id) sessionId.setAttribute("exId", id);
            }
            
            // Si le BOUTON "Modifier" a été appuyé
            if (!boutonradio.equals("rien")) {

                // Ouverture d'une session pour modifier ma table dans mySQL
                    // Attention : pour en afficher le résultat, il faut que l'affichage se trouve APRES la mise à jour !
                session = NewHibernateUtil.getSessionFactory().openSession();
                
                // affectation de mon objet employé
                Employeedetails employee = new Employeedetails();
                
                // récupération du nom et du numéro de la validation précédente
                if(session.get(Employeedetails.class, exId) != null)
                {
                    if (((Employeedetails) session.get(Employeedetails.class, exId)).getEname() != null) nomPrec = ((Employeedetails) session.get(Employeedetails.class, exId)).getEname();
                    if (((Employeedetails) session.get(Employeedetails.class, exId)).getEnumber() != null) numeroPrec = ((Employeedetails) session.get(Employeedetails.class, exId)).getEnumber();
                }
                
                session.close(); // FERMER ET REOUVRIR la session (sinon ça plante sur le session.update(employee);)
                
                session = NewHibernateUtil.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();
                
                // Si "Modifier" a été appuyé avec le choix radio "update" ET qu'il y a au moins une entrée input ET que ce qui se trouve dans les input ne correspond pas aux entrées précédentes, 
                // alors on modifie le nom et le numéro de l'employé dans notre BDD MySQL (sinon on affiche le nom et le numéro correspondant à l'id sélectionné)                
                if ( boutonradio.equals("update") && (!ename.equals("")||!enumber.equals("")) && !(ename.equals(nomPrec)&&enumber.equals(numeroPrec)) ) { 
                    employee.setId(id);
                    employee.setEname(ename);
                    employee.setEnumber(enumber);
                    session.update(employee);
                    transaction.commit();
                    }
                // Si "Modifier" a été appuyé avec le choix radio "delete"
                else if (boutonradio.equals("delete")) { // Suppression définitive de l'ID en question dans mySQL
                    employee.setId(id);
                    employee.setEname("");
                    employee.setEnumber("");
                    session.delete(employee);
                    transaction.commit();              
                }
                
                // Fermeture de la session
                session.close();
                
            } // Fin de si le BOUTON "Modifier" a été appuyé
                
            // Début de l'AFFICHAGE
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MaServletAfficher</title>");           
            out.println("</head>");
            out.println("<body>");
            
            out.println("<a href='jspSaisie.jsp'>Ajouter un(e) employé(e)</a><br>");
            out.println("<h1>Voici les données entrées : </h1>");
            
            // Lecture de ma table Employeedetails dans mySQL, je la mets dans employeeList, et affichage du nombre d'employés
            session = NewHibernateUtil.getSessionFactory().openSession();
            List<Employeedetails> employeeList = new ArrayList<Employeedetails>();
            Query query = session.createQuery("from Employeedetails");
            out.println("Nombre d'employés : " + query.list().size() + "<br><br>");
            employeeList = query.list(); // Je peux faire un size là-dessus si je ne veux pas réutiliser le query : query.list().size()
            session.close();
            
            // Affiche la liste des employés (et récupération au passage des données de l'id actuel et de celui de la session précédente)
            for (Employeedetails oJ : employeeList) {
                out.println("*** " + oJ.getId() + " ***<br>");
                out.println("Nom de l'employé(e) : " + oJ.getEname() + "<br>");
                out.println("Numéro de l'employé(e) : " + oJ.getEnumber() + "<br><br>");
                if (oJ.getId().equals(id)) { nom = oJ.getEname(); numero = oJ.getEnumber(); }
                if (oJ.getId().equals(exId)) { nomPrec = oJ.getEname(); numeroPrec = oJ.getEnumber(); }
            }
            
            
            // FORMULAIRE
            // Affichage du menu SELECT OPTION pour choisir l'employé à partir de son ID.
            out.println("<form action='MaServletAfficher' method='post'><select name='id'>");
            for (Employeedetails oJ : employeeList) {
                out.print("<option value='" + oJ.getId() + "' ");
                if (oJ.getId() == id) out.print(" selected>");
                else out.print(">");
                out.println(oJ.getId() + " : " + oJ.getEname() + "</option>");
            }
            out.println("</select>");
            
            // Affichage des BOUTONS RADIO, et du bouton MODIFIER
            out.print("<label>Update</label><input type='radio' name='boutonradio' value='update' ");
            if (boutonradio.equals("update") || boutonradio.equals("rien")) out.println("checked/>"); 
            else out.println("/>");
            out.print("<label>Delete</label><input type='radio' name='boutonradio' value='delete' ");
            if (boutonradio.equals("delete")) out.println("checked/>"); 
            else out.println("/>");
            out.println("<input type='submit' value='Modifier'/><br>");
                 
            // Affichage des 2 champs de saisie (fin du formulaire)
            out.println("<label>Nom&emsp;&emsp;</label><input type='text' name='ename' value='" + nom + "'/><br>");
            out.println("<label>Numéro&emsp;</label><input type='text' name='enumber' value='" + numero + "'/>");
            out.println("</form>");

            // out.println("id : " + id + " nom : " + nom + " numéro : " + numero + " exId : " + exId + " nomPrec : " + nomPrec + " numeroPrec : " + numeroPrec);
            
            out.println("</body>");
            out.println("</html>");
            
        }
    }
    
    // Exemples formateur :
    // nom = ((Employeedetails) session.get(Employeedetails.class, new Integer("2"))).getEname();
    // System.out.println(new Date() );

    

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
