    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loneclowntheory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Brandon
 */
public class Main
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        LCTBellLaPadula467 lct;

        Connection con = null;

        String connStr = "jdbc:mysql://localhost:3306";
        String user = "root";
        String pwd = "root";
        String dbms = "mysql";
        String dbName = "LoneClownTheory";

        try
        {
            con = DriverManager.getConnection(connStr, user, pwd);

            lct = new LCTBellLaPadula467(con, dbms, dbName);

            Category467[] c1 = {Category467.AZ,Category467.CA};
            SecurityLevel467 sl = new SecurityLevel467(SensitivityLevel467.SECRET, c1);
            lct.newSubject("s1", sl);

            Category467[] c2 = {Category467.AZ};
            sl = new SecurityLevel467(SensitivityLevel467.SECRET, c2);
            lct.newSubject("s2", sl);

            Category467[] c3 = {Category467.ABQ};
            sl = new SecurityLevel467(SensitivityLevel467.CONFIDENTIAL, c3);
            lct.newSubject("s3", sl);

            Category467[] c4 = {};
            sl = new SecurityLevel467(SensitivityLevel467.CONFIDENTIAL, c4);
            lct.newObject("o1", sl);

            System.out.println(lct.dominates("subject0", "s2"));
            System.out.println(lct.dominates("s1", "s2"));

            System.out.println(lct.updateSL("s1", new SecurityLevel467(SensitivityLevel467.SECRET, c2)));

            con.close();
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
    }
}
