package org.springframework.samples.petclinic.owner;

import org.springframework.samples.petclinic.model.NamedEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 *NamedEntityを継承してIdやnameプロパティを使用可能にする
 *@Table(name = "types")でtypesテーブルと結びつける
 */

@Entity
@Table(name = "types")
public class PetType extends NamedEntity {
	
}