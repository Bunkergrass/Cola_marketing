package com.htmessage.cola_marketing.activity.contacts;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.domain.User;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends BaseAdapter implements SectionIndexer {
    private Context context;
    private List<User> data;

    public ContactsAdapter(Context context, List<User> data) {
        this.context = context;
        this.data = data;
    }

    private static class ViewHolder {
        ImageView iv_avatar;
        TextView nameTextview, tvHeader;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public User getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_contact_list, null);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.tvHeader = convertView.findViewById(R.id.header);
            holder.nameTextview = convertView.findViewById(R.id.tv_name);
            holder.iv_avatar = convertView.findViewById(R.id.iv_avatar);
            convertView.setTag(holder);
        }

        User user = data.get(position);

        holder.tvHeader.setVisibility(View.VISIBLE);
        String nick = user.getNick();
        String avatar = user.getAvatar();
        String headerLetter = user.getInitialLetter();
        String preHeaderLetter = getPreHeaderLeeter(position);

        if (headerLetter.equals(preHeaderLetter)) {
            holder.tvHeader.setVisibility(View.GONE);

        } else {
            holder.tvHeader.setVisibility(View.VISIBLE);
            holder.tvHeader.setText(headerLetter);
        }
        holder.nameTextview.setText(nick);
        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar);
        Glide.with(context).load(avatar).apply(options).into(holder.iv_avatar);

        return convertView;
    }


    /**
     * implements SectionIndexer
     * */
    List<String> list;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        list = new ArrayList<String>();
        list.add(context.getString(R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {
            String letter = getItem(i).getInitialLetter();
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return positionOfSection.get(sectionIndex+1);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    private String getPreHeaderLeeter(int position) {
        if (position > 0) {
            User user = getItem(position - 1);
            return user.getInitialLetter();
        }
        return null;
    }

}
