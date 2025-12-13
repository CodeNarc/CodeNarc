echo Copy CodeNarc sample reports into "samples" directory...

PROJECT_DIR="/home/chris/Projects/CodeNarc/GitHub"
echo PROJECT_DIR=$PROJECT_DIR

HTML_FROM="$PROJECT_DIR/target/AntBuilderTestHtmlReport.html"
HTML_TO="$PROJECT_DIR/docs/samples/SampleCodeNarcHtmlReport.html"
#rm $HTML_TO
cp $HTML_FROM   $HTML_TO


SORTABLE_FROM="$PROJECT_DIR/target/AntBuilderTestSortableHtmlReport.html"
SORTABLE_TO="$PROJECT_DIR/docs/samples/SampleCodeNarcSortableHtmlReport.html"
#rm $SORTABLE_TO
cp $SORTABLE_FROM   $SORTABLE_TO


XML_FROM="$PROJECT_DIR/target/AntBuilderTestXmlReport.xml"
XML_TO="$PROJECT_DIR/docs/samples/SampleCodeNarcXmlReport.xml"
#rm $XML_TO
cp $XML_FROM   $XML_TO

JSON_FROM="$PROJECT_DIR/target/AntBuilderTestJsonReport.json"
JSON_TO="$PROJECT_DIR/docs/samples/SampleCodeNarcJsonReport.json"
#rm $JSON_TO
cp $JSON_FROM   $JSON_TO

TEXT_FROM="$PROJECT_DIR/target/AntBuilderTestTextReport.txt"
TEXT_TO="$PROJECT_DIR/docs/samples/SampleCodeNarcTextReport.txt"
#rm $TEXT_TO
cp $TEXT_FROM   $TEXT_TO

GITLAB_FROM="$PROJECT_DIR/target/AntBuilderTestGitlabCodeQualityReport.json"
GITLAB_TO="$PROJECT_DIR/docs/samples/SampleCodeNarcGitlabCodeQualityReport.json"
#rm $GITLAB_TO
cp $GITLAB_FROM   $GITLAB_TO

SARIF_FROM="$PROJECT_DIR/target/AntBuilderTestSarifReport.json"
SARIF_TO="$PROJECT_DIR/docs/samples/SampleCodeNarcSarifReport.sarif.json"
#rm $SARIF_TO
cp $SARIF_FROM   $SARIF_TO

