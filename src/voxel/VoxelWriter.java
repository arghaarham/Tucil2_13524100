package voxel;

import java.io.*;
import java.util.List;
import model.Vector3;
import octree.OctreeNode;

public class VoxelWriter {
    private int offset = 1;

    public void write(List<OctreeNode> leafNode, String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        for (OctreeNode leaf : leafNode) {
            writeObj(writer, leaf.box.min, leaf.box.max);
        }
        writer.close();
    }

    private void writeObj(BufferedWriter writer, Vector3 min, Vector3 max) throws IOException {
        Vector3[] v = { new Vector3(min.x, min.y, min.z), new Vector3(max.x, min.y, min.z),
                new Vector3(max.x, max.y, min.z), new Vector3(min.x, max.y, min.z), new Vector3(min.x, min.y, max.z),
                new Vector3(max.x, min.y, max.z), new Vector3(max.x, max.y, max.z), new Vector3(min.x, max.y, max.z) };

        for (Vector3 vertex : v) {
            writer.write("v " + vertex.x + " " + vertex.y + " " + vertex.z + "\n");
        }
        int o = offset;
        writer.write("f " + o + " " + (o + 1) + " " + (o + 2) + "\n");
        writer.write("f " + o + " " + (o + 2) + " " + (o + 3) + "\n");
        writer.write("f " + (o + 4) + " " + (o + 6) + " " + (o + 5) + "\n");
        writer.write("f " + (o + 4) + " " + (o + 7) + " " + (o + 6) + "\n");
        writer.write("f " + (o + 3) + " " + (o + 2) + " " + (o + 6) + "\n");
        writer.write("f " + (o + 3) + " " + (o + 6) + " " + (o + 7) + "\n");
        writer.write("f " + o + " " + (o + 5) + " " + (o + 1) + "\n");
        writer.write("f " + o + " " + (o + 4) + " " + (o + 5) + "\n");
        writer.write("f " + o + " " + (o + 3) + " " + (o + 7) + "\n");
        writer.write("f " + o + " " + (o + 7) + " " + (o + 4) + "\n");
        writer.write("f " + (o + 1) + " " + (o + 5) + " " + (o + 6) + "\n");
        writer.write("f " + (o + 1) + " " + (o + 6) + " " + (o + 2) + "\n");

        offset += 8;
    }
}