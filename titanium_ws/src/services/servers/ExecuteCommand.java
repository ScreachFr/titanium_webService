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

public class ExecuteCommand extends HttpServlet {
	private static final long serialVersionUID = -5959479921337336827L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject answer = new JSONObject();
		
		String key = req.getParameter(ServicesTools.KEY_ARG);
		String command = req.getParameter(ServicesTools.COMMAND_ARG);
		
		try {
			int idServer = Integer.parseInt(req.getParameter(ServicesTools.IDSERVER_ARG));
			int timeout = Integer.parseInt(req.getParameter(ServicesTools.TIMEOUT_ARG));
			
			if (!ServicesTools.nullChecker(key, idServer, command, timeout)) {
				answer = ServerUtils.executeCommand(key, idServer, command, timeout);
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
