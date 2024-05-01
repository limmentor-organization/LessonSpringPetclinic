package org.springframework.samples.petclinic.owner;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PetValidator implements Validator {
	// フィールドエラーを登録する際のエラーコード/メッセージを設定
	private static final String REQUIRED = "required";

	@Override
	public void validate(Object obj, Errors errors) {
		// Pet情報設定
		Pet pet = (Pet) obj;
		// petName取得
		String name = pet.getName();

		// 指定したnameが文字列を含む場合true
		if (!StringUtils.hasText(name)) {
			// nameフィールドのフィールドエラーを登録
			errors.rejectValue("name", REQUIRED, REQUIRED);
		}

		// Pet情報が新しい且つtypeがnullの場合true
		if (pet.isNew() && pet.getType() == null) {
			// typeフィールドのフィールドエラーを登録
			errors.rejectValue("type", REQUIRED, REQUIRED);
		}

		// birthDateがnullの場合true
		if (pet.getBirthDate() == null) {
			// typeフィールドのフィールドエラーを登録
			errors.rejectValue("birthDate", REQUIRED, REQUIRED);
		}
	}

	/*
	 * tips: supportsメソッドについて：
	 * ・バリデータが検証を行うクラスのオブジェクト型を受け取り、その型がこのバリデータがサポートしている型かどうかを検証する
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		// Petとclazzを比較する
		return Pet.class.isAssignableFrom(clazz);
	}
}
