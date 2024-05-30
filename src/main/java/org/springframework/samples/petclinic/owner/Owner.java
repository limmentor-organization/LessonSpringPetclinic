package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.style.ToStringCreator;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.util.Assert;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;

/**
 * @Entityを付与してこのクラスがJPAエンティティであることを示す
 * @Tableを付与してこのエンティティがownersテーブルとマッピングされる
 */

@Entity
@Table(name = "owners")
public class Owner extends Person {
	
	/**
	 * @Columnでフィールド名と"address"が紐づけされる
	 * @NotBlankで空白、null,空文字使用不可
	 */
	
	@Column(name = "address")
	@NotBlank
	private String address;
	
	@Column(name = "city")
	@NotBlank
	private String city;
	
	/**
	 * @Digitsを利用して入力制限
	 * integer=10は整数10桁まで入力可能、fraction=0は少数を許可しない処理
	 */
	
	@Column(name = "telephone")
	@NotBlank
	@Digits(fraction = 0, integer = 10)
	private String telephone;
	
	/**
	 * @OneToManyはOnwerが複数のpetを所有していることを意味して、cascadeType.ALLは飼い主に対する操作がpetにも影響することを意味して、fetchType.Eagerは関連のEntityを即時にロードすることを意味する
	 * fetchの使い方は正直いまいちピンと来ていない
	 * ＠JoinColumnはこのフィールドがDBのowner_idと結び付くことを意味している、
	 * @OrderByを使ってペットを名前で並び変えることができる
	 */
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_id")
	@OrderBy("name")
	private List<Pet> pets = new ArrayList<>();
	
	public String getAddress() {
		return this.address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getCity() {
		return this.city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getTelephone() {
		return this.telephone;
	}
	
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public List<Pet> getPets() {
		return this.pets;
	}
	
	public void addPet(Pet pet) {
		if (pet.isNew()) {
			getPets().add(pet);
		}
	}
	
	/**
	 * 指定されたIDを持つペットをリストから取得する処理
	 * ペットが新しくない場合にチェックされる
	 */
	
	public Pet getPet(Integer id) {
		for (Pet pet: getPets()) {
			if (!pet.isNew()) {
				Integer compId = pet.getId();
				if (compId.equals(id)) {
					return pet;
				}
			}
		}
		return null;
	}
	
	/**
	 * 指定された名前を持つペットをリストから取得する処理
	 * ignoreNewがtrueの際、新しいペットも無視される
	 */
	
	public Pet getPet(String name, boolean ignoreNew) {
		name = name.toLowerCase();
		for (Pet pet: getPets()) {
			String compName = pet.getName();
			if (compName != null && compName.equalsIgnoreCase(name)) {
				if (!ignoreNew || !pet.isNew()) {
					return pet;
				}
			}
		}
		return null;
	}
	
	/**
	 * @overrideを使用してオブジェクトの文字表現を返却
	 * ToStringCreatorはオブジェクトの詳細をフォーマットしてくれるライブラリ
	 */
	
	@Override
	public String toString() {
		return new ToStringCreator(this)
				.append("id", this.getId())
				.append("new", this.isNew())
				.append("lastname", this.getLastName())
				.append("firstName", this.getFirstName())
				.append("address", this.address)
				.append("city", this.city)
				.append("telephone", this.telephone)
				.toString();
	}
	
	/**
	 * Assert.notNull(petId, "Pet identifier must not be null!");はpetIdがnullでないことを確認
	 * Assert.notNull(visit, "Visit must not be null!");は訪問情報がnullでないことを確認
	 * Assert.notNull(pet, "Invalid Pet identifier!"): はペットがnullでないことを確認
	 */
	
	public void addVisit(Integer petId, Visit visit) {
		
		Assert.notNull(petId, "Pet identifier must not be null!");
		Assert.notNull(visit, "Visit must not be null!");
		
		Pet pet = getPet(petId);
		
		Assert.notNull(pet, "Invalid Pet identifier!");
		
		pet.addVisit(visit);
	}
}