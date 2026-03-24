package model;

public class Vector3{
    public doubke x, y, z;

    public Vector3(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3 min(Vector3 temp){
        double newX = Math.min(this.x, temp.x);
        double newy = Math.min(this.y, temp.y);
        double newz = Math.min(this.z, temp.z);

        return new Vector3(newX, newY, newZ);
    }
    public Vector3 max(Vector3 temp){
        double newX = Math.max(this.x, temp.x);
        double newy = Math.max(this.y, temp.y);
        double newz = Math.max(this.z, temp.z);

        return new Vector3(newX, newY, newZ);
    }
    public Vector3 mid(Vector3 temp){
        double newX = (this.x + temp.x)/2.0;
        double newy = (this.y + temp.y)/2.0;
        double newz = (this.z + temp.z)/2.0;

        return new Vector3(newX, newY, newZ);
    }

    @Override
    public String toString{
        return x + " " + y + " " + z;
    }
}