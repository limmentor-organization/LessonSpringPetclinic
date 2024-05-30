package org.springframework.samples.petclinic.system;

import javax.cache.configuration.MutableConfiguration;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Configurationはspringの設定クラスを意味する。このクラス内でBean定義を行う。
 * proxyBeanMethods =　falseはメソッド呼び出しのパフォーマンス向上のため、プロキシBeanメソッドを無効化する
 * @EnableCachingはSpringのキャッシュ機能を有効化する。これで@Cacheableなどのアノテーションが使用可能になる
 */

@Configuration(proxyBeanMethods = false)
@EnableCaching
class CacheConfiguration {

	/**
	 * @Beanを付記すると、このメソッドの結果がDIコンテナに格納される キャッシュマネージャcmに新しいキャッシュを作成。cacheConfiguration()で生成したものを、"vets"として返却している
	 */

	@Bean
	public JCacheManagerCustomizer petclinicCacheConfigurationCustomizer() {
		return cm -> cm.createCache("vets", cacheConfiguration());
	}
	
	/**
	 * キャッシュを定義するメソッド
	 * MutableConfigurationインスタンスを生成して、キャッシュの統計情報収集を有効化する
	 */

	private javax.cache.configuration.Configuration<Object, Object> cacheConfiguration() {
		return new MutableConfiguration<>().setStatisticsEnabled(true);
	}
}