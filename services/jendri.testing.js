me.jendri = 'jendri';

var TestCase = {
    isTrue: function(v) {
        if (!v) throw 'Value false';
    }
}

$(me).on('create', function() {
    me.jendri.register('jendri.testing', TestCase);
})
