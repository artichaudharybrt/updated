package com.gamegards.bigjackpot.account;

import com.gamegards.bigjackpot.Activity.Homepage;
import com.gamegards.bigjackpot.BaseActivity;
import com.gamegards.bigjackpot.WalletApplication;

import android.util.Log;
import android.content.SharedPreferences;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gamegards.bigjackpot.Interface.ApiRequest;
import com.gamegards.bigjackpot.Interface.Callback;
import com.gamegards.bigjackpot.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamegards.bigjackpot.ApiClasses.CommonFunctions;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.Utils.Functions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register_Activity extends BaseActivity {
    private static final String MY_PREFS_NAME = "Login_data";
    EditText edtPhone, edtname, edtReferalCode, edtEmail;
    EditText edtPassword, edit_phone_edit;
    TextView tv, tv_edit_no;
    int pStatus = 0;
    private Handler handler = new Handler();
    TextView imglogin;
    AlertDialog dialog;
    EditText edit_OTP;
    String verificationID, str_chk = "";

    RadioGroup radioGroup;
    boolean isSelected = true;
    RadioButton genderradioButton;
    ImageView imgBackground, imgBackgroundlogin;
    Activity context = this;
    public BottomSheetBehavior sheetBehavior;
    public View bottom_sheet;

    // Google Sign-In
    private GoogleSignInClient mGoogleSignInClient;


    private final String LOGIN = "login";
    private final String REGISTER = "register";
    private Bitmap panBitmap;
    LinearLayout lnrThirdScreen;
    LinearLayout lnrSeconScreen;
    LinearLayout lnrFirstScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_register);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtname = findViewById(R.id.edtname);
        edtEmail = findViewById(R.id.edtEmail);
        edtReferalCode = findViewById(R.id.edtReferalCode);
        imglogin = findViewById(R.id.imglogin);

        lnrFirstScreen = findViewById(R.id.lnrFirstScreen);
        lnrSeconScreen = findViewById(R.id.lnrSeconScreen);
        lnrThirdScreen = findViewById(R.id.lnrThirdScreen);

        // Initialize Google Sign-In
        // initializeGoogleSignIn(); // Commented out as requested

        // No need to retrieve wallet address - using global variable from WalletApplication

        imglogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CommonFunctions.isNetworkConnected(context)) {

                    if (edtname.getText().toString().equals("")) {
                        Toast.makeText(context, "Please enter your Name", Toast.LENGTH_SHORT).show();
                    } else if (edtEmail.getText().toString().equals("")) {
                        Toast.makeText(context, "Please enter your Email", Toast.LENGTH_SHORT).show();
                    } else if (!CommonFunctions.validate(edtEmail.getText().toString())) {
                        Toast.makeText(context, "Please enter a valid Email", Toast.LENGTH_SHORT).show();
                    } else if (edtPhone.getText().toString().equals("")) {
                        Toast.makeText(context, "Please enter your Phone", Toast.LENGTH_SHORT).show();
                    } else if (edtPassword.getText().toString().equals("")) {
                        Toast.makeText(context, "Please enter your Password", Toast.LENGTH_SHORT).show();
                    } else if (getCurrentWalletAddress().isEmpty()) {
                        Toast.makeText(context, "Please connect your BND wallet first from the wallet screen", Toast.LENGTH_LONG).show();
                    } else {
                        if (isSelected) {
                            RadioButton rb = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());

                            if (rb != null) {
                                // login(rb.getText() + "");
                                VerifyCode("", "", rb.getText() + "");
                            } else {
                                //  login("male");
                                VerifyCode("", "", "male");
                            }

                        } else {
                            Functions.showToast(context, "Please select Gender first ?");

                        }


                    }
//                    else{
//                        CommonFunctions.showNoInternetDialog(context);
//                    }
                }


            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    isSelected = true;
                }

            }
        });

    }

    private void login(final String value) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging in..");
        progressDialog.show();
        Functions.setDialogParams(dialog);


        HashMap params = new HashMap<String, String>();
        params.put("mobile", edtPhone.getText().toString());
        params.put("type", REGISTER);
        ApiRequest.Call_Api(context, Const.SEND_OTP, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                progressDialog.dismiss();
                handleResponse(resp, value);
            }

        });

    }

    private void handleResponse(String response, String value) {

        try {
            JSONObject jsonObject = new JSONObject(response);

            String code = jsonObject.getString("code");


            if (code.equalsIgnoreCase("200")) {

                String otp_id = jsonObject.getString("otp_id");
                phoneLogin(otp_id, value);

            } else {

                if (jsonObject.has("message")) {
                    String message = jsonObject.getString("message");
                    Functions.showToast(context, message);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void phoneLogin(final String otp_id, final String value) {
        // String phoneNumber= "+91"+edtPhone.getText().toString().trim();
        //SendVerificationCode(phoneNumber);
        final Dialog dialog = Functions.DialogInstance(context);
        dialog.setContentView(R.layout.dialog_subimt);
        dialog.setTitle("Title...");
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        edit_OTP = (EditText) dialog.findViewById(R.id.edit_OTP);
        edit_phone_edit = (EditText) dialog.findViewById(R.id.edit_phone_edit);
        tv_edit_no = (TextView) dialog.findViewById(R.id.tv_edit_no);

        tv_edit_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_chk = "1";
                edit_phone_edit.setVisibility(View.VISIBLE);
            }
        });

        dialog.findViewById(R.id.imgclose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_chk = "";
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.imglogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_OTP.getText().toString().length() > 0) {
                    String verify_code = edit_OTP.getText().toString();
                    VerifyCode(verify_code, otp_id, value);
                } else {
                    Functions.showToast(getApplicationContext(), "Please Enter OTP");

                }

            }
        });

        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        Functions.setDialogParams(dialog);

    }


    private void VerifyCode(final String code, final String otp_id, final String value) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging in..");
        progressDialog.show();
        Functions.setDialogParams(dialog);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            String code = jsonObject.getString("code");


                            if (code.equalsIgnoreCase("201")) {

                                String token = jsonObject.getString("token");

                                if (jsonObject.has("user")) {
                                    JSONObject jsonObject1 = jsonObject.getJSONArray("user").getJSONObject(0);
                                    String id = jsonObject1.getString("id");
                                    String name = jsonObject1.getString("name");
                                    String mobile = jsonObject1.getString("mobile");


                                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                    editor.putString("user_id", id);
                                    editor.putString("name", name);
                                    editor.putString("mobile", mobile);
                                    editor.putString("email", edtEmail.getText().toString());
                                    editor.putString("token", token);
                                    editor.apply();

                                    Intent i = new Intent(context, Homepage.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    Functions.showToast(context, "Login Successful");
                                } else {

                                    if (jsonObject.has("message")) {
                                        String message = jsonObject.getString("message");
                                        Functions.showToast(context, "Wrong mobile number or password");
                                    }

                                }


                            } else if (code.equalsIgnoreCase("200")) {
                                String token = jsonObject.getString("token");
                                String user_id = jsonObject.getString("user_id");

                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("user_id", user_id);
                                editor.putString("name", edtname.getText().toString());
                                editor.putString("mobile", edtPhone.getText().toString());
                                editor.putString("token", token);

                                editor.apply();

                                Intent i = new Intent(context, Homepage.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                Functions.showToast(context, "Register Successful");
//
                            } else {

                                if (jsonObject.has("message")) {
                                    String message = jsonObject.getString("message");
                                    Functions.showToast(context, message);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //  handleResponse(response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Functions.showToast(context, "Something went wrong");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
               /* params.put("otp", edit_OTP.getText().toString());
                params.put("otp_id", otp_id.trim());*/
                params.put("otp", "");
                params.put("otp_id", "");
                if (str_chk.equals("1")) {
                    params.put("mobile", edit_phone_edit.getText().toString());
                } else {
                    params.put("mobile", edtPhone.getText().toString());
                }
                params.put("name", edtname.getText().toString());
                params.put("email", edtEmail.getText().toString());
                params.put("wallet_address", getCurrentWalletAddress());
                params.put("pan_card_no", "");
                params.put("dob", "");
                params.put("state", "");
                params.put("password", edtPassword.getText().toString());
                params.put("gender", value.trim());
                params.put("referral_code", edtReferalCode.getText().toString().trim());
                if (panBitmap != null)
                    params.put("pan_card", Functions.Bitmap_to_base64(context, panBitmap));

                params.put("type", REGISTER);

                Log.e("LoginScreen_otp", "Register : " + params);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Const.TOKEN);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }

    public void LoginBtnClick(View view) {
        startActivity(new Intent(getApplicationContext(), LoginWithPasswordScreen_A.class));
    }

    // Initialize Google Sign-In
    private void initializeGoogleSignIn() {
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set click listener for Google button
        /*findViewById(R.id.imggoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });*/
    }

    // Start Google Sign-In process
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 9001);
    }

    // Handle Google Sign-In result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleGoogleSignInResult(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle successful Google Sign-In (just show popup, no backend)
    private void handleGoogleSignInResult(GoogleSignInAccount account) {
        if (account != null) {
            String name = account.getDisplayName();
            String email = account.getEmail();

            // Just show a toast with the Google account info (no backend call)
            Toast.makeText(this, "Google Sign-In Successful!\nName: " + name + "\nEmail: " + email, Toast.LENGTH_LONG).show();

            // Optionally pre-fill the form fields
            if (name != null && !name.isEmpty()) {
                edtname.setText(name);
            }

            // Sign out from Google to allow re-selection next time
            mGoogleSignInClient.signOut();
        }
    }



    /**
     * Get current wallet address (for use in registration)
     */
    private String getCurrentWalletAddress() {
        // Get wallet address from global WalletApplication variable
        return WalletApplication.getWalletAddress();
    }


}