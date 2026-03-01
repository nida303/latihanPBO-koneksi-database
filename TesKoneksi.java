import java.sql.Connection; 

public class TesKoneksi { 
    public static void main(String[] args) { 
        try { 
            Connection c = Koneksi.configDB(); 
            
            if (c != null) { 
                javax.swing.JOptionPane.showMessageDialog(null, "Hore! Java dan MySQL sudah salaman."); 
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
}