var db, siteVersion, initialized;

var DB_SCHEMA = 1;

me.jendri = 'jendri';
window.indexedDB = window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;


var AppCache = {
    load: function (file, options) {
        if (siteVersion !== undefined) {
            var t = db.transaction(["files"])
            var store = t.objectStore("files");
            var request = store.get(file);
            request.onsuccess = function (event) {
                if (request.result) {
                    if (request.result.version != siteVersion) {
                        AppCache.dropVersions(file, options);
                    } else {
                        if (options.success)
                            options.success(request.result.content);
                    }
                } else {
                    AppCache.ajax(file, options);
                }
            }

            request.onerror = function (event) {
                $.ajax(file, options);
            }

            return;
        } else {
            $.ajax(file, options);
        }
    },

    dropVersions: function (file, options) {
        db.transaction(["files"], "readwrite").
            objectStore("files").delete(file).onsuccess = function () {
            AppCache.ajax(file, options);
        }
    },

    ajax: function (file, options) {
        if (siteVersion === undefined)
            return $.ajax(file, options);

        var osuccess = options.success;
        options.success = function (data) {
            var t = db.transaction(["files"], "readwrite");
            t.objectStore("files").put({version: siteVersion, name: file, content: data});
            if (osuccess) osuccess(data);
        }

        $.ajax(file, options);
    }
}


$(me).on('create', function () {
    var request = indexedDB.open("appcache", DB_SCHEMA);
    request.onsuccess = function (e) {
        db = e.target.result;
        me.jendri.register('appcache', AppCache);
        siteVersion = me.jendri.siteVersion;
        if (!siteVersion) console.log('AppCache disabled: no site version set');
    }

    request.onupgradeneeded = function (e) {
        var db = e.target.result;

        db.createObjectStore("files", {keyPath: "name"});
    }

})