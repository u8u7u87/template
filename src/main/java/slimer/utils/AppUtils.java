package slimer.utils;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

public class AppUtils implements ApplicationContextAware {

	/** 是否开启开发模式 */
	public static final boolean DEBUG_MODE = true;

	private Logger logger = LoggerFactory.getLogger(AppUtils.class);

	private static ApplicationContext applicationContext = null;

	public static ApplicationContext getApplicationContext() {
		return AppUtils.applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		AppUtils.applicationContext = applicationContext;
	}

	public static Map<String, Object> getBeanWithServiceAnnotaition() {
		return AppUtils.getBeansWithAnnotation(Service.class);
	}

	public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
		return AppUtils.applicationContext.getBeansWithAnnotation(annotationType);
	}

	public static <T> T getBean(Class<T> requiredType) {
		return AppUtils.applicationContext.getBean(requiredType);
	}

	public static Object getBean(String serviceid) {
		return AppUtils.getApplicationContext().getBean(serviceid);
	}

}