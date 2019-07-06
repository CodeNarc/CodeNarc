    function sortByPriority(tr1, tr2) {
        var item1 = columnValue(tr1, 'priorityColumn');
        var item2 = columnValue(tr2, 'priorityColumn');
        return (item1 > item2);
    }

    function sortByRule(tr1, tr2) {
        var item1 = columnValue(tr1, 'ruleColumn');
        var item2 = columnValue(tr2, 'ruleColumn');
        initializeCountPerRule();
        return (countPerRule[item1] < countPerRule[item2]);
    }

    function sortByRuleName(tr1, tr2) {
        var item1 = columnValue(tr1, 'ruleColumn');
        var item2 = columnValue(tr2, 'ruleColumn');
        return (item1 > item2);
    }

    function sortByFile(tr1, tr2) {
        var item1 = columnValue(tr1, 'pathColumn');
        var item2 = columnValue(tr2, 'pathColumn');
        initializeCountPerFile();

        var priority1 = columnValue(tr1, 'priorityColumn');
        var priority2 = columnValue(tr2, 'priorityColumn');
        var inversePriority1 = 100 - parseInt(priority1);
        var inversePriority2 = 100 - parseInt(priority2);

        var sort1 = "" + countPerFile[item1] + " " + item1 + " " + inversePriority1;
        var sort2 = "" + countPerFile[item2] + " " + item2 + " " + inversePriority2;

        return (sort1 < sort2);
    }

    function columnValue(tr, name) {
        return tr.getElementsByClassName(name).item(0).innerHTML;
    }

    var countPerRule = null;
    var countPerFile = null;

    function initializeCountPerRule() {
        if (countPerRule == null) {
            var rows = getTableRows();
            countPerRule = { };
            for(var i = 0; i < rows.length; i++) {
                var key = columnValue(rows.item(i), 'ruleColumn');
                if (key in countPerRule) {
                    countPerRule[key] = countPerRule[key] + 1;
                }
                else {
                    countPerRule[key] = 1;
                }
            }
        }
    }

    function initializeCountPerFile() {
        if (countPerFile == null) {
            var rows = getTableRows();
            countPerFile = { };
            for(var i = 0; i < rows.length; i++) {
                var key = columnValue(rows.item(i), 'pathColumn');
                if (key in countPerFile) {
                    countPerFile[key] = countPerFile[key] + 1;
                }
                else {
                    countPerFile[key] = 1;
                }
            }
        }
    }

    function getTableBody() {
        return document.getElementById('violationsTable').getElementsByTagName('tbody').item(0);
    }

    function getTableRows() {
        return getTableBody().getElementsByTagName('tr');
    }

    // Adapted from http://codereview.stackexchange.com/questions/37632/how-should-i-sort-an-html-table-with-javascript-in-a-more-efficient-manner
    function sortData(comparisonFunction) {
        var tableData = getTableBody();
        var rowData = getTableRows();

        for(var i = 0; i < rowData.length - 1; i++) {
            for(var j = 0; j < rowData.length - (i + 1); j++) {
                if (comparisonFunction(rowData.item(j), rowData.item(j+1))) {
                    tableData.insertBefore(rowData.item(j+1), rowData.item(j));
                }
            }
        }
    }