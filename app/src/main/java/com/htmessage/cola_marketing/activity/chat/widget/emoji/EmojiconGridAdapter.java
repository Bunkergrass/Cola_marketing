package com.htmessage.cola_marketing.activity.chat.widget.emoji;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.utils.Emoji.Emojicon;
import com.htmessage.cola_marketing.utils.Emoji.SmileUtils;

import java.util.List;

public class EmojiconGridAdapter extends ArrayAdapter<Emojicon> {
    private Emojicon.Type emojiconType;


    public EmojiconGridAdapter(Context context, int textViewResourceId, List<Emojicon> objects, Emojicon.Type emojiconType) {
        super(context, textViewResourceId, objects);
        this.emojiconType = emojiconType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            if(emojiconType == Emojicon.Type.BIG_EXPRESSION){
                convertView = View.inflate(getContext(), R.layout.row_big_expression, null);
            }else{
                convertView = View.inflate(getContext(), R.layout.row_expression, null);
            }
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_expression);
        Emojicon emojicon = getItem(position);

        //if you want show a name for the icons, you can set text to R.id.tv_name

        if(SmileUtils.DELETE_KEY.equals(emojicon.getEmojiText())){
            imageView.setImageResource(R.drawable.delete_expression);
        }else{
            if(emojicon.getIcon() != 0){
                imageView.setImageResource(emojicon.getIcon());
            }else if(emojicon.getIconPath() != null){
                RequestOptions options = new RequestOptions().placeholder(R.drawable.default_expression);
                Glide.with(getContext()).load(emojicon.getIconPath()).apply(options).into(imageView);
            }
        }


        return convertView;
    }
}
