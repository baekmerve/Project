package com.example.actionprice.myPage;

import com.example.actionprice.customerService.post.PostService;
import com.example.actionprice.customerService.post.dto.PostListDTO;
import com.example.actionprice.exception.UserNotFoundException;
import com.example.actionprice.user.User;
import com.example.actionprice.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author 연상훈
 * @created 2024-10-27 오후 3:40
 * @info 로그인 중인 사용자와 MyPage의 주인이 일치하는지
 * controller에서 확인하고 나서 여기로 보내니까 따로 처리하지 않아도 됨
 */
@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final UserRepository userRepository;
    private final PostService postService;

    /**
     * MyPage 내에서 사용자의 개인 정보를 출력하는 기능
     * @param username
     * @author 연상훈
     * @created 2024-10-27 오후 3:41
     * @throws UserNotFoundException
     * @info Map<String, String> 형태로 결과를 반환함
     */
    @Override
    public Map<String, String> getPersonalInfo(String username) {
        log.info("[class] MyPageServiceImpl - [method] getPersonalInfo > 실행");
        User user = userRepository.findById(username)
            .orElseThrow(() -> new UserNotFoundException("user(" + username + ") does not exist"));

        return Map.of("username", username, "email", user.getEmail());
    }

    /**
     * MyPage 내 회원탈퇴(user 삭제) 기능
     * @author 연상훈
     * @created 2024-10-27 오후 3:43
     * @throws UserNotFoundException
     * @info 관심사로 따지면 UserService에 있어야 하지만,
     * 삭제는 MyPage 내에서만 가능하게 할 생각이고,
     * 그거 하나 때문에 UserService를 여기에 가져오는 건 너무 낭비라서 이렇게 함
     */
    @Override
    public void deleteUser(String username) {
        log.info("[class] MyPageServiceImpl - [method] deleteUser > 실행");
        User user = userRepository.findById(username)
            .orElseThrow(() -> new UserNotFoundException("user(" + username + ") does not exist"));

        userRepository.delete(user);
    }

    /**
     * MyPage 내 자기가 쓴 게시글 목록 출력 기능
     * @param username
     * @param keyword : 검색에 사용될 키워드. 키워드가 없는 경우에 대해서는 getPostListForMyPage 메서드 안에서 알아서 처리함
     * @param pageNum : 게시글 목록의 페이지 번호
     * @author 연상훈
     * @created 2024-10-27 오후 3:47
     */
    @Override
    public PostListDTO getMyPosts(String username, String keyword, int pageNum) {
        log.info("[class] MyPageServiceImpl - [method] getMyPosts > 실행");

        return postService.getPostListForMyPage(username, keyword, pageNum);
    }

}
