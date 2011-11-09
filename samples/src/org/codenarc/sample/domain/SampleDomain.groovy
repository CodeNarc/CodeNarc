package org.codenarc.sample.domain

import org.codenarc.sample.domain.OtherDomain
import org.codenarc.sample.other.Other
import org.codenarc.sample.other.Other

/**
 * Sample domain class that exhibits several CodeNarc violations
 */
class SampleDomain {
    String name
    Map mappings
    OtherDomain otherDomain

    void initialize() {
        def title = new String('Sample Domain')
    }

    void validate() {
        if (name) {
            // TODO
        }
        else {
            // should do something
        }
    }
}