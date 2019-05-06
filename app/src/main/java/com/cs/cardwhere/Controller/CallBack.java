package com.cs.cardwhere.Controller;

import com.cs.cardwhere.Bean.CardBean;

import java.util.ArrayList;

public interface CallBack {
    void onSuccess(ArrayList<CardBean> cards);
    void onFail(String msg);
}
