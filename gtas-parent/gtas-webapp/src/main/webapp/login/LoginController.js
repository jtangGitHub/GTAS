
(function () {
    'use strict';
    app.controller('LoginController',
        function($state, $scope, $rootScope, $q, $stateParams, userService, $mdToast, AuthService,
                 Session, sessionFactory, APP_CONSTANTS, $sessionStorage, $location, $interval, $window,$translate) {
            //Set locale here to change language setting for web site
			$scope.locale = "en";
            $scope.currentUser = {};
            $scope.credentials = {
                j_username: '',
                j_password: ''
            };

            $scope.login = function (credentials) {
                
                AuthService.login(credentials).then(function (user){
                    if($rootScope.authenticated){
                        AuthService.getCurrentUser().then(function (user){
                            $scope.currentUser.data = user;
                        });
                    }else {
                        if(user.status == 401){
                        }
                    }
                });
            };

            $scope.$watch('currentUser.data', function (user) {
                
                if (angular.isDefined(user)) {
                    console.log("$scope.currentUser has data");
                    Session.create(user.firstName, user.userId,
                        user.roles);
                    $sessionStorage.put(APP_CONSTANTS.CURRENT_USER, user);
                    
        			$translate.use($scope.locale);
        			$location.search('lang', $scope.locale);
                    //window.location.href = APP_CONSTANTS.HOME_PAGE;
                    $window.location.href = APP_CONSTANTS.MAIN_PAGE;

                    //$window.location.href = '#/dashboard';
                    //$state.go('dashboard', $stateParams, {
                    //    reload: true,
                    //    inherit: false,
                    //    notify: true
                    //});

                    // $state.go('home');
                    //
                    //$interval(function () {
                    //    $state.go('dashboard', $stateParams, {
                    //        reload: true,
                    //        inherit: false,
                    //        notify: true
                    //    });
                    //}, 3000, true);


                }
            });

        });

    }());

