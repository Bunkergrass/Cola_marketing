package com.htmessage.cola_marketing.activity.myLists;

import android.view.View;

public interface ListAdapterListener {
    void onClick(int position,View itemView);
    void onLongClick(int position,View itemView);
}
