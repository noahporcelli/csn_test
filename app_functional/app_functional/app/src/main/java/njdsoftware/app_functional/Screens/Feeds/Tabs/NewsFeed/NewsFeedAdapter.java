package njdsoftware.app_functional.Screens.Feeds.Tabs.NewsFeed;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import njdsoftware.app_functional.R;
import njdsoftware.app_functional.Screens.Feeds.FeedItem;

/**
 * Created by user on 25/07/2016.
 */
public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.MyViewHolder> {

    private List<FeedItem> feedItemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView primaryText, quoteText, likeNumText, commentNumText, likeNumText2, commentNumText2;
        public CircleImageView profileImage;
        public ImageView typeImage;

        public MyViewHolder(View view) {
            super(view);
            //set up view handles.
            primaryText = (TextView) view.findViewById(R.id.user_name_view);
            quoteText = (TextView) view.findViewById(R.id.user_bio_view);
            likeNumText = (TextView) view.findViewById(R.id.like_num_text);
            commentNumText = (TextView) view.findViewById(R.id.comment_num_text);
            likeNumText2 = (TextView) view.findViewById(R.id.like_num_text_2);
            commentNumText2 = (TextView) view.findViewById(R.id.comment_num_text_2);
            profileImage = (CircleImageView) view.findViewById(R.id.user_pic_view);
            typeImage = (ImageView) view.findViewById(R.id.type_image);
        }
    }

    public NewsFeedAdapter(List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.screens_feeds_feed_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FeedItem thisItem = feedItemList.get(position);
        holder.primaryText.setText(thisItem.primaryText);
        holder.quoteText.setText(thisItem.userQuote);
        holder.likeNumText.setText(String.valueOf(thisItem.likeNum));
        holder.commentNumText.setText(String.valueOf(thisItem.commentNum));
        holder.likeNumText2.setText(String.valueOf(thisItem.likeNum));
        holder.commentNumText2.setText(String.valueOf(thisItem.commentNum));
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }
}
