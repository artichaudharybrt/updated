package com.gamegards.bigjackpot.Activity;

import static com.gamegards.bigjackpot.Activity.Homepage.MY_PREFS_NAME;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamegards.bigjackpot.Adapter.NotificationAdapter;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.Utils.Functions;
import com.gamegards.bigjackpot.model.NotificationItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    TextView txtheader;
    ProgressDialog progressDialog;
    ImageView imgclosetop;

    RecyclerView rv_notiList;

    ArrayList<NotificationItem> notificationItemArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        progressDialog = new ProgressDialog(NotificationActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        txtheader = findViewById(R.id.txtheader);
        imgclosetop = findViewById(R.id.imgclosetop);
        rv_notiList = findViewById(R.id.rv_notiList);
        txtheader.setText("NOTIFICATIONS");


        rv_notiList.setLayoutManager(new LinearLayoutManager(this));


        imgclosetop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getNotificationList();

    }

    private void getNotificationList() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.USER_NOTI_LIST,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();
                        Log.d("DATA_CHECK_LIST", "onResponse: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {

                                progressDialog.dismiss();
                                notificationItemArrayList = new ArrayList<>();
                                JSONArray jsonArray = jsonObject.getJSONArray("notification");
                                Log.d("DATA_CHECK_ARRYA", "onResponse: " + jsonArray);
                                Log.d("NOTIFICATION_FILTER", "Total notifications received: " + jsonArray.length());

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    NotificationItem notificationItem = new NotificationItem();

                                    notificationItem.id = jsonObject1.getString("id");
                                    notificationItem.msg = jsonObject1.getString("msg");
                                    notificationItem.added_date = jsonObject1.getString("added_date");
                                    notificationItem.updated_date = jsonObject1.getString("updated_date");

                                    // Parse isDeleted field - default to "0" if not present
                                    notificationItem.isDeleted = jsonObject1.optString("isDeleted", "0");

                                    // Only add notification if it's not deleted (isDeleted != "1")
                                    if (!notificationItem.isDeleted.equals("1")) {
                                        notificationItemArrayList.add(notificationItem);
                                        Log.d("NOTIFICATION_FILTER", "Added notification ID: " + notificationItem.id + ", isDeleted: " + notificationItem.isDeleted);
                                    } else {
                                        Log.d("NOTIFICATION_FILTER", "Filtered out deleted notification ID: " + notificationItem.id + ", isDeleted: " + notificationItem.isDeleted);
                                    }
                                }

                                Log.d("NOTIFICATION_FILTER", "Filtered notifications count: " + notificationItemArrayList.size() + " (showing only non-deleted notifications)");
                                rv_notiList.setAdapter(new NotificationAdapter(NotificationActivity.this, notificationItemArrayList));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  progressDialog.dismiss();
                Functions.showToast(NotificationActivity.this, "Something went wrong");
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences prefs = NotificationActivity.this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                params.put("user_id", prefs.getString("user_id", ""));
                params.put("token", prefs.getString("token", ""));
                Log.d("paremter_java_with_list", "getParams: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Const.TOKEN);
                return headers;
            }
        };

        Volley.newRequestQueue(NotificationActivity.this).add(stringRequest);
    }

}