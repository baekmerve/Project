package com.example.actionprice.customerService.chatGpt;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Setter
@RequiredArgsConstructor
public class ChatGptFetcher {

  @Value("${customerService.chatGpt.apiKey}")
  private String apiKey;

  @Value("${customerService.chatGpt.url}")
  private String apiUrl;

  @Value("${customerService.chatGpt.model}")
  private String apiModel;

  public String generateChatGPTAnswer(String username, String content){

    String jsonPayload = composeJsonPayload(username, content);

    WebClient webClient = WebClient.builder()
        .baseUrl(apiUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
        .build();

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
