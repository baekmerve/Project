package com.example.actionprice.post;

import com.example.actionprice.customerService.post.PostForm;
import com.example.actionprice.customerService.post.PostService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class PostTest {

    @Autowired
    PostService postService;

    @Test
    @Disabled
    void testCreatePostsALot() {
        for (int i = 0; i < 150; i++) {
            String title = String.format("post title %d", i);
            String content = String.format("post content %d", i);
            boolean published = i%2 == 0;

            PostForm postForm = new PostForm("useraaaa", title, content, published);
            postService.createPost(postForm);
        }
    }

}
