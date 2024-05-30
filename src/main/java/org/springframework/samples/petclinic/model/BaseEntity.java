package org.springframework.samples.petclinic.model;

import java.io.Serializable;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * @MappedSuperclassを付与すると、
 * 上位クラスを継承したEntityクラスで共通カラムを使用することができる
 */

@MappedSuperclass
public class BaseEntity implements Serializable {
	
	/**
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)はエンティティの主キーidを自動採番できる
	 */
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	public Integer getId() {
		return this.id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * エンティティが既に存在するかしないかの確認
	 */
	
	public boolean isNew() {
		return this.id == null;
	}
}