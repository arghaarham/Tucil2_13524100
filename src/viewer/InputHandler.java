package viewer;

import java.awt.event.*;
import javax.swing.JPanel;

public class InputHandler extends MouseAdapter {
    private final Camera cam;
    private final JPanel panel;
    private int lastX, lastY;

    private static final double ROT_SPD  = 0.005; // speed rotasi (rad per piksel)
    private static final double ZOOM_SPD = 0.12;  // faktor zoom

    public InputHandler(Camera cam, JPanel panel) {
        this.cam   = cam;
        this.panel = panel;
    }

    // catat posisi mouse saat tombol ditekan
    @Override
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
    }

    // drag horizontal → yaw, drag vertikal → pitch
    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - lastX;
        int dy = e.getY() - lastY;
        lastX = e.getX();
        lastY = e.getY();

        cam.yaw   -= dx * ROT_SPD;
        cam.pitch += dy * ROT_SPD;
        cam.refresh();
        panel.repaint();
    }

    // scroll ke depan = zoom masuk, scroll ke belakang = zoom keluar
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double factor = 1.0 + e.getPreciseWheelRotation() * ZOOM_SPD;
        cam.dist = Math.max(1e-4, cam.dist * factor);
        cam.refresh();
        panel.repaint();
    }
}
