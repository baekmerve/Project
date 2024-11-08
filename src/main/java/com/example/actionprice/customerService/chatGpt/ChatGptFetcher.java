package com.example.actionprice.customerService.chatGpt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * chat gpt에게 질문하고, 답변을 듣기 위한 fetcher
 * @author 연상훈
 * @created 2024-11-08 오전 1:05
 * @info 쓸 때마다 돈 나가니까 사용에 주의
 */
@Component
public class ChatGptFetcher {

  private String apiKey;
  private String apiUrl;
  private String apiModel;
  private WebClient webClient;

  public ChatGptFetcher(
      @Value("${customerService.chatGpt.apiKey}") String apiKey,
      @Value("${customerService.chatGpt.url}") String apiUrl,
      @Value("${customerService.chatGpt.model}") String apiModel
  ) {

    this.apiKey = apiKey;
    this.apiUrl = apiUrl;
    this.apiModel = apiModel;

    this.webClient = WebClient.builder()
        .baseUrl(this.apiUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.apiKey)
        .build();
  }

  /**
   * chat gpt한테서 질문하고 응답을 받아옴
   * @param username 우리 서비스에서는 컴플레인을 넣은 고객님의 username
   * @param content 그리고 컴플레인의 내용
   * @author 연상훈
   * @created 2024-11-08 오전 1:06
   */
  public String generateChatGPTAnswer(String username, String content){

    String jsonPayload = composeJsonPayload(username, content);

    ResponseEntity<ChatGptResponse> responseEntity = webClient.post()
        .bodyValue(jsonPayload)
        .retrieve()
        .toEntity(ChatGptResponse.class)
        .block();

    if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
      return responseEntity.getBody().getChoices().get(0).getMessage().getContent();
    }

    return "sorry, we couldn't get ChatGPT's answer";
  }

  /**
   * chat gpt한테서 질문할 payload를 구성하는 메서드
   * @param username 우리 서비스에서는 컴플레인을 넣은 고객님의 username
   * @param content 그리고 컴플레인의 내용
   * @author 연상훈
   * @created 2024-11-08 오전 1:06
   */
  private String composeJsonPayload(String username, String content){
    String jsonPayload = String.format(
        """
        {
          "model": "%s",
          "messages": [
            {"role": "system", "content": "You are a helpful assistant for customer service inquiries. Provide accurate and empathetic responses to customer questions. Please respond in Korean"},
            {"role": "user", "content": "username : %s, question : %s"}
          ],
          "temperature": 0.7,
          "max_tokens": 500
        }
        """,
        apiModel,
        username,
        content
    );

    return jsonPayload;
  }

}
