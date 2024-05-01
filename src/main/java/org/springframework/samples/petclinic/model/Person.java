package org.springframework.samples.petclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;

/*
 * tips:
 * ・＠MappedSuperclass : 親クラスで共通カラムを管理したいときに使う
 * 　→ 上位クラスを継承したEntityクラスが、共通カラムを使用することができるようになる
 */
@MappedSuperclass
public class Person extends BaseEntity {

	// @Column : フィールドとカラムをマッピングする（name属性は指定した名前をDBのカラムとマッピングする）
	@Column(name = "first_name")
	@NotBlank
	private String firstName;

	@Column(name = "last_name")
	@NotBlank
	private String lastName;

	// firstNameゲッター
	public String getFirstName() {
		return this.firstName;
	}

	// firstNameセッター
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	// lastNameゲッター
	public String getLastName() {
		return this.lastName;
	}

	// lastNameセッター
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
