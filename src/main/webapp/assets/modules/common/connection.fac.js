xtuple.factory("connectionFactory", function ($rootScope) {
  var stompUrl = baseUrl + '/ws';
  var broadcastClient;
  //var subscription;

  var $$ = {
    connect: function () {
      broadcastClient = Stomp.over(new SockJS(stompUrl));
      broadcastClient.debug = function () {};
      broadcastClient.connect({}, function (frame) {
      }, function (error) {
      });
    }
  }
  //getUserInfoByKey: "/app/user/findByKey",
  //  subscribeUserInfo: "/user/queue/info"
  //var subscription = broadcastClient.subscribe(socketUri.subscribeUserInfo, function (response) {
  //  var userInfo = JSON.parse(response.body);
  //  $rootScope.userInfo = userInfo;
  //  utils.sendNotification(jsonValue.notifications.userInfo, userInfo);
  //  subscription.unsubscribe();
  //});
  //
  //broadcastClient.send(socketUri.getUserInfoByKey, {},
  //  JSON.stringify({key: localStorageService.get(jsonValue.storage.key)}));
  var instance = {
    registerCompany: function (companyInfo) {
      //'/user/' + userName + '/reply
      var subscription = broadcastClient.subscribe("/topic/" + companyInfo.admin +"/company/register", function (response) {
        //var userInfo = JSON.parse(response.body);
        //$rootScope.userInfo = userInfo;
        //utils.sendNotification(jsonValue.notifications.userInfo, userInfo);
        //subscription.unsubscribe();
        var html = ansi_up.ansi_to_html(response.body) + "<br/>";
        $(".console").append(html);
      });

      broadcastClient.send("/app/user/company/register", {}, JSON.stringify(companyInfo));
    },

    reconnect: function () {
      instance.isConnected() && broadcastClient.disconnect();
      $$.connect();
    },

    isConnected: function () {
      return broadcastClient !== undefined && broadcastClient.connected;
    },

    initialize: function () {}
  }
  return instance;
});
