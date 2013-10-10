package org.odata4j.test.unit;

/*
 * #%L
 * interaction-commands-odata-bridge
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


import junit.framework.Assert;

import org.core4j.Func;
import org.odata4j.core.OFuncs;
import org.odata4j.core.Throwables;

public class Asserts {

  public static void assertThrows(Class<?> expectedThrowableType, Runnable code) {
    try {
      code.run();
    } catch (Throwable t) {
      if (expectedThrowableType.equals(t.getClass()))
        return;
      Throwables.propagate(t);
    }
    Assert.fail(String.format("Expected %s to be thrown, but nothing thrown", expectedThrowableType.getSimpleName()));
  }

  public static void assertThrows(Class<?> expectedThrowableType, Func<?> code) {
    assertThrows(expectedThrowableType, OFuncs.asRunnable(code));
  }

}
