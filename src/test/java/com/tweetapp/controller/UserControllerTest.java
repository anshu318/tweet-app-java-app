package com.tweetapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.config.constants.*;
import com.tweetapp.domain.*;
import com.tweetapp.dto.*;
import com.tweetapp.exception.*;
import com.tweetapp.service.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anshuman Panda
 * @project TweetApp-JAVA-API
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration()
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {

	@Mock
	private UserOperationsService userOperationsService;

	@InjectMocks
	private UserController userController;

	private MockMvc mockMvc;

	private static String convertToJson(Object ob) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(ob);
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Test
	public void testChangePassword() throws Exception {
		NewPassword newPassword = new NewPassword();
		newPassword.setNewPassword("password");
		newPassword.setContact("1234567890");

		final UserModel model = UserModel.builder().username("anshu318").password("password").name("Anshuman")
				.contactNum("7978683397").email("anshumanpanda318@gmail.com").build();

		Mockito.doReturn(model).when(userOperationsService).changePassword("anshu318", newPassword.getNewPassword(),
				newPassword.getContact());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/tweets/{username}/forgot", "anshu318")
				.contentType(MediaType.APPLICATION_JSON).content(convertToJson(model))
				.accept(MediaType.APPLICATION_JSON);

		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = mvcResult.getResponse();

		Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());

	}

	@Test
	public void testGetAllUsers() throws Exception {
		final List<UserModel> userModelList = Arrays.asList(
				UserModel.builder().username("anshu318").name("anshuman")
						.email("anshuman@gmail.com").build(),
				UserModel.builder().username("henry").name("henry")
						.email("henry@yahoo.com").build(),
				UserModel.builder().username("mathewjobin").name("jobin")
						.email("mathew@outloook.com").build(),
				UserModel.builder().username("johndoe").name("jon")
						.email("johndoe@smpt.com").build(),
				UserModel.builder().username("bhanu").name("bhanu")
						.email("bhanu@ipready.com").build());

		Mockito.doReturn(userModelList).when(userOperationsService).getAllUsers();
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tweets/users/all");

		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = mvcResult.getResponse();

		Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());
	}

	@Test
	public void testChangePasswordForPassWordMismatchException() throws Exception {

		NewPassword newPassword = new NewPassword();
		newPassword.setNewPassword("newPassword");
		newPassword.setContact("1234567890");

		Mockito.doThrow(
				new PasswordMisMatchException(Constants.Password_MisMatch_Exception))
				.when(userOperationsService)
				.changePassword("user2", newPassword.getNewPassword(), newPassword.getContact());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/tweets/{username}/forgot", "user2")
				.content(convertToJson(newPassword)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = mvcResult.getResponse();

		Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

	}

	@Test
	public void searchForUsers() throws Exception {
		final List<UserModel> userModelList = Arrays.asList(
				UserModel.builder().username("anshu318").name("anshuman")
						.email("anshuman@gmail.com").build(),
				UserModel.builder().username("anshu318").name("Anshuman")
						.email("anshuman1@gmail.com").build());

		Mockito.doReturn(userModelList).when(userOperationsService).getUsersByUsername("anshu318");
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tweets/user/search/{username}","anshu318");

		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = mvcResult.getResponse();

		Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());
	}
}
