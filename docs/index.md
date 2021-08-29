---
layout: default
title: CodeNarc
---  

# CodeNarc

## Static Analysis for Groovy

**CodeNarc** analyzes Groovy code for defects, bad practices, inconsistencies, style issues
and more. A flexible framework for rules, rulesets and custom rules means it's easy to configure CodeNarc
to fit into your project. Build tool, framework support, and report generation are all enterprise ready.

<table>
<tr>
    <td width="50%" class="section">
          <h2>CodeNarc Rules</h2>
          CodeNarc triggers violations based on rules. Click the links to the left to view the
          <a href="codenarc-rule-index.html">index of all rules</a>, or individual rule categories (rulesets), such as
          the <a href="codenarc-rules-basic.html">basic</a>, or <a href="codenarc-rules-imports.html">import</a>
          rules. Or you can <a href="codenarc-creating-ruleset.html">create your own ruleset</a>;
          see how easy it is in this <a href="http://www.youtube.com/watch?v=ZPu8FaZZwRw">screencast</a>.
     </td>
    <td valign="middle" align="middle" style="margin:auto; vertical-align:middle">
        <div>
            <a class="getitbutton" href="https://github.com/CodeNarc/CodeNarc/releases">
                Get 2.2.0
            </a>
        </div>
        <div>
            <a href="https://github.com/CodeNarc/CodeNarc/blob/master/CHANGELOG.md">Release Notes</a>
        </div>
    </td>
</tr>
<tr>
     <td class="section">
        <h2>Running CodeNarc</h2>
         Run CodeNarc with the <a href="codenarc-ant-task.html">Ant Task</a>,
         the <a href="codenarc-command-line.html">command-line runner</a>, or
         <a href="codenarc-run-as-a-test.html">as part of your test suite</a>.
            Also,  plugins exist for <a href="codenarc-other-tools-frameworks.html">Maven</a>,
           <a href="codenarc-other-tools-frameworks.html">Gradle</a>, <a href="codenarc-other-tools-frameworks.html">Grails</a>,
           <a href="codenarc-other-tools-frameworks.html">Griffon</a>,
           <a href="codenarc-other-tools-frameworks.html">SonarQube</a>,
           <a href="codenarc-other-tools-frameworks.html">Visual Studio Code</a>,
           and <a href="codenarc-other-tools-frameworks.html">Jenkins</a>. See our
            <a href="codenarc-other-tools-frameworks.html">Integration</a> page for more details.
           Reports come in HTML, XML, or text format.  Take a look at a
           <a href="SampleCodeNarcHtmlReport.html">Sample CodeNarc HTML Report</a>,
           or a <a href="./SampleCodeNarcXmlReport.xml">Sample CodeNarc XML Report</a>.
     </td>
     <td class="section">
         <h2>Requirements</h2>
         <strong>CodeNarc</strong> requires:
         <ul>
              <li>Java 1.7 or later</li>
              <li><a href="http://groovy-lang.org/">Groovy</a> version 2.5 or later</li>
              <li><a href="https://www.slf4j.org/">SLF4J</a> API and binding jar(s)</li>
              <li><a href="https://dx42.github.io/gmetrics/">GMetrics</a> 1.1 or later, if using the Size/Complexity rules</li>
         </ul>
     </td>
</tr>
<tr>
     <td class="section">
          <h2>Get it from Maven2</h2>
           For projects built using <a href="http://maven.apache.org/">Maven</a>, <strong>CodeNarc</strong> is available
           from the
               <a href="https://repo1.maven.org/maven2/org/codenarc/CodeNarc/">Maven Central Repository</a>
                <ul>
                     <li>groupId = org.codenarc</li>
                     <li>artifactId = CodeNarc</li>
                </ul>
     </td>
     <td class="section">
         <h2>Inspirations</h2>
         We're inspired by the wonderful <a href="https://pmd.github.io/">PMD</a>
        and <a href="http://checkstyle.sourceforge.net/">Checkstyle</a> Java static analysis tools, as well
        as the extensive Groovy inspections performed by <a href="http://www.jetbrains.com/idea/">IntelliJ IDEA</a>.
     </td>
</tr>
</table>
