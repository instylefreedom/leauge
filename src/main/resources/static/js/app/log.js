let Page = function () {


        const inputInit = () => {
            $.ajax({
                url: '/api/user/get/name',
                // headers: { 'Authorization':ACCESS_TOKEN},
                type: 'GET',
                dataType: 'json',
                processData: false,
                contentType: 'application/json',
                success: function (response, state, xhr, $form) {

                    // if(response.result_code === 200){
                        $.each(response.data, function(i, obj) {
                            $('#player1') .append($("<option></option>") .attr("value",obj.name) .text(obj.name));
                            $('#player2') .append($("<option></option>") .attr("value",obj.name) .text(obj.name));
                            $('#player3') .append($("<option></option>") .attr("value",obj.name) .text(obj.name));
                            $('#player4') .append($("<option></option>") .attr("value",obj.name) .text(obj.name));
                            $('#player5') .append($("<option></option>") .attr("value",obj.name) .text(obj.name));
                            $('#player6') .append($("<option></option>") .attr("value",obj.name) .text(obj.name));
                            $('#player7') .append($("<option></option>") .attr("value",obj.name) .text(obj.name));
                            $('#player8') .append($("<option></option>") .attr("value",obj.name) .text(obj.name));
                            $('#player9') .append($("<option></option>") .attr("value",obj.name) .text(obj.name));
                            $('#player10') .append($("<option></option>") .attr("value",obj.name) .text(obj.name));
                            // $('#userName').append($(document.createElement('option')).prop({
                            //     value: obj.name,
                            //     text: obj.name}))
                            // $('input[name=userName]').append("<option value\""+obj.name+"\">"+obj.name+"</option>");
                        });

                },
                error: () => {
                    showToastrError('Failure : Loading a list of country', 'The server can not process the request.');
                }

            });
        }
        return {
            init: () => {
                inputInit();
            }
        };

}();

jQuery(document).ready(() => {
    Page.init();
});

$(document).read
