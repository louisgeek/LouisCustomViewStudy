package com.louisgeek.louiscustomviewstudy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.louisgeek.likedoubanloadingview.LikeDouBanSmileLoadingView;

public class MainActivity extends AppCompatActivity {

    float progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(new CustomView02(this));
        // setContentView(new BitmapCustomView03(this));
        // setContentView(new LoadingCustomView04(this));
        //  setContentView(new LoadingCustomView05(this));
        // setContentView(new CustomView06(this));
        //   setContentView(new CustomSearchView07(this));
      // setContentView(new LikeDouBanSmileLoadingView(this));
        //setContentView(new RadarVIew08(this));
        // setContentView(new BezierCurveView(this));
        //setContentView(new BezierCurveViewThird(this));
        // setContentView(new BezierCurveViewDrawCircle(this));
         /*setContentView(new Path_EVEN_ODD_WINDING_View(this));*/
        //setContentView(new BezierCurveViewCircle2Heart(this));
      final LikeDouBanSmileLoadingView id_smile_loading = (LikeDouBanSmileLoadingView) findViewById(R.id.id_smile_loading);
        Button id_start = (Button) findViewById(R.id.id_start);
        Button id_stop = (Button) findViewById(R.id.id_stop);

        id_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_smile_loading.startLoad();
            }
        });

        id_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_smile_loading.stopLoading();
            }
        });
    /*    final CustomSearchView07 id_search = (CustomSearchView07) findViewById(R.id.id_search);
        Button id_start = (Button) findViewById(R.id.id_start);
        Button id_stop = (Button) findViewById(R.id.id_stop);

        id_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_search.startSearch();
            }
        });

        id_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_search.stopSearchLoading();
            }
        });*/

   /*     final LoadingCustomView04 id_loading = (LoadingCustomView04) findViewById(R.id.id_loading);
        final LoadingCustomView04 id_loading_2 = (LoadingCustomView04) findViewById(R.id.id_loading_2);
        final LoadingCustomView04 id_loading_3 = (LoadingCustomView04) findViewById(R.id.id_loading_3);
        final LoadingCustomView04 id_loading_4 = (LoadingCustomView04) findViewById(R.id.id_loading_4);
        final LoadingCustomView04 id_loading_5 = (LoadingCustomView04) findViewById(R.id.id_loading_5);
        final LoadingCustomView04 id_loading_6 = (LoadingCustomView04) findViewById(R.id.id_loading_6);
        final LoadingCustomView04 id_loading_7 = (LoadingCustomView04) findViewById(R.id.id_loading_7);
        final LoadingCustomView05 id_loading_8 = (LoadingCustomView05) findViewById(R.id.id_loading_8);
        id_loading_7.setProgress(60);
        id_loading_7.setProgressColor(Color.parseColor("#BC6F99"));
        id_loading_7.setProgressBankgroundColor(Color.parseColor("#F55555"));
        id_loading_7.setProgressBarBankgroundStyle(LoadingCustomView04.HOLLOW);
        id_loading_7.setProgressBarFrameHeight(15);

        Button add = (Button) findViewById(R.id.id_add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = progress + 5;
                progress = progress > 100 ? 0 : progress;
                id_loading.setProgress(progress);
                id_loading_2.setProgress(progress);
                id_loading_3.setProgress(progress);
                id_loading_4.setProgress(progress);
                id_loading_5.setProgress(progress);
                id_loading_6.setProgress(progress);
                id_loading_7.setProgress(progress);
               // id_loading_8.setProgress(progress);
            }
        });*/
    }
}
