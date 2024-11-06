package com.example.actionprice.customerService.post;

import com.example.actionprice.customerService.post.dto.PostListDTO;
import com.example.actionprice.customerService.post.dto.PostSimpleDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public PostSimpleDTO createPost(@RequestBody @Validated(PostForm.PostCreateGroup.class) PostForm postForm){
        log.info(
            "[class] PostController - [method] createPost - username : {}, | title : {} | content : {}",
            postForm.getUsername(),
            postForm.getTitle(),
            postForm.getContent()
        );
        return postService.createPost(postForm);
    }

    /**
     * 게시글 보기 기능
     * @param postId 내용을 확인할 게시글의 postId
     * @author 연상훈
     * @created 2024-10-27 오후 1:53
     * @see
     * "/api/post/{postId}/detail?commentPageNum=0" 같은 방식으로 호출해야 함
     * commentPageNum은 선택사항. 없으면 0으로 처리
     */
    @PreAuthorize("@postServiceImpl.isPostPublished(#postId) or hasRole('ROLE_ADMIN') or @postServiceImpl.checkPostOwner(#postId, authentication.principal.getUsername())")
    @GetMapping("/{postId}/detail")
    public PostSimpleDTO goDetailPost(
        @PathVariable("postId") Integer postId,
        @RequestParam(name = "page", defaultValue = "0", required = false) Integer page
    ) {
        log.info("goDetailPost");
        return postService.getDetailPost(postId, page);
    }

    /**
     * 게시글 내용 수정을 위해 수정 페이지에서 보여줄 내용을 반환하는 기능. 업데이트 화면으로 이동
     * @param postId 수정할 게시글의 postId
     * @author 연상훈
     * @created 2024-10-27 오후 1:57
     * @info 그냥 post 객체를 반환하면 안 되니까 PostSimpleDTO를 반환
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or @postServiceImpl.checkPostOwner(#postId, authentication.principal.getUsername())")
    @GetMapping("/{postId}/update/{username}")
    public PostSimpleDTO goUpdatePost(
        @PathVariable("postId") Integer postId,
        @PathVariable("username") String username
    ) {
        log.info("goUpdatePost");

        log.info(
            "[class] PostController - [method] deletePost - id : {} | username : {}",
            postId,
            username)
        ;

        return postService.goUpdatePost(postId, username);
    }

    /**
     * 게시글 수정 기능
     * @param postId
     * @param postForm PostForm(valid : PostUpdateGroup)
     * @author 연상훈
     * @created 2024-10-27 오후 2:26
     * @info 게시글 수정 후 Map<String, Object> 형태로 처리 결과 messege와 postId를 반환함.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or @postServiceImpl.checkPostOwner(#postId, authentication.principal.getUsername())")
    @PostMapping("/{postId}/update")
    public PostSimpleDTO updatePost(
        @PathVariable("postId") Integer postId,
        @RequestBody @Validated(PostForm.PostUpdateGroup.class) PostForm postForm
    ) {

        return postService.updatePost(postId, postForm);
    }

    /**
     * 게시글 삭제 기능
     * @param postId
     * @author 연상훈
     * @created 2024-10-27 오후 2:45
     * @info 게시글 수정 후 처리 결과 messege를 반환함
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or @postServiceImpl.checkPostOwner(#postId, authentication.principal.getUsername())")
    @PostMapping("/{postId}/delete")
    public PostSimpleDTO deletePost(
        @PathVariable("postId") Integer postId
    ) {
        return postService.deletePost(postId);
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
        @RequestParam(name = "pageNum", defaultValue = "0", required = false) Integer pageNum,
        @RequestParam(name = "keyword", defaultValue = "", required = false) String keyword
    ) {
        log.info(
            "[class] PostController - [method] getPostList - page : {} | keyword : {}",
            pageNum,
            keyword
        );
        return postService.getPostList(pageNum, keyword);
    }

}
