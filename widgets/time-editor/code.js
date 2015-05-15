/**
 * Created by chedim on 12/21/14.
 */

me.datejs = 'datejs';
me.server = 'server';
me.storage = 'storage';
me.router = 'router';

var task = {};
var typesIndex = ['A', 'B', 'C', 'D', 'E'];

var types = [
    {
        title: 'A',
        id: 'A',
        type: 'type'
    },
    {
        title: 'B',
        id: 'B',
        type: 'type'
    },
    {
        title: 'C',
        id: 'C',
        type: 'type'
    },
    {
        title: 'D',
        id: 'D',
        type: 'type'
    },
    {
        title: 'E',
        id: 'E',
        type: 'type'
    },
];

$(c).on('resume', function () {
    $('form').on('submit', onFormSubmit);
    console.log('waiting for tags', me.storage.tags);
    me.storage.tags.$on(tagsReceiver);
    $('.cancel').click(function () {
        $(c).triggerHandler('cancel');
    })
    $('.del-holder').hide();
    $(c).on('click', '.del-holder.open .delete', onDeleteClick);
});

$(c).on('pause', function () {
    tagsReceiver.$off();
})


var onFormSubmit = function (e) {
    e.preventDefault();
    task.description = $('.description').val();
    task.type = 'A';
    task.tags = [];
    var tags = $('.tagbox').process().getSelected();
    var createdTags = 0;
    for (var i = 0; i < tags.length; i++) {
        if (tags[i].type == 'type') {
            task.type = tags[i].id;
        } else {
            if (tags[i].created) createdTags++;
            task.tags.push(tags[i].id);
        }
    }

    var dateStart = $('.date').data('value');
    var duration = $('.duration').data('value');
    console.log('---DURATION:', duration);
    if (duration.interval < 0) {
        if (!confirm("Task duration is negative. If you will decide to continue, this task will be saved as uncompleted. Continue?"))
            return;
        duration.end = null;
        $('.duration').process().setTimeEnd(null);
    }

    dateStart.setHours(duration.start.getHours());
    dateStart.setMinutes(duration.start.getMinutes());
    task.start = dateStart.toString('yyyy-MM-dd HH:mm');
    console.log('SAVING TASK DURATION:', duration);
    if (duration.end !== null) {
        task.duration = Math.round(duration.interval / 1000);
        var dateEnd = dateStart.clone().addMilliseconds(duration.interval);
        task.end = dateEnd.toString('yyyy-MM-dd HH:mm');
    } else {
        task.duration = null;
        task.end = null;
    }

    var action = 'susu_add_time';
    if (task.id) {
        action = 'susu_update_time';
    }

    $(c).addClass('request');
    $.ajax('/wp-admin/admin-ajax.php?action=' + action, {
        method: 'post',
        data: task,
        complete: function () {
            $(c).removeClass('request');
            me.storage.$promise('tags');
            if (createdTags) me.storage.tags.$refresh();
        },
        success: function (data) {
            data = JSON.parse(data);
            console.log('+++ SAVED TASK:', data);
            $(c).triggerHandler('complete', data, action);
        },
        error: function (e) {
            console.log('Error:', arguments);
            alert('Error: unable to save time entry.');
        }
    });
}

var updateSelectedTags = function () {
    var tagbox = $('.tagbox').process();
    var tags = tagbox.options.data;
    tagbox.clear();
    tagbox.select(typesIndex.indexOf(task.type));
    for (var i = 0; i < task.tags.length; i++) {
        for (var j = types.length; j < tags.length; j++) {
            if (tags[j].id == task.tags[i].id) {
                tagbox.select(j);
                break;
            }
        }
    }
}

me.setTask = function (t) {
    task = t;
    $('.del-holder').show();
    $('.description').val(t.description);

    updateSelectedTags();

    $('.date').process().setDate(new Date(task.start.replace(/-/g, '/')));
    var duration = $('.duration').process();
    duration.setTimeStart(new Date(task.start.replace(/-/g, '/')));
    if (task.end != null) {
        duration.setTimeEnd(new Date(task.end.replace(/-/g, '/')));
    } else {
        duration.setTimeEnd(null);
    }
}

me.clear = function () {
    task = {};
    $('.del-holder').hide();
    $('.description').val('');

    var tagbox = $('.tagbox').process();
    tagbox.clear();

    $('.date').process().setDate(new Date());
    var duration = $('.duration').process();
    duration.setTimeStart(new Date());
    duration.setTimeEnd(null);
}

var onDeleteClick = function () {
    $(c).addClass('request');
    $.ajax('/wp-admin/admin-ajax.php?action=susu_delete_time', {
        method: 'post',
        data: { id: task.id },
        complete: function () {
            $(c).removeClass('request');
        },
        success: function () {
            console.log('Deletion success:', task);
            $(c).triggerHandler('delete', task);
        },
        error: function () {
            alert('Unable to delete this task due to the server error');
        }
    });
}

var tagsReceiver = function (tags) {
    console.log('GOT TAGS', tags);
    var data = types.slice(0, types.length);
    for (var i = 0; i < tags.length; i++) {
        tags[i].type = 'tag';
        data.push(tags[i]);
    }
    $('.tagbox').process().setOptions({
        limit: false,
        placeholder: 'Search tags',
        data: data,
        distinct: {
            type: 'type'
        },
        autoCreate: true,
        onCreate: function (title, items) {
            return {
                title: title,
                id: title
            };
        },
        createSuggest: function(input, sel) {
            if (input.length < 3 && sel > 0 || sel > 5) return false;
            return 'Create tag: '+input;
        }
    });
    if (task && task.tags) {
        updateSelectedTags();
    }
};


