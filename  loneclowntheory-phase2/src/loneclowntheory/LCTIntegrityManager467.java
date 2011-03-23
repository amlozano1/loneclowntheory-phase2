/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loneclowntheory;

import java.sql.Connection;
import java.sql.Statement;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Brandon
 */
public class LCTIntegrityManager467 extends LCTBellLaPadula467 implements IntegrityManager467
{
    private String dbms;
    private String dbName;
    private Connection con;
    //define table names
//    public static final String acm = "acm";
//    public static final String entityTable = "entityTable";
    ///define columns in acm
//    public static final String subject = acm + ".subject";
//    public static final String entity = acm + ".entity";
//    public static final String right = acm + ".right";
//    public static final String granter = acm + ".granter";
//    public static final String timestamp = acm + ".timestamp";
    //define rights for acm
//    public static final char read_only = 'r';
//    public static final char read_write = 'w';
//    public static final char write_only = 'a';
//    public static final char execute = 'e';
    //define columns in entityTable
    public static final String entityID = "entityID";
    public static final String entityName = "entityName";
    public static final String subjectOrObject = "subject_or_object";
    public static final String max_sensitivity = "max_sensitivity";
    public static final String max_category = "max_category";
    public static final String curr_sensitivity = "curr_sensitivity";
    public static final String curr_category = "curr_category";
    public static final String integ = "integrity";
    //define other constants
//    public static final String subject0 = "subject0";

    public LCTIntegrityManager467()
    {
        super();

        String connStr = "jdbc:mysql://localhost:3306";
        String user = "root";
        String pwd = "root";
        this.dbms = "mysql";
        this.dbName = "LoneClownTheory_biba";

        try
        {
            this.con = DriverManager.getConnection(connStr, user, pwd);

            String query = "USE " + dbName;
            Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            stmt.execute(query);
            con.setAutoCommit(true);
        }
        catch (Exception e)
        {
            System.out.println("In LCTIntegrityManager467 Constructor: " + e);
        }
    }

    public LCTIntegrityManager467(Connection connArg, String dbmsArg, String dbNameArg)
    {
        super();
        this.con = connArg;
        this.dbms = dbmsArg;
        this.dbName = dbNameArg;
        try
        {
            String query = "USE " + dbName;
            Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            stmt.execute(query);
            con.setAutoCommit(true);
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
    }

    /**
     * Creates a new subject with a maximum security clearance level, and integrity level.
     *
     * @param subjectName The new subject's name.
     * @param maxLevel    The subject's maximum security level.
     * @param integrity   The subject's integrity level.
     */
    public void newSubject(String subjectName, SecurityLevel467 maxLevel, IntLevel467 integrity)
    {
        Statement stmt = null;
        String query = "";
        String catStr = this.getCatString(maxLevel);

        try
        {
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            query = "INSERT INTO " + dbName + "." + entityTable
                    + " (`" + entityName + "`,`"
                    + subjectOrObject + "`,`"
                    + max_sensitivity + "`,`"
                    + max_category + "`,`"
                    + curr_sensitivity + "`,`"
                    + curr_category + "`,`"
                    + integ + "`) "
                    + "VALUES ('"
                    + subjectName
                    + "','1','"
                    + maxLevel.sensitivity.ordinal() + "',"
                    + catStr + ",'"
                    + +maxLevel.sensitivity.ordinal() + "',"
                    + catStr + ",'"
                    + integrity.ordinal() + "')";

            stmt.executeUpdate(query);
            System.out.println("OK");
            stmt.close();
        }
        catch (MySQLIntegrityConstraintViolationException e) //Predconition fails
        {
            System.out.println("NO");
        }
        catch (SQLException e)
        {
            System.out.println("NO");
        }
    }

    /**
     * Creates a new object with a security classification and integrity level.
     *
     * @param objectName  The new object's name.
     * @param level       The classification of the new object.
     * @param integrity   The object's integrity level.
     */
    public void newObject(String objectName, SecurityLevel467 level, IntLevel467 integrity)
    {
        Statement stmt = null;
        String query = "";
        String catStr = this.getCatString(level);

        try
        {
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            query = "INSERT INTO " + dbName + "." + entityTable
                    + " (`" + entityName + "`,`"
                    + subjectOrObject + "`,`"
                    + max_sensitivity + "`,`"
                    + max_category + "`,`"
                    + curr_sensitivity + "`,`"
                    + curr_category + "`,`"
                    + integ + "`) "
                    + "VALUES ('"
                    + objectName
                    + "','0','0','','"
                    + level.sensitivity.ordinal() + "',"
                    + catStr + ",'"
                    + integrity.ordinal() + "')";

            stmt.executeUpdate(query);
            System.out.println("OK");
            stmt.close();
        }
        catch (MySQLIntegrityConstraintViolationException e) //Predconition fails
        {
            System.out.println("NO");
        }
        catch (SQLException e)
        {
            System.out.println("NO");
        }
    }

    /**
     * Returns "OK" when the subject can perform the action on the object. Behaves as if the subject
     * is actually accessing the object in that it also updates the subject and object's integrity
     * levels based on the action.
     *
     * @param subjectName  The name of the subject.
     * @param objectName   The name of the object.
     * @param action       The requested action. {'r', 'u', 'w'} // NOTE: Per Mike -> {'r', 'w', 'a', 'e'}
     * @return             {"OK", "NO"}
     */
    @Override
    public String access(String subjectName, String objectName, String action)
    {
        String rtnStr = "NO";

        boolean result = false;

        switch (action.charAt(0))
        {
            case read_only:
                result = this.dominates(objectName, subjectName);
                break;
            case write_only:
                result = this.dominates(subjectName, objectName);
                break;
            case read_write:
                result = this.dominates(subjectName, objectName) && this.dominates(objectName, subjectName);
                break;
            case execute:
                result = this.dominates(subjectName, objectName);
                break;
            default:
                result = false;
                break;
        }

        if (result)
        {
            rtnStr = "OK";
        }
        else
        {
            rtnStr = "NO";
        }

        return rtnStr;
    }

    @Override
    public boolean dominates(String subjectName, String objectName)
    {
        boolean dom = false;

        // Statement for queries
        Statement stmt = null;

        String query = "SELECT A.entityName as subj, "
                + "B.entityName as obj, "
                + "A.integrity as subjInteg, "
                + "B.integrity as objInteg "
                + "FROM " + entityTable + " AS A, "
                + entityTable + " AS B "
                + "HAVING subj='" + subjectName
                + "' AND obj='" + objectName
                + "' AND objInteg<=subjInteg;";

        // try-catch block for SQLExceptions
        try
        {
            // Create the statment object
            stmt = con.createStatement();

            // Get the result set for the query
            ResultSet rs = stmt.executeQuery(query);

            // check to see if it has at least one row, indicating that the subject does have the right on the entity
            if (rs.next())
            {
                // If so, the return string is set to "OK"
                dom = true;
            }

            // Close the result set and statement
            rs.close();
            stmt.close();
        } // Catch any SQLExceptions
        catch (SQLException e)
        {
            System.out.println("In checkRights: " + e);
            dom = false;
        }

        return dom;
    }
}
