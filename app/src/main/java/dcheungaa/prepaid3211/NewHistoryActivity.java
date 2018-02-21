package dcheungaa.prepaid3211;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewHistoryActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "dcheungaa.prepaid3211.REPLY";

    private EditText editTextItemName;
    private EditText editTextItemCost;
    private EditText editTextPurchasedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViews();

        final Button btnAddHistory = findViewById(R.id.btn_add_history);
        btnAddHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();

                String itemNameString = editTextItemName.getText().toString();
                String itemCostString = editTextItemCost.getText().toString();
                String purchasedAtString = editTextPurchasedAt.getText().toString();

                if (TextUtils.isEmpty(itemNameString) ||
                        TextUtils.isEmpty(itemCostString) ||
                        TextUtils.isEmpty(purchasedAtString)) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    replyIntent.putExtra(EXTRA_REPLY, new String[]{
                            itemNameString,
                            itemCostString,
                            purchasedAtString
                    });
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }

    private void findViews() {
        editTextItemName = findViewById(R.id.editTextItemName);
        editTextItemCost = findViewById(R.id.editTextItemCost);
        editTextPurchasedAt = findViewById(R.id.editTextPurchasedAt);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
