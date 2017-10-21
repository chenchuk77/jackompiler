/**
 * Created by chenchuk on 10/19/17.
 *
 * javascript XHR Client for the Hack Assembler REST service.
 *
 * Each key pressed will POST the *.asm code written in the Hack machine language
 * to the server. The server will reply with JSON of the binary converted code.
 *
 */

$(document).ready(function() {
    $('#asmText').keyup(function(){
        $.ajax({
            url : 'http://www.devopskb.net:8090/assemble',
            type: "POST",
            dataType: "json",
            data : {
                source :     $('#asmText').val(),
                location : 'test location'
            },
            success : function(data) {
                //$('#hackText').val(data.source);
                $('#hackText').val(data.output);
            },
            error: function (jqXHR, exception) {
                var msg = '';
                if (jqXHR.status === 0) {
                    msg = 'Not connect.\n Verify Network.';
                } else if (jqXHR.status == 404) {
                    msg = 'Requested page not found. [404]';
                } else if (jqXHR.status == 500) {
                    msg = 'Internal Server Error [500].';
                } else if (exception === 'parsererror') {
                    msg = 'Requested JSON parse failed.';
                } else if (exception === 'timeout') {
                    msg = 'Time out error.';
                } else if (exception === 'abort') {
                    msg = 'Ajax request aborted.';
                } else {
                    msg = 'Uncaught Error.\n' + jqXHR.responseText;
                }
                $('#post').html(msg);
            }

        })
    });
    $("#exampleAdd").click(function(){
        window.open('asm-examples/Add.asm');
    });
    $("#exampleMult").click(function(){
        window.open('asm-examples/Mult.asm');
    });
    $("#exampleRect").click(function(){
        window.open('asm-examples/Rect.asm');
    });
});
