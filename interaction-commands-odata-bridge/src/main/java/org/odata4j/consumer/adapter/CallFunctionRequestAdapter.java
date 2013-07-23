package org.odata4j.consumer.adapter;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.core4j.Enumerable;
import org.joda.time.LocalDateTime;
import org.odata4j.core.Guid;
import org.odata4j.core.OFunctionParameter;
import org.odata4j.core.OFunctionParameters;
import org.odata4j.core.OFunctionRequest;
import org.odata4j.core.OObject;
import org.odata4j.core.OSimpleObjects;
import org.odata4j.core.UnsignedByte;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.producer.ODataProducer;
import org.odata4j.exceptions.NotImplementedException;

public class CallFunctionRequestAdapter<T> extends
        AbstractOQueryRequestAdapter<T> implements OFunctionRequest<T> {

  private final List<OFunctionParameter> params = new LinkedList<OFunctionParameter>();

  public CallFunctionRequestAdapter(ODataProducer producer,
          String serviceRootUri, String functionName) {
    super(producer, serviceRootUri);
  }

  @Override
  public Enumerable<T> execute() {
    throw new NotImplementedException("Not supported yet.");
  }

  // set parameters to the function call
  @Override
  public CallFunctionRequestAdapter<T> parameter(String name, OObject value) {
    params.add(OFunctionParameters.create(name, value));
    return this;
  }

  @Override
  public OFunctionRequest<T> pBoolean(String name, boolean value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.BOOLEAN, value));
  }

  @Override
  public OFunctionRequest<T> pByte(String name, UnsignedByte value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.BYTE, value));
  }

  @Override
  public OFunctionRequest<T> pSByte(String name, byte value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.SBYTE, value));
  }

  @Override
  public OFunctionRequest<T> pDateTime(String name, Calendar value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.DATETIME, value));
  }

  @Override
  public OFunctionRequest<T> pDateTime(String name, Date value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.DATETIME, value));
  }

  @Override
  public OFunctionRequest<T> pDateTime(String name, LocalDateTime value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.DATETIME, value));
  }

  @Override
  public OFunctionRequest<T> pDecimal(String name, BigDecimal value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.DECIMAL, value));
  }

  @Override
  public OFunctionRequest<T> pDouble(String name, double value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.DOUBLE, value));
  }

  @Override
  public OFunctionRequest<T> pGuid(String name, Guid value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.GUID, value));
  }

  @Override
  public OFunctionRequest<T> pInt16(String name, short value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.INT16, value));
  }

  @Override
  public OFunctionRequest<T> pInt32(String name, int value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.INT32, value));
  }

  @Override
  public OFunctionRequest<T> pInt64(String name, long value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.INT64, value));
  }

  @Override
  public OFunctionRequest<T> pSingle(String name, float value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.SINGLE, value));
  }

  @Override
  public OFunctionRequest<T> pTime(String name, Calendar value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.TIME, value));
  }

  @Override
  public OFunctionRequest<T> pTime(String name, Date value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.TIME, value));
  }

  @Override
  public OFunctionRequest<T> pTime(String name, LocalDateTime value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.TIME, value));
  }

  @Override
  public OFunctionRequest<T> pString(String name, String value) {
    return parameter(name, OSimpleObjects.create(EdmSimpleType.STRING, value));
  }

}
