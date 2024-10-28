package com.example.actionprice.customerService.post;

import com.example.actionprice.customerService.post.dto.PostDetailDTO;
import com.example.actionprice.customerService.post.dto.PostListDTO;
import com.example.actionprice.customerService.post.dto.PostSimpleDTO;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author 연상훈
 * @created 2024-10-27 오후 1:24
 */
@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    /**
     * 게시글 생성 기능
     * @param postForm PostForm(valid : PostCreateGroup)
     * @author 연상훈
     * @created 2024-10-27 오후 1:45
     * @see :
     * 게시글 생성 후 바로 PostDetailDTO를 반환하는 것보다
     * postId를 반환하고, 그걸 가지고 리다이렉트해서 goDetailPost 메서드를 사용하는 것이 훨씬 효율적이라서
     * postId만 반환함
     */
    @PostMapping(value = "/create" , consumes = MediaType.APPLICATION_JSON_VALUE)
    public PostSimpleDTO createPost(@RequestBody @Validated(PostForm.PostCreateGroup.class) PostForm postForm){
        log.info("[class] PostController - [method] createPost - username : {}, | title : {} | content : {}", postForm.getUsername(), postForm.getTitle(), postForm.getContent());
        return postService.createPost(postForm);
    }

    /**
     * 게시글 보기 기능
     * @param postId 내용을 확인할 게시글의 postId
     * @param commentPageNum comment가 많아서 페이지가 있다면, 확인할 comment 페이지 번호 
     * @author 연상훈
     * @created 2024-10-27 오후 1:53
     * @see
     * "/api/post/{postId}/detail?commentPageNum=0" 같은 방식으로 호출해야 함
     * commentPageNum은 선택사항. 없으면 0으로 처리
     */
    @GetMapping(value = "/{postId}/detail")
    public PostDetailDTO goDetailPost(
        @PathVariable("postId") Integer postId,
        @RequestParam(name = "commentPageNum", defaultValue = "1", required = false) Integer commentPageNum
    ) {
        log.info("goDetailPost");
        return postService.getDetailPost(postId, commentPageNum);
    }

    /**
     * 게시글 내용 수정을 위해 수정 페이지에서 보여줄 내용을 반환하는 기능
     * @param postId 수정할 게시글의 postId
     * @param requestBody Map<String, String> 형태이며, 반드시 logined_username를 담고 있어야 함
     * @author 연상훈
     * @created 2024-10-27 오후 1:57
     * @info 그냥 post 객체를 반환하면 안 되니까 PostSimpleDTO를 반환
     */
    @GetMapping(value = "/{postId}/update")
    public PostSimpleDTO goUpdatePost(
        @PathVariable("postId") Integer postId,
        @RequestBody Map<String, String> requestBody
    ) {
        log.info("goUpdatePost");

        String logined_username = requestBody.get("logined_username");

        log.info("[class] PostController - [method] deletePost - id : {} | username : {}", postId, logined_username);

        return postService.goUpdatePost(postId, logined_username);
    }

    /**
     * 게시글 수정 기능
     * @param postId
     * @param postForm PostForm(valid : PostUpdateGroup)
     * @author 연상훈
     * @created 2024-10-27 오후 2:26
     * @info 게시글 수정 후 Map<String, Object> 형태로 처리 결과 messege와 postId를 반환함.
     */
    @PostMapping(value = "/{postId}/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> updatePost(
        @PathVariable("postId") Integer postId,
        @RequestBody @Validated(PostForm.PostUpdateGroup.class) PostForm postForm
    ) {
        String messege = postService.updatePost(postId, postForm);

        return Map.of("message", messege, "postId", postId);
    }

    /**
     * 게시글 삭제 기능
     * @param postId
     * @param requestBody Map<String, String> 형태이며, 반드시 logined_username를 담고 있어야 함
     * @author 연상훈
     * @created 2024-10-27 오후 2:45
     * @info 게시글 수정 후 처리 결과 messege를 반환함
     */
    @PostMapping(value = "/{postId}/delete" ,consumes = MediaType.APPLICATION_JSON_VALUE)
    public String deletePost(
        @PathVariable("postId") Integer postId,
        @RequestBody Map<String, String> requestBody
    ) {
        String logined_username = requestBody.get("logined_username");

        log.info("[class] PostController - [method] deletePost - id : {} | username : {}", postId, logined_username);

        return postService.deletePost(postId, logined_username);
    }

    /**
     * 게시글 목록 출력 기능
     * @param pageNum 선택사항, 기본값 0
     * @param keyword 선택사항
     * @author 연상훈
     * @created 2024-10-27 오후 2:47
     * @see "/api/post/list?pageNum=0&keyword=abc" 형태로 입력해야 함.
     */
    @GetMapping("/list")
    public PostListDTO getPostList(
        @RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum,
        @RequestParam(name = "keyword", required = false) String keyword
    ) {
        log.info("[class] PostController - [method] getPostList - page : {} | keyword : {}", pageNum, keyword);
        return postService.getPostList(pageNum, keyword);
    }

}
