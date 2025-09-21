package org.lisasp.swing.filechooser.jfx;

import java.awt.*;

class AWTUtils {
    static void center(Window frame) {
        Rectangle r = getVirtualScreenBounds(frame);

        int x = (int) (r.getX() + Math.max(0, (r.getWidth() - frame.getWidth()) / 2));
        int y = (int) (r.getY() + Math.max(0, (r.getHeight() - frame.getHeight()) / 2));

        frame.setLocation(x, y);
    }

    private static Rectangle getVirtualScreenBounds(Window w) {
        return w.getGraphicsConfiguration().getDevice().getDefaultConfiguration().getBounds();
    }
}
