xtuple.controller("companyKonsoleController", function($scope, $location) {
  //$scope.close = function() {
  //  $location.path("/company/list");
  //}
});

xtuple.controller("companyRegistrationController", function($scope, connectionFactory, $location) {
  console.log("konsole");
  $scope.register = function() {
    if ($scope.companyInfo.adminPassword !== $(".adminConfirmPassword").val()) {
      alertify.error("Admin password & Confirm password not match.");
      return;
    }
    connectionFactory.registerCompany($scope.companyInfo);
    $location.path("/company/registration-konsole");
  };
});
xtuple.controller("companyListController", function($scope, $http, $location) {
  $http.get('company')
    .success(function (data, status, headers, config) {
      $.each(data, function(i, d){d.sysReport = ansi_up.ansi_to_html(d.sysReport);});
      $scope.companies = data;
    })
    .error(function (data, status, headers, config) {
      alertify.error("Could not get companies.");
    });
});