package org.weeels.plugins.cardflight;

import org.apache.cordova.CallbackContext;
import com.getcardflight.interfaces.*;
import com.getcardflight.models.Card;

import android.util.Log;

public class TokenizationHandler implements CardFlightTokenizationHandler {

  private CallbackContext tokenizeCallbackContext;
  private Card card;

  public TokenizationHandler(Card c, CallbackContext ctx) {
    card = c;
    tokenizeCallbackContext = ctx;
  }

  @Override
  public void tokenizationSuccessful(String result){
    // if (card.token == null) {
    if (result == null) {
      log("card tokenization no token result");
      tokenizeCallbackContext.error("Got tokenization success callback but token was null");
    } else {
      log("card tokenization success");
      // tokenizeCallbackContext.success(card.token);
      tokenizeCallbackContext.success(result);
    }
  }

  @Override
  public void tokenizationFailed(String error, int errorCode) {
    logError(error);
    tokenizeCallbackContext.error(error);
  }

  private void log(String s) {
    Log.i("CDVCardFlightTokenizationHandler", s);
  }

  private void logError(String s) {
    Log.e("CDVCardFlightTokenizationHandler", s);
  }
}