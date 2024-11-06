package com.example.actionprice.customerService.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostForm {

    @NotNull(groups ={PostCreateGroup.class, PostUpdateGroup.class})
    @NotBlank(groups ={PostCreateGroup.class, PostUpdateGroup.class})
    private String username;

    @NotNull(groups = {PostCreateGroup.class, PostUpdateGroup.class})
    @NotBlank(groups = {PostCreateGroup.class, PostUpdateGroup.class})
    private String title;

    @NotNull(groups = {PostCreateGroup.class, PostUpdateGroup.class})
    @NotBlank(groups = {PostCreateGroup.class, PostUpdateGroup.class})
    private String content;

    // 원래 이것도 넣어야 하지만 프론트에서 준비가 안 되어 있어서 잠시 빼둠. 나중에 넣어야 함
    @NotNull(groups = {PostCreateGroup.class})
    private boolean published;

    public interface PostCreateGroup{}
    public interface PostUpdateGroup {}
}
