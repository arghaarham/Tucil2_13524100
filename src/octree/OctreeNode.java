package octree;

import java.util.List;
import java.util.ArrayList;
import model.Box;
import model.Triangle;

public class OctreeNode{
    public Box box;
    public List<Triangle> triangle;
    public OctreeNode[] children;
    public int h;
    public boolean isLeaf;

    public OctreeNode(Box box, List<Triangle> triangle, int h){
        this.box = box;
        this.triangle = triangle;
        this.children = null;
        this.h = h;
        this.isLeaf = false;
    }
}