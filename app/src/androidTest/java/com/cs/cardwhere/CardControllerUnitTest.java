package com.cs.cardwhere;

import android.content.Context;

import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.cs.cardwhere.Bean.CardBean;
import com.cs.cardwhere.Controller.CallBack;
import com.cs.cardwhere.Controller.CardController;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class CardControllerUnitTest {

    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    String cardId = "";

    @Test
    public void testAddCard() {
        CardController cardController = new CardController(context);
        CardBean card = new CardBean();

        card.setCompany("Fook Restaurant");
        card.setName("Manager Wong");
        card.setTel("23457890");
        card.setEmail("test@example.com");
        card.setAddress("141-145 Ngan Shing st, City One-Shatin");
        card.setUserId("1");
        card.setImageUri("https://res.cloudinary.com/dvfyipg5k/image/upload/v1557199383/CardWhere/j8ljeyg70q58qgauvxlg.jpg");
        card.setLatitude(0);
        card.setLongitude(0);

        assertEquals(true, cardController.addCard(card));
    }

    @Test
    public void testGetCards() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final ArrayList<CardBean> cardsList = new ArrayList<>();

        CardController cardController = new CardController(context);

        cardController.getCards("1", new CallBack() {
            @Override
            public void onSuccess(ArrayList<CardBean> cards) {
                cardId = cards.get(0).getCardId();
                cardsList.addAll(cards);
                latch.countDown();
            }

            @Override
            public void onFail(String msg) {
                fail();
            }
        });
        latch.await();
        assertEquals(true, cardsList.size()>0);
    }

    @Test
    public void testDeleteCard() {
        CardController cardController = new CardController(context);
        assertEquals(true, cardController.deleteCard(cardId));
    }
}