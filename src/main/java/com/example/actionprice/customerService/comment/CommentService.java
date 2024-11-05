package com.example.actionprice.customerService.comment;

import java.util.List;
import org.springframework.data.domain.Page;

public interface CommentService {

    CommentSimpleDTO createComment(Integer postId, String username, String content);
    CommentSimpleDTO updateComment(Integer commentId, String logined_username, String content);
    CommentSimpleDTO deleteComment(Integer commentId, String logined_username) throws IllegalAccessException;

    CommentListDTO getCommentListByPostId(Integer postId, Integer pageNum);
    CommentListDTO getCommentListByUsername(String username, Integer pageNum);

    String generateAnswer(Integer postId, String answerType);
}
