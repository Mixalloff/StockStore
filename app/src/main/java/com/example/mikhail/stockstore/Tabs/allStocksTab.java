package com.example.mikhail.stockstore.Tabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mikhail.stockstore.AsyncClasses.AsyncGetPhoto;
import com.example.mikhail.stockstore.AsyncClasses.AsyncRequestToServer;
import com.example.mikhail.stockstore.Classes.APIConstants;
import com.example.mikhail.stockstore.Classes.APIRequestConstructor;
import com.example.mikhail.stockstore.Classes.ResponseInterface;
import com.example.mikhail.stockstore.Classes.ServerResponseHandler;
import com.example.mikhail.stockstore.Classes.StockCardAdapter;
import com.example.mikhail.stockstore.Classes.WorkWithServer;
import com.example.mikhail.stockstore.Entities.Company;
import com.example.mikhail.stockstore.Entities.Stock;
import com.example.mikhail.stockstore.R;
import com.example.mikhail.stockstore.StartActivity;
import com.example.mikhail.stockstore.StocksActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikhail on 09.12.15.
 */
public class allStocksTab extends Fragment {
    StockCardAdapter adapter;
    private List<Stock> stocks = new ArrayList<>();
    RecyclerView rv;
    int countOfLoadingStocks = 5;
    AsyncRequestToServer request;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

// Тестовые карточки
        initializeTestData();
        request = new AsyncRequestToServer(getActivity(), handler);
        request.execute(APIConstants.GET_ALL_STOCKS);

        Toast.makeText(getContext().getApplicationContext(), "Акции onCreate", Toast.LENGTH_SHORT).show();
    }

    /*public void onPause(){
        super.onPause();
        request.cancel(true);
        Toast.makeText(getContext().getApplicationContext(), "Акции pause", Toast.LENGTH_SHORT).show();
    }

    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getContext().getApplicationContext(), "Акции destroy", Toast.LENGTH_SHORT).show();
    }

    public void onResume() {
        super.onResume();
        Toast.makeText(getContext().getApplicationContext(), "Акции onResume", Toast.LENGTH_SHORT).show();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Toast.makeText(getContext().getApplicationContext(), "Акции saveInstanse", Toast.LENGTH_SHORT).show();
    }

    public void onStart() {
        super.onStart();
        Toast.makeText(getContext().getApplicationContext(), "Акции onStart", Toast.LENGTH_SHORT).show();
    }

    public void onStop() {
        super.onStop();
        Toast.makeText(getContext().getApplicationContext(), "Акции onStop", Toast.LENGTH_SHORT).show();
    }*/

    /*public void onRestart() {
        super.onRestart();
        Toast.makeText(getContext().getApplicationContext(), "Акции restart", Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.all_stocks_tab, container, false);

        // Подгружаем токен, если есть
        String token = WorkWithServer.getToken(getActivity());
        rv = (RecyclerView) v.findViewById(R.id.cards);
        initRecyclerView(container);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        rv.setItemAnimator(itemAnimator);

        return v;
    }

    // Инициализация RecyclerView карточек
    public void initRecyclerView(ViewGroup container){
        Context context = this.getActivity();
        LinearLayoutManager llm = new LinearLayoutManager(context);

        rv.setLayoutManager(llm);
        adapter = new StockCardAdapter(stocks);
        rv.setAdapter(adapter);
    }

    private void initializeTestData() {
        String testPhoto =  "http://sportmax-abakan.ru/upload/medialibrary/9bf/eskiz.png";
        String testCompanyPhoto = "https://img.grouponcdn.com/coupons/gMH7PGJwA4KdS3teZNvpXD/nike-highres-500x500";
        stocks.add(new Stock("1", "Наушники Nike БЕСПЛАТНО", "С 1 сентября 2015 года при единовременной покупке товаров-участников акции в магазине \"СпортМакс\" по адресу г.Абакан ул.Стофато 5д, на сумму 1500 (одна тысяча пятьсот) рублей, Покупатель БЕСПЛАТНО получает наушники Nike.", testPhoto, new Company("NIKE", testCompanyPhoto), true));
    }

    private ResponseInterface handler = new ResponseInterface() {
        @Override
        public void onInternalServerError(JSONObject response) {

        }

        @Override
        public void onUnknownRequestUri(JSONObject response) {

        }

        @Override
        public void onError(JSONObject response) {
            startActivity(new Intent(getContext(),StartActivity.class));
        }

        @Override
        public void onRegister(JSONObject response) {

        }

        @Override
        public void onGetToken(JSONObject response) {

        }

        @Override
        public void onUserGetAllStocks(JSONObject response) {
            try {
                // Обновляем список акций
                stocks.clear();
               // adapter.notifyItemRemoved(0);

                JSONArray data = new JSONArray(response.get("data").toString());
                int count = data.length() > countOfLoadingStocks ? countOfLoadingStocks : data.length();
                for (int i = 0; i < count; i++){
                    JSONObject stock = new JSONObject(data.get(i).toString());
                    stocks.add(new Stock(stock));
                   // adapter.notifyItemInserted(i);
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUserGetAllCompanies(JSONObject response) {

        }

        @Override
        public void onUserAddStock(JSONObject response) {

        }

        @Override
        public void onUserGetFeed(JSONObject response) {

        }
    };

}


