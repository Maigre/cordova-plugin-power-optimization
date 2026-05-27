package cordova.plugin.PowerOptimization;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ApplicationExitInfo;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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
        } else if (action.equals("GetLastExitReasons")) {
            this.GetLastExitReasons(context, packageName, callbackContext);
            return true;
        } else if (action.equals("GetMemoryInfo")) {
            this.GetMemoryInfo(context, callbackContext);
            return true;
        } else if (action.equals("GetStandbyBucket")) {
            this.GetStandbyBucket(context, callbackContext);
            return true;
        } else if (action.equals("IsAutoRevokeWhitelisted")) {
            this.IsAutoRevokeWhitelisted(context, callbackContext);
            return true;
        } else if (action.equals("RequestAutoRevokeWhitelist")) {
            this.RequestAutoRevokeWhitelist(context, packageName, callbackContext);
            return true;
        }
        return false;
    }

    /**
     * PO-9: Auto-revoke / hibernation watch (Android 11+, API 30).
     * Returns true if the app is on the auto-revoke whitelist (i.e. the OS
     * will NOT auto-strip its permissions or hibernate it after long idle).
     * On Android &lt; 11 returns true (no hibernation policy exists, so the
     * app is effectively whitelisted).
     */
    @TargetApi(Build.VERSION_CODES.R)
    public boolean IsAutoRevokeWhitelisted(Context context, CallbackContext callbackContext) {
        try {
            boolean whitelisted = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                android.content.pm.PackageManager pm = context.getPackageManager();
                whitelisted = pm.isAutoRevokeWhitelisted();
            }
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, whitelisted);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        } catch (Exception e) {
            callbackContext.error("IsAutoRevokeWhitelisted: " + e.getMessage());
            return false;
        }
    }

    /**
     * PO-9: Opens the app-details settings page so the user can toggle the
     * "Remove permissions and free up space" option off (Android 11+).
     * No deep-link to that specific toggle is part of the public API; the
     * app-details page is the closest the platform exposes.
     */
    @TargetApi(Build.VERSION_CODES.R)
    public boolean RequestAutoRevokeWhitelist(Context context, String packageName, CallbackContext callbackContext) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + packageName));
                context.startActivity(intent);
                callbackContext.success();
                return true;
            } else {
                callbackContext.error("AUTO_REVOKE not available before Android 11");
                return false;
            }
        } catch (Exception e) {
            callbackContext.error("RequestAutoRevokeWhitelist: " + e.getMessage());
            return false;
        }
    }

    // Only allow android M or newest versions
    @TargetApi(Build.VERSION_CODES.M)
    public boolean IsIgnoringBatteryOptimizations(Context context, String packageName, CallbackContext callbackContext) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                boolean ignoring = pm.isIgnoringBatteryOptimizations(packageName);
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, ignoring);
                callbackContext.sendPluginResult(pluginResult);
                return true;
            } else {
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
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
                    return true;
                }
                boolean restricted = am.isBackgroundRestricted();
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, restricted));
                return true;
            } else {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
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
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
                    return true;
                }
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, pm.isPowerSaveMode()));
                return true;
            } else {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
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
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                boolean isIgnoring;
                switch (connMgr.getRestrictBackgroundStatus()) {
                    case RESTRICT_BACKGROUND_STATUS_ENABLED:
                        // Data Saver on, app is NOT whitelisted — background data is restricted.
                        isIgnoring = false;
                        break;
                    case RESTRICT_BACKGROUND_STATUS_WHITELISTED:
                        // Data Saver on, app IS whitelisted — background data is allowed.
                        isIgnoring = true;
                        break;
                    case RESTRICT_BACKGROUND_STATUS_DISABLED:
                        // Data Saver is off — no restriction.
                        isIgnoring = true;
                        break;
                    default:
                        isIgnoring = false;
                }
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, isIgnoring));
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

    /**
     * PO-2: Returns up to 5 most recent process exit reasons (API 30+).
     * Answers why the app was killed — low memory, ANR, OEM-kill, etc.
     * Returns an empty array on Android < 11.
     */
    @TargetApi(Build.VERSION_CODES.R)
    public boolean GetLastExitReasons(Context context, String packageName, CallbackContext callbackContext) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ApplicationExitInfo> reasons = am.getHistoricalProcessExitReasons(packageName, 0, 5);
                JSONArray result = new JSONArray();
                for (ApplicationExitInfo info : reasons) {
                    JSONObject item = new JSONObject();
                    item.put("reason", info.getReason());
                    item.put("description", exitReasonString(info.getReason()));
                    item.put("timestamp", info.getTimestamp());
                    item.put("importance", info.getImportance());
                    item.put("processName", info.getProcessName());
                    result.put(item);
                }
                callbackContext.success(result);
                return true;
            } else {
                callbackContext.success(new JSONArray());
                return true;
            }
        } catch (Exception e) {
            callbackContext.error("GetLastExitReasons: " + e.getMessage());
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.R)
    private String exitReasonString(int reason) {
        switch (reason) {
            case ApplicationExitInfo.REASON_ANR:                       return "ANR";
            case ApplicationExitInfo.REASON_CRASH:                     return "CRASH";
            case ApplicationExitInfo.REASON_CRASH_NATIVE:              return "CRASH_NATIVE";
            case ApplicationExitInfo.REASON_DEPENDENCY_DIED:           return "DEPENDENCY_DIED";
            case ApplicationExitInfo.REASON_EXCESSIVE_RESOURCE_USAGE:  return "EXCESSIVE_RESOURCE_USAGE";
            case ApplicationExitInfo.REASON_EXIT_SELF:                 return "EXIT_SELF";
            case ApplicationExitInfo.REASON_INITIALIZATION_FAILURE:    return "INITIALIZATION_FAILURE";
            case ApplicationExitInfo.REASON_LOW_MEMORY:                return "LOW_MEMORY";
            case ApplicationExitInfo.REASON_OTHER:                     return "OTHER";
            case ApplicationExitInfo.REASON_PERMISSION_CHANGE:         return "PERMISSION_CHANGE";
            case ApplicationExitInfo.REASON_SIGNALED:                  return "SIGNALED";
            case ApplicationExitInfo.REASON_UNKNOWN:                   return "UNKNOWN";
            case ApplicationExitInfo.REASON_USER_REQUESTED:            return "USER_REQUESTED";
            case ApplicationExitInfo.REASON_USER_STOPPED:              return "USER_STOPPED";
            default:                                                    return "reason_" + reason;
        }
    }

    /**
     * PO-3: Returns system and app-level memory statistics.
     * Useful for correlating OOM kills with available memory at walk time.
     */
    public boolean GetMemoryInfo(Context context, CallbackContext callbackContext) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(memInfo);

            android.os.Debug.MemoryInfo debugMemInfo = new android.os.Debug.MemoryInfo();
            android.os.Debug.getMemoryInfo(debugMemInfo);

            JSONObject result = new JSONObject();
            result.put("availMem",            memInfo.availMem);
            result.put("totalMem",            memInfo.totalMem);
            result.put("threshold",           memInfo.threshold);
            result.put("lowMemory",           memInfo.lowMemory);
            result.put("nativeHeapAllocated", android.os.Debug.getNativeHeapAllocatedSize());
            result.put("nativeHeapSize",      android.os.Debug.getNativeHeapSize());
            result.put("javaHeapUsed",        Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
            result.put("javaHeapMax",         Runtime.getRuntime().maxMemory());
            result.put("totalPss",            (long) debugMemInfo.getTotalPss() * 1024L);
            callbackContext.success(result);
            return true;
        } catch (Exception e) {
            callbackContext.error("GetMemoryInfo: " + e.getMessage());
            return false;
        }
    }

    /**
     * PO-4: Returns the app's standby bucket name (API 28+).
     * ACTIVE / WORKING_SET / FREQUENT / RARE / RESTRICTED / EXEMPTED.
     * Buckets below WORKING_SET cause aggressive job / alarm deferral that
     * can degrade background GPS and audio on long walks.
     */
    @TargetApi(Build.VERSION_CODES.P)
    public boolean GetStandbyBucket(Context context, CallbackContext callbackContext) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                android.app.usage.UsageStatsManager usm =
                    (android.app.usage.UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                if (usm == null) {
                    callbackContext.success("UNKNOWN");
                    return true;
                }
                int bucket = usm.getAppStandbyBucket();
                callbackContext.success(standbyBucketString(bucket));
                return true;
            } else {
                callbackContext.success("UNKNOWN");
                return true;
            }
        } catch (Exception e) {
            callbackContext.error("GetStandbyBucket: " + e.getMessage());
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    private String standbyBucketString(int bucket) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // STANDBY_BUCKET_EXEMPTED (5) and STANDBY_BUCKET_RESTRICTED (45) added in API 30
            if (bucket == 5)  return "EXEMPTED";
            if (bucket == 45) return "RESTRICTED";
        }
        switch (bucket) {
            case android.app.usage.UsageStatsManager.STANDBY_BUCKET_ACTIVE:      return "ACTIVE";
            case android.app.usage.UsageStatsManager.STANDBY_BUCKET_WORKING_SET: return "WORKING_SET";
            case android.app.usage.UsageStatsManager.STANDBY_BUCKET_FREQUENT:    return "FREQUENT";
            case android.app.usage.UsageStatsManager.STANDBY_BUCKET_RARE:        return "RARE";
            default:                                                               return "bucket_" + bucket;
        }
    }

}