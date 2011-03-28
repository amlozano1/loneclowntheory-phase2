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
 * LCTBellLaPadula467
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
 * Policy manager for the Bell-LaPadula Model. See Section 5.2 in the textbook.
 *
 * @author Lone Clown Theory
 *
 */
public class LCTBellLaPadula467 extends LCTAuthPolicyManager467 implements BellLaPadula467
{
    
    // permissions
    public static final char read_only = 'r';
    public static final char read_write = 'w';
    public static final char write_only = 'a';
    public static final char execute = 'e';
    //define columns in entityTable
    public static final String entityID = "entityID";
    public static final String entityName = "entityName";
    public static final String subjectOrObject = "subject_or_object";
    public static final String max_sensitivity = "max_sensitivity";
    public static final String max_category = "max_category";
    public static final String curr_sensitivity = "curr_sensitivity";
    public static final String curr_category = "curr_category";

    /**
     * Default constructor:
     *
     * Creates the necessary DB connection without passing external parameters
     *
     */
    public LCTBellLaPadula467()
    {
        super();
        this.dbms = "mysql";
        this.dbName = "LoneClownTheory_blp";
        try
        {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "root");
            String query = "USE " + dbName;
            Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            stmt.execute(query);
            con.setAutoCommit(true);
        }
        catch (Exception e)
        {
            System.out.println("In LCTBellLaPadula467 Constructor: " + e);
        }
    }

    /**
     * Parameterized constructor:
     *
     * Accepts DB connection information supplied externally
     *
     */
    public LCTBellLaPadula467(Connection connArg, String dbmsArg, String dbNameArg)
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
     * Creates a new subject with a maximum security clearance level.
     *
     * @param subjectName The new subject's name.
     * @param maxLevel    The subject's maximum security level.
     * @author Brian Arvidson
     */
    public void newSubject(String subjectName, SecurityLevel467 maxLevel)
    {
        Statement stmt = null; // sql statement
        String query = ""; // query string
        String catStr = this.getCatString(maxLevel); // get the categories as a string

        try
        {
            // create a statement
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            // query to insert a new subject into DB
            query = "INSERT INTO " + dbName + "." + entityTable
                    + " (`" + entityName + "`,`"
                    + subjectOrObject + "`,`"
                    + max_sensitivity + "`,`"
                    + max_category + "`,`"
                    + curr_sensitivity + "`,`"
                    + curr_category + "`) "
                    + "VALUES ('"
                    + subjectName
                    + "','1','"
                    + maxLevel.sensitivity.ordinal() + "',"
                    + catStr + ",'"
                    + +maxLevel.sensitivity.ordinal() + "',"
                    + catStr + ")";

            // execute the query, print result and close statement
            stmt.executeUpdate(query);

            //Add to acm table subject 0 is root owner
            query = "INSERT INTO " + dbName + "." + acm
                    + " (`subject`, `entity`, `granter`, `right`"
                    + ") VALUES ('subject0', '"
                    + subjectName
                    + "', 'subject0', 'o')";

            stmt.executeUpdate(query); //execute (insert the row)

            //System.out.println("OK");
            stmt.close();
        }
        catch (MySQLIntegrityConstraintViolationException e) //Predconition fails
        {
            System.out.println("In newSubject: " + e);
            //System.out.println("NO");
        }
        catch (SQLException e) // Other sql exceptions
        {
            System.out.println("In newSubject: " + e);
            //System.out.println("NO");
        }
    }

    /**
     * Creates a new object with a security classification.
     *
     * @param objectName  The new object's name.
     * @param level       The classification of the new object.
     * @author Brian Arvidson
     */
    public void newObject(String objectName, SecurityLevel467 level)
    {
        Statement stmt = null; // sql statement
        String query = ""; // query string
        String catStr = this.getCatString(level); // get the categories as a string

        try
        {
            // create a statement
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
                    + curr_category + "`) "
                    + "VALUES ('"
                    + objectName
                    + "','0','0','','"
                    + level.sensitivity.ordinal() + "',"
                    + catStr + ")";

            // execute the query, print results and close statement
            stmt.executeUpdate(query);

            //Add to acm table subject 0 is root owner
            query = "INSERT INTO " + dbName + "." + acm
                    + " (`subject`, `entity`, `granter`, `right`"
                    + ") VALUES ('subject0', '"
                    + objectName
                    + "', 'subject0', 'o')";

            stmt.executeUpdate(query); //execute (insert the row)

            //System.out.println("OK");
            stmt.close();
        }
        catch (MySQLIntegrityConstraintViolationException e) //Predconition fails
        {
            System.out.println("In newObject: " + e);
            //System.out.println("NO");
        }
        catch (SQLException e) // other sql exceptions
        {
            System.out.println("In newObject: " + e);
            //System.out.println("NO");
        }
    }

    /**
     * Set's a subject's current security clearance to 'level', which must be dominated by the
     * subject's maximum security level. Returns "OK" on success, "NO" otherwise.
     *
     * @param subjectName  The name of the subject.
     * @param level        The new clearance level of the subject.
     * @return             {"OK", "NO"}
     * @author Brandon Andersen
     */
    public String updateSL(String subjectName, SecurityLevel467 level)
    {
        String rtnStr = "NO"; // return string
        String query = ""; // query string
        Statement stmt = null; // sql statement

        // create a temporary subject with the new level to allow testing
        // dominance inside the DB itself
        this.newSubject("temp", level);

        // see if the subject to have its SL updated dominates (based on max level)
        // the temp subject with the level to update to
        if (this.maxDominates(subjectName, "temp"))
        {
            // if so, create update query to update subject's current
            // level to the new level
            query = "UPDATE " + entityTable
                    + " SET " + curr_sensitivity + " = '" + level.sensitivity.ordinal()
                    + "', " + curr_category + " = " + this.getCatString(level)
                    + " WHERE " + entityName + " = '" + subjectName + "';";

            try
            {
                // create and execute the query
                stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                stmt.execute(query);
                rtnStr = "OK";
                stmt.close();
            }
            catch (SQLException e) // handle sql exceptions
            {
                System.out.println("In updateSL: " + e);
                rtnStr = "NO";
            }
        }
        // if the new level is not dominated by the subjects max level
        else
        {
            rtnStr = "NO";
        }

        // query to delete temp subject created for testing
        query = "DELETE FROM " + entityTable
                + " WHERE " + entityName + "='temp';";

        try
        {
            // create and execute the statement
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            stmt.execute(query);
            stmt.close();
        }
        catch (SQLException e) // handle sql exceptions
        {
            System.out.println("In updateSL: " + e);
        }

        // return the result
        return rtnStr;
    }

    /**
     * Object's current security classification is increased to 'level'. The new level should dominate
     * the old security classification level of the object. Returns "OK" on success, "NO" otherwise.
     *
     * @param objectName  The name of the object.
     * @param level       The new classification level of the object.
     * @return            {"OK", "NO"}
     * @author Anthony Lozano
     */
    public String classifyOL(String objectName, SecurityLevel467 level)
    {
        String rtnStr = "NO"; // return string
        String query = ""; // query string
        Statement stmt = null; // sql statement

        // create a temporary object to allow comparison within the DB using
        // dominates with the new level
        this.newObject("temp", level);

        // See if the new level dominates the object's current level
        if (this.dominates("temp", objectName))
        {
            // if so, create update query to update object's current level
            query = "UPDATE " + entityTable
                    + " SET " + curr_sensitivity + " = '" + level.sensitivity.ordinal()
                    + "', " + curr_category + " = " + this.getCatString(level)
                    + " WHERE " + entityName + " = '" + objectName + "';";

            try
            {
                // execute the query
                stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                stmt.execute(query);
                rtnStr = "OK";
                stmt.close();
            }
            catch (SQLException e) // handle sql exceptions
            {
                System.out.println("In classifyOL: " + e);
                rtnStr = "NO";
            }
        }
        // If new level did not dominate current level
        else
        {
            rtnStr = "NO";
        }

        // query to delete temp object
        query = "DELETE FROM " + entityTable
                + " WHERE " + entityName + "='temp';";

        try
        {
            // execute query to delete temp object
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            stmt.execute(query);
            stmt.close();
        }
        catch (SQLException e) // handle sql exceptions
        {
            System.out.println("In classifyOL: " + e);
        }

        // reutrn the result
        return rtnStr;
    }

    /**
     * A subject whose clearance dominates the object's classification lowers the security level of
     * the object. Returns "OK" on success, "NO" otherwise.
     *
     * @param subjectName  The name of the subject declassifying the object.
     * @param objectName   The name of the sanitized object.
     * @param level        The new level of the sanitized object.
     * @return             {"OK", "NO"}
     * @author Justin Paglierani
     */
    public String declassifyOL(String subjectName, String objectName, SecurityLevel467 level)
    {
        String rtnStr = "NO"; //return string
        String query = ""; // query string
        Statement stmt = null; // sql statement

        // create a temporary object to allow comparison within the DB using
        // dominates with the new level
        this.newObject("temp", level);

        // check to see if the subject trying to declass the object dominates
        // the object and if the object's level dominates the new level to ensure
        // that the level is being lowered (the object's data set is being reduced
        // to a subset of its current data set)
        // Note: this prevents sanitizing between two non-comparable categories
        // such as from {PHX} to {ABQ}
        if (this.dominates(subjectName, objectName) && this.dominates(objectName, "temp"))
        {
            // if so, create a query to update the objects current level to
            // the new level
            query = "UPDATE " + entityTable
                    + " SET " + curr_sensitivity + " = '" + level.sensitivity.ordinal()
                    + "', " + curr_category + " = " + this.getCatString(level)
                    + " WHERE " + entityName + " = '" + objectName + "';";

            try
            {
                // create and execute the update query
                stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                stmt.execute(query);
                rtnStr = "OK";
            }
            catch (SQLException e) // handle sql exceptions
            {
                System.out.println("In declassifyOL: " + e);
                rtnStr = "NO";
            }
        }
        // case where the subject's level does not dominate the objects level
        else
        {
            rtnStr = "NO";
        }

        // query to delete temp object
        query = "DELETE FROM " + entityTable
                + " WHERE " + entityName + "='temp';";

        try
        {
            // execute query to delete temp object
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            stmt.execute(query);
            stmt.close();
        }
        catch (SQLException e) // handle sql exceptions
        {
            System.out.println("In classifyOL: " + e);
        }

        // return result
        return rtnStr;
    }

    /**
     * Returns "OK" when the subject can perform the action on the object.
     *
     * @param subjectName  The name of the subject.
     * @param objectName   The name of the object.
     * @param action       The requested action. {'r', 'w', 'a', 'e'}
     * @return             {"OK", "NO"}
     * @author Brian Arvidson
     */
    public String access(String subjectName, String objectName, String action)
    {
        String rtnStr = "NO"; // return string

        // booleans for testing conditions
        boolean simple = false;
        boolean star = false;

        // test conditions for simple property based on action
        switch (action.charAt(0))
        {
            case execute:
            case write_only:
                // no reading occuring so no info flow
                simple = true;
                break;
            case read_only:
            case read_write:
                // reading occuring so flow of read info must be up
                simple = this.maxDominates(subjectName, objectName);
                break;
            default:
                simple = false;
                break;
        }

        // test conditions for star property based on action
        switch (action.charAt(0))
        {
            case execute:
                // no info flow so okay
                star = true;
                break;
            case write_only:
                // only appending, so flow of write data must be up
                star = this.dominates(objectName, subjectName);
                break;
            case read_only:
                // only reading, so flow of read data must be up
                star = this.dominates(subjectName, objectName);
                break;
            case read_write:
                // reading and writing, so flow of data must be horizontal
                star = this.dominates(subjectName, objectName) && this.dominates(objectName, subjectName);
                break;
            default:
                star = false;
                break;
        }

        // check conditions and assign return string result
        if (simple && star && this.checkRights(objectName, subjectName, action).equals("OK"))
        {
            rtnStr = "OK";
        }
        else
        {
            rtnStr = "NO";
        }

        // return result
        return rtnStr;
    }

    /**
     * Returns true when the subject current level dominates object level.
     *
     * @param subjectName  The name of the subject.
     * @param objectName   The name of the object.
     * @return             {true, false}
     * @author Brandon Andersen
     */
    protected boolean dominates(String subjectName, String objectName)
    {
        // result bool
        boolean dom = false;

        // Statement for queries
        Statement stmt = null;

        // query to test dominance within DB
        String query = "SELECT A.entityName as subj, "
                + "B.entityName as obj, "
                + "A.curr_sensitivity as subjLvl, "
                + "B.curr_sensitivity as objLvl, "
                + "LPAD(BIN(A.curr_category+0),11,'0') as subjcat, "
                + "LPAD(BIN(B.curr_category+0),11,'0') as objcat, "
                + "LPAD(BIN(A.curr_category+0 & B.curr_category+0),11,'0') as result "
                + "FROM " + entityTable + " AS A, "
                + entityTable + " AS B "
                + "HAVING subj='" + subjectName
                + "' AND obj='" + objectName
                + "' AND objcat=result AND objLvl<=subjLvl;";

        // try-catch block for SQLExceptions
        try
        {
            // Create the statment object
            stmt = con.createStatement();

            // Get the result set for the query
            ResultSet rs = stmt.executeQuery(query);

            // check to see if it has at least one row, indicating that the subject dominates object
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

        // return the result
        return dom;
    }

    /**
     * Returns true when the subject max level dominates object level.
     *
     * @param subjectName  The name of the subject.
     * @param objectName   The name of the object.
     * @return             {true, false}
     * @author Brandon Andersen
     */
    protected boolean maxDominates(String subjectName, String objectName)
    {
        // return bool
        boolean dom = false;

        // Statement for queries
        Statement stmt = null;

        // query to check if subject max level dominates object level
        String query = "SELECT A.entityName as subj, "
                + "B.entityName as obj, "
                + "A.max_sensitivity as subjLvl, "
                + "B.curr_sensitivity as objLvl, "
                + "LPAD(BIN(A.max_category+0),11,'0') as subjcat, "
                + "LPAD(BIN(B.curr_category+0),11,'0') as objcat, "
                + "LPAD(BIN(A.max_category+0 & B.curr_category+0),11,'0') as result "
                + "FROM " + entityTable + " AS A, "
                + entityTable + " AS B "
                + "HAVING subj='" + subjectName
                + "' AND obj='" + objectName
                + "' AND objcat=result AND objLvl<=subjLvl;";

        // try-catch block for SQLExceptions
        try
        {
            // Create the statment object
            stmt = con.createStatement();

            // Get the result set for the query
            ResultSet rs = stmt.executeQuery(query);

            // check to see if it has at least one row, indicating that the subject dominates object
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
            System.out.println("In maxDominates: " + e);
            dom = false;
        }

        //return the result
        return dom;
    }

    /**
     * Returns string representing the list of categories
     *
     * @param level         SecurityLevel467
     * @return              String
     * @author Brandon Andersen
     */
    protected String getCatString(SecurityLevel467 level)
    {
        // return string
        String catStr = "'";

        // loop through category array to collect categories into a string
        // Note: if a state is found, also assign its cities since the cities
        // are subsets of states
        for (int i = 0; i < level.categories.length; i++)
        {
            switch (level.categories[i])
            {
                case CA:
                    catStr = catStr + "CA,SFO,LAX,";
                    break;
                case AZ:
                    catStr = catStr + "AZ,PHX,TUS,";
                    break;
                case NM:
                    catStr = catStr + "NM,ABQ,";
                    break;
                case TX:
                    catStr = catStr + "TX,IAH,DAL,";
                    break;
                case SFO:
                    catStr = catStr + "SFO,";
                    break;
                case LAX:
                    catStr = catStr + "LAX,";
                    break;
                case PHX:
                    catStr = catStr + "PHX,";
                    break;
                case TUS:
                    catStr = catStr + "TUS,";
                    break;
                case ABQ:
                    catStr = catStr + "ABQ,";
                    break;
                case IAH:
                    catStr = catStr + "IAH,";
                    break;
                case DAL:
                    catStr = catStr + "DAL,";
                    break;
                default:
                    break;
            }
        }

        // check to see if length is 1, indicates empty category set
        if (catStr.length() == 1)
        {
            // if empty, just return an empty single paren set
            catStr = "''";
        }
        else
        {
            // otherwise, clip of the trailing comma and append a single paren
            catStr = catStr.substring(0, catStr.length() - 1) + "'";
        }

        //return the resulting string
        return catStr;
    }

    /**
     * Subject X revokes right R on the entity E_Name from subject Y.
     *
     * @param X         Revoker of the right
     * @param Y         Subject whose right is being revoked
     * @param R         {"r", "u", "c", "o", "d", "t"}
     * @param E_Name    The entity on which Y's right is being revoked
     * @return          "OK" on success, "NO" otherwise
     * @author Brandon
     */
    public String revoke(String X, String Y, String R, String E_Name)
    {
        // The return string, initially assumed to be "NO"
        String rtnStr = "NO";

        // First, check to see that we are not trying to revoke from subject0
        // since this is not allowed
        if (Y.equals(subject0))
        {
            rtnStr = "NO";
        } // Trying to revoke from a subject other than subject0, so keep going
        else
        {
            // Check to see if the revoker (X) is the owner of the entity (E_Name)
            if (this.checkRights(E_Name, X, own).equals("OK"))
            {
                // Create the query string to check for if the subject (Y) has
                // the right (R) on the entity (E_Name) in question
                String query = "SELECT * FROM " + dbName + "." + acm
                        + " WHERE " + subject + " = '" + Y
                        + "' AND " + entity + " = '" + E_Name
                        + "' AND " + right + " = '" + R + "'";

                // try-catch block for SQLExceptions
                try
                {
                    // Create a Statement object that will produce updateable result sets
                    Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

                    // Get the result set for the query
                    ResultSet rs = stmt.executeQuery(query);

                    // Test the result set to see if it is updateble
                    if (rs.getConcurrency() == ResultSet.CONCUR_UPDATABLE)
                    {
                        // Check to see if it has at least one row
                        if (rs.next())
                        {
                            // If trying to revoke 'o', make sure the revoker (X) is subject0
                            if (R.equals(own) && X.equals(subject0))
                            {
                                // Revoke ownership from revokee on the given entity
                                do
                                {
                                    rs.deleteRow();
                                }
                                while (rs.next());

                                // return string is "OK" since we at least revoked ownership
                                rtnStr = "OK";
                            }
                            else
                            {
                                // Revoke any right from revokee on the given entity
                                do
                                {
                                    rs.deleteRow();
                                }
                                while (rs.next());

                                // Resturn string is "OK" since we at least revoked one right
                                rtnStr = "OK";
                            }
                        } // If the right trying to be revoked is not present for the subject on the entity
                        else
                        {
                            rtnStr = "NO";
                        }
                    } // If the result set for the rights was not updateable
                    else
                    {
                        rtnStr = "NO";
                    }

                    // Close the result set and statment
                    rs.close();
                    stmt.close();
                } // Catch block for SQLExceptions
                catch (SQLException e)
                {
                    System.out.println("In Revoke: " + e);
                    rtnStr = "NO";
                }
            } // Someone other than an owner of E_Name is trying to revoke, not allowed
            else
            {
                rtnStr = "NO";
            }
        }

        // Return the final return string, either "OK" if at least one right was revoked, otherwise "NO"
        return rtnStr;
    }
}
