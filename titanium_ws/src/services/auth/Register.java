package services.auth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import services.ServicesTools;
import services.errors.ServerErrors;

public class Register extends HttpServlet {
	private static final long serialVersionUID = 3019529540940337210L;

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject anwser = new JSONObject();
		
		
		String username = req.getParameter(Authentication.PARAM_USERNAME);
		String password = req.getParameter(Authentication.PARAM_PASSWORD);
		String email = req.getParameter(Authentication.PARAM_EMAIL);
		
		if (!ServicesTools.nullChecker(username, password, email)) {
			anwser = Authentication.register(username, password, email);
		} else {
			anwser = ServicesTools.createJSONError(ServerErrors.MISSING_ARGUMENT);
		}
		
		PrintWriter out = resp.getWriter();
		out.write(anwser.toString());
		resp.setContentType("text/plain");
	}
}
