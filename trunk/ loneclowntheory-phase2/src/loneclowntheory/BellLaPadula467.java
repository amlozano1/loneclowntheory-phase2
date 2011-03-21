/**
 * 
 */
package loneclowntheory;

/**
 * Policy manager for the Bell-LaPadula Model. See Section 5.2 in the textbook.
 * 
 * @author TA
 *
 */
public interface BellLaPadula467 extends AuthPolicyManager467 {

    /**
     * Creates a new subject with a maximum security clearance level.
     *
     * @param subjectName The new subject's name.
     * @param maxLevel    The subject's maximum security level.
     */
    public void newSubject(String subjectName, SecurityLevel467 maxLevel);

    /**
     * Creates a new object with a security classification.
     *
     * @param objectName  The new object's name.
     * @param level       The classification of the new object.
     */
    public void newObject(String objectName, SecurityLevel467 level);

    /**
     * Set's a subject's current security clearance to 'level', which must be dominated by the
     * subject's maximum security level. Returns "OK" on success, "NO" otherwise.
     *
     * @param subjectName  The name of the subject.
     * @param level        The new clearance level of the subject.
     * @return             {"OK", "NO"}
     */
    public String updateSL(String subjectName, SecurityLevel467 level);

    /**
     * Object's current security classification is increased to 'level'. The new level should dominate
     * the old security classification level of the object. Returns "OK" on success, "NO" otherwise.
     *
     * @param objectName  The name of the object.
     * @param level       The new classification level of the object.
     * @return            {"OK", "NO"}
     */
    public String classifyOL(String objectName, SecurityLevel467 level);

    /**
     * A subject whose clearance dominates the object's classification lowers the security level of
     * the object. Returns "OK" on success, "NO" otherwise.
     *
     * @param subjectName  The name of the subject declassifying the object.
     * @param objectName   The name of the sanitized object.
     * @param level        The new level of the sanitized object.
     * @return             {"OK", "NO"}
     */
    public String declassifyOL(String subjectName, String objectName, SecurityLevel467 level);

    /**
     * Returns "OK" when the subject can perform the action on the object.
     *
     * @param subjectName  The name of the subject.
     * @param objectName   The name of the object.
     * @param action       The requested action. {'r', 'w', 'a', 'e'}
     * @return             {"OK", "NO"}
     */
    public String access(String subjectName, String objectName, String action);
}
