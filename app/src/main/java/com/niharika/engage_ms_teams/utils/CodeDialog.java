package com.niharika.engage_ms_teams.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.niharika.engage_ms_teams.R;


public class CodeDialog extends AppCompatDialogFragment {
    private EditText code;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.code_dialog, null);
        code = view.findViewById(R.id.enterCodeEdit);
        builder.setView(view)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Join Meet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mCode = code.getText().toString();
                        //open url
                        String pre = "https://test-video12.herokuapp.com/";//Enter here
                        String join_url = pre + mCode + "/preview";
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(join_url));
                        startActivity(browserIntent);
                    }
                });

        return builder.create();
    }
}
