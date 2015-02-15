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
      controller: "entranceController"
    })
    .when("/company/registration", {
      templateUrl: "modules/company/registration.html",
      controller: "companyRegistrationController"
    })
    .when("/entrance/setting", {
      templateUrl: "modules/entrance/setting.html"
    })
    .when("/company/list", {
      templateUrl: "modules/company/list.html",
      controller: "companyListController"
    })
    .otherwise({
      redirectTo: "/signIn"
    });
}]);

xtuple.run(function(connectionFactory, $rootScope, $location) {
  connectionFactory.reconnect();
  $rootScope.setting = function() {
    $location.path("/entrance/setting");
  }
});

