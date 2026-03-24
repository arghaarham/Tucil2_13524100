package model;

public class Box{
    public Vector3 min;
    public Vector3 max;

    public Box(Vector3 min, Vector3 max){
        this.min = min;
        this.max = max;
    }

    public Vector3 mid(){
        return min.mid(max);
    }

    public Box[] getOktan(){
        Vector3 mid = mid();
        return new Box[] {new Box(new Vector3(min.x, min.y, min.z), new Vector3(mid.x, mid.y, mid.z)), new Box(new Vector3(mid.x, min.y, min.z), new Vector3(max.x, mid.y, mid.z)), new Box(new Vector3(min.x, mid.y, min.z), new Vector3(mid.x, max.y, mid.z)), new Box(new Vector3(mid.x, mid.y, min.z), new Vector3(max.x, max.y, mid.z)), new Box(new Vector3(min.x, min.y, mid.z), new Vector3(mid.x, mid.y, max.z)), new Box(new Vector3(mid.x, min.y, mid.z), new Vector3(max.x, mid.y, max.z)), new Box(new Vector3(min.x, mid.y, mid.z), new Vector3(mid.x, max.y, max.z)), new Box(new Vector3(mid.x, mid.y, mid.z), new Vector3(max.x, max.y, max.z))};
    }
}