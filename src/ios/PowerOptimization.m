// PowerOptimization.m — iOS no-op stub
// All Android-specific battery/power APIs return safe defaults on iOS.
// Boolean checks → false, diagnostic queries → empty, menus → success (no-op).

#import <Cordova/CDV.h>

@interface PowerOptimization : CDVPlugin
@end

@implementation PowerOptimization

#pragma mark - Boolean checks (no Android equivalent on iOS → return false)

- (void)IsIgnoringBatteryOptimizations:(CDVInvokedUrlCommand*)command {
    [self.commandDelegate sendPluginResult:
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:NO]
                                callbackId:command.callbackId];
}

- (void)IsBackgroundRestricted:(CDVInvokedUrlCommand*)command {
    [self.commandDelegate sendPluginResult:
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:NO]
                                callbackId:command.callbackId];
}

- (void)IsPowerSaveMode:(CDVInvokedUrlCommand*)command {
    // iOS Low Power Mode — map to the same semantics as the Android boolean.
    BOOL lowPower = NO;
    if (@available(iOS 9.0, *)) {
        lowPower = [NSProcessInfo processInfo].isLowPowerModeEnabled;
    }
    [self.commandDelegate sendPluginResult:
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:lowPower]
                                callbackId:command.callbackId];
}

- (void)IsIgnoringDataSaver:(CDVInvokedUrlCommand*)command {
    [self.commandDelegate sendPluginResult:
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:YES]
                                callbackId:command.callbackId];
}

#pragma mark - Settings / menu actions (no-op on iOS)

- (void)RequestOptimizations:(CDVInvokedUrlCommand*)command {
    [self.commandDelegate sendPluginResult:
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK]
                                callbackId:command.callbackId];
}

- (void)RequestOptimizationsMenu:(CDVInvokedUrlCommand*)command {
    [self.commandDelegate sendPluginResult:
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK]
                                callbackId:command.callbackId];
}

- (void)RequestDataSaverMenu:(CDVInvokedUrlCommand*)command {
    [self.commandDelegate sendPluginResult:
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK]
                                callbackId:command.callbackId];
}

#pragma mark - Protected-apps check (no OEM power manager on iOS)

- (void)HaveProtectedAppsCheck:(CDVInvokedUrlCommand*)command {
    NSDictionary *result = @{@"skip_message": @NO, @"found_intent": @NO};
    [self.commandDelegate sendPluginResult:
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result]
                                callbackId:command.callbackId];
}

- (void)ProtectedAppCheck:(CDVInvokedUrlCommand*)command {
    NSDictionary *result = @{@"skip_message": @NO, @"found_intent": @NO};
    [self.commandDelegate sendPluginResult:
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result]
                                callbackId:command.callbackId];
}

#pragma mark - Diagnostic actions (PO-2 / PO-3 / PO-4)

- (void)GetLastExitReasons:(CDVInvokedUrlCommand*)command {
    // No equivalent API on iOS; return empty array so callers can guard safely.
    [self.commandDelegate sendPluginResult:
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:@[]]
                                callbackId:command.callbackId];
}

- (void)GetMemoryInfo:(CDVInvokedUrlCommand*)command {
    NSDictionary *result = @{
        @"availMem":           @(-1),
        @"totalMem":           @(-1),
        @"threshold":          @(-1),
        @"lowMemory":          @NO,
        @"nativeHeapAllocated":@(-1),
        @"nativeHeapSize":     @(-1),
        @"javaHeapUsed":       @(-1),
        @"javaHeapMax":        @(-1),
        @"totalPss":           @(-1),
        @"platform":           @"ios"
    };
    [self.commandDelegate sendPluginResult:
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result]
                                callbackId:command.callbackId];
}

- (void)GetStandbyBucket:(CDVInvokedUrlCommand*)command {
    [self.commandDelegate sendPluginResult:
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"UNKNOWN"]
                                callbackId:command.callbackId];
}

#pragma mark - PO-9 auto-revoke / hibernation (Android-only; iOS no-op = whitelisted)

- (void)IsAutoRevokeWhitelisted:(CDVInvokedUrlCommand*)command {
    // iOS has no equivalent hibernation policy — report as whitelisted so
    // callers don't show an Android-only warning to iOS users.
    [self.commandDelegate sendPluginResult:
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:YES]
                                callbackId:command.callbackId];
}

- (void)RequestAutoRevokeWhitelist:(CDVInvokedUrlCommand*)command {
    [self.commandDelegate sendPluginResult:
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK]
                                callbackId:command.callbackId];
}

@end
