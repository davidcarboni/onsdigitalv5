(function() {

    'use strict';

    //Contact Us Controller
    angular.module('onsTemplates')
        .controller('AboutCtrl', ['$scope', ContactUsController])

    function ContactUsController($scope) {
        $scope.breadcrumb = {
            parent: [],
            current: "About us"
        }
    }

})();