
me.storage = 'storage';
me.server = 'server';

var tagList = $('#taglist');

$(c).on('create', function () {

})

$(c).on('resume', function () {
    console.log('filter is waiting for STORAGE');
    $(c).on('open', function () {
        $('.controls input').focus();
    })

    $('.hidden').on('change', filterChanged);
    $('#description_filter, .hidden > input').on('keyup', function(e) {
        if (e.keyCode == 13) {
            filterChanged();
            $(c).removeClass('open');
        }
    })
    me.storage.tags.$on(tagsReceiver);
})

$(c).on('pause', function () {
    tagsReceiver.$off();
})

var filterTimeout;
var filterChanged = function() {
    if (filterTimeout) clearTimeout(filterTimeout);
    filterTimeout = setTimeout(function() {
        console.log('FILTER CHANGE');
        $(c).triggerHandler('change', me.getFilter());
    }, 300);
}


me.getFilter = function () {
    var filter = [];
    var selectedTypes = $('.types .selected');
    for (var i=0; i<selectedTypes.length; i++) {
        filter.push('type[]='+$(selectedTypes[i]).html());
    }

    var selectedTags = $('.selected', tagList);
    for (var i=0; i<selectedTags.length; i++) {
        filter.push('tags:like=['+$(selectedTags[i]).attr('data-id')+']');
    }

    var min_duration = parseInt($('#duration_min').val());
    if (!isNaN(min_duration)) {
        filter.push('duration:gte='+(min_duration*60));
    }

    var max_duration = parseInt($('#duration_maxs').val());
    if (!isNaN(max_duration)) {
        filter.push('duration:lte='+(max_duration*60));
    }

    var description_filter = $('#description_filter').val();
    if (description_filter) {
        filter.push('description:like='+description_filter);
    }
    return filter.join('&');
}

me.clear = function () {
    $('.selected').removeClass('selected');
    $('imput').val('');
}

me.selectTag = function (tagId) {
    $('a[data-id="'+tagId+'"]', tagList).addClass('selected');
}

me.updateTags = function () {
    me.storage.tags.$refresh();
}

var tagsReceiver = function (tags) {
    $(tagList).html('');
    console.log('Filter Got tags', tags, tagList);
    for(var i=0; i<tags.length; i++) {
        console.log(tagList.append);
        $(tagList).append('<a href="javascript:void(0)" data-id="'+tags[i].id+'">'+tags[i].title+'</a>');
    }
    console.log('filter rendered');
}