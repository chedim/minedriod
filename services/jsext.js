Object.defineProperty(Object.prototype, '$method', {
    enumerable: false,
    value: function (name, fun) {
        var t = this.prototype;
        if (!t) t = this;
        Object.defineProperty(t, name, {
            enumerable: false,
            value: fun
        });
    }
});

Array.$method('$nestedIndexOf', function (value) {
    var found = [];

    for (var i in this) {
        var item = this[i];
        if (item.$nestedIndexOf) {
            var subFound = item.$nestedIndexOf(value);
            subFound.unshift(i);
            found.push(subFound);
        } else if (value.test && (typeof item == 'string')) {
            if (value.test(item))
                found.push(i);
        }
    }
    return found;
});

Array.$method('$getPathNodes', function (p, childs) {
    if (!childs) childs = 'childs';
    if (!(p instanceof Array)) return this[p];
    var myKey = p.shift();
    var item = this[childs][myKey];
    var ret = [item];
    if (item.getByPath) {
        ret = ret.concat(item.$getPathNodes(p, childs));
    }
    return ret;
});


Array.$method('$clean', function () {
    var res = [];
    for (var o = 0; o < this.length; o++) {
        var x = this[o];
        if (x !== undefined)
            res.push(x);
    }
    return res;
});

var key = '!!__deffered__!!';
Object.$method('$wait', function (cb) {
    //console.log('$waiting on', this, this[key]);
    if (this[key] !== true && !(this[key] instanceof Array)) {
        //console.log('$wait on',this,'aborted');
        return cb(this);
    } else {
        //console.log('saving callback', cb, 'to', JSON.stringify(this[key]));
        if (!(this[key] instanceof Array)) this[key] = [];
        this[key].push(cb);
        //console.log('saved callback', cb, 'as a', this[key].length);
    }
});

Object.$method('$deffer', function () {
    //console.log('$deffer');
    var me = this;
    this[key] = true;
    return function defferedCallback() {
        var cb = me[key];
        delete me[key];
        if (cb instanceof Array) {
            //console.log('$wait completed');
            for (var i = 0; i < cb.length; i++) {
                cb[i].apply(me, arguments);
            }
        }
    }
});

Object.$method('$promise', function (name) {
    console.log('$promise', name);
    if (!this[key]) {
        this[name] = {};
        this[name][key + '_parent'] = this;
        this[name][key + '_name'] = name;
        this[name][key] = true;
    }
});

Object.$method('$resolve', function (value) {
    if (typeof value == 'undefined') value = this;
    var cb = this[key];
    var name = this[key + '_name'];
    if (name === undefined) {
        // already resolved?
        value = this;
    } else {
        console.log('$resolve', name);
        this[key + '_parent'][name] = value;
    }
    if (cb instanceof Array) {
        //console.log('wait for '+name+' resolved', cb);
        for (var i = 0; i < cb.length; i++) {
            cb[i](value);
        }
        return;
    }
    delete this[key];
    //console.log('resolved before $wait', this[key+'_name'], cb);
})

var providerKey = '=='+Math.floor(Math.random()*10000000)+'==';
var getProvider = function(o) {
    if (!o[providerKey]) {
        var provider = {
            provider: null,
            receivers: []
        };

        o.$method(providerKey, function() {
            return provider;
        });
    }

    return o[providerKey]();
}

var getChildProvider = function(parent, name) {
    if (!parent[name]) parent[name] = {};
    return getProvider(parent[name]);
}

var copyProviderRecord = function(from, to) {
    to[providerKey] = from[providerKey];
    return to;
}

Object.$method('$provider', function(name, getterFunction, cb) {
    var parent = this;
    var provider = getChildProvider(parent, name);
    provider.name = name;
    provider.provider = function(cb) {
        getterFunction(function(err, value) {
            provider.provided = true;
            parent[name] = copyProviderRecord(parent[name], value);
            var receiversToWait = provider.receivers.length;
            var errors = [];
            for (var i=0; i<provider.receivers.length; i++) {
                provider.receivers[i](parent[name], function(err) {
                    if (err) errors.push(err);
                    if (--receiversToWait < 1) {
                        if (cb) {
                            if (errors.length > 0) {
                                cb(errors);
                            } else {
                                cb();
                            }
                        }
                    }
                });
            }
        });
    }

    parent[name].$refresh(cb);
});

Object.$method('$on', function(receiver) {
    var target = this;
    var provider = getProvider(target);
    var index = provider.receivers.length;
    provider.receivers.push(receiver);
    if (provider.provided) {
        receiver(target, function(err) {
            throw err;
        });
    }

    receiver.$off = function() {
        provider.receivers.splice(index,1);
        delete receiver['$off'];
    }
});

Object.$method('$refresh', function(cb) {
    var provider = getProvider(this);
    if (provider.provider && provider.provided !== false) {
        provider.provided = false;
        provider.provider(cb);
    }
})