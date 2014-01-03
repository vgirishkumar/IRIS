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
define([ 'cs!Link' ], function( Link ){

	describe('the Link object TestCase',function(){
		
		// initialise environment for tests
		beforeEach(function() {
	
		});
		
		// Clean it up after each spec
		afterEach(function() {
	
		});
			
		//Specs
		describe('instantiation',function() {
			it('throws an exception if you forget "new"',function(){
				var thrown = null;
				var oops = null;
				try {
					oops = Link.Link();
				} catch(e) {
					thrown = e;
				}
				expect(oops).toBe(null);
				expect(thrown).toBe('Remember to use new on constructors!');
			});
			it('throws an exception if the link is constructed with no model',function(){
				var thrown = null;
				var link = null;
				try {
					link = new Link.Link(null, null);
				} catch(e) {
					thrown = e;
				}
				expect(link).toBe(null);
				expect(thrown.constructor).toBe(String);
				expect(thrown).toBe('Precondition failed:  No model');
			});
			it('throws an exception if the link is constructed with no href',function(){
				var thrown = null;
				var link = null;
				try {
					var mockModel = {};
					mockModel.href = null;
					link = new Link.Link(null, mockModel);
				} catch(e) {
					thrown = e;
				}
				expect(link).toBe(null);
				expect(thrown.constructor).toBe(String);
				expect(thrown).toBe('Precondition failed:  No model.href');
			});
			it('throws an exception if the link is constructed with href undefined',function(){
				var thrown = null;
				var link = null;
				try {
					var mockModel = {};
					link = new Link.Link(null, mockModel);
				} catch(e) {
					thrown = e;
				}
				expect(link).toBe(null);
				expect(thrown.constructor).toBe(String);
				expect(thrown).toBe('Precondition failed:  No model.href');
			});
			it('throws an exception if the link is constructed with no method',function(){
				var thrown = null;
				var link = null;
				try {
					var mockModel = {};
					mockModel.href = 'root';
					mockModel.method = null;
					link = new Link.Link(null, mockModel);
				} catch(e) {
					thrown = e;
				}
				expect(link).toBe(null);
				expect(thrown.constructor).toBe(String);
				expect(thrown).toBe('Precondition failed:  No model.method');
			});
			it('throws an exception if the link is constructed with method undefined',function(){
				var thrown = null;
				var link = null;
				try {
					var mockModel = {};
					mockModel.href = 'root';
					link = new Link.Link(null, mockModel);
				} catch(e) {
					thrown = e;
				}
				expect(link).toBe(null);
				expect(thrown.constructor).toBe(String);
				expect(thrown).toBe('Precondition failed:  No model.method');
			});
		});
	
		//Specs
		describe('Anchor generation',function() {
			it('Use model.name in anchor text',function(){
				var mockModel = {};
				mockModel.href = 'root';
				mockModel.name = 'Fish';
				var link = new Link.Link(null, mockModel, 'GET');
				expect(link).not.toBe(null);
				expect(link.hyperLink.text()).toBe('GET-Fish');
			});
			it('Use href in anchor text if model.name is null',function(){
				var mockModel = {};
				mockModel.href = 'root';
				mockModel.name = null;
				var link = new Link.Link(null, mockModel, 'GET');
				expect(link).not.toBe(null);
				expect(link.hyperLink.text()).toBe('GET-root');
			});
			it('Use href in anchor text if model.name is undefined',function(){
				var mockModel = {};
				mockModel.href = 'root';
				var link = new Link.Link(null, mockModel, 'GET');
				expect(link).not.toBe(null);
				expect(link.hyperLink.text()).toBe('GET-root');
			});
		});
		describe('Click handling',function() {
			it('Must override clicked',function(){
				var mockModel = {};
				mockModel.href = 'root';
				var link = new Link.Link(null, mockModel, 'GET');
				var thrown = null;
				try {
					link.hyperLink.click();
				} catch(e) {
					thrown = e;
				}
				expect(thrown.constructor).toBe(String);
				expect(thrown).toBe('Not implemented');
			});
		});
		describe('Trigger handling',function() {
			it('Must provide successHandler',function(){
				var mockModel = {};
				mockModel.href = 'root';
				var link = new Link.Link(null, mockModel, 'GET');
				var thrown = null;
				try {
					link.trigger();
				} catch(e) {
					thrown = e;
				}
				expect(thrown.constructor).toBe(String);
				expect(thrown).toBe("'successHandler' must be defined before calling trigger");
			});
			it('No call to trigger if clicked returns false',function(){
				var mockModel = {};
				mockModel.href = 'root';
				var link = new Link.Link(null, mockModel, 'GET');
				spyOn(link, "clicked").andCallFake(function() {
			        return false;
			    });
				var result = link.click();
				expect(result).toBe(false);  // don't refresh the window
			});
		});
	
		
	});  // end describe
});  // end define