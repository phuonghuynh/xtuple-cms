xtuple.controller("companyKonsoleController", function($scope, $location) {
  //$scope.close = function() {
  //  $location.path("/company/list");
  //}
});

xtuple.controller("companyRegistrationController", function($scope, connectionFactory, $location) {
  console.log("konsole");
  $scope.register = function() {
    connectionFactory.registerCompany($scope.companyInfo);
    $location.path("/company/registration-konsole");
  };
});
xtuple.controller("companyListController", function($scope, connectionFactory, $location) {

});