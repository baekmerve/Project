package com.example.actionprice.sendEmail;

import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.extern.log4j.Log4j2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author 연상훈
 * @created 24/10/01 22:50
 * @updated 24/10/11 20:37
 * @value verificationEmailRepository : 발송된 이메일 정보를 저장하는 레포지토리
 * @value random : 무작위 문자열을 만들어 주는 클래스
 * @value CHARACTERS : 인증코드 조합에 쓰일 문자열.
 * @value CODE_LENGTH : 인증코드의 길이 설정
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class SendEmailServiceImpl implements SendEmailService {

	private final VerificationEmailRepository verificationEmailRepository;
	private final SMTPConfiguration smtpConfiguration;

	// 무작위 문자열을 만들기 위한 준비물
	private final SecureRandom random = new SecureRandom();
	private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private final int CODE_LENGTH = 8;

	/**
	 * 회원가입 시 이메일 인증을 위한 인증 코드 발송 메서드
	 * @author 연상훈
	 * @created 2024-10-10 오전 11:13
	 * @updated 2024-10-11 오후 20:39
	 * @param email 전송 받을 이메일(수신자 이메일)
	 */
	@Override
	public boolean sendVerificationEmail(String email) throws Exception {

		// 해당 이메일로 발급 받은 verificationEmail이 있으면 가져오고, 없으면 null 반환 DB에 있음
		VerificationEmail verificationEmail = verificationEmailRepository.findById(email).orElse(null);

		if (verificationEmail != null) { // 이미 존재하는지 체크
			// 이미 있으면, 생성된 지 5분 이상 지났는지 체크
			if (ChronoUnit.MINUTES.between(verificationEmail.getCreatedAt(), LocalDateTime.now()) > 5) {
				// 5분이 지났으면 삭제함.
				verificationEmailRepository.delete(verificationEmail);
			} else{
				log.info("최근에 발송된 인증코드가 존재합니다."); // 5분이 지나지 않았으면, 다시 보내지 않고 그냥 발송된 것으로 넘어감
				return false;
			}
		}

		// 생성된 적 없거나 5분이 지났으면(위에서 지우고) 새로 생성됨
		verificationEmail = VerificationEmail.builder()
				.email(email)
				.verificationCode(generateRandomCode())
				.build();

		// 이메일 내용 구성.
		String subject = "[actionPrice] 회원가입 인증코드입니다.";
		String content = String.format("""
						인증코드
						-------------------------------------------
						%s
						-------------------------------------------
						""", verificationEmail.getVerificationCode());

		// 이메일 발송. 존재하지 않는 이메일로 발송 시 자동으로 예외 처리.
		// 그 경우에는 해당 객체가 DB에 저장되지 않으니 따로 삭제해줄 필요가 없음
		smtpConfiguration.sendEmail(email, subject, content);

		verificationEmailRepository.save(verificationEmail);

		return true;
	}

	/**
	 * 인증코드 검증 로직
	 * @param email 전송 받은 이메일(사용자가 입력한 이메일)
	 * @param verificationCode 인증코드(사용자가 입력한 인증코드)
	 * @author : 연상훈
	 * @created : 2024-10-12 오후 12:10
	 * @updated : 2024-10-12 오후 12:10
	 */
	@Override
	public String checkVerificationCode(String email, String verificationCode) {
		VerificationEmail verificationEmail = verificationEmailRepository.findById(email).orElse(null);

		// 잘못된 이메일이면
		if (verificationEmail == null) {
			return "잘못된 이메일을 입력하셨습니다.";
		}

		// 정상적인 이메일이면
		// 인증 코드 유효 시간 체크
		if (ChronoUnit.MINUTES.between(verificationEmail.getCreatedAt(), LocalDateTime.now()) < 5) {

			// 유효시간이 지나지 않았으면
			// 인증코드 일치하는지 확인
			if (!verificationEmail.getVerificationCode().equals(verificationCode)) {
				// 불일치
				return "인증코드가 일치하지 않습니다. 다시 입력해주세요.";
			}

			// 일치
			verificationEmailRepository.delete(verificationEmail); // 인증 성공해서 더이상 필요 없으니 삭제
			return "인증이 성공했습니다."; // 인증 성공

		} else { // 유효시간이 지났으면
			verificationEmailRepository.delete(verificationEmail); // 만료된 인증 코드 삭제
			return "인증코드가 만료되었습니다. 다시 진행해주세요."; // 만료된 코드로 인증 실패
		}
	}

	/**
	 * 인증코드 구성을 위한 랜덤한 8자리 문자열 생성 메서드
	 * @author : 연상훈
	 * @created : 2024-10-06 오후 7:42
	 * @updated : 2024-10-06 오후 7:42
	 */
	private String generateRandomCode(){
		StringBuilder code = new StringBuilder(CODE_LENGTH);
		for (int i = 0; i < CODE_LENGTH; i++) {
			int randomIndex = random.nextInt(CHARACTERS.length());
			code.append(CHARACTERS.charAt(randomIndex));
		}
		return code.toString();
	}

}
