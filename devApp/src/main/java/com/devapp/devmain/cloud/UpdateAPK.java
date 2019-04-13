package com.devapp.devmain.cloud;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.devapp.devmain.encryption.AndroidXMLDcompress;
import com.devapp.devmain.json.JSONParser;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.services.DownloadManagerReceiver;
import com.devapp.devmain.user.RestartTabActivity;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.BuildConfig;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.jar.JarFile;

public class UpdateAPK {
    private static Context context;
    Long downLoadrReference;
    AmcuConfig amcuConfig;
    DownloadManager downloadmanager;
    BroadcastReceiver mDownloadCompletedReceiver = new DownloadManagerReceiver();
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(context, "No update available", Toast.LENGTH_LONG)
                    .show();
        }
    };

    public static void installDownload(String path, Context ctx) {
        try {
//		path = Environment.getExternalStorageDirectory().getAbsolutePath()
//				+ "/Download/smartAmcu_version-10.76.apk";
            if (path != null) {

                if (readManifestFileFromAPK(path, ctx)) {

                    // Util.setNonMarketAppEnable(ctx);
                    //To set the file path
                    Util.downLoadFilePath = path;
                    boolean isRooted = Util.checkForRootedTab();
                    if (isRooted) {
                        try {
                            Util.doCommandsForUpgradeAPK(ctx, Util.downLoadFilePath, FarmerScannerActivity.apkFromPendrive);

                        } catch (Exception e) {
                            e.printStackTrace();

                        } finally {
                            Intent intent = new Intent(ctx, RestartTabActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ctx.startActivity(intent);
                        }
                    } else {
                        installDownloadForUnrootedTab(Util.downLoadFilePath, ctx);
                    }


                } else {
                    Util.displayErrorToast("Invalid APK!, please try again", ctx);
                }

            } else {
                Util.displayErrorToast("APK path not found!, please try again", ctx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean readManifestFileFromAPK(String path, Context ctx) {

        try {
            JarFile jf = new JarFile(path);
            InputStream is = jf.getInputStream(jf.getEntry("AndroidManifest.xml"));
            byte[] xml = new byte[is.available()];
            int br = is.read(xml);
            String xmlData = AndroidXMLDcompress.decompressXML(xml);

            xmlData = xmlData.replace("resourceID ", "");
            String[] keys = StringUtils.substringsBetween(xmlData, " ", "=");
            HashMap<String, String> map = new LinkedHashMap<>();

            String[] values = StringUtils.substringsBetween(xmlData, "\"", "\"");

            String packageName = "", versionCode = "";
            for (int i = 0; i < values.length; i++) {
                map.put(keys[i], values[i]);
                if (keys[i].equalsIgnoreCase("versionCode")) {
                    versionCode = values[i];
                    versionCode = versionCode.replace("0x", "");
                }

                if (keys[i].equalsIgnoreCase("package")) {
                    packageName = values[i];
                }
            }
            return verifyTheAPK(packageName, versionCode, ctx);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean verifyTheAPK(String packageName, String apkVersionCode, Context ctx) throws IOException {
        int currentVersionCode = 0, apkVersionC = 0;
        try {
            currentVersionCode = BuildConfig.VERSION_CODE;
//            currentVersionCode = ctx.getPackageManager().getPackageInfo(
//                    ctx.getPackageName(), 0).versionCode;
            apkVersionC = Integer.parseInt(apkVersionCode, 16);
            // Util.displayErrorToast("Version"+" "+apkVersionC,ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (packageName.equalsIgnoreCase(SmartCCUtil.PACKAGE_NAME)
                && (currentVersionCode < apkVersionC)) {
            return true;
        } else {
            return false;
        }
    }

    public static void installDownloadForUnrootedTab(String path, Context ctx) {

        if (path != null) {
            Util.setNonMarketAppEnable(ctx);
            // intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.putExtra(Intent.EXTRA_ALLOW_REPLACE, true);
            intent.setDataAndType(Uri.fromFile(new File(path)),
                    "application/vnd.android.package-archive");
            intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
            // intent.setAction(Intent.ACTION_PACKAGE_INSTALL);
            //Removed because not supporting onActivityStartResult
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            if (FarmerScannerActivity.mActivity == null) {
                Intent farmerScannerIntent = new Intent(ctx, FarmerScannerActivity.class);
                farmerScannerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                farmerScannerIntent.putExtra("UpdateAPK", true);
                ctx.startActivity(farmerScannerIntent);
                //FarmerScannerActivity.mActivity.startActivityForResult(intent, 1);
            } else {
                FarmerScannerActivity.mActivity.startActivityForResult(intent, 1);
            }
        }
    }

    public void setContext(Context contextf, String url) {
        context = contextf;
        String servicestring = Context.DOWNLOAD_SERVICE;
        amcuConfig = AmcuConfig.getInstance();
        downloadmanager = (DownloadManager) context
                .getSystemService(servicestring);
        IntentFilter filter = new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        // context.registerReceiver(downloadReceiver, filter);

        LocalBroadcastManager.getInstance(context.getApplicationContext())
                .registerReceiver(mDownloadCompletedReceiver, filter);

        url = amcuConfig.getURLHeader() + amcuConfig.getServer() + url;
        url = url.replace(" ", "%20");
        Uri uri = Uri.parse(url);
        Request request = new Request(uri);
        try {
            if (url != null) {

                CookieManager cm = (CookieManager) CookieHandler.getDefault();
                if (cm != null) {
                    CookieStore cs = cm.getCookieStore();
                    List<HttpCookie> cookies = cs.getCookies();

                    if (cs != null) {
                        String jSessionId = JSONParser.getJsessionId(cookies);
                        if (jSessionId != null) {
                            request.addRequestHeader("Cookie", "JSESSIONID=" + jSessionId);
                        }
                    }
                }

                deleteDuplicateFile();

                request.setAllowedNetworkTypes(Request.NETWORK_WIFI
                        | Request.NETWORK_MOBILE);
                request.setAllowedOverRoaming(true);
                request.setTitle("smartAmcu Download");
                request.setDescription("smartAmcu update is downloading");
                request.setDestinationInExternalFilesDir(contextf, Environment
                                .getExternalStorageDirectory().getAbsolutePath(),
                        "smartAmcuUpdate.apk");
                downLoadrReference = downloadmanager.enqueue(request);
                amcuConfig.setDownloadId(downLoadrReference);
                amcuConfig.setAPKUri(url);

            }
        } catch (Exception e) {
            e.printStackTrace();
            if (downLoadrReference != null) {
                downloadmanager.remove(downLoadrReference);
                amcuConfig.setDownloadId(0);
                com.devapp.devmain.cloud.APKManager.apkDownloadInprogress = false;
            }

        }
    }

    public void setContextForCache(Context contextf, String url) {
        context = contextf;
        String servicestring = Context.DOWNLOAD_SERVICE;
        amcuConfig = AmcuConfig.getInstance();
        downloadmanager = (DownloadManager) context
                .getSystemService(servicestring);
        IntentFilter filter = new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        // context.registerReceiver(downloadReceiver, filter);

        LocalBroadcastManager.getInstance(context.getApplicationContext())
                .registerReceiver(mDownloadCompletedReceiver, filter);

        url = amcuConfig.getURLHeader() + amcuConfig.getServer() + url;
        url = url.replace(" ", "%20");
        Uri uri = Uri.parse(url);
        Request request = new Request(uri);
        try {
            if (url != null) {

                CookieManager cm = (CookieManager) CookieHandler.getDefault();
                if (cm != null) {
                    CookieStore cs = cm.getCookieStore();
                    List<HttpCookie> cookies = cs.getCookies();

                    if (cs != null) {
                        String jSessionId = JSONParser.getJsessionId(cookies);
                        if (jSessionId != null) {
                            request.addRequestHeader("Cookie", "JSESSIONID=" + jSessionId);
                        }
                    }
                }

                deleteDuplicateFileCache();

                request.setAllowedNetworkTypes(Request.NETWORK_WIFI
                        | Request.NETWORK_MOBILE);
                request.setAllowedOverRoaming(true);
                request.setTitle("Download trigger");
                request.setDescription("");
                request.setDestinationInExternalFilesDir(contextf, Environment
                                .getExternalStorageDirectory().getAbsolutePath(),
                        "smartAmcuUpdate.png");
                downLoadrReference = downloadmanager.enqueue(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDuplicateFile() {
        try {
            File str = context.getExternalFilesDir(Environment
                    .getExternalStorageDirectory().getAbsolutePath());
            String filename = str.toString() + "/smartAmcuUpdate.apk";
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//Install apk for unrooted tab

    public void deleteDuplicateFileCache() {


        File dir = context.getExternalFilesDir(Environment
                .getExternalStorageDirectory().getAbsolutePath());

        try {
            FilenameFilter textFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    String lowercaseName = name.toLowerCase();
                    if (lowercaseName.contains(".png")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };

            File[] files = dir.listFiles(textFilter);
            for (File file : files) {
                if (!file.isDirectory()) {
                    if (file.exists()) {
                        file.delete();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        try {
//            File str = context.getExternalFilesDir(Environment
//                    .getExternalStorageDirectory().getAbsolutePath());
//            String filename = str.toString() + "/smartAmcuUpdate.png";
//            File file = new File(filename);
//            if (file.exists()) {
//                file.delete();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
