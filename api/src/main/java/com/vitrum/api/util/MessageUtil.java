package com.vitrum.api.util;

import com.vitrum.api.models.submodels.Recoverycode;
import com.vitrum.api.models.User;
import com.vitrum.api.models.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MessageUtil {

    private final JavaMailSender emailSender;

    public void sendMessage(Member member, String text, String subject) {
        if (member.isEmailsAllowed()) {
            try {
                var user = member.getUser();
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);

                helper.setFrom("tms.team.noreply@gmail.com");
                helper.setTo(user.getUsername());
                helper.setSubject(subject);
                helper.setText(text);

                emailSender.send(message);

            } catch (MessagingException e) {
                throw new RuntimeException("Something went wrong!");
            }
        }
    }

    public void sendRecoverycodeByEmail(User user, Recoverycode recoverycode) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom("tms.team.noreply@gmail.com");
            helper.setTo(user.getUsername());
            helper.setSubject("TMS Recovery code");
            helper.setText("Your password recovery code: " + recoverycode.getCode());

            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Something went wrong!");
        }
    }

}
