package com.example.actionprice.chatGPT;

import com.example.actionprice.customerService.chatGpt.ChatGptFetcher;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class ChatGptTest {

  @Autowired
  ChatGptFetcher chatGPTFetcher;

  @Test
  @Disabled
  void testChatGTP(){
    String answer = chatGPTFetcher.generateChatGPTAnswer("김철수", "왜 이렇게 고객센터 답변이 늦는 거죠?");
    System.out.println(answer);
  }

}
