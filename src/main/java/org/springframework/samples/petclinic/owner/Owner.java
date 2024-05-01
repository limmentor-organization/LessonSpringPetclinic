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

@Entity
@Table(name = "owners")
public class Owner extends Person {

	// @Column : フィールドとカラムをマッピングする（name属性は指定した名前をDBのカラムとマッピングする）
	@Column(name = "address")
	@NotBlank
	private String address;

	@Column(name = "city")
	@NotBlank
	private String city;

	/*
	 * tips: @Digitsについて
	 * ・integer属性は、整数の字数制限
	 * ・fraction属性は、小数点以下の字数制限
	 */
	@Column(name = "telephone")
	@NotBlank
	@Digits(fraction = 0, integer = 10)
	private String telephone;

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
	@JoinColumn(name = "owner_id")
	@OrderBy("name")
	private List<Pet> pets = new ArrayList<>();

	// addressゲッター
	public String getAddress() {
		return this.address;
	}

	// addressセッター
	public void setAddress(String address) {
		this.address = address;
	}

	// cityゲッター
	public String getCity() {
		return this.city;
	}

	// cityセッター
	public void setCity(String city) {
		this.city = city;
	}

	// telephoneゲッター
	public String getTelephone() {
		return this.telephone;
	}

	// telephoneセッター
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	// Petsゲッター
	public List<Pet> getPets() {
		return this.pets;
	}

	// Petsセッター
	public void addPet(Pet pet) {
		// ペット情報が新しい場合、Petsの追加する
		if (pet.isNew()) {
			// PetをListに追加する
			getPets().add(pet);
		}
	}

	// Petゲッター（名前から取得）
	public Pet getPet(String name) {
		// false : Pet情報が新しいかどうかの分岐を無視するよう設定
		return getPet(name, false);
	}

	// Petゲッター（idから取得）
	public Pet getPet(Integer id) {
		for (Pet pet : getPets()) {
			// Pet情報が新しくない場合true
			if (!pet.isNew()) {
				// petIdを取得する
				Integer compId = pet.getId();
				// petIdが指定したidと等しい場合true
				if (compId.equals(id)) {
					// Pet情報を返す
					return pet;
				}
			}
		}
		return null;
	}

	// Petゲッター（Pet情報の新旧を考慮しない場合はfalseをセット）
	public Pet getPet(String name, boolean ignoreNew) {
		// nameの値を小文字に変換する
		name = name.toLowerCase();
		for (Pet pet : getPets()) {
			String compName = pet.getName();
			// petのnameがnullでない、且つ大文字小文字区別せずに既存のnameがパラメタnameと一致する場合はtrue
			if (compName != null && compName.equalsIgnoreCase(name)) {
				// pet情報が新しくない場合はtrue
				if (!ignoreNew || !pet.isNew()) {
					return pet;
				}
			}
		}
		return null;
	}

	// toStringオーバーライド
	@Override
	public String toString() {
		// Owner情報が確認できるように設定
		return new ToStringCreator(this).append("id", this.getId())
				.append("new", this.isNew())
				.append("lastName", this.getLastName())
				.append("firstName", this.getFirstName())
				.append("address", this.address)
				.append("city", this.city)
				.append("telephone", this.telephone)
				.toString();
	}

	/*
	 * <<<PetにVisitを追加する>>>
	 * 
	 * tips: Assert.notNullについて
	 * ・第一引数のオブジェクトがnullでないことを検証し、nullであれば例外をスローする
	 * ・第二引数は例外が出た時のメッセージを設定
	 * ・nullチェックでプログラムの安全性や信頼性を向上させるためにも使われる
	 */
	public void addVisit(Integer petId, Visit visit) {
		// petIdのアサートチェック
		Assert.notNull(petId, "Pet identifier must not be null!");
		// visitのアサートチェック
		Assert.notNull(visit, "Visit must not be null!");
		// petIdを取得する
		Pet pet = getPet(petId);
		// Pet情報のアサートチェック
		Assert.notNull(pet, "Invalid Pet identifier!");
		// VisitにPetを追加する
		pet.addVisit(visit);
	}
}
