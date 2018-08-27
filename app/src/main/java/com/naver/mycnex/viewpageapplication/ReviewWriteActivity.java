package com.naver.mycnex.viewpageapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.mycnex.viewpageapplication.adapter.CommentListAdapter;
import com.naver.mycnex.viewpageapplication.data.Comment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ReviewWriteActivity extends AppCompatActivity {

    private Unbinder unbinder;
    //ArrayList<Comment> items = new ArrayList<>();
   // CommentListAdapter commentListAdapter;//
    @BindView(R.id.btnGoBack)ImageButton btnGoBack;
    @BindView(R.id.txt_rating)TextView txt_rating;
    @BindView(R.id.ratingBar)RatingBar ratingBar;
    @BindView(R.id.edit_write)EditText edit_write;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);
        unbinder = ButterKnife.bind(this);


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // get values and then displayed in a toast
               String totalStars = "Total Stars:: " + ratingBar.getNumStars();

                Toast.makeText(getApplicationContext(), totalStars + "\n" + rating, Toast.LENGTH_LONG).show();

            txt_rating.setText("" +rating);

            }
        });
    }
    @OnClick(R.id.btnGoBack)
    public void btnGoBack(){        // 뒤로가기
        finish();
    }
}
