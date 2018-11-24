package com.htmessage.cola_marketing.manager;

import android.content.Context;
import android.util.Log;

import com.htmessage.cola_marketing.domain.User;
import com.htmessage.cola_marketing.domain.UserDao;

import java.util.List;
import java.util.Map;

public class ContactsManager {
    private static ContactsManager contactsManager;
    private Context context;
    private UserDao userDao;
    private Map<String,User> contacts=null;


    synchronized public static void init(Context context) {
        if (contactsManager == null) {
            contactsManager = new ContactsManager(context);
        }
    }

    public ContactsManager(Context context) {
        this.context = context;
        userDao= new UserDao(context);
        initContacts();
    }

    public static ContactsManager getInstance(){
        if(contactsManager==null){
            throw  new RuntimeException("please init this first!");
        }
        return  contactsManager;
    }

    public boolean saveContactList(List<User> contactList) {
        Log.d("saveContactList----->",contactList.size()+"");
        contacts.clear();
        for (User user:contactList){
            contacts.put(user.getUsername(),user);
        }
        userDao.saveContactList(contactList);
        return true;
    }

    public Map<String, User> getContactList() {
        if(contacts==null){
            contacts=userDao.getContactList();
        }
        return contacts;
    }

    public void saveContact(User user){
        contacts.put(user.getUsername(),user);
        userDao.saveContact(user);
    }

    private void  initContacts(){
        contacts=userDao.getContactList();
    }
}
