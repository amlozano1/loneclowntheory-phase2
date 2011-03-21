/**
 * Don't forget that your implementation of this interface should be in
 * a package with the same name as your group name.
 */
package loneclowntheory;

/**
 * Use this interface as a guide for developing your own implementation
 * of this policy manager module.
 *
 * @author TA
 *
 */
public interface AuthPolicyManager467
{
    /**
     * Creates a new subject.  Initially, subject "subject0" is the "owner" of all new entities.
     *
     * @param subjectName Name of new subject
     */
    public void newSubject(String subjectName);

    /**
     * Creates a new object.  Initially, subject "subject0" is the "owner" of all new entities.
     *
     * @param objectName Name of new object
     */
    public void newObject(String objectName);

    /**
     * Removes the subject with subjectName == Name.  Subject "subject0" cannot be removed.  All
     * right entries associated with the subject are also removed.
     *
     * @param Name      Subject's name to be removed
     */
    public void removeSubject(String Name);

    /**
     * Removes the object with objectName == Name.  All right entries on the object are also removed
     * from the system.
     *
     * @param Name      Object's name to be removed
     */
    public void removeObject(String Name);

    /**
     * Subject X grants subject Y right R on the entity E_Name.
     *
     * @param X       Subject granting the right
     * @param Y       Subject being granted the right
     * @param R       {"r", "u", "c", "o", "d", "t"}
     * @param E_Name  The entity which subject Y will have rights
     * @return        "OK" on success, "NO" otherwise
     */
    public String grant(String X, String Y, String R, String E_Name);

    /**
     * Subject X gets right R on the entity E_Name by using its 't' or 'd' right on E_Name.
     *
     * @param X       Subject taking the right
     * @param R       {"r", "u", "c"}
     * @param E_Name  The entity which subject Y will have rights
     * @return        "OK" on success, "NO" otherwise
     */
    public String take(String X, String R, String E_Name);

    /**
     * Subject X revokes right R on the entity E_Name from subject Y.
     *
     * @param X         Revoker of the right
     * @param Y         Subject whose right is being revoked
     * @param R         {"r", "u", "c", "o", "d", "t"}
     * @param E_Name    The entity on which Y's right is being revoked
     * @param cascades  "N" for without cascades, "C" for with cascades
     * @return          "OK" on success, "NO" otherwise
     */
    public String revoke(String X, String Y, String R, String E_Name, String cascades);

    /**
     * Queries the acm table if subject X has right R on entity E_Name.
     *
     * @param E_Name  Entity on which subject's rights are being checked
     * @param X       Subject whose right is being checked
     * @param R       {"r", "u", "c", "o", "d", "t"}
     * @return        "OK" on success, "NO" otherwise
     */
    public String checkRights(String E_Name, String X, String R);
}
