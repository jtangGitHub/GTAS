app.controller('FlightsController', function ($scope, $http, flightService, gridService, $state, $interval, $stateParams) {

  $scope.selectedFlight=$stateParams.flight;

  var paginationOptions = {
    pageNumber: 1,
    pageSize: 10,
    sort: null
  };

  $scope.gridOptions = { 
    enableSorting: false,
    multiSelect: false,
    enableFiltering: false,     
    enableRowSelection: true, 
    enableSelectAll: false,
    enableRowHeaderSelection: false,
    enableGridMenu: false,  	
    paginationPageSizes: [10, 25, 50],
    paginationPageSize: 10,
    useExternalPagination: true,
    useExternalSorting: true,
    useExternalFiltering: true,
    
    onRegisterApi: function(gridApi) {
      $scope.gridApi = gridApi;
      
      gridApi.core.on.sortChanged($scope, function(grid, sortColumns) {
        if (sortColumns.length == 0) {
          paginationOptions.sort = null;
        } else {
          paginationOptions.sort = sortColumns[0].sort.direction;
        }
        getPage();
      });      
      
      gridApi.core.on.filterChanged( $scope, function() {
        var grid = this.grid;
      });
      
      gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
        paginationOptions.pageNumber = newPage;
        paginationOptions.pageSize = pageSize;
        getPage();
      });

      gridApi.selection.on.rowSelectionChanged($scope,function(row){
        $scope.selectedFlight=row.entity;
        console.log($scope.selectedFlight);
      })
    }
  };

  $scope.gridOptions.columnDefs = [
    { name: 'P', field: 'passengerCount', width: 50, enableFiltering: false,
        cellTemplate: '<button id="editBtn" type="button" class="btn-small" ng-click="grid.appScope.passengerNav(row)">{{COL_FIELD}}</button> ' ,
    },
    { name: 'H', field: 'ruleHitCount', width: 50, enableFiltering: false, cellClass: gridService.colorHits },
    { name: 'L', field: 'listHitCount', width: 50, enableFiltering: false, cellClass: gridService.colorHits },
    { name: 'Carrier', field: 'carrier', width: 75 },
    { name: 'Flight', field: 'flightNumber', width: 75 },
    { name: 'Dir', field: 'direction', width: 50 },    
    { name: 'ETA', displayName: 'ETA', field: 'eta' },
    { name: 'ETD', displayName: 'ETD', field: 'etd' },    
    { name: 'Origin', field: 'origin' },
    { name: 'OriginCountry', displayName: "Country", field: 'originCountry' },
    { name: 'Dest', field: 'destination' },
    { name: 'DestCountry', displayName: "Country", field: 'destinationCountry' }
  ];

  $scope.passengerNav = function(row){
    $scope.selectedFlight=row.entity;
    $state.go('flights.passengers',{ parent: 'flights', flight: $scope.selectedFlight });
  };

  var getPage = function() {
    console.log('requesting page #' + paginationOptions.pageNumber);
    flightService.getFlights(paginationOptions).then(function (page) {
      $scope.gridOptions.totalItems = page.totalFlights;
      $scope.gridOptions.data = page.flights;

      $interval( function() {
          $scope.gridApi.selection.selectRow($scope.gridOptions.data[0]);
      }, 0, 1);
    });
  };
  
  $scope.getTableHeight = function() {
     var rowHeight = 30;
     var headerHeight = 30;
     return {
        height: ($scope.gridOptions.data.length * rowHeight + 2 * headerHeight) + "px"
     };
  };  
  
  getPage();

  $state.go('flights.all');
});
