package com.example.actionprice.customerService.chatGpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatGptMessage {
  @JsonProperty("role")
  private String role;

  @JsonProperty("content")
  private String content;
}
