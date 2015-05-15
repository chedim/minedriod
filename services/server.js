me.jendri = 'jendri';

var Server = {
    query: function () {
        var name, method, args, cb;
        for (var i = 0; i < arguments.length; i++) {
            if (typeof arguments[i] == 'string') {
                if (!name) name = arguments[i];
                else if (!method) method = arguments[i].toUpperCase();
            } else if (typeof arguments[i] == 'object') {
                if (!args) args = arguments[i];
            } else if (typeof arguments[i] == 'function') {
                if (!cb) cb = arguments[i];
            }
        }
        Server.raw('application/' + name, method, args, cb);
    },
    raw: function () {
        var name, method = 'GET', args, cb;
        for (var i = 0; i < arguments.length; i++) {
            if (typeof arguments[i] == 'string') {
                if (!name) name = arguments[i];
                else if (!method) method = arguments[i].toUpperCase();
            } else if (typeof arguments[i] == 'object') {
                if (!args) args = arguments[i];
            } else if (typeof arguments[i] == 'function') {
                if (!cb) cb = arguments[i];
            }
        }
        name = me.jendri.api + name;
        if (args && method != 'GET') {
            args = JSON.stringify(args);
        }
        jQuery.ajax({
            url: name,
            method: method,
            dataType: 'json',
            data: args,
            success: function (a, b) {
                if (cb) cb(null, a);
            },
            error: function (a, b, e) {
                if (!e) e = 'error';
                if (cb) cb(e);
            }
        });
    },
    user: function (cb) {
        jQuery.ajax({
            url: 'application/login/?r=' + Math.random(),
            method: 'GET',
            dataType: 'json',
            success: function (a, b) {
                if (cb) cb(null, a);
            },
            error: function (a, b, e) {
                console.log(a.status);
                if (!e) e = 'error';
                if (cb) cb(e);
            }
        });
    },
    login: function (login, pass, cb) {
        var data = {
            login: login,
            password: pass
        };

        jQuery.ajax({
            url: 'application/login?' + Math.random(),
            method: 'POST',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data),
            dataType: 'json',
            success: function (a, b) {
                if (cb) cb(null, a);
            },
            error: function (a, b, e) {
                if (cb) cb(e);
            }
        })
    },
    templateTypes: function (cb) {
        jQuery.ajax({
            url: 'application/category',
            method: 'GET',
            dataType: 'json',
            success: function (a, b) {
                if (cb) cb(null, a);
            },
            error: function (a, b, e) {
                if (cb) cb(e);
            }
        })
    },
    logout: function (cb) {
        jQuery.ajax({
            url: 'application/login',
            method: 'DELETE',
            success: function (a, b) {
                if (cb) cb(null, a);
            },
            error: function (a, b, e) {
                if (cb) cb(e);
            }
        })
    },
    uploads: {},
    createProduct: function (data, cb) {
        Server.query('product', 'PUT', data, cb);
    },
    uploadCallback: function (url) {
        if (Server.uploads[url]) Server.uploads[url]();
    }
}
$(me).on('create', function() {
    console.log('Server created!');
    me.jendri.register('server', Server);
});

console.log('BINDED');
