package com.upstream.basemvvmimpl.presentation.view;

import android.view.View;

public interface IView {
    void showMsg(String msg);

    void handleError(Throwable throwable);

    void showErrorMsg(String msg);

    void showMsg(String msg, String actionMsg, View.OnClickListener clickListener);

    void showErrorMsg(String msg, String actionMsg, View.OnClickListener clickListener);

    void updateLanguage();
}
