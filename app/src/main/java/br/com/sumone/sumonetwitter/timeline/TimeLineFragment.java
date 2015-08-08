package br.com.sumone.sumonetwitter.timeline;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.com.sumone.sumonetwitter.R;
import br.com.sumone.sumonetwitter.profile.ProfileActivity;
import br.com.sumone.sumonetwitter.utils.TwitterController;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * Created by Tiago on 06/08/2015.
 *
 * Carrega a timeline do usuario
 */

public class TimeLineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private ListView mListViewTimeLine;
    private TimeLineAdapter mTimeLineAdapter;
    private SwipeRefreshLayout mSwipeLayout;

    public static TimeLineFragment newInstance() {
        return new TimeLineFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mListViewTimeLine = (ListView) view.findViewById(R.id.listViewTimeLine);
        mListViewTimeLine.setOnItemClickListener(this);

        setupRefresh();
        setupAdapter();

        new LoadTimeLine().execute();
    }

    private void setupAdapter() {
        mTimeLineAdapter = new TimeLineAdapter(new ArrayList<Status>(0));
        mListViewTimeLine.setAdapter(mTimeLineAdapter);
    }

    private void setupRefresh() {
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    @Override
    public void onRefresh() {
        new LoadTimeLine().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long idUser = ((Status) parent.getItemAtPosition(position)).getUser().getId();
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.ARG_USER_ID, idUser);
        startActivity(intent);
    }

    /**
     * Busca a time line
     */
    class LoadTimeLine extends AsyncTask<String, String, List<Status>> {

        @Override
        protected List<twitter4j.Status> doInBackground(String... strings) {
            try {
                return TwitterController.getInstance().getTwitter().getHomeTimeline();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> statuses) {
            super.onPostExecute(statuses);
            mTimeLineAdapter.update(statuses);
            mSwipeLayout.setRefreshing(false);
        }
    }
}
