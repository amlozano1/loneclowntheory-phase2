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

            Category467[] c1 =
            {
                Category467.AZ, Category467.CA
            };
            SecurityLevel467 sl = new SecurityLevel467(SensitivityLevel467.SECRET, c1);
            lct.newSubject("s1", sl);

            Category467[] c2 =
            {
                Category467.AZ
            };
            sl = new SecurityLevel467(SensitivityLevel467.SECRET, c2);
            lct.newSubject("s2", sl);

            Category467[] c3 =
            {
                Category467.ABQ
            };
            sl = new SecurityLevel467(SensitivityLevel467.CONFIDENTIAL, c3);
            lct.newSubject("s3", sl);

            Category467[] c4 =
            {
            };
            sl = new SecurityLevel467(SensitivityLevel467.CONFIDENTIAL, c4);
            lct.newObject("o1", sl);

            System.out.println("1 " + lct.dominates("subject0", "s2"));

            System.out.println("2 " + lct.dominates("s1", "s2"));

            System.out.println("3 " + lct.updateSL("s1", new SecurityLevel467(SensitivityLevel467.SECRET, c2)));

            System.out.println("4.0 " + lct.dominates("s3", "o1"));

            System.out.println("4.1 " + lct.classifyOL("o1", new SecurityLevel467(SensitivityLevel467.SECRET, c2)));

            System.out.println("4.2 " + lct.dominates("s3", "o1"));

            System.out.println("5 " + lct.declassifyOL("s3", "o1", sl));

            System.out.println("6 " + lct.declassifyOL("subject0", "o1", sl));

            System.out.println("7.0 " + lct.access("subject0", "o1", "r"));
            System.out.println("7.1 " + lct.access("subject0", "o1", "w"));
            System.out.println("7.2 " + lct.access("subject0", "o1", "a"));
            System.out.println("7.3 " + lct.access("subject0", "o1", "e"));

            System.out.println("8.0 " + lct.access("s1", "o1", "r"));
            System.out.println("8.1 " + lct.access("s1", "o1", "w"));
            System.out.println("8.2 " + lct.access("s1", "o1", "a"));
            System.out.println("8.3 " + lct.access("s1", "o1", "e"));

            System.out.println("9.0 " + lct.access("s2", "o1", "r"));
            System.out.println("9.1 " + lct.access("s2", "o1", "w"));
            System.out.println("9.2 " + lct.access("s2", "o1", "a"));
            System.out.println("9.3 " + lct.access("s2", "o1", "e"));

            System.out.println("10.0 " + lct.access("s3", "o1", "r"));
            System.out.println("10.1 " + lct.access("s3", "o1", "w"));
            System.out.println("10.2 " + lct.access("s3", "o1", "a"));
            System.out.println("10.3 " + lct.access("s3", "o1", "e"));

            con.close();
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
    }
}
