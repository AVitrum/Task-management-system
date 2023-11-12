package com.vitrum.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitrum.api.credentials.authentication.AuthService;
import com.vitrum.api.dto.Request.AuthenticationRequest;
import com.vitrum.api.dto.Request.RegisterRequest;
import com.vitrum.api.dto.Response.AuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    public void testRegister() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testUser")
                .email("test@example.com")
                .password("passworD123")
                .role("USER")
                .build();

        doNothing().when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }


    @Test
    public void testAuthenticate() throws Exception {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .username("testUser")
                .password("passworD123")
                .build();

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .accessToken("mockedAccessToken")
                .refreshToken("mockedRefreshToken")
                .build();

        when(authService.authenticate(authenticationRequest)).thenReturn(authenticationResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticationRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("mockedAccessToken"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh_token").value("mockedRefreshToken"));
    }
}
