package com.vitrum.api.credentials.password;

import com.vitrum.api.config.JwtService;
import com.vitrum.api.credentials.user.User;
import com.vitrum.api.credentials.user.UserRepository;
import com.vitrum.api.dto.Request.ChangePasswordRequest;
import com.vitrum.api.dto.Request.ResetPasswordRequest;
import com.vitrum.api.recoverycode.Recoverycode;
import com.vitrum.api.recoverycode.RecoverycodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.Principal;
import java.time.LocalTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final RecoverycodeRepository recoverycodeRepository;
    @Autowired
    private JavaMailSender emailSender;


    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password!");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalStateException("Password are the same with current!");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same!");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
    }

    public void resetPassword(ResetPasswordRequest request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Wrong email!"));
        var recoverycode = user.getRecoverycode().get(0);
        if (recoverycode.isExpired()) {
            recoverycodeRepository.delete(recoverycode);
            throw new IllegalStateException("Code is expired");
        }
        if (!recoverycode.getCode().equals(request.getCode())) {
            throw new IllegalStateException("The codes are not the same");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalStateException("Password are the same with current!");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same!");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
        recoverycodeRepository.delete(recoverycode);
    }

    public void getRecoverycode(String email) {
        try {
            var user = repository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Wrong email!"));
            var recoverycode = getRecoverycode(user);
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

    private Recoverycode getRecoverycode(User user) {
        if (recoverycodeRepository.findByUser(user).isPresent()) {
            var recoverycode = recoverycodeRepository.findByUser(user)
                    .orElseThrow(() -> new IllegalArgumentException("Wrong User!"));
            recoverycodeRepository.delete(recoverycode);
        }

        var recoverycode = Recoverycode.builder()
                .code(new Random().nextLong(999999 - 100000 + 1) + 100000)
                .creationTime(LocalTime.now())
                .user(user)
                .build();
        recoverycodeRepository.save(recoverycode);
        return recoverycode;
    }
}
