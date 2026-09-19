package com.threemsystems.rentmanager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.window.java.layout.WindowInfoTrackerCallbackAdapter;
import androidx.window.layout.DisplayFeature;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.WindowInfoTracker;
import androidx.window.layout.WindowLayoutInfo;

/**
 * Foldables and split-screen: layouts use {@code values-w600dp}/{@code values-w840dp}.
 * Book-style folds add hinge inset to {@link BoundedWidthLayout} only.
 */
public final class AdaptiveUi {
    private AdaptiveUi() {}

    public static void attach(AppCompatActivity activity) {
        View content = activity.findViewById(android.R.id.content);
        if (content == null || content.getTag(R.id.adaptive_window_listener) != null) {
            return;
        }
        WindowInfoTrackerCallbackAdapter adapter =
                new WindowInfoTrackerCallbackAdapter(WindowInfoTracker.getOrCreate(activity));
        Consumer<WindowLayoutInfo> listener = info -> applyHinge(activity, info);
        content.setTag(R.id.adaptive_window_listener, listener);
        adapter.addWindowLayoutInfoListener(activity, ContextCompat.getMainExecutor(activity), listener);
        activity.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                adapter.removeWindowLayoutInfoListener(listener);
            }
        });
    }

    private static void applyHinge(Activity activity, WindowLayoutInfo info) {
        int extra = 0;
        for (DisplayFeature feature : info.getDisplayFeatures()) {
            if (!(feature instanceof FoldingFeature)) {
                continue;
            }
            FoldingFeature fold = (FoldingFeature) feature;
            if (fold.isSeparating() && fold.getOrientation() == FoldingFeature.Orientation.VERTICAL) {
                extra = activity.getResources().getDimensionPixelSize(R.dimen.screen_padding);
                break;
            }
        }
        View content = activity.findViewById(android.R.id.content);
        if (content != null) {
            applyHingeToBounded(content, extra);
        }
    }

    private static void applyHingeToBounded(View view, int extra) {
        if (view instanceof BoundedWidthLayout) {
            ((BoundedWidthLayout) view).setHingeInsetPx(extra);
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyHingeToBounded(group.getChildAt(i), extra);
            }
        }
    }
}
