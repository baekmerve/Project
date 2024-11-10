package com.example.actionprice.user;

import com.example.actionprice.sendEmail.SendEmailService;
import com.example.actionprice.user.forms.UserRegisterForm;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author : 연상훈
 * @created : 2024-10-05 오후 10:52
 * @updated : 2024-10-12 오전 00:59
 * @info @CustomRestAdvice - handlerBindException로 유효성 검사 오류를 처리하고 있으니 별도로 할 필요가 없음
 */
@RestController
@RequestMapping("/api/user")
@Log4j2
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final SendEmailService sendEmailService;

  /**
   * 로그인 기능 @PostMapping("/login")
   * @author : 연상훈
   * @created : 2024-10-06 오후 6:35
   * @see : @PostMapping("/login")
   * @info
   * 로그인 로직에 해당하는 @PostMappin("/user/login")은 CustomSecurityConfig에서 처리하기 때문에 별도의 메서드가 필요 없음.
   * 오히려 만들었다간 요청 충돌이 생김. 참고로 user login 할 때 UserLoginForm을 사용함
   */

  /**
   * 로그아웃 기능
   * @author 연상훈
   * @created 2024-10-10 오전 9:30
   * @see : @PostMapping("/logout")
   * @info CustomSecurityConfig에서 처리하기 때문에 별도의 메서드가 필요 없음.
   */

  /**
   * 회원가입 기능
   * @param userRegisterForm : UserRegisterForm을 사용해야 함
   * @author : 연상훈
   * @created : 2024-10-06 오후 8:26
   * @updated 2024-10-22 오후 2:14 : 불필요한 예외 처리를 CONFLICT 응답으로 대체함
   * @throw UsernameAlreadyExistsException
   */
  @PostMapping("/register")
  public ResponseEntity<String> register(@Valid @RequestBody UserRegisterForm userRegisterForm) {
    // 유효성 검사는 @CustomRestAdvice가 자동으로 처리함
    boolean isUserAlreadyExists = userService.createUser(userRegisterForm);
    if(isUserAlreadyExists){
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Register failed. User already exists");
    }
    String message = String.format("Register success. Welcome, %s", userRegisterForm.getUsername());
    return ResponseEntity.ok(message);
  }

  /**
   * 회원가입 시 인증코드 발송 기능
   * @param userRegisterForm : UserRegisterForm을 사용해야 함
   * @group SendVerificationCodeGroup
   * @author : 연상훈
   * @created : 2024-10-06 오후 8:24
   * @throw InvalidEmailAddressException
   * @info sendVerificationEmail 같은 경우에는 워낙 여러 가지 동작을 함께 수행해야 하고, 결과값을 전달하는 과정도 복잡해서 그냥 예외처리 해준 것
   * @info 응답 방식을 통일하고 싶었으나, 이 로직은 HttpStatus가 중요하고, 그걸 수정하려면 따로 예외 처리를 해야 함.
   * 근데 고작 컨트롤러 하나에서 딱 한 번 쓰이는 예외를 위해 따로 만들어주기에는 낭비라서 이건 그냥 통합하지 않고 그대로 두기로 함
   * @see : https://www.knotend.com/g/a#N4IgzgpgTglghgGxgLwnARgiAxA9lAWxAC5QA7XAEwjBPIEYAmRgVkYBZ6AOOkBDCAhIhALuOAVLsA+4wAJAOD2BCwZABfADQgy7AJwAGAMwA2DU1790g4SMAf3bLmAcFqniJIqYBcJwDXjAfiWqyAdh1cNDUYfLWMBIWIQAHkvNQ0Weno9PXYfMNMIkHk7QAHJqRZAEN6pHMAKhs8VNXp2FnZgrRZ0s0iADViyYJ16Qy4G0j5w4RiK9RZAxK0ePpMmkFbh0a4fdi4jKYHIwAmBwB0O2UBIOqlAEbX7SWs2vS16H0WORszABjrnKUAHGqlAB5HAH3bAA5qpQAtVtsCND4fIleqBpplACATcikgEZBwCvNeVvPF2OwdEC9LdhAAzRCQNo9apAiaYyJDbxAnSseok2ZtHRsDQpPT0GnwqRiQA7Qzk2okDHpGIyaQ4pIAM8cA3V2ACha2lclnp9OwaQAXKAAVwgdJYWkYXD0aTWGWEEAAHgBjCAAB0VMFwZCkgAyZwA1nUoKhBKABzGgkADaXsqzDYnB43k0ugMRgAuspfeptPpDIwQOT-IE6iBI9GQ3GjN4qjU6g102o-AEgiFE3EEkkUmlC2R4rzq+X2n4utwC1GKw3Uk3qmN6MTa7naiEGt4FksVgnax1Wz0m+PlhGOyM+8TvOdLtcFbWF5OmwCgSC08uNzKbkiaqj0cfowfgfRR2oCSwiTxa3ej+S-FT29Hn6+m3pAUmRZWsKR-JteUZYCbzUIDGXYZkmxlRD5VgsgoP5QVvHpLUdRrcMKjNBAEAAdRgShFQACxIeh6VUYiEAACQgGA3SoxUSGWBjBAQAAFOBKEoGAyDdEh6LUKgICiKBqFgUTvT9VgOG4HtYzDBMkxLVMLy7NIc2qYdqW8GdukfFdDH7IM1F3bM1FPLd90CQ8H33S80R8DFvH-EJrN8b9NXM+CQMgpJoOwotFlQlJAM1bVdTTRQgA
   */
  @PostMapping("/sendVerificationCode") //요청을 json 타입으로 받음
  public ResponseEntity<String> sendVerificationCode(@Validated(UserRegisterForm.SendVerificationCodeGroup.class) @RequestBody UserRegisterForm userRegisterForm) throws Exception {

    // 유효성 검사는 @CustomRestAdvice가 자동으로 처리함

    String email = userRegisterForm.getEmail();

    //이미 사용하고 있는 이메일인지 체크하는 로직
    if(userService.checkUserExistsWithEmail(email)){
      log.info("[class] UserController - [method] sendVerificationCode - email already used");
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already used");
    }

    // 발송되었으면 true, 이미 5분 내로 발송된 것이 있으면 false.
    // 이미 발송된 것이 있지만 5분이 지났으면 새로 발송해주고 true 반환.
    // 발송 실패 시 InvalidEmailAddressException으로 처리
    boolean isEmailSent = sendEmailService.sendVerificationEmail(email);

    String resultOfSending = isEmailSent ? "인증코드가 성공적으로 발송되었습니다." : "최근 5분 내로 이미 발송된 인증코드가 있습니다. 발송된 코드를 사용해주세요.";
    return ResponseEntity.ok(resultOfSending);
  }

  /**
   * 회원가입 시 인증코드 검증 기능
   * @param userRegisterForm : UserRegisterForm을 사용해야 함
   * @author : 연상훈
   * @created : 2024-10-06 오후 8:24
   */
  @PostMapping("/checkVerificationCode")
  public Map<String, String> checkVerificationCode(@Validated(UserRegisterForm.CheckValidityOfVerificationCodeGroup.class) @RequestBody UserRegisterForm userRegisterForm){
    // 유효성 검사는 @CustomRestAdvice가 자동으로 처리함
    String resultOfVerification = sendEmailService.checkVerificationCode(userRegisterForm.getEmail(), userRegisterForm.getVerificationCode());
    return Map.of("resultOfVerification", resultOfVerification);
  }

  /**
   * username 중복 체크
   * @param userRegisterForm : UserRegisterForm을 사용해야 함
   * @author 연상훈
   * @created 2024-10-10 오전 11:16
   * @updated 2024-10-10 오후 16:31
   * @group CheckForDuplicateUsernameGroup
   * @info 응답 방식을 통일하고 싶었으나, 이 로직은 HttpStatus가 중요하고, 그걸 수정하려면 따로 예외 처리를 해야 함.
   * 근데 고작 컨트롤러 하나에서 딱 한 번 쓰이는 예외를 위해 따로 만들어주기에는 낭비라서 이건 그냥 통합하지 않고 그대로 두기로 함
   */
  @PostMapping("/checkForDuplicateUsername")
  public ResponseEntity<String> checkForDuplicateUsername(@Validated(UserRegisterForm.CheckDuplicationOfUsernameGroup.class) @RequestBody UserRegisterForm userRegisterForm){

    // 유효성 검사는 @CustomRestAdvice가 자동으로 처리함
    log.info("[class] UserController - [method] checkForDuplicateUsername - operate");

    boolean useranme_already_exist = userService.checkUserExistsWithUsername(userRegisterForm.getUsername());

    // userService.checkUserExistsWithUsername()는 존재하면 true, 존재하지 않으면 false 반환
    if (useranme_already_exist) {
      log.info("[class] UserController - [method] checkForDuplicateUsername - Username already exists");
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
    }

    log.info("[class] UserController - [method] checkForDuplicateUsername - new username");
    return ResponseEntity.ok("Username is available");
  }

  /**
   * 이미 존재하는 유저인지 체크
   * @author 연상훈
   * @created 2024-11-05 오후 3:28
   * @info checkForDuplicateUsername의 반대
   */
  @PostMapping("/checkUserExists")
  public ResponseEntity<String> checkUserExists(@Validated(UserRegisterForm.CheckDuplicationOfUsernameGroup.class) @RequestBody UserRegisterForm userRegisterForm){
    log.info("[class] UserController - [method] checkUserExists");

    boolean useranme_already_exist = userService.checkUserExistsWithUsername(userRegisterForm.getUsername());

    // userService.checkUserExistsWithUsername()는 존재하면 true, 존재하지 않으면 false 반환
    if (useranme_already_exist) {
      return ResponseEntity.ok("the user exists");
    }

    return ResponseEntity.status(HttpStatus.CONFLICT).body("the user does not exist");
  }

  /**
   * 비밀변호 변경 메서드
   * @author 연상훈
   * @created 2024-11-08 오후 2:42
   * @info 검증에 UserRegisterForm을 그대로 사용함
   * @info 비밀번호 분실 상태일 수도 있으니 따로 보안은 걸지 않았음
   */
  @PostMapping("/changePassword")
  public ResponseEntity<String> changePassword(@Valid @RequestBody UserRegisterForm userRegisterForm){
    log.info("[class] UserController - [method] changePassword");

    // 유효성 검사는 @CustomRestAdvice가 자동으로 처리함
    boolean isPasswordChanged = userService.changePassword(userRegisterForm.getUsername(), userRegisterForm.getPassword());

    if(isPasswordChanged){
      return ResponseEntity.ok("Changing password success.");
    }

    return ResponseEntity.status(HttpStatus.CONFLICT).body("Changing password failed.");
  }

  /**
   * 비밀번호 변경 시 인증코드 발송 메서드
   * @author 연상훈
   * @created 2024-11-08 오후 2:43
   * @info
   */
  @PostMapping("/sendVerificationCodeForChangingPW")
  public ResponseEntity<String> sendVerificationCodeForChangingPW(
          @Validated(UserRegisterForm.SendVerificationCodeGroup.class) @RequestBody UserRegisterForm userRegisterForm
  ) throws Exception {

    // 유효성 검사는 @CustomRestAdvice가 자동으로 처리함
    log.info("[class] UserController - [method] sendVerificationCode");

    String email = userRegisterForm.getEmail();

    //이미 사용하고 있는 이메일인지 체크하는 로직
    if(!userService.checkUsernameAndEmailExists(userRegisterForm.getUsername(), email)) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("No one is using both of them(username & email)");
    }

    // 발송되었으면 true, 이미 5분 내로 발송된 것이 있으면 false.
    // 이미 발송된 것이 있지만 5분이 지났으면 새로 발송해주고 true 반환.
    // 발송 실패 시 InvalidEmailAddressException으로 처리
    boolean isEmailSent = sendEmailService.sendVerificationEmail(email);

    String resultOfSending = isEmailSent ? "인증코드가 성공적으로 발송되었습니다." : "최근 5분 내로 이미 발송된 인증코드가 있습니다. 발송된 코드를 사용해주세요.";
    return ResponseEntity.ok(resultOfSending);
  }

}
