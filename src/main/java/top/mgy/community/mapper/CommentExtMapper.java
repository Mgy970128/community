package top.mgy.community.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import top.mgy.community.model.Comment;
import top.mgy.community.model.CommentExample;

import java.util.List;

public interface CommentExtMapper {
  int incCommentCount(Comment comment);
}