var me = this;
me.jendri = 'jendri';

var handler = function (e) {
    if (e.which != 1) return;
    var toOpen = null;
    if ($(e.target).is('.openable')) {
        if (!$(e.target).is('open'))
            toOpen = e.target;
    } else if ($(e.target).is('.noopen') || $(e.target).parents('.noopen').length > 0) {
        return;
    }

    var parent = $(e.target).parents('.openable').not('.open');
    var keepOpened = $(e.target).parents('.openable.open');
    if (parent.length > 0) {
        toOpen = parent;
    }
    var toClose = $('.openable.open');
    toClose.removeClass('open');
    $(keepOpened).addClass('open');
    $(toOpen).addClass('open').triggerHandler('open');
    var toTrigger = toClose.not('.open');
//    console.log('OPENABLE REPORT. Clicked:', e.target, 'parent:', $(e.target).parent(), 'To open:', toOpen, 'toClose:', toClose, 'keepOpened:', keepOpened
//        , 'toTrigger:', toTrigger);
    toTrigger.triggerHandler('close');
};

$(me).on('create', function () {
    me.jendri.register('openable', me);
});

$(me).on('resume', function () {
    $(document.body).on('click', handler);
});

$(me).on('pause', function () {
    $(document.body).off(handler);
})