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

public class EditServer extends HttpServlet {
	private static final long serialVersionUID = -3587567677988907422L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject answer = new JSONObject();
		
		String key = req.getParameter(ServicesTools.KEY_ARG);
		String name = req.getParameter(ServicesTools.NAME_ARG);
		String host = req.getParameter(ServicesTools.HOST_ARG);
		String password = req.getParameter(ServicesTools.PASSWORD_ARG);
		
		try {
			int  idOrga = Integer.parseInt(req.getParameter(ServicesTools.IDORGA_ARG));
			int port = Integer.parseInt(req.getParameter(ServicesTools.PORT_ARG));
			int idServer = Integer.parseInt(req.getParameter(ServicesTools.IDSERVER_ARG));
			
			if (!ServicesTools.nullChecker(key, idOrga, name, host, port, password, idServer)) {
				answer = ServerUtils.editServer(key, idServer, name, host, port, password);
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
