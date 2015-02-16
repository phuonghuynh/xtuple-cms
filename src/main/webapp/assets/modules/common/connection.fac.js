xtuple.factory("connectionFactory", function ($rootScope) {
  var stompUrl = baseUrl + '/ws';
  var broadcastClient;
  var registerCompanySub;

  var $$ = {
    clean: function() {
      registerCompanySub !== undefined && registerCompanySub.unsubscribe();
    },

    connect: function () {
      broadcastClient = Stomp.over(new SockJS(stompUrl));
      broadcastClient.debug = function () {};
      broadcastClient.connect({}, function (frame) {
      }, function (error) {
      });
    }
  }

  var instance = {
    registerCompany: function (companyInfo) {
      registerCompanySub !== undefined && registerCompanySub.unsubscribe();
      registerCompanySub = broadcastClient.subscribe("/topic/" + companyInfo.installName +"/company/register", function (response) {
        var html = ansi_up.ansi_to_html(response.body) + "<br/>";
        $(".console").append(html);
      });

      $(".console").html('');
      broadcastClient.send("/app/user/company/register", {}, JSON.stringify(companyInfo));
    },

    reconnect: function () {
      instance.isConnected() && broadcastClient.disconnect() && $$.clean();
      $$.connect();
    },

    isConnected: function () {
      return broadcastClient !== undefined && broadcastClient.connected;
    },

    initialize: function () {}
  }
  return instance;
});
