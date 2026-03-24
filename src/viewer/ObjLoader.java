package viewer;

import java.io.*;
import java.util.*;
import model.Vector3;
import model.Triangle;

public class ObjLoader {
    public List<Vector3>  vertice  = new ArrayList<>();
    public List<Triangle> triangle = new ArrayList<>();

    public void load(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("File tidak ditemukan: \"" + path + "\"");
        }

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int nLine = 0;

        while ((line = br.readLine()) != null) {
            nLine++;
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] part = line.split("\\s+");
            if (part[0].equals("v")) {
                bacaVertex(part, nLine);
            } else if (part[0].equals("f")) {
                bacaFace(part, nLine);
            }
        }
        br.close();
    }

    private void bacaVertex(String[] part, int nLine) {
        if (part.length < 4) {
            throw new IllegalArgumentException("Format vertex tidak valid di baris-" + nLine);
        }
        double x = Double.parseDouble(part[1]);
        double y = Double.parseDouble(part[2]);
        double z = Double.parseDouble(part[3]);
        vertice.add(new Vector3(x, y, z));
    }

    private void bacaFace(String[] part, int nLine) {
        if (part.length < 4) {
            throw new IllegalArgumentException("Format face tidak valid di baris-" + nLine);
        }
        int i0 = faceIdx(part[1]) - 1;
        int i1 = faceIdx(part[2]) - 1;
        int i2 = faceIdx(part[3]) - 1;

        if (i0 < 0 || i0 >= vertice.size() ||
            i1 < 0 || i1 >= vertice.size() ||
            i2 < 0 || i2 >= vertice.size()) {
            throw new IllegalArgumentException("Index vertex tidak valid di baris-" + nLine);
        }
        triangle.add(new Triangle(vertice.get(i0), vertice.get(i1), vertice.get(i2)));
    }

    // ambil indeks integer dari format "i", "i/t", atau "i/t/n"
    private int faceIdx(String s) {
        return Integer.parseInt(s.split("/")[0]);
    }
}
