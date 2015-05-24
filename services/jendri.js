!function ($) {

    // ------ Dependency Injection
    var options = {
        source: '',
        startup: 'router',
        home: 'index',
        debug: false,
        container: 'body',
        api: '',
        siteVersion: undefined,
        interface: 'default'
    };
    var AppCache;
    var ajax = function (file, options) {
        if (AppCache) {
            AppCache.load(file, options);
        } else {
            $.ajax(file, options);
        }
    }

    if (window.Jendri) {
        options = $.extend(true, options, window.Jendri);
        delete window['Jendri'];
    }
    var Jendri = options;
    window['J'] = Jendri;
    var services = [], hash = {}, hashBack = {}, tagCb = {};
    Jendri.__callbacks = tagCb;
    Jendri.register = function (tags, object) {
        if (!(tags instanceof Array)) tags = [tags];
        object.__usageCount = 0;
        var serviceId = services.push(object) - 1;
        hashBack[serviceId] = tags;
        for (var i = 0; i < tags.length; i++) {
            if (!hash[tags[i]]) hash[tags[i]] = [];
            hash[tags[i]].unshift(serviceId);
        }
        console.log('Service ', object, 'registered as', tags);

        for (var x = 0; x < tags.length; x++) {
            var tag = tags[x];
            if (!tagCb[tag]) continue;
            for (var i = 0; i < tagCb[tag].length; i++) {
                if (typeof tagCb[tag][i] == 'function') {
                    if (object !== null) {
                        if (object.__usageCount == 0) {
                            $(object).triggerHandler('resume');
                        }
                        object.__usageCount++;
                    }
                    tagCb[tag][i](null, object);
                }
            }
            delete tagCb[tag];
        }

    };

    Jendri.read = function (tag) {
        if (hash[tag] && hash[tag].length > 0) {
            return services[hash[tag][0]];
        } else return null;
    }

    Jendri.get = function (tag, callback) {
        var oldStyle = tag.substr(0, 1) == '$';
        if (oldStyle) {
            tag = tag.substr(1);
        }
        if (hash[tag] && hash[tag].length > 0) {
            if (services[hash[tag][0]].__usageCount == 0) {
                $(services[hash[tag][0]]).triggerHandler('resume');
            }
            services[hash[tag][0]].__usageCount++;
            return callback(null, services[hash[tag][0]]);
        } else if (typeof tag == 'string') {
            if (tagCb[tag]) {
                tagCb[tag].push(callback);
                console.log('waiting', tag);
            } else {
                tagCb[tag] = [callback];
                console.log('loading', tag, hash[tag]);
                loadService(Jendri.source + '/services/' + tag + '.js', function (e) {
                    console.log('loaded', tag, services[tag]);
                    if (e) {
                        console.error('Service not loaded:', e);
                        for (var i = 0; i < tagCb[tag].length; i++) {
                            if (typeof tagCb[tag][i] == 'function') {
                                tagCb[tag][i](e, null);
                            }
                        }
                        delete tagCb[tag];
                        return;
                    }

                    if (oldStyle) {
                        // auto register
                        if (window[tag]) {
                            Jendri.register(tag, window[tag]);
//                            delete window[tag];
                        } else {
                            Jendri.register(tag, {});
                        }
                    }


                });
            }

        }
    }

    Jendri.free = function (serviceObject) {
        serviceObject.__usageCount--;
        if (serviceObject.__usageCount == 0) {
            $(serviceObject).triggerHandler('pause');
        }
    }

    Jendri.unregister = function (object) {
        var serviceId = services.indexOf(object);
        if (serviceId < 0) return;
        var tags = hashBack[serviceId];
    }

    Jendri.report = function () {
        console.log(services, hash, hashBack);
    }

    Jendri.register('jendri', Jendri);

    var loadService = function (location, cb) {
        ajax(location, {
            dataType: 'text',
            success: function (data) {
                console.log('Loaded service:', location);
                var code = '// ' + location + '\n' +
                    'var me = this;\n';
                var fun = new Function('$', code + data);
                // dependencies
                var module = new fun($);

                // injecting Jendri
                if (!module.jendri || module.jendri == 'jendri') module.jendri = Jendri;

                var deps = Object.keys(module);
                var tasks = deps.length;
                var depResolvingError = undefined;

                var onDependenciesResolved = function (e) {
                    console.log('Dependencies of', location, 'resolved', e);
                    if (!e) $(module).triggerHandler('create');
                    if (cb) return cb(e);
                }

                var resolvedDependencyCallback = function (e, n, d) {
                    if (e) {
                        if (false === (d = $(module).triggerHandler('depfail', n))) {
                            depResolvingError = e;
                        } else {
                            e = undefined;
                        }
                    }
                    if (n) {
                        module[n] = d;
                    }
                    if (--tasks <= 0) {
                        return onDependenciesResolved(depResolvingError);
                    }
                }
                var resolveDependency = function (key, dependency) {
                    var optional = dependency.substr(0, 1) == '~';
                    if (optional) dependency = dependency.substr(1);

                    Jendri.get(dependency, function (e, d) {
                        if (e && optional) {
                            e = undefined;
                            d = undefined;
                        }
                        return resolvedDependencyCallback(e, key, d);
                    });
                }
                if (deps.length == 0) return onDependenciesResolved();
                for (var i = 0; i < deps.length; i++) {
                    var key = deps[i];
                    var dependency = module[key];
                    console.log('Dep#', i, ':', key, '<-', dependency);
                    if (typeof dependency != 'string') {
                        resolvedDependencyCallback();
                        continue;
                    } else {
                        resolveDependency(key, dependency);
                    }
                }
            },
            error: function (a, b, e) {
                if (cb) return cb(e);
            }
        });
    }

    var origAppend = $.fn.append;

    $.fn.append = function () {
        return origAppend.apply(this, arguments).trigger("append");
    };

// Starting application

    window.DEBUG = Jendri.debug;
    Jendri.get('console', function () {
        init();
    });

    var init = function () {
        Jendri.get('appcache', function (e, r) {
            if (!e && r) {
                console.log('Using AppCache service');
                AppCache = r;
            }
            Jendri.get(Jendri.startup);
        });
    }
    $(window).on('beforeunload', function () {
        for (var i = 0; i < services.length; i++) {
            $(services[i]).triggerHandler('destroy');
        }
    })
}(jQuery);

