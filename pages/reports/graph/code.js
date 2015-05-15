/**
 * Created by chedim on 3/21/15.
 */
me.server = 'server';
me.storage = 'storage';
me.date = 'datejs';

var chart, year = new Date().getFullYear(), month = new Date().getMonth();
var chartOptions = {
    angleShowLineOut: false,
    multiTooltipTemplate: "<%if (name){%><%=name%>: <%}%><%= value %>"
};

$(c).on('resume', function () {
    chart = $('.chart-holder').process();
    console.log('CHART:', chart);
    renderChart(year, month + 1);
    $('#year').on('change', onYearChanged);
    $('#month').on('change', onMonthChanged);
});

var renderChart = function (year, month) {
    loadChart(year, month, function (data) {
        console.log('!!! >>>>> CHARTDATA:', data);
        chart.setData(data);
    })
}

var loadChart = function (year, month, cb) {
    $('.chart-holder').addClass('loading');
    me.server.raw("/wp-admin/admin-ajax.php?action=susu_chart_report&year=" + year + "&month=" + month, function (err, data) {
            if (err) return alert(err);
            var result = {};

            var monLen = me.date.getDaysInMonth(year, month - 1);

            while (monLen) result[monLen--] = {
                E: 0,
                D: 0,
                C: 0,
                B: 0,
                A: 0
            };

            monLen = me.date.getDaysInMonth(year, month - 1);

            for (var i = 0; i < data.length; i++) {
                var piece = data[i];
                var len = Math.round(parseInt(piece.value) / 360) / 10;
                result[piece.date][piece.type] = len;
//                result[piece.date].sum += len;
            }

            cb(result);
            $('.chart-holder').removeClass('loading');
        }
    )

}

var createDataset = function (type, days) {
    var colors = me.storage.colors[type];

    var dataset = {
        label: type,
        fillColor: 'rgba(' + colors.join(',') + ', 0.75)',
        strokeColor: 'rgba(' + colors.join(',') + ', 0)',
        highlightFill: 'rgba(' + colors.join(',') + ', 0.1)',
        highlightStroke: 'rgba(' + colors.join(',') + ', 0.1)',
        pointColor: 'rgba(' + colors.join(',') + ', 0.75)',
        pointHighlightFill: 'rgba(' + colors.join(',') + ', 1)',
        data: []
    };

    while (days--) dataset.data[days] = 0;

    return dataset;
}

var onMonthChanged = function(e, m) {
    month = m;
    renderChart(year, month+1);
}

var onYearChanged = function(e, y) {
    year = y;
    renderChart(year, month+1);
}