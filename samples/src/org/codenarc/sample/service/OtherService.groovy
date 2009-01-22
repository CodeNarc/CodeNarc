package org.codenarc.sample.service

/**
 * Sample service class that exhibits several CodeNarc violations
 */
class OtherService {
    String name

    void doOtherService() {
        try {
            // should do something here
        }
        finally {
            // should do something here
        }
    }

    void openFile() {
        try {
            new File('otherfile.txt')
        }
        finally {
            if (!name) {
                return
            }
        }
        println 'ok'
    }
}