import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/userInfo")
public class UserInfoServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取用户信息
        String blogId = req.getParameter("blogId");
        if (blogId == null) {
            // 列表页, 获取当前登陆用户的信息
            // 直接从 session 中获取即可~~
            getUserInfoFromSession(req, resp);
        } else {
            // 详情页, 获取文章作者的信息
            // 查询数据库
            getUserInfoFromDB(req, resp, Integer.parseInt(blogId));
        }
    }

    private void getUserInfoFromDB(HttpServletRequest req, HttpServletResponse resp, int blogId) throws IOException {
        // 1. 先根据 blogId 查询 Blog 对象, 获取到 userId (作者是谁)
        BlogDao blogDao = new BlogDao();
        Blog blog = blogDao.selectOne(blogId);
        if (blog == null) {
            // 如果参数传来的这个 blogId 是随便瞎写的. 数据库里没有.
            resp.setStatus(404);
            resp.setContentType("text/html;charset=utf8");
            resp.getWriter().write("blogId 不存在");
            return;
        }
        // 2. 根据 userId 查询对应的 User 对象即可
        UserDao userDao = new UserDao();
        User user = userDao.selectById(blog.getUserId());
        if (user == null) {
            resp.setStatus(404);
            resp.setContentType("text/html;charset=utf8");
            resp.getWriter().write("blogId 不存在");
            return;
        }
        // 3. 把 user 对象返回给浏览器了
        user.setPassword("");
        resp.setContentType("application/json;charset=utf8");
        resp.getWriter().write(objectMapper.writeValueAsString(user));
    }

    private void getUserInfoFromSession(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.setStatus(403);
            resp.setContentType("text/html;charset=utf8");
            resp.getWriter().write("当前未登录");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.setStatus(403);
            resp.setContentType("text/html;charset=utf8");
            resp.getWriter().write("当前未登录");
            return;
        }
        // user 获取到了, 把 user 中的 password 给干掉, 然后返回.
        user.setPassword("");

        resp.setContentType("application/json; charset=utf8");
        resp.getWriter().write(objectMapper.writeValueAsString(user));
    }
}
