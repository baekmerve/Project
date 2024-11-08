package com.example.actionprice.myPage;

import com.example.actionprice.customerService.post.dto.PostListDTO;
import com.example.actionprice.user.UserRole;
import com.example.actionprice.user.favorite.FavoriteService;
import com.example.actionprice.user.favorite.FavoriteSimpleDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 연상훈
 * @created 2024-10-27 오후 3:30
 * @info
 * request 내 토큰 추출 및 검사는
 * service의 관심사에 맞지 않는 것 같아서 controller에서 해결
 * 그리고 그렇게 하니까 양쪽 따 훨씬 깔끔함
 */
@RestController
@RequestMapping("/api/mypage")
@Log4j2
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final FavoriteService favoriteService;

    /**
     * 자신의 MyPage의 개인정보 확인 페이지로 이동하는 기능
     * @param username : MyPage의 주인의 username
     * @author 연상훈
     * @created 2024-10-27 오후 3:34
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{username}/personalinfo")
    public Map<String, String> goPersonalInfo(
        @PathVariable("username") String username
    ) {
        log.info("[class] MyPageController - [method] goPersonalInfo > 실행");
        checkQualification(username);
        return myPageService.getPersonalInfo(username);
    }
    
    /**
     * 자신의 MyPage의 게시글 확인 페이지로 이동하는 기능
     * @param username : MyPage의 주인의 username
     * @author 연상훈
     * @created 2024-10-27 오후 3:34
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{username}/myposts")
    public PostListDTO goMyPosts(
             @PathVariable("username") String username,
             @RequestParam(required = false, name = "keyword") String keyword,
             @RequestParam(required = false, name = "pageNum", defaultValue = "0") int pageNum)
    {
        log.info("[class] MyPageController - [method] goMyPosts > 실행");
        checkQualification(username);
        return myPageService.getMyPosts(username, keyword, pageNum);
    }

    /**
     * 자신의 MyPage의 즐겨찾기 목록 확인 페이지로 이동하는 기능
     * @param username : MyPage의 주인의 username
     * @author 연상훈
     * @created 2024-10-27 오후 3:34
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{username}/wishlist")
    public List<FavoriteSimpleDTO> getMyWishlist(@PathVariable("username") String username) {
        log.info("[class] MyPageController - [method] getMyWishlist > 실행");
        checkQualification(username);
        return favoriteService.getFavoriteList(username);
    }

    /**
     * 자신의 MyPage의 회원탈퇴(user 삭제) 기능
     * @param username : MyPage의 주인의 username
     * @author 연상훈
     * @created 2024-10-27 오후 3:34
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/{username}/deleteUser")
    public String deleteUser(@PathVariable("username") String username){
        log.info("[class] MyPageController - [method] deleteUser > 실행");
        checkQualification(username);
        myPageService.deleteUser(username);

        return "delete user : success";
    }

    /**
     * 로그인한 사용자가 마이페이지에 접근할 자격이 있는지 확인하는 메서드
     * @throws AccessDeniedException
     * @author 연상훈
     * @created 2024-11-08 오전 11:07
     * @info 마이페이지 주인 or 어드민만 접근 가능
     * @info 권한 없으면 예외 발생
     */
    private void checkQualification(String username){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String logined_username = userDetails.getUsername();

        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority(UserRole.ROLE_ADMIN.name()); // 비교대상
        boolean isAdmin = userDetails.getAuthorities().contains(adminAuthority);

        if(username.equals(logined_username) || isAdmin){
            return;
        }

        String messege = String.format("you are not allowed to access %s's myPage", username);
        throw new AccessDeniedException(messege);
    }

}
