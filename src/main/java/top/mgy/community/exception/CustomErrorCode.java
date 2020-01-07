package top.mgy.community.exception;

/**
 * 异常枚举类
 */
public enum CustomErrorCode implements ICustomizeErrorCode {
    /**
     * 问题未找到
     */
    QUESTION_NOT_FOUND(2001,"你找的问题飞了,要不换一个试试!"),
    TARGET_PARAM_NOT_FOUND(2002,"未选择任何问题或评论进行回复！"),
    ON_LOGIN(2003,"当前操作需要登录，请登录后重试！"),
    SYS_ERROR(2004,"服务器冒烟了，要不稍后再试试"),
    TYPE_PARAM_NOT_WRONG(2005,"评论类型错误，或不存在"),
    COMMENT_NOT_FOUND(2006,"回复的评论不存在"),
    RDAD_NOTIFICATION_FAIL(2007,"非法操作，没有权限！"),
    NOTIFICATION_NOT_FOUND(2008,"消息不翼而飞了！"),
    FILE_UPLOAD_FAIL(2009,"图片上传失败！")
    ;

    private Integer code;
    private String message;


    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    CustomErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
