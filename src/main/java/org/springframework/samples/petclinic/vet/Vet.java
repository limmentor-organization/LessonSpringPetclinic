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

/**
*VetクラスはPersonを継承していて名前やidが使用可能になる
*@Table(name = "vets"):VetエンティティがDBのvetsと紐づけされる
*/

@Entity
@Table(name = "vets")
public class Vet extends Person {
	
	/**
	 *@ManyToMany(fetch = FetchType.EAGER): VetエンティティとSpecialtyエンティティが多対多の関係にあることを示す。一人の獣医師（Vet）が複数の専門分野（Specialty）を持つことができ、同時に一つの専門分野が複数の獣医師に関連付けられる
	 *@JoinTable: 中間テーブルvet_specialtiesを使って、vet_idとspecialty_idでVetとSpecialtyの関係を管理。name = "vet_specialties": 中間テーブルの名前をvet_specialtiesと指定
	 *joinColumns = @JoinColumn(name = "vet_id"): 中間テーブルのvet_id列が、Vetエンティティの主キーに対応することを意味する
	 *inverseJoinColumns = @JoinColumn(name = "specialty_id"): 中間テーブルのspecialty_id列が、Specialtyエンティティの主キーに対応することを意味する
	 *FetchType.EAGERは、Vetエンティティをデータベースから取得するときに、関連するSpecialtyエンティティも一緒にすぐに読み込む
	 */

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "vet_specialties", joinColumns = @JoinColumn(name = "vet_id"), 
		inverseJoinColumns = @JoinColumn(name = "specialty_id"))
	private Set<Specialty> specialties;

	protected Set<Specialty> getSpecialtiesInternal() {
		if (this.specialties == null) {
			this.specialties = new HashSet<>();
		}
		return this.specialties;
	}

	protected void setSpecialtiesInternal(Set<Specialty> specialties) {
		this.specialties = specialties;
	}
	
	/**
	 *@XmlElementを付与するとメソッドの戻り値がXMLの要素としてシリアライズされることを指定する
	 *PropertyComparator.sortメソッドを使用して、sortedSpecsリストをソート
	 *new MutableSortDefinition("name", true, true)はソートの定義
	 *Collections.unmodifiableListでリストを返却することで、値が変更不可になる
	 */

	@XmlElement
	public List<Specialty> getSpecialties() {
		List<Specialty> sortedSpecs = new ArrayList<>(getSpecialtiesInternal());
		PropertyComparator.sort(sortedSpecs, new MutableSortDefinition("name", true, true));
		return Collections.unmodifiableList(sortedSpecs);
	}

	public int getNrOfSpecialties() {
		return getSpecialtiesInternal().size();
	}

	public void addSpecialty(Specialty specialty) {
		getSpecialtiesInternal().add(specialty);
	}
}