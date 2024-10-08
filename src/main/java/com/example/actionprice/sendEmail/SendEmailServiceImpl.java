package com.example.actionprice.sendEmail;

import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.extern.log4j.Log4j2;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author 연상훈
 * @created 24/10/01 22:50
 * @updated 24/10/06 19:15
 * @value senderEmail = 보낼 사람의 이메일. properties에 등록되어 있음. 현재 연상훈 이메일
 * @value javaMailSender = 자바에서 공식적으로 지원하는 이메일 발송 클래스.
 * @info :
 * 1. 객체를 @Service로 수정.
 * 2. 비즈니스 로직 구현.
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class SendEmailServiceImpl implements SendEmailService {
		
	@Value("${spring.mail.username}")
	private String senderEmail;

	private final JavaMailSender javaMailSender;
	private final VerificationEmailRepository verificationEmailRepository;

	// 무작위 문자열을 만들기 위한 준비물
	private final SecureRandom random = new SecureRandom();
	private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private final int CODE_LENGTH = 8;

	@Override
	public String sendVerificationEmail(String email) {

		// 해당 이메일로 발급 받은 verificationEmail이 있으면 가져오고, 없으면 null 반환
		VerificationEmail verificationEmail = verificationEmailRepository.findById(email).orElse(null);

		String verificationCode = generateRandomCode();

		if (verificationEmail != null) { // 이미 존재하는지 체크
			// 이미 있으면, 생성된 지 5분 이상 지났는지 체크
			if (ChronoUnit.MINUTES.between(verificationEmail.getCreatedAt(), LocalDateTime.now()) > 5) {
				verificationEmail.setVerificationCode(verificationCode);
				verificationEmailRepository.save(verificationEmail); // 업데이트
			} else{
				return "최근에 발송된 인증코드가 존재합니다."; // 5분이 지나지 않았으면, 다시 보내지 않고 그냥 발송된 것으로 넘어감
			}
		} else {
			// 존재하지 않는 경우 새로 생성
			verificationEmail = VerificationEmail.builder()
					.email(email)
					.verificationCode(verificationCode)
					.build();
			verificationEmailRepository.save(verificationEmail); // 새로 저장
		}

		// 이메일 발송
		try{
			String subject = "[actionPrice] 회원가입 인증코드입니다.";
			String content = String.format("""
							인증코드
							-------------------------------------------
							%s
							-------------------------------------------
							""", verificationCode);

			sendSimpleMail(email, subject, content);
			return "인증코드가 성공적으로 발송되었습니다.";
		}
		catch(Exception e){
			return "인증코드 발송에 실패했습니다.";
		}
	}

	@Override
	public String checkVerificationCode(String email, String verificationCode) {
		VerificationEmail verificationEmail = verificationEmailRepository.findById(email).orElseThrow();

		// 인증 코드 유효 시간 체크
		if (ChronoUnit.MINUTES.between(verificationEmail.getCreatedAt(), LocalDateTime.now()) < 5) {
			if (!verificationEmail.getVerificationCode().equals(verificationCode)) {
				return "인증코드가 일치하지 않습니다. 다시 입력해주세요.";
			}
			verificationEmailRepository.delete(verificationEmail); // 인증 성공해서 더이상 필요 없으니 삭제
			return "인증이 성공했습니다."; // 인증 성공
		} else {
			verificationEmailRepository.delete(verificationEmail); // 만료된 인증 코드 삭제
			return "인증코드가 만료되었습니다. 다시 진행해주세요."; // 만료된 코드로 인증 실패
		}
	}

	/**
	 * @author 연상훈
	 * @created 24/10/01 22:50
	 * @updated 24/10/02 11:46
	 * @param : receiverEmail = 받는 사람의 이메일
	 * @param : subject = 보낼 이메일의 제목
	 * @param : content = 보낼 이메일의 내용
	 * @throws Exception
	 * @info 간단 이메일 발송 로직. 오류를 대충 처리했음
	 */
	public boolean sendSimpleMail(String receiverEmail, String subject, String content) throws Exception{
		
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(senderEmail);
		simpleMailMessage.setTo(receiverEmail);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(content);
		
		try {
			javaMailSender.send(simpleMailMessage);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;		
	}

	/**
	 * @author 연상훈
	 * @created 24/10/01 22:50
	 * @updated 24/10/02 11:46
	 * @param : receiverEmail = 받는 사람의 이메일
	 * @param : subject = 보낼 이메일의 제목
	 * @param : content = 보낼 이메일의 내용
	 * @throws Exception
	 * @info 간단 이메일 발송 로직. 오류를 대충 처리했음
	 */
	public boolean sendMimeMail(String receiverEmail, String subject, String content) throws Exception{
		MimeMessage message = javaMailSender.createMimeMessage();
		
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, false, "UTF-8");
		mimeMessageHelper.setFrom(senderEmail);
		mimeMessageHelper.setTo(receiverEmail); // 메일 수신자
		mimeMessageHelper.setSubject(subject); // 메일 제목
		mimeMessageHelper.setText(content); // 메일 본문 내용, HTML 여부
		
		try {
            javaMailSender.send(message);
        } catch (Exception e) {
        	e.printStackTrace();
			return false;
		}

		return true;
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
