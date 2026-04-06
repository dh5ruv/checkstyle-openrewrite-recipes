/*xml
<module name="Checker">
  <module name="TreeWalker">
    <module name="com.puppycrawl.tools.checkstyle.checks.ArrayTypeStyleCheck"/>
  </module>
</module>
*/
package org.checkstyle.autofix.recipe.arraytypestyle.javastyle;

public class InputJavaStyle {

    int[] nums;

    String strings[];                    // violation 'Array brackets at illegal position.'

    char[] toCharArray() {
        return null;
    }

    void method(String args[]) {         // violation 'Array brackets at illegal position.'
    }

}