app.controller('UserSettingsController', function ($scope, $state, $interval, $stateParams,user,userService, $location, flightsModel,filterService,$mdToast) {

    $scope.userCredentials = {
        firstName: '',
        lastName: '',
        newPassword:'',
        confirmPassword:''
    };
    $scope.updateCredentials=function()
    {
        user.data.firstName=$scope.userCredentials.firstName;
        user.data.lastName=$scope.userCredentials.lastName;
        user.data.password=$scope.userCredentials.newPassword;
        userService.updateUser(user.data).
            then(
            function () {
                $scope.successMessage="Your credentials were updated successfully." ;
                $scope.success=true;
            }
        );
    };
    init=function()
    {
        $scope.userCredentials.firstName=user.data.firstName;
        $scope.userCredentials.lastName=user.data.lastName;
        $scope.userCredentials.newPassword=user.data.password;
        $scope.userCredentials.confirmPassword=user.data.password;
        $scope.success=false;
        $scope.error=false;
    };
    init();


    //$scope.setFilter = function () {
    //
    //    /*	$scope.partialView="setFilter";
    //     $state.go('user-settings.setFilter');*/
    //    $location.path('/set/filter');
    //};



//---------------------------------------- Filter JS -----------------------

    var userData = function (user) {
            $scope.user = user.data;      $scope.filter.userId=user.data.userId;
            if(user.data.filter!=null) {
                if(user.data.filter.originAirports!=null)
                    document.querySelector('#originAirports').selectize.setValue(user.data.filter.originAirports);
                if(user.data.filter.destinationAirports!=null)
                    document.querySelector('#destinationAirports').selectize.setValue(user.data.filter.destinationAirports);
                if(user.data.filter.etaStart)
                    $scope.filter.etaStart=user.data.filter.etaStart;
                if(user.data.filter.etaEnd)
                    $scope.filter.etaEnd=user.data.filter.etaEnd;
                if(user.data.filter.flightDirection)
                    $scope.filter.flightDirection=user.data.filter.flightDirection;
            }
        },
        initFilter=function() {
            $scope.success=false;
            $scope.error=false;
            userService.getUserData().then(userData);
        },
        getOptionsFromJSONArray = function (that, property) {
            $.getJSON('./data/' + property + '.json', function (data) {
                try {
                    data.forEach(function (item) {
                        that.addOption(item);
                    });
                } catch (exception) {
                    throw exception;
                }
            });
        },
        airports = (function () {
            var json = null;
            $.ajax({
                'async': false,
                'global': false,
                'url': 'airports.json',
                'dataType': "json",
                'success': function (data) {
                    json = data;
                }
            });
            return json;
        })(),
        alert = function (content) {
            $mdToast.show(
                $mdToast.simple().content(content).position("top right").hideDelay(3000)
            );
        },
        filterCreated  = function (filter) { alert('Filter has been saved');},
        filterUpdated  = function (filter) { alert('Filter has been updated'); };

    $scope.flightDirections = [
        {label: 'Inbound', value: 'I'},
        {label: 'Outbound', value: 'O'},
        {label: 'Any', value: 'A'}
    ];

    $scope.etaStartDays = [
        {label: 'Today-1', value: '-1'},{label: 'Today-2', value: '-2'}, {label: 'Today-3', value: '-3'},
        {label: 'Today-4', value: '-4'},{label: 'Today-5', value: '-5'}, {label: 'Today-6', value: '-6'},
        {label: 'Today-7', value: '-7'},{label: 'Today-8', value: '-8'}, {label: 'Today-9', value: '-9'},
        {label: 'Today-10', value: '-10'}
    ];

    $scope.etaEndDays = [
        {label: 'Today+1', value: '1'},{label: 'Today+2', value: '2'}, {label: 'Today+3', value: '3'},
        {label: 'Today+4', value: '4'},{label: 'Today+5', value: '5'}, {label: 'Today+6', value: '6'},
        {label: 'Today+7', value: '7'},{label: 'Today+8', value: '8'}, {label: 'Today+9', value: '9'},
        {label: 'Today+10', value: '10'}
    ];

    $scope.filter = {
        userId:'', originAirports: [], destinationAirports: [], flightDirection:flightsModel.direction,  etaStart:-1,etaEnd:1
    };
    $scope.setFilter=function() {
        if($scope.user.filter==null ) {
            filterService.setFilter($scope.filter, $scope.user.userId).
            then(filterCreated);
        }
        else {
            filterService.updateFilter($scope.filter, $scope.user.userId).then(filterUpdated);
        }
    };

    $('#originAirports,#destinationAirports').selectize({
        delimiter: ',',
        valueField: 'id',
        labelField: 'name',
        "searchField": "name",
        "sortField": "name",
        "create": false,
        "persist": false,

        "onInitialize": function () {
            getOptionsFromJSONArray(this, 'airports');

        }
    });

    $("#originAirports").change(function(){
        $scope.filter.originAirports=$(this).val().split(',');

    });

    $("#destinationAirports").change(function(){

        $scope.filter.destinationAirports=$(this).val().split(',');
    });

    initFilter();



});
