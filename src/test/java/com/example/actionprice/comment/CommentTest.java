package com.example.actionprice.comment;

import com.example.actionprice.customerService.comment.CommentService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class CommentTest {

    @Autowired
    CommentService commentService;

    @Test
    @Disabled
    void testCreateCommentALot() {
        int count = 0;
        for (int i = 145; i > 120; i--) {
            for(int j=0; j<30; j++){
                String content = String.format("post %d - comment content - %d", i, count);
                commentService.createComment(i, "useraaaa", content);
                count++;
            }
        }
    }
}
