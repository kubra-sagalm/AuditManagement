package com.example.AuditManagement.Service;

import com.example.AuditManagement.DTO.UpdateUserRequest;
import com.example.AuditManagement.DTO.UserProfileResponse;
import com.example.AuditManagement.Entity.User;
import com.example.AuditManagement.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;


    public UserService(UserRepository userRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }


    public List<User> getAllTeamMembers() {
        return userRepository.findByRole_Name("TEAM_MEMBER");
    }

    public void sendMail(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kubrasaglam309@gmail.com");  //
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }


    public UserProfileResponse updateCurrentUserProfile(String currentEmail, UpdateUserRequest request) {


        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));


        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }


        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }



        user = userRepository.save(user);

        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().getName(),
                user.isActive()
        );

    }


    public UserProfileResponse getCurrentUserProfile(String currentEmail) {

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().getName(),
                user.isActive()
        );
    }


}
