package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.NamedEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "pets")
public class Pet extends NamedEntity {

	// @Column : フィールドとカラムをマッピングする（name属性は指定した名前をDBのカラムとマッピングする）
	@Column(name = "birth_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;

	/*
	 * ●@JoinColumnについて：
	 *・外部キーカラムを指定する
	 */
	@ManyToOne
	@JoinColumn(name = "type_id")
	private PetType type;

	/*
	 * tips:
	 * ●@OneToManyの属性について：
	 *・cascade属性：
	 *  ALL            // 以下全てを選択
	 *	PERSIST        // 特定のエンティティを保存するとき、関連するエンティティを保存する。
	 *	MERGE          // 特定のエンティティを修正するとき、関連するエンティティも修正する。
	 *	REMOVE         // 特定のエンティを削除するとき、関連するオブジェクトを削除する。
	 *	REFRESH        // 特定のエンティティをEntity Managerで更新(refresh)するとき、関連するオブジェクトも更新する。
	 *	DETACH         // 特定のエンティティをEntityManagerから除外するとき(detach)、関連するオブジェクトも除外する。
	 *
	 *・fetch属性：
	 * → EAGER : 対象テーブル取得時に関連テーブルも取得するSQLも実行する。
	 * → LAZY  : 関連テーブルのフィールドを参照した時に、関連テーブルを取得するSQLを実行する。
	 * 
	 * ●@JoinColumnについて：
	 *・外部キーカラムを指定する
	 *
	 *●@OrderByについて
	 *・指定したフィールドに基づいてソートする
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "pet_id")
	@OrderBy("visit_date ASC")
	private Set<Visit> visits = new LinkedHashSet<>();

	// birthDateセッター
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	// birthDateゲッター
	public LocalDate getBirthDate() {
		return this.birthDate;
	}

	// typeゲッター
	public PetType getType() {
		return this.type;
	}

	// typeセッター
	public void setType(PetType type) {
		this.type = type;
	}

	// VisitSetゲッター
	public Collection<Visit> getVisits() {
		return this.visits;
	}

	// visitセッター
	public void addVisit(Visit visit) {
		getVisits().add(visit);
	}
}
