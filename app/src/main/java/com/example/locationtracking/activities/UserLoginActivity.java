package com.example.locationtracking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.locationtracking.api.ApiClient;
import com.example.locationtracking.api.ApiEndPoint;
import com.example.locationtracking.components.AppCheckBox;
import com.example.locationtracking.components.AppEditText;
import com.example.locationtracking.util.AppToast;
import com.example.locationtracking.R;
import com.example.locationtracking.util.UserDataManager;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginActivity extends AppCompatActivity {

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @BindView(R.id.btnCheckBox)
    AppCheckBox btnCheckBox;

    @BindView(R.id.txtEmployeeId)
    AppEditText txtEmployeeId;

    @BindView(R.id.txtPassword)
    EditText txtPassword;

    @BindView(R.id.txtHeadline)
    TextView txtHeadline;

    private ApiEndPoint apiEndPoint;

    private String employeeId;
    private String password;

    @OnClick(R.id.btnLogin)
    public void login() {

        if (txtEmployeeId.getText().toString().trim().length() == 0) {
            AppToast.showCenterToastWithVibrate(getApplicationContext(), "Enter Employee Id.");
            return;
        }

        if (txtPassword.getText().toString().trim().length() == 0) {
            AppToast.showCenterToastWithVibrate(getApplicationContext(), "Enter Password.");
            return;
        }

        employeeId = txtEmployeeId.getText().toString();
        password = txtPassword.getText().toString();
        userLogin(employeeId, password);
    }


    @OnClick(R.id.btnShowPassword)
    public void showHidePassword() {
        if (txtPassword.getInputType() == InputType.TYPE_CLASS_TEXT) {
            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        ButterKnife.bind(this);

        apiEndPoint = ApiClient.getClient().create(ApiEndPoint.class);

        txtHeadline.setText(R.string.app_name);
        btnCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                UserDataManager.setKeyLoginRemember(getApplicationContext(), b);
            }
        });

        boolean autoLogin = UserDataManager.getKeyLoginRemember(getApplicationContext());
        if (autoLogin) {
            btnCheckBox.setChecked(true);
            String employeeId = UserDataManager.getKeyEmployeeId(getApplicationContext());
            if (employeeId != null) {
                txtEmployeeId.setText(employeeId);
            }
            String password = UserDataManager.getKeyPassword(getApplicationContext());
            if (password != null) {
                txtPassword.setText(password);
            }
            if ((employeeId != null) && (password != null)) {
                if ((employeeId.trim().length() > 0) && (password.trim().length() > 0)) {
                    login();
                }
            }
        }
    }


    private void userLogin(final String employeeId, final String password) {

        Call<ResponseBody> call = apiEndPoint.login(
                employeeId,
                password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (response.isSuccessful()) {

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        final JSONObject dataJson = object.getJSONObject("data");

                        if (dataJson.has("error")) {
                            AppToast.showCenterToast(getApplicationContext(), dataJson.getString("error"));
                            return;
                        }


                        String name = dataJson.getString("name");
                        String photoUrl = dataJson.getString("photo_url");
                        String designation = dataJson.getString("designation");
                        String accessToken = dataJson.getString("access_token");
                        String timeInterval = dataJson.getString("time_interval");
                        UserDataManager.setKeyName(getApplicationContext(),name);
                        UserDataManager.setKeyPhotoUrl(getApplicationContext(),photoUrl);
                        UserDataManager.setKeyDesignation(getApplicationContext(),designation);
                        UserDataManager.setKeyEmployeeId(getApplicationContext(),txtEmployeeId.getText().toString());
                        UserDataManager.setKeyPassword(getApplicationContext(),txtPassword.getText().toString());
                        UserDataManager.setKeyAccessToken(getApplicationContext(),accessToken);
                        UserDataManager.setKeyTimeInterval(getApplicationContext(),timeInterval);

                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);

                    } catch (Exception ex) {

                    }
                } else {

                    if (response.code() == 401) {
                        try {
                            JSONObject object = new JSONObject(response.errorBody().string());
                            AppToast.showCenterToastWithVibrate(getApplicationContext(), object.getString("message"));
                        } catch (Exception ex) {

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        String employeeId = UserDataManager.getKeyEmployeeId(getApplicationContext());
        if (employeeId != null) {
            txtEmployeeId.setText(employeeId);
        }
        String password = UserDataManager.getKeyPassword(getApplicationContext());
        if (password != null) {
            txtPassword.setText(password);
        }
    }
}