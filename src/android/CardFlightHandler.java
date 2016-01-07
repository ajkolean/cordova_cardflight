package org.weeels.plugins.cardflight;

import com.getcardflight.models.Card;
import com.getcardflight.interfaces.*;
import android.util.Log;

public class CardFlightHandler implements CardFlightDeviceHandler {
  
  private Card card;
  private CDVCardFlight parent;

  private boolean isConnected;

  public CardFlightHandler(CDVCardFlight p) {
    parent = p;
  }

  public void resetCard() {
    card = null;
  }
  
  public Card getCard(){
    return card;
  }

  @Override
  public void readerCardResponse(Card c) {
    log("readerCardResponse callback");
    card = c;
    parent.cardReadCallback();
  }

  @Override
  public void readerIsConnecting() {
    log("readerIsConnecting callback");
    parent.readerConnectingCallback();
  }

  @Override
  public void readerIsAttached() {
    log("readerIsAttached callback");
    isConnected = true;
    parent.readerAttachedCallback();
  }

  @Override
  public void readerIsDisconnected() {
    log("readerIsDisconnected callback");
    // if (isConnected) parent.initializeReader();
    isConnected = false;
    parent.readerDisconnectedCallback();
  }

  @Override
  public void deviceBeginSwipe() {
    log("deviceBeginSwipe callback");
    parent.deviceBeginSwipe();
  }

  @Override
  public void readerFail(String errorMessage, int errorCode) {
    logError("readerFail callback code:"+errorCode+", with message: " + errorMessage);
    // if (isConnected) parent.initializeReader();
    isConnected = false;
  }

  private void log(String s) {
    Log.i("CDVCardFlightDeviceHandler", s);
  }

  private void logError(String s) {
    Log.e("CDVCardFlightDeviceHandler", s);
  }
}