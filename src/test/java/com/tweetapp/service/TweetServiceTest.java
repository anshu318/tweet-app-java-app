package com.tweetapp.service;

import com.tweetapp.domain.Tweet;
import com.tweetapp.dto.Comment;
import com.tweetapp.dto.TweetResponse;
import com.tweetapp.exception.InvalidUsernameException;
import com.tweetapp.exception.TweetDoesNotExistException;
import com.tweetapp.repository.TweetRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.doNothing;

/**
 * @author Anshuman Panda
 * @project TweetApp-JAVA-API
 */
@Log4j2
@ExtendWith(SpringExtension.class)
public class TweetServiceTest {

    @Mock
    private TweetRepository tweetRepository;

    @InjectMocks
    private TweetService tweetService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllTweets() {
        final List<Tweet> tweets = Arrays.asList(new Tweet("1", "user1", "tweet1", "first", Instant.now().toString(), Collections.singletonList("2"), Arrays.asList(new Comment("user2", "comment1"), new Comment("user2", "comment2"), new Comment("user2", "comment2"))),

                new Tweet("2", "user2", "tweet2", "second", Instant.now().toString(), Collections.singletonList("2"), Arrays.asList(new Comment("user3", "comment1"), new Comment("user3", "comment2"), new Comment("user1", "comment1"))));

        Mockito.when(tweetRepository.findAll()).thenReturn(tweets);
        List<Tweet> fetchTweets = tweetService.getAllTweets();
        Assertions.assertEquals(fetchTweets.size(), tweets.size());
        log.debug("Tested - #MethodName: getAllTweets() successfully.");

    }

    @Test
    public void testGetUserTweets() throws InvalidUsernameException {
        final String username = "anshu";
        final String loggedInUser = "anshu";

        final List<Tweet> tweets = Collections.singletonList(new Tweet("1", "anshu", "tweet1", "first", Instant.now().toString(), Collections.singletonList("2"), Arrays.asList(new Comment("user2", "comment1"), new Comment("user2", "comment2"), new Comment("user2", "comment2"))));

        Mockito.when(tweetRepository.findByUsername(username)).thenReturn(tweets);

        List<TweetResponse> fetchTweets = tweetService.getUserTweets(username, loggedInUser);
        Assertions.assertEquals(fetchTweets.get(0).getUsername(), tweets.get(0).getUsername());
        log.debug("Tested - #MethodName: getUserTweets() successfully.");
    }

    @Test
    public void testDeleteTweet() {
        final String tweetId = "123";
        Mockito.when(tweetRepository.existsById(tweetId)).thenReturn(true);
        doNothing().when(tweetRepository).deleteById(tweetId);
        tweetService.deleteTweet(tweetId);
        log.debug("Tested - #MethodName: deleteTweet() successfully.");
    }

    @Test
    public void testPostNewTweet() {
        Tweet newTweet = new Tweet();
        newTweet.setTweetText("text");
        Mockito.when(tweetRepository.insert(newTweet)).thenReturn(newTweet);
        Tweet tweet = tweetService.postNewTweet("anshu", newTweet);
        Assertions.assertNotNull(tweet);
        log.debug("Tested - #MethodName: postNewTweet() successfully.");
    }

    @Test
    public void testGetTweet() {
        Tweet tweet = new Tweet("1", "anshu", "tweet1", "first", Instant.now().toString(), Collections.singletonList("2"), Arrays.asList(new Comment("user2", "comment1"), new Comment("user2", "comment2"), new Comment("user2", "comment2")));
        Mockito.when(tweetRepository.findById("1")).thenReturn(Optional.of(tweet));
        TweetResponse tweetResponse = tweetService.getTweet(tweet.getTweetId(), tweet.getUsername());
        Assertions.assertNotNull(tweetResponse);
        log.debug("Tested - #MethodName: getTweet() successfully.");
    }

    @Test
    public void testUpdateTweet() {
        Tweet tweet = new Tweet("1", "anshu", "tweet1", "first", Instant.now().toString(), Collections.singletonList("2"), Arrays.asList(new Comment("user2", "comment1"), new Comment("user2", "comment2"), new Comment("user2", "comment2")));
        Mockito.when(tweetRepository.findById("1")).thenReturn(Optional.of(tweet));
        Mockito.when(tweetRepository.save(tweet)).thenReturn(tweet);
        Tweet tweetResponse = tweetService.updateTweet(tweet.getUsername(), tweet.getTweetId(), "new tweet");
        Assertions.assertNotNull(tweetResponse);
        log.debug("Tested - #MethodName: updateTweet() successfully.");
    }

    @Test
    public void testLikeTweet() throws TweetDoesNotExistException {
        Tweet tweet = new Tweet();
        tweet.setTweetId("1");
        tweet.setTweetText("tweet");
        Mockito.when(tweetRepository.findById("1")).thenReturn(Optional.of(tweet));
        Mockito.when(tweetRepository.save(tweet)).thenReturn(tweet);
        Tweet LikeTweetResponse = tweetService.likeTweet("abc", tweet.getTweetId());
        Assertions.assertNotNull(LikeTweetResponse);
        log.debug("Tested - #MethodName: likeTweet() successfully.");
    }

    @Test
    public void testDislikeTweet() throws TweetDoesNotExistException {
        Tweet tweet = new Tweet();
        tweet.setTweetId("1");
        tweet.setTweetText("tweet");
        List<String> likes = new ArrayList<>();
        likes.add("abc");
        tweet.setLikes(likes);
        Mockito.when(tweetRepository.findById("1")).thenReturn(Optional.of(tweet));
        Mockito.when(tweetRepository.save(tweet)).thenReturn(tweet);
        Tweet LikeTweetResponse = tweetService.dislikeTweet("abc", tweet.getTweetId());
        Assertions.assertNotNull(LikeTweetResponse);
        log.debug("Tested - #MethodName: dislikeTweet() successfully.");
    }

    @Test
    public void testReplyTweet() throws TweetDoesNotExistException {
        Tweet tweet = new Tweet();
        tweet.setTweetId("1");
        tweet.setTweetText("tweet");
        List<String> likes = new ArrayList<>();
        likes.add("abc");
        tweet.setLikes(likes);
        Mockito.when(tweetRepository.findById("1")).thenReturn(Optional.of(tweet));
        Mockito.when(tweetRepository.save(tweet)).thenReturn(tweet);
        Tweet LikeTweetResponse = tweetService.replyTweet("abc", tweet.getTweetId(), "reply");
        Assertions.assertNotNull(LikeTweetResponse);
        log.debug("Tested - #MethodName: replyTweet() successfully.");
    }

}
