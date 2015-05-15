me.jsext = '$jsext';

var titles = [];
var filter = '';
var selection = 0;
var mnodes = [];
var selectionLog = [];
var distinct = [];
var openClick = false;
var el = $('.___tagbox');

var options = me.options = {
    data: [],
    waitForData: true,
    input: {

    },
    box: {
        'max-width': '100%',
        'max-height': '100%'
    },
    drop: {

    },
    sel: {

    },
    placeholder: 'Loading...',
    title: 'title',
    childs: 'childs',
    distinct: false,
    groupBy: 'type',
    groupTitles: [],
    orderBy: false,
    order: 'ASC',
    hideNoTitles: false,
    tagStyle: 'inside',
    tabindex: 0,
    deselectOnClick: true,
    limit: 5, // количество элементов в подсказке
    filter: function (text) {            // изменение фильтра
        text = text.replace(/\\/g, '\\\\');
        var match = titles.$nestedIndexOf(new RegExp(text, 'i'));
        return match;
    },
    select: function (node) {             // выбор элемента
        return node;
    },
    deselect: function (node) {
        return node;
    },
    render: function (node) {             // рендер элемента
        var span = $('<span/>');
        var title = node[options.title];
        $(span).attr('path', node.path);
        if (node.values) {
//            console.log('substituting values', title, node.values);
            for (var i in node.values) {
                title = title.replace(i, '"' + node.values[i] + '"');
            }
        }
        $(span).html(title);
        return span;
    },
    suggest: function (node) {           // рендер элемента в саггесте
        var title = node[me.options.title];
        var options = title.match(/([^\\]|^)\$\w[\w\d_]*/g);
        if (options) {
//            console.log('Found options:', options);
            for (var i = 0; i < options.length; i++) {
                var option = options[i];
                if (option.substr(0, 1) != '$') {
                    option = option.substr(1);
                }
                title = title.replace(option, '<input name="option-' + option + '"/>');
            }
        }
        return title;
    },
    createSuggest: null
};

var makeTitles = function (node) {
    var titles = [];
    if (!node.concat) {
        for (var i in node) {
            var item = node[i];
            if (item.selected) continue;
            if (item[options.title]) {
                titles[i] = item[options.title];
                if (item[options.childs]) {
                    titles[i] = makeTitles(item[options.childs]);
                }
            } else {
                titles.concat(makeTitles(item));
            }
        }
    } else {
        var nl = node.length;
        for (i = 0; i < nl; i++) {
            var item = node[i];
            if (item.selected) continue;
            if (item[options.title]) {
                titles[i] = item[options.title];
                if (item[options.childs]) {
                    titles[i] = makeTitles(item[options.childs]);
                }
            }
        }
    }
    return titles;
};

var hide = me.hide = function () {
    console.log('CLOSING', el);
    $(el).removeClass('open');
    if (options.inlineDrop) {
        $(el).css('margin-bottom', '');
    }
}

var clear = me.clear = function () {
    $(selectionLog).each(function (i) {
        var num = selectionLog[i];
        options.data[num].selected = false;
    });

    selectionLog = [];
    filter = '';
    selection = 0;
    mnodes = [];
    distinct = [];
    $('.sel', el).html('');
    $('.controls input').val('');
    redraw();
    hide();
}

var open = me.open = function () {
    $(el).addClass('open');
}

var indexOf = me.indexOf = function (type, id) {
    for (i = 0; i < options.data.length; i++) {
        var el = options.data[i];
        if (el.type == type && el.id == id)
            return i;
    }
    return undefined;
}

var select = me.select = function () {
    var index = arguments[0];
    if (arguments.length == 2) {
        index = indexOf(arguments[0], arguments[1]);
    }
    if (index === undefined) return;

    var item = options.data[index];
    var elem = $('ul li[num="' + index + '"]');
    console.log('Trying to select item', index, ':', elem);
    if (item.selected) return;
    if (checkDistinct(item)) return;

    // checking  parameters:
    var params = getItemParameters(elem);
    if (params === false) {
        return false;
    }

    // item is selected
    item.values = params;
    registerDistinct(item);
    selectionLog.push(parseInt(index));
    options.data[index].selected = true;
    var item = options.select(options.data[index]);
    src = options.render(item);
    $(src).attr('num', index).addClass('tagbox-selected-item');
    $('.closer', src).click(function (e) {
        var t = $(this).parents('.tagbox-selected-item');
        deselect($(t).attr('num'));
        $('.controls input').focus();
        $(e).stopPropagation();
    })
    $('.sel', el).append(src);
    mw = w = $(el).innerWidth() - 20;
    $('.sel>*', el).each(function (i, sel) {
        w -= $(sel).outerWidth() + 3;
        if (w < 0) {
            w = mw - $(sel).outerWidth() - 3;
        }
    })
    if (w < 150)
        w = mw;

    $('.controls input').css({
        width: w
    }).val('');
    makeTitles(options.data);
    redraw(filter);
    hide();
    setTimeout(function () {
        $('.controls input').focus();
        $(c).triggerHandler('change');
    }, 1);
    return true;
}

var deselect = me.deselect = function (num) {
    num = parseInt(num);
    //        var sli = selectionLog.indexOf(num);
    //        if (sli > -1) {
    //            selectionLog = selectionLog.splice(0, sli).concat(selectionLog.splice(sli));
    //        }
    if (isNaN(num)) return;
    if (typeof options.deselect == 'function')
        if (!options.deselect(options.data[num])) return;
    delete selectionLog[selectionLog.indexOf(num)];
    selectionLog = selectionLog.$clean();
    if (!options.data[num] || !options.data[num].selected) {
        return;
    }

    // item deselected
    removeDistinct(options.data[num]);
    options.data[num].selected = false;
    var rm = $('.sel *[num="' + num + '"]');
    console.log('TB DESELECT REMOVING:', rm);
    rm.remove();
    $(c).triggerHandler('change');
    makeTitles(options.data);
    hide();
}

var setData = me.setData = function (data) {
    me.clear();
    options.data = data;
    titles = makeTitles(options.data);
}

var setOptions = me.setOptions = function (opts) {
    $.extend(options, opts);
    console.log('Got options:', opts, '->', options);
    clear();
    titles = makeTitles(options.data);
}

var checkDistinct = function (item) {
    if (!options.distinct) {
        return false;
    }

    if (typeof options.distinct == 'string') {
        if (distinct.indexOf(item[options.distinct]) != -1) {
            return true;
        }
    } else {
        for (var distField in options.distinct) {
            if (options.distinct.hasOwnProperty(distField)) {
                var key = options.distinct[distField];
                if (options.distinct[distField] instanceof Array) {
                    if (options.distinct[distField].indexOf(item[distField]) == -1) {
                        return false
                    }
                } else {
                    if (item[distField] != options.distinct[distField]) {
                        return false;
                    }
                }

                if (distinct.indexOf(distField + ':' + item[distField]) != -1) {
                    return true;
                }
            }
        }
    }

    return false;
}

var registerDistinct = function (item) {
    if (!options.distinct) {
        return;
    }

    if (typeof options.distinct == 'string') {
        distinct.push(item[options.distinct]);
    } else {
        for (var distField in options.distinct) {
            if (options.distinct.hasOwnProperty(distField)) {
                var key = options.distinct[distField];
                if (key instanceof Array) {
                    if (key.indexOf(item[distField]) == -1) return;
                } else if (item[distField] != key) return;

                distinct.push(distField + ':' + item[distField]);
                console.log('distinct added:', distField + ':' + item[distField], distinct);
            }
        }
    }
}

var removeDistinct = function (item) {
    if (!options.distinct) {
        return;
    }

    if (typeof options.distinct == 'string') {
        var i = distinct.indexOf(item[options.distinct]);
        distinct.splice(i, 1);
    } else {
        for (var distField in options.distinct) {
            if (options.distinct.hasOwnProperty(distField)) {
                var key = options.distinct[distField];
                var i = distinct.indexOf(distField + ':' + item[distField]);
                if (i > -1) {
                    distinct.splice(i, 1);
                    console.log('distinct removed', distField + ':' + item[distField], distinct);
                }
            }
        }
    }

}

var redraw = me.redraw = function (newFilter) {
    if (!newFilter) newFilter = $('.controls input').val();
    if (options.waitForData && (!options.data || options.data.length == 0)) {
        return;
    }
    $('.controls input').attr('placeholder', options.placeholder).removeAttr('disabled');
//    console.log('TBOX redrawing with filter', newFilter, options.data);
    var match = options.filter(newFilter, options.data);
//    console.log('TBOX match', match);
    mnodes = [];
    // getting matched nodes
    for (var i = 0; i < match.length; i++) {
        var pnodes = options.data.$getPathNodes(match[i]);
//        console.log('TBOX pnodes', pnodes);

        if (pnodes instanceof Array) {
            for (var x in pnodes) {
                mnodes.push(pnodes[x]);
            }
        } else {
            if (pnodes.created) continue;
            mnodes.push(pnodes);
        }
        //            break;
    }
//    console.log('TBOX matched nodes', mnodes);
    if (mnodes.length == 0 && !options.autoCreate && !options.createSuggest) {
        return hide();
    }
    filter = newFilter;
    var suggest = '';
    var inj = 0;
    selection = null;
    var oldOB = '';
    var groups = {};

    // rendering list
    if (options.orderBy !== false) {
        var sorter = function (by, order) {
            return $(this).sort(function (i1, i2) {
                var s1 = i1[by], s2 = i2[by], m = 1;
                if (order.toUpperCase() == 'DESC') m = -1
                if (s1 > s2) return 1 * m;
                if (s1 < s2) return -1 * m;
                return 0;
            });
        }
        if (typeof options.orderBy == 'string') {
            mnodes = sorter.call(mnodes, options.orderBy, options.order);
        } else {
            $(options.orderBy).each(function (by, order) {
                mnodes = sorter.call(mnodes, by, order);
            });
        }
    }

    var suggested = 0;
    $(mnodes).each(function (i) {
        var group = 'global-group';
        suggest = '';
        // skipping already selected items
        if (mnodes[i].selected) return;
        var num = options.data.indexOf(mnodes[i]);
        if (selection === null) selection = i;
        // skipping elements with no orderTitle
        if (options.hideNoTitles && !options.groupTitles[mnodes[i][options.groupBy]])
            return;
        // skipping if there is selected node with the same as ours defined in options distinct field
        if (checkDistinct(mnodes[i])) return;

        if (options.groupBy) {
            group = oldOB = mnodes[i][options.groupBy];
            if (typeof groups[group] == 'undefined') {
                groups[group] = [];
                suggest += '<div><p>';
                if (options.groupTitles[mnodes[i][options.groupBy]]) {
                    suggest += options.groupTitles[mnodes[i][options.groupBy]];
                }
                suggest += '</p></div>';
            }
        }
        if (inj++ < options.limit || options.limit === false)
            suggest += '<li num="' + num + '" i="' + (inj - 1) + '"><p>' + options.suggest(mnodes[i]) + '</p></li>';
        if (!groups[group]) groups[group] = [];
        groups[group].push({
            id: i,
            source: mnodes[i],
            html: suggest
        });
        suggested++;
    });
    suggest = '';
    if (options.groupTitles && options.groupTitles.length) {
        $.each(options.groupTitles, function (i, el) {
            if (typeof groups[i] == 'undefined') return;
            $(groups[i]).each(function (i, node) {
                suggest += node.html;
            });
        });
    } else {
        $.each(groups, function (i, group) {
            if (typeof groups[i] == 'undefined') return;
            $(groups[i]).each(function (i, node) {
                suggest += node.html;
            });
        })
    }
    if (options.createSuggest) {
        var cs = options.createSuggest(filter, suggested);
        if (cs) {
            var item = '<li class="virtual"><p>' + cs + '</p></li>';
            suggest = item + suggest;
        }
    }
    console.log('Suggest:', suggest == '');
    if (suggest == '') hide();
    else {
        $('ul', el).html(suggest);
        open();
    }

    $('li:first', el).addClass('selected');
    $('li', el).click(
        function (e) {
            if ($(e.target).is('input')) return;
            var elem = $(this);
            var num = -1;
            if ($(elem).is('li.virtual') || $(elem).parents('li.virtual').length > 0) {
                num = autoCreate();
            } else {
                num = parseInt($(elem).attr('num'));
            }
            console.log('Clicked', elem, $(elem).attr('num'), num);
            if (num > -1 && select(num)) {
                $('.controls input').focus();
                e.stopPropagation();
            }
            hide();
        }).mouseover(function () {
            var elem = this;
            var num = parseInt($(elem).attr('i'));
            if (num != selection) {
                selection = num;
                $('li', el).removeClass('selected');
                $(this).addClass('selected');
            }
        });
    $('ul', el).css('width', $(el).width());
    if (options.inlineDrop && suggested) {
        $(el).css('margin-bottom', $('.drop', el).outerHeight() + 5);
    }
}

var getSelected = me.getSelected = function () {
    var ret = [];
    for (i = 0; i < selectionLog.length; i++) {
        ret.push(options.data[selectionLog[i]]);
    }
    if (options.autoCreate && $('.controls input').val()) {
        if (options.onCreate) {
            var newitem = options.onCreate($('.controls input').val(), options.data);
            if (newitem) {
                newitem.created = true;
                ret.push(newitem);
            }
        }
    }
    return ret;
}

titles = makeTitles(options.data);

$(el).addClass('tagbox');
if (options.tagStyle == 'inside') {
    $(el).html('<span class="sel"/><span class="controls"><input type="text" placeholder="Loading..." disabled="true"/></span><ul class="drop hidden"></ul>');
} else if (options.tagStyle == 'outside') {
    $(el).html('<span class="sel"/><span class="controls"><input type="text" placeholder="Loading..." disabled="true"/><ul class="drop hidden"></ul></span>');
}
$(el).css(options.box);
$('.controls input').css(options.input).attr('tabindex', options.tabindex);
$('.sel', el).css(options.sel).delegate('*[num]', 'click', function (e) {
    if (options.deselectOnClick) {
        deselect($(this).attr('num'));
        e.stopPropagation();
    }
});

$('ul', el).css(options.drop);
$('.controls input').keyup(
    function (e) {
        var newFilter = $('.controls input').val();
        if (newFilter != filter) {
            redraw(newFilter);
        }
    }).keydown(
    function (e) {
        var num = $('ul li:eq(' + selection + ')').attr('num');
        if ($('.drop', el).css('display') != 'block') {
            num = undefined;
        }
        var newFilter = $('.controls input').val();
        switch (e.keyCode) {
            case 8:        // bsp
                if (this.value == '') {
                    deselect(selectionLog.pop());
                    redraw();
                }

                break;
            case 13:        // enter
                console.log('ENTER PRESSED; num =', num);
                if (num === undefined) {
                    num = autoCreate();
                    hide();
                }
                if (num !== undefined) {
                    if (select(num)) {
                        redraw();
                        hide();
                    }
                }
                e.preventDefault();
                $('.controls input').val('').focus();
                break;
            case 27:        // escape
                hide();
                $('.controls input').blur();
                break;
            case 38:        // up arrow
                $('li', el).removeClass('selected');
                if (--selection < 0) selection = 0;
                var sel = $($('li', el)[selection]).addClass('selected');
                var pos = sel.position();
                var scroll = $('ul', el).scrollTop();
                var t = scroll + pos.top;
                if (t < scroll)
                    $('ul', el).scrollTop(t);
                else if (t == scroll) {
                    $('ul', el).scrollTop(0);
                }
                e.stopPropagation();
                return false;
            case 40:        // down arrow
                $('li', el).removeClass('selected');
                if (++selection > $('li', el).length - 1) selection = $('li', el).length - 1;
                var sel = $($('li', el)[selection]).addClass('selected');
                var pos = sel.position();
                var t = pos.top + sel.height() - $('ul', el).height() + 30 + $('ul', el).scrollTop();
                if (t > 0)
                    $('ul', el).scrollTop(t);
                e.stopPropagation();
                if ($('ul', el).css('display') == 'none') {
                    open();
                    redraw();
                }
                return false;
            default:
                break;
        }
        filter = newFilter;
    }).click(function () {
        redraw();
    });


$(el).click(function (e) {
    if (e.target == this) {
        $('.controls input').focus().click();
    }
    openClick = true;
    //        e.stopPropagation();
    //        e.preventDefault();
    //        return false;
});

$(c).on('resume', function () {
    bindParamsEvents();
    hide();
});


var getItemParameters = function (el) {
    var inputs = $('input', el);
    var values = [];
    console.log('---- Checking item', el, ' Inputs:', inputs);
    for (var i = 0; i < inputs.length; i++) {
        var input = $(inputs[i]);
        console.log('-------- ', 'item', i, 'val:', input.val());
        if (input.val() == '') {
            setTimeout(function () {
                console.log('Focusing on', input);
                input.focus();
            }, 1);
            return false;
        } else {
            var name = $(input).attr('name').replace('option-', '');
            values[name] = input.val();
        }
    }

    console.log('---- ', 'all inputs have values');
    return values;
}

var bindParamsEvents = function () {
    $(c).on('change', 'input', function (e) {
        e.stopPropagation();
    });

    $(c).on('keydown', 'li input', function (e) {
        var keyCode = e.keyCode;
//        console.log('kcode', keyCode);
        var el = $(e.target).parents('li');
        switch (e.keyCode) {
            case 27:
                $('input', el).val('');
                setTimeout(function () {
                    $('.controls input').focus();
                }, 1);
                break;
            case 9:
            case 13:
                var num = $(el).attr('num');
//                console.log('num', num, el);
                select(num);
                break;
        }
    })
}

var autoCreate = function() {
    var ntitle = $('.controls input').val();
    var num = undefined;
    if (options.onCreate && ntitle) {
        console.log('Autocreating:', ntitle);
        var newitem = options.onCreate(ntitle, options.data);
        if (newitem && options.data.indexOf(newitem) == -1) {
            newitem.created = true;
            num = options.data.length;
            options.data.push(newitem);
        } else {
            num = options.data.indexOf(newitem);
        }
    }
    return num;
}