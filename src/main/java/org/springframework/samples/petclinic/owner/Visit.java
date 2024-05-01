package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

	// @Column : フィールドとカラムをマッピングする（name属性は指定した名前をDBのカラムとマッピングする）
	@Column(name = "visit_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@NotBlank
	private String description;

	// Visitコンストラクタ
	public Visit() {
		// 現在日時を指定
		this.date = LocalDate.now();
	}

	// dateゲッター
	public LocalDate getDate() {
		return this.date;
	}

	// dateセッター
	public void setDate(LocalDate date) {
		this.date = date;
	}

	// descriptionゲッター
	public String getDescription() {
		return this.description;
	}

	//descriptionセッター
	public void setDescription(String description) {
		this.description = description;
	}
}
