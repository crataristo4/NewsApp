package com.hard.code.tech.newsapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hard.code.tech.newsapp.R;
import com.hard.code.tech.newsapp.models.Article;
import com.hard.code.tech.newsapp.utils.Utils;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<Article> articleList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public NewsAdapter(List<Article> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items_news, parent, false),
                onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {

        NewsViewHolder newsViewHolder = holder;
        Article article = articleList.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(article.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        newsViewHolder.loading.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        newsViewHolder.loading.setVisibility(View.INVISIBLE);
                        return false;
                    }
                }).transition(DrawableTransitionOptions.withCrossFade())
                .into(newsViewHolder.imgPhoto);

        newsViewHolder.txtTitle.setText(article.getTitle());
        newsViewHolder.txtDesc.setText(article.getDescription());
        newsViewHolder.txtSource.setText(article.getSource().getName());
        newsViewHolder.txtAuthor.setText(article.getAuthor());
        newsViewHolder.txtPubDate.setText(Utils.DateFormat(article.getPublishedAt()));
        newsViewHolder.txtTime.setText("\u2022" + Utils.DateToTimeFormat(article.getPublishedAt()));


    }

    @Override
    public int getItemCount() {
        return articleList == null ? 0 : articleList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView txtTitle, txtPubDate, txtTime,
                txtSource, txtAuthor, txtDesc;
        private ImageView imgPhoto;
        private ProgressBar loading;
        private OnItemClickListener itemClickListener;

        NewsViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            itemView.setOnClickListener(this);
            this.itemClickListener = onItemClickListener;

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            txtPubDate = itemView.findViewById(R.id.txtPubDate);
            txtSource = itemView.findViewById(R.id.txtSource);
            txtTime = itemView.findViewById(R.id.txtTime);
            imgPhoto = itemView.findViewById(R.id.imgNewsPhoto);
            loading = itemView.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(view, getAdapterPosition());


        }
    }
}
