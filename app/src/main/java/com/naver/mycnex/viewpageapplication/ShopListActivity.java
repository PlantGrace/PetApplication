package com.naver.mycnex.viewpageapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.naver.mycnex.viewpageapplication.adapter.ShopListActListAdapter;
import com.naver.mycnex.viewpageapplication.data.Store;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;


public class ShopListActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.btnAdd) Button btnAdd;
    @BindView(R.id.btnGoBack) ImageButton btnGoBack;
    @BindView(R.id.listView) ListView listView;

    // 리스트뷰 관련
    ArrayList<Store> storeList = new ArrayList<>();
    ShopListActListAdapter shopListActListAdapter;

    /** OnCreate **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        //버터나이프
        unbinder = ButterKnife.bind(this);

        shopListActListAdapter = new ShopListActListAdapter(storeList);
        listView.setAdapter(shopListActListAdapter);
    }

    /********** OnClick **********/
    @OnClick(R.id.btnAdd)
    public void btnAdd(){
        Intent intent = new Intent(ShopListActivity.this, RegisterShopActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.btnGoBack)
    public void btnGoBack(){
        finish();
    }

    /** OnDestroy **/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //버터나이프
        unbinder.unbind();
    }

    /********** OnItemClick **********/
    @OnItemClick(R.id.listView)
    public void listView(int position){
        // TODO :
        // 클릭한 요소의 객체정보를 담은 ShopActivity 를 실행해야 한다.
        Intent intent = new Intent(ShopListActivity.this,ShopActivity.class);
        startActivity(intent);
    }
}
