package org.springframework.samples.petclinic.owner;

import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

/*
 * tips: 
 * ●@Componentについて：
 * ・アプリ起動時に1回だけインスタンス化され、DIコンテナで管理され、必要な時に利用される
 * ・アプリ層からDB層までのプログラム全体をカバーするアノテーション
 * 　→ @Controllerなどのステレオタイプアノテーションの根底となるもの（@Controller等は@Componentを継承している）
 * 
 * ●Formatterについて：
 * ・Formatterインターフェースを実装すると、Spring MVCが必要な時に自動的に実装したメソッドを呼び出すことができる
 * ・今回はprintとparseメソッドを実装しているため、Spring MVCが自動的に呼び出すようになる
 */
@Component
public class PetTypeFormatter implements Formatter<PetType> {

	// Repository層変数宣言
	private final OwnerRepository owners;

	// コンストラクタインジェクション（spring推奨）
	/*
	 * メリット：
	 * ・フィールドがfinal宣言でき、イミュータブルなオブジェクトにしたり、
	 * 、必要な依存関係のみを不変にすることができる
	 * 
	 * ・循環依存を防ぐことができる
	 */
	@Autowired
	public PetTypeFormatter(OwnerRepository owners) {
		this.owners = owners;
	}

	/*
	 * <<<printメソッドの実装： petNameの値を返す>>>
	 * ・ペット情報登録/編集画面で、POSTリクエスト送信時にこのメソッドが呼び出される
	 */
	@Override
	public String print(PetType petType, Locale locale) {
		// petNameの値を返す
		return petType.getName();
	}

	/*
	 * <<<parseメソッドの実装： フォームから送信されたpetTypeをDBのpetTypeに変換する>>>
	 * ・ペット情報登録/編集画面で、GET,POSTリクエスト送信時にこのメソッドが呼び出される
	 */
	@Override
	public PetType parse(String text, Locale locale) throws ParseException {
		// DBからPetTypeを取得する
		Collection<PetType> findPetTypes = this.owners.findPetTypes();
		for (PetType type : findPetTypes) {
			// DBのPetTypeNameがフォーム入力情報のPetTypeNameと等しい場合true
			if (type.getName().equals(text)) {
				// petTypeNameを返す
				return type;
			}
		}
		// エラーメッセージをスローする
		throw new ParseException("type not found: " + text, 0);
	}
}
