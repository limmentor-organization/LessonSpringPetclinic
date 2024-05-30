package org.springframework.samples.petclinic.owner;

import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

/**
 *Formatter<PetType>を実装。PetTypeオブジェクトのフォーマットを制御し、文字列への変換と文字列からのパースを可能にする
 *@Componentはステレオタイプアノテーションでコンポネントスキャンの対象になる
 */

@Component
public class PetTypeFormatter implements Formatter<PetType> {
	
	private final OwnerRepository owners;
	
	public PetTypeFormatter(OwnerRepository owners) {
		this.owners = owners;
	}
	
	/**
	 *与えられたPetTypeオブジェクトを文字列に変換
	 */
	
	@Override
	public String print(PetType petType, Locale locate) {
		return petType.getName();
	}
	
	/**
	 *与えられた文字列をPetTypeオブジェクトに変換
	 *OwnerRepositoryから利用可能なすべてのペットの種類を取得して、受け取った文字列と一致すればpettypeオブジェクトを返却、無ければ例外をスローする
	 */
	
	@Override
	public PetType parse(String text, Locale locale) throws ParseException {
		Collection<PetType> findPetTypes = this.owners.findPetTypes();
		for (PetType type: findPetTypes) {
			if (type.getName().equals(text)) {
				return type;
			}
		}
		throw new ParseException("type not found: " + text, 0);
	}
}