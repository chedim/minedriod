/**
 * Created by chedim on 3/19/15.
 */

var me = this;
me.jendri = 'jendri';

var handler = function (e) {
    if (e.which != 1) return;
    var parent = $(e.target).parent();
    var isSelected = $(e.target).is('.selected');
    if (parent.is('.one')) {
        $('.selected', parent).removeClass('selected');
        if (!isSelected)
            $(e.target).addClass('selected');
    } else {
        $(e.target).toggleClass('selected');
    }
    $(e.target).parent().trigger('change');
}

$(me).on('create', function () {
    me.jendri.register('selection', me);
})


$(me).on('resume', function () {
    $(document.body).on('click', '.selection > *', handler);
});

$(me).on('pause', function () {
    $(document.body).off(handler);
})