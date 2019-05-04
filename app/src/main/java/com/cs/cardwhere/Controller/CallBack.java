package com.cs.cardwhere.Controller;

import com.cs.cardwhere.Models.Card;

import java.util.ArrayList;

public interface CallBack {
    void onSuccess(ArrayList<Card> cards);
    void onFail(String msg);
}
