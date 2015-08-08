package br.com.sumone.sumonetwitter.utils;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Tiago on 05/08/2015.
 *
 * Classe Utilitaria para controle das funcionalidades do Twitter4j
 */
public class TwitterController {

    private Twitter twitter;
    private TwitterFactory twitterFactory = null;
    private RequestToken requestToken;
    private User user;

    static TwitterController twitterController = new TwitterController();

    public static TwitterController getInstance() {
        return twitterController;
    }

    /**
     * Define as configurações para Twitter4j
     */
    public TwitterController() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET);
        Configuration configuration = configurationBuilder.build();
        twitterFactory = new TwitterFactory(configuration);
        twitter = twitterFactory.getInstance();
    }

    private TwitterFactory getTwitterFactory() {
        return twitterFactory;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public RequestToken getRequestToken() {
        if(requestToken == null) {
            try {
                requestToken = twitter.getOAuthRequestToken(Constants.TWITTER_CALLBACK_URL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return requestToken;
    }

    public void setTwitterFactory(AccessToken accessToken) {
        twitter = twitterFactory.getInstance(accessToken);
    }

    public static void stop() {
        twitterController = new TwitterController();
    }
}
