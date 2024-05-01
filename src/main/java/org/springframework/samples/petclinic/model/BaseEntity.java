package org.springframework.samples.petclinic.model;

import java.io.Serializable;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/*
 * tips:
 * ・@MappedSuperclass : 親クラスで共通カラムを管理したいときに使う
 * 　→ 上位クラスを継承したEntityクラスが、共通カラムを使用することができるようになる
 * 
 * ・Serializable : javaのインスタンスをbyte配列として出力できるようになる
 * 　→ インスタンスをファイルやメモリなどに保存できるようになる、サーバ再起動した際にセッション情報が保持されるようになる
 */
@MappedSuperclass
public class BaseEntity implements Serializable {

	/*
	 *  tips:
	 *  ・@Id : 主キーフィールドを宣言
	 *  
	 *  ・@GeneratedValue : プライマリキーカラムにユニークな値を自動で生成，付与する方法を指定する
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// idゲッター
	public Integer getId() {
		return id;
	}

	// idセッター
	public void setId(Integer id) {
		this.id = id;
	}

	// 新規作成idかの判別
	public boolean isNew() {
		return this.id == null;
	}
}
