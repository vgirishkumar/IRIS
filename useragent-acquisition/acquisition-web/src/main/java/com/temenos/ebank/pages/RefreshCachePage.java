package com.temenos.ebank.pages;

import org.apache.wicket.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.common.MethodCacheInterceptor;

public class RefreshCachePage extends BasePage {

	@SpringBean
	private MethodCacheInterceptor methodCacheInterceptor;

	@SuppressWarnings("rawtypes")
	public RefreshCachePage(){
		InjectorHolder.getInjector().inject(this);
		
		AjaxLink refreshDictionariesCache = new AjaxLink("refreshDictionaries") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				methodCacheInterceptor.clearCache();
			}
		};
		add(refreshDictionariesCache);
		AjaxLink refreshDBStringResources = new AjaxLink("refreshStringInDb"){

			@Override
			public void onClick(AjaxRequestTarget target) {
				Application.get().getResourceSettings().getPropertiesFactory().clearCache();
			}			
		};
		add(refreshDBStringResources);
	}

}
