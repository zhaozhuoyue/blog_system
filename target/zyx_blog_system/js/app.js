//创建新的函数
function getLoginStatus() {
    $.ajax({
        type:'get',
        url:'login',
        success:function(body) {
            //得到200，此处不做任何工作
            console.log("当前已经登陆过");
        },
        error:function() {
            //得到403（非2开头的状态码都会触发这个error分支）
            //让页面强行跳转login.html
            console.log("当前还未登陆过");
            location.assign('login.html');
        }
    })
}
