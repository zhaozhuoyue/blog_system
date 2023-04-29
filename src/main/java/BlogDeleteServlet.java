import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/blog_delete")
public class BlogDeleteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 先判定用户的登陆状态
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.setStatus(403);
            resp.setContentType("text/html; charset=utf8");
            resp.getWriter().write("您当前未登录, 不能删除!");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.setStatus(403);
            resp.setContentType("text/html; charset=utf8");
            resp.getWriter().write("您当前未登录, 不能删除!");
            return;
        }
        // 2. 获取到 blogId
        String blogId = req.getParameter("blogId");
        if (blogId == null) {
            // 这个 blogId 参数不存在, 无法删除
            resp.setStatus(404);
            resp.setContentType("text/html; charset=utf8");
            resp.getWriter().write("您当前删除的 blogId 有误");
            return;
        }
        // 3. 查询出这个 blogId 对应的 Blog 对象
        BlogDao blogDao = new BlogDao();
        Blog blog = blogDao.selectOne(Integer.parseInt(blogId));
        if (blog == null) {
            // 这个 blogId 参数不存在, 无法删除
            resp.setStatus(404);
            resp.setContentType("text/html; charset=utf8");
            resp.getWriter().write("您当前删除的 博客 不存在! blogId=" + blogId);
            return;
        }
        // 4. 判定登陆用户是否就是文章作者
        if (blog.getUserId() != user.getUserId()) {
            // blog.getUserId() 文章的作者
            // user.getUserId() 从 session 里拿的登陆的用户是谁.
            // 不一样, 说明在删别人的文章.
            // 直接返回 403
            resp.setStatus(403);
            resp.setContentType("text/html; charset=utf8");
            resp.getWriter().write("当前您不能删除别人的博客!");
            return;
        }
        // 5. 真正执行删除操作.
        blogDao.delete(Integer.parseInt(blogId));
        // 6. 返回 302 重定向
        resp.sendRedirect("blog_list.html");
    }
}
