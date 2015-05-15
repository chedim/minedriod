var page = 0;
me.storage = 'storage';
me.openable = 'openable';
me.selection = 'selection';
me.server = 'server';
me.datejs = 'datejs';
me.async = 'async';
var currentFilter;
var currentPage = 0;
var pageLimit = 10;

var renderHash = {};
var renderOrder = [];

var lineTpl = $('.time.template'), editor = $('.editor.template'), list = $('.list'), loader = $('.loader'),
    loadmore = $('.loadmore'), addForm;

$(c).on('start', function () {
    me.reload();
});

me.reload = function (skip, items) {
    me.storage.$promise('times');
    if (skip === undefined) skip = 0;
    if (items === undefined) items = getAllLines().length;
    if (items == 0) items = pageLimit;
    var filter = {
        $skip: skip,
        $limit: items
    };
    var url = '/wp-admin/admin-ajax.php?action=susu_list_times';
    if (currentFilter) {
        url += '&' + currentFilter;
    }

    console.log('---- LOADING TIMES', url, filter);
    $('.loadmore').addClass('loading');
    me.server.raw(url, filter, function (e, data) {
        $('.loadmore').removeClass('loading');
        console.log('---- GOT TIMES', data);
        if (me.storage.times.length > 0) {
            for (var i = 0; i < data.length; i++) {
                me.storage.times.push(data[i]);
            }
            me.storage.times.$resolve();
        } else {
            me.storage.times.$resolve(data);
        }
    });
}

me.render = function (page) {
    onEditorCancel();
    if (!page) page = currentPage;
    me.storage.times.$wait(function (times) {
        editor.addClass('template');
        console.log('RENDERING PAGE', page);
        $('>.time[page="' + page + '"]', list).attr('delete', true);
        if (!times || times.length < pageLimit) {
            loadmore.hide();
        } else {
            loadmore.show();
        }
        if (!times) return;
        me.async.each(times, function (time, cb) {
            var line = getLineForTime(time);
            $(line.el).attr('page', page);
            $(line.el).removeAttr('delete');

            updateLineFromTime(line, time);

            cb();
        }, function timesRendered(err) {
            $('.time[delete="true"]', list).remove();
        });
    })
}

var getLineForTime = function (time) {
    var line = renderHash[time.id];
    if (!line) {
        line = {
            el: lineTpl.clone().removeClass('template'),
            page: page
        }
        renderHash[time.id] = line;
        $(line.el).attr('data-id', time.id);
        $(list).append(line.el);
        $(line.el).on('open', function () {
            onTimeOpen(line, $(line.el).data('time'));
        });
        $(line.el).on('close', onEditorCancel);
    }
    return line;
}

var updateLineFromTime = function (line, time) {
    $('.title', line.el).text(time.description);
    var dateStart = new Date(time.start.replace(/-/g, '/'));
    $('.date', line.el).text(dateStart.toString('MM/dd/yy'));
    line.el.addClass(time.type);

    if (time.tags) {
        $('.tags', line.el).html('');
        for (var j = 0; j < time.tags.length; j++) {
            var tag = $('<div class="tag noopen"><i>' + time.tags[j].title + '</i></div>');
            $(tag).data('tag', time.tags[j]);
            $('.tags', line.el).append(tag);
        }
    } else {
        time.tags = [];
    }

    if (time.end == null) {
        line.el.addClass('no-duration');
    } else {
        line.el.removeClass('no-duration');
    }
    $(line.el).data('time', time);
}

$(c).on('resume', function () {
    addForm = $('.add-form');

    me.render();
    $(editor).on('complete', onEditorComplete);
    $(editor).on('cancel', onEditorCancel);
    $(editor).on('delete', onTimeDeleted);
    $(addForm).on('complete', onAddComplete);
    $(addForm).on('cancel', onAddCancel);
    $('.filter').on('change', onFilterChanged);
    $(loadmore).on('click', onLoadMoreClick);
    $(list).on('click', '.time .time-head .tag', onTagClick);
});

var renderAdder = function () {
    return;
    console.log('Rendering adder');
    var line = {
        el: lineTpl.clone().removeClass('template'),
        time: {}
    }
    $('.title', line.el).text('Add record');
    line.el.addClass('add');
    list.append(line.el);
    $(line.el).on('open', function () {
        onTimeOpen(line);
    });
}

var onTimeOpen = function (line, time) {
    console.log('time open', time);
    $(line.el).append(editor);
    $(editor).removeClass('template').addClass('hidden');

    var process = editor.process();
    if (time) {
        $(editor).process().setTask(time);
    } else {
        $(editor).process().clear();
    }
}

var onEditorComplete = function (e, time) {
    onEditorCancel();
    var line = getLineForTime(time);
    updateLineFromTime(line, time);
//    $('.time-filter').process().updateTags();
}

var recountPages = function() {
    $('> .time', list).not('.add, .template').each(function(i, el) {
        var page = i % pageLimit;
        $(el).attr('page', page);
    });
}

var getAllLines = function() {
    return $('> .time', list).not('.add, .template');
}

var onEditorCancel = function () {
    $(editor).parent('.open').removeClass('open');
    $(c).append(editor);
    $(editor).addClass('template');
    console.log('EDITOR CANCELLED');
}

var onTimeDeleted = function (event, time) {
    onEditorCancel();
    var line = renderHash[time.id];
    delete renderHash[time.id];

    $(line.el).remove();
}

var onFilterChanged = function (e, filter) {
    getAllLines().attr('delete', 'true');
    if (typeof filter != 'undefined') {
        onEditorCancel();
        currentPage = 0;
        console.log('GOT NEW FILTER', filter);
        currentFilter = filter;
        me.reset();
        me.reload();
        me.render();
    }
}

var onLoadMoreClick = function () {
    currentPage++;
    me.reload(getAllLines().length, pageLimit);
    me.render();
}

me.reset = function() {
    renderHash = {};
    getAllLines().remove();
}


var onTagClick = function(e) {
    var elem = $(e.target);
    if (!elem.is('.tag')) elem = elem.parents('.tag');
    console.log('elem', elem);
    var filterProcess = $('.time-filter').process();
    var tag = $(elem).data('tag');
    if (!tag) return;
    filterProcess.clear();
    filterProcess.selectTag(tag.id);
    e.stopPropagation();
    e.preventDefault();
}


var onAddComplete = function(e, time) {
    console.log('Add complete:', e, time);
    onAddCancel();
    var line = getLineForTime(time);
    updateLineFromTime(line, time);
    $(list).prepend(line.el);
    $(line.el).attr('page', 0);
    me.storage.times.unshift(time);

//    $('.time-filter').process().updateTags();
}

var onAddCancel = function() {
    $(addForm).parent('.open').removeClass('open');
    console.log('on add cancel', addForm, $(addForm).parent());
}