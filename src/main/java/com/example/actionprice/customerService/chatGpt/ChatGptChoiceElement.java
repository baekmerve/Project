package com.example.actionprice.customerService.chatGpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 연상훈
 * @created 2024-11-08 오전 1:05
 */
@Getter
@Setter
public class ChatGptChoiceElement {

  @JsonProperty("index")
  private int index;

  @JsonProperty("message")
  private ChatGptMessage message;

  @JsonProperty("finish_reason")
  private String finish_reason;

}
