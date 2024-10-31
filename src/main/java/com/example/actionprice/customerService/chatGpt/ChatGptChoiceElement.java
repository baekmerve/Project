package com.example.actionprice.customerService.chatGpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

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
