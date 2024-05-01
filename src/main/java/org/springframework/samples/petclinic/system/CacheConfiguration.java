package org.springframework.samples.petclinic.system;

import javax.cache.configuration.MutableConfiguration;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * <<<Spring bootのキャッシュ構成をカスタマイズするための設定クラス>>>
 * 
 * tips:
 * ●@Configurationアノテーションについて：
 * ・プログラム全体の設定に関する役割を担当するクラス（@Beanと一緒に用いられる ← プログラムの設定をするためのメソッドに付与）
 * ・proxyBeanMethods属性について：
 *  → trueの場合、Beanメソッドが呼び出された時に毎回インスタンスが生成される
 *  → falseの場合、既存のBeanが呼び出される
 *  
 * ●@EnableCachingについて：
 * ・キャッシュ管理機能を有効にする
 */
@Configuration(proxyBeanMethods = false)
@EnableCaching
public class CacheConfiguration {

	/*
	 * <<<vetsという名前のキャッシュを作成し、その設定をcacheConfigurationメソッドで定義されたキャッシュ構成を行う>>>
	 * 
	 * tips: JCacheManagerCustomizerについて：
	 * ・キャッシュマネージャーをカスタマイズするためのコールバックインターフェース
	 *  → コールバックインターフェースとは、あるイベントが発生した際に呼び出されるメソッドを定義するためのインターフェース
	 */
	@Bean
	public JCacheManagerCustomizer petclinicCacheConfigurationcustomizer() {
		// vetsのキャッシュを作成し、指定したキャッシュ構成を付与する
		return cm -> cm.createCache("vets", cacheConfiguration());
	}

	/*
	 * <<<キャッシュの構成を定義するメソッド>>>
	 * 
	 * tips: MutableConfigurationについて：
	 * ・キャッシュの動作や属性を設定するためのメソッドを提供するクラスのこと
	 * ・setStatisticsEnabledメソッドは、統計情報の収集が有効にでき、キャッシュのパフォーマンスや挙動を調べることができる
	 */
	private javax.cache.configuration.Configuration<Object, Object> cacheConfiguration() {
		// キャッシュの統計情報の収集を有効にする
		return new MutableConfiguration<>().setStatisticsEnabled(true);
	}
}
