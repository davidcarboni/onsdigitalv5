(function() {

    'use strict';

    //Contact Us Controller
    angular.module('onsTemplates')
        .controller('PrivacyCtrl', ['$scope', ContactUsController])

    function ContactUsController($scope) {
        $scope.breadcrumb = {
            parent: [],
            current: "Cookies and privacy"
        }
    }

})();