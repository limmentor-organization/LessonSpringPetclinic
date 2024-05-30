package org.springframework.samples.petclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * BaseEntityを継承してidを引き継いだり、BaseEntityのメソッドなどが利用できるようにる
 */

@MappedSuperclass
public class NamedEntity extends BaseEntity {
	
	/**
	 * @Columnを利用してDB上での名前を"name"に設定している
	 */
	
	@Column(name = "name")
	private String name;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * StringのライブラリをOverrideして名前を取得する処理に変更している
	 */
	
	@Override
	public String toString() {
		return this.getName();
	}
}