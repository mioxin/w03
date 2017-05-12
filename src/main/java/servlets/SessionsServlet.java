package servlets;

import com.google.gson.Gson;
import dbService.DBException;
import dbService.DBService;
import dbService.dataSets.UsersDataSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author v.chibrikov
 *         <p>
 *         Пример кода для курса на https://stepic.org/
 *         <p>
 *         Описание курса и лицензия: https://github.com/vitaly-chibrikov/stepic_java_webserver
 */
public class SessionsServlet extends HttpServlet {
    private final DBService accountService;

    public SessionsServlet(DBService accountService) {
        this.accountService = accountService;
    }

    //get logged user profile
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        String sessionId = request.getSession().getId();
        String p = request.getParameter("p");
        UsersDataSet profile = null;
        try {
            //profile = accountService.getUserBySessionId(sessionId);
            switch (p){
                case "delsession":
                    profile = getProfile(sessionId);
                    delSession(profile.getName());
                    break;
                default:
                    profile = getProfile(sessionId);
                    break;
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
        if (profile == null) {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(profile);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().println(json);
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    //sign in
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String pass = request.getParameter("pass");
        String sessionId = request.getSession().getId();

        if (login == null || pass == null) {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        UsersDataSet profile = null;
        try {
            profile = accountService.getUserByLogin(login);
        } catch (DBException e) {
            e.printStackTrace();
        }
        if (profile == null || !profile.getPass().equals(pass)) {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        else {
            try {
                accountService.addSession(request.getSession().getId(), login);
            } catch (DBException e) {
                e.printStackTrace();
            }
        }
        Gson gson = new Gson();
        String json = gson.toJson(profile);
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(json);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    //sign out
    public void doDelete(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String sessionId = request.getSession().getId();
        UsersDataSet profile = null;
        try {
            profile = accountService.getUserBySessionId(sessionId);
        } catch (DBException e) {
            e.printStackTrace();
        }
        if (profile == null) {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            try {
                accountService.deleteSession(profile.getName());
            } catch (DBException e) {
                e.printStackTrace();
            }
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().println("Goodbye!");
            response.setStatus(HttpServletResponse.SC_OK);
        }

    }

    private void delSession(String login) throws DBException {
        accountService.deleteSession(login);
    }
    private UsersDataSet getProfile(String sessionId) throws DBException {
        return accountService.getUserBySessionId(sessionId);
    }
}
