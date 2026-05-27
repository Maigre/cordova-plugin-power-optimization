package cordova.plugin.PowerOptimization;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.Arrays;
import java.util.List;

public class Constants {

    private Context context;

    // MIUI
    private static final String MIUI_ACTION_POWER_SAVE_EXTRA_NAME = "package_name";
    private static final String MIUI_ACTION_POWER_SAVE_EXTRA_LABEL = "package_label";
    private static final String[] MIUI_ACTION_POWERSAVE = {"com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"};

    // MIUI / Xiaomi autostart manager — the dedicated "Autostart" toggle that must be
    // explicitly enabled for Xiaomi/MIUI devices to survive OEM background-kill;
    // this is distinct from the power-keeper entry above.
    private static final String MIUI_AUTOSTART_PKG = "com.miui.securitycenter";
    private static final String MIUI_AUTOSTART_CLS = "com.miui.permcenter.autostart.AutoStartManagementActivity";

    // SAMSUNG crash "com.samsung.android.lool","com.samsung.android.sm.ui.battery.AppSleepListActivity"
    private static final String SAMSUNG_SYSTEMMANAGER_POWERSAVING_ACTION = "com.samsung.android.sm.ACTION_BATTERY";
    private static final String SAMSUNG_SYSTEMMANAGER_NOTIFICATION_ACTION = "com.samsung.android.sm.ACTION_SM_NOTIFICATION_SETTING";
    // SAMSUNG ANDROID 7.0
    private static final String SAMSUNG_SYSTEMMANAGER_POWERSAVING_PACKAGE_V3 = "com.samsung.android.lool";
    private static final String SAMSUNG_SYSTEMMANAGER_POWERSAVING_PACKAGE_V3_ACTIVITY = "com.samsung.android.sm.ui.battery.BatteryActivity";

    // SAMSUNG ANDROID 6.0
    private static final String SAMSUNG_SYSTEMMANAGER_POWERSAVING_PACKAGE_V2 = "com.samsung.android.sm_cn";
    private static final String SAMSUNG_SYSTEMMANAGER_POWERSAVING_PACKAGE_V2_ACTIVITY = "com.samsung.android.sm.ui.battery.BatteryActivity";

    // SAMSUNG ANDROID 5.0/5.1
    private static final String SAMSUNG_SYSTEMMANAGER_POWERSAVING_PACKAGE_V1 = "com.samsung.android.sm";
    private static final String SAMSUNG_SYSTEMMANAGER_POWERSAVING_PACKAGE_V1_ACTIVITY = "com.samsung.android.sm.ui.battery.BatteryActivity";

    // HUAWEI
    private static final String HUAWEI_ACTION_POWERSAVING = "huawei.intent.action.HSM_PROTECTED_APPS";
    private static final String HUAWEI_COMPONENT_POWERSAVING_PKG = "com.huawei.systemmanager";
    private static final String HUAWEI_COMPONENT_POWERSAVING_CLS = "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity";

    // LETV
    private static final String LETV_ACTION_POWERSAVING_PKG = "com.letv.android.letvsafe";
    private static final String LETV_ACTION_POWERSAVING_CLS = "com.letv.android.letvsafe.BackgroundAppManageActivity";

    // MEIZU

    private static final String MEIZU_POWERSAVING_ACTION = "com.meizu.power.PowerAppKilledNotification";
    private static final String MEIZU_DEFAULT_PACKAGE = "com.meizu.safe";
    private static final String MEIZU_POWERSAVING_ACTIVITY_V2_2 = "com.meizu.safe.cleaner.RubbishCleanMainActivity";
    private static final String MEIZU_POWERSAVING_ACTIVITY_V3_4 = "com.meizu.safe.powerui.AppPowerManagerActivity";
    private static final String MEIZU_POWERSAVING_ACTIVITY_V3_7 = "com.meizu.safe.powerui.PowerAppPermissionActivity";

    // ─── Modern OEM intents (added 2026-05-19) ─────────────────────────────
    // Sourced from dontkillmyapp.com; covers the most common One UI 4+,
    // OnePlus, Oppo/Realme, Vivo, Honor activities. Intents that don't
    // resolve on the current device are filtered out by
    // ProtectedApps.HaveProtectedAppIntent() at runtime, so it's safe to
    // include all variants.

    // SAMSUNG One UI 4+ (sleeping-apps / app-sleep activities)
    private static final String SAMSUNG_ONEUI4_SLEEPING_APPS_PKG = "com.samsung.android.lool";
    private static final String SAMSUNG_ONEUI4_SLEEPING_APPS_CLS = "com.samsung.android.sm.battery.ui.deep.AppSleepListActivity";
    // Newer One UI variant
    private static final String SAMSUNG_ONEUI5_SLEEPING_APPS_PKG = "com.samsung.android.lool";
    private static final String SAMSUNG_ONEUI5_SLEEPING_APPS_CLS = "com.samsung.android.sm.battery.app.power.AppSleepingActivity";

    // OnePlus (ChainLaunch / autostart manager)
    private static final String ONEPLUS_PKG = "com.oneplus.security";
    private static final String ONEPLUS_CLS = "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity";

    // OPPO / Realme (ColorOS startup manager)
    private static final String OPPO_PKG = "com.coloros.safecenter";
    private static final String OPPO_CLS = "com.coloros.safecenter.startupapp.StartupAppListActivity";

    // Vivo (FunTouch / OriginOS bg-startup manager)
    private static final String VIVO_PKG = "com.vivo.permissionmanager";
    private static final String VIVO_CLS = "com.vivo.permissionmanager.activity.BgStartUpManagerActivity";

    // Honor MagicOS (post-Huawei split)
    private static final String HONOR_PKG = "com.hihonor.systemmanager";
    private static final String HONOR_CLS = "com.hihonor.systemmanager.startupmgr.ui.StartupNormalAppListActivity";

    public List<Intent> powerManagerIntents;

    public Constants(Context context){
        this.context = context;
        populateList();
    }


    private void populateList(){

        // Xiaomi intents
        Intent xiomi_1 = new Intent();
        xiomi_1.setComponent(new ComponentName(MIUI_ACTION_POWERSAVE[0], MIUI_ACTION_POWERSAVE[1]));
        xiomi_1.putExtra(MIUI_ACTION_POWER_SAVE_EXTRA_NAME, context.getPackageName());
        xiomi_1.putExtra(MIUI_ACTION_POWER_SAVE_EXTRA_LABEL, getApplicationName());

        // Xiaomi autostart manager — the "Autostart" toggle that guards against OEM kill
        // on MIUI devices; separate from the power-keeper activity above.
        Intent xiaomi_autostart = new Intent();
        xiaomi_autostart.setComponent(new ComponentName(MIUI_AUTOSTART_PKG, MIUI_AUTOSTART_CLS));
        xiaomi_autostart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Samsung intents
        Intent samsung_1 = new Intent();
        samsung_1.setAction(SAMSUNG_SYSTEMMANAGER_POWERSAVING_ACTION);
        samsung_1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent samsung_2 = new Intent();
        samsung_2.setComponent(new ComponentName(SAMSUNG_SYSTEMMANAGER_POWERSAVING_PACKAGE_V3, SAMSUNG_SYSTEMMANAGER_POWERSAVING_PACKAGE_V3_ACTIVITY));
        samsung_2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent samsung_3 = new Intent();
        samsung_3.setComponent(new ComponentName(SAMSUNG_SYSTEMMANAGER_POWERSAVING_PACKAGE_V2, SAMSUNG_SYSTEMMANAGER_POWERSAVING_PACKAGE_V2_ACTIVITY));
        samsung_3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent samsung_4 = new Intent();
        samsung_4.setComponent(new ComponentName(SAMSUNG_SYSTEMMANAGER_POWERSAVING_PACKAGE_V1, SAMSUNG_SYSTEMMANAGER_POWERSAVING_PACKAGE_V1_ACTIVITY));
        samsung_4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Huawei
        Intent huawei_1 = new Intent();
        huawei_1.setAction(HUAWEI_ACTION_POWERSAVING);
        Intent huawei_2 = new Intent();
        huawei_2.setComponent(new ComponentName(HUAWEI_COMPONENT_POWERSAVING_PKG, HUAWEI_COMPONENT_POWERSAVING_CLS));

        // LeTV
        // TODO Test it
        Intent letv_1 = new Intent();
        letv_1.setComponent(new ComponentName(LETV_ACTION_POWERSAVING_PKG, LETV_ACTION_POWERSAVING_CLS));

        // Meizu
        // TODO Test it
        Intent meizu_1 = new Intent();
        meizu_1.setAction(MEIZU_POWERSAVING_ACTION);
        Intent meizu_2 = new Intent();
        meizu_2.setClassName(MEIZU_DEFAULT_PACKAGE, MEIZU_POWERSAVING_ACTIVITY_V2_2);
        Intent meizu_3 = new Intent();
        meizu_3.setClassName(MEIZU_DEFAULT_PACKAGE, MEIZU_POWERSAVING_ACTIVITY_V3_4);
        Intent meizu_4 = new Intent();
        meizu_4.setClassName(MEIZU_DEFAULT_PACKAGE, MEIZU_POWERSAVING_ACTIVITY_V3_7);

        // TODO ASUS, Elephone, Sony STAMINA, Huawei Phone manager (EMUI 5)

        // ─── Modern OEM intents (2026-05-19) ───────────────────────────────
        // Samsung One UI 4+ sleeping-apps (legacy AppSleepListActivity)
        Intent samsung_oneui4 = new Intent();
        samsung_oneui4.setComponent(new ComponentName(SAMSUNG_ONEUI4_SLEEPING_APPS_PKG, SAMSUNG_ONEUI4_SLEEPING_APPS_CLS));
        samsung_oneui4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Samsung One UI 5+ sleeping-apps (newer AppSleepingActivity)
        Intent samsung_oneui5 = new Intent();
        samsung_oneui5.setComponent(new ComponentName(SAMSUNG_ONEUI5_SLEEPING_APPS_PKG, SAMSUNG_ONEUI5_SLEEPING_APPS_CLS));
        samsung_oneui5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // OnePlus chain-launch / autostart manager
        Intent oneplus_1 = new Intent();
        oneplus_1.setComponent(new ComponentName(ONEPLUS_PKG, ONEPLUS_CLS));
        oneplus_1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // OPPO / Realme ColorOS startup manager
        Intent oppo_1 = new Intent();
        oppo_1.setComponent(new ComponentName(OPPO_PKG, OPPO_CLS));
        oppo_1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Vivo FunTouch / OriginOS background-startup manager
        Intent vivo_1 = new Intent();
        vivo_1.setComponent(new ComponentName(VIVO_PKG, VIVO_CLS));
        vivo_1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Honor MagicOS (post-Huawei split)
        Intent honor_1 = new Intent();
        honor_1.setComponent(new ComponentName(HONOR_PKG, HONOR_CLS));
        honor_1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        powerManagerIntents = Arrays.asList(
                xiomi_1,
                xiaomi_autostart,
                samsung_1,
                samsung_2,
                samsung_3,
                samsung_4,
                samsung_oneui4,
                samsung_oneui5,
                huawei_1,
                huawei_2,
                letv_1,
                meizu_1,
                meizu_2,
                meizu_3,
                meizu_4,
                oneplus_1,
                oppo_1,
                vivo_1,
                honor_1
        );
    }

    public List<Intent> getPowermanagerIntents(){
        return powerManagerIntents;
    }


    public String getApplicationName() {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
    }

}