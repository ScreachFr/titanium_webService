package services.diag;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class CheckDatabase extends HttpServlet {
	private static final long serialVersionUID = -8079860390589231094L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject answer = new JSONObject();

		answer = DiagUtils.checkDatabase();

		PrintWriter out = resp.getWriter();
		out.write(answer.toString());
		resp.setContentType("text/plain");

	}
}
