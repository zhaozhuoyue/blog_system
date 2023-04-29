create database if not exists zyx_blog_system;

use zyx_blog_system;

drop table if exists blog;
create table blog (
    blogId int primary key auto_increment,
    title varchar(256),
    content text,
    postTime datetime,
    userId int
);

drop table if exists user;
create table user(
    userId int primary key auto_increment,
    username varchar(50) unique,
    password varchar(50)
);

insert into blog values(null,"这是第一篇博客","从今天开始，我要认真写代码",'2023-3-28 10:25:00',1);
insert into blog values(null,"这是第二篇博客","从今天开始，我要认真写代码",'2023-3-29 10:35:00',1);

insert into user values(null,"zhangsan","123");
insert into user values(null,"lisi","123");