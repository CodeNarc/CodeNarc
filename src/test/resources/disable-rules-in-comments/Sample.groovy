class MyClass {     // codenarc-disable

    def maxValue = 10
    /* codenarc-enable */

    def myMethod(def name) {            // codenarc-disable NoDef
        def other = 123
                                        // codenarc-enable NoDef

        /* codenarc-disable NoDef, Println */
        println other
        /* codenarc-enable NoDef, Println */

        println "name=$name"            /*codenarc-disable-line*/
    }

    private void doStuff() {            // codenarc-disable-line NoDef

        // codenarc-disable R1, R2, R3, R4, NoDef
        def count = 99
    }

}