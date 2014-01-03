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
define(['cs!actions', 'cs!views'], function(actions, views){
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
				expect(actions.ActionFactory).toThrow('Remember to use new on constructors!');
			});
		});
	
		//Specs
		describe('createActions',function() {
			it('throws an exception if called with no model',function(){
				var thrown = null;
				try {
					var factory = new actions.ActionFactory();
					factory.createActions(null, null);
				} catch(e) {
					thrown = e;
				}
				expect(thrown.constructor).toBe(String);
				expect(thrown).toBe('Precondition failed:  No model');
			});
			it('throws an exception if called with no rel',function(){
				var thrown = null;
				try {
					var factory = new actions.ActionFactory();
					var mockModel = {};
					mockModel.rel = null;
					factory.createActions(null, mockModel);
				} catch(e) {
					thrown = e;
				}
				expect(thrown.constructor).toBe(String);
				expect(thrown).toBe('Precondition failed:  No model.rel');
			});
			it('creates a ViewAction if called with empty rel',function(){
				var factory = new actions.ActionFactory();
				var mockModel = {};
				mockModel.href = 'root';
				mockModel.rel = '';
				var action = factory.createActions(null, mockModel);
				expect(action).not.toBe(null);
				expect(action.constructor).toBe(actions.ViewAction);
			});
			it('creates a RefreshAction if called with an Action object instead of a link model',function(){
				var factory = new actions.ActionFactory();
				var mockView = new views.View();
				var mockAction = new actions.ViewAction(mockView, {rel: 'self', href: 'linkToSomewhere'});
				var action = factory.createActions(null, mockAction);
				expect(action).not.toBe(null);
				expect(action.constructor).toBe(actions.RefreshAction);
			});
		});
	
	});  // end describe
});  // end define