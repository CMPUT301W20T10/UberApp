package com.cmput301w20t10.uberapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cmput301w20t10.uberapp.R;

public class AddFundsFragment extends DialogFragment {
    private OnFragmentInteractionListener listener;
    private EditText addFunds;

    // for main to implement
    public interface OnFragmentInteractionListener {
        void updateBalance(Float addAmount);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnFragmentInteractionListener) context;
    }

    public AddFundsFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_funds_fragment, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        addFunds = view.findViewById(R.id.amount_to_add);

        return builder
                .setView(view)
                .setTitle("Add Funds")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Float addAmount = Float.parseFloat(addFunds.getText().toString());
                        listener.updateBalance(addAmount);
                    }
                }).create();
    }
}
