<style>
    #detailSection md-tabs-wrapper > md-tabs-canvas > md-pagination-wrapper > md-tab-item {
        width: 1050px;
    }

    md-tab-item {
        max-width: none !important;
    }

    .capitalize {
        text-transform: capitalize;
    }

    h3 {
        margin-top: -3em;
    }
</style>
<div style="padding-left: 25px; padding-top: 25px; padding-bottom: 25px; padding-right: 25px; min-width: 1680px; min-height: 1500px;">
    <!--<h3><span class="glyphicon glyphicon-user glyphiconFlightPax img-circle"></span>Passenger Detail</h3>-->

    <div class="span12 col-sm-offset-1" style="padding-left: 25px; padding-top: 25px; padding-bottom: 25px; padding-right: 25px;">
        <div class="container">
            <div class="row">
                <div class="col-sm-4 capitalize">{{'pass.lastname' | translate}}:&nbsp; <strong>{{ passenger.lastName }}</strong></div>
                <div class="col-sm-4 capitalize">{{'pass.firstname' | translate}}:&nbsp; <strong>{{ passenger.firstName }}</strong></div>
                <div class="col-sm-4 capitalize">{{'pass.middlename' | translate}}:&nbsp; <strong>{{ passenger.middleName }}</strong></div>
            </div>
            <div class="row">
                <div class="col-sm-4">{{'pass.gender' | translate}}:&nbsp; <strong> {{ passenger.gender }}</strong></div>
                <div class="col-sm-4">{{'pass.dob' | translate}}:&nbsp; <strong> {{ passenger.dob | date:"MM/dd/yyyy" }}</strong></div>
                <div class="col-sm-4">{{'pass.paxtype' | translate}}:&nbsp; <strong> {{ passenger.passengerType }}</strong></div>
            </div>
            <div class="row">
                <div class="col-sm-4">{{'pass.origin' | translate}}:&nbsp; <strong> {{ passenger.embarkCountry }}</strong></div>
                <div class="col-sm-4">{{'pass.destination' | translate}}:&nbsp; <strong> {{ passenger.debarkCountry }}</strong></div>
                <div class="col-sm-4">{{'pass.citizenship' | translate}}:&nbsp; <strong> {{ passenger.citizenshipCountry }}</strong></div>
            </div>
        </div>
    </div>
    <!-- END OF SPAN 12 -->

    <div class="container" style="padding: 10px;">
        <div class="row">
            <div class="col-md-4"></div>
            <div class="col-md-4"></div>
            <div class="col-md-4"></div>
        </div>
    </div>
    <section>
        <div class="container" style="height:100%;">
            <div class="row">
                <div class="col-md-4"></div>
                <div class="col-md-4"></div>
                <div class="col-md-4"></div>
            </div>
        </div>
        <div class="panel panel-default" style="min-height: 1200px; max-height: 1600px;">
            <md-tabs id="detailSection" md-stretch-tabs="always" md-dynamic-height>
                <md-tab label="{{ 'pass.details' | translate}}" style="max-width: 150px;">
                    <div class="span12" style="background-color: rgb(255, 255, 255, 0.15);">
                        <div class="container" style="height:100%;">
                            <div style="padding: 10px;"></div>
                            <div class="row col-md-offset-1">
                                <div class="col-sm-2 capitalize">&nbsp;&nbsp;&nbsp;<span style="text-decoration: underline;">{{'flight.flight' | translate}}:&nbsp; <strong>{{
                                    passenger.flightNumber }}</strong></span></div>
                                <div class="col-sm-2">{{'pass.originairport' | translate}}:&nbsp; <strong>{{ passenger.flightOrigin }}</strong></div>
                                <div class="col-sm-2">{{'pass.destinationairport' | translate}}:&nbsp; <strong>{{ passenger.flightDestination }}</strong></div>
                                <div class="col-sm-2">{{'pass.etd' | translate}}:&nbsp; <strong>{{ passenger.flightETD | date:"MM/dd/yyyy HH:mm" }}</strong></div>
                                <div class="col-sm-2">{{'pass.eta' | translate}}:&nbsp; <strong>{{ passenger.flightETA | date:'MM/dd/yyyy HH:mm' }}</strong></div>
                                <div class="col-sm-1">{{'pass.seat' | translate}}:&nbsp; <strong>{{ passenger.seat }}</strong></div>
                            </div>
                        </div>
                    </div>
                    <div style="padding: 30px;"></div>
                    <div class="span10">
                        <table class="table table-hover table-condensed table-striped col-lg-offset-1"
                               style="border-collapse: separate; padding-left: 20px; width: 1200px; text-align: center;">
                            <thead>
                            <tr class="text-center">
                                <th style="text-align: center">{{'doc.doc' | translate}} #</th>
                                <th style="text-align: center">{{'doc.type' | translate}}</th>
                                <th style="text-align: center">{{'doc.iss.country' | translate}}</th>
                                <th style="text-align: center">{{'doc.iss.date' | translate}}</th>
                                <th style="text-align: center">{{'doc.exp.date' | translate}}</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr ng-repeat="doc in passenger.documents">
                                <td style="text-align: center">{{doc.documentNumber}}</td>
                                <td style="text-align: center">{{doc.documentType}}</td>
                                <td style="text-align: center">{{doc.issuanceCountry}}</td>
                                <td style="text-align: center">{{doc.issuanceDate}}</td>
                                <td style="text-align: center">{{doc.expirationDate}}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </md-tab>
                <!-- PNR Tab -->

                <md-tab label="PNR" ng-if="passenger.pnrVo.pnrRecordExists">
                    <md-tab-body>
                        <div style="padding: 10px;"></div>
                        <div class="span12">
                            <div class="container" style="height:100%;">
                                <div class="row">
                                    <div class="col-sm-4">
                                        &nbsp;&nbsp;&nbsp;&nbsp; {{'pnr.recordlocator' | translate}}:&nbsp; <span
                                                style="text-decoration: underline;"><strong>{{
                                            passenger.pnrVo.recordLocator }}</strong></span>
                                    </div>
                                    <div class="col-sm-4">
                                        {{'pnr.bookingdate' | translate}}:&nbsp; <strong>{{ passenger.pnrVo.dateBooked | date
                                            }}</strong>

                                    </div>
                                    <div class="col-sm-4">
                                         {{'pnr.received' | translate}}:&nbsp; <strong>{{ passenger.pnrVo.dateReceived | date }}</strong>

                                    </div>
                                </div>
                            </div>
                        </div>
                        <div style="padding: 30px;"></div>
                        <!-- LEFT DIV -->
                        <div class="col-lg-6" style="min-height: 200px; min-width: 40px; height: 100%; ">
                            <div class="span6">
                                <div class="container">
                                    <div id="OrderPackages">
                                        <table id="tableSearchResults"
                                               class="table table-hover table-striped table-condensed">
                                            <tbody>
                                            <tr id="package1" class="accordion-toggle" data-parent="#OrderPackages"
                                                data-target=".packageDetails1">
                                                <td><strong>{{'pnr.itenerary' | translate}} ({{ passenger.pnrVo.flightLegs.length }})</strong></td>
                                                <td></td>
                                                <td></td>
                                            </tr>
                                            <tr>
                                                <td colspan="6">
                                                    <div class="accordion-body packageDetails1" id="accordion1">
                                                        <table class="table table-condensed"
                                                               style="border-collapse: separate; text-align: center;">
                                                            <thead>
                                                              <tr class="text-center" style="text-align: center">
                                                                   <th style="text-align: center">{{'pnr.leg' | translate}}</th>
                                                                   <th style="text-align: center">{{'flight.flight' | translate}}#</th>
                                                                   <th style="text-align: center">{{'pass.originairport' | translate}}</th>
                                                                   <th style="text-align: center">{{'pass.destinationairport' | translate}}</th>
                                                                   <th style="text-align: center">{{'pnr.flightdate' | translate}}</th>
                                                                   <th style="text-align: center">{{'pass.etd' | translate}}</th>
                                                               </tr>
                                                            </thead>
                                                            <tbody>
                                                            <tr ng-repeat="doc in passenger.pnrVo.flightLegs">
                                                                <td style="text-align: center">{{ doc.legNumber }}</td>
                                                                <td style="text-align: center">{{ doc.flightNumber }}</td>
                                                                <td style="text-align: center">{{ doc.originAirport }}</td>
                                                                <td style="text-align: center">{{ doc.destinationAirport }}</td>
                                                                <td style="text-align: center">{{ doc.flightDate }}</td>
                                                                <td style="text-align: center">{{ doc.etd }}</td>
                                                            </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr id="package2" class="accordion-toggle" data-parent="#OrderPackages"
                                                data-target=".packageDetails2">
                                                <td><strong>{{'pnr.names' | translate}}  ({{ passenger.pnrVo.passengers.length }})</strong></td>
                                                <td></td>
                                                <td></td>
                                            </tr>
                                            <tr>
                                                <td colspan="6">
                                                    <div class="accordion-body packageDetails2" id="accordion2">
                                                        <table class="table table-condensed">
                                                            <thead>
                                                            <tr class="text-center" style="text-align: center">
                                                                <th style="text-align: center">{{'pnr.first' | translate}}</th>
                                                                <th style="text-align: center">{{'pnr.middle' | translate}}</th>
                                                                <th style="text-align: center">{{'pnr.last' | translate}}</th>
                                                            </tr>
                                                            </thead>
                                                            <tbody>
                                                            <tr ng-repeat="pass in passenger.pnrVo.passengers">
                                                                <td style="text-align: center">{{ pass.firstName }}</td>
                                                                <td style="text-align: center">{{ pass.middleName }}</td>
                                                                <td style="text-align: center">{{ pass.lastName }}</td>
                                                            </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr id="package1" class="accordion-toggle" data-parent="#OrderPackages"
                                                data-target=".packageDetails1">
                                                <td><strong>{{'pnr.documents' | translate}} ({{ passenger.documents.length }})</strong></td>
                                                <td></td>
                                                <td>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="6">
                                                    <div class="accordion-body packageDetails1" id="accordion1">
                                                        <table class="table table-condensed"
                                                               style="border-collapse: separate; text-align: center;">
                                                            <thead>
                                                            <tr class="text-center" style="text-align: center">
                                                                <th style="text-align: center">{{'doc.type' | translate}}</th>
                                                                <th style="text-align: center">{{'doc.name' | translate}}</th>
                                                                <th style="text-align: center">{{'doc.country' | translate}}</th>
                                                                <th style="text-align: center">{{'doc.Number' | translate}}</th>
                                                                <th style="text-align: center">{{'doc.gender' | translate}}</th>
                                                                <th style="text-align: center">{{'doc.dob' | translate}}</th>
                                                            </tr>
                                                            </thead>
                                                            <tbody>
                                                            <tr ng-repeat="doc in passenger.documents">
                                                                <td style="text-align: center">{{doc.documentType}}</td>
                                                                <td style="text-align: center"></td>
                                                                <td style="text-align: center">{{doc.issuanceCountry}}
                                                                </td>
                                                                <td style="text-align: center">{{doc.documentNumber}}
                                                                </td>
                                                                <td style="text-align: center"></td>
                                                                <td style="text-align: center"></td>
                                                            </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr id="package1" class="accordion-toggle" data-parent="#OrderPackages"
                                                data-target=".packageDetails1">
                                                <td><strong>{{'add.addresses' | translate}} ({{ passenger.pnrVo.addresses.length }})</strong>
                                                </td>
                                                <td></td>
                                                <td>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="6">
                                                    <div class="accordion-body packageDetails1" id="accordion1">
                                                        <table class="table table-condensed">
                                                            <thead>
                                                            <tr class="text-center" style="text-align: center">
                                                                <th style="text-align: center">{{'add.city' | translate}}</th>
                                                                <th style="text-align: center">{{'add.state' | translate}}</th>
                                                                <th style="text-align: center">{{'add.Country' | translate}}</th>
                                                            </tr>
                                                            </thead>
                                                            <tbody ng-repeat="addr in passenger.pnrVo.addresses track by $index">
                                                            <tr>
                                                                <td style="text-align: center">{{ addr.city }}</td>
                                                                <td style="text-align: center">{{ addr.state }}</td>
                                                                <td style="text-align: center">{{ addr.country }}</td>
                                                            </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr id="package1" class="accordion-toggle" data-parent="#OrderPackages"
                                                data-target=".packageDetails1">
                                                <td><strong>{{'phone.phonenumbers' | translate}} ({{ passenger.pnrVo.phoneNumbers.length }})</strong>
                                                </td>
                                                <td></td>
                                                <td>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="6">
                                                    <div class="accordion-body packageDetails1" id="accordion1">
                                                        <table class="table table-condensed">
                                                            <thead>
                                                                    <tr class="text-center" style="text-align: center">
                                                                        <th style="text-align: center">{{'phone.phonenumber' | translate}}</th>

                                                                    </tr>
                                                                </thead>
                                                            <tbody ng-repeat="phn in passenger.pnrVo.phoneNumbers track by $index">
                                                                <tr>
                                                                    <td style="text-align: center">{{ phn.number }}</td>
                                                                </tr>
                                                            </tbody>

                                                        </table>
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr id="package1" class="accordion-toggle" data-parent="#OrderPackages"
                                                data-target=".packageDetails1">
                                                <td><strong>{{'email.emailaddresses' | translate}} ({{ passenger.pnrVo.emails.length
                                                    }})</strong>
                                                </td>
                                                <td></td>
                                                <td>

                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="6">
                                                    <div class="accordion-body packageDetails1" id="accordion1">
                                                        <table class="table table-condensed">
                                                            <tr ng-repeat="email in passenger.pnrVo.emails track by $index">
                                                                <td>{{ email.address }}</td>
                                                            </tr>
                                                        </table>
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr id="package1" class="accordion-toggle"
                                                data-parent="#OrderPackages" data-target=".packageDetails1">
                                                <td><strong>{{'cc.creditcards' | translate}} ({{ passenger.pnrVo.creditCards.length
                                                    }})</strong>
                                                </td>
                                                <td></td>
                                                <td>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="6">
                                                    <div class="accordion-body packageDetails1" id="accordion1">
                                                        <table class="table table-condensed">
                                                            <thead>
                                                            <tr class="text-center" style="text-align: center">
                                                                <th style="text-align: center">{{'cc.holder' | translate}}</th>
                                                                <th style="text-align: center">{{'cc.Type' | translate}}</th>
                                                                <th style="text-align: center">{{'cc.Number' | translate}}</th>
                                                                <th style="text-align: center">{{'cc.expdate' | translate}}</th>
                                                            </tr>
                                                            </thead>
                                                            <tbody>
                                                            <tr ng-repeat="cc in passenger.pnrVo.creditCards track by $index">
                                                                <td style="text-align: center">{{cc.accountHolder}}</td>
                                                                <td style="text-align: center">{{cc.cardType}}</td>
                                                                <td style="text-align: center">{{cc.number}}</td>
                                                                <td style="text-align: center">{{cc.expiration |
                                                                    date:'MM-dd-yyyy' }}
                                                                </td>
                                                            </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr id="package1" class="accordion-toggle" data-parent="#OrderPackages"
                                                data-target=".packageDetails1">
                                                <td><strong>{{'ff.FrequentFlyers' | translate}} ({{
                                                    passenger.pnrVo.frequentFlyerDetails.length
                                                    }})</strong></td>
                                                <td></td>
                                                <td>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="6">
                                                    <div class="accordion-body packageDetails1" id="accordion1">
                                                        <table class="table table-condensed">
                                                            <thead>
                                                            <tr class="text-center" style="text-align: center">
                                                                <th style="text-align: center">{{'ff.airline' | translate}}</th>
                                                                <th style="text-align: center">{{'ff.number' | translate}}</th>
                                                            </tr>
                                                            </thead>
                                                            <tbody>
                                                            <tr ng-repeat="ff in passenger.pnrVo.frequentFlyerDetails track by $index">
                                                                <td style="text-align: center">{{ff.carrier}}</td>
                                                                <td style="text-align: center">{{ff.number}}</td>
                                                            </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr id="package1" class="accordion-toggle" data-parent="#OrderPackages"
                                                data-target=".packageDetails1">
                                                <td><strong>{{'tt.travelagencies' | translate}} ({{ passenger.pnrVo.agencies.length
                                                    }})</strong>
                                                </td>
                                                <td></td>
                                                <td>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="6">
                                                    <div class="accordion-body packageDetails1" id="accordion1">
                                                        <table class="table table-condensed">
                                                         <thead>
                                                            <tr class="text-center" style="text-align: center">
                                                                <th style="text-align: center">{{'tt.country' | translate}}</th>
                                                                <th style="text-align: center">{{'tt.id' | translate}}</th>
                                                                <th style="text-align: center">{{'tt.location' | translate}}</th>
                                                                <th style="text-align: center">{{'tt.Name' | translate}}</th>
                                                                <th style="text-align: center">{{'tt.Phone' | translate}}</th>
                                                              </tr>
                                                            </thead>
                                                        <tbody>
                                                                <tr ng-repeat="agency in passenger.pnrVo.agencies">
                                                                        <td style="text-align: center">{{agency.country}}</td>
                                                                        <td style="text-align: center">{{agency.identifier}}</td>
                                                                        <td style="text-align: center">{{agency.location}}</td>
                                                                        <td style="text-align: center">{{agency.name}}</td>
                                                                        <td style="text-align: center">{{agency.phone}}</td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr id="package1" class="accordion-toggle" data-parent="#OrderPackages"
                                                data-target=".packageDetails1">
                                                <td><strong>{{'pnr.baggage' | translate}} ({{ passenger.pnrVo.bagCount }})</strong></td>
                                                <td></td>
                                                <td>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="6">
                                                    <div class="accordion-body packageDetails1" id="accordion1">
                                                        <table class="table table-condensed">
                                                            <tr>

                                                            </tr>
                                                        </table>
                                                    </div>
                                                </td>
                                            </tr>

                                            <tr id="package1" class="accordion-toggle" data-parent="#OrderPackages"
                                                data-target=".packageDetails1">
                                                <td><strong>{{'pnr.seatinformation' | translate}}</strong></td>
                                                <td></td>
                                                <td>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="6">
                                                    <div class="accordion-body packageDetails1" id="accordion1">
                                                       <table class="table table-condensed"
                                                               style="border-collapse: separate; text-align: center;">
                                                            <thead>
                                                            <tr class="text-center" style="text-align: center">
                                                                <th style="text-align: center">{{'doc.name' | translate}}</th>
                                                                <th style="text-align: center">{{'flight.flight' | translate}}#</th>
                                                                <th style="text-align: center">{{'pnr.seat' | translate}}#</th>
                                                            </tr>
                                                            </thead>
                                                            <tbody>
                                                            <tr ng-repeat="sa in passenger.pnrVo.seatAssignments">
                                                                <td style="text-align: center">{{ sa.firstName }} {{ sa.lastName }}</td>
                                                                <td style="text-align: center">{{ sa.flightNumber }}</td>
                                                                <td style="text-align: center">{{ sa.number }}</td>
                                                            </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr></tr>
                                            </tbody>
                                        </table>
                                    </div>
                                    <!-- END of ORDERED PACKS -->
                                </div>
                            </div>
                        </div>
                        <!-- RIGHT DIV -->
                        <div class="col-md-offset-2 col-lg-6 col-lg-offset-0"
                             style="min-height: 200px; min-width: 40px;  max-height: 1300px; overflow: scroll;">
                            <table class="table table-striped">
                                <tbody>
                                <tr ng-repeat="i in passenger.pnrVo.rawList track by $index">
                                    <td><span>{{ $index+1 }} .&nbsp;&nbsp;&nbsp; {{ i }}</span></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </md-tab-body>
                </md-tab>
                <md-tab label="Disposition History">
	                <div class="span6" style="background-color: rgb(255, 255, 255, 0.15);">
	                	<div class="container">
	                	<div style="padding: 10px;"></div>
	                		<div class="col-sm-offset-2" style="padding-left:15px">
	                			<div style="display:inline-block">Disposition Status:</div>
	                			<md-input-container style="padding-left:5px">
                                <md-select ng-model="currentDispStatus">
                                	<md-option value=-1 selected="selected">N/A</md-option>
                                    <md-option ng-repeat="item in dispositionStatus" value="{{item.id}}">
                                        {{item.name}}
                                    </md-option>
                                </md-select>
                            </md-input-container>
	                		</div>
	                		<div class="col-sm-7 col-sm-offset-2">
	                			<textarea ng-model ="currentDispComments" rows="7" style="min-width:100%;resize:none"></textarea>
	                			<div style="float:right">
	  								<md-button type="submit"
					                      ng-disabled="currentDispComments === empty || currentDispComments === '' || currentDispStatus === '-1'"
					                      class="md-raised md-primary col-sm-offset-6" ng-click="saveDisposition()">
					               	<i class="glyphicon glyphicon-save"></i><i class="glyphicon glyphicon-flag"></i> Save
					           		</md-button>
				            	</div>
	                		</div>
	                	</div>
	                	<div style="padding:10px;"></div>
	                	<div>
	                		<h2 class="col-sm-offset-1"><strong>Disposition History:</strong></h2>
	                	</div>
	                	<div class="container" ng-repeat ="disp in passenger.dispositionHistory | orderBy:'createdAt':true">
	                	<div style="padding: 10px;"></div>
	                		<div>
	                			<md-input-container class="col-sm-offset-2" style="padding-left:15px">
	                                <md-select ng-model="passengerTempHistory" disabled>
	                                    <md-option selected=selected>
                                        	{{disp.status}}
	                                    </md-option>
	                                </md-select>
                            	</md-input-container>
	                		</div>
	                		<div class="col-sm-7 col-sm-offset-2">
	                			<textarea rows="7" style="min-width:100%;resize:none" disabled>{{disp.comments}}</textarea>
				            </div>
	                	</div>
	                </div>
                	</div>
                </md-tab>
                <md-tab label="Loading Flight History..." ng-disabled="isLoadingFlightHistory" ng-if="isLoadingFlightHistory">
                </md-tab>
                <md-tab label="{{ 'pass.flt.history' | translate}}" ng-if="!isLoadingFlightHistory">
                    <div class="span6" style="background-color: rgb(255, 255, 255, 0.15);">
                        <div class="container">
                            <div style="padding: 10px;"></div>
                            <div class="row">

                                <div class="row"
                                     ng-repeat-start="(key, value) in passenger.flightHistoryVo.flightHistoryMap">
                                    <div style="padding: 10px;"></div>
                                    <span class="col-sm-offset-1"
                                          style="margin-left:3.5%; font-weight: bold;">&nbsp;{{ 'doc.doc' | translate}}# : {{ key }}</span>

                                    <div style="padding: 15px;"></div>
                                    <table class="table table-striped">
                                        <thead>
                                        <tr class="text-center" style="text-align: center">
                                            <th style="text-align: center">{{ 'flight.carrieflight' | translate}}</th>
                                            <th style="text-align: center">{{ 'pass.eta' | translate}}</th>
                                            <th style="text-align: center">{{ 'pass.etd' | translate}}</th>
                                            <th style="text-align: center">{{ 'pass.origin' | translate}}</th>
                                            <th style="text-align: center">{{ 'pass.originairport' | translate}}</th>
                                            <th style="text-align: center">{{ 'pass.destination' | translate}}</th>
                                            <th style="text-align: center">{{ 'pass.originairport' | translate}}</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr ng-repeat="j in value track by $index">
                                            <td style="text-align: center">{{j.fullFlightNumber}}</td>
                                            <td style="text-align: center">{{j.eta | date:'MM/dd/yyyy HH:mm'}}</td>
                                            <td style="text-align: center">{{j.etd | date:'MM/dd/yyyy HH:mm'}}</td>
                                            <td style="text-align: center">{{j.originCountry}}</td>
                                            <td style="text-align: center">{{j.origin}}</td>
                                            <td style="text-align: center">{{j.destinationCountry}}</td>
                                            <td style="text-align: center">{{j.destination}}</td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <span ng-repeat-end></span>
                            </div>
                        </div>
                    </div>
                    <div style="padding: 30px;"></div>
                </md-tab>
            </md-tabs>
        </div>
    </section>
</div>
