package model;

public class Vector3{
    public double x, y, z;

    public Vector3(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3 min(Vector3 temp){
        double newX = Math.min(this.x, temp.x);
        double newY = Math.min(this.y, temp.y);
        double newZ = Math.min(this.z, temp.z);

        return new Vector3(newX, newY, newZ);
    }
    public Vector3 max(Vector3 temp){
        double newX = Math.max(this.x, temp.x);
        double newY = Math.max(this.y, temp.y);
        double newZ = Math.max(this.z, temp.z);

        return new Vector3(newX, newY, newZ);
    }
    public Vector3 mid(Vector3 temp){
        double newX = (this.x + temp.x)/2.0;
        double newY = (this.y + temp.y)/2.0;
        double newZ = (this.z + temp.z)/2.0;

        return new Vector3(newX, newY, newZ);
    }

    @Override
    public String toString(){
        return x + " " + y + " " + z;
    }
}