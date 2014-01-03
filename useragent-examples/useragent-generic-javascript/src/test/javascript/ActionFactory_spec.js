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
// Load test target and describe tests.
define(['cs!actions'], function(actions){
	describe('the ActionFactory object TestCase',function(){
		
		// Run before our tests
		beforeEach(function() {
		});
		
		// Clean it up after each spec
		afterEach(function() {
		});
			
		// Constructor Specs
		describe('instantiation',function() {
			it('throws an exception if you forget "new"',function(){
				var thrown = null;
				var oops = null;
				try {
					oops = actions.ActionFactory();
				} catch(e) {
					thrown = e;
				}
				expect(oops).toBe(null);
				expect(thrown).toBe('Remember to use new on constructors!');
			});
		});
	
		//Specs
		describe('createLink',function() {
			it('throws an exception if the createLink is called with no model',function(){
				var thrown = null;
				try {
					var factory = new actions.ActionFactory();
					factory.createLink(null, null);
				} catch(e) {
					thrown = e;
				}
				expect(thrown.constructor).toBe(String);
				expect(thrown).toBe('Precondition failed:  No model');
			});
			it('throws an exception if the createLink is called with no rel',function(){
				var thrown = null;
				try {
					var factory = new actions.ActionFactory();
					var mockModel = {};
					mockModel.rel = null;
					factory.createLink(null, mockModel);
				} catch(e) {
					thrown = e;
				}
				expect(thrown.constructor).toBe(String);
				expect(thrown).toBe('Precondition failed:  No model.rel');
			});
			it('Default GETLink should be creted if createLink is called with empty rel',function(){
				var factory = new actions.ActionFactory();
				var mockModel = {};
				mockModel.href = 'root';
				mockModel.rel = '';
				var link = factory.createLink(null, mockModel);
				expect(link).not.toBe(null);
				expect(link.constructor).toBe(actions.ViewAction);
			});
		});
	
	});  // end describe
});  // end define