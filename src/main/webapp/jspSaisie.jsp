<%-- 
    Document   : jspSaisie
    Created on : 27 mars 2018, 11:16:18
    Author     : Formation
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <% 
            int donneePassage=0;
            if(session.getAttribute("donneePassage") != null) donneePassage = (int) session.getAttribute("donneePassage");
            if (donneePassage == 1) out.print("<script type='text/javascript'>alert('Employé(e) ajouté(e) !');</script>");
            session.setAttribute("donneePassage", 0); // Pour éviter de revoir l'alert quand on revient sur cette page
        %>
        
        <form action="MaServletAjouter" method="POST">
            <h1>Ajout d'un employé</h1>
            <label>Nom de l'employé(e) : </label>
            <input type="text" name="ename"/><br>
            <label>Numéro de l'employé(e) : </label>
            <input type="text" name="enumber"/>
            <input type="submit" value="Ajouter"/>
            
        </form>
        <form method="post" action="MaServletAfficher">
            <input type="submit" value="Voir la liste"/>  
        </form>
        
    </body>
</html>
