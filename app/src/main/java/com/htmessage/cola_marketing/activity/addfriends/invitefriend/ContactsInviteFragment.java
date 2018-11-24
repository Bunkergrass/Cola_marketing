package com.htmessage.cola_marketing.activity.addfriends.invitefriend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.contacts.details.UserDetailsActivity;
import com.htmessage.cola_marketing.manager.ContactsManager;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ContactsInviteFragment extends Fragment implements ContactsInviteAdapter.OnItemClickListener {
    private RecyclerView recyclerview;
    private ContactsInviteAdapter adapter;
    private InviteSidebar sidebar;
    private List<ContactInfo> lists = new ArrayList<>();
    private List<ContactInfo> topList = new ArrayList<>();
    private List<ContactInfo> lastList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_invite_contacts, container, false);
        initView(inflate);
        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        setListener();
    }

    private void setListener() {
        adapter.setListener(this);
        sidebar.setVisibility(View.VISIBLE);
        sidebar.setListView(recyclerview);
    }

    private void initData() {
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ContactsInviteAdapter(getActivity(), lists);
        recyclerview.setAdapter(adapter);
        ContactsFetcherHelper.queryContactInfo(getActivity(), new ContactsFetcherHelper.OnContactsHasListener() {
            @Override
            public void onContactsComplete(final List<ContactInfo> list, final JSONArray moblies, final String mobileString) {
                lists.clear();
                lists.addAll(list);
                Collections.sort(lists, new PinyinComparator());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendContactsToService(mobileString);
                    }
                });
            }
        });
    }

    private void initView(View root) {
        recyclerview = root.findViewById(R.id.recyclerview);
        sidebar = root.findViewById(R.id.sidebar);
    }

    @Override
    public void onItemClick(View view, ContactInfo info) {
//        int type = info.getType();
//        String userId = info.getUserId();
//        if (type != 1) {
//            if (!"0".equals(userId)) {
//                startActivity(new Intent(getActivity(), UserDetailsActivity.class).putExtra(HTConstant.JSON_KEY_HXID, userId));
//            }
//        }
    }

    @Override
    public void onInviteButtonClick(View view, ContactInfo info) {
        int type = info.getType();
        if (type == 1) {
            sendSMS(getString(R.string.invite_msg), info.getPhoneNumber());
        } else {
            String userId = info.getUserId();
            if (!"0".equals(userId)) {
                startActivity(new Intent(getActivity(), UserDetailsActivity.class).putExtra(HTConstant.JSON_KEY_HXID, userId));
            }
        }
    }

    public class PinyinComparator implements Comparator<ContactInfo> {

        @Override
        public int compare(ContactInfo o1, ContactInfo o2) {
            String py1 = o1.getLetter();
            String py2 = o2.getLetter();
            if (py1.equals(py2)) {
                return o1.getName().compareTo(o2.getName());
            } else {
                if ("#".equals(py1)) {
                    return 1;
                } else if ("#".equals(py2)) {
                    return -1;
                }
                return py1.compareTo(py2);
            }

        }
    }

    private void sendContactsToService(final String mobiles) {
        topList.clear();
        lastList.clear();
        List<Param> params = new ArrayList<>();
        params.add(new Param("plist", mobiles));
        new OkHttpUtils(getActivity()).post(params, HTConstant.URL_SEND_CONTANCTS, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("slj", "上传的到的数据:" + jsonObject.toJSONString());
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        JSONArray array = jsonObject.getJSONArray("user");
                        if (array != null && array.size() != 0) {
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                String tel = object.getString(HTConstant.JSON_KEY_TEL);
                                String userId = object.getString(HTConstant.JSON_KEY_HXID);
                                if (mobiles.contains(tel)) {
                                    for (int j = 0; j < lists.size(); j++) {
                                        ContactInfo contactInfo = lists.get(j);
//                                        CommonUtils.setContactsInfoInitialLetter(contactInfo);
                                        if (contactInfo.getPhoneNumber().equals(tel) && ContactsManager.getInstance().getContactList().containsKey(userId)) {
                                            contactInfo.setType(3);
                                            contactInfo.setUserId(userId);
                                        } else if (contactInfo.getPhoneNumber().equals(tel)) {
                                            contactInfo.setType(2);
                                            contactInfo.setUserId(userId);
                                        }
                                        if (!"0".equals(contactInfo.getUserId())) {
                                            contactInfo.setLetter("*");
                                            if (!topList.contains(contactInfo) && contactInfo.getType() == 2) {
                                                topList.add(contactInfo);
                                            }
                                            if (lastList.contains(contactInfo)) {
                                                lastList.remove(contactInfo);
                                            }
                                        } else {
                                            if (!lastList.contains(contactInfo) && contactInfo.getType() == 1) {
                                                lastList.add(contactInfo);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case -1:
                        break;
                    default:
                        break;
                }
                lists.clear();
                lists.addAll(0, topList);
                Collections.sort(lastList, new PinyinComparator());
                lists.addAll(lastList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMsg) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 发送短信
     *
     * @param smsBody
     */

    private void sendSMS(String smsBody, String mobile) {
        Uri smsToUri = Uri.parse("smsto:" + mobile);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", smsBody);
        startActivity(intent);
    }
}
