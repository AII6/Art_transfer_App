package com.example.afinal.ui.processing;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.afinal.MainActivity;
import com.example.afinal.R;
import com.example.afinal.ui.picture.PictureViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        submitBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Context content =  contentIv.getContext();
//                Matrix style = templateIv.getImageMatrix();
//                System.out.println(style);
                System.out.println();
            }
        });
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
            //显示图片
            Glide.with(this).load(imageUri).into(contentIv);
        }
        if (requestCode == PICK_TEMPLATE_CODE && resultCode == RESULT_OK) {
            final Uri imageUri = Objects.requireNonNull(data).getData();
            //显示图片
            Glide.with(this).load(imageUri).into(styleIv);
        }
    }





}