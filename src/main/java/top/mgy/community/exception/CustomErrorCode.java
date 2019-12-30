package top.mgy.community.exception;

/**
 * 异常枚举类
 */
public enum CustomErrorCode implements ICustomizeErrorCode {
    /**
     * 问题未找到
     */
    QUESTION_NOT_FOUND("你找的问题飞了,要不换一个试试!");

    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    CustomErrorCode(String message){
        this.message = message;
    }


}
