package com.temenos.interaction.test;

/*
 * #%L
 * interaction-test
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


import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Run all JUnit test cases twice. Once for Jersey and once for CXF runtime.
 */
@RunWith(Parameterized.class)
public abstract class AbstractRuntimeTest {

  private final Logger log = Logger.getLogger(this.getClass().getName());

  protected Logger getLogger() {
    return this.log;
  }

  protected enum RuntimeFacadeType {
    JERSEY, CXF
  }

  public AbstractRuntimeTest(RuntimeFacadeType type) {
    switch (type) {
    case JERSEY:
      this.rtFacade = new JerseyRuntimeFacade();
      break;
    case CXF:
      this.rtFacade = new CxfRuntimeFacade();
      break;
    default:
      throw new RuntimeException("JAX-RS runtime type not supported: " + type);
    }
    this.getLogger().info("******************************************************************");
    this.getLogger().info("Activated Runtime Facade: " + type);
    this.getLogger().info("******************************************************************");
  }

  @Parameterized.Parameters
  public static List<Object[]> data() {
    // TODO enable CXF as soon as implementation is completed and all test cases are green
    Object[][] a = new Object[][] { { RuntimeFacadeType.JERSEY } /*, { RuntimeFacadeType.CXF } */ };
    return Arrays.asList(a);
  }

  protected RuntimeFacade rtFacade;

}
