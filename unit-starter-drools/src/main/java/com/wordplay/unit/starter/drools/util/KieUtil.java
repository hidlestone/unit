package com.wordplay.unit.starter.drools.util;

import org.kie.api.runtime.KieContainer;

/**
 * @author zhuangpf
 */
public class KieUtil {

	private static KieContainer kieContainer;

	public static KieContainer getKieContainer() {
		return kieContainer;
	}

	public static void setKieContainer(KieContainer kieContainer) {
		KieUtil.kieContainer = kieContainer;
	}
	
}
