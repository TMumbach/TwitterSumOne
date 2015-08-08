package br.com.sumone.sumonetwitter.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.lang.reflect.Field;

/**
 * Created by Tiago on 04/08/2015.
 *
 * Activity com definições basicas
 */
public class BaseActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutResource layoutResource = getClass().getAnnotation(LayoutResource.class);

        setContentView(layoutResource.contentView());

        if(layoutResource.toolbar() != -1) {
            mToolbar = (Toolbar) findViewById(layoutResource.toolbar());
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(layoutResource.displayHomeAsUp());
        }

        injectViews();
    }

    public Toolbar getToolbarView() {
        return mToolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Busca todos os campos com com a InjectView em automatiza
     * findViewById
     *
     *  @see InjectView
     */
    private void injectViews() {
        Field[] declaredFields = getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if(declaredField.isAnnotationPresent(InjectView.class)) {
                declaredField.setAccessible(true);
                try {
                    declaredField.set(this, findViewById(declaredField.getAnnotation(InjectView.class).value()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
