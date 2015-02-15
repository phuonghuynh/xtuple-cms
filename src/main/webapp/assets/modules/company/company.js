xtuple.controller("companyListController", function($scope, entranceService, connectionFactory) {

});

xtuple.controller("companyRegistrationController", function($scope, entranceService, connectionFactory) {
  $scope.register = function() {
    connectionFactory.registerCompany($scope.companyInfo);
  };

  //$scope.companyInfo = undefined;
});

xtuple.factory("companyService", function($http) {
  return {
  }
});