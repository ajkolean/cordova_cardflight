package org.weeels.plugins.cardflight;

import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getcardflight.models.Card;
import com.getcardflight.models.CardFlight;
import com.getcardflight.models.Reader;

import android.util.Log;

public class CDVCardFlight extends CordovaPlugin {

  private Reader reader;
  private CardFlightHandler handler;
  private CordovaInterface cdv;

  private CallbackContext cardReadCallbackContext;
  private CallbackContext readerAttachedCallbackContext;
  private CallbackContext readerConnectingCallbackContext;
  private CallbackContext readerDisconnectedCallbackContext;
  private CallbackContext readerConnectedCallbackContext;
  private CallbackContext onBeginSwipeCallbackContext;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    cdv = cordova;
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    boolean success = true;

    if (action.equals("authorizeCardFlightAccount")) {
      this.authorizeCardFlightAccount(args.getString(0), args.getString(1), callbackContext);
    } else if (action.equals("initializeReader")) {
      this.initializeReader(callbackContext);
    } else if (action.equals("onReaderAttached")) {
      this.onReaderAttached(callbackContext);
    } else if (action.equals("onReaderConnecting")) {
      this.onReaderConnecting(callbackContext);
    } else if (action.equals("onSwipeDetected")) {
      this.onSwipeDetected(callbackContext);
    } else if (action.equals("onBatteryLow")) {
      this.onBatteryLow(callbackContext);
    } else if (action.equals("onReaderDisconnected")) {
      this.onReaderDisconnected(callbackContext);
    } else if (action.equals("tokenizeLastSwipe")) {
      this.tokenizeCard(callbackContext);
    } else if (action.equals("onReaderConnected")) {
      this.onReaderConnected(callbackContext);
    } else if (action.equals("watchForSwipe")) {
      this.watchForSwipe(callbackContext);
    } else if (action.equals("onCardRead")) {
      this.onCardRead(callbackContext);
    } else {
      success = false;
    }
    
    return success;
  }

  private void authorizeCardFlightAccount(String apiToken, String stripeMerchantToken, CallbackContext callbackContext) {
    if (apiToken == null || stripeMerchantToken == null) {
      logError("Need to send both an api token and a stripe merchant token to authorize cardflight");
      callbackContext.error("Need to send both an api token and a stripe merchant token to authorize cardflight");
    }

    CardFlight.getInstance().setApiTokenAndAccountToken(apiToken, stripeMerchantToken);
    log("CardFlight authorized on this device");
    callbackContext.success("CardFlight authorized on this device");
  }

  private void initializeReader(final CallbackContext callbackContext) {
    // cdv.getThreadPool().execute(new Runnable() {
    //   public void run() {
    //     reader = new Reader(cdv.getActivity().getApplicationContext(), handler);
    //     callbackContext.success("CardFlight reader initialized");
    //     // reader = new Reader(this.cordova.getActivity().getApplicationContext(), handler, new AutoConfigHandler(callbackContext));
    //     // log("Reader AutoConfigHandler has cordova callback");
    //   }
    // });
    initializeReader();
    callbackContext.success("CardFlight reader initialized");
  }

  public void initializeReader(){
    log("CardFlight reader initializing");
    handler = new CardFlightHandler(this);
    reader = new Reader(cdv.getActivity().getApplicationContext(), handler);
  }

  private void watchForSwipe(CallbackContext callbackContext) {
    handler.resetCard();
    reader.beginSwipe();
    log("CardFlight reader awaiting swipe");
    callbackContext.success("CardFlight reader awaiting swipe");
  }

  public void onCardRead(CallbackContext callbackContext) {
    log("Setting onCardRead callback");
    cardReadCallbackContext = callbackContext;
  }

  public void onReaderAttached(CallbackContext callbackContext) {
    log("Setting onReaderAttached callback");
    readerAttachedCallbackContext = callbackContext;
  }

  public void onReaderConnecting(CallbackContext callbackContext) {
    log("Setting onReaderConnecting callback");
    readerConnectingCallbackContext = callbackContext;
  }

  public void onReaderDisconnected(CallbackContext callbackContext) {
    log("Setting onReaderDisconnected callback");
    readerDisconnectedCallbackContext = callbackContext;
  }

  public void onReaderConnected(CallbackContext callbackContext) {
    log("Setting onReaderConnected callback");
    readerConnectedCallbackContext = callbackContext;
  }

  public void onSwipeDetected(CallbackContext callbackContext) {
    log("Setting onSwipeDetected callback");
    onBeginSwipeCallbackContext = callbackContext;
  }

  public void onBatteryLow(CallbackContext callbackContext) {
    logError("onBatteryLow not supported by Android CardFlight SDK");
    callbackContext.error("Cannot use onBatteryLow on Android");
  }

  public void tokenizeCard(CallbackContext callbackContext) {
    log("tokenizing card");
    Card card = handler.getCard();
    
    if (card == null) {
      callbackContext.error("No card to tokenize");
    } else {
      TokenizationHandler tokenHandler = new TokenizationHandler(card, callbackContext);
      card.tokenize(tokenHandler, cordova.getActivity().getApplicationContext());
    }
  }

  public void cardReadCallback() {
    if (cardReadCallbackContext != null) {
      sendSuccessToCallback(cardReadCallbackContext, "Card read successfully");
    }
  }

  public void readerConnectingCallback() {
    if (readerConnectingCallbackContext != null) {
      sendSuccessToCallback(readerConnectingCallbackContext, "Reader is connecting");
    }
  }

  public void readerAttachedCallback() {
    if (readerAttachedCallbackContext != null) {
      sendSuccessToCallback(readerAttachedCallbackContext, "Reader attached");
    }

    if (readerConnectedCallbackContext != null) {
      sendSuccessToCallback(readerConnectedCallbackContext, "Reader attached");
    }
  }

  public void readerDisconnectedCallback() {
    if (readerDisconnectedCallbackContext != null) {
      sendSuccessToCallback(readerDisconnectedCallbackContext, "Reader disconnected");
    }
  }

  public void deviceBeginSwipe() {
    if (onBeginSwipeCallbackContext != null) {
      sendSuccessToCallback(onBeginSwipeCallbackContext, "Reader swipe begin");
    }
  }

  public void readerFail(String msg) {
    if (readerConnectingCallbackContext != null) {
      sendErrorToCallback(readerConnectingCallbackContext, "Error connecting: "+msg);
    }
  }

  private void sendSuccessToCallback(CallbackContext callbackContext, String message) {
    PluginResult result = new PluginResult(PluginResult.Status.OK, message);
    result.setKeepCallback(true);
    callbackContext.sendPluginResult(result);
  }

  private void sendErrorToCallback(CallbackContext callbackContext, String message) {
    PluginResult result = new PluginResult(PluginResult.Status.ERROR, message);
    result.setKeepCallback(true);
    callbackContext.sendPluginResult(result);
  }

  private void log(String s) {
    Log.i("CDVCardFlight", s);
  }

  private void logError(String s) {
    Log.e("CDVCardFlight", s);
  }
}