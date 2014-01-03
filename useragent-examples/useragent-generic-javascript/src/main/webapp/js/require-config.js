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
var require = {
//	CoffeeScript: {bare: true, runtime:'inline'},
//    baseUrl: "js",
    paths: {
        "cs": "vendor/cs",
        "coffee-script": "vendor/coffee-script",
        "underscore": "vendor/underscore-1.2.2.min",
        "jquery": "vendor/jquery-1.7.1.min",
        "jquery-datalink": "vendor/jquery.datalink-1.0.0pre"
    },
    shim: {
        underscore: {
          exports: '_'
        }
    },    
    waitSeconds: 15
};