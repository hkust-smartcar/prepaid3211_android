package dcheungaa.prepaid3211;

import android.app.Activity;
import android.app.PendingIntent;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import dcheungaa.prepaid3211.Models.HistoryViewModel;

public class MainActivity extends AppCompatActivity implements BalanceFragment.OnFragmentInteractionListener, HistoryFragment.OnFragmentInteractionListener, AdminFragment.OnFragmentInteractionListener {

    public static MyDatabase database;
    public static AlertDialog sharedSecretDialog;
    public static MutableLiveData<String> stringOut;
    public static WeakReference<HistoryViewModel> historyViewModelReference;
    public static ActionBar actionBar;
    public static DateFormat dateFormat;

    private BalanceFragment balanceFragment;
    private HistoryFragment historyFragment;
    private AdminFragment adminFragment;

    private NfcAdapter nfcAdapter;

    private BottomNavigationView bottomNavigationView;

    private float dp = 0;

    public static final int id_action_admin = 100;

    private SharedPreferences preferences;
    private HistoryViewModel historyViewModel;

    public static final int NEW_HISTORY_ACTIVITY_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = MyDatabase.getDatabase(this);

        dateFormat = SimpleDateFormat.getDateTimeInstance();


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        dp = getResources().getDisplayMetrics().density;

        preferences = getPreferences(MODE_PRIVATE);

        stringOut = new MutableLiveData<>();

        setupDialogs();
        setupBottomNavigation(this);
        findViews();
        setupNfc();

        if (preferences.getBoolean("enabled_admin", false)) {
            addAdminMenuItem();
        }

        historyViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        historyViewModelReference = new WeakReference<HistoryViewModel>(historyViewModel);

        balanceFragment = new BalanceFragment();
        historyFragment = new HistoryFragment();
        adminFragment = new AdminFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragement_container, balanceFragment).commit();
        }


        handleIntent(getIntent());
    }

    private void setupDialogs() {

        {
            final LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            linearLayout.setPadding((int) (dp * 20), (int) (dp * 16), (int) (dp * 20), (int) (dp * 20));

            final EditText editTextSharedSecret = new EditText(this);
            editTextSharedSecret.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            editTextSharedSecret.setHint(R.string.enter_shared_secret);

            linearLayout.addView(editTextSharedSecret);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle(R.string.enable_admin_tools)
                    .setCancelable(false)
                    .setView(linearLayout)
                    .setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sharedSecret = editTextSharedSecret.getText().toString();

                            preferences.edit().putBoolean("enabled_admin", true).apply();

                            addAdminMenuItem();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            sharedSecretDialog = builder.create();
        }

    }

    private void setupBottomNavigation(final Context c) {
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                        switch (item.getItemId()) {
                            case R.id.action_balance:
                                transaction.replace(R.id.fragement_container, balanceFragment).commit();
                                break;
                            case R.id.action_history:
                                transaction.replace(R.id.fragement_container, historyFragment).commit();
                                break;
                            case id_action_admin:
                                transaction.replace(R.id.fragement_container, adminFragment).commit();
                                break;
                        }
                        return true;
                    }
                });
    }

    private void addAdminMenuItem() {
        bottomNavigationView.getMenu()
                .add(Menu.NONE, 100, 100, "Admin")
                .setIcon(R.drawable.ic_security_black_24dp)
                .setEnabled(true);
    }

    private void findViews() {
        //fragmentContainer = findViewById(R.id.fragement_container);
    }

    private void setupNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, getResources().getString(R.string.error_no_nfc_support), Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!nfcAdapter.isEnabled()) {
            stringOut.setValue(getResources().getString(R.string.info_nfc_disabled));
        } else {
            stringOut.setValue(getResources().getString(R.string.prompt_approach_card));
        }
    }


    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Log.i("TECH LIST", Arrays.toString(tag.getTechList()));
            new UltralightReaderTask(this).execute(tag);
        }
    }

    private static class UltralightReaderTask extends AsyncTask<Tag, Void, byte[]> {

        private WeakReference<MainActivity> activity;

        UltralightReaderTask(MainActivity mainActivity) {
            super();
            activity = new WeakReference<>(mainActivity);
        }

        @Override
        protected byte[] doInBackground(Tag... params) {
            Tag tag = params[0];

            MifareUltralight ultralight = MifareUltralight.get(tag);
            if (ultralight == null || ultralight.getType() != MifareUltralight.TYPE_ULTRALIGHT_C) {
                // Mifare Ultralight C is not supported by this Tag.
                return null;
            }

            try {
                ultralight.connect();

                return readPages(ultralight, 0);
            } catch (IOException ioException) {
                stringOut.setValue(activity.get().getResources().getString(R.string.exception_card_communication_failed));
            }

            return null;
        }

        private byte[] readPages(MifareUltralight ultralight, int page) throws IOException {
            return ultralight.readPages(page);
        }

        @Override
        protected void onPostExecute(byte[] result) {
            if (result != null) {
                stringOut.setValue("Read content: " + Arrays.toString(result));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[] {
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        };

        String[][] techList = new String[][] {
                new String[] {
                        NfcA.class.getName(),
                        MifareUltralight.class.getName(),
                        Ndef.class.getName()
                }
        };

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link MainActivity} requesting to stop the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    /**
     * This method gets called, when a new Intent gets associated with the current activity instance.
     * Instead of creating a new activity, onNewIntent will be called. For more information have a look
     * at the documentation.
     *
     * In our case this method gets called, when the user attaches a Tag to the device.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * It's important, that the activity is in the foreground (resumed). Otherwise
     * an IllegalStateException is thrown.
     */
    @Override
    protected void onResume() {
        super.onResume();

        setupForegroundDispatch(this, nfcAdapter);
    }

    /**
     * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
     */
    @Override
    protected void onPause() {
        stopForegroundDispatch(this, nfcAdapter);

        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }
}
