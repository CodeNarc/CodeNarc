import org.codenarc.analyzer.DirectorySourceAnalyzer
import org.codenarc.report.HtmlReportWriter
import org.codenarc.ruleset.CompositeRuleSet
import org.codenarc.ruleset.XmlFileRuleSet
import org.codenarc.AnalysisContext

println 'Run CodeNarc'

final BASE = '../samples/src'
final SOURCE_DIRS = null

def sourceAnalyzer = new DirectorySourceAnalyzer(baseDirectory:BASE, sourceDirectories:SOURCE_DIRS)

def ruleSetFiles = ['rulesets/basic.xml','rulesets/exceptions.xml','rulesets/imports.xml']
def ruleSet = new CompositeRuleSet()
ruleSetFiles.each { ruleSetFile -> ruleSet.add(new XmlFileRuleSet(ruleSetFile)) }

def results = sourceAnalyzer.analyze(ruleSet)
def analysisContext = new AnalysisContext(ruleSet:ruleSet, sourceDirectories:SOURCE_DIRS)

def reportWriter = new HtmlReportWriter(outputFile:'reports/CodeNarcReport.html', title:'Sample Code')
reportWriter.writeOutReport(analysisContext, results)

println "Done."
