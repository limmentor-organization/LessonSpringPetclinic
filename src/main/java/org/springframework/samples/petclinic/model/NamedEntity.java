package org.springframework.samples.petclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/*
 * tips:
 * ・＠MappedSuperclass : 親クラスで共通カラムを管理したいときに使う
 * 　→ 上位クラスを継承したEntityクラスが、共通カラムを使用することができるようになる
 */
@MappedSuperclass
public class NamedEntity extends BaseEntity {

	// @Column : フィールドとカラムをマッピングする（name属性は指定した名前をDBのカラムとマッピングする）
	@Column(name = "name")
	private String name;

	// nameゲッター
	public String getName() {
		return this.name;
	}

	// nameセッター
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * tips : toStringをオーバーライドする意味
	 *・オーバーライドしないとObjectのtoStringが呼び出され、ハッシュ値が呼び出される
	 *・オーバーライドして返す値を明確化させる
	 */
	@Override
	public String toString() {
		return this.getName();
	}
}
