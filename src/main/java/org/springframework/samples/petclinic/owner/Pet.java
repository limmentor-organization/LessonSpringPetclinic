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
	
	@Column(name = "birth_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;
	
	/**
	 *ペットとペットのタイプの関係を表現
	 *ペットが1つのタイプに関連図けられる
	 *@JoinColumnは外部キーとして使われるカラム名を指定
	 */
	
	@ManyToOne
	@JoinColumn(name = "type_id")
	private PetType type;
	
	/**
	 *ペットと診察の関係を示す
	 *@OneToManyアノテーションで1つのペットが複数の診療を所持できる
	 *@JoinColumn(name = "pet_id") は、診察のテーブルにおいてペットを参照するための外部キーのカラム名を指定
	 *@OrderBy("visit_date ASC") は、診察を診察日時の昇順でソートする
	 */
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "pet_id")
	@OrderBy("visit_date ASC")
	private Set<Visit> visits = new LinkedHashSet<>();
	
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
	
	public LocalDate getBirthDate() {
		return this.birthDate;
	}
	
	public PetType getType() {
		return this.type;
	}
	
	public void setType(PetType type) {
		 this.type = type;
	}
	
	public Collection<Visit> getVisits() {
		return this.visits;
	}
	
	public void addVisit(Visit visit) {
		this.getVisits().add(visit);
	}
}