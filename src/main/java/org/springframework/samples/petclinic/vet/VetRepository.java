package org.springframework.samples.petclinic.vet;

import java.util.Collection;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

/*
 * tips:
 * ・Repositoryを継承することで、継承したインターフェースはCRUD操作できるようになる
 * ・<Vet, Integer>は、Vetはrepositoryが取り扱うentityの型を指定、Integerは主キーの型を指定してる
 */
public interface VetRepository extends Repository<Vet, Integer> {

	/*
	 * tips:
	 * 
	 * ●@Transactionalについて：
	 * ・メソッドをトランザクションに参加させることができる
	 * ・readOnly = trueで、読み込み専用にさせる
	 * 
	 * @Cacheableについて：
	 * ・メソッドが呼び出された時に、その結果をキャッシュに保存し、同じ引数で再度呼び出された時は保存したキャッシュから結果を返す
	 * ・キャッシュは、高速なデータアクセスを可能にすることができ、計算結果の再利用もすることができる
	 */

	// DBからvetsテーブルのレコードを全て取得する
	@Transactional(readOnly = true)
	@Cacheable("vets")
	Collection<Vet> findAll() throws DataAccessException;

	// DBからvetsテーブルのレコードをPage付きで取得する
	@Transactional(readOnly = true)
	@Cacheable("vets")
	Page<Vet> findAll(Pageable pageable) throws DataAccessException;
}
