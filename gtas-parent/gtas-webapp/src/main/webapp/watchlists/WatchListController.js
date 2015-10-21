(function () {
    'use strict';
    app.controller('WatchListController', function ($scope, gridOptionsLookupService, $q, watchListService, $interval, spinnerService, $timeout) {
        var watchlist = {},
            tabs = [],
            model = {
                Document: function (entity) {
                    this.id = entity ? entity.id : null;
                    this.documentType = entity ? entity.documentType : null;
                    this.documentNumber = entity ? entity.documentNumber : null;
                },
                Passenger: function (entity) {
                    this.id = entity ? entity.id : null;
                    this.firstName = entity ? entity.firstName : null;
                    this.lastName = entity ? entity.lastName : null;
                    this.dob = entity ? entity.dob : null;
                }
            };

        $scope.Document = new model.Document();
        $scope.Passenger = new model.Passenger();

        $scope.watchlistGrid = gridOptionsLookupService.getGridOptions('watchlist');

        $scope.documentTypes = [
            { id: "P", label: "PASSPORT" },
            { id: "V", label: "VISA" }
        ];

        watchlist.types = {
            "Document": {
                entity: "DOCUMENT",
                columns: [{
                    field: "documentType",
                    name: "documentType",
                    displayName: "Type",
                    "type": "string"
                }, {
                    field: "documentNumber",
                    name: "documentNumber",
                    displayName: "Number",
                    "type": "string"
                }]
            },
            "Passenger": {
                entity: "PASSENGER",
                columns: [{
                    field: "firstName",
                    name: "firstName",
                    displayName: "First Name",
                    "type": "string"
                }, {
                    field: "lastName",
                    name: "lastName",
                    displayName: "Last Name",
                    "type": "string"
                }, {
                    cellFilter: "date:\'yyyy-MM-dd\'",
                    field: "dob",
                    name: "dob",
                    displayName: "DOB",
                    "type": "date"
                }]
            }
        };
        $scope.data = {};
        $scope.watchlistGrid.enableCellEditOnFocus = true;
        $scope.watchlistGrid.columnDefs = watchlist.types.Document.columns;
        $scope.getListItemsFor = function (listName) {
            watchListService.getListItems(watchlist.types[listName].entity, listName).then(function (response) {
                var obj, data = [], items = response.data.result.watchlistItems;
                $scope.activeTab = listName;
                $scope.watchlistGrid.columnDefs = watchlist.types[listName].columns;
                $scope.watchlistGrid.exporterCsvFilename = 'watchlist-' + listName + '.csv';
                if (items === undefined) {
                    $scope.watchlistGrid.data = [];
                    return false;
                }
                items.forEach(function (item) {
                    obj = {id: item.id };
                    item.terms.forEach(function (term) {
                        obj[term.field] = term.type === 'date' ? moment(term.value).format('YYYY-MM-DD') : term.value;
                    });
                    data.push(obj);
                });
                $scope.data[listName] = data;
                $scope.watchlistGrid.data = data;
            });
        };

        $scope.updateGrid = function (listName) {
//            $scope.template = listName;
            if ($scope.data[listName]) {
                $scope.activeTab = listName;
                $scope.watchlistGrid.columnDefs = watchlist.types[listName].columns;
                $scope.watchlistGrid.exporterCsvFilename = 'watchlist-' + listName + '.csv';
                $scope.watchlistGrid.data = $scope.data[listName];
                return;
            }

            $scope.getListItemsFor(listName);
        };

        Object.keys(watchlist.types).forEach(function (key) {
            tabs.push({title: key});
        });

        $scope.tabs = tabs;
        $scope.activeTab = tabs[0].title;
        $scope.rowSelected = false;

        $scope.Add = function () {
            $scope[$scope.activeTab] = new model[$scope.activeTab]();
        };

        $scope.saveRow = function (rowEntity) {
            var watchlistType = watchlist.types[$scope.activeTab],
                columnTypeDict = {},
                entity = watchlistType.entity,
                method = !rowEntity.id ? 'addItem' : 'updateItem',
                terms = [],
                columnType,
                value,
                ready = true;

            watchlistType.columns.forEach(function (column) {
                columnTypeDict[column.name] = column.type;
            });

            Object.keys(rowEntity).forEach(function (key) {
                if (['$$hashKey', 'id'].indexOf(key) === -1) {
                    columnType = columnTypeDict[key];
                    value = rowEntity[key];
                    if (!value) {
                        ready = false;
                    }
                    if (columnType === 'date') {
                        value = moment(value).format('YYYY-MM-DD');
                    }
                    terms.push({entity: entity, field: key, type: columnType, value: value});
                }
            });
            if (ready) {
                watchListService[method]($scope.activeTab, entity, rowEntity.id, terms).then(function (response) {
                    console.log('saved');
                    $scope[$scope.activeTab].id = response.data.result;
                    $scope.watchlistGrid.data.unshift($scope[$scope.activeTab]);
                    $scope.gridApi.selection.clearSelectedRows();
                    $scope.rowSelected = false;
                    $scope.Add();
                });
            }
        };

        $scope.watchlistGrid.onRegisterApi = function (gridApi) {
            $scope.gridApi = gridApi;
            gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                if (row.isSelected) {
                    $scope[$scope.activeTab] = row.entity;
                    $scope.rowSelected = true;
                } else {
                    $scope.rowSelected = false;
                    $scope.gridApi.selection.clearSelectedRows();
                    $scope.Add();
                }
            });
            //           gridApi.rowEdit.on.saveRow($scope, $scope.saveRow);
            //           gridApi.rowEdit.flushDirtyRows($scope.watchlistGrid);
        };

        $scope.removeRow = function () {
            var entity = watchlist.types[$scope.activeTab].entity,
                selectedRowEntities = $scope.gridApi.selection.getSelectedRows();

            selectedRowEntities.forEach(function (rowEntity) {
                var rowIndexToDelete = $scope.watchlistGrid.data.indexOf(rowEntity);
                watchListService.deleteItem($scope.activeTab, entity, rowEntity.id).then(function () {
                    $scope.watchlistGrid.data.splice(rowIndexToDelete, 1);
                });
            });
        };

        $scope.updateWatchlistService = function () {
            if ($scope.updating) { return false; }
            $scope.updating = true;
            watchListService.compile().then(function () {
                spinnerService.show('html5spinner');
                $timeout(function () {
                    spinnerService.hide('html5spinner');
                    $scope.updating = false;
                }, 2500);
            });
        };

        $scope.$scope = $scope;
    });
}());
