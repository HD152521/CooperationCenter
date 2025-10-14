package com.cooperation.project.cooperationcenter.domain.member.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailgunService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendHtmlMessage(String to, String subject, String resetLink) throws MessagingException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        String htmlBody = htmlTemplate(resetLink);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        try {
            mailSender.send(message);
            System.out.println("이메일 전송 성공!");
        } catch (MailException e) {
            System.out.println("[-] 이메일 전송중에 오류가 발생하였습니다 " + e.getMessage());
            throw e;
        }
    }

    public String htmlTemplate(String resetLink){
        return """
        <html lang="ko">
        <head>
            <meta charset="UTF-8">
        </head>
        <body style="margin:0; padding:0; background-color:#f4f7fb; font-family:'Apple SD Gothic Neo','Segoe UI',sans-serif;">
            <div style="max-width:480px; margin:40px auto; background-color:#ffffff; border-radius:12px; box-shadow:0 4px 10px rgba(0,0,0,0.08);">
                
                <div style="background-color:#2563EB; color:white; text-align:center; padding:18px; font-size:22px; font-weight:600;">
                    비밀번호 재설정 안내
                </div>

                <div style="padding:24px 28px; line-height:1.6; color:#333;">
                    <p style="font-size:15px; margin-bottom:18px;">안녕하세요 😊</p>
                    
                    <p style="font-size:15px; margin-bottom:20px;">
                        비밀번호를 잊으셨나요?<br>
                        아래 버튼을 눌러 새 비밀번호를 설정하실 수 있습니다.
                    </p>

                    <div style="text-align:center; margin:28px 0;">
                        <a href="%s"
                           style="background-color:#2563EB; color:#ffffff; text-decoration:none; font-weight:600;
                                  padding:12px 24px; border-radius:8px; display:inline-block; box-shadow:0 2px 4px rgba(37,99,235,0.3);">
                            비밀번호 재설정하기
                        </a>
                    </div>

                    <p style="font-size:14px; color:#555;">
                        본 메일은 회원님의 요청으로 발송되었습니다.<br>
                        만약 비밀번호 재설정을 요청하지 않으셨다면, 이 메일을 무시하셔도 됩니다.
                    </p>
                </div>

                <div style="background-color:#f0f0f0; text-align:center; font-size:12px; color:#666;
                            padding:14px; border-top:1px solid #e0e0e0;">
                    © 2025 Cooperation Center. All rights reserved.
                </div>

            </div>
        </body>
        </html>
        """.formatted(resetLink);
    }

}