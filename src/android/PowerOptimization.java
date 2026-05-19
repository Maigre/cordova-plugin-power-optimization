package cordova.plugin.PowerOptimization;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;

import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED;

/**
 * This class echoes a string called from JavaScript.
 */
public class PowerOptimization extends CordovaPlugin {

    public static final String TAG = "PowerOptimization";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Context context = cordova.getActivity().getApplicationContext();
        String packageName = context.getPackageName();
        Log.d(TAG, "Action: " + action + ", Package name: " + packageName);
        if (action.equals("IsIgnoringBatteryOptimizations")) {
            this.IsIgnoringBatteryOptimizations(context, packageName, callbackContext);
            return true;
        // Check if the app is allowed to do this https://developer.android.com/training/monitoring-device-state/doze-standby
        } else if (action.equals("RequestOptimizations")) {
            this.RequestOptimizations(context, packageName, callbackContext);
            return true;
        } else if (action.equals("RequestOptimizationsMenu")) {
            this.RequestOptimizationsMenu(context, packageName, callbackContext);
            return true;
        } else if (action.equals("IsBackgroundRestricted")) {
            this.IsBackgroundRestricted(context, callbackContext);
            return true;
        } else if (action.equals("IsPowerSaveMode")) {
            this.IsPowerSaveMode(context, callbackContext);
            return true;
        } else if (action.equals("IsIgnoringDataSaver")) {
            this.IsIgnoringDataSaver(context, packageName, callbackContext);
            return true;
        } else if (action.equals("RequestDataSaverMenu")) {
            this.RequestDataSaverMenu(context, packageName, callbackContext);
            return true;
        } else if (action.equals("HaveProtectedAppsCheck")) {
            Boolean force = args.optBoolean(0, false);
            ProtectedApps.HaveProtectedAppIntent(context, callbackContext);
            return true;
        } else if (action.equals("ProtectedAppCheck")) {
            Boolean force = args.optBoolean(0, false);
            ProtectedApps.ProtectedAppCheck(context, callbackContext, force);
            return true;
        }
        return false;
    }

    // Only allow android M or newest versions
    @TargetApi(Build.VERSION_CODES.M)
    public boolean IsIgnoringBatteryOptimizations(Context context, String packageName, CallbackContext callbackContext) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String message = "";
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (pm.isIgnoringBatteryOptimizations(packageName)) {
                    message ="true";
                }
                else
                {
                    message ="false";
                }
                callbackContext.success(message);
                return true;
            }
            else
            {
                callbackContext.error("BATTERY_OPTIMIZATIONS Not available.");
                return false;
            }
        } catch (Exception e) {
            callbackContext.error("IsIgnoringBatteryOptimizations: failed N/A");
            return false;
        }
    }

    // Only allow android M or newest versions
    @TargetApi(Build.VERSION_CODES.M)
    public boolean RequestOptimizations(Context context, String packageName, CallbackContext callbackContext) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + packageName));
                context.startActivity(intent);
                callbackContext.success();
                return true;
            }
            else
            {
                callbackContext.error("BATTERY_OPTIMIZATIONS Not available.");
                return false;
            }
        } catch (Exception e) {
            callbackContext.error("N/A");
            return false;
        }
    }

    // Only allow android M or newest versions.
    // Pre-fix bug: the `pm.isIgnoringBatteryOptimizations(packageName)` guard
    // was INVERTED — the settings page only opened when the app was already
    // whitelisted (i.e. when the user didn't need it). The action
    // ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS has no API restriction tied
    // to current whitelist state; the guard served no purpose. Removed so
    // the menu opens unconditionally on API 23+, which is what every caller
    // intended. The FlanerieAudioMap JS layer (P1.12) had been routing
    // around this via `GEO.showAppSettings()`; the workaround can be
    // unwound at the JS level whenever convenient.
    @TargetApi(Build.VERSION_CODES.M)
    public boolean RequestOptimizationsMenu(Context context, String packageName, CallbackContext callbackContext) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                callbackContext.success();
                return true;
            }
            else
            {
                callbackContext.error("BATTERY_OPTIMIZATIONS Not available.");
                return false;
            }
        } catch (Exception e) {
            callbackContext.error("RequestOptimizationsMenu: failed N/A");
            return false;
        }
    }

    // Returns "true" if the user (or an OEM policy) has set the app's
    // background activity to "Restricted" in Settings — separate from the
    // Doze whitelist (IsIgnoringBatteryOptimizations). On Samsung A41-class
    // devices this layer is what consistently killed the app mid-walk on
    // the 2026-05-18 field test; detecting it at onboarding lets us
    // hard-block before the walker is sent off.
    //
    // The underlying API is ActivityManager.isBackgroundRestricted(),
    // available from Android 9 / API 28 onwards. On older Android we have
    // no equivalent signal — return "false" so callers can fast-path.
    @TargetApi(Build.VERSION_CODES.P)
    public boolean IsBackgroundRestricted(Context context, CallbackContext callbackContext) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                if (am == null) {
                    callbackContext.success("false");
                    return true;
                }
                boolean restricted = am.isBackgroundRestricted();
                callbackContext.success(restricted ? "true" : "false");
                return true;
            } else {
                callbackContext.success("false");
                return true;
            }
        } catch (Exception e) {
            callbackContext.error("IsBackgroundRestricted: failed N/A");
            return false;
        }
    }

    // Returns "true" when phone-wide battery saver is active. Distinct from
    // app-level restrictions (IsIgnoringBatteryOptimizations / IsBackgroundRestricted)
    // — this is the global "Économiseur de batterie" toggle that the user
    // may flip when their phone is low on battery. Background-service
    // throttling and timer coalescing can degrade the walk audibly while
    // it's on. Surface as a SOFT WARNING (not a hard block) — the walker
    // may genuinely need to keep it on to finish the day.
    //
    // PowerManager.isPowerSaveMode() is available from API 21 / Lollipop.
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean IsPowerSaveMode(Context context, CallbackContext callbackContext) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (pm == null) {
                    callbackContext.success("false");
                    return true;
                }
                callbackContext.success(pm.isPowerSaveMode() ? "true" : "false");
                return true;
            } else {
                callbackContext.success("false");
                return true;
            }
        } catch (Exception e) {
            callbackContext.error("IsPowerSaveMode: failed N/A");
            return false;
        }
    }

    // Only allow android N or newest versions
    @TargetApi(Build.VERSION_CODES.N)
    public boolean IsIgnoringDataSaver(Context context, String packageName, CallbackContext callbackContext) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                String message = "";
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                switch (connMgr.getRestrictBackgroundStatus()) {
                    case RESTRICT_BACKGROUND_STATUS_ENABLED:
                        // The app is whitelisted. Wherever possible,
                        // the app should use less data in the foreground and background.
                        message = "false";
                        break;

                    case RESTRICT_BACKGROUND_STATUS_WHITELISTED:
                        // Background data usage is blocked for this app. Wherever possible,
                        // the app should also use less data in the foreground.
                        message = "true";
                        break;
                    case RESTRICT_BACKGROUND_STATUS_DISABLED:
                        // Data Saver is disabled. Since the device is connected to a
                        // metered network, the app should use less data wherever possible.
                        message = "true";
                        break;
                }
                callbackContext.success(message);
                return true;

            } else {
                callbackContext.error("DATA_SAVER Not available.");
                return false;
            }
        } catch (Exception e) {
            callbackContext.error("IsIgnoringDataSaver: failed N/A");
            return false;

        }
    }

    // Only allow android N or newest versions
    @TargetApi(Build.VERSION_CODES.N)
    public boolean RequestDataSaverMenu(Context context, String packageName, CallbackContext callbackContext) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent();
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                intent.setAction(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + packageName));
                context.startActivity(intent);

                callbackContext.success();
                return true;
            }else{
                callbackContext.error("DATA_SAVER Not available.");
                return false;
            }
        } catch (Exception e) {
            callbackContext.error("RequestDataSaverMenu failed: N/A");
            return false;
        }
    }

}