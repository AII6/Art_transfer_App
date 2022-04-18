package com.example.afinal.ui.processing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProcessingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ProcessingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is processing fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}