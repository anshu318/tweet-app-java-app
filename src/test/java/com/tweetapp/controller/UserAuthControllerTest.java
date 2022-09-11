package com.tweetapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.config.constants.Constants;
import com.tweetapp.domain.UserModel;
import com.tweetapp.dto.AuthenticationRequest;
import com.tweetapp.exception.UsernameAlreadyExists;
import com.tweetapp.service.UserOperationsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * @author Anshuman Panda
 * @project TweetApp-JAVA-API
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@WebAppConfiguration()
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserAuthControllerTest {

    @Mock
    private UserOperationsService userModelService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserAuthController userAuthController;

    private MockMvc mockMvc;

    private static String convertToJson(Object ob) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(ob);
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userAuthController).build();
    }

    @Test
    public void testSubscribeClient() throws Exception {
        final UserModel userModel = UserModel.builder()
                .username("anshu318")
                .name("Anshuman")
                .email("anshumanpanda318@gmail.com")
                .contactNum("7978683397")
                .password("password")
                .build();


        Mockito.doReturn(userModel).when(userModelService).createUser(userModel);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJson(userModel))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    public void testSubscribeClient_catchUserNameAlreadyExistException() throws Exception {
        final UserModel userModel = UserModel.builder()
                .username("anshu318")
                .name("Anshuman")
                .email("anshumanpanda318@gmail.com")
                .contactNum("7978683397")
                .password("password")
                .build();
        Mockito.doAnswer(invocation -> {
                    throw new UsernameAlreadyExists(Constants.Invalid_Username_Exception);
                })
                .when(userModelService)
                .createUser(userModel);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJson(userModel))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    @Test
    public void testSubscribeClient_catchException() throws Exception {
        final UserModel userModel = UserModel.builder()
                .username("anshu318")
                .name("Anshuman")
                .email("anshumanpanda318@gmail.com")
                .contactNum("7978683397")
                .password("password")
                .build();
        Mockito.doAnswer(invocation -> {
                    throw new Exception("Some Error");
                })
                .when(userModelService)
                .createUser(userModel);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertToJson(userModel))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
    }

    @Test
    public void testAuthenticateClientWithException() throws Exception {

        final AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername("anshu318");
        authenticationRequest.setPassword("password");

        Mockito.doAnswer(invocation -> {
                    throw new Exception(Constants.Invalid_Username_Exception);
                })
                .when(authenticationManager)
                .authenticate(new UsernamePasswordAuthenticationToken("anshu318", "password"));

        final RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/login")
                .content(convertToJson(authenticationRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder).andReturn();
        Assert.assertTrue(HttpStatus.UNAUTHORIZED.is4xxClientError());
    }

    @Test
    public void testAuthenticateClient() throws Exception {

        final AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername("anshu318");
        authenticationRequest.setPassword("password");

        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        Mockito.when(authenticationManager.authenticate
                        (new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())))
                .thenReturn(authentication);

        final RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/login")
                .content(convertToJson(authenticationRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }
}
