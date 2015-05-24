me.hljs = '$hljs';

$(c).on('resume', function() {
    var code = $('content pre').text();
    console.log('CODE SRC', code);
    code = code.replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/\n/g, '<br/>')
        .replace(/  /g, '&nbsp;&nbsp;')
        .replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;');
    console.log('CODE', code);
    $('content').html(code);
    $('content').attr('class', $(c).attr('lang'));
    me.hljs.highlightBlock($('content')[0]);
});