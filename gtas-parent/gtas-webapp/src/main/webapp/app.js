var app;
(function () {
    'use strict';
    var pageDefaults = {
            pageNumber: 1,
            pageSize: 10
        },
        appDependencies = [
            'ui.router',
            'ct.ui.router.extras',
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
            'angularSpinners'
        ],
        initialize = function ($rootScope) {
            //these two are for learning router state
            $rootScope.$on('$stateChangeStart', function (e, toState) {
                $rootScope.$broadcast('stateChanged', toState.name);
            });
        },
        router = function ($stateProvider) {
            $stateProvider
                .state('dashboard', {
                    url: '/dashboard',
                    templateUrl: 'dashboard/dashboard.html',
                    controller: 'DashboardController'
                })
                .state('admin', {
                    url: '/admin',
                    templateUrl: 'admin/admin.header.html',
                    controller: 'AdminCtrl'
                })
                .state('admin.users', {
                    url: '/',
                    sticky: true,
                    dsr: true,
                    views: {
                        "content@admin": {
                            templateUrl: 'admin/admin.html'
                        }
                    }
                })
                .state('admin.addUser', {
                    url: '/user',
                    params: {
                        action: null,
                        user: null
                    },
                    sticky: true,
                    dsr: true,
                    views: {
                        "content@admin": {
                            controller: 'UserCtrl',
                            templateUrl: 'admin/user.html'
                        }
                    }
                })
                .state('flights', {
                    url: '/flights',
                    sticky: true,
                    dsr: true,
                    templateUrl: 'flights/flights.html',
                    controller: 'FlightsController',
                    resolve: {
                        flights: function (flightService) {
                            return flightService.getFlights(flightService.model);
                        }
                    }
                })
                .state('flightsPassengers', {
                    url: '/passengers/:id/:flightNumber/:origin/:destination',
                    templateUrl: 'pax/pax.table.html',
                    controller: 'PaxController',
                    resolve: {
                        passengers: function (paxService, $stateParams) {
                            return paxService.getPax($stateParams.id, paxService.fromFlightModel($stateParams));
                        }
                    }
                })
                .state('queryFlights', {
                    url: '/query/flights',
                    controller: 'FlightsController',
                    templateUrl: 'flights/query-flights.html',
                    resolve: {
                        flights: function (executeQueryService, $stateParams) {
                            var postData, query = JSON.parse(localStorage['query']);
                            postData = {
                                pageNumber: $stateParams.pageNumber || pageDefaults.pageNumber,
                                pageSize: $stateParams.pageSize || pageDefaults.pageSize,
                                query: query
                            };
                            return executeQueryService.queryFlights(postData);
                        }
                    }
                })
                .state('queryPassengers', {
                    url: '/query/passengers',
                    templateUrl: 'pax/query.pax.table.html',
                    controller: 'PaxController',
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
                    templateUrl: 'pax/pax.detail.html',
                    controller: 'PassengerDetailCtrl',
                    resolve: {
                        passenger: function (paxDetailService, $stateParams) {
                            return paxDetailService.getPaxDetail($stateParams.paxId, $stateParams.flightId);
                        }
                    }
                })
                .state('paxAll', {
                    url: '/passengers',
                    templateUrl: 'pax/pax.table.html',
                    controller: 'PaxController',
                    resolve: {
                        passengers: function (paxService) {
                            return paxService.getAllPax(paxService.model);
                        }
                    }
                })
                .state('query-builder', {
                    url: '/query-builder',
                    templateUrl: 'query-builder/query.html',
                    controller: 'QueryBuilderController'
                })
                .state('risk-criteria', {
                    url: '/risk-criteria',
                    templateUrl: 'risk-criteria/risk-criteria.html',
                    controller: 'RiskCriteriaController'
                })
                .state('watchlists', {
                    url: '/watchlists',
                    templateUrl: 'watchlists/watchlists.html',
                    controller: 'WatchListController'
                });
        };

    app = angular
        .module('myApp', appDependencies)
        .config(router)
        .run(initialize);
}());
