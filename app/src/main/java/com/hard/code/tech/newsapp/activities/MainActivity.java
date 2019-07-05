package com.hard.code.tech.newsapp.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hard.code.tech.newsapp.R;
import com.hard.code.tech.newsapp.adapters.NewsAdapter;
import com.hard.code.tech.newsapp.api.APIClient;
import com.hard.code.tech.newsapp.api.API_Interface;
import com.hard.code.tech.newsapp.databinding.ActivityMainBinding;
import com.hard.code.tech.newsapp.models.Article;
import com.hard.code.tech.newsapp.models.News;
import com.hard.code.tech.newsapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {


    ConstraintLayout errorConstraintLayout;
    private ActivityMainBinding activityMainBinding;
    private RecyclerView recyclerView;
    private List<Article> articleList = new ArrayList<>();
    private NewsAdapter newsAdapter;
    private TextView txtHeadline, txtErrorTitle, txtErrorMsg;
    private ImageView errorImg;
    private Button btnRetry;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initViews();

        onSwipeLoading("");
    }

    private void initViews() {
        txtHeadline = activityMainBinding.idHeadLine;
        swipeRefreshLayout = activityMainBinding.swipeRefresh;
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        txtErrorTitle = activityMainBinding.LayoutError.errorTitle;
        txtErrorMsg = activityMainBinding.LayoutError.errorMessage;
        errorConstraintLayout = activityMainBinding.LayoutError.errorLayout;
        btnRetry = activityMainBinding.LayoutError.btnRetry;
        errorImg = activityMainBinding.LayoutError.errorImage;

        swipeRefreshLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    swipeRefreshLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    swipeRefreshLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                Rect rect = new Rect();
                swipeRefreshLayout.getDrawingRect(rect);
                swipeRefreshLayout.setProgressViewOffset(false, 0, rect.centerY() - (swipeRefreshLayout.getProgressCircleDiameter() / 2));
            }
        });


    }


    void showErrorMessage(int imageView, String errTitle, String errMsg) {

        if (errorConstraintLayout.getVisibility() == View.GONE) {
            errorConstraintLayout.setVisibility(View.VISIBLE);
        }

        errorImg.setImageResource(imageView);
        txtErrorTitle.setText(errTitle);
        txtErrorMsg.setText(errMsg);

        btnRetry.setOnClickListener(view -> {

            onSwipeLoading("");

        });

    }

    private void loadNewsData(String keyword) {
        errorConstraintLayout.setVisibility(View.GONE);
        txtHeadline.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        API_Interface api = APIClient.getApiClient().getAPI();

        recyclerView = activityMainBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        String country = Utils.getCountry();
        String language = Utils.getLanguage();

        Call<News> newsCall;


        if (keyword.length() > 0) {
            newsCall = api.getEverything(keyword, language, "publishedAt", getString(R.string.apiKey));
        } else {
            newsCall = api
                    .getNews(country, getString(R.string.apiKey));

        }


        newsCall.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {

                if (response.isSuccessful() && response.body().getArticles() != null) {

                    if (!articleList.isEmpty()) {
                        articleList.clear();
                    }

                    articleList = response.body().getArticles();
                    newsAdapter = new NewsAdapter(articleList, MainActivity.this);
                    recyclerView.setAdapter(newsAdapter);
                    newsAdapter.notifyDataSetChanged();
                    txtHeadline.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);

                    initListener();

                } else {

                    txtHeadline.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    //   Utils.displayToast(MainActivity.this, "Error : " + response.message());
                    String errCode;
                    switch (response.code()) {
                        case 404:
                            errCode = "404 not found";
                            break;
                        case 500:
                            errCode = "Aww! snap.. Broken server";
                            break;
                        default:
                            errCode = "Ooops! unknown error";
                            break;
                    }

                    showErrorMessage(R.drawable.no_result,
                            "No results found",
                            "Please try again\n" + errCode);
                }

            }

            @Override
            public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {
                //Utils.displayToast(MainActivity.this, "Error : " + t.getLocalizedMessage());
                showErrorMessage(R.drawable.oops,
                        "Aww! snap",
                        "Network failure , Please try again\n" + t.getLocalizedMessage());
                swipeRefreshLayout.setRefreshing(false);
                txtHeadline.setVisibility(View.GONE);
            }
        });

    }


    private void initListener() {
        newsAdapter.setOnItemClickListener((view, position) -> {
            ImageView imageView = view.findViewById(R.id.imgNewsPhoto);

            Article article = articleList.get(position);
            Intent intent = new Intent(MainActivity.this, NewsDetailsActivity.class);

            intent.putExtra("url", article.getUrl());
            intent.putExtra("title", article.getTitle());
            intent.putExtra("urlToImage", article.getUrlToImage());
            intent.putExtra("pubDate", article.getPublishedAt());
            intent.putExtra("source", article.getSource().getName());
            intent.putExtra("author", article.getAuthor());

            Pair<View, String> pair = Pair.create(imageView, ViewCompat.getTransitionName(imageView));
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    MainActivity.this,
                    pair
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(intent, activityOptionsCompat.toBundle());
            } else {
                startActivity(intent);
            }


        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search for latest news");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {//loadNewsData(query);
                    onSwipeLoading(query);
                } else {
                    Utils.displayToast(MainActivity.this, "Must type two or more words");
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //loadNewsData(newText);
                return false;
            }
        });

        searchMenuItem.getIcon().setVisible(false, false);
        return true;
    }

    @Override
    public void onRefresh() {
        loadNewsData("");
    }

    public void onSwipeLoading(String keyword) {
        swipeRefreshLayout.post(() -> loadNewsData(keyword));
    }
}
