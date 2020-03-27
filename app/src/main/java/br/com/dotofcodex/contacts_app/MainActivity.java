package br.com.dotofcodex.contacts_app;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import br.com.dotofcodex.contacts_app.adapter.ContactRecyclerViewAdapter;
import br.com.dotofcodex.contacts_app.model.Contact;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
* More information in the following link:
 *  https://stackoverflow.com/questions/11218845/how-to-get-contacts-phone-number-in-android
* */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.rv_contacts)
    protected RecyclerView contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (!hasPermissions()) {
            requestPermission();
            return;
        }

        setupRecyclerViewContacts();
        readContacts();
    }

    private void setupRecyclerViewContacts() {
        contacts.setHasFixedSize(true);
        contacts.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateContacts(List<Contact> contacts) {
        this.contacts.setAdapter(new ContactRecyclerViewAdapter(this, contacts));
    }

    private boolean hasPermissions() {
        boolean read = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        boolean write = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        return read && write;
    }

    private void requestPermission() {
        Dexter
            .withActivity(this)
            .withPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
            .withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    for (PermissionDeniedResponse permission : report.getDeniedPermissionResponses()) {
                        if (permission.getPermissionName().equals(Manifest.permission.READ_CONTACTS)) {
                            Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), "Por favor, forneça permissão de leitura", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("OK", (View v) -> {
                                requestPermission();
                            });
                            snackbar.show();
                        }
                    }

                    for (PermissionGrantedResponse permission : report.getGrantedPermissionResponses()) {
                        if (permission.getPermissionName().equals(Manifest.permission.READ_CONTACTS)) {
                            readContacts();
                        }
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).check();
    }

    private void readContacts() {
        ContentResolver resolver = getContentResolver();
        String[] projection = new String[] {
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.Data.DATA1
        };

        String selection = ContactsContract.Data.MIMETYPE + " IN ('" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "')";
        try (Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI, projection, selection, null, null)) {
            if (cursor != null) {
                cursor.moveToFirst();
                try {
                    List<Contact> contacts = new ArrayList<>();
                    Contact contact = null;
                    String id = null;
                    String name = null;
                    String number = null;

                    do {
                        id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID));
                        name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME));
                        number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DATA1));
                        contact = new Contact(id, name, number);
                        contacts.add(contact);
                    } while (cursor.moveToNext());

                    updateContacts(contacts);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Falha na leitura dos contatos", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}