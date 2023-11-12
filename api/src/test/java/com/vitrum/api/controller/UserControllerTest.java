package com.vitrum.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitrum.api.credentials.user.Role;
import com.vitrum.api.credentials.user.User;
import com.vitrum.api.credentials.user.UserService;
import com.vitrum.api.dto.Request.ChangeUserCredentials;
import com.vitrum.api.dto.Request.RegisterRequest;
import com.vitrum.api.dto.Response.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @BeforeEach
    public void init() {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        "test@example.com",
                        "passworD123"
                )
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                User.builder()
                        .username("AVitrum")
                        .email("andrey.almashi@gmail.com")
                        .role(Role.ADMIN)
                        .password("qwertY12")
                        .build(),
                null,
                AuthorityUtils.createAuthorityList("ROLE_ADMIN")
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testCreateUser() throws Exception {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testUser")
                .email("test@example.com")
                .password("passworD123")
                .role("USER")
                .build();

        doNothing().when(userService).create(any(RegisterRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testProfile() throws Exception {
        UserProfileResponse userProfileResponse = UserProfileResponse.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .role(Role.USER)
                .build();

        when(userService.profile(any(HttpServletRequest.class))).thenReturn(userProfileResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testUser"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("USER"))
                .andDo(print());
    }

    @Test
    public void testChangeCredentials() throws Exception {
        ChangeUserCredentials changeUserCredentials = ChangeUserCredentials.builder()
                .username("testUser")
                .role("ADMIN")
                .email("newemail@example.com")
                .newUsername("newUsername")
                .build();

        doNothing().when(userService).changeCredentials(any(ChangeUserCredentials.class));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/changeCredentials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changeUserCredentials)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testBanUser() throws Exception {
        doNothing().when(userService).ban("testUser");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/ban")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
