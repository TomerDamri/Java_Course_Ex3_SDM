package sdm.servlets;


import course.java.sdm.engine.controller.ISDMController;
import sdm.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "FileServlet", urlPatterns = {"/pages/salesAreas/uploadFile"})
@MultipartConfig()
public class FileUploadServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("fileupload/form.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Part file = request.getPart("file");
        ISDMController sdmController = ServletUtils.getsdmController(getServletContext());
        sdmController.loadFile(file);

        out.println("file loaded successfully");
    }


}
