package org.springframework.samples.petclinic.owner;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/*
 * tips:
 * ・Repositoryを継承することで、継承したインターフェースはCRUD操作できるようになる
 * ・<Owner, Integer>は、Ownerはrepositoryが取り扱うentityの型を指定、Integerは主キーの型を指定してる
 */
public interface OwnerRepository extends Repository<Owner, Integer> {

	/*
	 * tips:
	 * ●SQLについて：
	 * ・「Owner owner」は「エンティティ 変数名」で定義してる
	 * ・「:id」は@Param("id")とマッピングしているので、引数に入ったidがそのまま「:id」に移る
	 * 
	 * ●@Transactionalについて
	 * ・メソッドをトランザクションに参加させることができる
	 * ・readOnly = trueで、読み込み専用にさせる
	 */

	// name順に全てのPetTypeレコードを取得する
	@Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
	@Transactional(readOnly = true)
	List<PetType> findPetTypes();

	// idによってOwnerレコードを取得する
	@Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id =:id")
	@Transactional(readOnly = true)
	Owner findById(@Param("id") Integer id);

	// lastNameの値を含むlastNameを持つOwnerレコードを取得する
	@Query("SELECT DISTINCT owner FROM Owner owner left join owner.pets WHERE owner.lastName LIKE :lastName%")
	@Transactional(readOnly = true)
	Page<Owner> findByLastName(@Param("lastName") String lastName, Pageable pageable);

	// Owner情報をDBに保存する
	void save(Owner owner);

	//Ownerレコードを全て取得する
	@Query("SELECT owner FROM Owner owner")
	@Transactional(readOnly = true)
	Page<Owner> findAll(Pageable pageable);
}
