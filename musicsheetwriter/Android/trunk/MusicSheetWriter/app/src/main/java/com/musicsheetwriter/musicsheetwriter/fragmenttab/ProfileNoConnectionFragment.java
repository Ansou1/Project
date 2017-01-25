package com.musicsheetwriter.musicsheetwriter.fragmenttab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.musicsheetwriter.musicsheetwriter.R;
import com.musicsheetwriter.musicsheetwriter.authentication.ILoggingManager;


public class ProfileNoConnectionFragment extends Fragment {

    private ILoggingManager mLoggingManager;

    private Button mLogIn;


    public ProfileNoConnectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_profile_no_connection, container, false);

        // get the button to trigger the log in
        mLogIn = (Button) view.findViewById(R.id.log_in);

        // launch the login action from the listener on click
        mLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        return view;
    }

    private void logIn() {
        mLoggingManager.requestConnectedUser();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mLoggingManager = (ILoggingManager) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ILoggingManager");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLoggingManager = null;
    }
}
