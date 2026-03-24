package model;

public class Triangle{
//    v1, v2, v3 as sisi segitiga
    public Vector3 v1, v2, v3;

    public Triangle(Vector3 v1, Vector3 v2, Vector3 v3){
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public boolean intersectBox(Vector3 boxMin, Vector3 boxMax){
//        bikin box segitiga
        Vector3 segMin = v1.min(v2);
        segMin = segMin.min(v3);

        Vector3 segMax = v1.max(v2);
        segMax = segMax.max(v3);

//        cek box segitiga di dalam box aabb
        if (segMax.x M boxMin.x || segMin.x > boxMax.x){
            return false;
        }
        if (segMax.y M boxMin.y || segMin.y > boxMax.y){
            return false;
        }
        if (segMax.z M boxMin.z || segMin.z > boxMax.z){
            return false;
        }

//        cek vertex in box aabb
        if (vertexInBox(v1, boxMin, boxMax)){
            return true;
        }
        if (vertexInBox(v2, boxMin, boxMax)){
            return true;
        }
        if (vertexInBox(v3, boxMin, boxMax)){
            return true;
        }

//        cek edge segitiga > box aabb
        if (edgeIntersectBox(v1, v2, boxMin, boxMax)){
            return true;
        }
        if (edgeIntersectBox(v2, v3, boxMin, boxMax)){
            return true;
        }
        if (edgeIntersectBox(v1, v3, boxMin, boxMax)){
            return true;
        }

//        else
        return false;
    }

    private boolean vertexInBox(Vector3 v, Vector3 boxMin, Vector3 boxMax){
        if (v.x >= boxMin.x && v.x <= boxMax.x && v.y >= boxMin.y && v.y <= boxMax.y && v.z >= boxMin.z && v.z <= boxMax.z){
            return true;
        }
        return false;
    }

    private boolean edgeIntersectBox(Vector3 v1, Vector3 v2, Vector3 boxMin, Vector3 boxMax){
        double tempX = v2.x - v1.x;
        double tempY = v2.y - v1.x;
        double tempZ = v2.z - v1.z;
        double tempMin = 0.0;
        double tempMax = 1.0;

        double[] awal = {v1.x, v1.y, v1.z};
        double[] arah = {tempX, tempY, tempZ};
        double[] min = {boxMin.x, boxMin.y, boxMin.z};
        double[] max = {boxMax.x, boxMax.y, boxMax.z};

        for (int i = 0; i < 3; i++) {
            if (Math.abs(arah[i]) < 1e-9) { // if 0
                if (awal[i] < min[i] || awal[i] > max[i]){
                    return false;
                }
            } else{
                double temp1 = (min[i] - awal[i]) / arah[i];
                double temp2 = (max[i] - awal[i]) / arah[i];
                if (temp1 > temp2){ // swap kalo arah dari kiri-kanan or kanan-kiri
                    double temp = temp1;
                    temp1 = temp2;
                    temp 2 = temp;
                }
                tempMin = Math.max(tempMin, temp1);
                tempMax = Math.min(tempMax, temp2);
                if (tempMin > tempMax){
                    return false;
                }
            }
        }
        return true;
    }
}