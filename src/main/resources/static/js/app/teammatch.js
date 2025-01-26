var main = {
    init : function(){
        var _this=this;
        $('#btn-match').on('click',function(){
            _this.match();
        });
        // $('#board-save').on('click',function(){
        //     _this.board_save();
        // });
        // $('#board-update').on('click',function(){
        //     _this.board_update();
        // });
        // $('#board-temp').on('click',function(){
        //     _this.board_temp();
        // });
        // $('#board-delete').on('click',function(){
        //     _this.board_delete();
        // });
    },

    match : function() {

        let data = {
            users : [$('#p1').val(),
                $('#p2').val(),
                $('#p3').val(),
                $('#p4').val(),
                $('#p5').val(),
                $('#p6').val(),
                $('#p7').val(),
                $('#p8').val(),
                $('#p9').val(),
                $('#p10').val()]
        };

        $.ajax({
            type: 'POST',
            url: '/api/team/match',
            dataType:'text',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function(data){
            alert("매치성공")
            window.location.href='/match/result';
        }).error(function(request, status, error) {
            console.log("code: " + request.status)
            console.log("message: " + request.responseText)
            console.log("error: " + error)
            console.log("code2: " + JSON.parse(request.responseText))
            alert(JSON.stringify(JSON.parse(request.responseText).exception));
        });
    },
    board_save : function() {
        // oEditors.getById["scontent"].exec("UPDATE_CONTENTS_FIELD",[])
        // let content = document.getElementById("scontent").value
        // if(content===''){
        //     alert("내용을 입력해 주세요")
        //     oEditors.getById["scontent"].exec("FOCUS")
        //     return
        // }else {
        //     console.log(content)
        // }
        // console.log(quill.getContents())
        // console.log(quill.root.innerHTML.trim())

        // console.log(quill.Text())
        let data = {
            title : $('#title').val(),
            type : $('#type').val(),
            content : quill.root.innerHTML.trim()
        };

        $.ajax({
            type: 'POST',
            url: '/api/board/create',
            dataType:'text',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function(data){

        }).fail(function(error) {
            alert(JSON.stringify(error));
        });
    },

    board_update : function() {
        // oEditors.getById["scontent"].exec("UPDATE_CONTENTS_FIELD",[])
        // let content = document.getElementById("scontent").value
        // if(content===''){
        //     alert("내용을 입력해 주세요")
        //     oEditors.getById["scontent"].exec("FOCUS")
        //     return
        // }else {
        //     console.log(content)
        // }
        let data = {
            title : $('#title').val(),
            boardId : $('#boardId').val(),
            content : quill.root.innerHTML.trim()
        };

        $.ajax({
            type: 'PATCH',
            url: '/api/board/update',
            dataType:'text',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function(data){
            alert(data);
            window.location.href='/board/admin';
        }).fail(function(error) {
            alert(JSON.stringify(error));
        });
    },

    board_temp : function() {
        // oEditors.getById["scontent"].exec("UPDATE_CONTENTS_FIELD",[])
        // let content = document.getElementById("scontent").value
        // if(content===''){
        //     alert("내용을 입력해 주세요")
        //     oEditors.getById["scontent"].exec("FOCUS")
        //     return
        // }else {
        //     console.log(content)
        // }
        let data = {
            title : $('#title').val(),
            type : $('#type').val(),
            content : quill.root.innerHTML.trim()
        };

        $.ajax({
            type: 'POST',
            url: '/api/board/temp',
            dataType:'text',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function(data){
            alert(data);
            window.location.href='/board/admin';
        }).fail(function(error) {
            alert(JSON.stringify(error));
        });
    },


};

main.init();