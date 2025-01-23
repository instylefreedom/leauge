 $(document).ready(function () {
        var t = $('#userTable').DataTable({
            paging: false,
            serverSide: false,
            processing: true,
            destroy: true,
            "ajax": {
                "url": "/api/user/get/dashboard/4",
                "type": "post",
                "dataSrc": "data"
                //     function(res){
                //     var data = res.data;
                //     return data;
                // }
            }
            , "columns": [
                {"data": "userId"},
                // {},
                {"data": "userName"},
                {"data": "tier"},
                {"data": "rating"},
                {"data": "mainLane"},
                {"data": "subLane"},
                {"data": "matchNumber"},
                {"data": "win"},
                {"data": "loss"},
                {"data": "winRate"},
                {"data": "winPoint"},
                // {"data": "cwin"},
                // {"data": "close"},
            ]

            // , "columnDefs" : [{
            //     "targets": [ 0 ],
            //     "visible": false,
            //     "searchable": true
            // }]

            , "order" : [[1, 'asc']]

            // , "rowCallback" : function(row, data, index){
            //     if(data[2]=='다이아3'){
            //         $(row).find('td:eq(1)').css('background-color', 'silver');
            //     }
            // }

            , "createdRow" : (row, data, index) => {
                if(data.tier != null) {
                    if (data.tier === ("그랜드마스터")) {
                        row.querySelector(':nth-child(3)').classList.add('grandmaster');
                    }
                    if (data.tier === ("마스터")) {
                        row.querySelector(':nth-child(3)').classList.add('master');
                    }
                    if (data.tier.slice(0, -1) === ("다이아몬드")) {
                        row.querySelector(':nth-child(3)').classList.add('diamond');
                    }
                    if (data.tier.slice(0, -1) === ("에메랄드")) {
                        row.querySelector(':nth-child(3)').classList.add('emerald');
                    }
                    if (data.tier.slice(0, -1) === ("플레티넘")) {
                        row.querySelector(':nth-child(3)').classList.add('platinum');
                    }
                    if (data.tier.slice(0, -1) === ("골드")) {
                        row.querySelector(':nth-child(3)').classList.add('gold');
                    }
                    if (data.tier.slice(0, -1) === ("실버")) {
                        row.querySelector(':nth-child(3)').classList.add('silver');
                    }
                    if (data.tier.slice(0, -1) === ("브론즈")) {
                        row.querySelector(':nth-child(3)').classList.add('bronze');
                    }
                    if (data.tier.slice(0, -1) === ("아이언")) {
                        row.querySelector(':nth-child(3)').classList.add('iron');
                    }
                }
            }


        });
     t.on( 'draw.dt', function () {
         var PageInfo = $('#userTable').DataTable().page.info();
         t.column(0, { page: 'current' }).nodes().each( function (cell, i) {
             cell.innerHTML = i + 1 + PageInfo.start;
         } );
     } );


     // tr 클릭시 링크 걸기
     $(document).on('click', '#userTable > tbody > tr' , function(e){
         const target = $(e.currentTarget);
         const userName = target.children().eq(1).text();
         location.href = "/user/admin/update/" + userName;
     });
});

 var main = {
     init : function() {
         var _this = this;

         $('#btn-dashboard').on('click', function () {
             _this.btn_dashboard();
         });
     },
     btn_dashboard : function() {
         var season = $('#season').val();
         var t = $('#userTable').DataTable({
             paging: false,
             serverSide: false,
             processing: true,
             destroy: true,
             "ajax": {
                 "url": "/api/user/get/dashboard/"+season,
                 "type": "post",
                 "dataSrc": "data"
             }
             , "columns": [
                 {"data": "userId"},
                 // {},
                 {"data": "userName"},
                 {"data": "tier"},
                 {"data": "rating"},
                 {"data": "mainLane"},
                 {"data": "subLane"},
                 {"data": "matchNumber"},
                 {"data": "win"},
                 {"data": "loss"},
                 {"data": "winRate"},
                 {"data": "winPoint"},
                 // {"data": "cwin"},
                 // {"data": "close"},
             ]
             , "order" : [[1, 'asc']]

             , "createdRow" : (row, data, index) => {
                 if(data.tier != null) {
                     if (data.tier === ("그랜드마스터")) {
                         row.querySelector(':nth-child(3)').classList.add('grandmaster');
                     }
                     if (data.tier === ("마스터")) {
                         row.querySelector(':nth-child(3)').classList.add('master');
                     }
                     if (data.tier.slice(0, -1) === ("다이아몬드")) {
                         row.querySelector(':nth-child(3)').classList.add('diamond');
                     }
                     if (data.tier.slice(0, -1) === ("에메랄드")) {
                         row.querySelector(':nth-child(3)').classList.add('emerald');
                     }
                     if (data.tier.slice(0, -1) === ("플레티넘")) {
                         row.querySelector(':nth-child(3)').classList.add('platinum');
                     }
                     if (data.tier.slice(0, -1) === ("골드")) {
                         row.querySelector(':nth-child(3)').classList.add('gold');
                     }
                     if (data.tier.slice(0, -1) === ("실버")) {
                         row.querySelector(':nth-child(3)').classList.add('silver');
                     }
                     if (data.tier.slice(0, -1) === ("브론즈")) {
                         row.querySelector(':nth-child(3)').classList.add('bronze');
                     }
                     if (data.tier.slice(0, -1) === ("아이언")) {
                         row.querySelector(':nth-child(3)').classList.add('iron');
                     }
                 }
             }


         });
         t.on( 'draw.dt', function () {
             var PageInfo = $('#userTable').DataTable().page.info();
             t.column(0, { page: 'current' }).nodes().each( function (cell, i) {
                 cell.innerHTML = i + 1 + PageInfo.start;
             } );
         } );


         // tr 클릭시 링크 걸기
         $(document).on('click', '#userTable > tbody > tr' , function(e){
             const target = $(e.currentTarget);
             const userName = target.children().eq(1).text();
             location.href = "/user/admin/update/" + userName;
         });
    }

 }
 main.init();