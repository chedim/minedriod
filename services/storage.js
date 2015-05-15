me.jendri = 'jendri';

var Storage = {}, key = '__jendri_storage';
$(me).on('create', function() {
    me.jendri.register('storage', Storage);

    var savedValues = JSON.parse(localStorage.getItem(key));
    if (savedValues) {
        $(Storage).extend(savedValues);
    }
    console.log('Storage:', Storage);
});

$(Storage).on('destroy', function() {
    delete Storage.__usageCount;
    localStorage.setItem(key, JSON.stringify(Storage));
})