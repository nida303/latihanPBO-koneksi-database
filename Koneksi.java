import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane; 

public class Koneksi {

   
    private static Connection mysqlconfig;

    public static Connection configDB() throws SQLException {
        try {
            if (mysqlconfig == null || mysqlconfig.isClosed()) {

                String url = "jdbc:mysql://localhost:3307/db_akademik";
                String user = "root"; 
                String pass = "";    

                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

                mysqlconfig = DriverManager.getConnection(url, user, pass);

                System.out.println("Koneksi Sukses!"); 
            }

        } catch (SQLException e) {
            System.err.println("Koneksi Gagal: " + e.getMessage());
            JOptionPane.showMessageDialog(
                null,
                "Gagal Koneksi ke Database:\n" + e.getMessage()
            );

            throw e;
        }

        return mysqlconfig;
    }
}

