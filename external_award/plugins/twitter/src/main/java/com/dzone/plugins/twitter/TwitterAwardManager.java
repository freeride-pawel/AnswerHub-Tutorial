package com.dzone.plugins.twitter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;

import com.qato.dao.awards.AwardDao;
import com.qato.infrastructure.spring.RequestInfo;
import com.qato.managers.award.builtin.AwardRunnerStore;
import com.qato.managers.award.builtin.BuiltinAwardRunner;
import com.qato.managers.user.UserManager;
import com.qato.models.award.Award;
import com.qato.models.award.AwardType.Level;
import com.qato.models.site.Site;
import com.qato.models.user.AuthenticationMode;
import com.qato.models.user.User;

@Component 

public class TwitterAwardManager {
	@Autowired 
	AwardDao awardDao;
	
	@Autowired
	UserManager userManager;
	
	@Autowired
    ConnectionRepository connectionRepository; 

	@Autowired
	RequestInfo requestInfo;
	
	
	
	/** 
	 * This method goes through all the users and works out which users to grant
	 * different levels of Twitter awards to. 
	 */
	public List<User> getUsersToAward(Level level)
	{
		//BuiltinAwardRunner goldAward = builtinRunners.get("twitter_gold_award");
		//BuiltinAwardRunner silverAward = builtinRunners.get("twitter_silver_award");
		List<User> users = new ArrayList<User>();
		
		Connection<Twitter> connection = connectionRepository.findPrimaryConnection(Twitter.class);
		Twitter twitter = connection != null ? connection.getApi() : new TwitterTemplate();
		List<User> userList = userManager.getAllUsers();//getSortedByReputationInSite(1,100, site);
		System.out.println("I know about " + userList.size() + " users ");
		List<Award> awards = new ArrayList<Award>();
		Site site = requestInfo.getSite();
		for(User user:userList)
		{
			//check that this user has Twitter authentication enabled 
			for(AuthenticationMode mode: user.getAuthModes())
			{
				/**
				 * Check if the user has got Twitter
				 */
				if(mode.getAuthService().equalsIgnoreCase("twitter"))
				{
					//Don't re-award stuff 
					List<Object[]> existingAwards = awardDao.getSiteAndUserAwards(user, site);
					boolean hasGold = false; 
					boolean hasSilver = false;
					for(Object[] objects : existingAwards)
					{
						if(((String)objects[1]).equalsIgnoreCase(TwitterGoldAwardRunner.NAME))
						{
							hasGold = true;
						}
						if(((String)objects[1]).equalsIgnoreCase(TwitterSilverAwardRunner.NAME))
						{
							hasSilver = true;
						}
					}
					
					//Get the number of followers for this user's ID
					Long id = new Long(mode.getAuthInfo());
					int numFollowers = twitter.friendOperations().getFollowerIds(id).size();
					if(numFollowers > 10000 && level.equals(Level.gold))
					{	
						System.out.println("Give gold awards");
						//if this has not already been awarded.
						if(!hasGold)
						{
							users.add(user);		
						}
					}
					if(numFollowers > 1000 && numFollowers < 10000 && level.equals(Level.silver))
					{
						System.out.println("Give silver award");
						//atype.setLevel(Level.silver);
						//silverAward.award(requestInfo, null);
						if(!hasSilver)
						{
							users.add(user);
						}
					}
				}
			}
		}
		return users;
	}

}
