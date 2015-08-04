app.controller('QueryBuilderController', function ($scope, $injector, QueryBuilderCtrl, $filter, $q, ngTableParams, queryBuilderService) {
    $injector.invoke(QueryBuilderCtrl, this, {$scope: $scope });
    var data = [];

    $scope.loadRule = function () {
        var obj = this.$data[this.$index];
        $scope.ruleId = obj.id;
        $scope.loadSummary({title: obj.title, description: obj.description });
        $scope.$builder.queryBuilder('loadRules', obj.query);
    };

    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        count: 10,          // count per page
        filter: {},
        sorting: {
            hits: 'desc',
            destinationDateTimeSort: 'asc' //, 'number': 'asc'     // initial sorting
        }
    }, {
        counts: [],         // disable / hide page row count toggle
        total: data.length, // length of data
        getData: function ($defer, params) {
            queryBuilderService.getList($scope.authorId).then(function (myData) {
                var filteredData, orderedData;
                data = [];

                if (myData.result === undefined || !Array.isArray(myData.result)) {
                    return;
                }

                myData.result.forEach(function (obj) {
                    data.push(obj);
                });

                filteredData = params.filter() ? $filter('filter')(data, params.filter()) : data;
                orderedData = params.sorting() ? $filter('orderBy')(filteredData, params.orderBy()) : data;

                params.total(orderedData.length); // set total for recalc pagination
                $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            });
        }
    });

    $scope.buildAfterEntitiesLoaded();

    $scope.delete = function () {
        queryBuilderService.deleteQuery($scope.ruleId, $scope.authorId).then(function (myData) {
            $scope.newRule();
            $scope.tableParams.reload();
        });
    };

    $scope.summaryDefaults = {description: null, title: ''};
    $scope.ruleId = null;
    $scope.saving = false;

    $scope.save = function () {
        var queryObject;
        if ($scope.saving) {
            return;
        }
        $scope.title = $scope.title.trim();
        if (!$scope.title.length) {
            alert('title can not be blank!');
                $scope.saving = false;
            return;
        }
        queryObject = {
            id: $scope.ruleId,
            title: $scope.title,
            description: $scope.description || null,
            userId: $scope.authorId,
            query: $scope.$builder.queryBuilder('saveRules')
        };

        queryBuilderService.saveQuery(queryObject).then(function (myData) {
            if (myData.errorCode !== undefined) {
                alert(myData.errorMessage);
                $scope.saving = false;
                return;
            }
            $scope.ruleId = myData.result[0].id || null;
            $scope.tableParams.reload();
            $scope.saving = false;
        });
    };

    $scope.viewType = null;
    $scope.viewTypeChange = function () {
        console.log($scope.viewType);
    };
});
