package com.dzone.plugins.twitter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

import com.qato.dao.awards.AwardDao;
import com.qato.dao.awards.BuiltinAwardDao;
import com.qato.infrastructure.spring.RequestInfo;
import com.qato.managers.award.builtin.BuiltinAwardRunner;
import com.qato.managers.user.UserManager;
import com.qato.models.action.Action;
import com.qato.models.award.Award;
import com.qato.models.award.AwardType;
import com.qato.models.award.AwardType.Level;
import com.qato.models.award.AwardType.Mode;
import com.qato.models.site.Site;
import com.qato.models.user.AuthenticationMode;
import com.qato.models.user.User;

public class TwitterSilverAwardRunner implements BuiltinAwardRunner {

	@Autowired 
	TwitterAwardManager twitterAwardManager; 
	
	
	public static final String NAME = "Twitter Silver Award";
		
	@Override
	public boolean canAward(RequestInfo info, Action action) {
		return true;
	}

	@Override
	public Collection<Award> award(RequestInfo info, Action action) {
		return null;

	
	}

	@Override
	public Collection<Award> catchUp(Site site, String key) {
		System.out.println("Get Catching Up on Silver...");
		List<Award> awards = new ArrayList<Award>();
		List<User> users = twitterAwardManager.getUsersToAward(Level.silver);
		System.out.println("Awarding " + users.size() + " with silver twitter award");
		for(User user: users)
		{
			Award a = new Award();
			a.setUser(user);
			a.setSite(site);
			awards.add(a);
		}
		
		return awards;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "This Twitter award is given to Twitter users with over 1000 followers";
	}

	@Override
	public Level getLevel() {
		return Level.silver;
	}

	@Override
	public Mode getMode() {
		return Mode.per_user;
	}

	@Override
	public Set<Class<?>> getListening() {
		return null;
	}

}
