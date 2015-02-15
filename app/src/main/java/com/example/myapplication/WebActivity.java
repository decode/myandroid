package com.example.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class WebActivity extends Activity {

    private TextView text_weather;
    private ImageView image_verify;
    private EditText edit_username, edit_password, edit_verify;
    Bitmap bitmap;
    ProgressDialog pDialog;
    private AsyncHttpClient client;

    String success_url = "http://www.gxfwpt.com/Backend/CompanyData/Index/index.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        processView();
        processController();
    }

    private void processView() {
        text_weather = (TextView) findViewById(R.id.txt_weather);
        image_verify = (ImageView) this.findViewById(R.id.img_verify);
        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_verify = (EditText) findViewById(R.id.edit_verify);

        client = new AsyncHttpClient();
        PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        String verify = "http://www.gxfwpt.com/Backend/Base/Public/verify.html";
        client.get(verify, new FileAsyncHttpResponseHandler(this) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                // Do something with the file `response`
                Bitmap bitmap = BitmapFactory.decodeFile(response.getPath());
                image_verify.setImageBitmap(bitmap);
            }
        });
    }

    private void processController() {

        String url = "https://weather.yahoo.com/china/guangxi/guilin-2166474/";
//        new LoadImage().execute(verify);
    }

    public void clickLogin(View view) {

        String login_url = "http://www.gxfwpt.com/Backend/Base/Public/Login.html";

        RequestParams params = new RequestParams();
        params.put("username", edit_username.getText());
        params.put("password", edit_password.getText());
        params.put("verify", edit_verify.getText());

        client.post(login_url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                text_weather.setText("Login...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                try {
                    String result = new String(response, "UTF-8");
                    Log.d("HTML Result", result);
                    text_weather.setText(result);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WebActivity.this);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();
        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap image) {
            if(image != null){
                image_verify.setImageBitmap(image);
                pDialog.dismiss();
            }else{
                pDialog.dismiss();
                Toast.makeText(WebActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
