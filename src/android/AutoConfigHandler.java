package org.weeels.plugins.cardflight;

import org.apache.cordova.CallbackContext;
import com.getcardflight.interfaces.*;
import android.util.Log;

public class AutoConfigHandler implements CardFlightAutoConfigHandler {

  private CallbackContext callbackContext;

  public AutoConfigHandler(CallbackContext ctx) {
    callbackContext = ctx;
  }

  @Override
  public void autoConfigProgressUpdate(int progress) {
    log("AutoConfig percent complete: "+progress+"%");
  }

  @Override
  public void autoConfigFinished() {
    log("AutoConfig successful");
    callbackContext.success("Initialization success");
  }

  @Override
  public void autoConfigFailed() {
    logError("AutoConfig failed");
    callbackContext.error("Initialization failed: AutoConfig failed");
  }

  private void log(String s) {
    Log.i("CDVCardFlightAutoConfigHandler", s);
  }

  private void logError(String s) {
    Log.e("CDVCardFlightAutoConfigHandler", s);
  }
}