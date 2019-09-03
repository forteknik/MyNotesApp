package com.forteknik.mynotes2;

import android.view.View;

public class CostumeOnItemClickListener implements View.OnClickListener {

    private int position;
    private OnItemClickCallback onItemClickCallback;

    public CostumeOnItemClickListener(int position, OnItemClickCallback onItemClickCallback) {
        this.position = position;
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public void onClick(View view) {
        onItemClickCallback.onItemClicked(view, position);

    }

    public interface OnItemClickCallback {
        void onItemClicked(View view, int position);
    }

}
