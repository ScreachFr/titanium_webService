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

public class Logout extends HttpServlet {
	private static final long serialVersionUID = -3615129246437772949L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject answer = new JSONObject();
		
		String key = req.getParameter(ServicesTools.KEY_ARG);
		
		if (!ServicesTools.nullChecker(key)) {
			answer = Authentication.logout(key);
		} else {
			answer = ServicesTools.createJSONError(ServerErrors.MISSING_ARGUMENT);
		}
		
		PrintWriter out = resp.getWriter();
		out.write(answer.toString());
		resp.setContentType("text/plain");
	}
}
