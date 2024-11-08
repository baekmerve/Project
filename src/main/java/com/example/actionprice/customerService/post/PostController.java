package com.example.actionprice.customerService.post;

import com.example.actionprice.customerService.post.dto.PostListDTO;
import com.example.actionprice.customerService.post.dto.PostSimpleDTO;
import com.example.actionprice.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/{postId}/detail")
    public PostSimpleDTO goDetailPost(
        @PathVariable("postId") Integer postId,
        @RequestParam(name = "page", defaultValue = "0", required = false) Integer page
    ) {
        log.info("goDetailPost");

        // 로그인하지 않은 사람도 볼 수는 있도록
        String logined_username = null;
        boolean isAdmin = false;

        try {
            logined_username = getUsernameWithPrincipal();
            isAdmin = isLoginedUserAdmin();
        } catch (Exception e) {
            log.info("로그인 하지 않은 사용자가 게시글에 접근함");
            log.info(e.getMessage());
        }

        log.info("로그인 한 사용자가 게시글에 접근함. logined_username : {}", logined_username);
        return postService.getDetailPost(postId, page, logined_username, isAdmin);
    }

    /**
     * 게시글 내용 수정을 위해 수정 페이지에서 보여줄 내용을 반환하는 기능. 업데이트 화면으로 이동
     * @param postId 수정할 게시글의 postId
     * @author 연상훈
     * @created 2024-10-27 오후 1:57
     * @info 그냥 post 객체를 반환하면 안 되니까 PostSimpleDTO를 반환
     */
    @PreAuthorize("isAuthenticated()")
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

        return postService.goUpdatePost(postId, getUsernameWithPrincipal(), isLoginedUserAdmin());
    }

    /**
     * 게시글 수정 기능
     * @param postId
     * @param postForm PostForm(valid : PostUpdateGroup)
     * @author 연상훈
     * @created 2024-10-27 오후 2:26
     * @info 게시글 수정 후 Map<String, Object> 형태로 처리 결과 messege와 postId를 반환함.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}/update")
    public PostSimpleDTO updatePost(
        @PathVariable("postId") Integer postId,
        @RequestBody @Validated(PostForm.PostUpdateGroup.class) PostForm postForm
    ) {

        return postService.updatePost(postId, postForm, getUsernameWithPrincipal(), isLoginedUserAdmin());
    }

    /**
     * 게시글 삭제 기능
     * @param postId
     * @author 연상훈
     * @created 2024-10-27 오후 2:45
     * @info 게시글 수정 후 처리 결과 messege를 반환함
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}/delete")
    public PostSimpleDTO deletePost(
        @PathVariable("postId") Integer postId
    ) {
        return postService.deletePost(postId, getUsernameWithPrincipal(), isLoginedUserAdmin());
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

    private String getUsernameWithPrincipal(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    private boolean isLoginedUserAdmin(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority(UserRole.ROLE_ADMIN.name()); // 비교대상
        return userDetails.getAuthorities().contains(adminAuthority);
    }

}
