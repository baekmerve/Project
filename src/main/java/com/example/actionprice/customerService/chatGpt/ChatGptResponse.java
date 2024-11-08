package com.example.actionprice.customerService.chatGpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 연상훈
 * @created 2024-11-08 오전 1:05
 */
@Getter
@Setter
public class ChatGptResponse {

  @JsonProperty("id")
  private String id;

  @JsonProperty("object")
  private String object;

  @JsonProperty("created")
  private long created;

  @JsonProperty("model")
  private String model;

  @JsonProperty("choices")
  private List<ChatGptChoiceElement> choices;

  @JsonProperty("usage")
  private ChatGptUsage usage;
}
