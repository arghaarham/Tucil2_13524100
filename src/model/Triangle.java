package model;

public class Triangle{
//    v1, v2, v3 as sisi segitiga
    public Vector3 v1, v2, v3;

    public Triangle(Vector3 v1, Vector3 v2, Vector3 v3){
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public boolean intersectBox(Vector3 boxMin, Vector3 boxMax) {
        // titik tengah dan half kotak
        double cx = (boxMin.x + boxMax.x) / 2.0;
        double cy = (boxMin.y + boxMax.y) / 2.0;
        double cz = (boxMin.z + boxMax.z) / 2.0;
        double ex = (boxMax.x - boxMin.x) / 2.0;
        double ey = (boxMax.y - boxMin.y) / 2.0;
        double ez = (boxMax.z - boxMin.z) / 2.0;

        // geser segitiga biar kotak di origin
        double[] v0 = {v1.x - cx, v1.y - cy, v1.z - cz};
        double[] v1a = {v2.x - cx, v2.y - cy, v2.z - cz};
        double[] v2a = {v3.x - cx, v3.y - cy, v3.z - cz};

        // 3 edge segitiga
        double[] e0 = {v1a[0]-v0[0], v1a[1]-v0[1], v1a[2]-v0[2]};
        double[] e1 = {v2a[0]-v1a[0], v2a[1]-v1a[1], v2a[2]-v1a[2]};
        double[] e2 = {v0[0]-v2a[0], v0[1]-v2a[1], v0[2]-v2a[2]};

        // cek1 3 axis kotak (x, y, z)
        if (Math.max(Math.max(v0[0], v1a[0]), v2a[0]) < -ex ||
                Math.min(Math.min(v0[0], v1a[0]), v2a[0]) >  ex) return false;
        if (Math.max(Math.max(v0[1], v1a[1]), v2a[1]) < -ey ||
                Math.min(Math.min(v0[1], v1a[1]), v2a[1]) >  ey) return false;
        if (Math.max(Math.max(v0[2], v1a[2]), v2a[2]) < -ez ||
                Math.min(Math.min(v0[2], v1a[2]), v2a[2]) >  ez) return false;

        // cek2 normal segitiga
        double[] normal = cross(e0, e1);
        if (!planeTest(normal, v0, ex, ey, ez)) return false;

        // cek3 9 axis cross product
        double[][] edges = {e0, e1, e2};
        double[][] axes = {{1,0,0}, {0,1,0}, {0,0,1}};
        double[][] verts = {v0, v1a, v2a};
        double[] halfSize = {ex, ey, ez};

        for (double[] edge : edges) {
            for (double[] axis : axes) {
                double[] a = cross(edge, axis);
                if (!satTest(a, verts, halfSize)) return false;
            }
        }

        return true;
    }

    // cross product dua vektor
    private double[] cross(double[] a, double[] b) {
        return new double[]{
                a[1]*b[2] - a[2]*b[1],
                a[2]*b[0] - a[0]*b[2],
                a[0]*b[1] - a[1]*b[0]
        };
    }

    // cek separating axis normal segitiga
    private boolean planeTest(double[] n, double[] v0, double ex, double ey, double ez) {
        double d = n[0]*v0[0] + n[1]*v0[1] + n[2]*v0[2];
        double r = ex*Math.abs(n[0]) + ey*Math.abs(n[1]) + ez*Math.abs(n[2]);
        return Math.abs(d) <= r;
    }

    // cek separating axis umum (SAT)
    private boolean satTest(double[] axis, double[][] verts, double[] halfSize) {
        double p0 = dot(axis, verts[0]);
        double p1 = dot(axis, verts[1]);
        double p2 = dot(axis, verts[2]);
        double r = halfSize[0]*Math.abs(axis[0])
                + halfSize[1]*Math.abs(axis[1])
                + halfSize[2]*Math.abs(axis[2]);
        double mn = Math.min(Math.min(p0, p1), p2);
        double mx = Math.max(Math.max(p0, p1), p2);
        return !(mn > r || mx < -r);
    }

    private double dot(double[] a, double[] b) {
        return a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
    }


}