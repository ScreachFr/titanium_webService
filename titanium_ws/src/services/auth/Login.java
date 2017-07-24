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


public class Login extends HttpServlet {
	private static final long serialVersionUID = -1969175381640448283L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject answer = new JSONObject();
		
		String username = req.getParameter(ServicesTools.USERNAME_ARG);
		String password = req.getParameter(ServicesTools.PASSWORD_ARG);
		
		if (!ServicesTools.nullChecker(username, password)) {
			answer = Authentication.login(username, password);
		} else {
			answer = ServicesTools.createJSONError(ServerErrors.MISSING_ARGUMENT);
		}
		
		PrintWriter out = resp.getWriter();
		out.write(answer.toString());
		resp.setContentType("text/plain");
	}
	
}
