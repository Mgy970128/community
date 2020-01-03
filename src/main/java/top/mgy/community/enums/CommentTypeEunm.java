package top.mgy.community.enums;

public enum CommentTypeEunm {
    QUESTION(1),
    COMMENT(2);

    private Integer type;

    public static boolean isExist(Integer type) {

        for (CommentTypeEunm commentTypeEunm : CommentTypeEunm.values()) {
            if(commentTypeEunm.getType().equals(type)){
                return true;
            }
        }

        return false;
    }

    public Integer getType() {
        return type;
    }

    CommentTypeEunm(Integer type) {
        this.type = type;
    }
}
