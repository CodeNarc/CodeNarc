package org.codenarc.sample.service

import org.codenarc.sample.domain.SampleDomain
import org.codenarc.sample.processor.SampleProcessor

/**
 * Sample service class that exhibits several CodeNarc violations
 * @author John Doe
 */
class SampleService {
    String name
    Map mappings
    SampleProcessor processor

    void doService() {
        def shouldRun = new Boolean(true)
        try {
            processor.run()
        } catch(Throwable t) {
            // TODO
        }
    }

    void openFile() {
        try {
            new File('somefile.txt')
        }
        finally {
            if (!name) {
                throw new Exception('bad stuff')
            }
        }
    }
}