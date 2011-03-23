/**
 * Lone Clown Theory
 *
 * Brandon Andersen
 * Brian Arvidson
 * Anthony Lozano
 * Justin Paglierani
 *
 * CSE 467/598
 * Spring 2011
 * Prof. Ahn
 *
 * LCTIntegrityManager467
 */
package loneclowntheory;

// imports
import java.sql.Connection;
import java.sql.Statement;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Integrity manager module.  See Section 6.2 in the textbook.
 *
 * @author Lone Clown Theory
 *
 */
public class LCTIntegrityManager467 extends LCTBellLaPadula467 implements IntegrityManager467
{
    // DB connection information

    protected String dbName = "LoneClownTheory_biba";
    public static final String integ = "integrity";

    /**
     * Default constructor:
     *
     * Creates the necessary DB connection without passing external parameters
     *
     */
    public LCTIntegrityManager467()
    {
        super();
        try
        {
            this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "root");

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

    /**
     * Parameterized constructor:
     *
     * Accepts DB connection information supplied externally
     *
     */
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
     * @author Brian Arvidson
     */
    public void newSubject(String subjectName, SecurityLevel467 maxLevel, IntLevel467 integrity)
    {
        Statement stmt = null; // sql statement
        String query = ""; // query string
        String catStr = this.getCatString(maxLevel); // category string

        try
        {
            // create the statement
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            // query to insert new subject
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

            // execute the query, print result and close statement
            stmt.executeUpdate(query);

            //Add to acm table subject 0 is root owner
            query = "INSERT INTO " + dbName + "." + acm
                    + " (`subject`, `entity`, `granter`, `right`"
                    + ") VALUES ('subject0', '"
                    + subjectName
                    + "', 'subject0', 'o')";

            stmt.executeUpdate(query); //execute (insert the row)

            System.out.println("OK");
            stmt.close();
        }
        catch (MySQLIntegrityConstraintViolationException e) //Predconition fails
        {
            System.out.println("NO");
        }
        catch (SQLException e) // Other sql exceptions
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
     * @author Brian Arvidson
     */
    public void newObject(String objectName, SecurityLevel467 level, IntLevel467 integrity)
    {
        Statement stmt = null; // sql statement
        String query = ""; // query string
        String catStr = this.getCatString(level); // category string

        try
        {
            // create the statement
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            // query string to insert new object
            // Note: the object's max_sensitivity and max_category
            // is set to default minimum values since they don't apply
            // to objects
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

            // execute the query, print results and close statement
            stmt.executeUpdate(query);

            //Add to acm table subject 0 is root owner
            query = "INSERT INTO " + dbName + "." + acm
                    + " (`subject`, `entity`, `granter`, `right`"
                    + ") VALUES ('subject0', '"
                    + objectName
                    + "', 'subject0', 'o')";

            stmt.executeUpdate(query); //execute (insert the row)

            System.out.println("OK");
            stmt.close();
        }
        catch (MySQLIntegrityConstraintViolationException e) //Predconition fails
        {
            System.out.println("NO");
        }
        catch (SQLException e) // Other sql exceptions
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
     * @author Justin Paglierani
     */
    @Override
    public String access(String subjectName, String objectName, String action)
    {
        String rtnStr = "NO"; // return string

        boolean result = false; // return bool

        // check conditions based on BIBA
        switch (action.charAt(0))
        {
            case read_only:
                // reading occuring so flow of read info must be down
                result = this.dominates(objectName, subjectName);
                break;
            case write_only:
                // writing occuring so flow of write data must be down
                result = this.dominates(subjectName, objectName);
                break;
            case read_write:
                // reading and writing so flow of info must be horizontal
                result = this.dominates(subjectName, objectName) && this.dominates(objectName, subjectName);
                break;
            case execute:
                // executing so info flow must be down
                result = this.dominates(subjectName, objectName);
                break;
            default:
                result = false;
                break;
        }

        // set return string based on result
        if (result)
        {
            rtnStr = "OK";
        }
        else
        {
            rtnStr = "NO";
        }

        //return the result
        return rtnStr;
    }

    /**
     * Returns true when the subject integrity level dominates object integrity level.
     *
     * @param subjectName  The name of the subject.
     * @param objectName   The name of the object.
     * @return             {true, false}
     * @author Brandon Andersen
     */
    @Override
    public boolean dominates(String subjectName, String objectName)
    {
        boolean dom = false; // return bool

        // Statement for queries
        Statement stmt = null;

        // query to check if subject integrity is >= object integrity
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

            // check to see if it has at least one row, indicating subject integrity is >= object integrity
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
            System.out.println("In dominates: " + e);
            dom = false;
        }

        // return results
        return dom;
    }
}
