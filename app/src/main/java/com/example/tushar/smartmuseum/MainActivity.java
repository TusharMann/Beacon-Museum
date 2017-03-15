package com.example.tushar.smartmuseum;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayAdapter<String> arrayAdapter;
    private LinearLayout mContainer;
    private ProgressDialog progressDialog;
    int a;

    private static String[] mPermissions = { Manifest.permission.ACCESS_FINE_LOCATION};
    private MyApp.OnListRefreshListener onListRefreshListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!havePermissions()) {
            Log.i(TAG, "Requesting permissions needed for this app.");
            requestPermissions();
        }

        if(!isBlueEnable()){
            Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(bluetoothIntent);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        mContainer = (LinearLayout) findViewById(R.id.activity_main);



        List<String> items = new ArrayList<>(MyApp.getInstance().regionNameList);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);

        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!MyApp.getInstance().regionList.isEmpty()) {
                    try {
//                        String beaconSSN = MyApp.getInstance().regionList.get(i).getId2().toHexString();
//                        Intent regionIntent = new Intent(ShoppingCartActivity.this,RegionDetailActivity.class);
//                        regionIntent.putExtra("beacon_ssn",beaconSSN);
//                        regionIntent.putExtra("name", MyApp.getInstance().regionNameList.get(i));
//                        startActivity(regionIntent);
                    } catch (ArrayIndexOutOfBoundsException e) {/*Do nothing*/}
                }
            }
        });
        listView.setAdapter(arrayAdapter);

    }



    private boolean isBlueEnable() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        return bluetoothAdapter.isEnabled();

    }

    private boolean havePermissions() {
        for(String permission:mPermissions){
            if(ActivityCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                return  false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                mPermissions, PERMISSIONS_REQUEST_CODE);
    }


    private void showLinkToSettingsSnackbar() {
        if (mContainer == null) {
            return;
        }
        Snackbar.make(mContainer,
                R.string.permission_denied_explanation,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).show();
    }


    private void showRequestPermissionsSnackbar() {
        if (mContainer == null) {
            return;
        }
        Snackbar.make(mContainer, R.string.permission_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request permission.
                        ActivityCompat.requestPermissions(ShoppingCartActivity.this,
                                mPermissions,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
    }



    @Override
    protected void onResume() {
        super.onResume();
        onListRefreshListener = new MyApp.OnListRefreshListener() {
            @Override
            public void onListRefresh() {
                notifyListChange();
            }
        };
        MyApp.getInstance().onListRefreshListener = onListRefreshListener;
        MyApp.getInstance().context = this;
    }

}
