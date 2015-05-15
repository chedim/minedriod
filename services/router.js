// ------ DEPENDENCIES
me.jendri = 'jendri';
me.async = '$async';
me.scoped = '$jquery.scoped';
me.less = '~$less';
me.testing = 'jendri.testing';
me.jsext = '$jsext';
me.appcache = '~appcache';

// ------ ROUTER
var Router = {};
var previousLoadedAnchor = null, previousLoadedPage = null, pageComponent = null;
var prefix = null, prefixAnchor = null;
var interfaceName = null, interfaceLink = null, interfaceComponent = null;
var body = $('body');
var mainContainer = '#content';
var mainContainerParent;
var lock = [];
var uiReady = false;

/**
 * Initializes window hash checker
 */
Router.init = function () {
    console.log('Routerinit');
    Router.setUI(me.jendri.interface, function() {
        console.log('Starting router checker');
        var routerChecker = setInterval(function () {
            if (window.location.hash !== previousLoadedAnchor) {
                $(window.location).trigger('change');
                try {
                    console.log('Reloading page', uiReady);
                    Router.reload(function() {
                        if (!me.jendri.debug)
                            console.clear();
                    });
                } catch (e) {
                    alert("Client error.");
                    clearInterval(routerChecker);
                    if (!me.jendri.debug)
                        console.clear();
                    throw e;
                }
            }
        }, 100);
    });
}

$(window).on('beforeunload', function (e) {
    $('*[component]').triggerHandler('free');
});

Router.savePrefix = function () {
    var link = decodeLink(window.location.hash);
    prefix = link.page;
    prefixAnchor = location.hash;
    previousLoadedAnchor = '';
    previousLoadedPage = prefix;
    location.hash = '';
}

Router.dropPrefix = function () {
    previousLoadedAnchor = prefixAnchor;
    previousLoadedPage = prefix;
    prefix = null;
    prefixAnchor = null;
    window.location.hash = prefixAnchor;
}

/**
 * decodes url and parameters contained in url
 * @param url
 * @returns {page: string, {args: {name:val}}}
 */
var decodeLink = function (url) {
    var result = {
        args: {}
    };
    var page = url;
    if (page.match('\\?')) {
        var args = page.split('?');
        result.page = args.shift();
        args = args.join('?').split('&');
        for (var i = 0; i < args.length; i++) {
            var arg = args[i].split('=');
            result.args[decodeURIComponent(arg[0])] = decodeURIComponent(arg[1]);
        }
    } else {
        result.page = page;
    }
//        if (prefix) {
//            if (!result.page) {
//                result.page = prefix;
//            } else {
//                result.page = prefix + '/' +result.page;
//            }
//        }
    return result;
}

Router.setUI = function (ui, cb) {
    if (ui == interfaceLink) return cb();
    var link = decodeLink(ui);
    console.log('UI:', ui, '->', link);
    if (interfaceName == link.page) {
        interfaceComponent.trigger('arguments', link.args);
        if (cb) cb();
    } else {
        if (interfaceComponent == null) {
            interfaceComponent = $('<DIV/>');
            $(me.jendri.container).append(interfaceComponent);
        }
        uiReady = false;
        interfaceName = link.page;
        console.log('router loading interface', link.page);
        Router.load(interfaceComponent, 'interfaces/' + link.page, link.args, function () {
            uiReady = true;
            console.log('!!! INTERFACE LOADED');
            if (cb) cb();
            else Router.reload();
        });
    }
}

Router.setInterfaceFor = function (type, cb) {
    switch (type) {
        case '1':
            Router.setUI('user', cb);
            break;
        case '2':
            Router.setUI('manager', cb);
            break;
        case '666':
            Router.setUI('admin', cb);
            break;
        default:
            Router.setUI('unapproved', cb);
    }
}

Router.setMainContainer = function (container) {
    mainContainer = $(container);
}

Router.default = function (link) {
    var hash = window.location.hash;

    if (!hash || hash == '#') {
        console.log('default navigate:', link);
        Router.navigate(link);
    } else {
        console.log('default navigate:', hash);
    }
    uiReady = true;
}

//
Router.reload = function (cb) {
    if (!uiReady) return;
    var link = decodeLink(window.location.hash.substr(1));
    if (!link.page) {
        link.page = me.jendri.home;
    }
    console.log('Link:', link, 'Previous:', previousLoadedPage);
    if (link != previousLoadedPage) {
        this.load(mainContainer, 'pages/' + link.page, link.args, cb);
        previousLoadedAnchor = window.location.hash;
        previousLoadedPage = link.page;
    } else {
        $(mainContainer).trigger('arguments', link.args);
        if (cb) cb();
    }
}

Router.uiReady = function (b) {
    uiReady = b;
}

Router.isUiReady = function () {
    return uiReady;
}

Router.load = function (target, link, args, callback) {
    console.log('!!!', 'loading', link, 'to', target);
    if (typeof target == "string") {
        target = $(target, $(me.jendri.container));
    }
    $('*[component]', target).triggerHandler('free');
    $(target).triggerHandler('free');
    $(target).off();
    $(target).addClass('loading_component').removeClass('component_failed');

    if (lock.indexOf(target) > -1) {
        console.log('LOCKED:', target);
        return;
    }

    var moduleCode;
    lock.push(target);

    var ajax = function(file, options) {
        if (me.appcache) {
            me.appcache.load(file, options);
        } else {
            $.ajax(file, options);
        }
    }
    var verifyModule = function (cb) {
        ajax(me.jendri.source + '/' + link + '/verify.js', {
            dataType: 'text',
            success: function (data) {
                var code = '// ' + link + '\n' +
                    'var me = this;\n';
                console.log('Creating verify function for', link);
                var fun = new Function('c', '$', 'module', 'result', code + data);
                fun = $.extend(true, fun, me.testing);
                console.log('FUN', me.testing, fun);
                fun.apply(me.testing, [target, $, moduleCode, cb]);
            },
            error: function (a, b, e) {
                return cb(e);
            }
        });
    }

    var fin = function (e) {
        var i = lock.indexOf(target);
        delete lock[i];
        if (e) {
            $(target).html('');
            $(target).attr('component-failed', link).addClass('component_failed').removeClass('loading_component');
            console.error('Failed to load component', link, 'to', target, '\nReason:', e);
        } else {
            $(target).removeClass('loading_component');
            $(target).attr('component', link);
            console.info('Module loaded: '+link+' ->', target);
        }
        if (args) {
            $(target).triggerHandler('arguments', args);
        }
        if (me.jendri.testing) {
            verifyModule(function (e) {
                if (callback) callback(e, target, link, arguments);
                else if (e) {
                    console.error(e);
                }
            });
        } else {
            if (callback) callback(e, target, link, arguments);
            else if (e) {
                console.error(e);
            }
        }
    }

    var loadStyle = function (error) {
        ajax(me.jendri.source + link + '/style.' + ((me.less) ? 'less' : 'css'), {
            dataType: 'text',
            success: function (data) {
                var addStyle = function (css) {
                    var elem = $('<style type="text/css" scoped="scoped"></style>');
                    $(elem).text(css);
                    $(target).prepend(elem);
                    loadCode(error);
                }
                console.log('LESS>', me.less);
                if (me.less) {
                    data = '*[component="' + link + '"] {\n' + data + '\n}';
                    var parser = new (me.less.Parser);
                    parser.parse(data, function (e, tree) {
                        if (e) return loadCode(e);
                        var css = tree.toCSS();
                        addStyle(css);
                    })
                } else {
                    addStyle(data);
                }
            },
            error: function (a, b, e) {
                e.previousError = error;
                if (a.status == 404 && !error) e = null;
                console.warn('Failed to load style for', link, ':', error);
                if (!error) e = null;
                loadCode(e);
            }
        });
    }

    var loadCode = function (error) {
        ajax(me.jendri.source + link + '/code.js', {
            dataType: 'text',
            success: function (data) {
                var code = '// ' + link + '\n' +
                    'var me = this;\n';

                var fun = new Function('c', '$', code + data);
                var fakeJQuery = function() {
                    if (arguments.length == 1 && typeof arguments[0] == 'string') {
                        arguments = [arguments[0], target];
                    }
                    return $.apply(null, arguments);
                };
                fakeJQuery.__proto__ = $;
                var deps = new fun(target, fakeJQuery);
                console.info(link,'code.');
                $(target).process(deps);
                moduleCode = deps;
                me.async.forEach(Object.keys(deps), function (key, cb) {
                    if (typeof deps[key] != 'string') return cb();
                    me.jendri.get(deps[key], function (e, m) {
                        console.log(link, '<=', deps[key], e);
                        if (e) return cb(e);
                        deps[key] = m;
                        cb();
                    });
                }, function onDependenciesResolved(err) {
                    if (err) {
                        console.error('Dependencies failed for', link, ':', err);
                        err.previousError = error;
                        fin(err);
                    } else {
                        console.log('Dependencies resolved for', link+'@', target);
                        console.warn('start:', link, target);
                        $(target).triggerHandler('start', args);
                        target.$wait(function() {
                            console.warn('Start completed:', link, target);
                            Router.widgetize(target, function() {
                                console.warn('resume:', link);
                                $(target).triggerHandler('resume');
                                target.$wait(function() {
                                    console.log('resume complete:', link);
                                    fin();
                                });
                            });
                        });
                    }
                });
            },
            error: function (a, b, e) {
                console.error('No code found for', link, e);
                Router.widgetize(target, function() {
                    e.previousError = error;
                    if (!error) e = null;
                    fin(e);
                })
            }
        });
    }

    var pageFin = function (result) {
        loadStyle(result);
    }

    ajax(me.jendri.source+ link + '/page.html', {
        success: function (data) {
            console.log(link, 'html');
            var content = $(target).html();
            console.log('-->',link, 'content', target, content);
            var template = $(data);
            $('content', template).html(content);
            $(target).html('');
            $(target).append(template);
            pageFin();
        },
        error: function (a, b, e) {
            console.warn('Failed to load html for', link, ':', e);
            pageFin(e);
        }
    });
}

Router.bindMutationObserver = function (target) {
    var MutationObserver = window.MutationObserver || window.WebKitMutationObserver;
    var observer = new MutationObserver(function (mutations, observer) {
        $(target).trigger('mutation', mutations);
    });
    try {
        observer.observe(target, {
            subtree: true,
            attributes: true
        });
    } catch (e) {
        console.error(e);
    }
}

Router.widgetize = function (target, callback) {
    var containers = $('*[widget]', target);
    var processed = 0;
    var cb = new Function();
    if (callback) {
        cb = function () {
            if (++processed >= containers.length) {
                return callback();
            }
        }
    }

    if (containers.length == 0) {
        if (callback) return callback();
    }

    for (var i = 0; i < containers.length; i++) {
        var container = containers[i];
        var isTemplate = $(container).parents('.templates');
        if (isTemplate.length > 0) {
            cb();
            continue;
        }
        var link = decodeLink($(container).attr('widget'));
        $(container).removeAttr('widget');
        Router.load(container, 'widgets/' + link.page, link.args, cb);
    }
}

Router.navigate = function (url) {
    window.location.hash = url;
}

Router.getCurrentPage = function () {
    var link = decodeLink(window.location.hash.substr(1));
    if (!link.page) link.page = 'index';
    return link.page;
}

Router.getCurrentInterface = function () {
    return interfaceName;
}

Router.getMainContainer = function () {
    return mainContainer;
}


$(me).on('create', function () {
    me.jendri.register('router', Router);
    Router.init();
})


$(me).on('depfail', function (e, name) {
    console.log('Router: depfail:', name);
    return null;
});

jQuery.fn.process = function() {
    if (arguments[0]) {
        $(this).data('process', arguments[0]);
    } else {
        return $(this).data('process');
    }
}