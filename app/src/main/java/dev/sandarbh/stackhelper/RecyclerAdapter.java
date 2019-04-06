package dev.sandarbh.stackhelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okio.Utf8;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PostHolder> {

    private Context context;
    private List<Post> postsList;

    public RecyclerAdapter(Context c,List<Post> list){
        postsList = list;
        context = c;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View post = inflater.inflate(R.layout.post,viewGroup,false);

        PostHolder newHolder = new PostHolder(post);
        Log.e("DEBUG", "onCreateViewHolder: ");
        return newHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder postHolder, int position) {

        Log.e("DEBUG", "onBindViewHolder: ");

        Post newPost = postsList.get(position);
        String tmp;

        if(newPost.isAnswered)
            postHolder.qTitle.setBackground(context.getResources().getDrawable(R.drawable.post_top_answered));

        postHolder.qTitle.setText(newPost.qTitle);
        postHolder.qBody.setText(newPost.qBody);

        tmp = "Answers : "+ newPost.getFormattedValue(newPost.answerCount);
        postHolder.answerCount.setText(tmp);

        tmp = "Votes : "+newPost.getFormattedValue(newPost.votesCount);
        postHolder.votesCount.setText(tmp);

        //SET TAGS

        postHolder.username.setText(newPost.user.username);
        if(newPost.user.reputation == -1){
            postHolder.reputation.setText("-");
        }
        else{
            postHolder.reputation.setText(newPost.getFormattedValue(newPost.user.reputation));
        }

        tmp = "Views : "+newPost.getFormattedValue(newPost.viewCount);
        postHolder.viewCount.setText(tmp);

        postHolder.dateCreated.setText(newPost.dateCreated);

        Picasso.get().load(newPost.user.avatarURL.toString()).
                into(postHolder.userAvatar);

        postHolder.userInfo.setOnClickListener(v -> {
            context.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(newPost.user.profileURL)));
        });

    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder {

        private TextView qTitle,qBody,answerCount,votesCount,tags,username,reputation,viewCount,dateCreated;
        private ImageView userAvatar;
        private LinearLayout userInfo;

        public PostHolder(View itemView) {
            super(itemView);

            Log.e("DEBUG", "PostHolder: ");
            qTitle = itemView.findViewById(R.id.qTitle);
            qBody = itemView.findViewById(R.id.qBody);
            answerCount = itemView.findViewById(R.id.num_ans);
            votesCount = itemView.findViewById(R.id.num_votes);
            tags = itemView.findViewById(R.id.tags);
            username = itemView.findViewById(R.id.username);
            reputation = itemView.findViewById(R.id.user_reputation);
            viewCount = itemView.findViewById(R.id.views);
            dateCreated = itemView.findViewById(R.id.dateCreated);

            userAvatar = itemView.findViewById(R.id.avatar);

            userInfo = itemView.findViewById(R.id.user_info);
        }
    }

}
