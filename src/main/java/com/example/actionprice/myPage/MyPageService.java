package com.example.actionprice.myPage;

import com.example.actionprice.customerService.post.dto.PostListDTO;
import java.util.Map;

public interface MyPageService {
    Map<String, String> getPersonalInfo(String username);
    void deleteUser(String username);
    PostListDTO getMyPosts(String username, String keyword, int pageNum);
}
