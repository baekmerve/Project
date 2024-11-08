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
public class ChatGptMessage {
  @JsonProperty("role")
  private String role;

  @JsonProperty("content")
  private String content;
}
