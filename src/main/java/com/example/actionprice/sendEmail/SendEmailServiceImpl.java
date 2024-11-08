package com.example.actionprice.sendEmail;

import com.example.actionprice.exception.InvalidEmailAddressException;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.FlagTerm;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author 연상훈
 * @created 24/10/01 22:50
 * @updated 24/10/11 20:37
 * @value senderEmail : 보낼 사람의 이메일. properties에 등록되어 있음. 현재 연상훈 이메일
 * @value javaMailSender : 자바에서 공식적으로 지원하는 이메일 발송 클래스.
 * @value verificationEmailRepository : 발송된 이메일 정보를 저장하는 레포지토리
 * @value pop3Configuration : 이메일 발송 후 잘못된 이메일로 보내졌는지 체크하기 위한 일종의 컴포넌트(형식은 configuration
 * @value random : 무작위 문자열을 만들어 주는 클래스
 * @value CHARACTERS : 인증코드 조합에 쓰일 문자열.
 * @value CODE_LENGTH : 인증코드의 길이 설정
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
	
	private final Pop3Configuration pop3Configuration;

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
		sendSimpleMail(email, subject, content);

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
	 * 이메일 발송이 완료되었는지 확인하는 메서드
	 * @param email 인증코드가 발송된 이메일 주소
	 * @author : 연상훈
	 * @created : 2024-10-10 오후 9:44
	 * @updated 2024-10-12 오전 11:39 : 기능적으로는 차이가 없지만, 읽지 않은 메시지만 검색함으로써 메모리 사용량을 크게 줄임
	 * @info store와 folder를 try() 안에 넣어서 try가 실패하면 자연스럽게 닫히게 함
	 * @info result 변수를 사용하여 folder와 store가 닫히고 나서 메서드가 종료하도록 합니다.
	 * @info 너무 길어서 별도의 메서드로 내부 로직을 분리하려다가, 그러면 이게 너무 짧아져서 그대로 둠
	 */
	private boolean isCompleteSentEmail(String email) throws MessagingException, IOException {
		boolean result = true;
		log.info("이메일 발송이 완료되었는지 확인을 시작합니다.");

		try (Store store = pop3Configuration.getPop3Store()) {

			if (store.isConnected()) {
				store.close(); // 이미 연결된 경우 연결 닫기
			}

			store.connect(); // POP3 서버에 연결

			// 지정한 이메일 폴더 열기
			try (Folder emailFolder = store.getFolder(pop3Configuration.getFolder())) {
				emailFolder.open(Folder.READ_ONLY); // 읽기 전용으로 폴더 열기

				log.info("이메일 폴더를 개방합니다. 아직 읽지 않은 메시지를 찾습니다.");

				// 아직 읽지 않은 메시지만 찾기
				//FlagTerm 메세지 표시 ,
				Message[] messages = emailFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

				// 각 메시지의 내용을 뜯어보기
				log.info("30초 내로 반송된 이메일이 있는지 검색합니다.");
				for (Message message : messages) {

					// 현재 시간에서 지정된 시간만큼 이전 시간 계산
					Instant untilTime = Instant.now().minusSeconds(pop3Configuration.getUntilTime());

					// 메일의 발송 날짜가 지정된 시간 이내인지 확인
					if (untilTime.isBefore(message.getSentDate().toInstant())) {

						log.info("30초 내로 반송된 이메일이 존재합니다.");

						// 반송된 이메일 확인
						Address[] fromAddresses = message.getFrom();

						log.info("이메일이 어디서 왔는지 확인합니다.");

						if(fromAddresses != null || fromAddresses.length > 0){
							log.info("이메일 발신자 체크");
							String from = fromAddresses[0].toString();
							if (from != null || !from.isEmpty()) {
								// 누구한테서 온 이메일인지 확인하고, 그게 Mail Delivery Subsystem <mailer-daemon@googlemail.com>이라면 반송된 메일이 맞습니다.
								// X-Failed-Recipients를 사용하면 훨씬 간결하지만 X-Failed-Recipients가 존재하지 않는 경우도 많아서 안정성이 매우 떨어집니다.
								if (from != null && from.contains("mailer-daemon") || from.contains("postmaster")) {
									log.info("이메일 발신자 체크 : mailer-daemon");

									// 이번에는 그 이메일의 내용물을 확인합니다.
									MimeMultipart multipart = (MimeMultipart) message.getContent();

									for (int i = 0; i < multipart.getCount(); i++) {
										BodyPart bodyPart = multipart.getBodyPart(i);

										// 이메일이 누구한테 보냈다가 반송된 것인지는 message/rfc822 안에만 있습니다.
										if (bodyPart.isMimeType("message/rfc822")) {

											log.info("이메일 내용 체크");

											MimeMessage originalMessage = (MimeMessage) bodyPart.getContent();

											// 반송된 이메일의 주인을 출력합니다.
											String originalTo = originalMessage.getRecipients(Message.RecipientType.TO)[0].toString();

											// 방금 보낸 이메일과 반송된 이메일의 주인이 일치하는지 확인합니다.
											if(originalTo.equals(email)) {

												log.info("이메일의 본래 수신자 : " + originalTo);

												log.info("[{}]로 전송한 이메일이 반송되었습니다.", email);
												result = false; // 반송된 이메일이므로, 이메일 전송은 실패입니다.

												try {
													// 반송된 이메일을 이메일 수신함에서 삭제
													log.info("반송된 이메일은 삭제합니다.");
													message.setFlag(Flags.Flag.DELETED, true);

												} catch (MessagingException e) {
													log.error("반송 이메일 삭제 중 에러 발생. error : {}", e.getMessage());
												}

											}

										}

									}

								}
							}
						}

					}
				}
			} // Folder 자동으로 닫힘
		} // Store 자동으로 닫힘

		return result;
	}

	/**
	 * 단순 이메일 발송 메서드
	 * @param receiverEmail 받는 사람의 이메일
	 * @param subject 보낼 이메일의 제목
	 * @param content 보낼 이메일의 내용
	 * @author 연상훈
	 * @created 24/10/01 22:50
	 * @updated 24/10/15 오전 09:47 : 에러 코드 단순화
	 * @throws InvalidEmailAddressException
	 * @info 이 메서드 실행 중에 오류가 발생하면 자체적으로 InvalidEmailAddressException 등의 예외로 던지기 때문에 별도의 오류 처리가 필요 없음.
	 */
	private void sendSimpleMail(String receiverEmail, String subject, String content) throws MessagingException, IOException {
		log.info("이메일 전송 메서드 시작");
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(senderEmail);
		simpleMailMessage.setTo(receiverEmail);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(content);

		javaMailSender.send(simpleMailMessage);

		if (!isCompleteSentEmail(receiverEmail)){
			throw new InvalidEmailAddressException("[" + receiverEmail + "] does not exist");
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

	/**
	 * @author 연상훈
	 * @param receiverEmail = 받는 사람의 이메일
	 * @param subject = 보낼 이메일의 제목
	 * @param content = 보낼 이메일의 내용
	 * @created 24/10/01 22:50
	 * @throws Exception
	 * @info 간단 이메일 발송 로직. 오류를 대충 처리했음.
	 * @deprecated
	 */
	private void sendMimeMail(String receiverEmail, String subject, String content) throws Exception{
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
		}
	}

}
