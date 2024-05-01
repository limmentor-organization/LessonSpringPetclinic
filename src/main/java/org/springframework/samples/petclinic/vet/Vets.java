package org.springframework.samples.petclinic.vet;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/*
 * tips:@XmlRootElementについて
 * ・xmlのルート要素であることを指定
 */
@XmlRootElement
public class Vets {

	// vetsリストを定義
	private List<Vet> vets;

	// フィールドをxmlタグへマッピング
	@XmlElement
	public List<Vet> getVetList() {
		// vetsがnullかどうか
		if (vets == null) {
			// ArrayListインスタンスを生成
			vets = new ArrayList<>();
		}
		// vetsリストを返す
		return vets;
	}
}
