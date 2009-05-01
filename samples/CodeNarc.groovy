/**
 * Groovy script to simplify running CodeNarc from the command-line
 *
 * @author Chris Mair
 * @version $Revision: 106 $ - $Date: 2009-03-28 22:36:10 -0400 (Sat, 28 Mar 2009) $
 */
class CodeNarc {

    public static void main(String[] args) {
        println "Running CodeNarc with $args"
        try {
            org.codenarc.CodeNarc.main(args)
        }
        catch(Throwable t) {
            t.printStackTrace()
        }
    }

}