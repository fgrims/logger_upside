//package com.upside.logger;
//
//import android.annotation.SuppressLint;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.app.usage.UsageStats;
//import android.app.usage.UsageStatsManager;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.IBinder;
//import android.util.Log;
//import androidx.annotation.Nullable;
//import androidx.core.app.NotificationCompat;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
////import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.storage.*;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//
//import android.net.Uri;
//
//
//public class Tracer extends Service {
//
//    private static final String CHANNEL_ID = "TRACER_CHANNEL";
//    public File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//
//    public File file;
//    public File f_tracer;
//    public FileWriter outfile;
//    public FileWriter fw;
//
//    boolean flag = false; // set to true if you want to write the processes on a file
//
//    Context context = this;
//    UsageStatsManager manager;
//
//    // list the active processes each `time` ms
//    protected static final long DEFAULT_TIMEOUT = 1000;
//    private ProcessTracer tracer;
//    public List<UsageStats> runningAppProcessInfo;
//    private HandlerThread mThread;
//    private Handler mHandler;
//    public boolean first_time = true;
//    ArrayList<String> whitelist = new ArrayList<>();
//    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
//
//    private static FirebaseApp INSTANCE;
//
//    public FirebaseStorage storage;
//    public StorageReference storageRef;
//    public StorageReference logProcesses;
//    //private FirebaseAuth mAuth;
//    boolean check_auth = false;
//    ArrayList<String> packageList = new ArrayList<>(Arrays.asList(
//            "air.com.ubisoft.brawl.halla.platform.fighting.action.pvp",
//            "com.Psyonix.RL2D",
//            "com.ea.gp.fifaultimate",
//            "com.supercell.clashroyale",
//            "com.chess",
//            "com.epicgames.fortnite",
//            "com.epicgames.portal",
//            "com.upside.kioskmanager",
//            "com.upside.logger",
//            "com.google.android.networkstack.tethering",
//            "com.factory.mmigroup",
//            "com.sec.android.app.DataCreate",
//            "com.android.cts.priv.ctsshim",
//            "com.samsung.android.smartswitchassistant",
//            "com.sec.android.app.setupwizardlegalprovider",
//            "com.samsung.android.app.galaxyfinder",
//            "com.sec.location.nsflp2",
//            "com.android.uwb.resources",
//            "com.sec.android.app.chromecustomizations",
//            "com.samsung.android.app.cocktailbarservice",
//            "com.android.internal.display.cutout.emulation.corner",
//            "com.google.android.ext.services",
//            "com.android.internal.display.cutout.emulation.double",
//            "com.sec.location.nfwlocationprivacy",
//            "com.microsoft.appmanager",
//            "com.android.providers.telephony",
//            "com.sec.android.app.ve.vebgm",
//            "com.sec.android.app.parser",
//            "com.android.dynsystem",
//            "com.samsung.android.networkstack",
//            "com.google.android.googlequicksearchbox",
//            "com.sec.android.app.bluetoothagent",
//            "com.samsung.android.calendar",
//            "com.google.android.cellbroadcastservice",
//            "com.android.providers.calendar",
//            "com.osp.app.signin",
//            "com.samsung.android.emergency",
//            "com.sec.automation",
//            "com.android.providers.media",
//            "com.google.android.onetimeinitializer",
//            "com.google.android.ext.shared",
//            "com.android.internal.systemui.navbar.gestural_wide_back",
//            "com.android.virtualmachine.res",
//            "com.android.wallpapercropper",
//            "com.google.android.federated-compute",
//            "com.samsung.android.keycustomizationinfobackupservice",
//            "com.samsung.android.wallpaper.res",
//            "com.samsung.android.smartmirroring",
//            "com.skms.android.agent",
//            "com.samsung.android.mapsagent",
//            "com.sec.android.app.safetyassurance",
//            "com.samsung.android.incallui",
//            "com.samsung.android.knox.containercore",
//            "com.samsung.android.kidsinstaller",
//            "com.sec.usbsettings",
//            "com.samsung.android.easysetup",
//            "com.android.externalstorage",
//            "com.samsung.android.aware.service",
//            "com.android.htmlviewer",
//            "com.android.companiondevicemanager",
//            "com.android.mms.service",
//            "com.samsung.android.rubin.app",
//            "com.android.providers.downloads",
//            "com.google.android.health.connect.backuprestore",
//            "com.google.android.apps.messaging",
//            "com.sec.android.easyMover.Agent",
//            "com.google.android.networkstack.tethering.overlay",
//            "com.samsung.android.mdx.quickboard",
//            "com.android.internal.systemui.onehanded.gestural",
//            "vendor.qti.hardware.cacert.server",
//            "com.wsomacp",
//            "com.sec.android.iaft",
//            "com.monotype.android.font.foundation",
//            "com.samsung.android.knox.kpecore",
//            "com.sec.android.app.factorykeystring",
//            "com.samsung.android.knox.app.networkfilter",
//            "com.sec.android.app.samsungapps",
//            "com.google.android.configupdater",
//            "com.samsung.android.accessibility.talkback",
//            "com.google.android.providers.media.module",
//            "com.sec.android.smartfpsadjuster",
//            "com.google.android.overlay.modules.permissioncontroller",
//            "com.samsung.android.app.settings.bixby",
//            "com.sec.android.app.billing",
//            "com.sec.epdgtestapp",
//            "com.samsung.android.game.gamehome",
//            "com.sec.android.app.desktoplauncher",
//            "com.samsung.android.wifi.p2paware.resources",
//            "com.sec.android.daemonapp",
//            "com.google.ar.core",
//            "com.sec.sve",
//            "com.android.providers.downloads.ui",
//            "com.android.vending",
//            "com.android.pacprocessor",
//            "com.android.simappdialog",
//            "com.samsung.android.knox.attestation",
//            "com.samsung.android.secsoundpicker",
//            "com.google.android.adservices.api",
//            "com.samsung.internal.systemui.navbar.sec_gestural",
//            "com.microsoft.skydrive",
//            "com.android.internal.display.cutout.emulation.hole",
//            "com.android.internal.display.cutout.emulation.tall",
//            "com.sec.android.app.soundalive",
//            "com.sec.android.provider.badge",
//            "com.android.certinstaller",
//            "de.axelspringer.yana.zeropage",
//            "com.android.carrierconfig",
//            "com.android.internal.systemui.navbar.threebutton",
//            "com.android.wifi.dialog",
//            "com.samsung.SMT",
//            "com.samsung.cmh",
//            "android",
//            "com.samsung.android.wifi.increase.scan.interval.resources",
//            "com.samsung.android.wcmurlsnetworkstack",
//            "com.qualcomm.qtil.aptxacu",
//            "com.samsung.android.sm.devicesecurity",
//            "com.google.android.overlay.modules.cellbroadcastreceiver",
//            "com.samsung.android.peripheral.framework",
//            "com.samsung.internal.systemui.navbar.sec_gestural_no_hint",
//            "com.google.android.overlay.gmsconfig.searchselector",
//            "com.google.android.sdksandbox",
//            "com.samsung.android.net.wifi.wifiguider",
//            "com.samsung.android.wifi.softapwpathree.resources",
//            "com.android.egg",
//            "com.android.mtp",
//            "com.android.ons",
//            "com.android.stk",
//            "com.samsung.android.messaging",
//            "com.android.backupconfirm",
//            "com.samsung.klmsagent",
//            "com.sec.android.app.SecSetupWizard",
//            "com.samsung.android.app.telephonyui",
//            "com.samsung.android.wifi.softap.resources",
//            "com.samsung.android.samsungpositioning",
//            "com.google.android.as",
//            "com.google.android.gm",
//            "com.google.android.apps.tachyon",
//            "com.google.android.overlay.gmsconfig.common",
//            "com.android.settings.intelligence",
//            "com.sec.bcservice",
//            "com.sec.modem.settings",
//            "com.monotype.android.font.samsungone",
//            "com.android.internal.systemui.navbar.gestural_extra_wide_back",
//            "com.samsung.android.privacydashboard",
//            "com.android.systemui.accessibility.accessibilitymenu",
//            "com.google.android.permissioncontroller",
//            "com.sec.android.app.servicemodeapp",
//            "com.google.android.setupwizard",
//            "com.android.providers.settings",
//            "com.samsung.accessibility",
//            "com.sec.imsservice",
//            "com.android.sharedstoragebackup",
//            "com.samsung.android.mobileservice",
//            "com.android.printspooler",
//            "com.samsung.android.mdx.kit",
//            "com.samsung.storyservice",
//            "com.android.dreams.basic",
//            "com.google.android.overlay.modules.ext.services",
//            "com.google.android.as.oss",
//            "com.android.se",
//            "com.android.inputdevices",
//            "com.samsung.android.wifi.resources",
//            "com.android.rkpdapp",
//            "com.google.android.overlay.gmsconfig.photos",
//            "com.samsung.android.kgclient",
//            "com.android.bips",
//            "com.qti.dpmserviceapp",
//            "com.samsung.android.app.contacts",
//            "com.samsung.android.service.peoplestripe",
//            "com.samsung.android.da.daagent",
//            "com.google.android.captiveportallogin",
//            "com.google.android.overlay.gmsconfig.geotz",
//            "com.samsung.android.smartcallprovider",
//            "com.samsung.android.app.smartcapture",
//            "com.sec.android.desktopmode.uiservice",
//            "com.google.android.modulemetadata",
//            "com.samsung.android.app.taskedge",
//            "com.samsung.android.dynamiclock",
//            "com.sec.android.app.camerasaver",
//            "com.samsung.advp.imssettings",
//            "com.samsung.android.location",
//            "com.wt.wtsarmanager",
//            "com.sec.android.app.clockpackage",
//            "com.sec.android.RilServiceModeApp",
//            "com.google.android.webview",
//            "com.samsung.android.mdecservice",
//            "com.google.android.overlay.modules.documentsui",
//            "com.google.android.networkstack",
//            "com.android.server.telecom",
//            "com.sec.imslogger",
//            "com.android.keychain",
//            "com.qti.snapdragon.qdcm_ff",
//            "com.android.chrome",
//            "com.samsung.android.themecenter",
//            "com.samsung.android.server.wifi.mobilewips",
//            "com.google.android.packageinstaller",
//            "com.google.android.gms",
//            "com.google.android.gsf",
//            "com.google.android.tts",
//            "android.autoinstalls.config.samsung",
//            "com.android.wifi.resources",
//            "com.samsung.android.container",
//            "com.android.calllogbackup",
//            "com.google.android.partnersetup",
//            "com.android.cameraextensions",
//            "com.sec.android.diagmonagent",
//            "com.android.localtransport",
//            "com.google.android.overlay.gmsconfig.asi",
//            "com.google.android.overlay.gmsconfig.gsa",
//            "com.samsung.android.biometrics.app.setting",
//            "com.sec.spp.push",
//            "com.android.carrierdefaultapp",
//            "com.android.credentialmanager",
//            "com.android.theme.font.notoserifsource",
//            "com.sec.android.app.myfiles",
//            "com.android.proxyhandler",
//            "com.google.android.safetycenter.resources",
//            "com.android.internal.display.cut"));
//    HashMap<String, Long> alarmProcess = new HashMap<>();
//    private ExecutorService executorService;
//
//    public void onCreate() {
//        super.onCreate();
//
////        mAuth = FirebaseAuth.getInstance(this.getInstance(this.getApplicationContext()));
////
////        if (mAuth.getCurrentUser() != null)
////        {
////            System.out.println("CHECK AUTH 1 ");
////            String uid = mAuth.getCurrentUser().getUid();
////            Log.e("USER ALREADY AUTHENTICATED ", "USER ID: " + uid);
////            check_auth = true;
////        } else {
////            System.out.println("CHECK AUTH 2");
////            signInAnonymously();
////        }
//
//        try{
//            storage = FirebaseStorage.getInstance(this.getInstance(this.getApplicationContext()));
//            // storageRef = storage.getReferenceFromUrl("gs://processestracing.appspot.com");
//            storageRef = storage.getReference();
//
//        } catch(Exception e) {
//            Log.e("ERROR STORAGE", "ERROR: " + e);
//        }
//
//        if (flag)
//        {
//            file = new File(this.downloadsDir, "processes.txt");
//
//            try {
//                if (!this.file.exists()) {
//                    boolean created = this.file.createNewFile();
//                    if (created)
//                        Log.e("TRACING", "file for processes created");
//                    else
//                        Log.e("TRACING", "file for processes not created");
//                }
//                this.outfile = new FileWriter(this.file.getAbsoluteFile());
//            } catch (IOException e) {
//                Log.e("TRACING", "ERROR" + e);
//            }
//        }
//
//        try {
//            manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
//        } catch(Exception e) {
//            Log.e("Running Processes", "Error: " +e);
//        }
//
//        // here
//    }
//
//    public static FirebaseApp getInstance(Context context) {
//        if (INSTANCE == null) {
//            INSTANCE = getSecondProject(context);
//        }
//        return INSTANCE;
//    }
//    private static FirebaseApp getSecondProject(Context context) {
//        FirebaseOptions options1 = new FirebaseOptions.Builder()
//                .setApiKey("AIzaSyBh-PO6DK8_u7CS1NjaN9Dph0jSgX2z0V4")
//                .setApplicationId("1:530039730185:android:b21b7f6df4e7e761dd08d8")
//                .setProjectId("processestracing")
//                .setStorageBucket("processestracing.appspot.com")
//                .build();
//
//        return FirebaseApp.initializeApp(context, options1, "tracer_app");
//        //return FirebaseApp.getInstance("tracer_app");
//    }
////    private void signInAnonymously() {
////        mAuth.signInAnonymously()
////                .addOnCompleteListener(task -> {
////                    if (task.isSuccessful()) {
////                        // Sign in success, update UI with the signed-in user's information
////                        check_auth = true;
////
////                    } else {
////                        // If sign in fails, display a message to the user.
////                        Log.e("MyFirebaseService", "signInAnonymously:failure", task.getException());
////                        check_auth = false;
////                    }
////                });
////    }
//
//    public void createFile(String userID, String game){
//        String f_sha = randomSHA();
//        String fn = "processes_" + userID + "_" + game + "_" + f_sha + ".txt";
//
//        f_tracer = new File(this.downloadsDir, fn);
//        logProcesses = storageRef.child("tracing/" + userID + "/" + fn);
//
//        try{
//            if(!this.f_tracer.exists()) {
//                boolean created = this.f_tracer.createNewFile();
//                if(created)
//                    Log.e("TRACING FILE", "f_tracer created");
//                else
//                    Log.e("TRACING FILE", "f_tracer not created");
//            }
//            this.fw = new FileWriter(this.f_tracer.getAbsoluteFile());
//        } catch (IOException e) {
//            Log.e("TRACING FILE", "Error opening f_tracer for writing");
//        }
//    }
//
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // This is necessary for the service to continue running in the background
//        // Return START_STICKY to ensure the service restarts if it's killed by the system
//
////        String userID_tmp = intent.getStringExtra("userID");
////        String userID;
////        if (userID_tmp != null) {
////            userID = userID_tmp;
////        } else {
////            userID = "nullname";
////        }
////        String pkgname = intent.getStringExtra("pkgName");
////        String n_pkgname;
////        if(pkgname != null) {
////            n_pkgname = getLastPart(pkgname);
////        } else {
////            n_pkgname = "nullpkg";
////        }
//        String userID_tmp = "test";
//        String userID;
//        if (userID_tmp != null) {
//            userID = userID_tmp;
//        } else {
//            userID = "nullname";
//        }
//
//        String pkgname = "nullGameTEST_TRUE";
//        String n_pkgname;
//        if(pkgname == "nullGame") {
//            n_pkgname = "nullpkg";
//
//            Log.e("STOP", "STOP_SELF");
//        } else {
//            n_pkgname = "test";
//        }
//        createFile(userID, n_pkgname);
//        setService();
//        mHandler.removeCallbacks(tracer);
//        mHandler.postDelayed(tracer, DEFAULT_TIMEOUT);
//        createNotificationChannel();
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
//        notificationManager.notify(1, getNotification());
//        startForeground(1, getNotification());
//        return START_STICKY;
//    }
//
//    public static String getLastPart(String input) {
//        String[] parts = input.split("\\.");
//        return parts[parts.length - 1];
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    public void setService() {
//        this.getSystemService( ACTIVITY_SERVICE );
//        mThread = new HandlerThread("mThread");
//        mThread.start();
//        mHandler = new Handler(mThread.getLooper());
//        tracer = new ProcessTracer();
//        executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(tracer);
//
//        Log.e("Running Processes", "Start Tracing");
//    }
//
//
//    private class ProcessTracer implements Runnable {
//
//        Map<String, String> eventData = new HashMap<>();
//
//        @Override
//        public void run() {
//            try {
//
//                Log.e("HELLO", "ALIVE");
//                // list the process running from 10 seconds ago to now
//                runningAppProcessInfo = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, System.currentTimeMillis()-10000, System.currentTimeMillis());
//                //Log.e("Running process", "List size: " + runningAppProcessInfo.size());
//
//                if (first_time) {
//                    /*
//                    // if this is the first log
//                    // write the entire log in ArrayList whitelist
//                    for (UsageStats process : runningAppProcessInfo) {
//                        String processName = process.getPackageName();
//                        whitelist.add(processName);
//                    }
//                    first_time = false;
//
//                    // for debug
//                    deleteElement();
//                    Log.d("Whitelist", "size: " + whitelist.size());
//                    for (String element : whitelist) {
//                        Log.d("Whitelist", "Whitelist " + element);
//                    }
//                     */
//                    first_time = false;
//                    for (String element : packageList) {
//                        Log.d("PackageList", "Whitelist " + element);
//                    }
//                }
//
//                // if flag is true, write the processes in a file
//                // this is used the first time the tablet is used to log the native processes
//                if (flag) writeOnFile(runningAppProcessInfo);
//
//                for (UsageStats process : runningAppProcessInfo) {
//                    String processName = process.getPackageName();
//                    //Log.e("Running Processes", "Process running: " + processName);
//                    //if (!whitelist.contains(processName)) {
//                    if (!packageList.contains(processName)) {
//                        // if there's a process not in the white list
//                        // add it to alarm list with timestamp
//
//                        //Log.e("CHECK", "Name: " + processName + " LastTimeForegroundServiceUsed: " + process.getLastTimeVisible());
//                        if (!processName.contains("android") && !processName.contains("samsung")) {
//                            if (!alarmProcess.containsKey(processName)) {
//                                // if the process appear the first time in the whitelist
//                                // add it
//                                eventData.put("alarm", processName);
//                                eventData.remove("alarm");
//                                // write on file the process
//                                fw.append(processName);
//                                fw.append(", ");
//                                fw.append(sdf1.format(timestamp));
//                                fw.append("\n");
//                                Log.e("ALARM", "ALARM " + processName + " NOT AUTHORIZED THE FIRST TIME!");
//                                alarmProcess.put(processName, process.getLastTimeVisible());
//                            } else if (alarmProcess.containsKey(processName) && process.getLastTimeVisible() > alarmProcess.get(processName)) {
//                                // if the process already is in the alarm
//                                // and its timestamp is greater than the one already saved
//                                // then alarm and update
//                                alarmProcess.replace(processName, process.getLastTimeVisible());
//
//                                eventData.put("alarm", processName);
//                                fw.append(processName);
//                                fw.append(", ");
//                                fw.append(sdf1.format(timestamp));
//                                fw.append("\n");
//                                eventData.remove("alarm");
//                                Log.e("ALARM", "ALARM 2: " + processName + " NOT AUTHORIZED ANOTHER TIME!");
//                                //int size = alarmProcess.size();
//                                //Log.e("TEST", "HASH MAP SIZE : " + size);
//                            }
//                        }
//                    }
//                }
//
//                /*
//                //for debuggig
//                for (String i : alarmProcess.keySet()) {
//                    Log.e("ALARM LIST","key: " + i + " value: " + alarmProcess.get(i));
//                }*/
//            } catch (Exception e) {
//                Log.e("Running Processes", "Error: " + e);
//            }
//            mHandler.postDelayed(this, DEFAULT_TIMEOUT);
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        mHandler.removeCallbacks(tracer);
//        mThread.quitSafely();
//
//        executorService.shutdown();
//        super.onDestroy();
//
//        try{
//            //this.bw.close();
//            fw.flush();
//            fw.close();
//        } catch (IOException e) {
//            Log.e("AccelerometerLogging", "Error closing file_acc");
//        }
//
//        if(check_auth) {
//            // add firebase
//            Uri tracing_uri = Uri.fromFile(f_tracer);
//
//            logProcesses.putFile(tracing_uri)
//                    .addOnSuccessListener(taskSnapshot -> {
//                        // File uploaded successfully, delete local file
//                        if (f_tracer.delete()) {
//                            Log.e("DELETE TRACING FILE", "tracing file deleted");
//                        } else {
//                            Log.e("DELETE TRACING FILE", "tracing file not deleted");
//                        }
//                    })
//                    .addOnFailureListener(exception -> {
//                        // Handle unsuccessful uploads
//                        Log.e("UPLOAD TRACING FILE", "tracing file upload failed: " + exception.getMessage());
//                    });
//        } else {
//            Log.e("AUTH ERROR", "ERROR: Not authenticated");
//        }
//    }
//
//    public void deleteElement() {
//        // for debug purposes
//        whitelist.remove("tv.twitch.android.app");
//        whitelist.remove("com.google.android.youtube");
//        whitelist.remove("com.hp.printercontrol");
//        whitelist.remove("com.netflix.mediaclient");
//        whitelist.remove("com.amazon.avod.thirdpartyclient");
//        whitelist.remove("com.example.era");
//        whitelist.remove("com.chess");
//
//    }
//
//    @SuppressLint("ObsoleteSdkInt")
//    private Notification getNotification() {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent,
//                PendingIntent.FLAG_IMMUTABLE);
//        NotificationCompat.Builder builder = new
//                NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Tracer")
//                .setContentText("Tracing Processes")
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentIntent(pendingIntent)
//                .setOngoing(true);
//        if (android.os.Build.VERSION.SDK_INT >=
//                android.os.Build.VERSION_CODES.S) {
//
//            builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);
//        }
//        return builder.build();
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Tracer Service Channel",
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//            NotificationManager manager =
//                    getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Tracer Service Channel",
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//            NotificationManager manager =
//                    getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }*/
//    }
//
//    public void writeOnFile(List<UsageStats> l) {
//        // method used the first time the tablet turned on
//        // create a file with all native package used to create the whitelist
//
//        for (UsageStats process : l) {
//            String processName = process.getPackageName();
//            try {
//                outfile.append(processName);
//                outfile.append("\n");
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        flag = false;
//        Log.e("TRACING", "WRITE ON FILE");
//
//        try{
//            outfile.flush();
//            outfile.close();
//        } catch (IOException e) {
//            Log.e("TRACING", "Error closing file");
//        }
//    }
//
//    public static String randomSHA(){
//
//        SecureRandom r = new SecureRandom();
//        byte[] randNum = new byte[1000];
//        r.nextBytes(randNum);
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-512");
//
//            try {
//                md.update(randNum);
//                byte[] hashValue = md.digest();
//
//                byte[] encoded = Base64.getEncoder().encode(hashValue);
//                String hash = new String(encoded, StandardCharsets.UTF_8);
//                hash = hash.replaceAll("[^a-zA-Z0-9]", "");
//                Log.e("RANDOM HASH VALUE", "RANDOM HASH:" + hash);
//                return hash;
//            } catch (Exception e) {
//                Log.e("HASH ERROR", "ERROR:" + e);
//                return null;
//            }
//        } catch (NoSuchAlgorithmException e) {
//            return null;
//        }
//    }
//}
