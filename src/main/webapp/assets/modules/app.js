var baseUrl = (function () {
  var paths = window.location.pathname.split('/');
  paths.pop();
  return window.location.protocol + '//' + window.location.host + paths.join('/');
})();

var xtuple = angular.module("Xtuple", [
  "ngResource", "ngCookies", "ngRoute", "anguFixedHeaderTable"
]);

xtuple.config(["$routeProvider", function ($routeProvider) {
  $routeProvider
    .when("/signIn", {
      templateUrl: "modules/entrance/signIn.html",
      controller: "entranceSignInController"
    })
    .when("/signOut", {
      templateUrl: "modules/entrance/signIn.html",
      controller: "entranceSignOutController"
    })
    .when("/company/registration", {
      templateUrl: "modules/company/registration.html",
      controller: "companyRegistrationController"
    })
    .when("/company/registration-konsole", {
      templateUrl: "modules/company/registration-konsole.html",
      controller: "companyKonsoleController"
    })
    .when("/entrance/setting", {
      templateUrl: "modules/entrance/setting.html",
      controller: "entranceSettingController"
    })
    .when("/company/list", {
      templateUrl: "modules/company/list.html",
      controller: "companyListController"
    })
    .otherwise({
      redirectTo: "/company/list"
    });
}]);

xtuple.run(function (connectionFactory, $rootScope, $location) {
  $(".leftMenu > li").click(function (e) {
    $(".leftMenu > li").removeClass("active");
    $(e.currentTarget).addClass("active");
  });
  connectionFactory.reconnect();

  $rootScope.$on('$routeChangeSuccess', function (event, next, current) {
    if (!/\/signIn\//i.test($location.path())) {
      if ($rootScope.userInfo === undefined) {
        $location.path("/signIn");
      }
    }
    else if ($rootScope.userInfo !== undefined) {
      $location.path("/");
    }

    //console.log(event, next, current);
    //
    //$(".leftMenu > li").removeClass("active");
    //if (!/\/company\/registration\//i.test($location.path())) {
    //  $($(".leftMenu > li")[1]).addClass("active");
    //}
    //else if (!/\/company\/list\//i.test($location.path())) {
    //  $($(".leftMenu > li")[0]).addClass("active");
    //}
    //else if (!/\/company\/registration-konsole\//i.test($location.path())) {
    //  $($(".leftMenu > li")[2]).addClass("active");
    //}
    //else if (!/\/entrance\/setting\//i.test($location.path())) {
    //  $($(".leftMenu > li")[3]).addClass("active");
    //}
  });
});

