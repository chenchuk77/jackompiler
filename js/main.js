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
            url : 'http://localhost:8080/assemble',
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
            error:   function(jqXHR, textStatus, errorThrown) {
                console.log("Error, status = " + textStatus + ", " +
                    "error thrown: " + errorThrown
                );
            }

        })
    })
});
