import java.io.*;
import java.util.*;
import model.Vector3;
import model.Triangle;

public class ObjParser{
    public List<Vector3> vertice;
    public List<Triangle> triangle;

    public ObjParser(){
        this.vertice = new ArrayList<>();
        this.triangle = new ArrayList<>();
    }

    public void parse(String path) throws IOException{
        File file = new File(path);
        if(!file.exists()){
            throw new IOException("ERROR | File tidak ditemukan di \"" + path + "\"");
        }

//        read per line
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int nLine = 0;

        while ((line = reader.readLine()) != null){
            nLine++;
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")){ // comment
                continue;
            }

            String[] part = line.split("\\s+");

            if (part[0].equals("v")){
                parseVertex(part, nLine);
            } else if (part[0].equals("f")){
                parseFace(part, nLine);
            }
        }

        reader.close();
    }

    private void parseVertex(String[] part, int nLine){
        if (part.length < 4){
            throw new IllegalArgumentException("ERROR | Format vertex tidak valid di baris-" + nLine);
        };

        int i0 = parseFaceIndex(part[1]) - 1;
        int i1 = parseFaceIndex(part[2]) - 1;
        int i2 = parseFaceIndex(part[3]) - 1;

        if (i0 < 0 || i0 >= vertice.size() || i1 < 0 || i1 >= vertice.size() || i2 < 0 || i2 >= vertice.size()){
            throw new IllegalArgumentException("ERROR | Index vertex tidak valid di baris-" + nLine);
        }

        triangle.add(new Triangle(vertice.get(i0), vertice.get(i1), vertice.get(i2)));
    }

    private int parseFaceIndex(String temp){
        return Integer.parseInt(temp.split("/")[0]);
    }
}