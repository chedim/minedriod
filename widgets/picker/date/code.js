/**
 * Created by chedim on 12/21/14.
 */

var me = this;
me.date = 'datejs';
me.openable = 'openable';

var weekdays = ['mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun'];
var calendarMonth;

var views = {
    dateValue: $('#date-value'),
    dateHolder: $('.date.holder'),
    datePrev: $('.date.holder .prev'),
    dateNext: $('.date.holder .next'),
    calendar: $('.calendar'),
    calendarDays: $('.calendar .days'),
    monthName: $('.calendar .monthName'),
    calendarControls: $('.calendar .controls')
}, selectedDate = new Date(), dateFormat = 'MM/dd/yy', timeFormat = 'hh:mm tt';

$(c).on('start', function (args) {
    if (args.date) {
        selectedDate = new Date(args.date);
    }
    if (args.date_format) dateFormat = args.date_format;
    if (args.time_format) timeFormat = args.time_format;
});

$(c).on('resume', function () {
    $(c).on('click', '.day, .prev, .next', onDayClick);
    $(c).on('click', '.controls .prevMonth', onPrevMonthClick);
    $(c).on('click', '.controls .nextMonth', onNextMonthClick);
    $(views.calendar).on('mousedown', function(e) {
        e.stopPropagation();
    });
    $(views.calendar).on('mouseup', function(e) {
        e.stopPropagation();
    });
    $(views.dateValue).on('focusout', function(e) {
        e.stopPropagation();
        e.preventDefault();
    })
    $(c).keydown(onKeyDown);
    $(views.dateValue).on('focusin', onFocus);
    $(c).on('focusout', onFocusOut);
    renderDate();
})

var openingDate;
$(views.dateHolder).on('open', function () {
    openingDate = selectedDate.clone();
    if (!calendarMonth) renderCalendar(selectedDate);
})

$(views.dateHolder).on('close', function () {
    console.log('CLOSE');
//    calendarMonth = selectedDate.clone();
    renderCalendar(selectedDate.clone())
})

function renderDate() {
    renderSelectedDate();
}

function renderSelectedDate() {
    views.dateValue.text(selectedDate.toString(dateFormat));
    var datePrev = selectedDate.clone().add(-1).day();
    views.datePrev.data('date', datePrev).text(datePrev.toString(dateFormat));
    var dateNext = selectedDate.clone().add(1).day();
    views.dateNext.data('date', dateNext).text(dateNext.toString(dateFormat));
    $(c).data('value', selectedDate);
}

function renderCalendar(month) {
    calendarMonth = month.clone();
    month = month.clone();
    var sel = selectedDate.clone();
    sel.clearTime();
    console.log('outerHeight of dateHolder', views.dateHolder.outerHeight());
    $(views.calendar).height((views.dateHolder.outerHeight() +
        views.dateNext.outerHeight() +
        views.datePrev.outerHeight() - 2
        ) + 'px');
    $(views.calendar).css('top', -(views.datePrev.outerHeight() - 1) + 'px');
    views.monthName.text(month.toString('MMMM, yyyy'));
    var controlsHeight = $(views.calendarControls).outerHeight();
    $(views.calendarDays).css('top', controlsHeight + 'px');
    views.calendarDays.html('');
    month.moveToFirstDayOfMonth();
    var monthName = month.getMonth(), monthStartDow = weekdays.indexOf(month.toString('ddd').toLowerCase());
    month.moveToLastDayOfMonth();
    var weeksTotal = Math.ceil((monthStartDow + month.getDate()) / 7);
    month.moveToFirstDayOfMonth();
    var dayWidth = (100 / 7), dayHeight = (100 / weeksTotal);
    do {
        var dayVal = month.toString('d');
        var day = $('<a href="javascript:void(0)" class="day" tabindex="-1"><p>' + dayVal + '</p></a>'),
            week = Math.floor(parseInt(dayVal - 1 + monthStartDow) / 7),
            dow = month.toString('ddd').toLowerCase();
        $(day).addClass(dow);
        $(day).addClass('week' + week);
        $(day).css({
            width: dayWidth + '%',
            height: dayHeight + '%',
            left: (dayWidth * weekdays.indexOf(dow)) + '%',
            top: (dayHeight * week) + '%'
        });
        if (sel.equals(month.clone().clearTime())) {
            $(day).addClass('selected');
        }
        $(views.calendarDays).append(day);
        $('p', day).data('date', month.clone());
        month.add(1).day();
    } while (monthName == month.getMonth());
}

var onDayClick = function (e) {
    console.log('day click');
    setTimeout(function () {
        selectedDate = $(e.target).data('date');
        if (!selectedDate) {
            console.log('parent', $(e.target).parents('.day').data('date'));
            selectedDate = $(e.target).parents('.day').data('date');
        }
        renderDate();
        renderCalendar(selectedDate);
        $(views.dateHolder).removeClass('open');
    });
    e.stopPropagation();
}

var onPrevMonthClick = function () {
    console.log('next month click');
    calendarMonth.add(-1).month();
    console.log('nextMonth', calendarMonth);
    setTimeout(function () {
        renderCalendar(calendarMonth);
    }, 1);
}

var onNextMonthClick = function () {
    console.log('next month click');
    calendarMonth.add(1).month();
    console.log('nextMonth', calendarMonth);
    setTimeout(function () {
        renderCalendar(calendarMonth);
    }, 1);
}

me.setDate = function (date) {
    selectedDate = date.clone();
    console.log('SELDAte', selectedDate);
    renderDate();
}

me.open = function () {
    openingDate = selectedDate.clone();
    var toOpen = $(c).parents('.openable');
    $('.openable.open', document.body).removeClass('open');
    $(toOpen).addClass('open');
    views.dateHolder.addClass('open');
    renderCalendar(selectedDate);
}

me.close = function () {
    views.dateHolder.removeClass('open');
}

me.isOpen = function () {
    return views.dateHolder.hasClass('open');
}

var onKeyDown = function (e) {
    console.log(e.keyCode);
    var redraw = false;
    if (me.isOpen()) {
        redraw = true;
        if (e.keyCode == 37) {
            selectedDate.addDays(-1);
        } else if (e.keyCode == 38) {
            selectedDate.addDays(-7);
        } else if (e.keyCode == 39) {
            selectedDate.addDays(1);
        } else if (e.keyCode == 40) {
            selectedDate.addDays(7);
        } else if (e.keyCode == 27) {
            selectedDate = openingDate;
            me.close();
        } else {
            if (e.keyCode == 9 || e.keyCode == 13) {
                me.close();
            }
            return;
        }
    } else if (e.keyCode == 13) {
        me.open();
        return;
    }
    if (redraw) {
        renderDate();
        renderCalendar(selectedDate);
        e.preventDefault();
        e.stopPropagation();
    }
}

var onFocus = function () {
    console.log('FOCUS');
    me.open();
}

var onFocusOut = function() {
    console.log('FOCUS OUT');
    me.close();
}