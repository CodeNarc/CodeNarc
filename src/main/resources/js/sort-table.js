    function sortByPriority(tr1, tr2) {
        var item1 = tr1.getElementsByClassName('priorityColumn').item(0)
        var item2 = tr2.getElementsByClassName('priorityColumn').item(0)
        return (item1.innerHTML > item2.innerHTML);
    }

    function sortByRule(tr1, tr2) {
        var item1 = tr1.getElementsByClassName('ruleColumn').item(0)
        var item2 = tr2.getElementsByClassName('ruleColumn').item(0)
        return (item1.innerHTML > item2.innerHTML);
    }

    function sortByFile(tr1, tr2) {
        var item1 = tr1.getElementsByClassName('pathColumn').item(0)
        var item2 = tr2.getElementsByClassName('pathColumn').item(0)
        return (item1.innerHTML > item2.innerHTML);
    }

    // Adapted from http://codereview.stackexchange.com/questions/37632/how-should-i-sort-an-html-table-with-javascript-in-a-more-efficient-manner
    function sortData(comparisonFunction) {
        var tableData = document.getElementById('violationsTable').getElementsByTagName('tbody').item(0);
        var rowData = tableData.getElementsByTagName('tr');

        for(var i = 0; i < rowData.length - 1; i++) {
            for(var j = 0; j < rowData.length - (i + 1); j++) {
                if (comparisonFunction(rowData.item(j), rowData.item(j+1))) {
                    tableData.insertBefore(rowData.item(j+1), rowData.item(j));
                }
            }
        }
    }