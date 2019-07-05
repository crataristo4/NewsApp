package com.hard.code.tech.newsapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.hard.code.tech.newsapp.R;
import com.hard.code.tech.newsapp.databinding.ActivityNewsDetailsBinding;
import com.hard.code.tech.newsapp.utils.Utils;

public class NewsDetailsActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

    LinearLayout titleAppBar;
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    String mUrl, mImg, mTitle, mDate, mSource, mAuthor;
    WebView webView;
    private ActivityNewsDetailsBinding activityNewsDetailsBinding;
    private ImageView imageView;
    private TextView txtAppBarTitle, txtAppBarSubTitle, txtDate, txtTime, txtTitle;
    private boolean isToolbarView = false;
    private FrameLayout dateFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNewsDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_news_details);

        initViews();

    }

    private void initViews() {

        toolbar = activityNewsDetailsBinding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appBarLayout = activityNewsDetailsBinding.appbar;
        appBarLayout.addOnOffsetChangedListener(this);

        CollapsingToolbarLayout collapsingToolbarLayout = activityNewsDetailsBinding.collapsingToolbar;
        collapsingToolbarLayout.setTitle("");

        dateFrameLayout = activityNewsDetailsBinding.dateBehavior;
        titleAppBar = activityNewsDetailsBinding.titleAppbar;
        imageView = activityNewsDetailsBinding.backdrop;
        txtAppBarTitle = activityNewsDetailsBinding.titleOnAppbar;
        txtAppBarSubTitle = activityNewsDetailsBinding.subtitleOnAppbar;
        txtDate = activityNewsDetailsBinding.date;
        txtTime = activityNewsDetailsBinding.time;
        txtTitle = activityNewsDetailsBinding.title;

        Intent getNewsDetailsIntent = getIntent();
        if (getNewsDetailsIntent != null) {
            mUrl = getNewsDetailsIntent.getStringExtra("url");
            mImg = getNewsDetailsIntent.getStringExtra("urlToImage");
            mAuthor = getNewsDetailsIntent.getStringExtra("author");
            mSource = getNewsDetailsIntent.getStringExtra("source");
            mTitle = getNewsDetailsIntent.getStringExtra("txtTitle");
            mDate = getNewsDetailsIntent.getStringExtra("pubDate");

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.error(Utils.getRandomDrawbleColor());

            Glide.with(this).load(mImg).apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);

            txtAppBarTitle.setText(mSource);
            txtAppBarSubTitle.setText(mUrl);
            txtDate.setText(Utils.DateFormat(mDate));
            txtTitle.setText(mTitle);

            String author = null;
            if (mAuthor != null) {
                author = "\u2022" + mAuthor;
            } else {
                author = "";
            }

            txtTime.setText(mSource + author + " \u2022 " + Utils.DateToTimeFormat(mDate));

            initWebView(mUrl);

        }
    }

    private void initWebView(String url) {
        webView = activityNewsDetailsBinding.webView;
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffSet) {

        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffSet) / (float) maxScroll;

        if (percentage == 1f && isToolbarView) {
            dateFrameLayout.setVisibility(View.GONE);
            titleAppBar.setVisibility(View.VISIBLE);
            isToolbarView = !isToolbarView;

        } else if (percentage < 1f && isToolbarView) {
            dateFrameLayout.setVisibility(View.VISIBLE);
            titleAppBar.setVisibility(View.GONE);
            isToolbarView = !isToolbarView;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Intent intent;
        if (itemId == R.id.action_view_web) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mUrl));
            startActivity(intent);

            return true;

        } else if (itemId == R.id.action_share) {

            try {
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, mSource);
                String body = mTitle + "\n\n" + mUrl + "\n\n" + "Shared from My News App" + "\n\n";
                intent.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(intent, "Share with :"));


            } catch (Exception e) {
                Utils.displayToast(NewsDetailsActivity.this, "Error " + e.getLocalizedMessage());
            }

        }

        return super.onOptionsItemSelected(item);
    }
}
