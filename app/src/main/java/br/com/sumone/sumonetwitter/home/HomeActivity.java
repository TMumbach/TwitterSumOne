package br.com.sumone.sumonetwitter.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.sumone.sumonetwitter.R;
import br.com.sumone.sumonetwitter.base.BaseActivity;
import br.com.sumone.sumonetwitter.base.LayoutResource;
import br.com.sumone.sumonetwitter.map.TweetMapFragment;
import br.com.sumone.sumonetwitter.profile.ProfileActivity;
import br.com.sumone.sumonetwitter.timeline.TimeLineFragment;
import br.com.sumone.sumonetwitter.utils.Constants;
import br.com.sumone.sumonetwitter.utils.ImageUtils;
import br.com.sumone.sumonetwitter.utils.TwitterController;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by Tiago on 04/08/2015.
 */
@LayoutResource(contentView = R.layout.activity_home, toolbar = R.id.toolbar, displayHomeAsUp = false)
public class HomeActivity extends BaseActivity implements NavigationDrawer.OnNavigateItemSelected {

    private static final String NAV_ITEM_ID = "navItemId";

    private NavigationDrawer mNavigationDrawer;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int mNavItemId;

        if (null == savedInstanceState) {
            mNavItemId = R.id.drawer_item_time_line;

        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        mNavigationDrawer = new NavigationDrawer(this, mNavItemId);

        mNavigationDrawer.setOnNavigateItemSelected(this);

        handleIntent();

        setFragment(R.id.drawer_item_time_line, getString(R.string.time_line));
    }

    private void handleIntent() {
        Uri uri = getIntent().getData();

        if (uri != null && uri.toString().startsWith(Constants.TWITTER_CALLBACK_URL)) {
            String verifier = uri.getQueryParameter(Constants.IEXTRA_OAUTH_VERIFIER);
            new TwitterAccessToken().execute(verifier);
        }
        else {
            new TwitterAccessToken().execute();
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mNavigationDrawer.setConfiguration(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
            return mNavigationDrawer.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerpen()) {
            mNavigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavigationDrawer.getNavItemId());
    }

    /**
     * Carrega os dados do usuario para o cabecalho do menu lateral
     *
     * @see NavigationDrawer
     * @param user
     */
    private void setupDrawerHeader(User user) {
        mUser = user;
        ((TextView) findViewById(R.id.textViewUserName)).setText(user.getName());
        ((TextView) findViewById(R.id.textViewTwitterName)).setText("@" + user.getScreenName());

        ImageUtils.setImageIntoFromURL((ImageView) findViewById(R.id.imageViewProfile), user.getOriginalProfileImageURL());

        findViewById(R.id.imageViewProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra(ProfileActivity.ARG_USER_ID, mUser.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void itemSelected(MenuItem item) {
        setFragment(item.getItemId(), item.getTitle());
    }

    private void setFragment(int drawerItemId, CharSequence title) {
        Fragment mCurrentFragment = null;

        switch (drawerItemId) {
            case R.id.drawer_item_time_line:
                mCurrentFragment = TimeLineFragment.newInstance();
                break;
            case R.id.drawer_item_map:
                mCurrentFragment = TweetMapFragment.newInstance();
                break;
            case R.id.drawer_exit:
                logout();
                break;

        }
        getSupportActionBar().setTitle(title);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, mCurrentFragment).commit();
    }

    private void logout() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.PREF_KEY_TOKEN);
        editor.remove(Constants.PREF_KEY_SECRET);
        editor.remove(Constants.PREF_KEY_TWITTER_IS_LOGGED_IN);
        editor.commit();

        TwitterController.stop();
    }

    /**
     * Busca o Token de acesso ao Twitter
     */
    class TwitterAccessToken extends AsyncTask<String, String, User> {

        @Override
        protected User doInBackground(String... params) {
            Twitter twitter = TwitterController.getInstance().getTwitter();
            RequestToken requestToken = TwitterController.getInstance().getRequestToken();

            if(params != null && (params.length > 0 && !params[0].isEmpty())) {
                String verifier = params[0];

                try {
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString(Constants.PREF_KEY_TOKEN, accessToken.getToken());
                    editor.putString(Constants.PREF_KEY_SECRET, accessToken.getTokenSecret());
                    editor.putBoolean(Constants.PREF_KEY_TWITTER_IS_LOGGED_IN, true);
                    editor.commit();

                    return twitter.showUser(accessToken.getUserId());
                } catch (Exception e) {

                }
            }
            else {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String accessTokenString = sharedPreferences.getString(Constants.PREF_KEY_TOKEN, "");
                String accessTokenSecret = sharedPreferences.getString(Constants.PREF_KEY_SECRET, "");
                AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                try {
                    TwitterController.getInstance().setTwitterFactory(accessToken);
                    return TwitterController.getInstance().getTwitter().showUser(accessToken.getUserId());
                } catch (TwitterException e) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            setupDrawerHeader(user);
        }
    }

}
