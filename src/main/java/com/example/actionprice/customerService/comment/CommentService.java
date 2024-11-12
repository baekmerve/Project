package com.example.actionprice.customerService.comment;

public interface CommentService {

    CommentSimpleDTO createComment(Integer postId, String username, String content);
    CommentSimpleDTO updateComment(Integer commentId, String content, String logined_username);
    CommentSimpleDTO deleteComment(Integer commentId, String logined_username, boolean isAdmin) throws IllegalAccessException;

    CommentListDTO getCommentListByPostId(Integer postId, Integer pageNum, String logined_username, boolean isAdmin);
    CommentListDTO getCommentListByUsername(String username, Integer pageNum);

    String generateAnswer(Integer postId, String answerType);
}
