package com.example.myapplication.ui.contacts;

import com.example.myapplication.model.Contact;
import java.util.List;

public interface ContactView {
    void displayContacts(List<Contact> contacts);
    void showPermissionExplanation();
    void requestRuntimePermission();
    void showAccessDeniedMessage();
}