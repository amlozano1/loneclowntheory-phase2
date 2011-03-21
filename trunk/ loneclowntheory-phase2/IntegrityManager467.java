/**
 * 
 */
package TA;

/**
 * Integrity manager module.  See Section 6.2 in the textbook.
 * 
 * @author TA
 *
 */
public interface IntegrityManager467 extends BellLaPadula467 {
	/**
	 * Creates a new subject with a maximum security clearance level, and integrity level.
	 * 
	 * @param subjectName The new subject's name.
	 * @param maxLevel    The subject's maximum security level.
	 * @param integrity   The subject's integrity level.
	 */
	public void newSubject(String subjectName, SecurityLevel467 maxLevel, IntLevel467 integrity);
	
	
	/**
	 * Creates a new object with a security classification and integrity level.
	 * 
	 * @param objectName  The new object's name.
	 * @param level       The classification of the new object.
	 * @param integrity   The object's integrity level.
	 */
	public void newObject(String objectName, SecurityLevel467 level, IntLevel467 integrity);
	
	
	/**
	 * Returns "OK" when the subject can perform the action on the object. Behaves as if the subject
	 * is actually accessing the object in that it also updates the subject and object's integrity 
	 * levels based on the action.
	 * 
	 * @param subjectName  The name of the subject.
	 * @param objectName   The name of the object.
	 * @param action       The requested action. {'r', 'u', 'w'}
	 * @return             {"OK", "NO"}
	 */
	@Override 
	public String access(String subjectName, String objectName, String action);
}
