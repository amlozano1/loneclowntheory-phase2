/**
 *
 */
package loneclowntheory;

import java.sql.Connection;
import java.sql.Statement;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Policy manager for the Bell-LaPadula Model. See Section 5.2 in the textbook.
 *
 * @author TA
 *
 */
public class LCTBellLaPadula467 extends LCTAuthPolicyManager467 implements BellLaPadula467
{

    private String dbms;
    private String dbName;
    private Connection con;
    //define table names
    public static final String acm = "acm";
    public static final String entityTable = "entityTable";
    ///define columns in acm
    public static final String subject = acm + ".subject";
    public static final String entity = acm + ".entity";
    public static final String right = acm + ".right";
    public static final String granter = acm + ".granter";
    public static final String timestamp = acm + ".timestamp";
    //define rights for acm
    public static final String read_only = "r";
    public static final String read_write = "w";
    public static final String write_only = "a";
    public static final String execute = "e";
    //define columns in entityTable
    public static final String entityID = entityTable + ".entityID";
    public static final String entityName = entityTable + ".entityName";
    public static final String subjectOrObject = entityTable + ".subject_or_object";
    public static final String sensitivity = entityTable + ".sensitivity";
    public static final String category = entityTable + ".category";
    //define other constants
    public static final String subject0 = "subject0";

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
     */
    public void newSubject(String subjectName, SecurityLevel467 maxLevel)
    {
        Statement stmt = null;

        String query = "";

        String catStr = "'";

        for (int i = 0; i < maxLevel.categories.length; i++)
        {
            switch (maxLevel.categories[i])
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
                    catStr = catStr + "NM,ABQ,";
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

        if (catStr.length() == 1)
        {
            catStr = "''";
        }
        else
        {
            catStr = catStr.substring(0, catStr.length() - 1) + "'";
        }

        try
        {
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            query = "INSERT INTO " + dbName + "." + entityTable
                    + " (`entityName`, `subject_or_object`, `sensitivity`, `category`) "
                    + "VALUES ('" + subjectName + "','1','" + maxLevel.sensitivity.ordinal() + "'," + catStr + ")";

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
            System.out.println(e);
        }
    }

    /**
     * Creates a new object with a security classification.
     *
     * @param objectName  The new object's name.
     * @param level       The classification of the new object.
     */
    public void newObject(String objectName, SecurityLevel467 level)
    {
        Statement stmt = null;

        String query = "";

        String catStr = "'";

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
                    catStr = catStr + "NM,ABQ,";
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

        if (catStr.length() == 1)
        {
            catStr = "''";
        }
        else
        {
            catStr = catStr.substring(0, catStr.length() - 1) + "'";
        }

        try
        {
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            query = "INSERT INTO " + dbName + "." + entityTable
                    + " (`entityName`, `subject_or_object`, `sensitivity`, `category`) "
                    + "VALUES ('" + objectName + "','0','" + level.sensitivity.ordinal() + "'," + catStr + ")";

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
            System.out.println(e);
        }
    }

    /**
     * Set's a subject's current security clearance to 'level', which must be dominated by the
     * subject's maximum security level. Returns "OK" on success, "NO" otherwise.
     *
     * @param subjectName  The name of the subject.
     * @param level        The new clearance level of the subject.
     * @return             {"OK", "NO"}
     */
    public String updateSL(String subjectName, SecurityLevel467 level)
    {
        return "";
    }

    /**
     * Object's current security classification is increased to 'level'. The new level should dominate
     * the old security classification level of the object. Returns "OK" on success, "NO" otherwise.
     *
     * @param objectName  The name of the object.
     * @param level       The new classification level of the object.
     * @return            {"OK", "NO"}
     */
    public String classifyOL(String objectName, SecurityLevel467 level)
    {
        return "";
    }

    /**
     * A subject whose clearance dominates the object's classification lowers the security level of
     * the object. Returns "OK" on success, "NO" otherwise.
     *
     * @param subjectName  The name of the subject declassifying the object.
     * @param objectName   The name of the sanitized object.
     * @param level        The new level of the sanitized object.
     * @return             {"OK", "NO"}
     */
    public String declassifyOL(String subjectName, String objectName, SecurityLevel467 level)
    {
        return "";
    }

    /**
     * Returns "OK" when the subject can perform the action on the object.
     *
     * @param subjectName  The name of the subject.
     * @param objectName   The name of the object.
     * @param action       The requested action. {'r', 'w', 'a', 'e'}
     * @return             {"OK", "NO"}
     */
    public String access(String subjectName, String objectName, String action)
    {
        return "";
    }

    public boolean dominates(String subjectName, String objectName)
    {
        boolean dom = false;

        // Statement for queries
        Statement stmt = null;

        String query = "SELECT A.entityName as subj, "
                + "B.entityName as obj, "
                + "A.sensitivity as subjLvl, "
                + "B.sensitivity as objLvl, "
                + "LPAD(BIN(A.category+0),11,'0') as subjcat, "
                + "LPAD(BIN(B.category+0),11,'0') as objcat, "
                + "LPAD(BIN(A.category+0 & B.category+0),11,'0') as result "
                + "FROM loneclowntheory.entityTable AS A, "
                + "loneclowntheory.entityTable AS B "
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
            // Debug print
            // System.out.println("In checkRights: " + e);
            // Failure, so return string set to "NO"
            dom = false;
        }

        return dom;
    }
}
