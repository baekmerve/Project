package com.example.actionprice.customerService.chatGpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 연상훈
 * @created 2024-11-08 오전 1:06
 */
@Getter
@Setter
public class ChatGptUsage {

  @JsonProperty("prompt_tokens")
  private int prompt_tokens;

  @JsonProperty("completion_tokens")
  private int completion_tokens;

  @JsonProperty("total_tokens")
  private int total_tokens;
}
