package com.example.myapplication.controller;

import android.content.Context;
import com.example.myapplication.db.ContactLoader;
import com.example.myapplication.model.Contact;
import com.example.myapplication.ui.contacts.ContactView;
import java.util.List;

public class ContactController {
    private final ContactView view;

    public ContactController(ContactView view) {
        this.view = view;
    }

    public void onFetchContactsRequested(Context context, boolean isPermissionGranted) {
        if (isPermissionGranted) {
            List<Contact> contacts = ContactLoader.loadAllContacts(context);
            view.displayContacts(contacts);
        } else {
            view.showPermissionExplanation();
        }
    }
}