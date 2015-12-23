package com.example.mikhail.stockstore.Classes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mikhail.stockstore.AsyncClasses.AsyncRequestToServer;
import com.example.mikhail.stockstore.CompanyInfoActivity;
import com.example.mikhail.stockstore.Entities.Stock;
import com.example.mikhail.stockstore.ProfileActivity;
import com.example.mikhail.stockstore.R;
import com.example.mikhail.stockstore.StockInfoActivity;
import com.example.mikhail.stockstore.StocksActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by mikhail on 02.12.15.
 */
public class StockCardAdapter extends RecyclerView.Adapter<StockCardAdapter.StocksViewHolder>{
    List<Stock> stocks;
    public StockCardAdapter(List<Stock> stocks){
        this.stocks = stocks;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public StocksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_card, parent, false);
        StocksViewHolder pvh = new StocksViewHolder(v);
        return pvh;
    }

    private void ellipsed(TextView tv, String text, float avail){
        TextPaint textpaint = tv.getPaint();
        tv.setText(text);
       // float avail = tv.getMeasuredWidth();
        String oneMore = "и ещё...";
        String more = "и ещё %d др.";
        CharSequence ellipsizedText = TextUtils.commaEllipsize(text, textpaint, avail,
                oneMore, more);
        tv.setText(ellipsizedText);
    }

    @Override
    public void onBindViewHolder(final StocksViewHolder holder, final int position) {
        holder.stockName.setText(stocks.get(position).name);
        holder.stockDescription.setText(stocks.get(position).description);

      //  CommonFunctions.setPhotoToImageView(stocks.get(position).photo, holder.stockPhoto);
        ImageLoader.getInstance().displayImage(stocks.get(position).photo, holder.stockPhoto);


        Locale ru = new Locale("ru");
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM y",ru);

        holder.stockDate.setText(format.format(stocks.get(position).dateFinish));
        holder.companyName.setText(stocks.get(position).company.name);

       // CommonFunctions.setPhotoToImageView(stocks.get(position).company.photo, holder.companyLogo);
        ImageLoader.getInstance().displayImage(stocks.get(position).company.photo, holder.companyLogo);

        //StocksViewHolder.isAdded = stocks.get(position).isAdded;
        if (stocks.get(position).isAdded){
            holder.addStockBtn.setBackgroundResource(R.drawable.added);
        }

       // ellipsed(holder.stockDescription, stocks.get(position).description, holder.stockPhoto.getWidth());
        //ellipsed(holder.stockName, stocks.get(position).name, holder.stockPhoto.getWidth());

        /*holder.stockName.setWidth(holder.stockPhoto.getWidth());
        holder.stockName.setLines(2);
        holder.stockDescription.setWidth(holder.stockPhoto.getWidth());
        holder.stockDescription.setLines(2);*/

        /*LinearLayout ll = (LinearLayout) holder.itemView;
        if (ll.getOrientation() == LinearLayout.HORIZONTAL){
            String name = stocks.get(position).name.length() <= 50 ?
                    stocks.get(position).name : stocks.get(position).name.substring(0,50) + "...";
            String description = stocks.get(position).description.length() <= 50 ?
                    stocks.get(position).description : stocks.get(position).description.substring(0,50) + "...";

            holder.stockName.setText(name);
            holder.stockDescription.setText(description);
        }*/

        holder.companyLogo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CompanyInfoActivity.class);
                intent.putExtra("company", stocks.get(position).company);
                v.getContext().startActivity(intent);
            }
        });

        holder.addStockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Toast.makeText(v.getContext().getApplicationContext(), stocks.get(position).id, Toast.LENGTH_SHORT).show();
                Activity host = (Activity) v.getContext();
                // APIRequestConstructor.userAddStock(host, stocks.get(position).id);

                holder.addStockBtn.setBackgroundResource(R.drawable.added);

                ResponseInterface handler;

                handler = new ResponseInterface() {
                    @Override
                    public void onInternalServerError(JSONObject response) {

                    }

                    @Override
                    public void onUnknownRequestUri(JSONObject response) {
                        try {
                            Toast.makeText(v.getContext().getApplicationContext(), response.getString("data"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(JSONObject response) {
                        try {
                            Toast.makeText(v.getContext().getApplicationContext(), response.getString("data"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onRegister(JSONObject response) {

                    }

                    @Override
                    public void onGetToken(JSONObject response) {

                    }

                    @Override
                    public void onUserGetAllStocks(JSONObject response) {

                    }

                    @Override
                    public void onUserGetAllCompanies(JSONObject response) {

                    }

                    @Override
                    public void onUserAddStock(JSONObject response) {
                        try {
                            Toast.makeText(v.getContext().getApplicationContext(), response.getString("name") + " добавлена на стену", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onUserGetFeed(JSONObject response) {

                    }
                };

                try {

                    AsyncRequestToServer request = new AsyncRequestToServer(host, handler);
                    request.setParameters(APIRequestConstructor.userAddStockParameters(host, stocks.get(position).id));
                    request.execute(APIConstants.USER_ADD_STOCK);

                    //ServerResponseHandler.CheckResponse(APIRequestConstructor.userAddStock(host, stocks.get(position).id), handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(v.getContext().getApplicationContext(), "Clicked!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(holder.itemView.getContext(), StockInfoActivity.class);
                intent.putExtra("stock", (Parcelable) stocks.get(position));
                holder.itemView.getContext().startActivity(intent);

               // holder.itemView.getContext().startActivity(intent.putExtras(b));
            }
        });
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    public static class StocksViewHolder extends RecyclerView.ViewHolder {
        public CardView cv;
        public TextView stockName;
        public TextView stockDescription;
        public ImageView stockPhoto;

        public TextView companyName;
        public ImageView companyLogo;
        public TextView stockDate;
        public boolean isAdded;

        public ImageButton addStockBtn;

        StocksViewHolder(final View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card);

            stockName = (TextView)itemView.findViewById(R.id.stock_name);
            stockDescription = (TextView)itemView.findViewById(R.id.stock_description);
            stockPhoto = (ImageView)itemView.findViewById(R.id.stock_photo);

            companyName = (TextView)itemView.findViewById(R.id.company_name);
            companyLogo = (ImageView)itemView.findViewById(R.id.company_logo);
            stockDate = (TextView)itemView.findViewById(R.id.stock_date);

            addStockBtn = (ImageButton)itemView.findViewById(R.id.add_stock_btn);

            isAdded = false;
        }
    }
}
