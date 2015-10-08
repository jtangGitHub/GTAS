(function () {
    'use strict';
    var setFlightsGrid = function (grid, data) {
            grid.totalItems = data.totalFlights;
            grid.data = data.flights;
        },
        flightDirections = [
            {label: 'Inbound', value: 'I'},
            {label: 'Outbound', value: 'O'},
            {label: 'Any', value: ''}
        ];
    app
        .controller('FlightsController', function ($scope, $state, $interval, $stateParams, flightService, gridService, uiGridConstants) {
            var getPage = function () {
                flightService.getFlights($scope.model).then(function (data) {
                    setFlightsGrid($scope.flightsGrid, data);
                });
            };

            $scope.model = flightService.model;

            $scope.selectedFlight = $stateParams.flight;

            $scope.flightDirections = flightDirections;

            $scope.flightsGrid = {
                paginationPageSizes: [10, 15, 25],
                paginationPageSize: $scope.model.pageSize,
                useExternalPagination: true,
                useExternalSorting: true,
                useExternalFiltering: true,
                enableHorizontalScrollbar: 0,
                enableVerticalScrollbar: 0,
                enableColumnMenus: false,

                onRegisterApi: function (gridApi) {
                    $scope.gridApi = gridApi;

                    gridApi.core.on.sortChanged($scope, function (grid, sortColumns) {
                        if (sortColumns.length === 0) {
                            $scope.model.sort = null;
                        } else {
                            $scope.model.sort = [];
                            for (var i = 0; i < sortColumns.length; i++) {
                                $scope.model.sort.push({
                                    column: sortColumns[i].name,
                                    dir: sortColumns[i].sort.direction
                                });
                            }
                        }
                        getPage();
                    });

                    gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                        if ($scope.model.pageNumber !== newPage || $scope.model.pageSize !== pageSize) {
                            $scope.model.pageNumber = newPage;
                            $scope.model.pageSize = pageSize;
                            getPage();
                        }
                    });
                }
            };

            $scope.flightsGrid.columnDefs = [
                {
                    name: 'passengerCount', displayName: 'P', width: 50, enableFiltering: false,
                    cellTemplate: '<button id="editBtn" type="button" class="btn-small" ng-click="grid.appScope.passengerNav(row)">{{COL_FIELD}}</button> ',
                },
                {
                    name: 'ruleHitCount',
                    displayName: 'H',
                    width: 50,
                    enableFiltering: false,
                    cellClass: gridService.colorHits,
                    sort: {
                        direction: uiGridConstants.DESC,
                        priority: 0
                    }
                },
                {
                    name: 'listHitCount',
                    displayName: 'L',
                    width: 50,
                    enableFiltering: false,
                    cellClass: gridService.colorHits,
                    sort: {
                        direction: uiGridConstants.DESC,
                        priority: 1
                    }
                },
                {name: 'fullFlightNumber', displayName: 'Flight', width: 75},
                {
                    name: 'eta', displayName: 'ETA',
                    sort: {
                        direction: uiGridConstants.DESC,
                        priority: 2
                    }
                },
                {name: 'etd', displayName: 'ETD'},
                {name: 'origin'},
                {name: 'originCountry', displayName: 'Country'},
                {name: 'destination'},
                {name: 'destinationCountry', displayName: 'Country'}
            ];

            $scope.passengerNav = function (row) {
                $scope.selectedFlight = row.entity;
                $state.go('flights.passengers', {parent: 'flights', flight: $scope.selectedFlight});
            };

            $scope.filter = function () {
                getPage();
            }

            $scope.reset = function () {
                $scope.model = flightService.initialModel();
                getPage();
            }

            $scope.getTableHeight = function () {
                return gridService.calculateGridHeight($scope.flightsGrid.data.length);
            };
            if ($stateParams.parent && $stateParams.parent === 'query') {

            } else {
                $state.go('flights.all');
            }
            getPage();
        })
        .controller('QueryFlightsController', function ($scope, $state, $interval, $stateParams, flightService, gridService, uiGridConstants, queryResults) {
            var getPage = function () {
                setFlightsGrid($scope.flightsGrid, queryResults.result.data || {flights: [], totalFlights: 0 });
            };

            $scope.model = flightService.model;

            $scope.selectedFlight = $stateParams.flight;

            $scope.flightDirections = flightDirections;

            $scope.flightsGrid = {
                paginationPageSizes: [10, 15, 25],
                paginationPageSize: $scope.model.pageSize,
                useExternalPagination: true,
                useExternalSorting: true,
                useExternalFiltering: true,
                enableHorizontalScrollbar: 0,
                enableVerticalScrollbar: 0,
                enableColumnMenus: false,

                onRegisterApi: function (gridApi) {
                    $scope.gridApi = gridApi;

                    gridApi.core.on.sortChanged($scope, function (grid, sortColumns) {
                        if (sortColumns.length === 0) {
                            $scope.model.sort = null;
                        } else {
                            $scope.model.sort = [];
                            for (var i = 0; i < sortColumns.length; i++) {
                                $scope.model.sort.push({
                                    column: sortColumns[i].name,
                                    dir: sortColumns[i].sort.direction
                                });
                            }
                        }
                        getPage();
                    });

                    gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                        if ($scope.model.pageNumber !== newPage || $scope.model.pageSize !== pageSize) {
                            $scope.model.pageNumber = newPage;
                            $scope.model.pageSize = pageSize;
                            getPage();
                        }
                    });
                }
            };

            $scope.flightsGrid.columnDefs = [
                {
                    name: 'passengerCount', displayName: 'P', width: 50, enableFiltering: false,
                    cellTemplate: '<button id="editBtn" type="button" class="btn-small" ng-click="grid.appScope.passengerNav(row)">{{COL_FIELD}}</button> ',
                },
                {
                    name: 'ruleHitCount',
                    displayName: 'H',
                    width: 50,
                    enableFiltering: false,
                    cellClass: gridService.colorHits,
                    sort: {
                        direction: uiGridConstants.DESC,
                        priority: 0
                    }
                },
                {
                    name: 'listHitCount',
                    displayName: 'L',
                    width: 50,
                    enableFiltering: false,
                    cellClass: gridService.colorHits,
                    sort: {
                        direction: uiGridConstants.DESC,
                        priority: 1
                    }
                },
                {name: 'fullFlightNumber', displayName: 'Flight', width: 75},
                {
                    name: 'eta', displayName: 'ETA',
                    sort: {
                        direction: uiGridConstants.DESC,
                        priority: 2
                    }
                },
                {name: 'etd', displayName: 'ETD'},
                {name: 'origin'},
                {name: 'originCountry', displayName: 'Country'},
                {name: 'destination'},
                {name: 'destinationCountry', displayName: 'Country'}
            ];

            $scope.passengerNav = function (row) {
                $scope.selectedFlight = row.entity;
                $state.go('flights.passengers', {parent: 'flights', flight: $scope.selectedFlight});
            };

            $scope.filter = function () {
                getPage();
            }

            $scope.reset = function () {
                $scope.model = flightService.initialModel();
                getPage();
            }

            $scope.getTableHeight = function () {
                return gridService.calculateGridHeight($scope.flightsGrid.data.length);
            };
            if ($stateParams.parent && $stateParams.parent === 'query') {

            } else {
                $state.go('flights.all');
            }
            getPage();
        });
}());