package com.example.mikhail.stockstore.AsyncClasses;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.example.mikhail.stockstore.Constants.APIConstants;
import com.example.mikhail.stockstore.Classes.ErrorsWorker;
import com.example.mikhail.stockstore.Classes.GlobalVariables;
import com.example.mikhail.stockstore.Classes.ServerResponseHandler;
import com.example.mikhail.stockstore.Classes.WorkWithResources;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mikhail on 19.12.15.
 */
public class AsyncRequestToServer extends AsyncTask<String, Integer, JSONObject>
{
    SwipeRefreshLayout swipe = null;

    // Callback
    private OnTaskCompleted callback;
    private OnTaskCompleted errorCallback;

    public void setCallback(OnTaskCompleted callback){
        this.callback = callback;
    }

    public void setErrorCallback(OnTaskCompleted errorCallback){
        this.errorCallback = errorCallback;
    }

    // Список POST-запросов
    final List<String> POST_Request = Arrays.asList(
            APIConstants.USER_AUTH,
            APIConstants.USER_REGISTER,
            APIConstants.USER_SUBSCRIBE_STOCK,
            APIConstants.USER_UNSUBSCRIBE_STOCK,
            APIConstants.USER_ADD_FRIEND,
            APIConstants.USER_DELETE_FRIEND,
            APIConstants.USER_SUBSCRIBE_COMPANY,
            APIConstants.USER_UNSUBSCRIBE_COMPANY,
            APIConstants.USER_SUBSCRIBE_CATEGORY,
            APIConstants.USER_UNSUBSCRIBE_CATEGORY
    );

    // Список GET-запросов
    final List<String> GET_Request = Arrays.asList(
            APIConstants.GET_ALL_STOCKS,
            APIConstants.GET_ALL_COMPANIES,
            APIConstants.GET_ALL_CATEGORIES,
            APIConstants.USER_GET_FEED,
            APIConstants.USER_GET_STOCKS_INFO,
            APIConstants.USER_GET_STOCKS_BY_COMPANY,
            APIConstants.USER_GET_STOCKS_BY_WORDPATH,
            APIConstants.USER_GET_STOCKS_BY_FILTER,
            APIConstants.USER_GET_ALL_FRIENDS,
            APIConstants.USER_GET_FRIENDS_FEED,
            APIConstants.USER_GET_FRIENDS_FILTER,
            APIConstants.USER_GET_SUBSCRIPTIONS_STOCKS,
            APIConstants.GET_SUBSCRIBED_COMPANIES,
            APIConstants.GET_SUBSCRIBED_CATEGORIES
    );

    // Параметры запроса
    private String urlParams = "";
    // Активити в которой создан объект
    Activity activity;

    public void setSwipeRefresh(SwipeRefreshLayout swipe){
        this.swipe = swipe;
    }

    public AsyncRequestToServer(Activity activity){
        this(activity, null);
    }

    public AsyncRequestToServer(Activity activity, OnTaskCompleted callback){
        this(activity, callback, null);
    }

    public AsyncRequestToServer(Activity activity, OnTaskCompleted callback, OnTaskCompleted errorCallback){
        this.activity = activity;
        this.callback = callback;
        this.errorCallback = errorCallback;
    }

    // Установка параметров с подпиской токеном
    public void setParameters(String urlParams){
       String token = WorkWithResources.getToken(activity);
       this.urlParams = token != null ? "token=" + token + "&" + urlParams : urlParams;
    }

    // Установка токена в параметры
    public void setParameters(){
        setParameters("");
    }


    public void setActivity(Activity activity){
        this.activity = activity;
    }

    // Метод выполняется перед началом doInBackground()
    protected void onPreExecute(){

    }

    // Проверяет, есть ли соединение
    public static boolean hasConnection(final Context context)
    {
        try {
            ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return (connectManager.getNetworkInfo(
                    ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
           // return internetAvailable;
        }catch(Exception e){
            return false;
        }
    }

    // Отправляет GET запрос
    private String sendGetRequest(String targetURL) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        int code = 0;

        try {

            //String encodedUrl = URLEncoder.encode(targetURL, "UTF-8");
            // String utf8String= new String(targetURL.getBytes("Unicode"), "UTF-8");

            url = new URL(GlobalVariables.server + "/" + targetURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            code = conn.getResponseCode();

            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorsWorker.ErrorObject(code).toString();
        }
        return result;
    }

        // Отправляет POST запрос
    private String sendPostRequest(String targetURL) {
        HttpURLConnection connection = null;
        int code = 0;

        try{
            URL url = new URL(GlobalVariables.server + "/" + targetURL);


            connection = (HttpURLConnection)url.openConnection();
           // code = connection.getResponseCode();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(urlParams.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            // Request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParams);
            wr.close();

            // Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while((line = rd.readLine()) != null){
                response.append(line);
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return ErrorsWorker.ErrorObject(connection.getResponseCode()).toString();
            } catch (IOException e1) {
                e1.printStackTrace();
                return ErrorsWorker.ErrorObject(code).toString();
            }
        } finally {
            if(connection != null){
                connection.disconnect();
            }
        }
    }

    // Здесь делайте всю длительную по времени работу.
    protected JSONObject doInBackground(String... targetURL)
    {
        if (AsyncRequestToServer.hasConnection(activity)) {
            for (String url : targetURL) {
                try {
                    if(urlParams.equals("")){
                        setParameters();
                    }

                    if(POST_Request.contains(url)){
                        return new JSONObject(sendPostRequest(url));
                    }
                    else if (GET_Request.contains(url)){
                        return new JSONObject(sendGetRequest(url+ "?" + urlParams));
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }

                // Ранний выход, если был вызван cancel().
                if (isCancelled())
                    break;
            }
        }
        return null;
    }

    // Этот метод будет вызван каждый раз, когда в потоке
    // выполнится publishProgress().
    protected void onProgressUpdate(Integer... progress)
    {
        //setProgressPercent(progress[0]);
    }

    // Этот метод будет вызван, когда doInBackground() завершится
    protected void onPostExecute(JSONObject result)
    {
        try {
            // Окончание свайпа
            if (swipe != null) {
                swipe.setRefreshing(false);
            }
            // Если ошибка показываем ее
            if (result.has("error")) {
                if(errorCallback != null){
                    errorCallback.onTaskCompleted(result);
                }
                else {
                    showError(result.get("type").toString());
                }
            }// Иначе вызов колбэка
            else if (callback != null) {
                callback.onTaskCompleted(result);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Показать ошибку
    public void showError(String errType){
        String result;
        switch (errType){
            case "400": {
                result = "Ошибка: " + errType;
                break;
            }
            case "403": {
                result = "Ошибка: " + errType;
                break;
            }
            case "404": {
                result = "Ошибка: " + errType;
                break;
            }
            case "500": {
                result = "Ошибка: " + errType;
                break;
            }
            default: {
                result = "Ошибка: " + errType;
                break;
            }
        }
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
    }
}