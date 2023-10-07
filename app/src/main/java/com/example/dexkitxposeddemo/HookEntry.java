package com.example.dexkitxposeddemo;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import io.luckypray.dexkit.DexKitBridge;
import io.luckypray.dexkit.builder.BatchFindArgs;
import io.luckypray.dexkit.descriptor.member.DexClassDescriptor;
import io.luckypray.dexkit.descriptor.member.DexMethodDescriptor;
import io.luckypray.dexkit.enums.MatchType;

import java.util.HashSet;

public class HookEntry implements IXposedHookLoadPackage {
    Set<String> check_info = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("method1234")));
    public static final String TAG = "TEST##cxa_dexkit";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        hookXiaoxi(loadPackageParam);

//        if (!loadPackageParam.processName.equals("com.example.cvc")) {
//            return;
//        }
//        Class<?> c1 = XposedHelpers.findClassIfExists(
//                "com.example.cvc.TroopFileTransferManager",
//                loadPackageParam.classLoader
//        );
//        Object obj1 = XposedHelpers.newInstance(c1);
//        Log.d(TAG, "1====>" + String.valueOf(obj1));
//
////        Class<?> c = XposedHelpers.findClassIfExists(
////                "com.example.cvc.TroopFileTransferManager$Item",
////                loadPackageParam.classLoader
////        );
////        Log.d(TAG, "1.5=====>" + String.valueOf(c));
////        Object obj = XposedHelpers.newInstance(c);
////        Log.d(TAG, "2=====>" + String.valueOf(obj));
////        vipHook(loadPackageParam);
//
//        Class<?> c2 = XposedHelpers.findClassIfExists(
//                "com.example.cvc.TroopFileTransferManager$Item",
//                loadPackageParam.classLoader
//        );
//        if (c2 != null) {
//            XposedHelpers.findAndHookMethod(
//                    "com.example.cvc.MainActivity",
//                    loadPackageParam.classLoader,
//                    "SendRequest",
//                    c2,
//                    new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            // 修改参数
//                            Object item = param.args[0];
//                            XposedHelpers.setObjectField(item, "FileName", "new_file_name.txt");
//                        }
//                    }
//            );
//        } else {
//            Log.e(TAG, "2====>" + String.valueOf(c2));
//        }
//
//        XposedHelpers.findAndHookMethod(
//                "com.example.cvc.MainActivity",
//                loadPackageParam.classLoader,
//                "onCreate",
//                Bundle.class,
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        // 获取Activity实例
//                        Activity activity = (Activity) param.thisObject;
//                        // 获取TextView并修改文本
//                        TextView textView = (TextView) activity.findViewById(
//                                activity.getResources().getIdentifier(
//                                        "tvMain", "id", loadPackageParam.packageName
//                                )
//                        );
//                        if (textView != null) {
//                            textView.setText("Hello Hook");
//                        }
//                    }
//                }
//        );
    }

    public void vipHook(XC_LoadPackage.LoadPackageParam loadPackageParam) throws NoSuchMethodException {
        System.loadLibrary("dexkit");
        String apkPath = loadPackageParam.appInfo.sourceDir;
        Set<String> check_info_class = new HashSet<>();
        check_info_class.add("myClass");
        check_info_class.add("thisis");
        try (DexKitBridge bridge = DexKitBridge.create(apkPath)) {
            if (bridge == null) {
                return;
            }
            Map<String, List<DexMethodDescriptor>> resultMap =
                    bridge.batchFindMethodsUsingStrings(
                            BatchFindArgs.builder()
                                    .addQuery("VipCheckUtil_method", check_info)
                                    .matchType(MatchType.CONTAINS)
                                    .build()
                    );
            Map<String, List<DexClassDescriptor>> resultMap2 = bridge.batchFindClassesUsingStrings(
                    BatchFindArgs.builder()
                            .addQuery("VipCheckUtil_class", check_info_class)
                            .matchType(MatchType.SIMILAR_REGEX)
                            .build()
            );

            List<DexMethodDescriptor> result = Objects.requireNonNull(resultMap.get("VipCheckUtil_method"));
            Log.d(TAG, String.format("VipCheckUtil_method结果数:%d", result.size()));
            assert result.size() == 1;

            for (DexMethodDescriptor descriptor : result) {
                Method isVipMethod = descriptor.getMethodInstance(loadPackageParam.classLoader);
                Log.d(TAG, String.format("发现方法:%s", isVipMethod));
                XposedBridge.hookMethod(isVipMethod, XC_MethodReplacement.returnConstant(true));
            }

            List<DexClassDescriptor> resultClass = Objects.requireNonNull(resultMap2.get("VipCheckUtil_class"));
            Log.d(TAG, String.format("VipCheckUtil_class结果数:%d", result.size()));
            assert resultClass.size() == 1;

            for (DexClassDescriptor descriptor : resultClass) {
                Log.d(TAG, String.format("发现类:%s", descriptor.getName()));
            }


        }
    }

    public enum LoginType {
        WX_LOGIN,
        SMS_LOGIN,
        PWD_LOGIN
    }

    /**
     * hook小溪
     *
     * @param loadPackageParam
     * @throws NoSuchMethodException
     */
    public void hookXiaoxi(XC_LoadPackage.LoadPackageParam loadPackageParam) throws NoSuchMethodException {
        Log.e(TAG, "这里开始劫持了..." + loadPackageParam.processName);

        if (!loadPackageParam.processName.equals("com.isy.gcdy")) {
            return;
        }

        Class clazz = XposedHelpers.findClass("com.isy.gcdy.ui.LoginActivity", loadPackageParam.classLoader);
        for (Method method : clazz.getDeclaredMethods()) {
            Log.e(TAG, "Method name: " + method.getName());
            for (Class paramType : method.getParameterTypes()) {
                Log.e(TAG, "Parameter type: " + paramType.getName());
            }
        }


//        可以劫持onCreate
//        Class<?> loginTypeClass = XposedHelpers.findClass("com.isy.gcdy.ui.LoginActivity$LoginType", loadPackageParam.classLoader);
//        XposedHelpers.findAndHookMethod(
//                "com.isy.gcdy.ui.LoginActivity",
//                loadPackageParam.classLoader,
//                "onCreate",
//                Bundle.class,
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        Log.e(TAG, "这里开始劫持了...1");
//                        // 获取Activity实例
//                        Activity activity = (Activity) param.thisObject;
//                        // 获取editPwd并修改inputType
//                        EditText editPwd = (EditText) activity.findViewById(
//                                activity.getResources().getIdentifier(
//                                        "edit_input_pwd_or_code", "id", loadPackageParam.packageName
//                                )
//                        );
//                        Log.e(TAG, "这里开始劫持了...2");
//                        if (editPwd != null) {
//                            editPwd.setRawInputType(InputType.TYPE_CLASS_TEXT);
//                            editPwd.setTransformationMethod(null);
//                            Log.e(TAG, "这里开始劫持了...3");
//                        }
//                        Log.e(TAG, "这里开始劫持了...4");
//                    }
//                }
//        );

//        XposedHelpers.findAndHookMethod(
//                "com.isy.gcdy.ui.LoginActivity",
//                loadPackageParam.classLoader,
//                "doSwitchLoginType",
//                LoginType.class,
//                View.class,
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        Log.e(TAG, "这里开始劫持了...1");
//                        // 获取Activity实例
//                        Activity activity = (Activity) param.thisObject;
//                        // 获取editPwd并修改inputType
//                        EditText editPwd = (EditText) activity.findViewById(
//                                activity.getResources().getIdentifier(
//                                        "edit_input_pwd_or_code", "id", loadPackageParam.packageName
//                                )
//                        );
//                        Log.e(TAG, "这里开始劫持了...2");
//                        if (editPwd != null) {
//                            editPwd.setRawInputType(InputType.TYPE_CLASS_TEXT);
//                            editPwd.setTransformationMethod(null);
//                            Log.e(TAG, "这里开始劫持了...3");
//                        }
//                        Log.e(TAG, "这里开始劫持了...4");
//                    }
//                }
//        );

        XposedHelpers.findAndHookMethod(
                "com.isy.gcdy.ui.LoginActivity",
                loadPackageParam.classLoader,
                "doSwitchLoginType",
                XposedHelpers.findClass("com.isy.gcdy.ui.LoginActivity$LoginType", loadPackageParam.classLoader),
                View.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.e(TAG, "doSwitchLoginType is called");
                        Log.e(TAG, "这里开始劫持了...1");
                        // 获取Activity实例
                        Activity activity = (Activity) param.thisObject;
                        // 获取editPwd并修改inputType
                        EditText editPwd = (EditText) activity.findViewById(
                                activity.getResources().getIdentifier(
                                        "edit_input_pwd_or_code", "id", loadPackageParam.packageName
                                )
                        );
                        Log.e(TAG, "这里开始劫持了...2");
                        if (editPwd != null) {
                            editPwd.setRawInputType(InputType.TYPE_CLASS_TEXT);
                            editPwd.setTransformationMethod(null);
                            Log.e(TAG, "这里开始劫持了...3");
                        }
                        Log.e(TAG, "这里开始劫持了...4");
                    }
                }
        );

    }
}
