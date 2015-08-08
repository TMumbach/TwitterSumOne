package br.com.sumone.sumonetwitter.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import br.com.sumone.sumonetwitter.R;
import br.com.sumone.sumonetwitter.base.BaseActivity;
import br.com.sumone.sumonetwitter.base.InjectView;
import br.com.sumone.sumonetwitter.base.LayoutResource;
import br.com.sumone.sumonetwitter.timeline.TimeLineAdapter;
import br.com.sumone.sumonetwitter.utils.ImageUtils;
import br.com.sumone.sumonetwitter.utils.TwitterController;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Created by Tiago on 07/08/2015.
 *
 * Classe para exibir o perfil e timeline de um usuario
 * nesessario passar um argumento com o id do usuario
 *
 * Long = ARG_USER_ID
 *
 */
@LayoutResource(contentView = R.layout.activity_profile, toolbar = R.id.toolbar)
public class ProfileActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener{

    public static final String ARG_USER_ID = "user_id";

    @InjectView(R.id.listViewProfile)
    private ListView mListViewProfile;

    @InjectView(R.id.swipe_container)
    private SwipeRefreshLayout mSwipeLayout;

    private TimeLineAdapter mTimeLineAdapter;

    private int lastTopValueAssigned = 0;

    private Long idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        handleIntent(getIntent());

        mSwipeLayout.setOnRefreshListener(this);

        setupHeader();
        setupAdapter();

        new LoadUser().execute(idUser);
        new LoadUserTimeLine().execute(idUser);
    }

    private void handleIntent(Intent intent) {
        idUser = intent.getExtras().getLong(ARG_USER_ID);
    }

    private void setupAdapter() {
        mTimeLineAdapter = new TimeLineAdapter(new ArrayList<twitter4j.Status>(0));
        mListViewProfile.setAdapter(mTimeLineAdapter);
    }

    private void setupHeader() {
        View view = LayoutInflater.from(this).inflate(R.layout.header_profile, mListViewProfile, false);
        mListViewProfile.addHeaderView(view);
        mListViewProfile.setOnScrollListener(this);
    }

    /**
     * Seta as informcoes do usuario no cabecalho da listView
     * @param user usuario carregado
     */
    private void configureHeaderUser(User user) {
        getSupportActionBar().setTitle(user.getName());
        getSupportActionBar().setSubtitle("@" + user.getScreenName());

        setText(R.id.textViewUserName, user.getName());
        setText(R.id.textViewTwitterName, "@" + user.getScreenName());

        setText(R.id.textViewFollowers, String.valueOf(user.getFollowersCount()));
        setText(R.id.textViewFollowing, String.valueOf(user.getFriendsCount()));
        setText(R.id.textViewTweets, String.valueOf(user.getStatusesCount()));

        ImageUtils.setImageIntoFromURL((ImageView) findViewById(R.id.imageViewUserProfile), user.getOriginalProfileImageURL());

        if(user.getProfileBannerURL() != null) {
            findViewById(R.id.headerContent).setBackgroundColor(Color.parseColor("#"+user.getProfileBackgroundColor()));
            ImageUtils.setImageIntoFromURL((ImageView) findViewById(R.id.imageViewBanner), user.getOriginalProfileImageURL(), false);
            final Target target = new Target() {

                @Override
                public void onPrepareLoad(Drawable arg0) {
                }

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                    ((ImageView) findViewById(R.id.imageViewBanner)).setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable arg0) {
                }
            };
            findViewById(R.id.imageViewBanner).setTag(target);
            Picasso.with(this).load(user.getProfileBannerURL()).into(target);
        }
    }

    @Override
    public void onRefresh() {
        new LoadUserTimeLine().execute(idUser);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {  }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        parallaxView(findViewById(R.id.imageViewBanner));
    }

    /**
     * Cria um efeito parallax na imagem de banner do usuario
     * @param view
     */
    private void parallaxView(View view) {
        Rect rect = new Rect();
        view.getLocalVisibleRect(rect);
        if (lastTopValueAssigned != rect.top) {
            lastTopValueAssigned = rect.top;
            view.setY(rect.top / 2.0f);
        }
    }

    class LoadUser extends AsyncTask<Long, String, User> {

        @Override
        protected User doInBackground(Long... ids) {
            try {
                return TwitterController.getInstance().getTwitter().showUser(ids[0]);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            configureHeaderUser(user);
        }

    }

    class LoadUserTimeLine extends AsyncTask<Long, String, List<Status>> {

        @Override
        protected List<twitter4j.Status> doInBackground(Long... ids) {
            try {
                return TwitterController.getInstance().getTwitter().getUserTimeline(ids[0]);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> statuses) {
            super.onPostExecute(statuses);
            mSwipeLayout.setRefreshing(false);
            mTimeLineAdapter.update(statuses);
        }
    }

    public void setText(int resId, String text) {
        ((TextView) findViewById(resId)).setText(text);
    }
}
