package org.springframework.samples.petclinic.vet;

import java.util.Collection;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repositoryクラスを継承してVetエンティティとその主キーを指定
 */

public interface VetRepository extends Repository<Vet, Integer> {
	
	/**
	 * @Transactional(readOnly = true)は読み取り専用
	 * @Cacheable("vets")はメソッドの結果をキャッシュし、次回以降の同じメソッド呼び出し時にキャッシュされた結果を返す処理
	 */
	
	@Transactional(readOnly = true)
	@Cacheable("vets")
	Collection<Vet> findAll() throws DataAccessException;
	
	/**
	 * Page<Vet> findAll(Pageable pageable)：ページング情報を含むPageableオブジェクトを引数に取り、ページングされたVetエンティティのPageオブジェクトを返却
	 */
	
	@Transactional(readOnly = true)
	@Cacheable("vets")
	Page<Vet> findAll(Pageable pageable) throws DataAccessException;
}