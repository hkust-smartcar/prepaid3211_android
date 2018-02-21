package dcheungaa.prepaid3211;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Date;

import dcheungaa.prepaid3211.Records.History;

import static android.app.Activity.RESULT_OK;
import static dcheungaa.prepaid3211.MainActivity.NEW_HISTORY_ACTIVITY_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminFragment extends PreferenceFragmentCompat {

    private OnFragmentInteractionListener listener;

    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.admin_preferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View adminView = super.onCreateView(inflater, container, savedInstanceState);

        MainActivity.actionBar.setTitle("Admin");

        getPreferenceScreen().setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                switch (preference.getKey()) {
                    case "pref_new_purchase":
                        if (adminView != null) {
                            Intent intent = new Intent(adminView.getContext(), NewHistoryActivity.class);
                            startActivityForResult(intent, NEW_HISTORY_ACTIVITY_REQUEST_CODE);
                        }
                        break;
                    default:
                        return false;
                }

                return true;
            }
        });

        return adminView;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminFragment newInstance() {
        return new AdminFragment();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_HISTORY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String[] historyParams = data.getStringArrayExtra(NewHistoryActivity.EXTRA_REPLY);

            String itemNameString = historyParams[0];
            String itemCostString = historyParams[1];
            String purchasedAtString = historyParams[2];

            Date purchased_at;

            try {
                purchased_at = MainActivity.dateFormat.parse(purchasedAtString);
            } catch (ParseException exception) {
                purchased_at = new Date();
            }

            History history = new History(itemNameString, Integer.parseInt(itemCostString), purchased_at);

            MainActivity.historyViewModelReference.get().insert(history);
        } else {
            Toast.makeText(getContext().getApplicationContext(), Integer.toString(requestCode), Toast.LENGTH_LONG).show();
        }
    }
}
