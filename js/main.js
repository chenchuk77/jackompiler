/**
 * Created by chenchuk on 10/19/17.
 */
<html>
<head>
<script type="text/javascript" src="jquery-1.6.2.min.js"></script>
    </head>

    <body>

    <button id="ajax">ajax call</button>
<button id="json">json</button>

    <script type="text/javascript">
    $('#json').click(function(){
        alert('json');
        $.getJSON("http://localhost:8080/restws/json/product/get",
            function(data) {
                alert(data);
            });
    });

$('#ajax').click(function(){
    alert('ajax');
    $.ajax({
        type: "GET",
        dataType: "json",
        url: "http://localhost:8080/restws/json/product/get",
        success: function(data){
            alert(data);
        }
    });
});

</script>



</body>

</html>