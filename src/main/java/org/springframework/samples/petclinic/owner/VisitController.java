package org.springframework.samples.petclinic.owner;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
class VisitController {
	
	private final OwnerRepository owners;
	
	public VisitController(OwnerRepository owners) {
		this.owners = owners;
	}
	
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	/**
	 *@ModelAttributeアノテーションを使用して、ペットと関連する訪問情報を準備
	 *指定されたオーナーIDとペットIDに基づいてオーナーとペットが検索しモデルに格納する処理
	 */
	
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId,
			Map<String, Object> model) {
		Owner owner = this.owners.findById(ownerId);
		
		Pet pet = owner.getPet(petId);
		model.put("pet", pet);
		model.put("owner", owner);
		
		Visit visit = new Visit();
		pet.addVisit(visit);
		return visit;
	}
	
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String initNewVisitForm() {
		return "pets/createOrUpdateVisitForm";
	}
	
	/**
	 *新しい訪問情報を処理
	 *フォームにバリデーションエラーがない場合は、訪問情報がオーナーに追加され、変更がDBに格納される処理
	 */
	
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@ModelAttribute Owner owner, @PathVariable int petId, @Valid Visit visit,
			BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}
		
		owner.addVisit(petId, visit);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
		return "redirect:/owners/{ownerId}";
	}
}