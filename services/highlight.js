
/**
 * Highlight.js wrapper
 **/

var queue = [];

var src = window['J'].soure + 'lib/highlight.js';


$(me).on('create', function() {
    Jendri.ajax(src, {
        dataType: 'text',
        success: function(data) {
            eval(data);
            for(var i=0; i<queue; i++) {
                hljs.highlightBlock($(queue[i]));
            }
            Jendri.register('highlight', hljs);
            delete window.hljs;
        }
    });
})
