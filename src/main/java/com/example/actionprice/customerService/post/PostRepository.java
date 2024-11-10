package com.example.actionprice.customerService.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * @author 연상훈
 * @created 2024-10-27 오후 1:25
 */
public interface PostRepository extends JpaRepository<Post, Integer>{
    
    Page<Post> findByTitleContainingOrUser_UsernameContaining(String titleKeyword, String usernameKeyword, Pageable pageable);

    Page<Post> findByUser_UsernameAndTitleContaining(String username, String keyword, Pageable pageable);
    Page<Post> findByUser_Username(String username, Pageable pageable);
}
