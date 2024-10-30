package com.example.actionprice.customerService.comment;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO logined_username으로 수정해달라고 부탁하기
/**
 * @author 연상훈
 * @created 2024-10-27 오후 12:13
 */
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@Log4j2
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성 기능
     * @param postId 당연히 PostDetail에서 댓글 추가/수정/삭제 등이 있을 테니, path에 이미 포함되어 있을 거임. 그리고 어느 post의 댓글로 추가될 지도 파악하기 위해 필요함
     * @param requestBody "logined_username"와 "content"가 맵 형태로 전달되어야 함
     * @value logined_username : 로그인 된 username
     * @value content : 댓글 내용
     * @author 연상훈
     * @created 2024-10-27 오후 12:14
     * @info 어차피 댓글 생성/수정/삭제는 PostDetail 안에서 이루어지고,
     * 그 결과 또한 PostDetail에서 확인하게 될 것인데,
     * 여기서 PostDetail을 반환하기에는 과정도 번거롭고 낭비가 많음
     * 차라리 postId만 반환하고 그 postId 가지고 리다이렉트해서 기존의 PostDetail에 대한 GetMapping으로 처리하게 두는 게 편하고 효율적임.
     */
    @PostMapping("/{postId}/detail")
    public Integer createComment(
            @PathVariable("postId") int postId,
            @RequestBody Map<String, String> requestBody
    ) {
        String logined_username = requestBody.get("username");
        String content = requestBody.get("content");
        log.info("[class] CommentController - [method] createComment - logined_username : {} | content : {}", logined_username, content);

        commentService.createComment(postId, logined_username, content);

        return postId;
    }

    /**
     * 댓글 수정 기능
     * @param postId 당연히 PostDetail에서 댓글 추가/수정/삭제 등이 있을 테니, path에 이미 포함되어 있을 거임. 여기선 그냥 리다이렉트용
     * @param commentId 어떤 댓글을 수정하려고 하는 지 파악하기 위한 용도
     * @param requestBody "logined_username"와 "content"가 맵 형태로 전달되어야 함
     * @value logined_username : 로그인 된 username
     * @value content : 댓글 내용
     * @author 연상훈
     * @created 2024-10-27 오후 12:14
     * @info 어차피 댓글 생성/수정/삭제는 PostDetail 안에서 이루어지고,
     * 그 결과 또한 PostDetail에서 확인하게 될 것인데,
     * 여기서 PostDetail을 반환하기에는 과정도 번거롭고 낭비가 많음
     * 차라리 postId만 반환하고 그 postId 가지고 리다이렉트해서 기존의 PostDetail에 대한 GetMapping으로 처리하게 두는 게 편하고 효율적임.
     */
    @PostMapping("/{postId}/detail/{commentId}/update")
    public Map<String, Object> updateComment(
            @PathVariable("postId") int postId,
            @PathVariable("commentId") int commentId,
            @RequestBody Map<String, String> requestBody
    ) {
        String logined_username = requestBody.get("username");
        String content = requestBody.get("content");

        commentService.updateComment(commentId, logined_username, content);

        return Map.of("postId", postId, "result", "success");
    }

    /**
     * 댓글 삭제 기능
     * @param postId 당연히 PostDetail에서 댓글 추가/수정/삭제 등이 있을 테니, path에 이미 포함되어 있을 거임. 여기선 용도가 없이 그냥 경로 지정용
     * @param commentId 어떤 댓글을 삭제하려고 하는 지 파악하기 위한 용도
     * @param requestBody "logined_username"가 맵 형태로 전달되어야 함
     * @value logined_username : 로그인 된 username
     * @author 연상훈
     * @created 2024-10-27 오후 12:14
     * @info 어차피 댓글 생성/수정/삭제는 PostDetail 안에서 이루어지고,
     * 그 결과 또한 PostDetail에서 확인하게 될 것인데,
     * 여기서 PostDetail을 반환하기에는 과정도 번거롭고 낭비가 많음
     * 차라리 postId만 반환하고 그 postId 가지고 리다이렉트해서 기존의 PostDetail에 대한 GetMapping으로 처리하게 두는 게 편하고 효율적임.
     * 이것도 리다이렉트 시키게 postId로 줄 것인지 아니면 그냥 이렇게 결과만 줄 지는 고민 중.
     * 이미 삭제된 comment 다시 삭제 못 하게 리다이렉트하는 게 좋을 것 같기는데 한데
     */
    @PostMapping("/{postId}/detail/{commentId}/delete")
    public Map<String, Object> deleteComment(
            @PathVariable("postId") int postId,
            @PathVariable("commentId") int commentId,
            @RequestBody Map<String, String> requestBody
    ) {
        String logined_username = requestBody.get("username");
        boolean isSuccess = commentService.deleteComment(commentId, logined_username);
        String result = isSuccess ? "delete comment success" : "delete comment failed";

        return Map.of("postId", postId, "result", result);
    }

    /**
     * 임시 댓글 리스트
     * @author 연상훈
     * @created 2024-10-29 오전 11:54
     * @see : 이건 기존 형태에 전혀 맞지 않고, 같은 곳으로 중복된 데이터를 전달하기 때문에 협의 후 수정하거나 삭제하거나 해야 함. 일단 프론트에 맞춰서 만들어 줬을 뿐임
     */
    // "/api/post/{postid}/comments"
    @GetMapping("/comments")
    public List<CommentSimpleDTO> getCommentList(
        @RequestParam(name = "postId", defaultValue = "0", required = false) Integer postId,
        @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
        @RequestParam(name = "size", defaultValue = "0", required = false) Integer size
    ){
        log.info("[class] CommentController - [method] getCommentList - postId : {} | page : {} | size : {}", postId, page, size);
        // post.getCommentSet()보다 Page<Comment>로 레포지토리에서 불러오는 게 훨씬 효율 좋고 편함
        Page<Comment> commentPage =
            commentService.getCommentListByPostId(postId, page);

        // true : 댓글 없음 | false : 댓글 있음
        boolean hasNoComments = (commentPage == null || !commentPage.hasContent());
        log.info("[class] CommentController - [method] getCommentList - hasNoComments : {}", hasNoComments);

        // commentPage에 아무 것도 없어도 당장은 오류가 나지 않지만,
        // 아무것도 없는 commentPage의 내부 값을 가져와서 변환하려는 시도는 오류가 나니까 이렇게 처리함
        List<CommentSimpleDTO> commentList =
            hasNoComments ? null : commentService.convertCommentPageToCommentSimpleDTOList(commentPage);
        int currentPageNum = hasNoComments ? 1 : (commentPage.getNumber() + 1);
        int currentPageSize = hasNoComments ? 0 : commentPage.getNumberOfElements();
        int listSize = hasNoComments ? 0 : commentList.size();
        int totalPageNum = hasNoComments ? 1 : commentPage.getTotalPages();

        log.info(
            "[class] CommentController - [method] getCommentList - currentPageNum : {} | currentPageSize : {} | listSize : {} | totalPageNum : {}",
            currentPageNum,
            currentPageSize,
            listSize,
            totalPageNum
        );

        return commentList;

    }
}
