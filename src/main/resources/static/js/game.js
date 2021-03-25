$(function() {
    loadData();
});

function getParameterByName(name) {
    var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
};

function loadData(){
    $.get('/api/game_view/'+getParameterByName('gp'))
        .done(function(data) {
            let playerInfo;
            if(data.gamePlayer[0].id == getParameterByName('gp'))
                playerInfo = [data.gamePlayer[0].player.username,data.gamePlayer[1].player.username];
            else
                playerInfo = [data.gamePlayer[1].player.username,data.gamePlayer[0].player.username];

            $('#playerInfo').text(playerInfo[0] + '(you) vs ' + playerInfo[1]);

            data.ships.forEach(function(shipPiece){
                shipPiece.location.forEach(function(shipLocation){
                    $('#'+shipLocation).addClass('ship-piece');
                })
            });
        })
        .fail(function( jqXHR, textStatus ) {
          alert( "Failed: " + textStatus );
        });
};