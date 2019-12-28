package com.arjo129.artest.arrendering;

import android.app.Activity;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;
import android.view.WindowManager;

import com.google.ar.core.Session;

public class DisplayRotationHelper implements DisplayManager.DisplayListener {
        private boolean viewportChanged;
        private int viewportWidth;
        private int viewportHeight;
        private final Context context;
        private final Display display;

        /**
         * Constructs the DisplayRotationHelper but does not register the listener yet.
         *
         * @param context the Android {@link Context}.
         */
        public DisplayRotationHelper(Context context) {
            this.context = context;
            display = context.getSystemService(WindowManager.class).getDefaultDisplay();
        }

        /** Registers the display listener. Should be called from {@link Activity#onResume()}. */
        public void onResume() {
            context.getSystemService(DisplayManager.class).registerDisplayListener(this, null);
        }

        /** Unregisters the display listener. Should be called from {@link Activity#onPause()}. */
        public void onPause() {
            context.getSystemService(DisplayManager.class).unregisterDisplayListener(this);
        }

        /**
         * Records a change in surface dimensions. This will be later used by {@link
         * #updateSessionIfNeeded(Session)}. Should be called from {@link
         * android.opengl.GLSurfaceView.Renderer
         * #onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)}.
         *
         * @param width the updated width of the surface.
         * @param height the updated height of the surface.
         */
        public void onSurfaceChanged(int width, int height) {
            viewportWidth = width;
            viewportHeight = height;
            viewportChanged = true;
        }

        /**
         * Updates the session display geometry if a change was posted either by {@link
         * #onSurfaceChanged(int, int)} call or by {@link #onDisplayChanged(int)} system callback. This
         * function should be called explicitly before each call to {@link Session#update()}. This
         * function will also clear the 'pending update' (viewportChanged) flag.
         *
         * @param session the {@link Session} object to update if display geometry changed.
         */
        public void updateSessionIfNeeded(Session session) {
            if (viewportChanged) {
                int displayRotation = display.getRotation();
                session.setDisplayGeometry(displayRotation, viewportWidth, viewportHeight);
                viewportChanged = false;
            }
        }

        /**
         * Returns the current rotation state of android display. Same as {@link Display#getRotation()}.
         */
        public int getRotation() {
            return display.getRotation();
        }

        @Override
        public void onDisplayAdded(int displayId) {}

        @Override
        public void onDisplayRemoved(int displayId) {}

        @Override
        public void onDisplayChanged(int displayId) {
            viewportChanged = true;
        }

}
