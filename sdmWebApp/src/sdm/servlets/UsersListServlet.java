package sdm.servlets;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import course.java.sdm.engine.controller.ISDMController;
import model.User;

public class UsersListServlet extends BaseServlet {

    protected void processRequest (HttpServletRequest request, HttpServletResponse response) throws IOException {
        // returning JSON objects, not HTML
        try {
            response.setContentType("application/json");
            ISDMController sdmController = getSDMController();
            Set<User> usersList = sdmController.getUsers();
            createJsonResponse(response, usersList);
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
