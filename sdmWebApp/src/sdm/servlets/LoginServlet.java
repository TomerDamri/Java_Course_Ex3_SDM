package sdm.servlets;

import com.google.gson.Gson;
import course.java.sdm.engine.controller.impl.SDMControllerImpl;
import model.User;
import model.response.LoginResponse;
import sdm.constants.Constants;
import sdm.utils.ServletUtils;
import sdm.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import static sdm.constants.Constants.USERNAME;
import static sdm.constants.Constants.USER_ROLE;

public class LoginServlet extends HttpServlet {

    // urls that starts with forward slash '/' are considered absolute
    // urls that doesn't start with forward slash '/' are considered relative to the place where this
    // servlet request comes from
    // you can use absolute paths, but then you need to build them from scratch, starting from the
    // context path
    // ( can be fetched from request.getContextPath() ) and then the 'absolute' path from it.
    // Each method with it's pros and cons...
    private final String SALES_AREAS_URL = "../salesAreas/salesAreas.html";
    private final String SIGN_UP_URL = "../signup/signup.html";
    private final String LOGIN_ERROR_URL = "/pages/loginerror/login_attempt_after_error.jsp"; // must start with '/' since will be used in
    // request dispatcher...

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String usernameFromSession = SessionUtils.getUsername(request);
        SDMControllerImpl sdmController = ServletUtils.getSDMController(getServletContext());

        if (usernameFromSession == null) { // user is not logged in yet

            String usernameFromParameter = request.getParameter(USERNAME);
            String userRoleFromParameter = request.getParameter(USER_ROLE);
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                // no username in session and no username in parameter - not standard situation. it's a conflict

                // stands for conflict in server state
                response.setStatus(409);

                // returns answer to the browser to go back to the sign up URL page
                response.setStatus(200);
                try (PrintWriter out = response.getWriter()) {
                    Gson gson = new Gson();
                    LoginResponse loginResponse = new LoginResponse(SIGN_UP_URL, null, null, null);
                    String json = gson.toJson(loginResponse);
                    out.println(json);
                    out.flush();
                }
            } else {

                // normalize the username value
                usernameFromParameter = usernameFromParameter.trim();

                /*
                 * One can ask why not enclose all the synchronizations inside the userManager object ? Well, the
                 * atomic action we need to perform here includes both the question (isUserExists) and (potentially)
                 * the insertion of a new user (addUser). These two actions needs to be considered atomic, and
                 * synchronizing only each one of them, solely, is not enough. (of course there are other more
                 * sophisticated and performable means for that (atomic objects etc) but these are not in our scope)
                 *
                 * The synchronized is on this instance (the servlet). As the servlet is singleton - it is promised
                 * that all threads will be synchronized on the very same instance (crucial here)
                 *
                 * A better code would be to perform only as little and as necessary things we need here inside the
                 * synchronized block and avoid do here other not related actions (such as request
                 * dispatcher\redirection etc. this is shown here in that manner just to stress this issue
                 */
                synchronized (this) {
                    if (sdmController.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";

                        // stands for unauthorized as there is already such user with this name
                        response.setStatus(401);
                        response.getOutputStream().println(errorMessage);
                    } else {
                        // add the new user to the users list

                        User.UserType userType = userRoleFromParameter.equals("customer") ? User.UserType.CUSTOMER
                                : User.UserType.STORE_OWNER;
                        UUID userId = sdmController.addUserToSystem(usernameFromParameter, userType);
                        // set the username in a session so it will be available on each request
                        // the true parameter means that if a session object does not exists yet
                        // create a new one
                        request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
                        request.getSession(false).setAttribute(Constants.USER_ID, userId);
                        request.getSession(false).setAttribute(Constants.USER_TYPE, userType);

                        // redirect the request to the chat room - in order to actually change the URL
                        System.out.println("On login, request URI is: " + request.getRequestURI());
                        response.setStatus(200);
//                        response.setContentType("application/json");
                        try (PrintWriter out = response.getWriter()) {
                            Gson gson = new Gson();
                            LoginResponse loginResponse = new LoginResponse(SALES_AREAS_URL, userId, usernameFromParameter, userType);
                            String json = gson.toJson(loginResponse);
                            out.println(json);
                            out.flush();
                        }
                    }
                }
            }
        } else {
            // user is already logged in
            // user is already logged in
            response.setStatus(200);
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                LoginResponse loginResponse = new LoginResponse(SALES_AREAS_URL, null, usernameFromSession, User.UserType.CUSTOMER);
                String json = gson.toJson(loginResponse);
                out.println(json);
                out.flush();
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left
    // to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
