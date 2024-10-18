package fr.charlesmj.ouichat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<Post> posts;
    private Context context;

    public Adapter(Context context, ArrayList<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post item = posts.get(position);
        Timestamp timestamp = item.getDate();
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
        holder.date.setText(sdf.format(date));

        holder.content.setText(item.getContent());
        holder.likeCount.setText(String.valueOf(item.getLikes()));
        holder.commentCount.setText(String.valueOf(item.getComments()));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView likeIcon, commentIcon;
        TextView username, date, content, likeCount, commentCount;

        public ViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.username);
            date = view.findViewById(R.id.date);
            content = view.findViewById(R.id.content);
            likeIcon = view.findViewById(R.id.like_icon);
            likeCount = view.findViewById(R.id.like_count);
            commentIcon = view.findViewById(R.id.comment_icon);
            commentCount = view.findViewById(R.id.comment_count);
        }
    }
}