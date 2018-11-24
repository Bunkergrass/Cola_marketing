package com.htmessage.cola_marketing.activity.moments;

import java.util.ArrayList;

public interface AdapterListener {
    void onUserClicked(int position, String userId);

    void onPraised(int position, String aid);

    void onCommented(int position, String aid);

    void onCancelPraised(int position, String aid);

    void onCommentDelete(int position, String cid);

    void onDeleted(int position, String aid);

    void onImageClicked(int position, int index, ArrayList<String> images);

    void onMomentTopBackGroundClock();
}
