/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

 require.config({

//    baseUrl: "/scripts",

	 urlArgs : 'ver=build.version',
	
	/* starting point for application */
    deps: ['backbone.marionette', 'bootstrap', 'Main','globalize','utils/XALangSupport'],

    shim: {
        backbone: {
            deps: [ 'underscore', 'jquery'],
            exports: 'Backbone'
        },
        bootstrap: {
            deps: ['jquery'],
            exports: 'jquery'
        },
        backgrid : {
        	deps: [ 'backbone','backbone-fetch-cache']
        },
        'backgrid-paginator' : {
        	deps: [ 'backbone', 'backgrid' ]
        },
        'backgrid-filter' : {
        	deps: [ 'backbone', 'backgrid' ]
        },
        'backbone-forms.templates' : {
        	deps: [ 'backbone-forms.list', 'backbone-forms' ]
        },
        'backbone-forms.XAOverrides' : {
        	deps: [ 'backbone-forms' ]
        },
		'Backbone.BootstrapModal' : {
			deps : ['jquery', 'underscore', 'backbone']
		},
		'bootstrap-editable': {
           deps: ['bootstrap']
       },
       'jquery-toggles' : {
    	   deps: ['jquery']
       },
       'jquery.cookie': {
           deps: [ 'jquery' ]
       },
       'tag-it': {
           deps: [ 'jquery', 'jquery-ui' ]
       },
       'jquery-ui': {
           deps: [ 'jquery' ]
       },
       'globalize' :{
   			exports: 'Globalize'
   		},
   	   'bootstrap-datepicker' : {
   		   deps: ['bootstrap']
   	   } ,

       pnotify :	{ deps: ['jquery'], exports: 'pnotify' },
       moment  :	{ deps: ['jquery'], exports: 'moment' },
       'localstorage' :{
            deps : ['backbone','underscore','jquery']
       },
       'backbone-fetch-cache' :{
            deps : ['backbone','underscore','jquery']
       },
       'visualsearch': {
            deps: [ 'jquery', 'jquery-ui','backbone','underscore']
       },
       'select2' : { 
    	   deps: ['jquery', 'bootstrap'] 
       },
       'bootbox' : {
         deps: ['jquery'] 
       }       
    },

    paths: {
    	
        jquery						: '../libs/bower/jquery/js/jquery.min',
        'jquery-ui'					: '../libs/other/jquery-ui/js/jquery-ui-1.10.3.custom',
        backbone					: '../libs/bower/backbone-amd/js/backbone',
        underscore					: '../libs/bower/underscore-amd/js/underscore',

        /* alias all marionette libs */
        'backbone.marionette'		: '../libs/bower/backbone.marionette/js/backbone.marionette',
		'backbone.wreqr'			: '../libs/bower/backbone.wreqr/js/backbone.wreqr',
        'backbone.babysitter'		: '../libs/bower/backbone.babysitter/js/backbone.babysitter',

        /* alias the bootstrap js lib */
        bootstrap					: '../libs/bower/bootstrap/js/bootstrap',
        
        /* BackGrid for Tables */
        'backgrid'					: '../libs/other/backgrid/backgrid',
        'backbone-fetch-cache'		: '../libs/other/backbone.fetch-cache',
        
        'backgrid-paginator'		: '../libs/bower/backgrid-paginator/js/backgrid-paginator',
        'backgrid-filter'			: '../libs/bower/backgrid-filter/js/backgrid-filter',
       
        'backbone-pageable'			: '../libs/bower/backbone-pageable/js/backbone-pageable',
		'localstorage'				: '../libs/bower/backbone.localstorage/backbone.localStorage',
        'backbone-forms'			: '../libs/bower/backbone-forms/js/backbone-forms',
        'backbone-forms.list'		: '../libs/bower/backbone-forms/js/list',
        'backbone-forms.templates'	: '../libs/bower/backbone-forms/js/bootstrap',
        'backbone-forms.XAOverrides': '../libs/fsOverrides/BBFOverrides',

		'Backbone.BootstrapModal'	: '../libs/bower/backbone.bootstrap-modal/js/backbone.bootstrap-modal',
		'bootstrap-editable'		: '../libs/bower/x-editable/js/bootstrap-editable',
		'bootstrap-datepicker'		: '../libs/other/datepicker/js/bootstrap-datepicker',
		
		'jquery.cookie'				: '../libs/other/jquery-cookie/js/jquery.cookie',
        'jquery-toggles'			: '../libs/bower/jquery-toggles/js/toggles.min',
        'tag-it'					: '../libs/bower/tag-it/js/tag-it',
        'select2'					: '../libs/bower/select2/select2',
        'bootbox'					: '../libs/bower/bootbox/js/bootbox',
        'pnotify'					: '../libs/bower/pines-notify/js/jquery.pnotify.min',
        'moment'					: '../libs/bower/moment/js/moment-with-langs.min',
        'visualsearch'				: '../libs/other/visualsearch/js/visualsearch',
        'globalize'               	: '../libs/bower/globalize/lib/globalize',
        /* handlebars from the require handlerbars plugin below */
        'handlebars'				: '../libs/bower/require-handlebars-plugin/js/Handlebars',
        /* require handlebars plugin - Alex Sexton */
        'i18nprecompile'			: '../libs/bower/require-handlebars-plugin/js/i18nprecompile',
        'json2'						: '../libs/bower/require-handlebars-plugin/js/json2',
        'hbs'						: '../libs/bower/require-handlebars-plugin/js/hbs',

        'tmpl'						: '../templates'
    },

    hbs: {
		disableI18n: true,
        helperPathCallback:         // Callback to determine the path to look for helpers
            function (name) {       // ('/template/helpers/'+name by default)
        		return "../helpers/XAHelpers";
        },
        templateExtension: "html", 
        compileOptions: {} 
    }
});
