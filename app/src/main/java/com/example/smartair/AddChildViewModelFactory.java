package com.example.smartair;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

public class AddChildViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public AddChildViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass,
                                          @NonNull CreationExtras extras) {
        if (modelClass.isAssignableFrom(AddChildViewModel.class)) {
            return (T) new AddChildViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
