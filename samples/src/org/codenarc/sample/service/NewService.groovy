package org.codenarc.sample.service

/**
 * Sample service class that exhibits several CodeNarc violations
 */
class NewService {
    static final DEFAULT_ID = 999
    String name
    def values = []

    void doNewService() {
        for(int i=0; i < values.size(); i++) {

        }
    }

    void waitForTermination() {
        while (!values.empty) {
            
        }
    }

}