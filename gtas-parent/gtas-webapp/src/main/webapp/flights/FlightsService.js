app.service("flightService", function ($http, $q) {
  'use strict';

  var defaultSort = [
    { column: 'ruleHitCount', dir: 'desc' },
    { column: 'listHitCount', dir: 'desc' },
    { column: 'eta', dir: 'desc' }
  ];

  var startDate = new Date();
  var endDate = new Date();
  endDate.setDate(endDate.getDate() + 3);

  // Return public API.
  return ({
    model: {
      pageNumber: 1,
      pageSize: 10,
      flightNumber: '',
      origin: '',
      dest: '',
      direction: 'I',
      etaStart: startDate,
      etaEnd: endDate,
      sort: defaultSort 
    },
    getFlights: getFlights
  });

  function getFlights(paginationOptions) {
    var request = $http({
      method: 'post',
      url: "/gtas/flights/",
      data: paginationOptions
    });
    
    return ( request.then(handleSuccess, handleError) );
  }

  // I transform the error response, unwrapping the application dta from
  // the API response payload.
  function handleError(response) {
    // The API response from the server should be returned in a
    // nomralized format. However, if the request was not handled by the
    // server (or what not handles properly - ex. server error), then we
    // may have to normalize it on our end, as best we can.
    if (!angular.isObject(response.data) || !response.data.message) {
        return ( $q.reject("An unknown error occurred.") );
    }

    // Otherwise, use expected error message.
    return ( $q.reject(response.data.message) );
  }

  // I transform the successful response, unwrapping the application data
  // from the API response payload.
  function handleSuccess(response) {
    return ( response.data );
  }
});
