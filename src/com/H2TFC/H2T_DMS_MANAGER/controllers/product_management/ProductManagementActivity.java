package com.H2TFC.H2T_DMS_MANAGER.controllers.product_management;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.H2TFC.H2T_DMS_MANAGER.MyApplication;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.H2TFC.H2T_DMS_MANAGER.adapters.EmployeeListAdapter;
import com.H2TFC.H2T_DMS_MANAGER.adapters.ProductListAdapter;
import com.H2TFC.H2T_DMS_MANAGER.models.Product;
import com.H2TFC.H2T_DMS_MANAGER.utils.ConnectUtils;
import com.H2TFC.H2T_DMS_MANAGER.utils.DownloadUtils;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.daimajia.swipe.SwipeLayout;
import com.parse.*;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
public class ProductManagementActivity extends Activity {
    ProductListAdapter productListAdapter;

    ListView lvProduct;
    TextView tvEmptyProduct;

    EditText editTextSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.productManagementTitle));

        if (ConnectUtils.hasConnectToInternet(ProductManagementActivity.this)) {
            DownloadUtils.DownloadParseProduct(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    tvEmptyProduct.setVisibility(View.VISIBLE);
                    productListAdapter.loadObjects();
                }
            });
        }

        ParseQueryAdapter.QueryFactory<Product> factory = new ParseQueryAdapter.QueryFactory<Product>() {
            @Override
            public ParseQuery<Product> create() {
                ParseQuery<Product> query = Product.getQuery();
                query.whereNotEqualTo("status",Product.ProductStatus.KHOA.name());
                query.orderByDescending("createdAt");
                query.fromPin(DownloadUtils.PIN_PRODUCT);
                return query;
            }
        };



        productListAdapter = new ProductListAdapter(this, factory);

        lvProduct = (ListView) findViewById(R.id.activity_product_management_listview);
        tvEmptyProduct = (TextView) findViewById(R.id.activity_product_management_tv_empty_product);
        tvEmptyProduct.setVisibility(View.INVISIBLE);
        lvProduct.setEmptyView(tvEmptyProduct);
        lvProduct.setAdapter(productListAdapter);
        editTextSearch = (EditText) findViewById(R.id.activity_product_management_search);


        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editTextSearch.getText().toString().length() != 0) {
                    ParseQueryAdapter.QueryFactory<Product> factory = new ParseQueryAdapter.QueryFactory<Product>() {
                        @Override
                        public ParseQuery<Product> create() {
                            ParseQuery<Product> query = Product.getQuery();
                            query.whereNotEqualTo("status", Product.ProductStatus.KHOA.name());
                            query.whereContains("name", editTextSearch.getText().toString());
                            query.orderByDescending("createdAt");
                            query.fromPin(DownloadUtils.PIN_PRODUCT);
                            return query;
                        }
                    };
                    productListAdapter = new ProductListAdapter(ProductManagementActivity.this,factory);
                    lvProduct.setAdapter(productListAdapter);
                    productListAdapter.notifyDataSetChanged();
                } else {
                    ParseQueryAdapter.QueryFactory<Product> factory = new ParseQueryAdapter.QueryFactory<Product>() {
                        @Override
                        public ParseQuery<Product> create() {
                            ParseQuery<Product> query = Product.getQuery();
                            query.whereNotEqualTo("status", Product.ProductStatus.KHOA.name());
                            query.orderByDescending("createdAt");
                            query.fromPin(DownloadUtils.PIN_PRODUCT);
                            return query;
                        }
                    };
                    productListAdapter = new ProductListAdapter(ProductManagementActivity.this,factory);
                    lvProduct.setAdapter(productListAdapter);
                    productListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
                case android.R.id.home: {
                    finish();
                    break;
                }
            case R.id.actionbar_product_add: {
                Intent intent = new Intent(ProductManagementActivity.this, ProductNewActivity.class);
                startActivityForResult(intent, MyApplication.REQUEST_ADD_NEW);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.acttion_bar_product_management, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        productListAdapter.loadObjects();
        productListAdapter.notifyDataSetChanged();
    }
}