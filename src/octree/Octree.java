package octree;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.concurrent.ForkJoinTask.invokeAll;
import model.Box;
import model.Vector3;
import model.Triangle;



public class Octree{

    public OctreeNode root;
    public int maxH;
    public AtomicInteger[] nNode;
    public AtomicInteger[] nSkip;
    public List<OctreeNode> leafNode;
    
    public Octree(int maxH){
        this.maxH = maxH;
        this.nNode = new AtomicInteger[maxH + 1];
        this.nSkip = new AtomicInteger[maxH + 1];

        for (int i = 0; i <= maxH; i++) {
            nNode[i] = new AtomicInteger(0);
            nSkip[i] = new AtomicInteger(0);
        }
        this.leafNode = new ArrayList<>();
    }

    private class OctreeTask extends RecursiveAction{
        private OctreeNode node;

        public OctreeTask(OctreeNode node){
            this.node = node;
        }

        @Override
        protected void compute(){
            buildRec(node);
        }
    }

    public void build(List<Triangle> triangle, Box sBox){
        root = new OctreeNode(sBox, triangle, 0);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new OctreeTask(root));
    }

    private void buildRec(OctreeNode node){
//        System.out.println("Thread: " + Thread.currentThread().getName() + " | depth: " + node.h);
        nNode[node.h].incrementAndGet();

        if (node.triangle.isEmpty()){
            nSkip[node.h].incrementAndGet();
            return;
        }
        if (node.h == maxH){
            node.isLeaf = true;
            synchronized (leafNode){
                leafNode.add(node);
            }
            return;
        }

        Box[] childBox = node.box.getOktan();
        node.children = new OctreeNode[8];

        List <OctreeTask> tasks = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            List<Triangle> childTriangle = new ArrayList<>();
            for (Triangle tri : node.triangle) {
                if (tri.intersectBox(childBox[i].min, childBox[i].max)){
                    childTriangle.add(tri);
                }
            }
            node.children[i] = new OctreeNode(childBox[i], childTriangle, node.h + 1);
            tasks.add(new OctreeTask(node.children[i]));
        }
        invokeAll(tasks);
    }

    public Box buildBBox (List<Vector3> vertice){
        Vector3 min = new Vector3(vertice.get(0).x, vertice.get(0).y, vertice.get(0).z);
        Vector3 max = new Vector3(vertice.get(0).x, vertice.get(0).y, vertice.get(0).z);
        for (Vector3 v : vertice){
            min = min.min(v);
            max = max.max(v);
        }
        return new Box(min, max);
    }
}