import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/blog")
public class BlogServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 按照约定的接口格式返回数据
        // 在博客列表页中, 已经使用了 BlogServlet.doGet 方法了.
        // 博客详情页, 也想用. 就需要做出区分. 使用 query string 来区分.
        // 如果请求带有 query string , 有 blogId 这个参数, 就认为是博客详情页的请求.
        // 如果请求不带有 query string, 就认为是博客列表页的请求.
        resp.setContentType("application/json; charset=utf8");

        BlogDao blogDao = new BlogDao();

        String blogId = req.getParameter("blogId");
        if (blogId == null) {
            // 博客列表页发起的请求
            List<Blog> blogs = blogDao.selectAll();
            resp.getWriter().write(objectMapper.writeValueAsString(blogs));
        } else {
            // 博客详情页发起的请求
            Blog blog = blogDao.selectOne(Integer.parseInt(blogId));
            resp.getWriter().write(objectMapper.writeValueAsString(blog));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 使用这个方法, 来实现提交新博客
        // 1. 先检查一下用户的登陆状态, 获取到会话和用户信息.
        //    如果未登录, 显然不能提交博客~
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.setStatus(403);
            resp.setContentType("text/html; charset=utf8");
            resp.getWriter().write("当前未登录, 不能发布博客!");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.setStatus(403);
            resp.setContentType("text/html; charset=utf8");
            resp.getWriter().write("当前未登录, 不能发布博客!");
            return;
        }

        // 2. 获取请求中的参数(博客的标题和正文)
        req.setCharacterEncoding("utf8");
        String title = req.getParameter("title");
        String content = req.getParameter("content");

        // 3. 构造 Blog 对象, 并且插入到数据库中.
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        blog.setUserId(user.getUserId());
        // blogId 是自增主键, 不需要设置. postTime 直接在数据库操作中, 已经使用 now 来设置了.
        BlogDao blogDao = new BlogDao();
        blogDao.insert(blog);

        // 4. 构造 重定向报文, 回到 博客列表页.
        resp.sendRedirect("blog_list.html");
    }
}
