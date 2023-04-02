package com.wordplay.unit.starter.drools.config;

import com.wordplay.unit.starter.drools.util.KieUtil;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.spring.KModuleBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

/**
 * 配置Drools的服务类，方便在Rest接口中调用。 该类负责加载具体的drl规则文件， 不再需要kmodule.xml配置文件了。
 *
 * @author zhuangpf
 */
@Configuration
public class DroolsConfig {

	/**
	 * 规则文件存放路径
	 */
	private static final String RULES_PATH = "drools/rules/";

	@Bean
	@ConditionalOnMissingBean(KieFileSystem.class)
	public KieFileSystem kieFileSystem() throws IOException {
		KieFileSystem kieFileSystem = getKieServices().newKieFileSystem();
		for (Resource file : getRuleFiles()) {
			kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_PATH + file.getFilename(), "UTF-8"));
		}
		return kieFileSystem;
	}

	/**
	 * 获取规则文件
	 */
	private Resource[] getRuleFiles() throws IOException {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		return resourcePatternResolver.getResources("classpath*:" + RULES_PATH + "**/*.*");
	}

	@Bean
	@ConditionalOnMissingBean(KieContainer.class)
	public KieContainer kieContainer() throws IOException {
		KieServices kieServices = getKieServices();
		final KieRepository kieRepository = kieServices.getRepository();
		kieRepository.addKieModule(new KieModule() {
			@Override
			public ReleaseId getReleaseId() {
				return kieRepository.getDefaultReleaseId();
			}
		});
		KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem());
		Results results = kieBuilder.getResults();
		if (results.hasMessages(Message.Level.ERROR)) {
			System.out.println(results.getMessages());
			throw new IllegalStateException("kieContainer error");
		}
		kieBuilder.buildAll();
		KieContainer kieContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
		KieUtil.setKieContainer(kieContainer);
		return kieContainer;
	}

	private KieServices getKieServices() {
		return KieServices.Factory.get();
	}

	@Bean
	@ConditionalOnMissingBean(KieBase.class)
	public KieBase kieBase() throws IOException {
		return kieContainer().getKieBase();
	}

	// 不能反复被使用，释放资源后需要重新获取。
	// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@Bean
	@ConditionalOnMissingBean(KieSession.class)
	public KieSession kieSession() throws IOException {
		return kieContainer().newKieSession();
	}

	@Bean
	@ConditionalOnMissingBean(KModuleBeanFactoryPostProcessor.class)
	public KModuleBeanFactoryPostProcessor kiePostProcessor() {
		return new KModuleBeanFactoryPostProcessor();
	}

}
