package com.tweetapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.config.constants.*;
import com.tweetapp.domain.*;
import com.tweetapp.dto.*;
import com.tweetapp.exception.*;
import com.tweetapp.service.TweetService;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Anshuman Panda
 * @project TweetApp-JAVA-API
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@WebAppConfiguration()
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTweetControllerTest {


    @Mock
    private TweetService tweetService;

    @InjectMocks
    private UserTweetController userTweetController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userTweetController).build();

    }

    @Test
    public void testGetAllTweets() throws Exception {
        final List<Tweet> tweets = Arrays.asList(
                new Tweet("1", "user1", "tweet1", "first",
                        new Date().toString(), Collections.singletonList("2"),
                        Arrays.asList(new Comment("user2", "comment1"),
                                new Comment("user2", "comment2"),
                                new Comment("user2", "comment2"))),

                new Tweet("2", "user2", "tweet2", "second",
                        new Date().toString(), Collections.singletonList("2"),
                        Arrays.asList(new Comment("user3", "comment1"),
                                new Comment("user3", "comment2"),
                                new Comment("user1", "comment1")))
        );

        Mockito.doReturn(tweets).when(tweetService).getAllTweets();
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/tweets/all");

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void testGetAllTweets_Exception() throws Exception {
        final List<Tweet> tweets = Arrays.asList(
                new Tweet("1", "user1", "tweet1", "first",
                        new Date().toString(), Collections.singletonList("2"),
                        Arrays.asList(new Comment("user2", "comment1"),
                                new Comment("user2", "comment2"),
                                new Comment("user2", "comment2"))),

                new Tweet("2", "user2", "tweet2", "second",
                        new Date().toString(), Collections.singletonList("2"),
                        Arrays.asList(new Comment("user3", "comment1"),
                                new Comment("user3", "comment2"),
                                new Comment("user1", "comment1")))
        );

        Mockito.doAnswer(invocation -> {
                    throw new TweetDoesNotExistException(Constants.Tweet_Not_Found);
                })
                .when(tweetService)
                .getAllTweets();

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/tweets/all");

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
    }

    @Test
    public void testPostTweet() throws Exception {
        Tweet postTweet = new Tweet("2", "user2", "tweet2", "second",
                new Date().toString(), Collections.singletonList("2"),
                Arrays.asList(new Comment("user3", "comment1"),
                        new Comment("user3", "comment2"),
                        new Comment("user1", "comment1")));


        ObjectMapper objectMapper = new ObjectMapper();
        String jsonObject = objectMapper.writeValueAsString(postTweet);


        Mockito.doReturn(postTweet).when(tweetService).postNewTweet("user2", postTweet);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/{username}/add", "user2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    public void testPostTweet_Exception() throws Exception {
        Tweet postTweet = new Tweet("2", "user2", "tweet2", "second",
                new Date().toString(), Collections.singletonList("2"),
                Arrays.asList(new Comment("user3", "comment1"),
                        new Comment("user3", "comment2"),
                        new Comment("user1", "comment1")));


        ObjectMapper objectMapper = new ObjectMapper();
        String jsonObject = objectMapper.writeValueAsString(postTweet);

        Mockito.doAnswer(invocation -> {
                    throw new Exception(Constants.Internal_Server_Error);
                })
                .when(tweetService)
                .postNewTweet("user2", postTweet);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/{username}/add", "user2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
    }

    @Test
    public void testGetUserTweetsWithInvalidUserNameException() throws Exception {

        Mockito.doThrow(new InvalidUsernameException(Constants.Invalid_Username_Exception))
                .when(tweetService)
                .getUserTweets("user2", "loggedInUser");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/tweets/{username}", "user2")
                .header("loggedInUser", "loggedInUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder).andReturn();
        Assert.assertTrue(HttpStatus.NOT_FOUND.is4xxClientError());
    }

    @Test
    public void testGetUserTweetsException() throws Exception {

        Mockito.doAnswer(invocation -> {
                    throw new Exception(Constants.Internal_Server_Error);
                })
                .when(tweetService)
                .getUserTweets("user2", "loggedInUser");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/tweets/{username}", "user2")
                .header("loggedInUser", "loggedInUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder).andReturn();
        Assert.assertTrue(HttpStatus.INTERNAL_SERVER_ERROR.is5xxServerError());
    }

    @Test
    public void testGetTweetDetails() throws Exception {
        TweetResponse tweetResponse = new TweetResponse();
        tweetResponse.setTweetId("1");
        tweetResponse.setUsername("user2");
        tweetResponse.setTweetText("tweet");
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/tweets/user2/1");
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void testGetTweetDetails_Exception() throws Exception {
        TweetResponse tweetResponse = new TweetResponse();
        tweetResponse.setTweetId("1");
        tweetResponse.setUsername("user2");
        tweetResponse.setTweetText("tweet");
        Mockito.doAnswer(invocation -> {
                    throw new Exception(Constants.Internal_Server_Error);
                })
                .when(tweetService)
                .getTweet("1", "user2");
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/tweets/user2/1");
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
    }

    @Test
    public void testUpdateTweet() throws Exception {
        Tweet updateTweet = new Tweet("1", "user2", "tweet2", "second",
                new Date().toString(), Collections.singletonList("2"),
                Arrays.asList(new Comment("user3", "comment1"),
                        new Comment("user3", "comment2"),
                        new Comment("user1", "comment1")));

        TweetUpdate tweetUpdate = new TweetUpdate();
        tweetUpdate.setTweetId("1");
        tweetUpdate.setTweetText("tweet2");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonObject = objectMapper.writeValueAsString(tweetUpdate);

        Mockito.doReturn(updateTweet).when(tweetService).
                updateTweet("user2", "1", "tweet2");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/tweets/{username}/update", "user2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void testUpdateTweet_TweetDoesNotExistException() throws Exception {
        TweetUpdate tweetUpdate = new TweetUpdate();
        tweetUpdate.setTweetId("1");
        tweetUpdate.setTweetText("tweet2");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonObject = objectMapper.writeValueAsString(tweetUpdate);

        Mockito.doAnswer(invocation -> {
                    throw new TweetDoesNotExistException(Constants.Tweet_Not_Found);
                }).when(tweetService).
                updateTweet("user2", "1", "tweet2");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/tweets/{username}/update", "user2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testUpdateTweet_Exception() throws Exception {
        TweetUpdate tweetUpdate = new TweetUpdate();
        tweetUpdate.setTweetId("1");
        tweetUpdate.setTweetText("tweet2");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonObject = objectMapper.writeValueAsString(tweetUpdate);

        Mockito.doAnswer(invocation -> {
                    throw new Exception(Constants.Internal_Server_Error);
                }).when(tweetService).
                updateTweet("user2", "1", "tweet2");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/tweets/{username}/update", "user2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
    }

    @Test
    public void deleteTweet() throws Exception {
        Mockito.when(tweetService.
                deleteTweet("1")).thenReturn(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/tweets/user2/delete/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    public void deleteTweet_TweetDoesNotExistException() throws Exception {
        Mockito.doAnswer(invocation -> {
                    throw new TweetDoesNotExistException(Constants.Tweet_Not_Found);
                })
                .when(tweetService).
                deleteTweet("1");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/tweets/user2/delete/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void deleteTweet_Exception() throws Exception {
        Mockito.doAnswer(invocation -> {
                    throw new Exception(Constants.Internal_Server_Error);
                })
                .when(tweetService).
                deleteTweet("1");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/tweets/user2/delete/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
    }

    @Test
    public void testLikeATweet() throws Exception {
        Tweet likeTweet = new Tweet("2", "user2", "tweet2", "last",
                new Date().toString(), Collections.singletonList("2"),
                Arrays.asList(new Comment("user3", "comment1"),
                        new Comment("user3", "comment2"),
                        new Comment("user1", "comment1")));


        Mockito.doReturn(likeTweet).when(tweetService).likeTweet("user2", "1");
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/user2/like/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void testLikeATweet_TweetDoesNotExistException() throws Exception {
        Mockito.doAnswer(invocation -> {
            throw new TweetDoesNotExistException(Constants.Tweet_Not_Found);
        }).when(tweetService).likeTweet("user2", "1");
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/user2/like/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testLikeATweet_Exception() throws Exception {
        Mockito.doAnswer(invocation -> {
            throw new Exception(Constants.Internal_Server_Error);
        }).when(tweetService).likeTweet("user2", "1");
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/user2/like/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
    }

    @Test
    public void testDislikeATweet() throws Exception {
        Tweet dislikeTweet = new Tweet("2", "user2", "tweet2", "second",
                new Date().toString(), Collections.singletonList("2"),
                Arrays.asList(new Comment("user3", "comment1"),
                        new Comment("user3", "comment2"),
                        new Comment("user1", "comment1")));


        Mockito.doReturn(dislikeTweet).when(tweetService).dislikeTweet("user2", "1");
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/user2/dislike/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void testDislikeATweet_TweetDoesNotExistException() throws Exception {
        Mockito.doAnswer(invocation -> {
                    throw new TweetDoesNotExistException(Constants.Tweet_Not_Found);
                })
                .when(tweetService).dislikeTweet("user2", "1");
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/user2/dislike/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testDislikeATweet_Exception() throws Exception {
        Mockito.doAnswer(invocation -> {
                    throw new Exception(Constants.Internal_Server_Error);
                })
                .when(tweetService).dislikeTweet("user2", "1");
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/user2/dislike/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
    }

    @Test
    public void testReplyATweet() throws Exception {
        Tweet replyTweet = new Tweet("2", "user2", "tweet2", "last",
                new Date().toString(), Collections.singletonList("2"),
                Arrays.asList(new Comment("user3", "comment1"),
                        new Comment("user3", "comment2"),
                        new Comment("user1", "comment1")));

        Reply reply = new Reply("comment");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonObject = objectMapper.writeValueAsString(reply);

        Mockito.doReturn(replyTweet).when(tweetService).replyTweet("user2", "1", reply.getComment());
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/user2/reply/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void testReplyATweet_Exception() throws Exception {
        Reply reply = new Reply("comment");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonObject = objectMapper.writeValueAsString(reply);

        Mockito.doAnswer(invocation -> {
                    throw new Exception(Constants.Internal_Server_Error);
                })
                .when(tweetService)
                .replyTweet("user2", "1", reply.getComment());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/user2/reply/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
    }

    @Test
    public void testReplyATweet_TweetNotExistException() throws Exception {
        Reply reply = new Reply("comment");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonObject = objectMapper.writeValueAsString(reply);

        Mockito.doAnswer(invocation -> {
                    throw new TweetDoesNotExistException(Constants.Tweet_Not_Found);
                })
                .when(tweetService)
                .replyTweet("user2", "1", reply.getComment());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/tweets/user2/reply/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
