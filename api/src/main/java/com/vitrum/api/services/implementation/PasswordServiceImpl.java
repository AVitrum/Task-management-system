package com.vitrum.api.services.implementation;

import com.vitrum.api.dto.request.ChangePasswordRequest;
import com.vitrum.api.dto.request.ResetPasswordRequest;
import com.vitrum.api.models.User;
import com.vitrum.api.models.submodels.Recoverycode;
import com.vitrum.api.repositories.RecoverycodeRepository;
import com.vitrum.api.repositories.UserRepository;
import com.vitrum.api.services.PasswordService;
import com.vitrum.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final RecoverycodeRepository recoverycodeRepository;
    private final MessageUtil messageUtil;

    @Override
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = User.getAuthUser(repository);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new IllegalStateException("Wrong password!");

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
            throw new IllegalStateException("Password is the same as the current one!");

        if (!request.getNewPassword().equals(request.getConfirmationPassword()))
            throw new IllegalStateException("Passwords do not match!");


        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Wrong email!"));
        var recoverycode = recoverycodeRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Code not found"));

        if (recoverycode.isExpired()) {
            recoverycodeRepository.delete(recoverycode);
            throw new IllegalStateException("Code is expired");
        }
        if (!recoverycode.getCode().equals(request.getCode()))
            throw new IllegalStateException("The codes do not match");

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
            throw new IllegalStateException("Password is the same as the current one!");

        if (!request.getNewPassword().equals(request.getConfirmationPassword()))
            throw new IllegalStateException("Passwords do not match!");


        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
        recoverycodeRepository.delete(recoverycode);
    }

    @Override
    public void getRecoverycode(String email) {
        var user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Wrong email!"));
        var recoverycode = generateRecoverycode(user);

        messageUtil.sendRecoverycodeByEmail(user, recoverycode);
    }

    private Recoverycode generateRecoverycode(User user) {
        if (recoverycodeRepository.findByUser(user).isPresent()) {
            Recoverycode recoverycode = recoverycodeRepository.findByUser(user)
                    .orElseThrow(() -> new IllegalArgumentException("Code not found"));
            recoverycodeRepository.delete(recoverycode);
        }

        long code = new Random().nextLong(999999 - 100000 + 1) + 100000;
        Recoverycode recoverycode = Recoverycode.builder()
                .code(code)
                .creationTime(LocalTime.now())
                .user(user)
                .build();
        recoverycodeRepository.save(recoverycode);
        return recoverycode;
    }
}
