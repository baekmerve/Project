package com.example.actionprice.customerService.chatGpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

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
