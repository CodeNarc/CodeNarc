package org.codenarc.rule;

import org.codehaus.groovy.control.Phases;

/**
 * Abstract superclass for Rules that use a Groovy AST Visitor and can optionally run in enhanced mode.
 *
 * @author Marcin Erdmann
 */
public abstract class AbstractEnhanceableAstVisitorRule extends AbstractAstVisitorRule {

    /**
     * Holds the name of the system property which allows to control the {@code enhancedMode} property.
     */
    public final static String ENHANCED_MODE_SYSTEM_PROPERTY = "org.codenarc.enhancedMode";

    /**
     * Controls weather to run in enhanced mode.
     *
     * Note that running in enhanced mode will use a later compilation phase which will require any classes referenced
     * from the analysed code to be available on the classpath.
     *
     * This property is set to {@code false} by default and can be also controlled using
     * {@code org.codenarc.enhancedMode} system property.
     */
    private boolean enhancedMode = Boolean.getBoolean(ENHANCED_MODE_SYSTEM_PROPERTY);

    public boolean isEnhancedMode() {
        return enhancedMode;
    }

    public void setEnhancedMode(boolean enhancedMode) {
        this.enhancedMode = enhancedMode;
    }

    @Override
    public int getCompilerPhase() {
        return enhancedMode ? Phases.SEMANTIC_ANALYSIS : super.getCompilerPhase();
    }

}
