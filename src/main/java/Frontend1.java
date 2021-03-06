/**
 * Created by Elvira on 21.02.14.
 */

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Frontend1 extends HttpServlet {

    Frontend1(){
        database.put("elvira",new User("elvira", "elvira"));
    }
    private  AtomicLong userIdGenerator = new AtomicLong();
    private Map<String, User> database = new HashMap<>();

    public static String getTime() {
        Date date = new Date();
        DateFormat formatter = new SimpleDateFormat("HH.mm.ss");
        return formatter.format(date);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        Map<String, Object> pageVariables = new HashMap<>();

        if (req.getPathInfo().equals("/authform")) {
            pageVariables.put("error", "");
            resp.getWriter().println(PageGenerator.getPage("authform.tml", pageVariables));
            return;
        }

        if (req.getPathInfo().equals("/user")) {
            HttpSession session = req.getSession();
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                userId = userIdGenerator.getAndIncrement();
                session.setAttribute("userId", userId);
            }
            pageVariables.put("refreshPeriod", "1000");
            pageVariables.put("serverTime", getTime());
            pageVariables.put("userId", userId);
            resp.getWriter().println(PageGenerator.getPage("user.tml", pageVariables));
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        Map<String, Object> pageVariables = new HashMap<>();

        if (req.getPathInfo().equals("/authorize")) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            if (username.equals("")) {
                pageVariables.put("error", "Введите логин");
                resp.getWriter().println(PageGenerator.getPage("authform.tml", pageVariables));
                return;
            }
            if (password.equals("")) {
                pageVariables.put("error", "Введите пароль");
                resp.getWriter().println(PageGenerator.getPage("authform.tml", pageVariables));
                return;
            }
            if (database.containsKey(username) && database.get(username).password.equals(password)) {
                resp.sendRedirect("/user");
                return;
            }

            pageVariables.put("error", "Не существует пользователя с таким паролем");
            resp.getWriter().println(PageGenerator.getPage("authform.tml", pageVariables));
            return;
        }
    }
}
