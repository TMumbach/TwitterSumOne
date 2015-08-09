package br.com.sumone.sumonetwitter.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import br.com.sumone.sumonetwitter.R;
import br.com.sumone.sumonetwitter.base.BaseActivity;
import br.com.sumone.sumonetwitter.base.LayoutResource;
import br.com.sumone.sumonetwitter.home.HomeActivity;
import br.com.sumone.sumonetwitter.utils.Constants;
import br.com.sumone.sumonetwitter.utils.DisplayUtils;
import br.com.sumone.sumonetwitter.utils.TwitterController;
import twitter4j.auth.RequestToken;

@LayoutResource(contentView = R.layout.activity_login, toolbar = R.id.toolbar)
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolbarView().setTranslationY(-DisplayUtils.dpToPx(72));
        getToolbarView().animate()
                .translationY(0)
                .setStartDelay(500)
                .setDuration(400)
                .start();

        if(!isConnected()) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.getBoolean(Constants.PREF_KEY_TWITTER_IS_LOGGED_IN, false)) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void login(View v) {
        if(!isConnected()) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        new TwitterLoginTask().execute();
    }

    class TwitterLoginTask extends AsyncTask<String, String, RequestToken> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected RequestToken doInBackground(String... params) {
            return TwitterController.getInstance().getRequestToken();
        }

        @Override
        protected void onPostExecute(RequestToken requestToken) {
            super.onPostExecute(requestToken);

            if(requestToken != null) {
                Intent intent = new Intent(getApplicationContext(), TwitterWebViewActivity.class);
                intent.putExtra(TwitterWebViewActivity.ARG_AUTHENTICATION_URL, requestToken.getAuthenticationURL());
                startActivity(intent);
            }
        }
    }

    public boolean isConnected() {
        ConnectivityManager connective = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connective != null) {
            NetworkInfo[] info = connective.getAllNetworkInfo();
            if(info != null){
                for(int i = 0; i < info.length; i++)
                    if(info[i].getState()== NetworkInfo.State.CONNECTED)
                        return true;
            }
        }
        return false;
    }
}