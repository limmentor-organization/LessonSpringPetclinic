package org.springframework.samples.petclinic.vet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.samples.petclinic.model.Person;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlElement;

@Entity
@Table(name = "vets")
public class Vet extends Person {

	/*
	 * ・vet_specialtiesテーブルのフィールド
	 * 
	 * tips:
	 * ●@ManyToManyの属性について：
	 *・fetch属性：
	 * → EAGER : 対象テーブル取得時に関連テーブルも取得するSQLも実行する。
	 * → LAZY  : 関連テーブルのフィールドを参照した時に、関連テーブルを取得するSQLを実行する。
	 * 
	 * ●@JoinColumnについて：
	 *・外部キーカラムを指定する
	 *
	 * ●@JoinTableについて：
	 * ・テーブル結合する際に詳細設定を追加するために使う
	 * ・name属性：結合するテーブル名を指定（デフォルトは、2つのプライマリーテーブルの連結名）
	 * ・joinColumns属性：関連付けを所有するエンティティのプライマリテーブルを参照する結合テーブルの外部キー列
	 * ・inverseJoinColumns属性：関連付けを所有していないエンティティのプライマリテーブルを参照する結合テーブルの外部キー列
	 * 
	 * ●Setについて：
	 * ・Listの重複不可能バージョン
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "vet_specialties", joinColumns = @JoinColumn(name = "vet_id"), inverseJoinColumns = @JoinColumn(name = "specialty_id"))
	private Set<Specialty> specialties;

	// specialtiesのゲッター（nullの場合はHashSetインスタンス初期化）
	protected Set<Specialty> getSpecialtiesInternal() {
		// specialtiesがnullかどうか
		if (this.specialties == null) {
			// HashSetインスタインスを初期化しセット
			this.specialties = new HashSet<>();
		}
		// specialtiesを返す
		return this.specialties;
	}

	// specialtiesセッター
	protected void setSpecialtiesInternal(Set<Specialty> specialties) {
		// specialtiesをセットする
		this.specialties = specialties;
	}

	/*
	 * ・nameでソートされたspecialtiesを取得する
	 * 
	 * tips:
	 * ●PropertyComparator.sortについて：
	 * ・指定されたソートに従って、指定されたリストをソートする
	 * 
	 * ●Collections.unmodifiableListについて：
	 * ・リストを変更不可化させる
	 * 
	 * ●MutableSortDefinitionについて：
	 * ・ソートの定義をする
	 * ・第一引数：比較するプロパティ
	 * ・第二引数：文字列の大文字小文字を無視するかどうか
	 * ・第三引数：昇順（true）か降順（false）のどちらでソートするか
	 */
	@XmlElement
	public List<Specialty> getSpecialties() {
		// specialtiesの値が入ったListを作成する
		List<Specialty> sortedSpecs = new ArrayList<>(getSpecialtiesInternal());
		// リストをソートする（nameプロパティを大文字小文字無視して昇順でソート）
		PropertyComparator.sort(sortedSpecs, new MutableSortDefinition("name", true, true));
		// リストを変更不可にして返す
		return Collections.unmodifiableList(sortedSpecs);
	}

	// specialtiesのサイズを取得する
	public int getNrOfSpecialties() {
		return getSpecialtiesInternal().size();
	}

	// 専門分野名をspecialtiesに追加する
	public void addSpecialty(Specialty specialty) {
		getSpecialtiesInternal().add(specialty);
	}
}
