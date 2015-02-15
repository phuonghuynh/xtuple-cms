xtuple.controller("entranceController", function($scope, entranceService, $location, $rootScope) {
  $scope.signIn = entranceService.signIn;
  //if ($rootScope.userInfo !== undefined) {
  //  $location.path("/company/list");
  //}
  $scope.cancel = function() {
    $location.path("/");
  }
});

xtuple.factory("entranceService", function($location) {
  return {
    signIn: function() {
      $location.path("/company/list");
    }
  }
});