me.openable = 'openable';

var value = $(c).attr('value');

if (!value) value = new Date().getMonth();
$('.value').html($('.month:eq('+value+')').html());
$(c).addClass('openable');


$(c).on('resume', function() {
    $('.month').click(onMonthClicked);
});

var onMonthClicked = function(e) {
    value = $(e.target).index();
    $('.value').html($(e.target).html());
    $(c).attr('value', value);
    $(c).removeClass('open');
    $(c).trigger('change', value);
}

me.getValue = function() {
    return value;
}