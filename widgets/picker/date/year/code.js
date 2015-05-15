var value = parseInt($(c).attr('value'));

if (!value) value = new Date().getFullYear();
$('.value').html(value);
$(c).addClass('openable');
$('#new_value').val(value);

$(c).on('resume', function() {
    $('#new_value').on('keyup', onNewValue);
});

$(c).on('open', function() {
    $('#new_value').focus();
})

var onNewValue = function(e) {
    value = parseInt($(e.target).val());
    if (value < 999 || value > 10000) value = false;

    if (!value) {
        $(e.target).addClass('error');
        return;
    } else {
        $(e.target).removeClass('error');
    }
    $('.value').html(value);
    $(c).attr('value', value);
    if (e.keyCode == 13) {
        $(c).removeClass('open');
        $(c).trigger('change', value);
    }
    if (e.keyCode == 27) {
        $(c).removeClass('open');
    }
}

me.getValue = function() {
    return value;
}