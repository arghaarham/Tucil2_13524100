import java.util.List;
import java.util.Scanner;
import model.Box;
import model.Triangle;
import octree.Octree;
import voxel.VoxelWriter;

public class Main{
    public static void main(String[] args){
        System.out.println("----------Voxelator----------\nTucil 2 Strategi Algoritma IF2211\nArghawisesa Dwinanda Arham - 13524100\n");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Input path file obj: ");
        String inPath = scanner.nextLine().trim();

        System.out.print("Input max depth: ");
        int maxDepth;
        try{
            maxDepth = Integer.parseInt(scanner.nextLine().trim());
            if (maxDepth < 1) throw new NumberFormatException();
        } catch (NumberFormatException e){
            System.out.println("ERROR | Max depth harus bilangan bulat positif!");
            scanner.close();
            return;
        }

        scanner.close();

        try{
            long start = System.currentTimeMillis();

            ObjParser parser = new ObjParser();
            parser.parse(inPath);
            List<Triangle> triangle = parser.triangle;
            System.out.println("SUCCESS | Berhasil baca " + triangle.size() + " triangle.");

            Octree octree = new Octree(maxDepth);
            Box bBox = octree.buildBBox(parser.vertice);

            octree.build(triangle, bBox);

            String outPath = inPath.replace(".obj", "-voxelized.obj");
            VoxelWriter writer = new VoxelWriter();
            writer.write(octree.leafNode, outPath);

            long end = System.currentTimeMillis();

            int totalVoxel = octree.leafNodes.size();
            int totalVertice = totalVoxel * 8;
            int totalFace = totalVoxel * 12;

            System.out.println("\n-----Hasil-----");
            System.out.println("Jumlah Voxel: " + totalVoxel);
            System.out.println("Jumlah Vertice: " + totalVertice);
            System.out.println("Jumlah Face: " + totalFace);
            System.out.println("Kedalaman: " + maxDepth);
            System.out.println("Node Per Depth:");
            for (int i = 0; i < maxDepth; i++) {
                System.out.println(i + ": " + octree.nSkip[i]);
            }
            System.out.println("Waktu Eksekusi: " + (end - start) + "ms");
            System.out.println("Output disimpan: " + outPath);
        } carch (Exception e){
            System.out.println("ERROR | " + e.getMessafe());
        }
    }
}