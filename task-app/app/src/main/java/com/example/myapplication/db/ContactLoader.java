package com.example.myapplication.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import com.example.myapplication.model.Contact;
import java.util.ArrayList;
import java.util.List;

public class ContactLoader {

    public static List<Contact> loadAllContacts(Context context) {
        List<Contact> contactList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

                if (idIndex == -1 || nameIndex == -1) continue;

                long contactId = cursor.getLong(idIndex);
                String name = cursor.getString(nameIndex);
                
                Contact contact = new Contact();
                contact.setId(contactId);
                contact.setName(name);

                Cursor phoneCursor = resolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{String.valueOf(contactId)}, null
                );
                if (phoneCursor != null) {
                    int pIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    if (pIndex != -1) {
                        while (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(pIndex);
                            if (phoneNumber != null) {
                                contact.addPhoneNumber(phoneNumber);
                            }
                        }
                    }
                    phoneCursor.close();
                }

                Cursor emailCursor = resolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{String.valueOf(contactId)}, null
                );
                if (emailCursor != null) {
                    int eIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                    if (eIndex != -1) {
                        while (emailCursor.moveToNext()) {
                            String email = emailCursor.getString(eIndex);
                            if (email != null) {
                                contact.addEmailAddress(email);
                            }
                        }
                    }
                    emailCursor.close();
                }

                contactList.add(contact);
            }
            cursor.close();
        }
        return contactList;
    }

    public static String getContactNameById(Context context, String contactId) {
        if (contactId == null || contactId.isEmpty()) return "";
        android.net.Uri contactUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{android.provider.ContactsContract.Contacts.DISPLAY_NAME};
        String selection = android.provider.ContactsContract.Contacts._ID + " = ?";
        String[] selectionArgs = new String[]{contactId};
        android.database.Cursor cursor = context.getContentResolver().query(contactUri, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(android.provider.ContactsContract.Contacts.DISPLAY_NAME);
            String name = cursor.getString(nameIndex);
            cursor.close();
            return name;
        }
        return "Unknown Contact";
    }

    public static String getPhoneNumberByContactId(Context context, String contactId) {
        String phoneNumber = null;
        android.net.Uri phoneUri = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selection = android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{contactId};
        android.database.Cursor cursor = context.getContentResolver().query(phoneUri, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            int pIndex = cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER);
            if (pIndex != -1) {
                phoneNumber = cursor.getString(pIndex);
            }
            cursor.close();
        }
        return phoneNumber;
    }

    public static String getEmailByContactId(Context context, String contactId) {
        String emailAddress = null;
        android.net.Uri emailUri = android.provider.ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String[] projection = new String[]{android.provider.ContactsContract.CommonDataKinds.Email.ADDRESS};
        String selection = android.provider.ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{contactId};
        android.database.Cursor cursor = context.getContentResolver().query(emailUri, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            int eIndex = cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Email.ADDRESS);
            if (eIndex != -1) {
                emailAddress = cursor.getString(eIndex);
            }
            cursor.close();
        }
        return emailAddress;
    }
}