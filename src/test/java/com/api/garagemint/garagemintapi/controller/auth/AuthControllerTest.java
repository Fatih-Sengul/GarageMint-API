package com.api.garagemint.garagemintapi.controller.auth;

import com.api.garagemint.garagemintapi.dto.auth.JwtTokensDto;
import com.api.garagemint.garagemintapi.dto.auth.LoginRequest;
import com.api.garagemint.garagemintapi.dto.auth.RegisterRequest;
import com.api.garagemint.garagemintapi.service.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void registerSuccess() throws Exception {
        JwtTokensDto tokens = JwtTokensDto.builder().accessToken("abc").tokenType("Bearer").build();
        when(authService.register(any(RegisterRequest.class))).thenReturn(tokens);

        RegisterRequest req = RegisterRequest.builder()
                .email("user@example.com")
                .username("user123")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("abc"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void registerValidationError() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginSuccess() throws Exception {
        JwtTokensDto tokens = JwtTokensDto.builder().accessToken("xyz").tokenType("Bearer").build();
        when(authService.login(any(LoginRequest.class))).thenReturn(tokens);

        LoginRequest req = LoginRequest.builder()
                .emailOrUsername("user")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("xyz"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void loginValidationError() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
