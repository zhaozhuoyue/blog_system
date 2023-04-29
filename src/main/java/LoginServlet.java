import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.从请求中获取到用户名和密码
        req.setCharacterEncoding("utf-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        if(username == null || password.equals("") || password == null ||password.equals("")){
            //用户名密码为空，直到返回登陆失败
            resp.setContentType("text/html;charset=utf8");
            resp.getWriter().write("用户名或密码为空！登陆失败");
            return;
        }

        //2.查询数据库，验证用户名密码是否正确
        UserDao userDao = new UserDao();
        User user = userDao.selectByName(username);
        if(user == null || !user.getPassword().equals(password)) {
            //用户名不存在,或者密码不相同，返回登陆失败
            resp.setContentType("text/html;charset=utf8");
            resp.getWriter().write("用户名或密码错误！登陆失败");
            return;
        }

        //3.如果正确，创建一个会话对象
        HttpSession session = req.getSession(true);
        // 在会话中保存一下user，以备后面使用.后续访问其他页面，就可以直接通过会话拿到当前是哪个用户在访问了
        session.setAttribute("user",user);

        //4.构造302响应报文
        resp.sendRedirect("blog_list.html");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //使用这个方法来针对当前登陆状态进行判定
        //1.获取一下当前的会话
        HttpSession session = req.getSession(false);
        if(session == null) {
            //没有会话，当前是未登录状态
            resp.setStatus(403);
            return;
        }
        //这里的user对象是否存在，还是要判定的
        //是否会出现，session存在，user不存在的情况？是的
        //当下没有这个情况，后面写到“退出登录”（注销），就会涉及到这个情况
        User user = (User) session.getAttribute("user");
        if(user == null) {
            //虽然有对话，但是里面没有 user对象，也认为是未登录状态
            resp.setStatus(403);
            return;
        }

        //2.返回 200 这样的响应即可
        // 不写这个代码也行，默认状态码就是200
        resp.setStatus(200);
    }
}
