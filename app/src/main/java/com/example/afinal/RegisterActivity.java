package com.example.afinal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.afinal.service.UserService;

public class RegisterActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText phone;
    RadioGroup sex;
    Button register;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        findViews();
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name=username.getText().toString().trim();
                String pass=password.getText().toString().trim();
                String phonenum=phone.getText().toString().trim();
                String sexstr=((RadioButton)RegisterActivity.this.findViewById(sex.getCheckedRadioButtonId())).getText().toString();
                Log.i("TAG",name+"_"+pass+"_"+phonenum+"_"+sexstr);
                UserService uService=new UserService(RegisterActivity.this);
                User user=new User();
                user.setUsername(name);
                user.setPassword(pass);
                user.setPhone(phonenum);
                user.setSex(sexstr);
                uService.register(user);
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void findViews() {
        username=(EditText) findViewById(R.id.usernameRegister);
        password=(EditText) findViewById(R.id.passwordRegister);
        phone=(EditText) findViewById(R.id.phoneRegister);
        sex=(RadioGroup) findViewById(R.id.sexRegister);
        register=(Button) findViewById(R.id.Register);
    }

}