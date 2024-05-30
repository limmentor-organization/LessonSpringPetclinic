package org.springframework.samples.petclinic.owner;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 *Repository<Owner, Integer>を継承してOwnerエンティティのCRUD処理を可能にする
 */

public interface OwnerRepository extends Repository<Owner, Integer> {
	
	/**
	 *PetTypeからペットの種類を取得する
	 *@Transactional(readOnly = true) は読み取り専用を意味する
	 */
	
	@Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
	@Transactional(readOnly = true)
	List<PetType> findPetTypes();
	
	/**
	 * 引数として渡された姓（lastName）に一致するオーナーをページングして取得
	 * ownerエンティティとownerエンティティのpetsフィールドを参照して値を取得している。またdistinct句て値の重複を避けている。
	 * 条件はlike句を使って条件を絞っている
	 */
	
	@Query("SELECT DISTINCT owner FROM Owner owner left join owner.pets WHERE owner.lastName LIKE :lastName%")
	@Transactional(readOnly = true)
	Page<Owner> findByLastName(@Param("lastName") String lastname, Pageable pageable);
	
	/**
	 *引数として渡されたIDに一致するオーナーを取得
	 */
	
	@Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id = :id")
	@Transactional(readOnly = true)
	Owner findById(@Param("id") Integer id);
	
	void save(Owner owner);
	
	/**
	 *ページングされた形式ですべてのオーナーを取得
	 */
	
	@Query("SELECT owner FROM Owner owner")
	@Transactional(readOnly = true)
	Page<Owner> findAll(Pageable pageable);
}