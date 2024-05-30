package org.springframework.samples.petclinic.owner;

import org.springframework.util.StringUtils;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *PetValidatorはValidatorインターフェースを実装。
 */

public class PetValidator implements Validator {
	
	public static final String REQUIRED = "required";
	
	/**
	 *validateメソッドをoverride
	 *受け取ったオブジェクトをPetにキャストして名前を取得する。
	 *名前がからの場合、エラーを収集
	 *ペットが新しい（データベースにまだ保存されていない）場合は、種類が指定されていない場合にエラーを収集
	 *ペットの誕生日が指定されていない場合もエラーを収集
	 */
	
	@Override
	public void validate(Object obj, Errors errors) {
		Pet pet = (Pet)obj;
		String name = pet.getName();
		
		if (!StringUtils.hasText(name)) {
			errors.rejectValue("name", REQUIRED, REQUIRED);
		}
		
		if (pet.isNew() && pet.getType() == null) {
			errors.rejectValue("type", REQUIRED, REQUIRED);
		}
		
		if (pet.getBirthDate() == null) {
			errors.rejectValue("birthDate", REQUIRED, REQUIRED);
		}
	}
	
	/**
	 *supportメソッドをoverride
	 *対応するクラスをサポートするかどうかを判定する処理
	 *Petクラスをサポートするため、Pet.class.isAssignableFrom(clazz)を返却
	 */
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Pet.class.isAssignableFrom(clazz);
	}
}

