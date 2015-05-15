var me = this;

me.date = 'datejs';
me.openable = 'openable';
var selectedTime, format = 'hh:mm', rendered = false;

var views = {
    holder: $('.holder'),
    value: $('.time-value'),
    period: $('.time-period'),
    clock: $('.clock'),
    hourArrow: $('.arrow.hour'),
    hourValue: $('.arrow.hour .value'),
    minArrow: $('.arrow.min'),
    minValue: $('.arrow.min .value')
}

$(c).on('start', function (args) {
    var time = new Date();
    if (args.time) {
        time.parse(args.time);
    }
    if (args.format) {
        format = args.format;
    }
    selectedTime = time;
});

$(c).on('resume', function () {
    $(window).on('mousemove', onMouseMove);
    $(window).on('mouseup', onMouseUp);
    $('.value', views.clock).on('mousedown', onMouseDown);
    $(views.clock).on('click', '.hour.mark', onHourClick);
    $(views.clock).on('click', '.minute.mark', onMinClick);
    $(views.clock).on('mousewheel', onScroll);
    $(views.period).click(onTimePeriodClick);
    $(views.value).on('focusin', onFocusIn);
    $(c).on('keydown', onKeyDown);
    me.setTime(selectedTime);
});

$(me).on('pause', function () {
    $(window).off('mousemove', onMouseMove);
    $(window).off('mouseup', onMouseUp);
    $('.value', views.clock).off('mousedown', onMouseDown);
    $('.hour', views.clock).off('click', onHourClick);
    $('.minute', views.clock).off('click', onMinClick);
});

var openingTime;
$(views.holder).on('open', function () {
    openingTime = selectedTime.clone();
    renderClock();
    renderArrows(selectedTime);
})

var setTime = function (time) {
    selectedTime = time.clone();
    views.value.text(selectedTime.toString(format));
    views.period.text(selectedTime.toString('tt'));
    $(c).trigger('change', time);
    $(c).data('value', time);
}

var renderClock = function () {
    if (rendered) return;
    rendered = true;
    var holderRadius = views.holder.outerWidth() / 1.8;
    var radius = holderRadius * 2.2, diameter = radius * 2, quardius = radius / 2;
    views.clock.height(diameter);
    views.clock.width(diameter);
    views.clock.css({
//        'border-radius': (d + 24) + 'px',
//        'margin-left': -radius + 'px',
//        'margin-top': -radius + 'px'
    });

    // rendering hours
    var angleStep = Math.PI / 6;
    var deg90 = Math.PI / 2;
//    console.log('angleStep', angleStep);
    for (var i = 0; i < 12; i++) {
        var hour = $('<div class="hour">' + (i + 1) + '</div>');
        hour.addClass('h' + (i + 1) + ' mark');
        var angle = angleStep * (i + 1) - deg90;
        var left = radius + Math.cos(angle) * quardius * 1.2 + 1;
        var top = radius + Math.sin(angle) * quardius * 1.2 + 1;
        hour.css({
            top: top + 'px',
            left: left + 'px'
        });
        $(views.clock).append(hour);
    }

    //rendering mins
    angleStep = Math.PI / 30;
    for (i = 0; i < 60; i += 5) {
        var minute = $('<div class="minute">' + (i) + '</div>');
        minute.addClass('min' + i + ' mark');
        var angle = angleStep * i - deg90;
        var left = radius + Math.cos(angle) * (radius * 0.8) + 1;
        var top = radius + Math.sin(angle) * (radius * 0.8) + 1;
        minute.css({
            top: top + 'px',
            left: left + 'px'
        });
        $(views.clock).append(minute);
    }
}
var deg90 = Math.PI / 2;

var renderArrows = function (time) {
    var h = time.getHours() / 12;
    var m = time.getMinutes() / 60;

    var hAngle = Math.PI * 2 * h - deg90;
    var mAngle = Math.PI * 2 * m - deg90;

    setArrowRotation(views.hourArrow, hAngle);
    setArrowRotation(views.minArrow, mAngle);

    $(views.hourValue).text(time.toString('h'));
    $(views.minValue).text(time.getMinutes());
}

var setArrowRotation = function (arrow, rad) {
    $(arrow).css('transform', 'rotate(' + rad + 'rad)');
    $('.value', arrow).css('transform', 'rotate(' + (-rad) + 'rad)');
}

var dragging = null, centerX, centerY, dragRad;
var onMouseMove = function (event) {
    if (dragging) {
        var x = event.pageX - centerX, y = -(event.pageY - centerY);
        dragRad = Math.atan2(x, y);
        setArrowRotation(dragging, dragRad - deg90);
        if ($(dragging).hasClass('hour')) {
            $('.value', dragging).text(convertRads(dragRad, 12));
        } else {
            $('.value', dragging).text(convertRads(dragRad, 60));
        }
        event.preventDefault();
        event.stopPropagation();
        return false;
    }
}

var onMouseDown = function (event) {
    if (!$(event.target).is('.value')) {
        dragging = null;
    } else {
        dragging = $(event.target).parents('.arrow');
        var offset = $(views.clock).offset();
        var w = $(views.clock).outerWidth(), h = $(views.clock).outerHeight();
        centerX = offset.left + Math.round(w / 2);
        centerY = offset.top + Math.round(h / 2);
        event.preventDefault();
        event.stopPropagation();
        console.log('now dragging', dragging);
        return false;
    }
}

var convertRads = function (rads, base) {
    var step = Math.PI * 2 / base;
    if (rads < 0) rads += Math.PI * 2;
    var h = Math.round(rads / step);
    return h;
}

var onMouseUp = function () {
    if (dragging != null) {
        if (dragRad != null) {
            var resTime = selectedTime.clone();
            if ($(dragging).is('.hour')) {
                var h = convertRads(dragRad, 12);
                $('.value', dragging).text(h);
                resTime.setHours(h);
            } else {
                var m = convertRads(dragRad, 60);
                $('.value', dragging).text(m);
                resTime.setMinutes(m);
            }
            setTime(resTime);
            renderArrows(resTime);
        }
        dragging = null;
        dragRad = null;
    }
}

var onHourClick = function (e) {
    var res = selectedTime.clone();
    console.log('HClick', e.target);
    var pm = 0;
    if (selectedTime.toString('tt') == 'PM') pm = 12;
    res.setHours(parseInt($(e.target).text()) + pm);
    setTime(res);
    renderArrows(res);
}

var onMinClick = function (e) {
    var res = selectedTime.clone();
    console.log('MClick', e.target);
    res.setMinutes($(e.target).text());
    setTime(res);
    renderArrows(res);
}

var onScroll = function (e) {
    var delta = Math.max(-1, Math.min(1, (e.originalEvent.wheelDeltaY || -e.originalEvent.detail)));
    selectedTime.addMinutes(delta);
    setTime(selectedTime);
    renderArrows(selectedTime);
    e.preventDefault();
    e.stopPropagation();
    return false;
}

var onTimePeriodClick = function () {
    var period = 12;
    if (selectedTime.toString('tt') == 'PM') {
        period = -12
    }
    selectedTime.addHours(period);
    setTime(selectedTime);
    $(c).triggerHandler('period', period);
}

me.setTime = function (time) {
    selectedTime = time.clone();
    views.value.text(selectedTime.toString(format));
    views.period.text(selectedTime.toString('tt'));
    renderArrows(time);
}

me.open = function () {
    openingTime = selectedTime.clone();
    $(views.holder).addClass('open');
    renderClock();
    renderArrows(selectedTime);
}

me.close = function () {
    $(views.holder).removeClass('open');
}

me.isOpen = function () {
    return views.holder.hasClass('open');
}


var onFocusIn = function () {
    me.open();
}

var onKeyDown = function (e) {
    var k = e.keyCode;
    console.log(me.isOpen(), k);
    var redraw = false;
    if (me.isOpen()) {
        redraw = true;
        if (k == 37) {
            selectedTime.addMinutes(-1);
        } else if (k == 39) {
            selectedTime.addMinutes(1);
        } else if (k == 38) {
            selectedTime.addHours(-1);
        } else if (k == 40) {
            selectedTime.addHours(1);
        } else if (k == 27) {
            selectedTime = openingTime;
            me.close();
        } else {
            if (k == 9 || k == 13) me.close();
            return;
        }
    } else if (k == 13) {
        me.open();
        return;
    }
    if (redraw) {
        setTime(selectedTime);
        renderArrows(selectedTime);
        e.preventDefault();
        e.stopPropagation();
        return false;
    }
}