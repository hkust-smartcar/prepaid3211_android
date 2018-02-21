package dcheungaa.prepaid3211;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dcheungaa.prepaid3211.Models.HistoryViewModel;
import dcheungaa.prepaid3211.Records.History;

import static android.app.Activity.RESULT_OK;
import static dcheungaa.prepaid3211.MainActivity.NEW_HISTORY_ACTIVITY_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BalanceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BalanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BalanceFragment extends Fragment {

    private View cardFrontLayout;
    private View cardBackLayout;
    private View cardFrame;
    private View btnNewHistory;
    private TextView textOut;
    private AnimatorSet setRightOut;
    private AnimatorSet setLeftIn;
    private boolean isCardBackVisible = false;
    private boolean isDuringCardFlip = false;

    private float dp = 0;
    private int secretCounter = 0;

    private OnFragmentInteractionListener listener;

    public BalanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BalanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BalanceFragment newInstance() {
        return new BalanceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View balanceView = inflater.inflate(R.layout.fragment_balance, container, false);

        dp = getResources().getDisplayMetrics().density;

        MainActivity.actionBar.setTitle(R.string.balance);

        findViews(balanceView);
        loadAnimations(container.getContext());

        float scale = dp * getResources().getInteger(R.integer.camera_distance);
        cardFrontLayout.setCameraDistance(scale);
        cardBackLayout.setCameraDistance(scale);
        cardBackLayout.setAlpha(0);

        View.OnClickListener flipCardListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (secretCounter == 2) {
                    MainActivity.sharedSecretDialog.show();
                }

                if (!isDuringCardFlip) {
                    flipCard();
                } else
                    secretCounter++;
            }
        };

        cardFrontLayout.setOnClickListener(flipCardListener);
        cardBackLayout.setOnClickListener(flipCardListener);

        setRightOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isDuringCardFlip = true;
                secretCounter = 0;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isDuringCardFlip = false;
                secretCounter = 0;
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });


        btnNewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(balanceView.getContext(), NewHistoryActivity.class);

                startActivityForResult(intent, NEW_HISTORY_ACTIVITY_REQUEST_CODE);
            }
        });

        RecyclerView historyRecyclerView = balanceView.findViewById(R.id.recent_history_view);
        historyRecyclerView.addItemDecoration(new DividerItemDecoration(container.getContext(), DividerItemDecoration.VERTICAL));
        final HistoryAdapter historyAdapter = new HistoryAdapter(container.getContext());
        historyRecyclerView.setAdapter(historyAdapter);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()) {
            private boolean isScrollEnabled = false;

            @Override
            public boolean canScrollVertically() {
                //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
                return isScrollEnabled && super.canScrollVertically();
            }
        });

        HistoryViewModel historyViewModel = MainActivity.historyViewModelReference.get();
        historyViewModel.getAllHistories().observe(this, new Observer<List<History>>() {
            @Override
            public void onChanged(@Nullable List<History> histories) {
                if (histories != null && histories.size() > 0)
                    historyAdapter.setHistories(histories.subList(0, Math.min(2, histories.size())));
                else
                    historyAdapter.setHistories(new ArrayList<History>());
            }
        });

        MainActivity.stringOut.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textOut.setText(s);
            }
        });

        return balanceView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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

    private void findViews(View view) {
        cardFrontLayout = view.findViewById(R.id.card_front);
        cardBackLayout = view.findViewById(R.id.card_back);
        cardFrame = view.findViewById(R.id.card_frame);
        btnNewHistory = view.findViewById(R.id.btn_new_history);
        textOut = view.findViewById(R.id.textLastUpdated);
    }

    private void loadAnimations(final Context context) {
        setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.back_flip);
        setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.front_flip);
    }

    public void flipCard() {
        if (!isCardBackVisible) {
            setRightOut.setTarget(cardFrontLayout);
            setLeftIn.setTarget(cardBackLayout);
            setRightOut.start();
            setLeftIn.start();
            isCardBackVisible = true;
        } else {
            setRightOut.setTarget(cardBackLayout);
            setLeftIn.setTarget(cardFrontLayout);
            setRightOut.start();
            setLeftIn.start();
            isCardBackVisible = false;
        }
    }
}
