import javax.swing.SwingUtilities;
import viewer.Viewer;

public class ViewerMain {
    public static void main(String[] args) {
        System.out.println("----------OBJ Viewer----------");
        System.out.println("Tucil 2 Strategi Algoritma IF2211");
        System.out.println("Arghawisesa Dwinanda Arham - 13524100\n");
        System.out.println("Membuka jendela viewer...");
        SwingUtilities.invokeLater(Viewer::new);
    }
}
