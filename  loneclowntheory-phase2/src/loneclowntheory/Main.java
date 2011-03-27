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
        LCTIntegrityManager467 lctInteg;

        // *********************************************************************
        // BLP TEST CASES
        // *********************************************************************

        System.out.println("*** TESTING BLP ***");

        lct = new LCTBellLaPadula467();

        Category467[] c1 =
        {
            Category467.AZ, Category467.CA, Category467.NM, Category467.TX
        };

        Category467[] c2 =
        {
            Category467.AZ
        };

        Category467[] c3 =
        {
            Category467.PHX
        };

        Category467[] c4 =
        {
            Category467.PHX, Category467.SFO
        };

        Category467[] c5 =
        {
        };
        // create new subject and object to test with at same levels to start
        lct.newSubject("s1", new SecurityLevel467(SensitivityLevel467.SECRET, c2));
        lct.newObject("o1", new SecurityLevel467(SensitivityLevel467.SECRET, c2));
        // test discretionary rights, grant and revoke
        System.out.println("1.0 " + lct.access("s1", "o1", "r")); // NO - missing discretionary right
        System.out.println("1.1 " + lct.grant("subject0", "s1", "r", "o1")); // OK
        System.out.println("1.2 " + lct.grant("subject0", "s1", "w", "o1")); // OK
        System.out.println("1.3 " + lct.grant("subject0", "s1", "a", "o1")); // OK
        System.out.println("1.4 " + lct.grant("subject0", "s1", "e", "o1")); // OK
        System.out.println("1.5 " + lct.access("s1", "o1", "r")); // OK - has discretionary right and same level
        System.out.println("1.6 " + lct.access("s1", "o1", "w")); // OK - has discretionary right and same level
        System.out.println("1.7 " + lct.access("s1", "o1", "a")); // OK - has discretionary right and same level
        System.out.println("1.8 " + lct.access("s1", "o1", "e")); // OK - has discretionary right and same level
        System.out.println("1.9 " + lct.revoke("subject0", "s1", "e", "o1")); // OK
        System.out.println("1.10 " + lct.access("s1", "o1", "e")); // NO - lost discretionary right
        System.out.println("1.11 " + lct.grant("subject0", "s1", "e", "o1")); // OK
        System.out.println("1.12 " + lct.access("s1", "o1", "e")); // OK - has discretionary right and same level
        // test updateSL and see if change affects access as expected for change in sensitivity
        System.out.println("2.0 " + lct.updateSL("s1", new SecurityLevel467(SensitivityLevel467.CONFIDENTIAL, c2))); // OK
        System.out.println("2.1 " + lct.access("s1", "o1", "r")); // NO - s1 no longer dominates o1 based on sensitivity
        System.out.println("2.2 " + lct.access("s1", "o1", "w")); // NO - o1 dominates s1 based on sensitivity
        System.out.println("2.3 " + lct.access("s1", "o1", "a")); // OK - o1 dominates s1 based on sensitivity
        System.out.println("2.4 " + lct.access("s1", "o1", "e")); // OK - execute so okay
        // restore level and retest access
        System.out.println("3.0 " + lct.updateSL("s1", new SecurityLevel467(SensitivityLevel467.SECRET, c2))); // OK
        System.out.println("3.1 " + lct.access("s1", "o1", "r")); // OK
        System.out.println("3.2 " + lct.access("s1", "o1", "w")); // OK
        System.out.println("3.3 " + lct.access("s1", "o1", "a")); // OK
        System.out.println("3.4 " + lct.access("s1", "o1", "e")); // OK
        // try to raise level of subject above max level for both sensitivity and category
        System.out.println("4.0 " + lct.updateSL("s1", new SecurityLevel467(SensitivityLevel467.TOP_SECRET, c2))); // NO - trying to set above max level
        System.out.println("4.1 " + lct.updateSL("s1", new SecurityLevel467(SensitivityLevel467.SECRET, c1))); // NO - trying to set above max level
        // test updateSL for change in category
        System.out.println("5.0 " + lct.updateSL("s1", new SecurityLevel467(SensitivityLevel467.SECRET, c3))); // OK
        System.out.println("5.1 " + lct.access("s1", "o1", "r")); // NO - s1 no longer dominates o1 based on categories
        System.out.println("5.2 " + lct.access("s1", "o1", "w")); // NO - s1 no longer dominates o1 based on categories
        System.out.println("5.3 " + lct.access("s1", "o1", "a")); // OK
        System.out.println("5.4 " + lct.access("s1", "o1", "e")); // OK
        // restore subject current level to max level
        System.out.println("6.0 " + lct.updateSL("s1", new SecurityLevel467(SensitivityLevel467.SECRET, c2))); // OK
        //
        System.out.println("7.0 " + lct.classifyOL("o1", new SecurityLevel467(SensitivityLevel467.TOP_SECRET, c2))); // OK
        System.out.println("7.2 " + lct.classifyOL("o1", new SecurityLevel467(SensitivityLevel467.TOP_SECRET, c1))); // OK

        System.out.println("8.0 " + lct.declassifyOL("s1", "o1", new SecurityLevel467(SensitivityLevel467.SECRET, c2))); // NO - s1 does not dominate o1
        System.out.println("8.1 " + lct.declassifyOL("subject0", "o1", new SecurityLevel467(SensitivityLevel467.SECRET, c2))); // OK - subject0 dominates o1
        System.out.println("8.2 " + lct.declassifyOL("s1", "o1", new SecurityLevel467(SensitivityLevel467.CONFIDENTIAL, c2))); // OK - s1 dominates o1
        System.out.println("8.3 " + lct.declassifyOL("s1", "o1", new SecurityLevel467(SensitivityLevel467.CONFIDENTIAL, c3))); // OK - s1 dominates o1


//        // *********************************************************************
//        // BIBA TEST CASES
//        // *********************************************************************
//        System.out.println("*** TESTING BIBA ***");
//
//        lctInteg = new LCTIntegrityManager467();
//
//        sl = new SecurityLevel467(SensitivityLevel467.SECRET, c1);
//        lctInteg.newSubject("s1", sl, IntLevel467.SOME_EVIDENCE);
//
//
//        sl = new SecurityLevel467(SensitivityLevel467.SECRET, c2);
//        lctInteg.newSubject("s2", sl, IntLevel467.UNVERIFIED);
//
//
//        sl = new SecurityLevel467(SensitivityLevel467.CONFIDENTIAL, c3);
//        lctInteg.newSubject("s3", sl, IntLevel467.FALSE);
//
//
//        sl = new SecurityLevel467(SensitivityLevel467.CONFIDENTIAL, c4);
//        lctInteg.newObject("o1", sl, IntLevel467.VERIFIED);
//
//        System.out.println("1 " + lctInteg.dominates("subject0", "s2"));
//
//        System.out.println("2.0 " + lctInteg.dominates("s1", "s2"));
//        System.out.println("2.1 " + lctInteg.dominates("s2", "s1"));
//
//        System.out.println("3.1 " + lctInteg.dominates("s2", "s1"));
//
//        System.out.println("4.0 " + lctInteg.dominates("s3", "o1"));
//        System.out.println("4.2 " + lctInteg.dominates("s3", "o1"));
//
//        System.out.println("7.0 " + lctInteg.access("subject0", "o1", "r"));
//        System.out.println("7.1 " + lctInteg.access("subject0", "o1", "w"));
//        System.out.println("7.3 " + lctInteg.access("subject0", "o1", "e"));
//
//        System.out.println("8.0 " + lctInteg.access("s1", "o1", "r"));
//        System.out.println("8.1 " + lctInteg.access("s1", "o1", "w"));
//        System.out.println("8.3 " + lctInteg.access("s1", "o1", "e"));
//
//        System.out.println("9.0 " + lctInteg.access("s2", "o1", "r"));
//        System.out.println("9.1 " + lctInteg.access("s2", "o1", "w"));
//        System.out.println("9.3 " + lctInteg.access("s2", "o1", "e"));
//
//        System.out.println("10.0 " + lctInteg.access("s3", "o1", "r"));
//        System.out.println("10.1 " + lctInteg.access("s3", "o1", "w"));
//        System.out.println("10.3 " + lctInteg.access("s3", "o1", "e"));
    }
}
