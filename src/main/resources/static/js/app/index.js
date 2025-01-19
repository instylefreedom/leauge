 var main = {
    init : function(){
        var _this=this;
        // $('#btn-save').on('click',function(){
        //     _this.save();
        // });
        $('#btn-update').on('click',function(){
            _this.update();
        });
        $('#btn-delete').on('click',function(){
            _this.delete();
        });
        $('#btn-log-save').on('click',function(){
            _this.logSave();
        });
        $('#btn-log-delete').on('click',function(){
            _this.logDelete();
        });
    },
     // save : function(){
     //    var data = {
     //        userName : $('#userName').val(),
     //        tier : $('#tier').val(),
     //        mainLane : $('#mainLane').val(),
     //        subLane : $('#subLane').val(),
     //        userEmail : $('#userEmail').val(),
     //    };
     //
     //    $.ajax({
     //        type:'POST',
     //        url:'/api/user/save/data',
     //        dataType:'text',
     //        contentType:'application/json; charset=utf-8',
     //        data: JSON.stringify(data)
     //    }).done(function(data){
     //        console.log(data);
     //        alert(data);
     //        window.location.href='/';
     //    }).fail(function(error) {
     //        alert(JSON.stringify(error));
     //    });
     // },

     update : function() {
         var data = {
             userName : $('#userName').val(),
             tier : $('#tier').val(),
             mainLane : $('#mainLane').val(),
             subLane : $('#subLane').val()
         };

         var id = $('#id').val();

         $.ajax({
             type: 'PUT',
             url: '/api/user/update/'+id,
             dataType:'text',
             contentType:'application/json; charset=utf-8',
             data: JSON.stringify(data)
         }).done(function(data){
             alert(data);
             window.location.href='/';
         }).fail(function(error) {
             alert(JSON.stringify(error));
         });
     },

     delete : function() {
         var id = $('#id').val();

         $.ajax({
             type: 'DELETE',
             url: '/api/user/delete/' + id,
             dataType: 'json',
             contentType: 'application/json; charset=utf-8',
         }).done(function () {
             alert('유저 정보가 삭제 되었습니다.');
             window.location.href = '/';
         }).fail(function (error) {
             alert(JSON.stringify(error));
         });
     },

     logSave : function(){
         var data = {
             gameDate : $('#gameDate').val(),
             season : $('#season').val(),
             round : $('#round').val(),
             // set : $('#set').val(),
             // team1 : $('#team1').val(),
             // team2 : $('#team2').val(),
             set1 : $('#set1').val(),
             set2 : $('#set2').val(),
             set3 : $('#set3').val(),
             player1 : $('#p1').val(),
             player2 : $('#p2').val(),
             player3 : $('#p3').val(),
             player4 : $('#p4').val(),
             player5 : $('#p5').val(),
             player6 : $('#p6').val(),
             player7 : $('#p7').val(),
             player8 : $('#p8').val(),
             player9 : $('#p9').val(),
             player10 : $('#p10').val()

         };

         $.ajax({
             type:'POST',
             url:'/api/log/save/data',
             dataType:'text',
             contentType:'application/json; charset=utf-8',
             data: JSON.stringify(data)
         }).done(function(data){
             // alert('내전 기록이 등록 되었습니다.');
             console.log(data);
             alert(data);
             window.location.href='/';
         }).fail(function(error) {
             alert(JSON.stringify(error));
         });
     },

     logDelete : function(){

         $.ajax({
             type:'DELETE',
             url:'/api/log/delete/latest',
             dataType:'text',
             contentType:'application/json; charset=utf-8'
         }).done(function(data){
             // alert('내전 기록이 등록 되었습니다.');
             console.log(data);
             alert(data);
             window.location.href='/game/view';
         }).fail(function(error) {
             alert(JSON.stringify(error));
         });
     }


 };

 main.init();