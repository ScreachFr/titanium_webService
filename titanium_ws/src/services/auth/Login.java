package services.auth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;


public class Login extends HttpServlet {
	private static final long serialVersionUID = -1969175381640448283L;

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject anwser = new JSONObject();
		anwser.put("Message", "Hello world!");
		
		PrintWriter out = resp.getWriter();
		
		out.write(anwser.toString());
		
		resp.setContentType("text/plain");
	}
	
}
