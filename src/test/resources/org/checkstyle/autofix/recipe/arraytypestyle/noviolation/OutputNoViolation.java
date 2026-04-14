/*xml
<module name="Checker">
  <module name="TreeWalker">
    <module name="com.puppycrawl.tools.checkstyle.checks.ArrayTypeStyleCheck"/>
  </module>
</module>
*/
package org.checkstyle.autofix.recipe.arraytypestyle.noviolation;

public class OutputNoViolation {

    int[] nums;

    char[] toCharArray() {
        return null;
    }

    void method(String[] args) {
    }

}
