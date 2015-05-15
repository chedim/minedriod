/**
 * Created by chedim on 3/24/15.
 */


var config = {
    vertical: $(c).is('.vertical')
};

var data = {};

var callbacks = {
    createBar: function () {
        return $('<div class="value"/>');
    }
};

me.setData = function (d) {
    data = d;
    console.log('BAR DATA', d);
    me.refresh();
}

me.refresh = function () {
    var graph = $('.graph');
    graph.html('');

    var abscissa, ordinate;
    if (config.vertical) {
        abscissa = $('.ordinate');
        ordinate = $('.abscissa');
    } else {
        abscissa = $('.abscissa');
        ordinate = $('.ordinate');
    }

    abscissa.html('');
    ordinate.html('');

    var maxVal = undefined, minVal = undefined, abscissLabels;
    for (var x in data) {
        var group = $('<div class="group"/>')
        var bar;
        var sum = 0;
        if (typeof data[x] == 'object') {
            for (var y in data[x]) {
                sum += data[x][y];
                bar = callbacks.createBar();
                $(bar).addClass('bar label-' + y);
                $(group).append(bar);
                $(bar).attr('data-value', data[x][y]);
            }
        }
        $(graph).append(group);
        var label = $('<div class="label"/>');
        $(label).html(x);
        $(group.append(label));
        var space = $('<div class="space"/>');
        $(space).attr('data-sum', sum);
        $(group).prepend(space);
        if (maxVal === undefined || maxVal < sum) maxVal = sum;
        if (minVal === undefined || minVal > sum) minVal = sum;
    }

    var fun = (config.vertical) ? 'width' : 'height';
    $('.bar', graph).each(function (i, e) {
        var value = $(e).attr('data-value');
        var size = (maxVal != 0) ? Math.abs(value / maxVal * 10000) / 100 : 0;
        if ($(e).is('.value')) {
            $(e).css(fun, size + '%');
        } else {
            $('.space', e).css(fun, 100 - size + '%');
            $('.value', e).css(fun, size + '%');
        }
    });

    $('.space', graph).each(function (i, e) {
        var sum = $(e).attr('data-sum');
        var size = (maxVal != 0) ? Math.round(sum / maxVal * 10000) / 100 : 100;
        $(e).css(fun, 100 - size + '%');
    })

    var max = $('<div class="max"/>');
    $(max).text(Math.round(maxVal *100) /100);
    $(ordinate).append(max);

    var min = $('<div class="min"/>');
    $(min).text(minVal);
    $(ordinate).append(min);
    var size = (maxVal != 0) ? Math.abs(minVal / maxVal * 10000) / 100 : 0;
    if (size != 0) {
        $(min).css(fun, size + '%');
    } else {
        $(min).css('bottom', 0);
    }
}

me.setBarCreator = function (f) {
    callbacks.createBar = f;
}

$(c).on('resume', function() {
    $(c).on('mouseover', '.bar', function() {

    })
})