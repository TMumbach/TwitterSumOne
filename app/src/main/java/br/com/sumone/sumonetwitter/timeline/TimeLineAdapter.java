package br.com.sumone.sumonetwitter.timeline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ocpsoft.pretty.time.PrettyTime;

import java.util.List;

import br.com.sumone.sumonetwitter.R;
import br.com.sumone.sumonetwitter.utils.ImageUtils;
import twitter4j.Status;

/**
 * Created by tiago on 07/08/2015.
 */
public class TimeLineAdapter extends BaseAdapter {

    List<Status> statuses;

    public TimeLineAdapter(List<Status> statuses) {
        this.statuses = statuses;
    }

    public void update(List<Status> statuses) {
        this.statuses = statuses;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return statuses.size();
    }

    @Override
    public Object getItem(int i) {
        return statuses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        final ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_twitter_timeline, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        Status status = statuses.get(position);

        ImageUtils.setImageIntoFromURL(holder.imageViewUser, status.getUser().getOriginalProfileImageURL());

        holder.textViewUserName.setText(status.getUser().getName());
        holder.textViewScreenName.setText("@" + status.getUser().getScreenName());
        holder.textViewTimeAgo.setText(new PrettyTime().format(status.getCreatedAt()));
        holder.textViewTweetText.setText(status.getText());

        return view;
    }

    class ViewHolder {

        ImageView imageViewUser;
        TextView textViewUserName;
        TextView textViewScreenName;
        TextView textViewTimeAgo;
        TextView textViewTweetText;

        public ViewHolder(View itemView) {
            imageViewUser = (ImageView) itemView.findViewById(R.id.imageViewUser);
            textViewUserName = (TextView) itemView.findViewById(R.id.textViewUserName);
            textViewScreenName = (TextView) itemView.findViewById(R.id.textViewScreenName);
            textViewTimeAgo = (TextView) itemView.findViewById(R.id.textViewTimeAgo);
            textViewTweetText = (TextView) itemView.findViewById(R.id.textViewTweetText);
        }
    }
}
