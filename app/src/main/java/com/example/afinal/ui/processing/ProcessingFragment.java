package com.example.afinal.ui.processing;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.afinal.R;
import com.example.afinal.service.MyRequest;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

public class ProcessingFragment extends Fragment {

    private ProcessingViewModel processingViewModel;
    private Button contentBt;
    private Button styleBt;
    private Button submitBt;
    private ImageView contentIv;
    private ImageView styleIv;
    private Bitmap contentBitmap;
    private Bitmap styleBitmap;
    private String TAG = "tag";
    private static final int PICK_PHOTO_CODE = 100;
    private static final int PICK_TEMPLATE_CODE = 101;
    //需要的权限数组 读/写/相机
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        processingViewModel =
                ViewModelProviders.of(this).get(ProcessingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_processing, container, false);
        requestPermission();
        return root;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contentBt = getActivity().findViewById(R.id.photo_bt);
        styleBt = getActivity().findViewById(R.id.template_bt);
        submitBt = getActivity().findViewById(R.id.submit_bt);
        contentIv = getActivity().findViewById(R.id.photo_iv);
        styleIv = getActivity().findViewById(R.id.template_iv);
        contentBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO_CODE);
            }
        });
        styleBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_TEMPLATE_CODE);
            }
        });
        final Handler handler = new Handler();
        submitBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // 3
                try {
                    //将位图转为字节数组后再转为base64
                    ByteArrayOutputStream contentOutputStream = new ByteArrayOutputStream();
                    contentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, contentOutputStream);
                    //发起网络请求，传入base64数据
                    getContentImgBase64(Base64.encodeToString(contentOutputStream.toByteArray(), Base64.DEFAULT));

                    ByteArrayOutputStream styleOutputStream = new ByteArrayOutputStream();
                    styleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, styleOutputStream);
                    //发起网络请求，传入base64数据
                    getStyleImgBase64(Base64.encodeToString(styleOutputStream.toByteArray(), Base64.DEFAULT));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    void getContentImgBase64(final String imgBase64) {
        new Thread() {//开线程
            @Override
            public void run() {
                MyRequest request = new MyRequest();//这里是我封装的一个网络请求方法，详细代码在最下方
                String data="content="+imgBase64;
                String res = request.post("http://10.241.127.208:30000/get",data);
                Log.i("res", res);//打印返回的结果
            }
        }.start();
    }

    void getStyleImgBase64(final String imgBase64) {
        new Thread() {//开线程
            @Override
            public void run() {
                MyRequest request = new MyRequest();//这里是我封装的一个网络请求方法，详细代码在最下方
                String data="style="+imgBase64;
                String res = request.post("http://10.241.127.208:30000/get",data);
                Log.i("res", res);//打印返回的结果
            }
        }.start();
    }
    /**
     * 权限请求结果
     * @param requestCode 请求码
     * @param permissions 请求权限
     * @param grantResults 授权结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 将结果转发给 EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(9527)
    private void requestPermission(){
        String[] param = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(this.getContext(),param)){
            //已有权限
            Toast.makeText(this.getContext(),"已有权限！",Toast.LENGTH_SHORT).show();
        }else {
            //无权限 则进行权限请求
            EasyPermissions.requestPermissions(this,"请求权限",9527,param);
        }
    }

    /**
     * Toast提示
     * @param msg 内容
     */
    private void showMsg(String msg){
        Toast.makeText(this.getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    /**
     * 返回Activity结果
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_CODE && resultCode == RESULT_OK) {
            final Uri imageUri = Objects.requireNonNull(data).getData();
            ContentResolver cr = this.getContext().getContentResolver();
            try {
                contentBitmap = BitmapFactory.decodeStream(cr.openInputStream(imageUri));//获取位图
            } catch (Exception e){
                e.printStackTrace();
            }
            //显示图片
            Glide.with(this).load(imageUri).into(contentIv);
        }
        if (requestCode == PICK_TEMPLATE_CODE && resultCode == RESULT_OK) {
            final Uri imageUri = Objects.requireNonNull(data).getData();
            ContentResolver cr = this.getContext().getContentResolver();
            try {
                styleBitmap = BitmapFactory.decodeStream(cr.openInputStream(imageUri));//获取位图
            } catch (Exception e){
                e.printStackTrace();
            }
            //显示图片
            Glide.with(this).load(imageUri).into(styleIv);
        }
    }





}