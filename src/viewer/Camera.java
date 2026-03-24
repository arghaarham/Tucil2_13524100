package viewer;

import model.Vector3;

// Representasi kamera orbit: mengorbit di sekeliling titik pusat model
public class Camera {
    double yaw, pitch, dist;
    Vector3 ctr;  // titik pusat orbit
    double fLen;  // panjang fokal dalam piksel

    // vektor basis kamera (diperbarui lewat refresh())
    double[] eye = new double[3]; // posisi mata di world space
    double[] fwd = new double[3]; // arah pandang (dari mata ke pusat)
    double[] rgt = new double[3]; // arah kanan kamera
    double[] up  = new double[3]; // arah atas kamera

    // reset kamera saat file baru dibuka
    public void reset(Vector3 newCtr, double newDist) {
        ctr   = newCtr;
        dist  = newDist;
        yaw   = 0.5;
        pitch = 0.3;
        refresh();
    }

    public Camera(Vector3 ctr, double dist) {
        this.ctr   = ctr;
        this.dist  = dist;
        this.yaw   = 0.5;
        this.pitch = 0.3;
        this.fLen  = 600.0;
        refresh();
    }

    // perbarui semua basis kamera dari sudut yaw, pitch, dan jarak
    public void refresh() {
        // klem pitch agar tidak terkena gimbal lock
        pitch = Math.max(-1.50, Math.min(1.50, pitch));

        double cp = Math.cos(pitch), sp = Math.sin(pitch);
        double cy = Math.cos(yaw),   sy = Math.sin(yaw);

        // posisi mata dalam world space
        eye[0] = ctr.x + dist * cp * sy;
        eye[1] = ctr.y + dist * sp;
        eye[2] = ctr.z + dist * cp * cy;

        // forward = normalize(center - eye)
        double fx = ctr.x - eye[0];
        double fy = ctr.y - eye[1];
        double fz = ctr.z - eye[2];
        double fl = Math.sqrt(fx*fx + fy*fy + fz*fz);
        fwd[0] = fx/fl; fwd[1] = fy/fl; fwd[2] = fz/fl;

        // right = normalize(worldUp x forward), worldUp = (0,1,0)
        // worldUp x fwd = (fwd[2], 0, -fwd[0])
        double rx = fwd[2], rz = -fwd[0]; // ry selalu 0
        double rl = Math.sqrt(rx*rx + rz*rz);
        if (rl < 1e-8) { rx = 1.0; rz = 0.0; rl = 1.0; }
        rgt[0] = rx/rl; rgt[1] = 0.0; rgt[2] = rz/rl;

        // up = fwd x right
        up[0] = fwd[1]*rgt[2] - fwd[2]*rgt[1];
        up[1] = fwd[2]*rgt[0] - fwd[0]*rgt[2];
        up[2] = fwd[0]*rgt[1] - fwd[1]*rgt[0];
    }

    // transformasi titik world space → camera space: kembalikan [cx, cy, cz]
    public double[] toCam(double wx, double wy, double wz) {
        double dx = wx - eye[0], dy = wy - eye[1], dz = wz - eye[2];
        return new double[]{
            dx*rgt[0] + dy*rgt[1] + dz*rgt[2], // cx (kanan)
            dx*up[0]  + dy*up[1]  + dz*up[2],  // cy (atas)
            dx*fwd[0] + dy*fwd[1] + dz*fwd[2]  // cz (dalam, positif = di depan)
        };
    }
}
