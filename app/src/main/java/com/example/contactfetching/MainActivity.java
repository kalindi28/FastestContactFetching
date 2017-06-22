package com.example.contactfetching;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ListView;

import com.example.contactfetching.DataClass.CommonDataClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    ListView mList;
    ArrayList<CommonDataClass> mArrContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList= (ListView) findViewById(R.id.list_contact);
        mArrContact=new ArrayList<>();
        askForContactPermission();

    }
    private  int PERMISSION_REQUEST_CONTACT=0;
    public void askForContactPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    ,PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();


                } else {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                }
            }else{
                readContacts();
                // getAllContactOfDevice(ActivityMenu.this);
            }
        }
        else{
            readContacts();
            // getAllContactOfDevice(ActivityMenu.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //getAllContactOfDevice(ActivityMenu.this);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    readContacts();
                } else {
                    //showAlert("You need permissions for contacts");
                    //ToastMaster.showMessage(getActivity(),"No permission for contacts");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;



            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void readContacts() {
        ContentResolver resolver = getContentResolver();
        Cursor contacts = resolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.Contacts._ID + " ASC");
        Cursor data = resolver.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.MIMETYPE + "=? OR " + ContactsContract.Data.MIMETYPE + "=? OR " + ContactsContract.Data.MIMETYPE + "=? ",
                new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
                ContactsContract.Data.CONTACT_ID + " ASC");

        HashMap<String, JSONObject> mapTemp = new HashMap<String, JSONObject>();
        //contactArray = new JSONArray();

        int idIndex = contacts.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
        //  int number = contacts.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
        int nameIndex = contacts.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
        int cidIndex = data.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID);
        int data1Index = data.getColumnIndexOrThrow(ContactsContract.Data.DATA1);
        boolean hasData = data.moveToNext();
        String email = "";
        String number = "";
        String photo = "";
        mArrContact.clear();
        while (contacts.moveToNext()) {

            try {
                email = "";
                number = "";
                photo = "";
                long id = contacts.getLong(idIndex);
                CommonDataClass contactObject = new CommonDataClass();
                contactObject.setStrContactName(contacts.getString(nameIndex));
                contactObject.setStrContactId(contacts.getString(idIndex));
                //  System.out.println("Contact(" + id + "): " + contacts.getString(nameIndex));
                if (hasData) {

                    long cid = data.getLong(cidIndex);
                    while (cid <= id && hasData) {
                        if (cid == id) {
                            String type = data.getString(data.getColumnIndex(ContactsContract.Data.MIMETYPE));
                            if (type.equalsIgnoreCase(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                                number = data.getString(data1Index);
                                Pattern pattern = Pattern.compile("[^0-9]");
                                Matcher matcher = pattern.matcher(number);
                                number = matcher.replaceAll("");
                                //  System.out.println("\t(" + cid + "/" + id + ").datanumber :" +
                                //           data.getString(data1Index) + "number" + number);
                                contactObject.setStrContactNumber(number);

                            }
                            if (type.equalsIgnoreCase(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                                email = data.getString(data1Index);
                                // System.out.println("\t(" + cid + "/" + id + ").data Email:" +
                                //        data.getString(data1Index) + "email" + email);
                                contactObject.setStrContactEmail( email);
                            }
                            if (type.equalsIgnoreCase(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
                                photo = data.getString(data.getColumnIndexOrThrow(ContactsContract.Data.PHOTO_THUMBNAIL_URI));
                                //  System.out.println("\t(" + cid + "/" + id + ").data photo:" +
                                //          data.getString(data1Index) + "photo" + photo);
                                if (photo != null) {
                                    contactObject.setStrContactImage(photo);
                                } else {
                                    contactObject.setStrContactImage( "");
                                }
                            } else {
                                contactObject.setStrContactImage("");
                            }
                        }
                        hasData = data.moveToNext();
                        if (hasData) {
                            cid = data.getLong(cidIndex);
                        }

                    }
                    if (contactObject.getStrContactNumber()==null) {
                        contactObject.setStrContactNumber( "");
                    }
                    if (contactObject.getStrContactEmail()==null) {
                        contactObject.setStrContactEmail("");
                    }
                    if (contactObject.getStrContactImage()==null) {
                        contactObject.setStrContactImage("");
                    }
                    mArrContact.add(contactObject);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setContactToList();
    }

    private void setContactToList() {
        mList.setAdapter(new ContactListAdapter(mArrContact,MainActivity.this));
    }
}
