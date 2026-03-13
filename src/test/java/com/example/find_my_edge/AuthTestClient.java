package com.example.find_my_edge;

import com.example.find_my_edge.common.auth.dto.AuthResponse;
import com.example.find_my_edge.common.auth.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestComponent
@RequiredArgsConstructor
public class AuthTestClient {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public String login(String email, String password) throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        MvcResult result =
                mockMvc.perform(
                               post("/auth/login")
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(request))

                       )
                       .andExpect(status().isOk())
                       .andReturn();

        String json = result.getResponse().getContentAsString();

        AuthResponse response =
                objectMapper.readValue(json, AuthResponse.class);

        return response.getAccessToken();
    }

    public <T> T getCall(String email, String password, String path, Class<T> clazz) throws Exception {
        String accessToken = login(email, password);

        MvcResult result =
                mockMvc.perform(
                               get(path)
                                       .header("Authorization", "Bearer " + accessToken)
                       )
                       .andExpect(status().isOk())
                       .andReturn();

        String json = result.getResponse().getContentAsString();
        return objectMapper.readValue(json, clazz);
    }
}