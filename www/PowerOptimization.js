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
            if (data == "true") resolve(true);
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