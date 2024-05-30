package org.springframework.samples.petclinic.vet;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @XmlRootElementはクラスがXMLのルート要素に対応することを示している。これによりJAXBを使用してオブジェクトをXMLに変換したり、XMLからオブジェクトに変換したりする際に、このクラスがXMLドキュメントのルート要素として使用される
 * 
 */

@XmlRootElement
public class Vets {
	
	private List<Vet> vets;
	
	/**
	 * @XmlElementによって、メソッドが返すリストがXMLの要素としてマッピングされる
	 */
	
	@XmlElement
	public List<Vet> getVetList() {
		if (vets == null ) {
			vets = new ArrayList<>();
		}
		return vets;
	}
	
}