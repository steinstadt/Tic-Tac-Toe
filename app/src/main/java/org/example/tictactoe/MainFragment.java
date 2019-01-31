package org.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {

    private AlertDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // ここでボタンを処理する
        View aboutButton = rootView.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.about_title);
                builder.setMessage(R.string.about_text);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok_label, null);
                mDialog = builder.show();
            }
        });
        // New,Continueボタンの処理
        View newButton = rootView.findViewById(R.id.new_button);
        View continueButton = rootView.findViewById(R.id.continue_button);
        // Newボタンに対するイベントリスナー
        newButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), GameActivity.class);
                getActivity().startActivity(intent);
            }
        });
        // Continueボタンに対するイベントリスナー
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra(GameActivity.KEY_RESTORE, true);
                getActivity().startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onPause(){
        super.onPause();

        // まだ表示されているならAboutダイアログを取り除く
        if (mDialog != null){
            mDialog.dismiss();
        }
    }

}
