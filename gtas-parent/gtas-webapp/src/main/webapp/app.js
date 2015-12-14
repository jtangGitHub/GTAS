var app;
(function () {
    'use strict';
    var pageDefaults = {
            pageNumber: 1,
            pageSize: 10
        },
        appDependencies = [
            'ui.router',
            'ui.grid',
            'ui.grid.resizeColumns',
            'ui.grid.moveColumns',
            'ui.grid.pagination',
            'ui.grid.autoResize',
            'ui.grid.edit',
            'ui.grid.rowEdit',
            'ui.grid.cellNav',
            'ui.grid.selection',
            'ui.grid.exporter',
            'ui.grid.expandable',
            'ngMaterial',
            'ngMessages',
            'ngAria',
            'ngAnimate',
            'angularSpinners',
            'ngFileUpload'
        ],
        localDateMomentFormat = function ($mdDateLocaleProvider) {
            // Example of a French localization.
            //$mdDateLocaleProvider.months = ['janvier', 'février', 'mars', ...];
            //$mdDateLocaleProvider.shortMonths = ['janv', 'févr', 'mars', ...];
            //$mdDateLocaleProvider.days = ['dimanche', 'lundi', 'mardi', ...];
            //$mdDateLocaleProvider.shortDays = ['Di', 'Lu', 'Ma', ...];
            // Can change week display to start on Monday.
            //$mdDateLocaleProvider.firstDayOfWeek = 1;
            // Optional.
            //$mdDateLocaleProvider.dates = [1, 2, 3, 4, 5, 6, ...];
            // Example uses moment.js to parse and format dates.
            $mdDateLocaleProvider.parseDate = function(dateString) {
                var m = moment(dateString, 'L', true);
                return m.isValid() ? m.toDate() : new Date(NaN);
            };
            $mdDateLocaleProvider.formatDate = function(date) {
                console.log('converting date');
                return moment(date).format('YYYY-MM-DD');
            };
            //$mdDateLocaleProvider.monthHeaderFormatter = function(date) {
            //    return myShortMonths[date.getMonth()] + ' ' + date.getFullYear();
            //};
            // In addition to date display, date components also need localized messages
            // for aria-labels for screen-reader users.
            //$mdDateLocaleProvider.weekNumberFormatter = function(weekNumber) {
            //    return 'Semaine ' + weekNumber;
            //};
            //$mdDateLocaleProvider.msgCalendar = 'Calendrier';
            //$mdDateLocaleProvider.msgOpenCalendar = 'Ouvrir le calendrier';
        },
        initialize = function ($rootScope) {
            $rootScope.$on('$stateChangeStart', function (e, toState, toParams) {
                $rootScope.$broadcast('stateChanged', toState, toParams);
            });
        },
        router = function ($stateProvider, $urlRouterProvider) {
            $urlRouterProvider.otherwise("/flights");
            $stateProvider
                .state('dashboard', {
                    url: '/dashboard',
                    views: {
                        '@': {
                            controller: 'DashboardController',
                            templateUrl: 'dashboard/dashboard.html'
                        }
                    }
                })
                .state('admin', {
                    url: '/admin',
                    views: {
                        "@": {
                            controller: 'AdminCtrl',
                            templateUrl: 'admin/admin.html'
                        }
                    }
                })
                .state('modifyUser', {
                    url: '/user/:userId',
                    views: {
                        '@': {
                            controller: 'UserCtrl',
                            templateUrl: 'admin/user.html'
                        }
                    }
                })
                .state('upload', {
                    url: '/upload',
                    views: {
                        '@': {
                            controller: 'UploadCtrl',
                            templateUrl: 'admin/upload.html'
                        }
                    }
                })
                .state('flights', {
                    url: '/flights',
                    views: {
                        '@': {
                            controller: 'FlightsController as flights',
                            templateUrl: 'flights/flights.html'
                        }
                    },
                    resolve: {
                        flights: function (passengersBasedOnUserFilter,flightsModel) {
                            return passengersBasedOnUserFilter.load();
                        }
                    }
                })
                .state('queryFlights', {
                    url: '/query/flights',
                    views: {
                        '@': {
                            controller: 'FlightsController',
                            templateUrl: 'flights/query-flights.html'
                        }
                    },
                    resolve: {
                        flights: function (executeQueryService) {
                            return executeQueryService.queryFlights();
                        }
                    }
                })
                .state('paxAll', {
                    url: '/passengers',
                    views: {
                        '@': {
                            controller: 'PaxController',
                            templateUrl: 'pax/pax.table.html'
                        }
                    },
                    resolve: {
                        passengers: function (paxService, paxModel) {
                            return paxService.getAllPax(paxModel.model);
                        }
                    }
                })
                .state('flightpax', {
                    url: '/flightpax/:id/:flightNumber/:origin/:destination/:direction/:eta/:etd',
                    views: {
                        '@': {
                            controller: 'PaxController',
                            templateUrl: 'pax/pax.table.html'
                        }
                    },
                    resolve: {
                        paxModel: function ($stateParams, paxModel) {
                            return {
                                model: paxModel.initial($stateParams),
                                reset: function() { this.model.lastName = ''; }
                            };
                        },
                        passengers: function (paxService, $stateParams, paxModel) {
                            //because of field/model not standard
                            $stateParams.dest = $stateParams.destination;
                            $stateParams.etaStart = $stateParams.eta;
                            $stateParams.etaEnd = $stateParams.etd;
                            return paxService.getPax($stateParams.id, paxModel.model);
                        }
                    }
                })
                .state('queryPassengers', {
                    url: '/query/passengers',
                    views: {
                        '@': {
                            controller: 'PaxController',
                            templateUrl: 'pax/pax.table.html'
                        }
                    },
                    resolve: {
                        passengers: function (executeQueryService, $stateParams) {
                            var postData, query = JSON.parse(localStorage['query']);
                            postData = {
                                pageNumber: $stateParams.pageNumber || pageDefaults.pageNumber,
                                pageSize: $stateParams.pageSize || pageDefaults.pageSize,
                                query: query
                            };
                            return executeQueryService.queryPassengers(postData);
                        }
                    }
                })
                .state('detail', {
                    url: '/paxdetail/{paxId}/{flightId}',
                    views: {
                        '@': {
                            controller: 'PassengerDetailCtrl',
                            templateUrl: 'pax/pax.detail.html',
                        }
                    },
                    resolve: {
                        passenger: function (paxDetailService, $stateParams) {
                            return paxDetailService.getPaxDetail($stateParams.paxId, $stateParams.flightId);
                        }
                    }
                })
                .state('build', {
                    url: '/build/:mode',
                    views: {
                        '@': {
                            controller: 'BuildController',
                            templateUrl: 'build/build.html'
                        }
                    }
                })
                .state('watchlists', {
                    url: '/watchlists',
                    views: {
                        '@': {
                            controller: 'WatchListController',
                            templateUrl: 'watchlists/watchlists.html'
                        }
                    }
                }).state('user-settings', {
                    url: '/user-settings',
                    views: {
                        '@': {
                            controller: 'UserSettingsController',
                            templateUrl: 'user-settings/user-settings.html'
                        }
                    },
                    resolve: {
                        user: function (userService) {
                            return userService.getUserData();
                        }
                    }
                }).state('setFilter', {
                    url: '/set/filter',
                    views: {
                        '@': {
                            controller: 'FilterCtrl',
                            templateUrl: 'user-settings/filter.html'
                        }
                    }
                });
        },
        NavCtrl = function ($scope) {
            var lookup = {
                admin: { name: ['admin', 'addUser', 'modifyUser'] },
                dashboard: { name: ['dashboard'] },
                flights: { name: ['flights'] },
                passengers: { name: ['paxAll', 'flightpax'] },
                queries: { mode: ['query'] },
                risks: { mode: ['rule'] },
                watchlists: { name: ['watchlists'] },
                usersettings: { name: ['user-settings','setFilter'] },
                upload: { name: ['upload'] }
            };
            $scope.onRoute = function (key) {
                return (lookup[key].name && lookup[key].name.indexOf($scope.stateName) >= 0) || (lookup[key].mode && lookup[key].mode.indexOf($scope.mode) >= 0);
            };
            $scope.showNav = function () {
                return ['queryFlights', 'queryPassengers', 'detail'].indexOf($scope.stateName) === -1;
            };
            $scope.$on('stateChanged', function (e, state, toParams) {
                $scope.stateName = state.name;
                $scope.mode = toParams.mode;
            });
        };

    app = angular
        .module('myApp', appDependencies)
        .config(router)
        .config(localDateMomentFormat)
        .run(initialize)
        .controller('NavCtrl', NavCtrl);
}());
