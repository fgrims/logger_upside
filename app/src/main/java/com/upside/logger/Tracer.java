package com.upside.logger;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.pusher.rest.Pusher;
import com.pusher.rest.data.Result;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Tracer extends Service {

    private static final String CHANNEL_ID = "TRACER_CHANNEL";
    public String id_slot;
    public File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    public File file;
    public FileWriter outfile;

    boolean flag = false; // set to true if you want to write the processes on a file

    Context context = this;
    UsageStatsManager manager;

    // list the active processes each `time` ms
    protected static final long DEFAULT_TIMEOUT = 1000;
    private ProcessTracer tracer;
    public List<UsageStats> runningAppProcessInfo;
    private HandlerThread mThread;
    private Handler mHandler;
    public boolean first_time = true;
    ArrayList<String> whitelist = new ArrayList<>();

    ArrayList<String> packageList = new ArrayList<>(Arrays.asList(
            "com.epicgames.fortnite",
            "com.epicgames.portal",
            "com.upside.kioskmanager",
            "com.upside.logger",
            "com.google.android.networkstack.tethering",
            "com.factory.mmigroup",
            "com.sec.android.app.DataCreate",
            "com.android.cts.priv.ctsshim",
            "com.samsung.android.smartswitchassistant",
            "com.sec.android.app.setupwizardlegalprovider",
            "com.samsung.android.app.galaxyfinder",
            "com.sec.location.nsflp2",
            "com.android.uwb.resources",
            "com.sec.android.app.chromecustomizations",
            "com.samsung.android.app.cocktailbarservice",
            "com.android.internal.display.cutout.emulation.corner",
            "com.google.android.ext.services",
            "com.android.internal.display.cutout.emulation.double",
            "com.sec.location.nfwlocationprivacy",
            "com.microsoft.appmanager",
            "com.android.providers.telephony",
            "com.sec.android.app.ve.vebgm",
            "com.sec.android.app.parser",
            "com.android.dynsystem",
            "com.samsung.android.networkstack",
            "com.google.android.googlequicksearchbox",
            "com.sec.android.app.bluetoothagent",
            "com.samsung.android.calendar",
            "com.google.android.cellbroadcastservice",
            "com.android.providers.calendar",
            "com.osp.app.signin",
            "com.samsung.android.emergency",
            "com.sec.automation",
            "com.android.providers.media",
            "com.google.android.onetimeinitializer",
            "com.google.android.ext.shared",
            "com.android.internal.systemui.navbar.gestural_wide_back",
            "com.android.virtualmachine.res",
            "com.android.wallpapercropper",
            "com.google.android.federatedcompute",
            "com.samsung.android.keycustomizationinfobackupservice",
            "com.samsung.android.wallpaper.res",
            "com.samsung.android.smartmirroring",
            "com.skms.android.agent",
            "com.samsung.android.mapsagent",
            "com.sec.android.app.safetyassurance",
            "com.samsung.android.incallui",
            "com.samsung.android.knox.containercore",
            "com.samsung.android.kidsinstaller",
            "com.sec.usbsettings",
            "com.samsung.android.easysetup",
            "com.android.externalstorage",
            "com.samsung.android.aware.service",
            "com.android.htmlviewer",
            "com.android.companiondevicemanager",
            "com.android.mms.service",
            "com.samsung.android.rubin.app",
            "com.android.providers.downloads",
            "com.google.android.health.connect.backuprestore",
            "com.google.android.apps.messaging",
            "com.sec.android.easyMover.Agent",
            "com.google.android.networkstack.tethering.overlay",
            "com.samsung.android.mdx.quickboard",
            "com.android.internal.systemui.onehanded.gestural",
            "vendor.qti.hardware.cacert.server",
            "com.wsomacp",
            "com.sec.android.iaft",
            "com.monotype.android.font.foundation",
            "com.samsung.android.knox.kpecore",
            "com.sec.android.app.factorykeystring",
            "com.samsung.android.knox.app.networkfilter",
            "com.sec.android.app.samsungapps",
            "com.google.android.configupdater",
            "com.samsung.android.accessibility.talkback",
            "com.google.android.providers.media.module",
            "com.sec.android.smartfpsadjuster",
            "com.google.android.overlay.modules.permissioncontroller",
            "com.samsung.android.app.settings.bixby",
            "com.sec.android.app.billing",
            "com.sec.epdgtestapp",
            "com.samsung.android.game.gamehome",
            "com.sec.android.app.desktoplauncher",
            "com.samsung.android.wifi.p2paware.resources",
            "com.sec.android.daemonapp",
            "com.google.ar.core",
            "com.sec.sve",
            "com.android.providers.downloads.ui",
            "com.android.vending",
            "com.android.pacprocessor",
            "com.android.simappdialog",
            "com.samsung.android.knox.attestation",
            "com.samsung.android.secsoundpicker",
            "com.google.android.adservices.api",
            "com.samsung.internal.systemui.navbar.sec_gestural",
            "com.microsoft.skydrive",
            "com.android.internal.display.cutout.emulation.hole",
            "com.android.internal.display.cutout.emulation.tall",
            "com.sec.android.app.soundalive",
            "com.sec.android.provider.badge",
            "com.android.certinstaller",
            "de.axelspringer.yana.zeropage",
            "com.android.carrierconfig",
            "com.android.internal.systemui.navbar.threebutton",
            "com.android.wifi.dialog",
            "com.samsung.SMT",
            "com.samsung.cmh",
            "android",
            "com.samsung.android.wifi.increase.scan.interval.resources",
            "com.samsung.android.wcmurlsnetworkstack",
            "com.qualcomm.qtil.aptxacu",
            "com.samsung.android.sm.devicesecurity",
            "com.google.android.overlay.modules.cellbroadcastreceiver",
            "com.samsung.android.peripheral.framework",
            "com.samsung.internal.systemui.navbar.sec_gestural_no_hint",
            "com.google.android.overlay.gmsconfig.searchselector",
            "com.google.android.sdksandbox",
            "com.samsung.android.net.wifi.wifiguider",
            "com.samsung.android.wifi.softapwpathree.resources",
            "com.android.egg",
            "com.android.mtp",
            "com.android.ons",
            "com.android.stk",
            "com.samsung.android.messaging",
            "com.android.backupconfirm",
            "com.samsung.klmsagent",
            "com.sec.android.app.SecSetupWizard",
            "com.samsung.android.app.telephonyui",
            "com.samsung.android.wifi.softap.resources",
            "com.samsung.android.samsungpositioning",
            "com.google.android.as",
            "com.google.android.gm",
            "com.google.android.apps.tachyon",
            "com.google.android.overlay.gmsconfig.common",
            "com.android.settings.intelligence",
            "com.sec.bcservice",
            "com.sec.modem.settings",
            "com.monotype.android.font.samsungone",
            "com.android.internal.systemui.navbar.gestural_extra_wide_back",
            "com.samsung.android.privacydashboard",
            "com.android.systemui.accessibility.accessibilitymenu",
            "com.google.android.permissioncontroller",
            "com.sec.android.app.servicemodeapp",
            "com.google.android.setupwizard",
            "com.android.providers.settings",
            "com.samsung.accessibility",
            "com.sec.imsservice",
            "com.android.sharedstoragebackup",
            "com.samsung.android.mobileservice",
            "com.android.printspooler",
            "com.samsung.android.mdx.kit",
            "com.samsung.storyservice",
            "com.android.dreams.basic",
            "com.google.android.overlay.modules.ext.services",
            "com.google.android.as.oss",
            "com.android.se",
            "com.android.inputdevices",
            "com.samsung.android.wifi.resources",
            "com.android.rkpdapp",
            "com.google.android.overlay.gmsconfig.photos",
            "com.samsung.android.kgclient",
            "com.android.bips",
            "com.qti.dpmserviceapp",
            "com.samsung.android.app.contacts",
            "com.samsung.android.service.peoplestripe",
            "com.samsung.android.da.daagent",
            "com.google.android.captiveportallogin",
            "com.google.android.overlay.gmsconfig.geotz",
            "com.samsung.android.smartcallprovider",
            "com.samsung.android.app.smartcapture",
            "com.sec.android.desktopmode.uiservice",
            "com.google.android.modulemetadata",
            "com.samsung.android.app.taskedge",
            "com.samsung.android.dynamiclock",
            "com.sec.android.app.camerasaver",
            "com.samsung.advp.imssettings",
            "com.samsung.android.location",
            "com.wt.wtsarmanager",
            "com.sec.android.app.clockpackage",
            "com.sec.android.RilServiceModeApp",
            "com.google.android.webview",
            "com.samsung.android.mdecservice",
            "com.google.android.overlay.modules.documentsui",
            "com.google.android.networkstack",
            "com.android.server.telecom",
            "com.sec.imslogger",
            "com.android.keychain",
            "com.qti.snapdragon.qdcm_ff",
            "com.android.chrome",
            "com.samsung.android.themecenter",
            "com.samsung.android.server.wifi.mobilewips",
            "com.google.android.packageinstaller",
            "com.google.android.gms",
            "com.google.android.gsf",
            "com.google.android.tts",
            "android.autoinstalls.config.samsung",
            "com.android.wifi.resources",
            "com.samsung.android.container",
            "com.android.calllogbackup",
            "com.google.android.partnersetup",
            "com.android.cameraextensions",
            "com.sec.android.diagmonagent",
            "com.android.localtransport",
            "com.google.android.overlay.gmsconfig.asi",
            "com.google.android.overlay.gmsconfig.gsa",
            "com.samsung.android.biometrics.app.setting",
            "com.sec.spp.push",
            "com.android.carrierdefaultapp",
            "com.android.credentialmanager",
            "com.android.theme.font.notoserifsource",
            "com.sec.android.app.myfiles",
            "com.android.proxyhandler",
            "com.google.android.safetycenter.resources",
            "com.android.internal.display.cut"));
    HashMap<String, Long> alarmProcess = new HashMap<>();
    private ExecutorService executorService;

    public void onCreate() {
        super.onCreate();

        if (flag)
        {
            file = new File(this.downloadsDir, "processes.txt");

            try {
                if (!this.file.exists()) {
                    boolean created = this.file.createNewFile();
                    if (created)
                        Log.e("TRACING", "file for processes created");
                    else
                        Log.e("TRACING", "file for processes not created");
                }
                this.outfile = new FileWriter(this.file.getAbsoluteFile());
            } catch (IOException e) {
                Log.e("TRACING", "ERROR" + e);
            }
        }

        try {
            manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        } catch(Exception e) {
            Log.e("Running Processes", "Error: " +e);
        }

        // here
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        // This is necessary for the service to continue running in the background
        // Return START_STICKY to ensure the service restarts if it's killed by the system
        id_slot = intent.getStringExtra("slotID");
        setService();
        mHandler.removeCallbacks(tracer);
        mHandler.postDelayed(tracer, DEFAULT_TIMEOUT);
        createNotificationChannel();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        notificationManager.notify(1, getNotification());
        startForeground(1, getNotification());
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setService() {
        this.getSystemService( ACTIVITY_SERVICE );
        mThread = new HandlerThread("mThread");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
        tracer = new ProcessTracer();
        /*
        try {
            tracer.run();
            Log.e("Running Processes", "Start Tracing");
        } catch(Exception e) {
            Log.e("Running Processes", "Error:",e);
        }*/
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(tracer);

        Log.e("Running Processes", "Start Tracing");
    }


    private class ProcessTracer implements Runnable {

        Pusher pusher = new Pusher("1822365", "faf40b647c793dd9ee86", "b03af5fa2309eb7974b2");

        Map<String, String> eventData = new HashMap<>();
        public ProcessTracer() {
            eventData.put("slot_id", id_slot);
        }

        @Override
        public void run() {
            try {
                pusher.setCluster("eu");
                pusher.setEncrypted(true);

                Log.e("HELLO", "ALIVE");
                // list the process running from 10 seconds ago to now
                runningAppProcessInfo = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, System.currentTimeMillis()-10000, System.currentTimeMillis());
                //Log.e("Running process", "List size: " + runningAppProcessInfo.size());

                if (first_time) {
                    /*
                    // if this is the first log
                    // write the entire log in ArrayList whitelist
                    for (UsageStats process : runningAppProcessInfo) {
                        String processName = process.getPackageName();
                        whitelist.add(processName);
                    }
                    first_time = false;

                    // for debug
                    deleteElement();
                    Log.d("Whitelist", "size: " + whitelist.size());
                    for (String element : whitelist) {
                        Log.d("Whitelist", "Whitelist " + element);
                    }
                     */
                    first_time = false;
                    for (String element : packageList) {
                        Log.d("PackageList", "Whitelist " + element);
                    }
                }

                // if flag is true, write the processes in a file
                // this is used the first time the tablet is used to log the native processes
                if (flag) writeOnFile(runningAppProcessInfo);

                for (UsageStats process : runningAppProcessInfo) {
                    String processName = process.getPackageName();
                    //Log.e("Running Processes", "Process running: " + processName);
                    //if (!whitelist.contains(processName)) {
                    if (!packageList.contains(processName)) {
                        // if there's a process not in the white list
                        // add it to alarm list with timestamp

                        //Log.e("CHECK", "Name: " + processName + " LastTimeForegroundServiceUsed: " + process.getLastTimeVisible());
                        if (!processName.contains("android") && !processName.contains("samsung")) {
                            if (!alarmProcess.containsKey(processName)) {
                                // if the process appear the first time in the whitelist
                                // add it
                                eventData.put("alarm", processName);
                                Result res = pusher.trigger("TRACER", "Unauthorized", eventData);
                                eventData.remove("alarm");
                                Log.e("STATUS", "MSG: " + res.getStatus());
                                Log.e("ALARM", "ALARM " + processName + " NOT AUTHORIZED THE FIRST TIME!");
                                alarmProcess.put(processName, process.getLastTimeVisible());
                            } else if (alarmProcess.containsKey(processName) && process.getLastTimeVisible() > alarmProcess.get(processName)) {
                                // if the process already is in the alarm
                                // and its timestamp is greater than the one already saved
                                // then alarm and update
                                alarmProcess.replace(processName, process.getLastTimeVisible());

                                eventData.put("alarm", processName);
                                Result res = pusher.trigger("TRACER", "Unauthorized", eventData);
                                eventData.remove("alarm");
                                Log.e("STATUS", "MSG: " + res.getStatus());
                                Log.e("ALARM", "ALARM 2: " + processName + " NOT AUTHORIZED ANOTHER TIME!");
                                //int size = alarmProcess.size();
                                //Log.e("TEST", "HASH MAP SIZE : " + size);
                            }
                        }
                    }
                }

                /*
                //for debuggig
                for (String i : alarmProcess.keySet()) {
                    Log.e("ALARM LIST","key: " + i + " value: " + alarmProcess.get(i));
                }*/
            } catch (Exception e) {
                Log.e("Running Processes", "Error: " + e);
            }
            mHandler.postDelayed(this, DEFAULT_TIMEOUT);
        }
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(tracer);
        mThread.quitSafely();

        executorService.shutdown();
        super.onDestroy();
    }

    public void deleteElement() {
        // for debug purposes
        whitelist.remove("tv.twitch.android.app");
        whitelist.remove("com.google.android.youtube");
        whitelist.remove("com.hp.printercontrol");
        whitelist.remove("com.netflix.mediaclient");
        whitelist.remove("com.amazon.avod.thirdpartyclient");
        whitelist.remove("com.example.era");
        whitelist.remove("com.chess");

    }

    @SuppressLint("ObsoleteSdkInt")
    private Notification getNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Tracer")
                .setContentText("Tracing Processes")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setOngoing(true);
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.S) {

            builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);
        }
        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Tracer Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager =
                    getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public void writeOnFile(List<UsageStats> l) {
        // method used the first time the tablet turned on
        // create a file with all native package used to create the whitelist

        for (UsageStats process : l) {
            String processName = process.getPackageName();
            try {
                outfile.append(processName);
                outfile.append("\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        flag = false;
        Log.e("TRACING", "WRITE ON FILE");

        try{
            outfile.flush();
            outfile.close();
        } catch (IOException e) {
            Log.e("TRACING", "Error closing file");
        }
    }
}
