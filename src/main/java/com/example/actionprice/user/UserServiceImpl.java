package com.example.actionprice.user;

import com.example.actionprice.admin.UserListDTO;
import com.example.actionprice.exception.UserNotFoundException;
import com.example.actionprice.user.forms.UserRegisterForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : 연상훈
 * @created : 2024-10-06 오후 9:17
 * @updated : 2024-10-10 오전 11:07
 */
@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * 유저 생성 기능. 대체로 회원가입
   * @param userRegisterForm
   * @author 연상훈
   * @created 2024-10-10 오전 11:05
   * @updated 2024-10-22 오후 2:13 : 반환 타입을 boolean으로 변경
   * @throw UsernameAlreadyExistsException
   */
  @Transactional
  @Override
  public boolean createUser(UserRegisterForm userRegisterForm) {
    log.info("--------------- [UserService] createUser ----------------");
    log.info("userRegisterForm: " + userRegisterForm);

    String inputed_username = userRegisterForm.getUsername();
    log.info("inputed_username: " + inputed_username);
    User existing_user = userRepository.findById(inputed_username).orElse(null);

    // 이미 존재하는 유저라면
    if(existing_user != null) {
      log.info(inputed_username + " already exists");
      return true;
    }

    log.info(userRegisterForm.getUsername() + " is new user");
    // user 구성
    User newUser = User.builder()
        .username(userRegisterForm.getUsername())
        .password(passwordEncoder.encode(userRegisterForm.getPassword()))
        .email(userRegisterForm.getEmail())
        .build();

    // 권한은 일반 유저. 사용자 권한을 줄 때는 반드시 UserRole 사용
    newUser.setUserRoles(false);

    // 저장
    userRepository.save(newUser);

    String result_str = newUser.getUsername() + "register successful";

    log.info(result_str);

    return false;
  }

  /**
   * 유저 로그인 기능
   * @author 연상훈
   * @created 2024-10-06 오후 9:17
   * @info 로그인 기능은 CustomSecurity와 LoginFilter로 처리하기 때문에 별도로 사용할 필요가 없음.
   */

  /**
   * 유저 로그아웃 기능
   * @author 연상훈
   * @created 2024-10-10 오전 10:23
   * @see : 로그아웃 기능은 CustomSecurity로 처리하기 때문에 별도로 사용할 필요가 없음.
   */

  /**
   * 해당 username을 가진 사용자가 존재하는지 체크하는 메서드.
   * @param username
   * @author 연상훈
   * @created 2024-10-10 오전 10:25
   * @updated 2024-10-10 오전 10:25
   * @see :
   * 존재하면 true / 존재하지 않으면 false 반환
   * 재사용 가능성이 높은 메서드인 만큼, 간단하게 username만 입력 받도록 구성
   */
  @Override
  public boolean checkUserExistsWithUsername(String username) {

    log.info("--------------- [UserService] check User Exists With Username ----------------");

    log.info("inputed_username: " + username);
    User existing_user = userRepository.findById(username).orElse(null);

    if(existing_user != null) {
      return true;
    }

    return false;
  }

  /**
   * 해당 email을 사용 중인 사용자가 존재하는지 체크하는 메서드.
   * @param email
   * @author 연상훈
   * @created 2024-10-10 오전 10:25
   * @updated 2024-10-10 오전 10:25
   * @see :
   * 존재하면 true / 존재하지 않으면 false 반환
   * 재사용 가능성이 높은 메서드인 만큼, 간단하게 email만 입력 받도록 구성
   */
  @Override
  public boolean checkUserExistsWithEmail(String email) {
    log.info("--------------- [UserService] check User Exists With Email ----------------");

    log.info("inputed_email: " + email);
    // DB에서 이메일로 유저 검색
    User existing_user = userRepository.findByEmail(email).orElse(null);

    if(existing_user != null) {
      return true;
    }

    return false;
  }

  @Override
  public boolean checkUsernameAndEmailExists(String username, String email) {

    User existing_user = userRepository.findByEmail(email).orElse(null);

    if(existing_user == null) {
      return false;
    }

    if(existing_user.getEmail().equals(email)) {
     return true;
    } else {
      return false;
    }
  }

  @Override
  public UserListDTO getUserList(String keyword, int pageNum) {
    log.info("[class] UsertServiceImpl - [method] getUserList -  - page : {} | keyword : {}", pageNum, keyword);
    Pageable pageable = PageRequest.of(pageNum, 10, Sort.by(Sort.Order.asc("username")));
    Page<User> userPage = null;

    if (keyword == null || keyword.isEmpty()) {
      keyword = "";
      userPage = userRepository.findAll(pageable);
    } else {
      userPage = userRepository.findByUsernameContainingOrEmailContaining(keyword, keyword, pageable);
    }

    UserListDTO listDTO = new UserListDTO(userPage, keyword);
    log.info("UserListDTO : " + listDTO.toString());

    return listDTO;
  }

  @Override
  public boolean changePassword(String username, String newPassword) {
    User user = userRepository.findById(username).orElse(null);

    if(user == null) {
      return false;
    }

    user.setPassword(passwordEncoder.encode(newPassword));

    // 로그인 실패해서 바꾸는 것일 수도 있으니, 기존 기록 초기화
    user.setLoginFailureCount(0);
    user.setLockedAt(null);
    userRepository.save(user);

    return true;
  }
}
