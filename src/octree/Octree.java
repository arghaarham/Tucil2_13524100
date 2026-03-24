package octree;

import java.util.List;
import java.util.ArrayList;
import model.Box;
import model.Vector3;
import model.Triangle;

public class Octree{
    public OctreeNode root;
    public int maxH;
    
    public int[] nNode;
    public int[] nSkip;
    public List<OctreeNode> leafNode;
    
    public Octree(int maxH){
        this.maxH = maxH;
        this.nNode = new int[maxH + 1];
        this.nSkip = new int[maxH + 1];
        this.leafNode = new ArrayList<>();
    }

    public void build(List<Triangle>, Box sBox){
        root - new OctreeNode(sBox, triangle, 0);
        buildRec(root);
    }

    private void buildRec(OctreeNode node){
        nNode[node.h]++;

        if (node.triangle.isEmpty()){
            nSkip[node.h]++;
            return;
        }
        if (node.h == maxH){
            node.isLeaf = true;
            leafNode.add(node);
            return;
        }

        Box[] childBox = node.box.getOktan();
        node.children = new OctreeNode[8];
        for (int i = 0; i < 8; i++) {
            List<Triangle> childTriangle = new ArrayList<>();
            for (Triangle tri : node.triangle) {
                if (tri.intersectsBox(childBox[i].min, childBox[i].max)){
                    childTriangle.add(tri);
                }
            }
            node.children[i] = new OcteeeNode(childBox[i], childTriangle, node.h + 1);
            buildRec(node.children[i]);
        }
    }

    public Box buildBBox (List<Vector3> vertice){
        Vector3 min = new Vector3(vertice.get(0).x, vertice.get(0).y, vertice.get(0).z);
        for (Vector3 v : vertice){
            min = min.min(v);
            max = max.max(v);
        }
        return new Box(min, max);
    }
}