package com.strac.api.controller.drive;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.strac.api.controller.drive.GoogleDriveAuthorizationController;
import org.strac.dao.config.StracExamDaoConfig;
import org.strac.dao.token.GoogleAccessTokenRefreshDao;
import org.strac.model.CredentialsResource;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {GoogleDriveAuthorizationController.class, StracExamDaoConfig.class})
@ExtendWith(MockitoExtension.class)
public class GoogleDriveAuthorizationControllerIntegrationTest {

    @Autowired
    private GoogleDriveAuthorizationController googleDriveAuthorizationController;

    @MockBean
    private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;

    @MockBean
    private Gson gson;

    @MockBean
    private GoogleAccessTokenRefreshDao googleAccessTokenRefreshDao;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(googleDriveAuthorizationController).build();
    }

    @Test
    void testAuthorizeUser() throws Exception {
        // Arrange
        String redirectUri = "http://localhost:8081/oauth2/callback"; // Ensure this matches the controller's URI
        String authorizationUrl = "https://accounts.google.com/o/oauth2/v2/auth?redirect_uri=" + redirectUri;

        // Mock the GoogleAuthorizationCodeFlow and AuthorizationCodeRequestUrl
        GoogleAuthorizationCodeRequestUrl authorizationCodeRequestUrlMock = mock(GoogleAuthorizationCodeRequestUrl.class);

        // Mock the GoogleAuthorizationCodeFlow to return the mocked authorization code request URL
        when(googleAuthorizationCodeFlow.newAuthorizationUrl()).thenReturn(authorizationCodeRequestUrlMock);
        when(authorizationCodeRequestUrlMock.setRedirectUri(redirectUri)).thenReturn(authorizationCodeRequestUrlMock);
        when(authorizationCodeRequestUrlMock.build()).thenReturn(authorizationUrl);

        // Act & Assert
        mockMvc.perform(get("/oauth2/auth"))
                .andExpect(status().isOk())
                .andExpect(content().string(authorizationUrl));
    }


    @Test
    void testHandleCallback() throws Exception {
        // Arrange
        String jsonResponse = "{\"accessToken\":\"accessToken\",\"refreshToken\":\"refreshToken\"}";

        // Create mock responses for the OAuth flow
        GoogleTokenResponse googleTokenResponse = mock(GoogleTokenResponse.class);
        when(googleTokenResponse.getAccessToken()).thenReturn("accessToken");
        when(googleTokenResponse.getRefreshToken()).thenReturn("refreshToken");

        // Mock the GoogleAuthorizationCodeFlow and GoogleAuthorizationCodeTokenRequest
        GoogleAuthorizationCodeTokenRequest googleAuthorizationCodeTokenRequestMock = mock(GoogleAuthorizationCodeTokenRequest.class);

        // When newTokenRequest is called, return the mocked GoogleAuthorizationCodeTokenRequest
        when(googleAuthorizationCodeFlow.newTokenRequest("testCode")).thenReturn(googleAuthorizationCodeTokenRequestMock);
        // Mock setRedirectUri and execute on the GoogleAuthorizationCodeTokenRequest mock
        when(googleAuthorizationCodeTokenRequestMock.setRedirectUri(anyString())).thenReturn(googleAuthorizationCodeTokenRequestMock);
        when(googleAuthorizationCodeTokenRequestMock.execute()).thenReturn(googleTokenResponse);

        // Mock Gson's toJson method to return the JSON string
        when(gson.toJson(any(CredentialsResource.class))).thenReturn(jsonResponse);

        // Act & Assert
        mockMvc.perform(get("/oauth2/callback")
                        .param("code", "testCode"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }




    @Test
    void testRefreshAccessToken() throws Exception {
        // Arrange
        String refreshToken = "refreshToken";
        String newAccessToken = "newAccessToken";
        String jsonResponse = "{\"accessToken\":\"newAccessToken\",\"refreshToken\":\"refreshToken\"}";

        when(googleAccessTokenRefreshDao.refreshAccessToken(refreshToken)).thenReturn(newAccessToken);
        when(gson.toJson(any(CredentialsResource.class))).thenReturn(jsonResponse);

        // Act & Assert
        mockMvc.perform(post("/oauth2/refresh")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }
}
