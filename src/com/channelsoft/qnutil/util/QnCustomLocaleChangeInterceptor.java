package com.channelsoft.qnutil.util;

import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * <P>
 * 青牛自定义Local处理拦截器
 * </p>
 * 
 * @since 1.0.0
 * @version 1.0.0
 * @author tiankuo
 */
public class QnCustomLocaleChangeInterceptor extends LocaleChangeInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler)
			throws ServletException {
		String newLocale = "en_US";
		if (newLocale != null) {
			LocaleResolver localeResolver = RequestContextUtils
					.getLocaleResolver(request);
			if (localeResolver == null) {
				throw new IllegalStateException(
						"No LocaleResolver found: not in a DispatcherServlet request?");
			}
			LocaleEditor localeEditor = new LocaleEditor();
			localeEditor.setAsText(newLocale);
			localeResolver.setLocale(request, response,
					(Locale) localeEditor.getValue());
		}
		return true;
	}

}
