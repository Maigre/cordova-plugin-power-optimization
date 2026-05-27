var exec = require('cordova/exec');

var MODULE = "PowerOptimization";

// Promise-based wrappers keep the Cordova bridge consistent across the Flanerie
// diagnostics screens and onboarding checks.

var execute = function (function_name, arg0) {
    return new Promise(function (resolve, reject) {
        console.debug("PowerOptimization:execute", { MODULE: MODULE, function_name: function_name, arg: [arg0] })
        exec(resolve, reject, MODULE, function_name, [arg0]);
    });
}

var execute_boolean = function (function_name, arg0) {
    return new Promise(function (resolve, reject) {
        var success = function (data) {
            // Accept both proper JSON booleans (new) and legacy "true"/"false" strings
            if (data === true || data === "true") resolve(true);
            else resolve(false);
        }
        exec(success, reject, MODULE, function_name, [arg0]);
    });
}

exports.IsIgnoringBatteryOptimizations = function (arg0) {
    return execute_boolean('IsIgnoringBatteryOptimizations', arg0);
};

// API 28+: true if the user (or OEM policy) restricted the app's background
// activity in Settings — separate from the Doze whitelist. Returns false on
// older Android (the signal didn't exist). Field test 2026-05-18 traced the
// Samsung A41 mid-walk kills to this layer.
exports.IsBackgroundRestricted = function (arg0) {
    return execute_boolean('IsBackgroundRestricted', arg0);
};

// API 21+: true when phone-wide "Économiseur de batterie" is on. Surfaced
// as a SOFT warning in onboarding — degrades walk audio quality (background
// service throttling, timer coalescing) but the user may need it for battery.
exports.IsPowerSaveMode = function (arg0) {
    return execute_boolean('IsPowerSaveMode', arg0);
};

exports.RequestOptimizations = function (arg0) {
    return execute('RequestOptimizations', arg0);
};

exports.RequestOptimizationsMenu = function (arg0) {
    return execute('RequestOptimizationsMenu', arg0);
};

exports.IsIgnoringDataSaver = function (arg0) {
    return execute_boolean('IsIgnoringDataSaver', arg0);
};

exports.RequestDataSaverMenu = function (arg0) {
    return execute('RequestDataSaverMenu', arg0);
};

exports.HaveProtectedAppsCheck = function (arg0) {
    return execute('HaveProtectedAppsCheck', arg0);
};

exports.ProtectedAppCheck = function (arg0) {
    return execute('ProtectedAppCheck', arg0);
};

// PO-2: Returns up to 5 most recent process exit reasons (Android 11+ / API 30).
// Each entry: { reason, description, timestamp, importance, processName }.
// Returns [] on older Android and on iOS.
exports.GetLastExitReasons = function (arg0) {
    return execute('GetLastExitReasons', arg0);
};

// PO-3: Returns system and app-level memory stats.
// Fields: availMem, totalMem, threshold, lowMemory, nativeHeapAllocated,
// nativeHeapSize, javaHeapUsed, javaHeapMax, totalPss (bytes).
exports.GetMemoryInfo = function (arg0) {
    return execute('GetMemoryInfo', arg0);
};

// PO-4: Returns the app's standby bucket name (Android 9+ / API 28).
// One of: ACTIVE | WORKING_SET | FREQUENT | RARE | RESTRICTED | EXEMPTED | UNKNOWN.
// Buckets below WORKING_SET cause aggressive job/alarm deferral that can
// degrade background GPS and audio on long walks.
exports.GetStandbyBucket = function (arg0) {
    return execute('GetStandbyBucket', arg0);
};