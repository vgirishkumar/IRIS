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
// Load and describe tests.
define(['cs!ResourceView'], function(ResourceView){
	describe('the ResourceView object TestCase',function(){
		
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
					oops = ResourceView.ResourceView({rel: 'self', href: 'api/', method: 'GET'}, rootId);
				} catch(e) {
					thrown = e;
				}
				expect(oops).toBe(null);
				expect(thrown).toBe('Remember to use new on constructors!');
			});
		});
	
		//Specs
	
		
		
		// private convenience methods
//		var appendToContainer = function(element) {
//			var container = document.getElementById(rootId);
//			container.appendChild(element);		
//		}
	
	});  // end describe
});  // end define