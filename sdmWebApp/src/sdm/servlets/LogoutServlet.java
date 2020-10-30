package sdm.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import course.java.sdm.engine.controller.impl.SDMControllerImpl;
import sdm.utils.ServletUtils;
import sdm.utils.SessionUtils;

@WebServlet(name = "LogoutServlet", urlPatterns = { "/logout" })
public class LogoutServlet extends HttpServlet {

    protected void processRequest (HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String usernameFromSession = SessionUtils.getUsername(request);
            SDMControllerImpl sdmController = ServletUtils.getSDMController(getServletContext());

            if (usernameFromSession != null) {
                System.out.println("Clearing session for " + usernameFromSession);
                sdmController.removeUser(usernameFromSession);
                SessionUtils.clearSession(request);

                /*
                 * when sending redirect, you need to think weather its relative or not. (you can read about it
                 * here:
                 * https://tomcat.apache.org/tomcat-5.5-doc/servletapi/javax/servlet/http/HttpServletResponse.html#
                 * sendRedirect(java.lang.String))
                 * 
                 * the best way (IMO) is to fetch the context path dynamically and build the redirection from it and
                 * on
                 */

                response.sendRedirect(request.getContextPath() + "/pages/login/login.html");
            }
        }
        catch (Exception ex) {
            response.setStatus(400);
            response.getWriter().println(ex.getMessage());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left
    // to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request
     *        servlet request
     * @param response
     *        servlet response
     * @throws ServletException
     *         if a servlet-specific error occurs
     * @throws IOException
     *         if an I/O error occurs
     */
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request
     *        servlet request
     * @param response
     *        servlet response
     * @throws ServletException
     *         if a servlet-specific error occurs
     * @throws IOException
     *         if an I/O error occurs
     */
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo () {
        return "Short description";
    }// </editor-fold>
}
