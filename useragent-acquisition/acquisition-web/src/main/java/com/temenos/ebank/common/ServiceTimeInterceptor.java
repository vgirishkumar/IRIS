package com.temenos.ebank.common;

import java.util.Date;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServiceTimeInterceptor implements MethodInterceptor {

	private static Log logger = LogFactory.getLog(ServiceTimeInterceptor.class); 

	/**
	 * On method invocation, this interceptor logs time spent during execution of intercepted method
	 * 
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		
		if( logger.isInfoEnabled() ){
			//log start time
			Date date = new Date();
			StringBuilder message = new StringBuilder().append( "Time interceptor: " ).append( methodInvocation.getMethod().getName() )
					.append(" started at ").append( DateFormatUtils.format(date, "HH:mm:ss.SSS") );
			/*
			 * it might be useful while looking at the console to know that the method is being invoked, but it is not
			 * relevant for post-execution log analysis. Unless we're investigating multi-thread issues.
			 */
//			logger.info(message );

			boolean successful = true;
			try {
				//execute intercepted method
				return methodInvocation.proceed();
			} catch (Exception e) {
				successful = false;
				throw e;
			} finally {
				//log end time
				Date endDate = new Date();
				Long timeSpentMillis = endDate.getTime() - date.getTime();
				logger.info(message.append(" executed in ").append( DurationFormatUtils.formatDurationHMS(timeSpentMillis) )
						.append(successful ? " successfully" : " with exception"));
			}
		}
		else{
			return methodInvocation.proceed();
		}
		
	}
}
