/*
 * #%L
 * useragent-generic-javascript
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
define([ 'jquery', 'cs!View', 'cs!GETLink' ], function( $, View, GETLink ){

	describe('the View object TestCase',function(){
		
		var rootId = 'testContainer';
	
		// Create an easily-removed container for our tests to play in
		beforeEach(function() {
			var container = document.createElement('div');
			container.setAttribute('id',rootId);		
			document.body.appendChild(container);
		});
		
		// Clean it up after each spec
		afterEach(function() {
			var container = document.getElementById(rootId);
			container.parentNode.removeChild(container);
		});
			
		// Constructor Specs
		describe('instantiation',function() {
			it('throws an exception if you forget "new"',function(){
				var thrown = null;
				var oops = null;
				try {
					oops = View.View(rootId);
				} catch(e) {
					thrown = e;
				}
				expect(oops).toBe(null);
				expect(thrown).toBe('Remember to use new on constructors!');
			});
		});
	
		//Specs
		describe('Test createLink',function() {
			it('Create a new link with a model',function(){
				view = new View.View(rootId);
				mockModel = {rel: 'self', href: 'linkToSomewhere'};
				link = view.createLink(null, mockModel);
				expect(link).not.toBe(mockModel);
			});
			it('Create a new link with a Link object',function(){
				view = new View.View(rootId);
				mockLink = new GETLink.GETLink(null, {rel: 'self', href: 'linkToSomewhere'});
				link = view.createLink(null, mockLink);
				expect(link).toBe(mockLink);
			});
		});

		
		
		// private convenience methods
//		var appendToContainer = function(element) {
//			var container = document.getElementById(rootId);
//			container.appendChild(element);		
//		};
	
	});  // end describe
});  // end define