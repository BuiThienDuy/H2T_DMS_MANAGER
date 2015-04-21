package com.H2TFC.H2T_DMS_MANAGER.controllers.promotion_management;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.H2TFC.H2T_DMS_MANAGER.MyApplication;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.adapters.ProductListAdapter;
import com.H2TFC.H2T_DMS_MANAGER.adapters.PromotionListAdapter;
import com.H2TFC.H2T_DMS_MANAGER.models.Product;
import com.H2TFC.H2T_DMS_MANAGER.models.Promotion;
import com.H2TFC.H2T_DMS_MANAGER.utils.ConnectUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.parse.*;

import java.util.List;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class PromotionManagementActivity extends Activity {
    EditText etSearch;
    ListView lvPromotion;
    TextView tvEmpty;
    ProgressBar progressBar;

    PromotionListAdapter promotionAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_management);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.promotionManagemenTitle));

        if(ConnectUtils.hasConnectToInternet(PromotionManagementActivity.this)) {
            DownloadUtils.DownloadParsePromotion(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    promotionAdapter.loadObjects();
                }
            });
        }

        InitializeComponent();
        SetupListView();
        SetupEvent();
    }

    public void InitializeComponent(){
        lvPromotion = (ListView) findViewById(R.id.activity_promotion_management_listview);
        tvEmpty = (TextView) findViewById(R.id.activity_promotion_management_tv_empty);
        progressBar = (ProgressBar) findViewById(R.id.activity_promotion_management_progressbar);

        etSearch = (EditText) findViewById(R.id.activity_promotion_management_search);
    }

    public void SetupListView() {
        // Query data from local data store
        ParseQueryAdapter.QueryFactory<Promotion> factory = new ParseQueryAdapter.QueryFactory<Promotion>() {
            @Override
            public ParseQuery<Promotion> create() {
                ParseQuery<Promotion> query = Promotion.getQuery();
                query.orderByDescending("createdAt");
                query.fromPin(DownloadUtils.PIN_PROMOTION);
                return query;
            }
        };

        lvPromotion.setEmptyView(tvEmpty);

        // Set list adapter
        promotionAdapter = new PromotionListAdapter(this, factory);
        lvPromotion.setEmptyView(tvEmpty);
        lvPromotion.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        promotionAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Promotion>() {
            @Override
            public void onLoading() {
                tvEmpty.setText("");
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaded(List<Promotion> list, Exception e) {
                if (list.size() == 0) {
                    tvEmpty.setText(getString(R.string.emptyPromotionList));
                }
                progressBar.setVisibility(View.GONE);

            }
        });
        lvPromotion.setAdapter(promotionAdapter);
    }

    public void SetupEvent() {
        lvPromotion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position >=0 ) {

                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etSearch.getText().toString().length() != 0) {
                    ParseQueryAdapter.QueryFactory<Promotion> factory = new ParseQueryAdapter.QueryFactory<Promotion>() {
                        @Override
                        public ParseQuery<Promotion> create() {
                            ParseQuery<Promotion> query = Promotion.getQuery();
                            query.whereContains("promotion_name", etSearch.getText().toString());
                            query.orderByDescending("createdAt");
                            query.fromPin(DownloadUtils.PIN_PROMOTION);
                            return query;
                        }
                    };
                    promotionAdapter = new PromotionListAdapter(PromotionManagementActivity.this,factory);
                    lvPromotion.setAdapter(promotionAdapter);
                    promotionAdapter.notifyDataSetChanged();
                } else {
                    ParseQueryAdapter.QueryFactory<Promotion> factory = new ParseQueryAdapter.QueryFactory<Promotion>() {
                        @Override
                        public ParseQuery<Promotion> create() {
                            ParseQuery<Promotion> query = Promotion.getQuery();
                            query.orderByDescending("createdAt");
                            query.fromPin(DownloadUtils.PIN_PROMOTION);
                            return query;
                        }
                    };
                    promotionAdapter = new PromotionListAdapter(PromotionManagementActivity.this,factory);
                    lvPromotion.setAdapter(promotionAdapter);
                    promotionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_promotion_management,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_promotion_management_new: {
                Intent intent = new Intent(PromotionManagementActivity.this,PromotionNewActivity.class);
                startActivityForResult(intent, MyApplication.REQUEST_ADD_NEW);
                break;
            }

            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        promotionAdapter.notifyDataSetChanged();
        promotionAdapter.loadObjects();

        lvPromotion.invalidateViews();
        super.onActivityResult(requestCode, resultCode, data);
    }
}