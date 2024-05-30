package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/owners/{ownerId}")
class PetController {
	
	private static final String VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";
	
	private final OwnerRepository owners;
	
	public PetController(OwnerRepository owners) {
		this.owners = owners;
	}
	
	/**
	 * @ModelAttributeで返却するオーナーのペットの種類をハンドラメソッドが呼ばれるたびにモデルに格納
	 */
	
	@ModelAttribute("types")
	public Collection<PetType> populatePetTypes() {
		return this.owners.findPetTypes();
	}
	
	/**
	 * 上と同様@ModelAttributeで返却するオーナーを格納
	 * ownerIdからオーナーを取得する
	 * ownerがnullの場合例外処理を実施
	 */
	
	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable("ownerId") int ownerId) {
		Owner owner = this.owners.findById(ownerId);
		if (owner == null) {
			throw new IllegalArgumentException("Owner Id not found:" + ownerId);
		}
		return owner;
	}
	
	/**
	 * 指定されたオーナーIDとペットIDに基づいてペットオブジェクトを返却する処理
	 */
	
	@ModelAttribute("pet")
	public Pet findPet(@PathVariable("ownerId") int ownerId, @PathVariable(name = "petId", required = false) Integer petId) {
		
		if (petId == null) {
			return new Pet();
		}
		
		Owner owner = this.owners.findById(ownerId);
		if (owner == null) {
			throw new IllegalArgumentException("Owner Id not found:" + ownerId);
		}
		return owner.getPet(petId);
	}
	
	@InitBinder("owner")
	public void initOwnerBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	/**
	 * データバインダーに対して、特定のバリデータ（PetValidator）を設定して、リクエストデータがバインドされる際に、PetValidatorが使用されてペットオブジェクトのバリデーションが行われる、
	 */
	
	@InitBinder("pet")
	public void initPetBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new PetValidator());
	}
	
	@GetMapping("/pets/new")
	public String initCreationForm(Owner owner, ModelMap model) {
		Pet pet = new Pet();
		owner.addPet(pet);
		model.put("pet", pet);
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}
	
	/**
	 * 新しいペットの作成フォームが送信された際に呼び出されるメソッド
	 * 最初の条件分岐はペットの名前が入力されていて、かつ新しいペットであり、かつ同じ名前のペットがすでに存在する場合に、"duplicate"エラーを追加。ペットの名前が重複している場合にエラーメッセージが表示される処理。
	 * 次の条件分岐は、ペットの誕生日が現在の日付よりも後の場合に、"typeMismatch.birthDate"エラーを追加します
	 */
	
	@PostMapping("/pets/new")
	public String processCreationForm(Owner owner, @Valid Pet pet, BindingResult result, ModelMap model,
			RedirectAttributes redirectAttribute) {
		if (StringUtils.hasText(pet.getName()) && pet.isNew() && owner.getPet(pet.getName(), true) != null) {
			result.rejectValue("name", "duplicate", "already exists");
		}
		
		LocalDate currentDate = LocalDate.now();
		if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}
		
		owner.addPet(pet);
		if (result.hasErrors()) {
			model.put("pet", pet);
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}
		
		this.owners.save(owner);
		redirectAttribute.addFlashAttribute("message", "New Pet has been Added");
		return "redirect:/owners/{ownerId}";
	}
	
	@GetMapping("/pets/{petId}/edit")
	public String initUpdateForm(Owner owner, @PathVariable("petId") int petId, ModelMap model,
			RedirectAttributes redirectAttributes) {
		Pet pet = owner.getPet(petId);
		model.put("pet", pet);
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}
	
	/**
	 * 既存のペットの更新フォームが送信された際に呼び出されるメソッド
	 * 最初の条件分岐では、ペットの名前が入力されている場合、同じ名前のペットがすでに存在するかどうかをチェックをする。存在する場合は、"duplicate"エラーを追加
	 * 次の条件分岐では、ペットの誕生日が現在の日付よりも後の場合、"typeMismatch.birthDate"エラーを追加
	 * 
	 */
	
	@PostMapping("/pets/{petId}/edit")
	public String processUpdateForm(@Valid Pet pet, BindingResult result, Owner owner, ModelMap model,
			RedirectAttributes redirectAttributes) {
		
		String petName = pet.getName();
		
		if (StringUtils.hasText(petName)) {
			Pet existingPet = owner.getPet(petName.toLowerCase(), false);
			if (existingPet != null && existingPet.getId() != pet.getId()) {
				result.rejectValue("name", "duplicate", "already exists");
			}
		}
		
		LocalDate currentDate = LocalDate.now();
		if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}
		
		if (result.hasErrors()) {
			model.put("pet", pet);
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}
		
		owner.addPet(pet);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "pet details has been edited");
		return "redirect:/owners/{ownerId}";
	}
}