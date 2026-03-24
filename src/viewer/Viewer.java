package viewer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import model.Triangle;
import model.Vector3;

public class Viewer extends JFrame {
    private final Camera   cam;
    private final Renderer rend;
    private final JPanel   canvas;
    private final JButton  btnBuka;
    private final JLabel   lblInfo;

    public Viewer() {
        super("OBJ Viewer — Tucil 2 IF2211");

        cam  = new Camera(new Vector3(0, 0, 0), 1.0);
        rend = new Renderer(null, cam);

        // -- toolbar atas --
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        toolbar.setBackground(new Color(40, 40, 52));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 70, 90)));

        btnBuka = new JButton("Buka File .obj");
        btnBuka.setFocusPainted(false);

        lblInfo = new JLabel("Belum ada file dibuka");
        lblInfo.setForeground(new Color(170, 170, 185));
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 12));

        toolbar.add(btnBuka);
        toolbar.add(lblInfo);

        // -- kanvas render --
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                rend.setSize(getWidth(), getHeight());
                rend.draw((Graphics2D) g);
            }
        };
        canvas.setBackground(new Color(28, 28, 36));

        // pasang handler rotasi dan zoom
        InputHandler ih = new InputHandler(cam, canvas);
        canvas.addMouseListener(ih);
        canvas.addMouseMotionListener(ih);
        canvas.addMouseWheelListener(ih);

        btnBuka.addActionListener(e -> bukaFile());

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(canvas,  BorderLayout.CENTER);

        setPreferredSize(new Dimension(900, 680));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // tampilkan dialog pilih file, lalu muat di background thread
    private void bukaFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("OBJ Files (*.obj)", "obj"));
        fc.setDialogTitle("Pilih file .obj");
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String path  = fc.getSelectedFile().getAbsolutePath();
        String fname = fc.getSelectedFile().getName();

        // nonaktifkan tombol dan tampilkan status memuat
        btnBuka.setEnabled(false);
        lblInfo.setText("Memuat " + fname + " ...");

        // parsing di background agar GUI tidak freeze (terutama model besar)
        SwingWorker<ObjLoader, Void> worker = new SwingWorker<>() {
            @Override
            protected ObjLoader doInBackground() throws Exception {
                ObjLoader loader = new ObjLoader();
                loader.load(path);
                return loader;
            }

            @Override
            protected void done() {
                btnBuka.setEnabled(true);
                try {
                    ObjLoader loader = get();
                    List<Triangle> tris  = loader.triangle;
                    List<Vector3>  verts = loader.vertice;

                    if (tris.isEmpty()) {
                        JOptionPane.showMessageDialog(Viewer.this,
                            "Tidak ada segitiga yang terbaca di file ini.",
                            "Peringatan", JOptionPane.WARNING_MESSAGE);
                        lblInfo.setText("Gagal memuat: tidak ada segitiga");
                        return;
                    }

                    // hitung bounding box → posisi dan jarak kamera awal
                    Vector3 mn = new Vector3(verts.get(0).x, verts.get(0).y, verts.get(0).z);
                    Vector3 mx = new Vector3(verts.get(0).x, verts.get(0).y, verts.get(0).z);
                    for (Vector3 v : verts) { mn = mn.min(v); mx = mx.max(v); }
                    Vector3 ctr  = mn.mid(mx);
                    double  diag = diagLen(mn, mx);
                    double  dist = diag * 1.5 + 1e-3;

                    cam.reset(ctr, dist);
                    rend.setModel(tris);

                    lblInfo.setText(fname + "   (" + tris.size() + " segitiga, " + verts.size() + " vertex)");
                    setTitle("OBJ Viewer — " + fname);
                    canvas.repaint();

                } catch (InterruptedException | ExecutionException ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    JOptionPane.showMessageDialog(Viewer.this,
                        "Gagal membuka file:\n" + cause.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                    lblInfo.setText("Gagal memuat file");
                }
            }
        };
        worker.execute();
    }

    // panjang diagonal bounding box
    private static double diagLen(Vector3 a, Vector3 b) {
        double dx = b.x-a.x, dy = b.y-a.y, dz = b.z-a.z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
}
