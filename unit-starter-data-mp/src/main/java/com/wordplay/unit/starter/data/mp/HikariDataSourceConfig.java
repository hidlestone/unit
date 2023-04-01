package com.wordplay.unit.starter.data.mp;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * Hikari数据源配置
 *
 * @author zhuangpf
 */
@Configuration
@ConditionalOnClass(HikariDataSource.class)
@ConditionalOnMissingBean(DataSource.class)
@ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.zaxxer.hikari.HikariDataSource", matchIfMissing = true)
public class HikariDataSourceConfig {

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	@ConditionalOnProperty(name = {"platform.security.enable-database-password-encryption"}, havingValue = "true", matchIfMissing = true)
	public HikariDataSource dataSource(DataSourceProperties properties) throws Exception {
		// 密码AES解密，重新设置密码
//		properties.setPassword(EncryptionUtil.decryptAES(properties.getPassword(), DataStarterConstant.AES_KEY));
		HikariDataSource dataSource = (HikariDataSource) properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
		// 如果有设置name，则重新设置数据库连接池名称
		if (StringUtils.hasText(properties.getName())) {
			dataSource.setPoolName(properties.getName());
		}
		return dataSource;
	}

}
