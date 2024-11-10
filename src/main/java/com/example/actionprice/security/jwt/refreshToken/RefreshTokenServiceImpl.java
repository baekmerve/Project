package com.example.actionprice.security.jwt.refreshToken;

import com.example.actionprice.exception.RefreshTokenException;
import com.example.actionprice.exception.RefreshTokenException.ErrorCase;
import com.example.actionprice.exception.UserNotFoundException;
import com.example.actionprice.security.jwt.JWTUtil;
import com.example.actionprice.user.User;
import com.example.actionprice.user.UserRepository;
import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 리프레시 토큰 서비스
 * @value refreshTokenValidityInMinutes : 리프레시 토큰 유효 시간 : 180(분)
 * @value refreshTokenRepository
 * @value userRepository
 * @value jwtUtil
 * @author 연상훈
 * @created 2024-10-19 오후 5:25
 * @info jwtUtil을 유일하게 관리하고 활용함.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final JWTUtil jwtUtil;

  private final int refreshTokenValidityInMinutes = 60*3; // 180분. 분단위

  // public method
  /**
   * 로그인 석세스 핸들러에서 토큰 발급에 사용되 메서드
   * @param username
   * @author 연상훈
   * @created 2024-10-20 오후 1:02
   * @info 리프레시 토큰 발급 받으려고 로그인하는 거니까 리프레시 토큰에 대한 검증이 거의 필요 없음.
   * @info 토큰이 없으면(refreshToken = null) 새로 발급해주고, 만약 이미 있는 게 블록된 것인지만 체크하면 됨
   */
  @Override
  public User issueRefreshToken(String username) {

    User user = userRepository.findById(username)
        .orElseThrow(() -> new UserNotFoundException("user(" + username + ") does not exist"));

    RefreshTokenEntity refreshTokenEntity = user.getRefreshToken();

    // 토큰이 존재하는지 체크
    if(refreshTokenEntity == null) {
      // 로그인 로직이기 때문에 없으면 새로 발급해줘야 함
      refreshTokenEntity = RefreshTokenEntity.builder()
          .user(user)
          .refreshToken(jwtUtil.generateToken(user, refreshTokenValidityInMinutes))
          .expiresAt(LocalDateTime.now().plusMinutes(refreshTokenValidityInMinutes))
          .build();

      // user 안에 넣으려면 pk 값이 있어야 하는데, 레포지토리에 들어가고 난 다음에야 pk 값이 생겨서 이렇게 함
      refreshTokenEntity = refreshTokenRepository.save(refreshTokenEntity);

      user.setRefreshToken(refreshTokenEntity);
      userRepository.save(user);

      // 어차피 새 토큰이라 유효성 검사 할 필요도 없으니 바로 리턴
      return user;
    }

    // 리프레시 토큰 재발급 받으려고 하거나 아직 발급 받은 적 없는 사람일 수 있으니,
    // 블록 여부와 위변조 여부만 확인.
    checkRefreshToken_and_reissue(user);

    return user;
  }

  /**
   * 리프레시 토큰 필터에서 엑세스 토큰 발급에 사용되는 메서드
   * @param username
   * @author 연상훈
   * @created 2024-10-20 오후 1:05
   * @info 엑세스 토큰에 대한 검증은 리프레시 토큰 필터에서 진행함
   * @info 이 메서드는 엑세스 토큰 발급만 처리해야 하고, 리프레시 토큰에 문제 있으면 바로 예외 처리 해야 함
   * @info 그래서 토큰 검사가 가장 엄격해야 함
   */
  @Override
  public User checkRefreshFirst(String username) {

    log.info("username : " + username);

    User user = userRepository.findById(username)
        .orElseThrow(() -> new UserNotFoundException("user(" + username + ") does not exist"));
    RefreshTokenEntity refreshTokenEntity = user.getRefreshToken();
    log.info("refresh_token : " + refreshTokenEntity.toString());

    // 토큰이 존재하는지 체크
    if(refreshTokenEntity == null) {
      throw new RefreshTokenException(ErrorCase.NO_REFRESH);
    }

    // 그 외 모든 레프레시 토큰 검사 실시
    checkRefreshToken(refreshTokenEntity);

    return user;
  }

  @Override
  public boolean setBlockUserByUsername(String username) {
    RefreshTokenEntity refreshToken = refreshTokenRepository.findByUser_Username(username)
        .orElseThrow(() -> new UserNotFoundException("user(" + username + ") does not exist"));

    boolean currentState = refreshToken.isBlocked();
    refreshToken.setBlocked(!currentState);

    refreshTokenRepository.save(refreshToken);

    return !currentState;
  }

  /**
   * 어드민페이지에서 토큰 재발급
   * @author 연상훈
   * @created 2024-10-28 오후 7:41
   * @info 어드민페이지에서 해주는 거라 검사도 없이 바로 재발급됨
   */
  @Override
  public void resetRefreshToken(String username) {
    User user = userRepository.findById(username)
        .orElseThrow(() -> new UserNotFoundException("user(" + username + ") does not exist"));

    // user와 token은 OneToOne 관계인데,
    // 새로운 애가 user랑 연결된 상태로 repository에 등록되면 오류 날 테니
    // 기존의 연결을 끊고 지워버림.
    // 리프레시 토큰 재발급은 토큰 탈취 시 사용하기 때문에 기존의 것이 있으면 지워버리는 게 좋음
    // 근데 신규 유저가 토큰 리셋 할 리는 없으니 애초에 기존 유저를 상정해도 될 것 같은데
    RefreshTokenEntity oldRefreshToken = user.getRefreshToken();
    if(oldRefreshToken != null) {
      user.setRefreshToken(null);
      refreshTokenRepository.delete(oldRefreshToken);
      refreshTokenRepository.flush(); // 이것을 즉시 반영함
    }

    RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
        .user(user)
        .refreshToken(jwtUtil.generateToken(user, refreshTokenValidityInMinutes))
        .expiresAt(LocalDateTime.now().plusMinutes(refreshTokenValidityInMinutes))
        .build();

    // user 안에 넣으려면 pk 값이 있어야 하는데, 레포지토리에 들어가고 난 다음에야 pk 값이 생겨서 이렇게 함
    refreshTokenEntity = refreshTokenRepository.save(refreshTokenEntity);

    user.setRefreshToken(refreshTokenEntity);
    userRepository.save(user);
  }

  // private method
  /**
   * 입력 받은 리프레시 토큰에 대해 토큰 관련 모든 유효성 검사를 진행하는 메서드
   * @param refreshTokenEntity
   * @author 연상훈
   * @created 2024-10-20 오후 3:08
   * @info 리프레시 토큰을 통한 엑세스 토큰 재발급 과정에서 사용하기 때문에 가장 엄격하게 검사함
   */
  private void checkRefreshToken(RefreshTokenEntity refreshTokenEntity) {

    if (refreshTokenEntity.isBlocked()) {
      throw new RefreshTokenException(ErrorCase.BLOCKED_REFRESH);
    }

    try {
      jwtUtil.validateToken(refreshTokenEntity.getRefreshToken());
    } catch (ExpiredJwtException e) {
      log.error("만료된 리프레시 토큰입니다.");
      throw new RefreshTokenException(ErrorCase.EXPIRED_REFRESH);
    } catch (UnsupportedJwtException e) {
      log.error("지원되지 않는 형식입니다. 위변조 가능성이 있는 리프레시 토큰입니다.");
      throw new RefreshTokenException(ErrorCase.INVALID_REFRESH);
    } catch (MalformedJwtException e) {
      log.error("잘못된 형식입니다. 위변조 가능성이 있는 리프레시 토큰입니다.");
      throw new RefreshTokenException(ErrorCase.INVALID_REFRESH);
    } catch (SignatureException e) {
      log.error("잘못된 서명입니다. 위변조 가능성이 있는 리프레시 토큰입니다.");
      throw new RefreshTokenException(ErrorCase.BADSIGN_REFRESH);
    } catch (ClaimJwtException e) {
      log.error("잘못된 클레임입니다. 위변조 가능성이 있는 리프레시 토큰입니다.");
      throw new RefreshTokenException(ErrorCase.UNEXPECTED_REFRESH);
    } catch (JwtException e){
      log.error("리프레시 토큰 검사 중 기타 오류가 발생하였습니다.");
      throw new RefreshTokenException(ErrorCase.NO_ACCESS);
    }
  }

  /**
   * 입력 받은 리프레시 토큰에 대해 토큰 관련 일부 유효성 검사를 진행하는 메서드
   * @param user
   * @author 연상훈
   * @created 2024-10-20 오후 3:08
   * @info 리프레시 토큰 발급/재발급 또는 삭제 시 사용하기 때문에 위변조 여부만 검사함
   * @info 위변조 된 리프레시 토큰 가져와서 멀쩡한 것으로 교환하려는 시도를 차단해야 하니까
   */
  private void checkRefreshToken_and_reissue(User user) {

    RefreshTokenEntity refreshTokenEntity = user.getRefreshToken();
    log.info("checkRefreshToken_partially - refresh token : " + refreshTokenEntity);
    if (refreshTokenEntity.isBlocked()) {
      throw new RefreshTokenException(ErrorCase.BLOCKED_REFRESH);
    }

    try {
      jwtUtil.validateToken(refreshTokenEntity.getRefreshToken());

      // 우선 유효성 검사부터 진행하고
    } catch (UnsupportedJwtException e) {
      log.error("지원되지 않는 형식입니다. 위변조 가능성이 있는 리프레시 토큰입니다.");
      throw new RefreshTokenException(ErrorCase.INVALID_REFRESH);
    } catch (MalformedJwtException e) {
      log.error("잘못된 형식입니다. 위변조 가능성이 있는 리프레시 토큰입니다.");
      throw new RefreshTokenException(ErrorCase.INVALID_REFRESH);
    } catch (SignatureException e) {
      log.error("잘못된 서명입니다. 위변조 가능성이 있는 리프레시 토큰입니다.");
      throw new RefreshTokenException(ErrorCase.BADSIGN_REFRESH);
    } catch (ExpiredJwtException e){
      // 유효성 검사가 끝난 뒤에 만료 체크. 만료 됐으면 에러 처리 하지 말고 재발급해 줌
      log.info("만료된 리프레시 토큰입니다. 재발급을 진행합니다.");
      refreshTokenEntity.setRefreshToken(jwtUtil.generateToken(user, refreshTokenValidityInMinutes));
      refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(refreshTokenValidityInMinutes));
      refreshTokenRepository.save(refreshTokenEntity);
    }

    // 그리고 토큰이 만료되기 60분 전이면 리프레시 토큰 재발급
    if (refreshTokenEntity.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(60))) {
      refreshTokenEntity.setRefreshToken(jwtUtil.generateToken(user, refreshTokenValidityInMinutes));
      refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(refreshTokenValidityInMinutes));
      refreshTokenRepository.save(refreshTokenEntity);
    }

  }
}
