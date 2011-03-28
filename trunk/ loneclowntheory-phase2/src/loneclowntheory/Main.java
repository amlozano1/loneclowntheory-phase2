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
        // *********************************************************************
        // BLP TEST CASES
        // *********************************************************************
        LCTBellLaPadula467 lct;

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
            Category467.CA
        };

        Category467[] c6 =
        {
            Category467.IAH
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
        // test classifyOL
        System.out.println("7.0 " + lct.classifyOL("o1", new SecurityLevel467(SensitivityLevel467.CONFIDENTIAL, c2))); // NO - new level must dominate old
        System.out.println("7.1 " + lct.classifyOL("o1", new SecurityLevel467(SensitivityLevel467.SECRET, c3))); // NO - new level must dominate old
        System.out.println("7.2 " + lct.classifyOL("o1", new SecurityLevel467(SensitivityLevel467.TOP_SECRET, c2))); // OK
        System.out.println("7.3 " + lct.classifyOL("o1", new SecurityLevel467(SensitivityLevel467.TOP_SECRET, c1))); // OK
        // test declassifyOL
        System.out.println("8.0 " + lct.declassifyOL("s1", "o1", new SecurityLevel467(SensitivityLevel467.SECRET, c2))); // NO - s1 does not dominate o1
        System.out.println("8.1 " + lct.declassifyOL("subject0", "o1", new SecurityLevel467(SensitivityLevel467.SECRET, c2))); // OK - subject0 dominates o1
        System.out.println("8.2 " + lct.declassifyOL("s1", "o1", new SecurityLevel467(SensitivityLevel467.CONFIDENTIAL, c2))); // OK - s1 dominates o1
        System.out.println("8.3 " + lct.declassifyOL("s1", "o1", new SecurityLevel467(SensitivityLevel467.CONFIDENTIAL, c3))); // OK - s1 dominates o1
        // test categories that are not relatable via dominance
        System.out.println("9.0 " + lct.updateSL("s1", new SecurityLevel467(SensitivityLevel467.SECRET, c4))); // NO - {AZ} and {PHX, SFO}
        System.out.println("9.1 " + lct.updateSL("s1", new SecurityLevel467(SensitivityLevel467.SECRET, c5))); // NO - {AZ} and {CA}
        System.out.println("9.2 " + lct.classifyOL("o1", new SecurityLevel467(SensitivityLevel467.CONFIDENTIAL, c6))); // NO - {PHX} and {IAH}
        System.out.println("9.3 " + lct.classifyOL("o1", new SecurityLevel467(SensitivityLevel467.SECRET, c2))); // OK
        System.out.println("9.4 " + lct.classifyOL("o1", new SecurityLevel467(SensitivityLevel467.SECRET, c5))); // NO - {AZ} and {CA}
        System.out.println("9.5 " + lct.declassifyOL("s1", "o1", new SecurityLevel467(SensitivityLevel467.SECRET, c5))); // NO - {AZ} and {CA}
        System.out.println("9.6 " + lct.declassifyOL("s1", "o1", new SecurityLevel467(SensitivityLevel467.SECRET, c4))); // NO - {AZ} and {PHX, SFO}
        System.out.println("9.7 " + lct.declassifyOL("s1", "o1", new SecurityLevel467(SensitivityLevel467.SECRET, c3))); // OK - {AZ} and {PHX}
        System.out.println("9.8 " + lct.declassifyOL("s1", "o1", new SecurityLevel467(SensitivityLevel467.SECRET, c6))); // NO - {PHX} and {IAH}

        // *********************************************************************
        // BIBA TEST CASES
        // *********************************************************************
        LCTIntegrityManager467 lctInteg;

        System.out.println("*** TESTING BIBA ***");

        lctInteg = new LCTIntegrityManager467();

        Category467[] c7 =
        {
            Category467.TX
        };

        lctInteg.newSubject("s1", new SecurityLevel467(SensitivityLevel467.SECRET, c7), IntLevel467.VERIFIED);
        lctInteg.newSubject("s2", new SecurityLevel467(SensitivityLevel467.SECRET, c7), IntLevel467.SOME_EVIDENCE);
        lctInteg.newSubject("s3", new SecurityLevel467(SensitivityLevel467.SECRET, c7), IntLevel467.UNVERIFIED);
        lctInteg.newSubject("s4", new SecurityLevel467(SensitivityLevel467.SECRET, c7), IntLevel467.FALSE);

        lctInteg.newObject("o1", new SecurityLevel467(SensitivityLevel467.SECRET, c7), IntLevel467.VERIFIED);
        lctInteg.newObject("o2", new SecurityLevel467(SensitivityLevel467.SECRET, c7), IntLevel467.SOME_EVIDENCE);
        lctInteg.newObject("o3", new SecurityLevel467(SensitivityLevel467.SECRET, c7), IntLevel467.UNVERIFIED);
        lctInteg.newObject("o4", new SecurityLevel467(SensitivityLevel467.SECRET, c7), IntLevel467.FALSE);
        // i(s1) = i(o1)
        System.out.println("1.0 " + lctInteg.access("s1", "o1", "r")); // OK
        System.out.println("1.1 " + lctInteg.access("s1", "o1", "w")); // OK
        System.out.println("1.2 " + lctInteg.access("s1", "o1", "a")); // OK
        System.out.println("1.3 " + lctInteg.access("s1", "o1", "e")); // OK
        // i(s1) > i(o2)
        System.out.println("2.0 " + lctInteg.access("s1", "o2", "r")); // NO
        System.out.println("2.1 " + lctInteg.access("s1", "o2", "w")); // NO
        System.out.println("2.2 " + lctInteg.access("s1", "o2", "a")); // OK
        System.out.println("2.3 " + lctInteg.access("s1", "o2", "e")); // OK
        // i(s1) > i(o3)
        System.out.println("3.0 " + lctInteg.access("s1", "o3", "r")); // NO
        System.out.println("3.1 " + lctInteg.access("s1", "o3", "w")); // NO
        System.out.println("3.2 " + lctInteg.access("s1", "o3", "a")); // OK
        System.out.println("3.3 " + lctInteg.access("s1", "o3", "e")); // OK
        // i(s1) > i(o4)
        System.out.println("4.0 " + lctInteg.access("s1", "o4", "r")); // NO
        System.out.println("4.1 " + lctInteg.access("s1", "o4", "w")); // NO
        System.out.println("4.2 " + lctInteg.access("s1", "o4", "a")); // OK
        System.out.println("4.3 " + lctInteg.access("s1", "o4", "e")); // OK
        // i(s2) < i(o1)
        System.out.println("5.0 " + lctInteg.access("s2", "o1", "r")); // OK
        System.out.println("5.1 " + lctInteg.access("s2", "o1", "w")); // NO
        System.out.println("5.2 " + lctInteg.access("s2", "o1", "a")); // NO
        System.out.println("5.3 " + lctInteg.access("s2", "o1", "e")); // NO
        // i(s2) = i(o2)
        System.out.println("6.0 " + lctInteg.access("s2", "o2", "r")); // OK
        System.out.println("6.1 " + lctInteg.access("s2", "o2", "w")); // OK
        System.out.println("6.2 " + lctInteg.access("s2", "o2", "a")); // OK
        System.out.println("6.3 " + lctInteg.access("s2", "o2", "e")); // OK
        // i(s2) > i(o3)
        System.out.println("7.0 " + lctInteg.access("s2", "o3", "r")); // NO
        System.out.println("7.1 " + lctInteg.access("s2", "o3", "w")); // NO
        System.out.println("7.2 " + lctInteg.access("s2", "o3", "a")); // OK
        System.out.println("7.3 " + lctInteg.access("s2", "o3", "e")); // OK
        // i(s2) > i(o4)
        System.out.println("8.0 " + lctInteg.access("s2", "o4", "r")); // NO
        System.out.println("8.1 " + lctInteg.access("s2", "o4", "w")); // NO
        System.out.println("8.2 " + lctInteg.access("s2", "o4", "a")); // OK
        System.out.println("8.3 " + lctInteg.access("s2", "o4", "e")); // OK
        // i(s3) < i(o1)
        System.out.println("9.0 " + lctInteg.access("s3", "o1", "r")); // OK
        System.out.println("9.1 " + lctInteg.access("s3", "o1", "w")); // NO
        System.out.println("9.2 " + lctInteg.access("s3", "o1", "a")); // NO
        System.out.println("9.3 " + lctInteg.access("s3", "o1", "e")); // NO
        // i(s3) < i(o2)
        System.out.println("10.0 " + lctInteg.access("s3", "o2", "r")); // OK
        System.out.println("10.1 " + lctInteg.access("s3", "o2", "w")); // NO
        System.out.println("10.2 " + lctInteg.access("s3", "o2", "a")); // NO
        System.out.println("10.3 " + lctInteg.access("s3", "o2", "e")); // NO
        // i(s3) = i(o3)
        System.out.println("11.0 " + lctInteg.access("s3", "o3", "r")); // OK
        System.out.println("11.1 " + lctInteg.access("s3", "o3", "w")); // OK
        System.out.println("11.2 " + lctInteg.access("s3", "o3", "a")); // OK
        System.out.println("11.3 " + lctInteg.access("s3", "o3", "e")); // OK
        // i(s3) > i(o4)
        System.out.println("12.0 " + lctInteg.access("s3", "o4", "r")); // NO
        System.out.println("12.1 " + lctInteg.access("s3", "o4", "w")); // NO
        System.out.println("12.2 " + lctInteg.access("s3", "o4", "a")); // OK
        System.out.println("12.3 " + lctInteg.access("s3", "o4", "e")); // OK
        // i(s4) < i(o1)
        System.out.println("13.0 " + lctInteg.access("s4", "o1", "r")); // OK
        System.out.println("13.1 " + lctInteg.access("s4", "o1", "w")); // NO
        System.out.println("13.2 " + lctInteg.access("s4", "o1", "a")); // NO
        System.out.println("13.3 " + lctInteg.access("s4", "o1", "e")); // NO
        // i(s4) < i(o2)
        System.out.println("14.0 " + lctInteg.access("s4", "o2", "r")); // OK
        System.out.println("14.1 " + lctInteg.access("s4", "o2", "w")); // NO
        System.out.println("14.2 " + lctInteg.access("s4", "o2", "a")); // NO
        System.out.println("14.3 " + lctInteg.access("s4", "o2", "e")); // NO
        // i(s4) < i(o3)
        System.out.println("15.0 " + lctInteg.access("s4", "o3", "r")); // OK
        System.out.println("15.1 " + lctInteg.access("s4", "o3", "w")); // NO
        System.out.println("15.2 " + lctInteg.access("s4", "o3", "a")); // NO
        System.out.println("15.3 " + lctInteg.access("s4", "o3", "e")); // NO
        // i(s4) = i(o4)
        System.out.println("16.0 " + lctInteg.access("s4", "o4", "r")); // OK
        System.out.println("16.1 " + lctInteg.access("s4", "o4", "w")); // OK
        System.out.println("16.2 " + lctInteg.access("s4", "o4", "a")); // OK
        System.out.println("16.3 " + lctInteg.access("s4", "o4", "e")); // OK
    }
}
