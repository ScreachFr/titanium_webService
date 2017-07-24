package services.organization;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import services.ServicesTools;
import services.errors.ServerErrors;

public class Create extends HttpServlet {
	private static final long serialVersionUID = -5230107117247247998L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject answer = new JSONObject();

		String key = req.getParameter(ServicesTools.KEY_ARG);
		String name = req.getParameter(ServicesTools.NAME_ARG);

		if (!ServicesTools.nullChecker(key, name)) {
			answer = OrganizationUtils.createOrganization(key, name);
		} else {
			answer = ServicesTools.createJSONError(ServerErrors.MISSING_ARGUMENT);
		}


		PrintWriter out = resp.getWriter();
		out.write(answer.toString());
		resp.setContentType("text/plain");
	}
}
