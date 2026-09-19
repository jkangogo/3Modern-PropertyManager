package com.threemsystems.rentmanager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ReportPdf {
    public static final String PUBLIC_FOLDER = "3Modern-docs";

    public interface Callback {
        void onReady(File file);

        void onError(String message);
    }

    private static final ExecutorService IO = Executors.newSingleThreadExecutor();
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private ReportPdf() {}

    public static void download(Activity activity, String url, String filename, Callback callback) {
        if (activity == null || callback == null) {
            return;
        }
        if (!ReportSupport.filled(url)) {
            callback.onError("Missing report address.");
            return;
        }
        java.lang.ref.WeakReference<Activity> ref = new java.lang.ref.WeakReference<>(activity);
        File cacheDir = activity.getApplicationContext().getCacheDir();
        IO.execute(() -> {
            HttpURLConnection connection = null;
            try {
                String safeName = filename == null || filename.trim().isEmpty() ? "report.pdf" : filename.trim();
                if (!safeName.toLowerCase(java.util.Locale.US).endsWith(".pdf")) {
                    safeName = safeName + ".pdf";
                }
                File staging = new File(cacheDir, "pdf-staging");
                if (!staging.exists() && !staging.mkdirs()) {
                    throw new IllegalStateException("Could not create a temporary folder.");
                }
                File tempFile = new File(staging, safeName);
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(60000);
                connection.setInstanceFollowRedirects(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Accept", "application/pdf,*/*");
                String token = SessionManager.get(PManagerApp.get()).getToken();
                if (ReportSupport.filled(token)) {
                    connection.setRequestProperty("X-PManager-Token", token);
                }
                int code = connection.getResponseCode();
                InputStream stream = code >= 400 ? connection.getErrorStream() : connection.getInputStream();
                if (stream == null) {
                    throw new IllegalStateException("The server sent an empty file.");
                }
                byte[] buffer = new byte[8192];
                int first = stream.read(buffer);
                if (first < 4 || buffer[0] != '%' || buffer[1] != 'P' || buffer[2] != 'D' || buffer[3] != 'F') {
                    stream.close();
                    throw new IllegalStateException("That report is not a PDF. Check the filters and try again.");
                }
                FileOutputStream output = new FileOutputStream(tempFile);
                output.write(buffer, 0, first);
                int read;
                while ((read = stream.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
                output.close();
                stream.close();
                File saved = publishToDownloads(PManagerApp.get(), tempFile, safeName);
                MAIN.post(() -> {
                    Activity host = ref.get();
                    if (host == null || host.isFinishing()) {
                        return;
                    }
                    callback.onReady(saved);
                });
            } catch (Exception e) {
                String message = e.getMessage() == null ? "Could not download the PDF." : e.getMessage();
                MAIN.post(() -> {
                    Activity host = ref.get();
                    if (host == null || host.isFinishing()) {
                        return;
                    }
                    callback.onError(message);
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private static File publishToDownloads(android.content.Context context, File source, String safeName) throws Exception {
        File visible = writePublicFile(source, safeName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            writeMediaStore(context, source, safeName);
        }
        if (visible != null) {
            return visible;
        }
        File appFolder = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), PUBLIC_FOLDER);
        if (!appFolder.exists() && !appFolder.mkdirs()) {
            return source;
        }
        File copy = new File(appFolder, safeName);
        copyFile(source, copy);
        return copy;
    }

    private static File writePublicFile(File source, String safeName) {
        try {
            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (downloads == null) {
                return null;
            }
            File folder = new File(downloads, PUBLIC_FOLDER);
            if (!folder.exists() && !folder.mkdirs()) {
                return null;
            }
            File dest = new File(folder, safeName);
            copyFile(source, dest);
            return dest;
        } catch (Exception e) {
            return null;
        }
    }

    private static void writeMediaStore(android.content.Context context, File source, String safeName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return;
        }
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, safeName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + PUBLIC_FOLDER);
        values.put(MediaStore.Downloads.IS_PENDING, 1);
        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            return;
        }
        try {
            OutputStream out = resolver.openOutputStream(uri);
            if (out == null) {
                resolver.delete(uri, null, null);
                return;
            }
            FileInputStream in = new FileInputStream(source);
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
            ContentValues done = new ContentValues();
            done.put(MediaStore.Downloads.IS_PENDING, 0);
            resolver.update(uri, done, null, null);
        } catch (Exception e) {
            try {
                resolver.delete(uri, null, null);
            } catch (Exception ignored) {
            }
        }
    }

    private static void copyFile(File source, File dest) throws Exception {
        FileInputStream in = new FileInputStream(source);
        FileOutputStream out = new FileOutputStream(dest);
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.flush();
        out.close();
    }

    public static void share(Activity activity, File file) {
        if (activity == null || file == null || !file.exists()) {
            UiNotifier.snack(activity, "There's no PDF to share yet.");
            return;
        }
        Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_SUBJECT, file.getName());
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(Intent.createChooser(share, "Share PDF"));
    }

    public static void savedMessage(Activity activity, File file) {
        if (file == null) {
            return;
        }
        UiNotifier.snack(activity, "Saved to Downloads/" + PUBLIC_FOLDER + "/" + file.getName());
    }

    public static void run(Activity activity, String url, String filename, boolean shareAfter) {
        if (activity == null) {
            return;
        }
        java.lang.ref.WeakReference<Activity> ref = new java.lang.ref.WeakReference<>(activity);
        UiNotifier.snack(activity, shareAfter ? "Preparing PDF to share…" : "Downloading PDF…");
        download(activity, url, filename, new Callback() {
            @Override
            public void onReady(File file) {
                Activity host = ref.get();
                if (host == null || host.isFinishing()) {
                    return;
                }
                if (shareAfter) {
                    share(host, file);
                } else {
                    savedMessage(host, file);
                }
            }

            @Override
            public void onError(String message) {
                Activity host = ref.get();
                if (host == null || host.isFinishing()) {
                    return;
                }
                UiNotifier.snack(host, message);
            }
        });
    }
}
