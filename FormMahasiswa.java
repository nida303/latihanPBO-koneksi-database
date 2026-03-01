
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.print.PrinterException;
import java.text.MessageFormat;


public class FormMahasiswa extends JFrame {

    private JLabel lblNim, lblNama, lblJurusan, lblJudul;
    private JTextField txtNim, txtNama, txtCari;
    private JComboBox<String> cmbJurusan;
    private JButton btnSimpan, btnReset, btnEdit, btnHapus, btnCetak;
    private JTable tabelMhs;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;

    public FormMahasiswa() {
        setTitle("Form Data Mahasiswa");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        setupUI();

        setupEvents();

        loadData();
    }

        private void setupUI() {
        lblJudul = new JLabel("Data Mahasiswa", SwingConstants.CENTER);
        lblJudul.setBounds(0, 10, 600, 30);
        add(lblJudul);

        lblNim = new JLabel("NIM:");
        lblNim.setBounds(20, 50, 100, 25);
        add(lblNim);

        txtNim = new JTextField();
        txtNim.setBounds(120, 50, 200, 25);
        add(txtNim);

        lblNama = new JLabel("Nama:");
        lblNama.setBounds(20, 90, 100, 25);
        add(lblNama);

        txtNama = new JTextField();
        txtNama.setBounds(120, 90, 300, 25);
        add(txtNama);

        lblJurusan = new JLabel("Jurusan:");
        lblJurusan.setBounds(20, 130, 100, 25);
        add(lblJurusan);

        String[] jurusans = {"Teknik Informatika", "Sistem Informasi", "Manajemen Informatika"};
        cmbJurusan = new JComboBox<>(jurusans);
        cmbJurusan.setBounds(120, 130, 200, 25);
        add(cmbJurusan);

        btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(120, 170, 100, 30);
        add(btnSimpan);

        btnReset = new JButton("Reset");
        btnReset.setBounds(230, 170, 100, 30);
        add(btnReset);

        btnEdit = new JButton("Edit");
        btnEdit.setBounds(340, 170, 100, 30);
        add(btnEdit);

        btnHapus = new JButton("Hapus");
        btnHapus.setBounds(450, 170, 100, 30);
        add(btnHapus);

        String[] header={"NIM", "Nama", "Jurusan"};
        tableModel = new DefaultTableModel(header, 0);
        tabelMhs = new JTable(tableModel);
        scrollPane = new JScrollPane(tabelMhs);
        add(scrollPane);

        // Label dan TextField Cari Data
        JLabel lblCari = new JLabel("Cari Data:");
        lblCari.setBounds(20, 230, 80, 25);
        add(lblCari);

        txtCari = new JTextField();
        txtCari.setBounds(100, 230, 200, 25);
        add(txtCari);

        // Geser posisi ScrollPane Tabel agak ke bawah sedikit jika perlu
        scrollPane.setBounds(20, 270, 540, 200);

        // Tambahkan Tombol CETAK
        btnCetak = new JButton("CETAK PDF");
        btnCetak.setBounds(460, 230, 100, 25); // Di sebelah kanan text cari
        add(btnCetak);



        }

        private void setupEvents() {
        // Aksi tombol Simpan
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simpanData();
            }
        });

        // Aksi tombol Reset
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kosongkanForm();
            }
        });

        tabelMhs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 1. Ambil baris yang diklik user
                int baris = tabelMhs.getSelectedRow();

                // 2. Ambil value dari tabel berdasarkan baris & kolom
                // Kolom 0: NIM, Kolom 1: Nama, Kolom 2: Jurusan
                String nim = tableModel.getValueAt(baris, 0).toString();
                String nama = tableModel.getValueAt(baris, 1).toString();
                String jur = tableModel.getValueAt(baris, 2).toString();

                // 3. Masukkan ke Form Input
                txtNim.setText(nim);
                txtNama.setText(nama);
                cmbJurusan.setSelectedItem(jur);

                // 4. Matikan Edit NIM (Karena Primary Key tidak boleh diubah sembarangan)
                txtNim.setEditable(false);

                // UX Tambahan: Matikan tombol Simpan, Hidupkan Edit/Hapus
                btnSimpan.setEnabled(false);
                btnEdit.setEnabled(true);
                btnHapus.setEnabled(true);
            }
        });



        // --- TAHAP 2: LOGIKA TOMBOL EDIT (UPDATE) ---
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Validasi: Pastikan ada data yang dipilih (NIM tidak kosong)
                    if (txtNim.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(FormMahasiswa.this, "Pilih data dari tabel dulu!");
                        return;
                    }

                    // Query Update
                    String sql = "UPDATE mahasiswa SET nama=?, jurusan=? WHERE nim=?";
                    Connection conn = Koneksi.configDB();
                    PreparedStatement pst = conn.prepareStatement(sql);

                    // Isi parameter (Perhatikan urutan ?)
                    pst.setString(1, txtNama.getText()); // Nama Baru
                    pst.setString(2, cmbJurusan.getSelectedItem().toString()); // Jurusan Baru
                    pst.setString(3, txtNim.getText()); // NIM Lama (Kunci WHERE)

                    // Eksekusi
                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(FormMahasiswa.this, "Data berhasil diubah");

                    // Refresh
                    loadData();
                    kosongkanForm();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FormMahasiswa.this, "Gagal edit: " + ex.getMessage());
                }
            }
        });

        // --- TAHAP 3: LOGIKA TOMBOL HAPUS (DELETE) ---
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validasi: Pastikan ada data yang dipilih
                if (txtNim.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(FormMahasiswa.this, "Pilih data yang akan dihapus!");
                    return;
                }

                // Konfirmasi Dialog (PENTING!)
                int konfirmasi = JOptionPane.showConfirmDialog(
                        FormMahasiswa.this,
                        "Yakin ingin menghapus data mahasiswa " + txtNama.getText() + "?",
                        "Konfirmasi Hapus",
                        JOptionPane.YES_NO_OPTION
                );

                if (konfirmasi == JOptionPane.YES_OPTION) {
                    try {
                        // Query Delete
                        String sql = "DELETE FROM mahasiswa WHERE nim=?";
                        Connection conn = Koneksi.configDB();
                        PreparedStatement pst = conn.prepareStatement(sql);

                        // Isi parameter
                        pst.setString(1, txtNim.getText());

                        // Eksekusi
                        pst.executeUpdate();

                        JOptionPane.showMessageDialog(FormMahasiswa.this, "Data berhasil dihapus");

                        // Refresh
                        loadData();
                        kosongkanForm();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(FormMahasiswa.this, "Gagal hapus: " + ex.getMessage());
                    }
                }
            }
        });

        // --- TOMBOL CETAK ---
        btnCetak.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Membuat Header dan Footer Laporan
                    MessageFormat header = new MessageFormat("Laporan Data Mahasiswa Informatika");
                    MessageFormat footer = new MessageFormat("Halaman {0,number,integer}");

                    // Perintah Mencetak JTable
                    tabelMhs.print(JTable.PrintMode.FIT_WIDTH, header, footer);

                } catch (PrinterException ex) {
                    JOptionPane.showMessageDialog(FormMahasiswa.this, "Gagal Mencetak: " + ex.getMessage());
                }
            }
        });

        // --- PENCARIAN DATA (KeyReleased pada txtCari) ---
        txtCari.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                String key = txtCari.getText();
                loadData(key); // Panggil loadData dengan keyword yang sedang diketik
            }
        });
    }
    // Logika Utama CRUD
    private void loadData() {
        // Panggilan default: tampilkan semua data
        loadData("");
    }

    private void loadData(String keyword) {
        // Bersihkan tabel
        tableModel.setRowCount(0);

        try {
            String sql;
            // Logika Dinamis: Jika keyword kosong, ambil semua. Jika ada, filter.
            if (keyword == null || keyword.isEmpty()) {
                sql = "SELECT * FROM mahasiswa";
            } else {
                // Menggunakan Wildcard %
                sql = "SELECT * FROM mahasiswa WHERE nama LIKE '%" + keyword + "%' "
                        + "OR nim LIKE '%" + keyword + "%' "
                        + "OR jurusan LIKE '%" + keyword + "%'";
            }

            Connection conn = Koneksi.configDB();
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("nim"),
                        rs.getString("nama"),
                        rs.getString("jurusan")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal Load: " + e.getMessage());
        }
    }

    private void simpanData() {
            // Validasi input
            if (txtNim.getText().isEmpty() || txtNama.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nim dan Nama tidak boleh kosong");
                return;
                
            }


            try {
                // Query insert dengan PreparedStatement
                String sql = "INSERT INTO mahasiswa (nim, nama, jurusan) VALUES (?, ?,?)";
                Connection conn = Koneksi.configDB();
                PreparedStatement pstat = conn.prepareStatement(sql);

                // isi parameter query
                pstat.setString(1, txtNim.getText());
                pstat.setString(2, txtNama.getText());
                pstat.setString(3, cmbJurusan.getSelectedItem().toString());

                // eksekusi query
                pstat.executeUpdate();

                // feedback sukses
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan");

                // refresh tabel dan form
                loadData();
                kosongkanForm();
            } catch (SQLException er) {
                JOptionPane.showMessageDialog(this, "Gagal Simpan (Cek Duplikasi NIM):\n" + er.getMessage());
            }
        }

    private void kosongkanForm() {
        txtNim.setText("");
        txtNama.setText("");
        cmbJurusan.setSelectedIndex(0);
        txtNim.requestFocus();

        // Kembalikan kondisi form ke awal
        txtNim.setEditable(true);
        btnSimpan.setEnabled(true);
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
        tabelMhs.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FormMahasiswa().setVisible(true);
            }
        });
    }
}