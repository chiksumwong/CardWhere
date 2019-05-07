package com.cs.cardwhere.Controller;

import android.app.Activity;
import android.content.Context;

import com.cs.cardwhere.Bean.CardBean;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;

public class CardControllerTest {


    @Mock
    Context context;

    @Mock
    Activity activity;

    @Mock
    private CallBack callBack;

    @Captor
    private ArgumentCaptor<CallBack>  callBackArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testGetCards() throws Exception {
        ArrayList<CardBean> cards = new ArrayList<>();
        CardController cardController = new CardController(context);

        cardController.getCards("5o6EjulZxTN1W41hDQUcprFE49q1", callBack);
        verify(cardController).getCards("5o6EjulZxTN1W41hDQUcprFE49q1", callBackArgumentCaptor.capture());
    }

//
//
//    @Test
//    public void testAddCard() {
//        CardController cardController = new CardController();
//        CardBean card = new CardBean();
//
//        card.setCompany("test company");
//        card.setName("test name");
//        card.setTel("1122222");
//        card.setEmail("test@example.com");
//        card.setAddress("test address");
//    }
//
//    @Test
//    public void testDeleteCard() {
//        CardController cardController = new CardController();
//        cardController.deleteCard("-Le4jC0Ldv1b_FU8hw61");
//    }
}