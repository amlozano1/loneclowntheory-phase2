/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loneclowntheory;

import java.sql.Connection;
import java.sql.Statement;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Brandon
 */
public class LCTAuthPolicyManager467 implements AuthPolicyManager467
{

    protected String dbms;
    protected String dbName;
    protected Connection con;
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
    public static final String read = "r";
    public static final String update = "u";
    public static final String own = "o";
    public static final String copy = "c";
    public static final String takeReadUpdate = "t";
    public static final String takeCopy = "d";
    //define columns in entityTable
    public static final String entityID = entityTable + ".entityID";
    public static final String entityName = entityTable + ".entityName";
    public static final String subjectOrObject = entityTable + ".subject_or_object";
    //define other constants
    public static final String subject0 = "subject0";

    public LCTAuthPolicyManager467()
    {
        super();
    }

    public LCTAuthPolicyManager467(Connection connArg, String dbmsArg, String dbNameArg)
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
     * Creates a new subject.  Initially, subject "subject0" is the "owner" of all new entities.
     *
     * @param subjectName Name of new subject
     */
    public void newSubject(String subjectName)
    {
        Statement stmt = null; //SQL statement wrapper

        //string to build the query in
        String query = "SELECT * FROM " + dbName + "." + entityTable + " WHERE " + entityName + "= '" + subjectName + "'";

        try
        {
            //open connection to db
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            //query
            ResultSet rs = stmt.executeQuery(query);

            //////////////////////
            //Add to entitytable//
            //////////////////////
            rs.moveToInsertRow(); //move cursor to updatable row
            rs.updateString(entityName, subjectName); //add name
            rs.updateString(subjectOrObject, "1"); //signify subejct
            rs.insertRow(); //insert the row

            /////////////////////////////
            //Add to acm table         //
            //subject 0 is root owner  //
            /////////////////////////////

            query = "INSERT INTO " + dbName + "." + acm + " (`subject`, `entity`, `granter`, `right`) VALUES ('subject0', '" + subjectName + "', 'subject0', 'o')";
            stmt.executeUpdate(query); //execute (insert the row)

            /*
            System.out.println("newSubject(" + subjectName
            + ") completed successfully");*/
            System.out.println("OK");

            rs.close(); //close the recordset
            stmt.close(); //close the statement
        }
        catch (MySQLIntegrityConstraintViolationException e) //Predconition fails
        {
            //System.out.println("newSubject() predoncition failed: " + e.getMessage());
            System.out.println("NO");
        }
        catch (SQLException e) //generic SQL error
        {
            System.out.println(e); //spit out SQL error
        }
    }

    /**
     * Creates a new object.  Initially, subject "subject0" is the "owner" of all new entities.
     *
     * @param objectName Name of new object
     */
    public void newObject(String objectName)
    {
        //wrapper for statement
        Statement stmt = null;

        //query skeleton
        String query = "SELECT * FROM " + dbName + "." + entityTable + " WHERE " + entityName + "= '" + objectName + "'";

        try
        {
            //open communication with db
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            //run the query
            ResultSet rs = stmt.executeQuery(query);

            //////////////////////
            //Add to entitytable//
            //////////////////////

            rs.moveToInsertRow(); //move cursor to insert row
            rs.updateString(entityName, objectName); //plug in name
            rs.updateString(subjectOrObject, "0"); //signify as object
            rs.insertRow(); //complete the insert

            //////////////////////////
            //Add to acmtable       //
            //subject0 is root owner//
            //////////////////////////

            query = "INSERT INTO " + dbName + "." + acm + " (`subject`, `entity`, `granter`, `right`) VALUES ('subject0', '" + objectName + "', 'subject0', 'o')";
            stmt.executeUpdate(query);


            /*
            System.out.println("newObject(" + objectName
            + ") completed successfully");*/
            System.out.println("OK");

            rs.close(); //close resultset
            stmt.close(); //close the db conn
        }
        catch (MySQLIntegrityConstraintViolationException e) //Predconition fails
        {
            //System.out.println("newObject() predoncition failed: " + e.getMessage());
            System.out.println("NO");
        }
        catch (SQLException e)
        {
            System.out.println(e); //spit out sql error
        }
    }

    /**
     * Removes the subject with subjectName == Name.  Subject "subject0" cannot be removed.  All
     * right entries associated with the subject are also removed.
     *
     * @param Name      Subject's name to be removed
     */
    public void removeSubject(String Name)
    {
        Statement stmt = null;

        String query = "SELECT * FROM " + dbName + "." + entityTable
                + " WHERE " + entityName + " = '" + Name
                + "' AND " + subjectOrObject + " = 1";

        try
        {
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            ResultSet rs = stmt.executeQuery(query);

            //Precondition: Subject exist in database
            //This also doesn't allow subject0 to be removed form the DB.
            if (!rs.next() || Name.equals(subject0))
            {
                if (Name.equals(subject0))
                {
                    System.out.println("NO! subject0 cannot be removed!");
                }
                else
                {
                    System.out.println("NO, " + Name + " does not exsits.");
                }
            }
            else
            {
                if (rs.getConcurrency() == ResultSet.CONCUR_UPDATABLE)
                {
                    rs.deleteRow();

                    System.out.println("OK, " + Name + " exsits and is being removed.");
                }
            }

            stmt.close();
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
    }

    /**
     * Removes the object with objectName == Name.  All right entries on the object are also removed
     * from the system.
     *
     * @param Name      Object's name to be removed
     */
    public void removeObject(String Name)
    {
        Statement stmt = null;

        String query = "SELECT * FROM " + dbName + "." + entityTable
                + " WHERE " + entityName + " = '" + Name
                + "' AND " + subjectOrObject + " = 0";

        try
        {
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            ResultSet rs = stmt.executeQuery(query);

            //Precondition: Object exist in database
            //Even though subject0 should be considered a subject_or_object which
            //would not allow for his removale by this function it is also checked here just in case.
            if (!rs.next() || Name.equals(subject0))
            {
                System.out.println("No, " + Name + " does not exsits.");
            }
            else
            {
                if (rs.getConcurrency() == ResultSet.CONCUR_UPDATABLE)
                {
                    rs.deleteRow();

                    System.out.println("OK, " + Name + " exsits and is being removed.");
                }
            }

            stmt.close();
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
    }

    /**
     * Subject granterName grants subject granteeName right righToBeGranted  on the entityName.
     *
     * @param granterName       Subject granting the right
     * @param granteeName       Subject being granted the right
     * @param righToBeGranted      {"r", "u", "c", "o", "d", "t"}
     * @param entityName  The entity which subject Y will have rights
     * @return        "OK" on success, "NO" otherwise
     */
    public String grant(String granterGranting, String grantee, String rightToBeGranted, String entityGrantedOn)
    {
        String returnString = "NO"; //start pessimistic
        String rightName;//string to hold the database name for the rights
        String query;
        if (rightToBeGranted.length() != 1)//should only pass one character in the right String
        {
            return "NO";
        }
        //begin preparing the query database
        try
        {
            Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            //ensure that the grantee is a subject
            query = "SELECT * FROM " + entityTable + " WHERE " + subjectOrObject + "='0' AND " + entityName + "='" + grantee + "';";

            if (stmt.executeQuery(query).next())//if the grantee is not a subject
            {
                return "NO";
            }
            if (rightToBeGranted.equals("o"))
            {
                query = "SELECT * FROM " + acm + " WHERE " + entity + "='" + entityGrantedOn + "' AND " + right + "='o';";
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next())//if there is an owner present
                {
                    if (rs.getString(subject).equals("subject0"))//if the owner present is subject0
                    {
                        if (rs.next())//see if anyone else owns the entity
                        {
                            return "NO";//if they do, return no
                        }
                    }
                    else//the subject that owns the object is not subject 0 so...
                    {
                        return "NO";
                    }
                }
            }
            // look up the grantee's rights
            if (checkRights(entityGrantedOn, granterGranting, "o").equals("OK") ||//if the granter is the owner OR the granter has the rights and has the copy right
                    checkRights(entityGrantedOn, granterGranting, rightToBeGranted).equals("OK") && checkRights(entityGrantedOn, granterGranting, "c").equals("OK"))
            {
                query = "INSERT INTO " + acm + " (" + subject + " ," + entity + " ," + right + " ," + granter + ")"
                        + " VALUES ('" + grantee + "', '" + entityGrantedOn + "', '" + rightToBeGranted + "', '" + granterGranting + "');";
                stmt.execute(query);
                ///INSERT INTO acm (subject,entity,right,granter) VALUES (grantee,entityGrantedOn,right,granter)
                returnString = "OK";
            }
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
        return returnString;

    }

    /**
     * Subject X gets right R on the entityToTakeRightsOn by "taking" it from
     * subject Y if subject Y has given them the appropriate take right.  Subject Y's
     * rights are unmodified.
     *
     * @param subjectTaking       Subject taking the right
     * @param rightToTake       {"r", "u", "c", "o", "d", "t"}
     * @param entityToTakeRightsOn  The entity which subject Y will have rights
     * @return        "OK" on success, "NO" otherwise
     */
    public String take(String subjectTaking, String rightToTake, String entityToTakeRightsOn)
    {
        String returnString = "NO";//start pessimistic
        try
        {
            Statement stmt = con.createStatement();
            String query = "INSERT INTO " + acm + " (" + subject + ", " + entity + ", " + right + ", " + granter + ")"
                    + " VALUES ('" + subjectTaking + "', '" + entityToTakeRightsOn + "', '" + rightToTake + "', '" + subjectTaking + "');";

            switch (rightToTake.charAt(0))
            {
                case 'r':
                case 'u':
                {
                    if ("OK".equals(checkRights(entityToTakeRightsOn, subjectTaking, "t")))
                    {
                        stmt.execute(query);
                        returnString = "OK";
                    }
                }
                break;
                case 'c':
                {
                    if ("OK".equals(checkRights(entityToTakeRightsOn, subjectTaking, "d")))
                    {
                        stmt.execute(query);
                        returnString = "OK";
                    }
                }
                break;
                default:
                    break;

            }
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
        return returnString;
    }

    /**
     * Subject X revokes right R on the entity E_Name from subject Y.
     *
     * @param X         Revoker of the right
     * @param Y         Subject whose right is being revoked
     * @param R         {"r", "u", "c", "o", "d", "t"}
     * @param E_Name    The entity on which Y's right is being revoked
     * @param cascades  "N" for without cascades, "C" for with cascades
     * @return          "OK" on success, "NO" otherwise
     * @author Brandon
     */
    public String revoke(String X, String Y, String R, String E_Name, String cascades)
    {
        // The return string, initially assumed to be "NO"
        String rtnStr = "NO";

        // Get the status of cascades as a boolean for reuse
        boolean cascade = cascades.equals("C");

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
                                // Check for cascading
                                if (cascade)
                                {
                                    // Query to revoke any rights granted by Y on the given entity
                                    query = "SELECT * FROM " + dbName + "." + acm
                                            + " WHERE " + entity + " = '" + E_Name
                                            + "' AND " + granter + " = '" + Y + "'";

                                    // Create a new Statement to get a different result set
                                    Statement stmtGranted = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

                                    // Get the result set for the granted rights
                                    ResultSet rsGranted = stmtGranted.executeQuery(query);

                                    // Test the result set to see if it is updateble
                                    if (rsGranted.getConcurrency() == ResultSet.CONCUR_UPDATABLE)
                                    {
                                        // Check to see if it has at least one row
                                        if (rsGranted.next())
                                        {
                                            // Call revoke recursivly with the revokee as the revoker since we are revoking ownership with cascade
                                            do
                                            {
                                                // This will be called for each right the the revokee (Y) has granted on the entity (E_Name)
                                                this.revoke(Y, rsGranted.getString(subject), rsGranted.getString(right), E_Name, cascades);
                                            }
                                            while (rsGranted.next());
                                        }
                                    }

                                    // Close the result set and statement for rights granted
                                    rsGranted.close();
                                    stmtGranted.close();
                                }

                                // Revoke ownership from revokee on the given entity
                                do
                                {
                                    rs.deleteRow();
                                }
                                while (rs.next());

                                // return string is "OK" since we at least revoked ownership
                                rtnStr = "OK";
                            } // Revocation of 'c'
                            else if (R.equals(copy))
                            {
                                // Revoke any 'c' from revokee on the entity
                                do
                                {
                                    rs.deleteRow();
                                }
                                while (rs.next());

                                // Check to see if this is a cascading revoke
                                if (cascade)
                                {
                                    // If so, then revoke any 'd', 't', 'r', 'u' granted by the revokee (Y) on the given entity (E_Name)
                                    query = "SELECT * FROM " + dbName + "." + acm
                                            + " WHERE " + entity + " = '" + E_Name
                                            + "' AND " + granter + " = '" + Y
                                            + "' AND (" + right + " = '" + takeCopy
                                            + "' OR " + right + " = '" + takeReadUpdate
                                            + "' OR " + right + " = '" + read
                                            + "' OR " + right + " = '" + update + "')";

                                    // Get the result set for the query
                                    rs = stmt.executeQuery(query);

                                    // Check to see if it has at least one entry
                                    if (rs.next())
                                    {
                                        // If so, delete
                                        do
                                        {
                                            rs.deleteRow();
                                        }
                                        while (rs.next());
                                    }
                                }

                                // Return string is "OK" since we at least revoked 'c'
                                rtnStr = "OK";
                            } // Revocation for all other rights, none of which cascade
                            else if (R.equals(takeCopy) || R.equals(takeReadUpdate) || R.equals(read) || R.equals(update))
                            {
                                // Revoke any 'd', 't', 'r', 'u' from revokee on the given entity
                                do
                                {
                                    rs.deleteRow();
                                }
                                while (rs.next());

                                // Resturn string is "OK" since we at least revoked one right
                                rtnStr = "OK";
                            } // If some other right (R) was passed that was not 'o', 'c', 'd', 't', 'r', 'u'
                            else
                            {
                                rtnStr = "NO";
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
                    // Debug print
                    // System.out.println("In Revoke: " + e);
                    // Failure, so return string set to "NO"
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

    /**
     * Queries the acm table if subject X has right R on entity E_Name.
     *
     * @param E_Name  Entity on which subject's rights are being checked
     * @param X       Subject whose right is being checked
     * @param R       {"r", "u", "c", "o", "d", "t"}
     * @return        "OK" on success, "NO" otherwise
     */
    public String checkRights(String E_Name, String X, String R)
    {
        // Return string initialized to "NO"
        String rtnStr = "NO";

        // Statement for queries
        Statement stmt = null;

        // Query string to check if the subject (X) has right (R) on entity (E_Name)
        String query = "SELECT " + subject + ", " + entity + ", " + right
                + " FROM " + dbName + "." + acm
                + " WHERE " + subject + " = '" + X
                + "' AND " + entity + " = '" + E_Name
                + "' AND " + right + " = '" + R + "'";

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
                rtnStr = "OK";
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
            rtnStr = "NO";
        }

        // Return the return string, either "OK" on success or "NO" on failure
        return rtnStr;
    }
}
