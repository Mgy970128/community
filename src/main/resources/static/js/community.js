
//提交回复
function post() {
    let questionId = $('#question_id').val();
    var content = $('#comment_content').val();
    comment2target(questionId,1,content)
}

//提交二级评论
function comment(e) {
    var id = e.getAttribute("data-id");
    var comment = $('#input-'+id).val();
    comment2target(id,2,comment);
}

/**
 * 评论异步请求
 * @param targetId
 * @param type
 * @param content
 */
function comment2target(targetId,type,content) {
   if(!content){
       alert("不能回复空内容~~")
       return
   }

    $.ajax({
        type:"POST",
        url:"/comment",
        contentType:"application/json",
        dataType:"json",
        data:JSON.stringify({
            "parentId":targetId,
            "content":content,
            "type":type,

        }),
        success:function (res) {
            if(res.code == 200){
                $('#comment_section').hide()
                window.location.reload();
            }else {
                //未登录
                if(res.code == 2003){
                    var isAccepted = confirm("尚未登录,是否要登录?");
                    if(isAccepted){
                        //登录
                        window.open("https://github.com/login/oauth/authorize?client_id=cd316dcc7d4211233c2a&redirect_uri=http://localhost:8080/callback&scope=user&state=2")
                        //设置登录标识
                        window.localStorage.setItem("closable",true);
                    }
                }else {
                    alert(res.message)
                }

            }
            console.log(res)
        }
    });


}


/**
 * 展开二级评论
 */
function collapseComments(e) {
    //获取data-id的值
    var id = e.getAttribute("data-id");
    //console.log($(e))
    //展开的id
    var comments = $('#comment-'+id);

    //判断二级评论展开状态，进行状态切换
    if(comments.hasClass("in")){
       //合起二级评论
        comments.removeClass("in")
        $(e).removeClass("comment-active")
    }else {
        console.log(1111)
        let commentBody = $("#comment-"+id);

        //判断是否已经加载了评论数据
        if(commentBody.children().length !=1){
            comments.addClass("in")
            $(e).addClass("comment-active")
        }else {
            $.getJSON("/comment/"+id,function (data) {
                console.log(data)
                //获取数据成功后打开折叠
                $.each(data.data.reverse(),function (index,comment) {

                    let mediaLeftElement = $("<div/>",{
                        "class":"media-left"
                    }).append($("<img/>",{
                        "class":"media-object img-rounded",
                        "scr":comment.user.avatarUrl
                    }));

                    let mediaBodyElement = $("<div/>",{
                        "class":"media-body"
                    }).append($("<h6/>",{
                        "class":"media-heading",
                        "html":comment.user.name
                    })).append($("<div/>",{
                        "html":comment.content
                    })).append($("<div/>",{
                        "class":"menu"
                    }).append($("<span/>",{
                        "class":"pull-right date",
                        "html":formatDate(comment.gmtCreate)
                    })));

                    let mediaElement = $("<div/>",{
                        "class":"media"
                    }).append(mediaLeftElement).append(mediaBodyElement);


                    let comentElement = $("<div/>",{
                        class:"col-lg-12 col-md-12 col-sm-12 col-xs-12 comments",
                    }).append(mediaElement);

                    commentBody.prepend(comentElement)
                });
                comments.addClass("in")
                $(e).addClass("comment-active")

            });
        }



    }
}

function selectTag(e) {
    let value = e.getAttribute("data-tag")
    let previous = $('#tag').val()
    //如果不包含当前点击标签
    if(previous.indexOf(value) == -1){
        if(previous){
            $('#tag').val(previous+','+value)
        }else {
            $('#tag').val(value)
        }
    }
}

function showSelectTag() {
   $('#select-tag').show();
}
