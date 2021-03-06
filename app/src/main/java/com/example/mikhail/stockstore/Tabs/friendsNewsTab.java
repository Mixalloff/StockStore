package com.example.mikhail.stockstore.Tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mikhail.stockstore.Adapters.StockCardAdapter;
import com.example.mikhail.stockstore.AsyncClasses.AsyncRequestToServer;
import com.example.mikhail.stockstore.AsyncClasses.OnTaskCompleted;
import com.example.mikhail.stockstore.Constants.APIConstants;
import com.example.mikhail.stockstore.Classes.ServerResponseHandler;
import com.example.mikhail.stockstore.Entities.Stock;
import com.example.mikhail.stockstore.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikhail on 17.01.16.
 */
public class friendsNewsTab extends Fragment {
    StockCardAdapter adapter;
    private List<Stock> stocks = new ArrayList<>();
    RecyclerView rv;
    int countOfLoadingStocks = 5;
    //AsyncRequestToServer request;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
// Тестовые карточки
        // initializeTestData();

       /* request = new AsyncRequestToServer(getActivity(), handler);
        request.setSwipeRefresh(this.getView());
        request.setSpinnerMessage("Загрузка акций");
        request.execute(APIConstants.GET_ALL_STOCKS);*/

        //Toast.makeText(getContext().getApplicationContext(), "Акции onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.all_stocks_tab, container, false);

        // Подгружаем токен, если есть
        // String token = WorkWithServer.getToken(getActivity());


        rv = (RecyclerView) v.findViewById(R.id.cards);
        initRecyclerView(container);
        initSwipe(v);

        //RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        //rv.setItemAnimator(itemAnimator);

        //initializeTestData(v);

        /*try{
            Intent intent = getActivity().getIntent();
            JSONObject stocksJson = new JSONObject(intent.getStringExtra("stocks"));
            handler.onUserGetAllStocks(stocksJson);
        }catch(Exception e){
            e.printStackTrace();
            request = new AsyncRequestToServer(getActivity(), handler);
            request.execute(APIConstants.USER_GET_FRIENDS_FEED);
        }*/

        AsyncRequestToServer request = new AsyncRequestToServer(getActivity(),  new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JSONObject result) {
                handler.onUserGetFriendsFeed(result);
            }
        });
        request.execute(APIConstants.USER_GET_FRIENDS_FEED);

        return v;
    }


    // Инициализация SwipeLayout
    public void initSwipe(View v){
        // Обновление при скролле вниз
        final SwipeRefreshLayout swipe = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
        swipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        request = new AsyncRequestToServer(getActivity(), handler);
                        request.setSwipeRefresh(swipe);
                        request.setSpinnerMessage("Загрузка акций");
                        request.execute(APIConstants.GET_ALL_STOCKS);
                    }
                },0);*/

                AsyncRequestToServer request = new AsyncRequestToServer(getActivity(),  new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(JSONObject result) {
                        handler.onUserGetAllFriends(result);
                    }
                });
                request.setSwipeRefresh(swipe);
                request.execute(APIConstants.USER_GET_ALL_FRIENDS);
            }
        });
    }

    // Инициализация RecyclerView карточек
    public void initRecyclerView(ViewGroup container){
        Context context = this.getActivity();
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        rv.setLayoutManager(llm);
        adapter = new StockCardAdapter(stocks, getActivity());
        rv.setAdapter(adapter);
    }

    private ServerResponseHandler handler = new ServerResponseHandler() {
        @Override
        public void onUserGetFriendsFeed(JSONObject response) {
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
    };
}
