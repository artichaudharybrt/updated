package com.gamegards.bigjackpot.Interface;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.Utils.Functions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiRequest {


    public static void Call_Api (final Context context, final String url, final HashMap mparams,
                                 final Callback callback){


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url ,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();

                        Functions.LOGE("API",""+url+"\n"+" \n"+mparams+"\n"+response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(callback!=null)
                                callback.Responce(response.toString(),"",null);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  progressDialog.dismiss();

                Functions.LOGE("API",""+url+"\n"+" \n"+mparams+"\n"+error.toString());

                if(callback!=null)
                    callback .Responce(error.toString(),"",null);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params = mparams;

//                Log.e("APIRequest",""+url+" \n"+mparams);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Const.TOKEN);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

//        Volley.newRequestQueue(this).add(stringRequest);

    }


}
