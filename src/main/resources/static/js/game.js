$(function () {
  loadData();
});

function getParameterByName(name) {
  var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
  return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}

function loadData() {
  $.get('/api/game_view/' + getParameterByName('gp'))
    .done(function (data) {
      var playerInfo;
      if (data.gamePlayer[0].id == getParameterByName('gp'))
        playerInfo = [data.gamePlayer[0].player, data.gamePlayer[1].player];
      else
        playerInfo = [data.gamePlayer[1].player, data.gamePlayer[0].player];

      $('#playerInfo').text(playerInfo[0].username + '(you) vs ' + playerInfo[1].username);

      data.ships.forEach(function (shipPiece) {
        shipPiece.location.forEach(function (shipLocation) {
          if(isHit(shipLocation,data.salvoes,playerInfo[0].id)  !=  0){
            $('#B_' + shipLocation).addClass('ship-piece-hited');
            $('#B_' + shipLocation).text(isHit(shipLocation,data.salvoes,playerInfo[0].id));
          }

          else
            $('#B_' + shipLocation).addClass('ship-piece');
        });
      });
      data.salvoes.forEach(function (salvo) {
        if (playerInfo[0].id === salvo.player) {
          salvo.location.forEach(function (location) {
            $('#S_' + location).addClass('salvo-piece');
            $('#S_' + location).text(salvo.turn);

          });
        } else {
          salvo.location.forEach(function (location) {
            $('#B_' + location).addClass('salvo');
          });
        }
      });
    })
    .fail(function (jqXHR, textStatus) {
      alert('Failed: ' + textStatus);
    });
}

function isHit(shipLocation,salvoes,playerId) {
  var turn = 0;
  salvoes.forEach(function (salvo) {
    if(salvo.player != playerId)
      salvo.location.forEach(function (location) {
        if(shipLocation === location)
          turn = salvo.turn;
      });
  });
  return turn;
}