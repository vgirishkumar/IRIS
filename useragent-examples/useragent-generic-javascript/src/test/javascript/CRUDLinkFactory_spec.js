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
define(['cs!CRUDLinkFactory', 'cs!GETLink'], function(CRUDLinkFactory, GETLink){
	describe('the CRUDLinkFactory object TestCase',function(){
		
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
					oops = CRUDLinkFactory.CRUDLinkFactory();
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
					var linkFactory = new CRUDLinkFactory.CRUDLinkFactory();
					linkFactory.createLink(null, null);
				} catch(e) {
					thrown = e;
				}
				expect(thrown.constructor).toBe(String);
				expect(thrown).toBe('Precondition failed:  No model');
			});
			it('throws an exception if the createLink is called with no rel',function(){
				var thrown = null;
				try {
					var linkFactory = new CRUDLinkFactory.CRUDLinkFactory();
					var mockModel = {};
					mockModel.rel = null;
					linkFactory.createLink(null, mockModel);
				} catch(e) {
					thrown = e;
				}
				expect(thrown.constructor).toBe(String);
				expect(thrown).toBe('Precondition failed:  No model.rel');
			});
			it('Default GETLink should be creted if createLink is called with empty rel',function(){
				var linkFactory = new CRUDLinkFactory.CRUDLinkFactory();
				var mockModel = {};
				mockModel.href = 'root';
				mockModel.rel = '';
				var link = linkFactory.createLink(null, mockModel);
				expect(link).not.toBe(null);
				expect(link.constructor).toBe(GETLink.GETLink);
			});
		});
	
	});  // end describe
});  // end define