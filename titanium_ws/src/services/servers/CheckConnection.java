package services.servers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import services.ServicesTools;
import services.errors.ServerErrors;

public class CheckConnection extends HttpServlet {
	private static final long serialVersionUID = 854796535142248158L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject answer = new JSONObject();
		
		String key = req.getParameter(ServicesTools.KEY_ARG);
		
		try {
			int idServer = Integer.parseInt(req.getParameter(ServicesTools.IDSERVER_ARG));
			
			if (!ServicesTools.nullChecker(key, idServer)) {
				answer = ServerUtils.checkConnection(key, idServer);
			} else {
				answer = ServicesTools.createJSONError(ServerErrors.MISSING_ARGUMENT);
			}
		} catch (NumberFormatException e) {
			answer = ServicesTools.createJSONError(ServerErrors.BAD_ARGUMENT);
		}
		
		
		PrintWriter out = resp.getWriter();
		out.write(answer.toString());
		resp.setContentType("text/plain");
	}
}
