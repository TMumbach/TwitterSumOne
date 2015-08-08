package br.com.sumone.sumonetwitter.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import br.com.sumone.sumonetwitter.R;
import br.com.sumone.sumonetwitter.base.BaseActivity;
import br.com.sumone.sumonetwitter.base.InjectView;
import br.com.sumone.sumonetwitter.base.LayoutResource;
import br.com.sumone.sumonetwitter.home.HomeActivity;

/**
 * Created by Tiago on 06/08/2015.
 *
 * Classe vara logar o aplicativo com o twitter, requisita um token
 * bem como autoriza a aplicacao usar o twitter
 */
@LayoutResource(contentView = R.layout.activity_twitter_webview)
public class TwitterWebViewActivity extends BaseActivity {

    public static final String ARG_AUTHENTICATION_URL = "authentication_url";

    private String authenticationUrl;

    @InjectView(R.id.webView)
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authenticationUrl = getIntent().getExtras().getString(ARG_AUTHENTICATION_URL);

        mWebView.loadUrl(authenticationUrl);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("oauth_verifier=")) {
                    Intent intent = new Intent(TwitterWebViewActivity.this, HomeActivity.class);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    finish();
                }
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings webSettings= mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
}
