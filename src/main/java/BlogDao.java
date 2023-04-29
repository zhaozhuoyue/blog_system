import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// +----------+--------------+------+-----+---------+----------------+
// | Field    | Type         | Null | Key | Default | Extra          |
// +----------+--------------+------+-----+---------+----------------+
// | blogId   | int(11)      | NO   | PRI | NULL    | auto_increment |
// | title    | varchar(256) | YES  |     | NULL    |                |
// | content  | text         | YES  |     | NULL    |                |
// | postTime | datetime     | YES  |     | NULL    |                |
// | userId   | int(11)      | YES  |     | NULL    |                |
// +----------+--------------+------+-----+---------+----------------+

// 封装针对博客表的相关操作
public class BlogDao {
    // 1. 插入一个博客到数据库中 -- 发布博客
    public void insert(Blog blog) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            // 1. 和数据库建立连接
            connection = DBUtil.getConnection();
            // 2. 构造 SQL
            String sql = "insert into blog values(null, ?, ?, now(), ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, blog.getTitle());
            statement.setString(2, blog.getContent());
            statement.setInt(3, blog.getUserId());
            // 3. 执行 SQL
            int ret = statement.executeUpdate();
            if (ret != 1) {
                System.out.println("博客插入失败!");
            } else {
                System.out.println("博客插入成功!");
            }
            //    [注意!!] close 其实不应该在这里调用. 一旦上面抛出异常, 此处的 close 可能无法被执行.
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 4. 释放对应的资源
            DBUtil.close(connection, statement, null);
        }
    }

    // 2. 根据博客 id 来查询指定博客 -- 博客详情页
    public Blog selectOne(int blogId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 1. 和数据库建立连接
            connection = DBUtil.getConnection();
            // 2. 构造 SQL
            String sql = "select * from blog where blogId = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, blogId);
            // 3. 执行 SQL
            resultSet = statement.executeQuery();
            // 4. 遍历结果集合
            if (resultSet.next()) {
                Blog blog = new Blog();
                blog.setBlogId(resultSet.getInt("blogId"));
                blog.setTitle(resultSet.getString("title"));
                blog.setContent(resultSet.getString("content"));
                blog.setPostTime(resultSet.getTimestamp("postTime"));
                blog.setUserId(resultSet.getInt("userId"));
                return blog;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, statement, resultSet);
        }
        return null;
    }

    // 3. 直接查询博客列表 -- 博客列表页
    public List<Blog> selectAll() {
        List<Blog> blogs = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 1. 和数据库建立连接
            connection = DBUtil.getConnection();
            // 2. 构造 SQL
            String sql = "select * from blog order by postTime desc";
            statement = connection.prepareStatement(sql);
            // 3. 执行 SQL
            resultSet = statement.executeQuery();
            // 4. 遍历结果集合
            while (resultSet.next()) {
                Blog blog = new Blog();
                blog.setBlogId(resultSet.getInt("blogId"));
                blog.setTitle(resultSet.getString("title"));
                String content = resultSet.getString("content");
                // 避免博客列表页显示的内容太长. 希望用户点到博客详情页里再看到这个详细正文的.
                if (content.length() > 100) {
                    content = content.substring(0, 100) + "...";
                }
                blog.setContent(content);
                blog.setPostTime(resultSet.getTimestamp("postTime"));
                blog.setUserId(resultSet.getInt("userId"));
                blogs.add(blog);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, statement, resultSet);
        }
        return blogs;
    }

    // 4. 删除指定博客 -- 删除博客
    public void delete(int blogId) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            // 1. 和数据库建立连接
            connection = DBUtil.getConnection();
            // 2. 构造 SQL
            String sql = "delete from blog where blogId = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, blogId);
            // 3. 执行 SQL
            int ret = statement.executeUpdate();
            if (ret != 1) {
                System.out.println("博客删除失败!");
            } else {
                System.out.println("博客删除成功!");
            }
            //    [注意!!] close 其实不应该在这里调用. 一旦上面抛出异常, 此处的 close 可能无法被执行.
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 4. 释放对应的资源
            DBUtil.close(connection, statement, null);
        }
    }
}
