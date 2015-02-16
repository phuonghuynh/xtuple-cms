xtuple.controller("entranceSignInController", function ($scope, $location, $rootScope, $http) {
  $scope.signIn = function () {
    $http.post('signIn', $scope.userLogin)
      .success(function (data, status, headers, config) {
        if (data.status === 200) {
          alertify.success(data.message);
          $rootScope.userInfo = data.hit;
          $location.path("/");
        }
        else {
          $rootScope.userInfo = undefined;
          alertify.error(data.message);
        }
      })
      .error(function (data, status, headers, config) {
        $rootScope.userInfo = undefined;
      });
  }
});

xtuple.controller("entranceSettingController", function ($scope, $location, $rootScope, $http) {
  $scope.update = function () {
    if ($scope.userSetting.password !== $(".confirmPassword").val()) {
      console.log("New password & Confirm password not match.");
      return;
    }
    $scope.userSetting.username = $rootScope.userInfo.username;
    $http.post('setting', $scope.userSetting)
      .success(function (data, status, headers, config) {
        if (data.status === 200) {
          $rootScope.userInfo = undefined;
          alertify.success(data.message);
          $location.path("/");
        }
        else {
          alertify.error(data.message);
        }
      })
      .error(function (data, status, headers, config) {
        $rootScope.userInfo = undefined;
      });
  }
});

xtuple.controller("entranceSignOutController", function ($scope, $location, $rootScope, $http) {
  $rootScope.userInfo = undefined;
  $location.path("/");
});
