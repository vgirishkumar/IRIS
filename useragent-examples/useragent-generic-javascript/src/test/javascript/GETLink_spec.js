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
define(['jquery', 'cs!GETLink'], function($, GETLink){

	describe('the GETLink object TestCase',function(){
		
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
					oops = GETLink.GETLink();
				} catch(e) {
					thrown = e;
				}
				expect(oops).toBe(null);
				expect(thrown).toBe('Remember to use new on constructors!');
			});
		});
	
		//Specs
		describe('Click handling',function() {
			it('Must override clicked',function(){
				var mockLinkModel = {};
				mockLinkModel.href = 'root';
				var link = new GETLink.GETLink(null, mockLinkModel);
				spyOn($, "ajax").andCallFake(function(options) {
					var mockResourceModel = {};
					var jqXHR = {};
					jqXHR.getResponseHeader = function() {};
			        options.success(
			        		mockResourceModel, 
			        		"textStatus", 
			        		jqXHR
			        );
			    });
				var result = link.hyperLink.click();
				expect(result[0].nodeName).toBe("A");
			    expect($.ajax.mostRecentCall.args[0]["url"]).toEqual("root");
			});
		});
	
		
	});  // end describe
});  // end define