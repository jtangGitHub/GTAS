app.factory('QueryBuilderCtrl', function () {
    'use strict';
    return function ($scope, $timeout) {
        var selectizeValueSetter = function (rule, value) {
                var $selectize = rule.$el.find(".rule-value-container .selectized");
                if ($selectize.length) {
                    $timeout(function () {
                        $selectize[0].selectize.setValue(value);
                    }, 100);
                }
            },
            getOptionsFromJSONArray = function (that, property) {
                //if (localStorage[property] === undefined) {
                $.getJSON('./data/' + property + '.json', function (data) {
                    //localStorage[property] = JSON.stringify(data);
                    try {
                        data.forEach(function (item) {
                            that.addOption(item);
                        });
                    } catch (exception) {
                        throw exception;
                    }
                });
                //} else {
                //    try {
                //    JSON.parse(localStorage[property]).forEach(function (item) {
                //    that.addOption(item);
                //    });
                //    } catch (exception) {
                //       throw exception;
                //    }
                //}
            };

        $scope.alerts = [];

        $scope.alert = function (type, text) {
            $scope.alerts.push({type: type, msg: text});
            $timeout(function () {
                $scope.alerts[$scope.alerts.length - 1].expired = true;
            }, 2000);
            $timeout(function () {
                $scope.alerts.splice($scope.alerts.length - 1, 1);
            }, 3000);
        };

        $scope.alertSuccess = function (text) {
            $scope.alert('success', text);
        };

        $scope.alertError = function (text) {
            $scope.alert('danger', text);
        };

        $scope.alertInfo = function (text) {
            $scope.alert('info', text);
        };

        $scope.alertWarn = function (text) {
            $scope.alert('warning', text);
        };

        $scope.closeAlert = function (index) {
            $scope.alerts.splice(index, 1);
        };

        $scope.today = moment().format('YYYY-MM-DD').toString();
        $scope.authorId = 'adelorie';
        $scope.calendarOptions = {
            format: 'yyyy-mm-dd',
            autoClose: true
        };
        $scope.options = {
            allow_empty: true,
            service: "DROOLS",
            plugins: {
                'bt-tooltip-errors': {delay: 100},
                'sortable': null,
                'filter-description': {mode: 'bootbox'},
                'bt-selectpicker': null,
                'unique-filter': null,
                'bt-checkbox': {color: 'primary'}
            },
            filters: []
        };

        $scope.showPencil = function (id) {
            $timeout(function () {
                var $pageControls = $('.ng-table-pagination').children();
                if ($scope.ruleId === null && $pageControls.length >= 4) {
                    $pageControls.eq($pageControls.length - 2).find('a')[0].click();
                }
                $timeout(function () {
                    var $tableRows = $('table tbody').eq(0).find('tr');
                    if ($scope.ruleId === null) {
                        $tableRows.last().click();
                    }
                    $scope.ruleId = id;
                    $scope.saving = false;
                }, 200);
            }, 200);
        };

        $scope.buildAfterEntitiesLoaded = function (options) {
            var property = 'entities',
                $builder = $('#builder'),
                supplement = {
                    selectize: function (obj) {
                        obj.plugin_config = {
                            "valueField": "id",
                            "labelField": "name",
                            "searchField": "name",
                            "sortField": "name",
                            "create": false,
                            "plugins": ["remove_button"],
                            "onInitialize": function () {
                                getOptionsFromJSONArray(this, obj.dataSource);
                            }
                        };
                        obj.valueSetter = selectizeValueSetter;
                    },
                    datepicker: function (obj) {
                        obj.validation = { "format": "YYYY-MM-DD" };
                        obj.plugin_config = {
                            "format": "yyyy-mm-dd",
                            "autoClose": true
                        };
                    }
                };
            // init
            $builder
                .on('afterCreateRuleInput.queryBuilder', function (e, rule) {
                    if (rule.filter !== undefined && rule.filter.plugin === 'selectize') {
                        rule.$el.find('.rule-value-container').css('min-width', '200px')
                            .find('.selectize-control').removeClass('form-control');
                    }
                });

            try {
                //if (localStorage[property] === undefined) {
                $.getJSON('./data/' + property + '.json', function (data) {
                    //localStorage[property] = JSON.stringify(data);
                    if (options && options.deleteEntity) {
                        data[options.deleteEntity] = null;
                        delete data[options.deleteEntity];
                    }
                    $scope.options.entities = data;
                    $scope.options.filters = [];
                    Object.keys($scope.options.entities).forEach(function (key){
                        $scope.options.entities[key].columns.forEach(function (column){
                            switch (column.plugin) {
                            case 'selectize':
                            case 'datepicker':
                                supplement[column.plugin](column);
                                break;
                            default:
                                break;
                            }
                            $scope.options.filters.push(column);
                        });
                    });
                    $builder.queryBuilder($scope.options);

                    $scope.$builder = $builder;
                    $scope.newRule();

                    $('.datepicker').datepicker({
                        startDate: $scope.today.toString(),
                        minDate: $scope.today.toString(),
                        format: 'yyyy-mm-dd',
                        autoClose: true
                    });
                });
                //} else {
                //    $scope.options.entities = JSON.parse(localStorage[property]);
                //    $builder.queryBuilder($scope.options);
                //    $scope.$builder = $builder;
                //}
            } catch (exception) {
                throw exception;
            }
        };

        $scope.isBeingEdited = function () {
            return $scope.ruleId === this.$data[this.$index].id;
        };

        $scope.loadSummary = function (summary) {
            Object.keys(summary).forEach(function (key) {
                $scope[key] = summary[key];
            });
        };

        $scope.formats = ["YYYY-MM-DD"];

        $scope.newRule = function () {
            $scope.ruleId = null;
            $scope.$builder.queryBuilder('reset');
            $scope.loadSummary($scope.summaryDefaults);
            document.getElementById('title').focus();
        };

        $scope.ruleId = null;
    };
});
