package viewer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import model.Triangle;

public class Renderer {
    private List<Triangle> tris;
    private Camera cam;
    private int W, H;

    // buffer yang direuse antar frame (realokasi hanya saat ukuran berubah)
    private BufferedImage img;
    private int[]    pixels; // warna tiap piksel (0x00RRGGBB)
    private double[] zbuf;   // kedalaman z terkecil tiap piksel

    private static final double NEAR   = 0.01;
    private static final int    BG_CLR = 0x1C1C24; // latar belakang gelap

    // arah cahaya ternormalisasi (hardcoded, tidak berubah)
    private static final double LX = 0.4629, LY = 0.9258, LZ = 0.6476;

    public Renderer(List<Triangle> tris, Camera cam) {
        this.tris = tris;
        this.cam  = cam;
    }

    // ganti model saat file baru dibuka
    public void setModel(List<Triangle> tris) {
        this.tris = tris;
    }

    public void setSize(int w, int h) {
        if (w == W && h == H){
            return; // tidak perlu realokasi
        }
        W = w; H = h;
        img    = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        pixels = new int[W * H];
        zbuf   = new double[W * H];
    }

    // gambar frame ke g
    public void draw(Graphics2D g) {
        if (W <= 0 || H <= 0){
            return;
        }

        // bersihkan buffer tiap frame
        Arrays.fill(pixels, BG_CLR);
        Arrays.fill(zbuf, Double.MAX_VALUE);

        if (tris != null && !tris.isEmpty()) {
            renderAll();
        }

        // kirim pixel buffer ke Graphics
        img.setRGB(0, 0, W, H, pixels, 0, W);
        g.drawImage(img, 0, 0, null);

        // overlay teks bantuan
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        if (tris != null && !tris.isEmpty()) {
            g.setColor(new Color(140, 140, 140));
            g.drawString("Drag: Rotasi  |  Scroll: Zoom", 8, H - 10);
            g.drawString("Segitiga: " + tris.size(), 8, H - 24);
        } else {
            // belum ada model — tampilkan petunjuk di tengah
            String msg = "Klik \"Buka File .obj\" untuk memuat model";
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(new Color(100, 100, 110));
            g.drawString(msg, (W - fm.stringWidth(msg)) / 2, H / 2);
        }

        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.setColor(new Color(140, 140, 140));
        String idText = "Arghawisesa Dwinanda Arham - 13524100";
        g.drawString(idText, W - g.getFontMetrics().stringWidth(idText) - 8, H - 10);
    }

    // iterasi semua segitiga, proyeksikan, rasterisasi ke pixel/z buffer
    private void renderAll() {
        // cache semua field kamera agar JIT bisa optimize loop
        double ex = cam.eye[0], ey = cam.eye[1], ez = cam.eye[2];
        double rx = cam.rgt[0], ry = cam.rgt[1], rz = cam.rgt[2];
        double ux = cam.up[0],  uy = cam.up[1],  uz = cam.up[2];
        double fx = cam.fwd[0], fy = cam.fwd[1], fz = cam.fwd[2];
        double fL = cam.fLen;
        double hw = W / 2.0, hh = H / 2.0;

        for (Triangle tri : tris) {
            // transformasi ke camera space (inline buat alokasi)
            double d1x = tri.v1.x - ex, d1y = tri.v1.y - ey, d1z = tri.v1.z - ez;
            double cx1 = d1x*rx + d1y*ry + d1z*rz;
            double cy1 = d1x*ux + d1y*uy + d1z*uz;
            double cz1 = d1x*fx + d1y*fy + d1z*fz;

            double d2x = tri.v2.x - ex, d2y = tri.v2.y - ey, d2z = tri.v2.z - ez;
            double cx2 = d2x*rx + d2y*ry + d2z*rz;
            double cy2 = d2x*ux + d2y*uy + d2z*uz;
            double cz2 = d2x*fx + d2y*fy + d2z*fz;

            double d3x = tri.v3.x - ex, d3y = tri.v3.y - ey, d3z = tri.v3.z - ez;
            double cx3 = d3x*rx + d3y*ry + d3z*rz;
            double cy3 = d3x*ux + d3y*uy + d3z*uz;
            double cz3 = d3x*fx + d3y*fy + d3z*fz;

            // lewati jika ada vertex di belakang near plane
            if (cz1 < NEAR || cz2 < NEAR || cz3 < NEAR) continue;

            // perspektif divide buat screen space
            double sx1 = hw + cx1/cz1*fL, sy1 = hh - cy1/cz1*fL;
            double sx2 = hw + cx2/cz2*fL, sy2 = hh - cy2/cz2*fL;
            double sx3 = hw + cx3/cz3*fL, sy3 = hh - cy3/cz3*fL;

            // warna dari normal world space
            // e1 = v2-v1, e2 = v3-v1
            double e1x = tri.v2.x-tri.v1.x, e1y = tri.v2.y-tri.v1.y, e1z = tri.v2.z-tri.v1.z;
            double e2x = tri.v3.x-tri.v1.x, e2y = tri.v3.y-tri.v1.y, e2z = tri.v3.z-tri.v1.z;
            double nx = e1y*e2z - e1z*e2y;
            double ny = e1z*e2x - e1x*e2z;
            double nz = e1x*e2y - e1y*e2x;
            double nl = Math.sqrt(nx*nx + ny*ny + nz*nz);
            if (nl < 1e-8) continue; // segitiga degenerate
            // dot(normal, cahaya) for lighting double sided (abs)
            double diff = Math.abs((nx*LX + ny*LY + nz*LZ) / nl);
            double br   = 0.15 + 0.85 * diff;
            int r  = (int)(br * 120); if (r  > 255) r  = 255;
            int gv = (int)(br * 155); if (gv > 255) gv = 255;
            int b  = (int)(br * 215); if (b  > 255) b  = 255;
            int clr = (r << 16) | (gv << 8) | b;

            // rasteriez ke buffer z
            fillTri(sx1, sy1, cz1, sx2, sy2, cz2, sx3, sy3, cz3, clr);
        }
    }

    // isi pixel segitiga pake z-test using koordinat barisentric
    private void fillTri(double px1, double py1, double pz1,
                         double px2, double py2, double pz2,
                         double px3, double py3, double pz3, int clr) {
        // bounding box layar segitiga, diklem ke batas kanvas
        int x0 = Math.max(0,   (int) Math.min(Math.min(px1, px2), px3));
        int y0 = Math.max(0,   (int) Math.min(Math.min(py1, py2), py3));
        int x1 = Math.min(W-1, (int) Math.ceil(Math.max(Math.max(px1, px2), px3)));
        int y1 = Math.min(H-1, (int) Math.ceil(Math.max(Math.max(py1, py2), py3)));
        if (x0 > x1 || y0 > y1) return;

        // penyebut koordinat barisentric
        double denom = (py2-py3)*(px1-px3) + (px3-px2)*(py1-py3);
        if (Math.abs(denom) < 1e-8) return;
        double inv = 1.0 / denom;

        for (int y = y0; y <= y1; y++) {
            int rowBase = y * W;
            double ypx3 = y - py3;
            for (int x = x0; x <= x1; x++) {
                double xpx3 = x - px3;
                // w1, w2, w3 = koordinat barisentric
                double w1 = ((py2-py3)*xpx3 + (px3-px2)*ypx3) * inv;
                double w2 = ((py3-py1)*xpx3 + (px1-px3)*ypx3) * inv;
                double w3 = 1.0 - w1 - w2;
                if (w1 < 0 || w2 < 0 || w3 < 0) continue;

                // interpolasi z dan z test
                double z = w1*pz1 + w2*pz2 + w3*pz3;
                int idx = rowBase + x;
                if (z < zbuf[idx]) {
                    zbuf[idx] = z;
                    pixels[idx] = clr;
                }
            }
        }
    }
}
