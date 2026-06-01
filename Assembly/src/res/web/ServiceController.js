$(document).ready(function() {
    $("#formTester").submit(function(e) {
        e.preventDefault();
        $.ajax({
            url: this.action,
            type: 'post',
            data: $(this).serialize(),
            beforeSend: function() {},
            success: function(data) {
                var response = $.parseJSON(data);
                alert("[RESPONSE]" + data);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                var message = '';
                if (jqXHR.status === 0) {
                    message = 'Not connect.\n Verify Network.';
                } else if (jqXHR.status == 404) {
                    message = 'Requested page not found. [404]';
                } else if (jqXHR.status == 500) {
                    message = 'Internal Server Error [500].';
                } else if (exception === 'parsererror') {
                    message = 'Requested JSON parse failed.';
                } else if (exception === 'timeout') {
                    message = 'Time out error.';
                } else if (exception === 'abort') {
                    message = 'Ajax request aborted.';
                } else {
                    message = 'Uncaught Error.\n' + jqXHR.responseText;
                }
                alert(message);
            }
        });
    });
});