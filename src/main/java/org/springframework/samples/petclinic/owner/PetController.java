package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.stereotype.Controller;
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
@RequestMapping("owners/{ownerId}")
public class PetController {
	// ペット情報登録/ペット情報更新画面パス
	private static final String VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";
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
	public PetController(OwnerRepository owners) {
		// Repository層導入
		this.owners = owners;
	}

	/*
	 * <<<DBからPetTypeListを取得して、typesとしてModel登録させるメソッド>>>
	 * 
	 * tips:
	 *●@ModelAttributeについて：
	 *・Handlerメソッドが実行される前に、このメソッドが実行されるようになる
	 *・引数の"owner"はmodel.addAttributeの第一引数と同じ
	 *
	 *●@PathVariableについて：
	 *・required属性はfalseにすることで、URLのパラメータの有無を考慮しなくてよくなる
	 * → @ModelAttributeのメソッドに付与することで、エンドポイントがパラメタのあるものとそうでないもののHandlerメソッド両方で扱えるようになる
	 */
	@ModelAttribute("types")
	public Collection<PetType> populatePetTypes() {
		// 全てのPetType情報を返す
		return this.owners.findPetTypes();
	}

	/*
	 * <<<DBからownerIdでOwnerを取得し、ownerとしてModel登録させるメソッド>>>
	 * 
	 * tips:
	 *●@ModelAttributeについて：
	 *・Handlerメソッドが実行される前に、このメソッドが実行されるようになる
	 *・引数の"owner"はmodel.addAttributeの第一引数と同じ
	 *
	 *●@PathVariableについて：
	 *・required属性はfalseにすることで、URLのパラメータの有無を考慮しなくてよくなる
	 * → @ModelAttributeのメソッドに付与することで、エンドポイントがパラメタのあるものとそうでないもののHandlerメソッド両方で扱えるようになる
	 */
	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable("ownerId") int ownerId) {

		// ownerIdでDBからOwner情報を取得する
		Owner owner = this.owners.findById(ownerId);
		// Owner情報がnullの場合true
		if (owner == null) {
			// エラーメッセージをスロー
			throw new IllegalArgumentException("Owner ID not found: " + ownerId);
		}
		// オーナー情報を返す
		return owner;
	}

	/*
	 * <<<DBからpetIdでPetを取得して、petとしてModel登録させるメソッド>>>
	 * 
	 * tips:
	 *●@ModelAttributeについて：
	 *・Handlerメソッドが実行される前に、このメソッドが実行されるようになる
	 *・引数の"owner"はmodel.addAttributeの第一引数と同じ
	 *
	 *●@PathVariableについて：
	 *・required属性はfalseにすることで、URLのパラメータの有無を考慮しなくてよくなる
	 * → @ModelAttributeのメソッドに付与することで、エンドポイントがパラメタのあるものとそうでないもののHandlerメソッド両方で扱えるようになる
	 */
	@ModelAttribute("pet")
	public Pet findPet(@PathVariable("ownerId") int ownerId,
			@PathVariable(name = "petId", required = false) Integer petId) {

		// petIdがnullの場合true
		if (petId == null) {
			// 生成したPetインスタンスを返す
			return new Pet();
		}

		// ownerIdでDBからOwner情報を取得する
		Owner owner = this.owners.findById(ownerId);
		// Owner情報がnullの場合true
		if (owner == null) {
			// エラーメッセージをスロー
			throw new IllegalArgumentException("Owner ID not found: " + ownerId);
		}
		// petIdでOwner情報からPet情報を取得し返す
		return owner.getPet(petId);
	}

	/*
	 * <<<Handlerメソッドの引数に対して処理するとき、idを除外する>>>
	 * 
	 * tips: @InitBinderについて
	 * ・@RequestMappingメソッドの全ての引数をサポートできるようになる
	 * ・引数のownerは、ModelAttributeのownerに紐づいている
	 * ・流れは以下のようになる
	 * 「パラメタ→@InitBinder→@ModelAttribute→@RequestMapping→ModelAndView」
	 */
	@InitBinder("owner")
	public void initOwnerBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	/*
	 * <<<Handlerメソッドの引数に対して処理するとき、PetValidatorを有効にするメソッド>>>
	 * 
	 * tips: @InitBinderについて
	 * ・@RequestMappingメソッドの全ての引数をサポートできるようになる
	 * ・引数のpetは、modelAttributeのpetに紐づいている
	 * ・流れは以下のようになる
	 * 「パラメタ→@InitBinder→@ModelAttribute→@RequestMapping→ModelAndView」
	 */
	@InitBinder("pet")
	public void intiPetBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new PetValidator());
	}

	/*
	 * <<<ペット情報登録画面に対して、GETリクエストを送るメソッド>>>
	 */
	@GetMapping("/pets/new")
	public String initCreationForm(Owner owner, ModelMap model) {
		// Petインスタンスを生成する
		Pet pet = new Pet();
		// PetインスタンスをOwnerに格納する
		owner.addPet(pet);
		// ペット情報をModelに登録する
		model.put("pet", pet);
		// ペット情報登録/ペット情報更新画面に遷移する
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	/*
	 * <<<ペット情報登録画面に対して、POSTリクエストを送るメソッド>>>
	 * 
	 * tips:
	 * ●@Validについて：
	 * ・フォーム内の各フィールドを検証し、データの有効性を確保する
	 * 
	 * ●RedirectAttributesについて：
	 * ・リダイレクト先にパラメータを送れるようになる
	 * ・オブジェクトを送る場合は、ModelMapに格納する必要がある
	 */
	@PostMapping("/pets/new")
	public String processCreationForm(Owner owner, @Valid Pet pet, BindingResult result, ModelMap model,
			RedirectAttributes redirectAttributes) {
		/*
		 * ●hasTextについて：
		 * ・Stringの文字列を含む場合は、trueを返す
		 * 
		 * ●rejectValueについて：
		 * ・フィールドにフィールドエラーを登録する
		 * ・第一引数：フィールド、第二引数：エラーコード
		 * 
		 *●if文の条件式について：
		 *・pet情報にNameの値が存在し、且つペット情報が最新であり、且つペット情報がnullでない場合はtrue
		 */
		if (StringUtils.hasText(pet.getName()) && pet.isNew() && owner.getPet(pet.getName(), true) != null) {
			// nameにエラーコードを登録する
			result.rejectValue("name", "duplicate");
		}

		// 現在時刻設定
		LocalDate currentDate = LocalDate.now();
		// PetのbirthDateがnullでない、且つbirthDateが現在時刻より後の場合true
		if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
			// birthDateにエラーコードを登録する
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}

		// Pet情報をOwnerに追加する
		owner.addPet(pet);
		// バリデーション実施
		if (result.hasErrors()) {
			// Pet情報をpetとしてmodelに登録する
			model.put("pet", pet);
			// ペット情報登録/ペット情報更新画面に遷移
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}

		// OwnerをDBに登録する
		this.owners.save(owner);
		// DBに登録成功した旨のメッセージをmodelに登録する
		redirectAttributes.addFlashAttribute("message", "New Pet has been Added");
		// オーナー詳細画面にリダイレクト
		return "redirect:/owners/{ownerId}";
	}

	// <<<ペット情報更新画面に対して、GETリクエストを送るメソッド>>>
	@GetMapping("pets/{petId}/edit")
	public String initUpdateForm(Owner owner, @PathVariable("petId") int petId, ModelMap model,
			RedirectAttributes redirectAttributes) {
		// petIdからPet情報を取得する
		Pet pet = owner.getPet(petId);
		// Pet情報をpetとしてModelに登録する
		model.put("pet", pet);
		// ペット情報登録/ペット情報更新画面のパス
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	/*
	 *  <<<ペット情報更新画面に対して、POSTリクエストを送るメソッド>>>
	 *  
	 *   * tips:
	 * ●@Validについて：
	 * ・フォーム内の各フィールドを検証し、データの有効性を確保する
	 * 
	 * ●RedirectAttributesについて：
	 * ・リダイレクト先にパラメータを送れるようになる
	 * ・オブジェクトを送る場合は、ModelMapに格納する必要がある
	 */
	@PostMapping("/pets/{petId}/edit")
	public String processupdateForm(@Valid Pet pet, BindingResult result, Owner owner, ModelMap model,
			RedirectAttributes redirectAttributes) {
		// ペットNameを取得する
		String petName = pet.getName();
		// petNameにString型の値が存在する場合true
		if (StringUtils.hasText(petName)) {
			// OwnerPet情報を取得する
			Pet existingPet = owner.getPet(petName.toLowerCase(), false);
			// Pet情報がnullでない、且つ入力フォームのpetIdとOwnerから取得したpetIdが異なる場合true
			if (existingPet != null && existingPet.getId() != pet.getId()) {
				// nameにエラーメッセージを登録する
				result.rejectValue("name", "duplicate", "already exists");
			}
		}

		// 現在の時刻を取得する
		LocalDate currentDate = LocalDate.now();
		// PetのbirthDateがnullでない、且つbirthDateが現在時刻より後の場合true
		if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
			// birthDateにエラーコードを登録する
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}

		// バリデーション実施
		if (result.hasErrors()) {
			// Pet情報をpetとしてModelに登録する
			model.put("pet", pet);
			// ペット情報登録/ペット情報更新画面パス
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}

		// OwnerにPet情報を登録する
		owner.addPet(pet);
		// Owner情報をDBに更新する
		this.owners.save(owner);
		// DBに登録成功した旨のメッセージをModelに登録する
		redirectAttributes.addFlashAttribute("message", "Pet details has been edited");
		// オーナー詳細画面にリダイレクト
		return "redirect:/owners/{ownerId}";
	}
}
