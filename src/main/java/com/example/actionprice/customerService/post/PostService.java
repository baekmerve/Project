package com.example.actionprice.customerService.post;

import com.example.actionprice.customerService.post.dto.PostListDTO;
import com.example.actionprice.customerService.post.dto.PostSimpleDTO;

public interface PostService {

    PostSimpleDTO createPost(PostForm form);
    PostSimpleDTO goUpdatePost(Integer postId, String logined_username, boolean isAdmin);
    PostSimpleDTO updatePost(Integer postId, PostForm form, String logined_username, boolean isAdmin);
    PostSimpleDTO deletePost(Integer postId, String logined_username, boolean isAdmin);
    PostSimpleDTO getDetailPost(Integer postId, int page, String logined_username, boolean isAdmin);

    PostListDTO getPostList(int pageNum, String keyword);
    PostListDTO getPostListForMyPage(String username, String keyword, Integer pageNum);

}
