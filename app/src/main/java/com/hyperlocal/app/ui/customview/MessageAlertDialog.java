package com.hyperlocal.app.ui.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hyperlocal.app.R;
import com.hyperlocal.app.ui.notification.NotificationAcceptRequestModel;

/**
 * @author ${Umesh} on 18-05-2018.
 */

public class MessageAlertDialog {
    Dialog dialog;
    private TextView titleTextView;
    private Button btnYes, btnNo;
    MessageAlertDialog messageAlertDialog;
    FrameLayout progressBarFramLayout;
    private NotificationAcceptRequestModel model;

    public MessageAlertDialog(Context context, NotificationAcceptRequestModel model) {
        this.model = model;
        dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.notification_dialog);
        if (dialog.getWindow() != null)
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initViews();
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    private void initViews() {
        titleTextView = dialog.findViewById(R.id.tv_ask);
        btnYes = dialog.findViewById(R.id.btn_yes);
        btnNo = dialog.findViewById(R.id.btn_no);
        progressBarFramLayout = dialog.findViewById(R.id.progressBar);
    }

    public void setTitle(String title) {
        if(model.getRequestType()!=null&&model.getRequestType().equals("give")){
            titleTextView.setText("One of your neighbors wants to give " + title);
        }else if(model.getRequestType()!=null&&model.getRequestType().equals("ask")){
            titleTextView.setText("One of your neighbors is asking for " + title);
        }else if(model.getRequestType()!=null&&model.getRequestType().equals("invite")){
            titleTextView.setText("One of your neighbors is invited you for " + title);
        }
    }


    public void setYesButtonListener(DialogButtonClickListener listener) {
        btnYes.setOnClickListener(view -> listener.acceptRequestButtonClick(model));
    }

    public void setNoButtonListener(DialogButtonClickListener listener) {
        btnNo.setOnClickListener(view -> listener.cancelButtonClick());
    }

    public void showProgressBar() {
        progressBarFramLayout.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBarFramLayout.setVisibility(View.GONE);
    }

    public interface DialogButtonClickListener {
        void acceptRequestButtonClick(NotificationAcceptRequestModel model);

        void cancelButtonClick();

    }

}
