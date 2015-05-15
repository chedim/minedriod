var picker = {
    start: $('.start'),
    end: $('.end'),
    interval: $('.interval')
}

var time = {};
me.datejs = 'datejs';

$(c).on('start', function (args) {
    time.start = (args.start) ? new Date(args.start) : new Date();
    if (args.end) time.end = new Date(args.end);
    else time.end = time.start.clone().addMilliseconds(3600000);
    console.log('PICKERS', picker);

    time.interval = time.end - time.start;
});

$(c).on('resume', function () {
    picker.end.on('change', function (e, end) {
        console.log('END PICKER CHANGE');
        time.interval = end - time.start;
        time.end = end;
        updateInterval();
    });
    picker.start.on('change', function (event, start) {
        console.log('START PICKER CHANGE');
        var diff = start - time.start;
        time.start = start;
        if (time.end != null) {
            time.end.addMilliseconds(diff);
            setTimeEnd(time.end);
        }
    });
    picker.interval.on('keyup', function (event) {
        var i = parseInt(picker.interval.val());
        if (event.keyCode == 38) {
            i -= 5;
            picker.interval.val(i);
        } else if (event.keyCode == 40) {
            i += 5;
            picker.interval.val(i);
        }
        if (i !== NaN) {
            setTimeEnd(time.start.clone().addMinutes(i));
        }
    });

    picker.start.process().setTime(time.start);
    if (time.end != null) {
        picker.end.process().setTime(time.end);
        updateInterval();
    } else {
        $(c).addClass('no-duration');
    }

    $('.add-duration a').click(function () {
        setTimeEnd(new Date());
        if (time.interval < 30000) {
            me.setInterval(3600000);
        }
        $(picker.interval).focus();
    });
    $('.del-duration a').click(function() {
        me.setInterval(null);
    })
    picker.end.on('period', onEndPeriod);
});

me.setInterval = function (interval) {
    if (interval == null) {
        time.end = null;
        time.interval = null;
        $(c).addClass('no-interval');
    } else {
        $(c).removeClass('no-interval');
        time.interval = interval;
        time.end = time.start.clone().addMilliseconds(interval);
        picker.end.process().setTime(time.end);
        updateInterval()
    }
}

var setTimeEnd = function (end) {
    if (end == null) {
        $(c).addClass('no-interval');
        time.end = null;
        time.interval = null;
    } else {
        $(c).removeClass('no-interval');
        time.end = end.clone();
        picker.end.process().setTime(time.end);
        time.interval = end - time.start;
        updateInterval();
    }
}

me.setTimeEnd = function (end) {
    if (end == null) {
        time.end = null;
        time.interval = null;
        $(c).addClass('no-interval');
    } else {
        me.setInterval(end - time.start);
        $(c).removeClass('no-interval');
    }
}

me.setTimeStart = function (start) {
    time.start = start;
    picker.start.process().setTime(time.start);
    if (time.end != null) {
        var diff = start - time.start;
        time.end.addMilliseconds(diff);
        me.setTimeEnd(time.end);
    }
    $(c).data('value', time);
}

var updateInterval = function () {
    if (time.interval != null) {
        picker.interval.val(Math.round(time.interval / 60000));
        if (time.interval < 0) picker.interval.addClass('negative');
        else picker.interval.removeClass('negative');
        $(c).data('value', time);
    }
}

var onEndPeriod = function(e, offset) {
    if (offset < 0) {
        if (time.end < time.start) {
            me.setTimeStart(time.start.addHours(-12));
        }
    }
}