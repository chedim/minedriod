/**
 * Created by chedim on 3/26/15.
 */

var oldAlert = window.alert;
window.alert = function() {
    oldAlert.apply(window, arguments);
}

window.error = function() {
    oldAlert.apply(window, arguments);
}


$(me).on('create', function() {
    me.jendri.register('alert', {});
})