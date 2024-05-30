package org.springframework.samples.petclinic.owner;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
class OwnerController {
	
	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";
	
	private final OwnerRepository owners;
	
	public OwnerController(OwnerRepository clinicService) {
		this.owners = clinicService;
	}
	
	/**
	 *@InitBinderはコントローラー内のデータバインディング検証のカスタマイズに利用される。、ここではWebDataBinderを利用してsetDisallowedFieldsメソッドでIDをキーにして、
	 *フォームから送信されたidフィールドを変更できないようにしている
	 */
	
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	/**
	 * ここでの@ModelAttributeはController内のハンドラメソッドが呼ばれるたびに、
	 * このfindOwnerメソッドが呼ばれてurlから取得されるOnwerIdの値に応じてオーナーオブジェクトを生成するか、Ownerを検索するして"owner"という名でモデルに追加される
	 */
	
	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
		return ownerId == null ? new Owner() : this.owners.findById(ownerId);
	}
	
	/**
	 * model.put("owner", owner);の部分は解体新書で学んだ、model.addAttribute("key",value)同様の処理
	 * Ownerオブジェクトをmodelに格納している
	 */
	
	@GetMapping("/owners/new")
	public String initCreationForm(Map<String, Object> model) {
		Owner owner = new Owner();
		model.put("owner", owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}
	
	/**
	 * オーナ作成フォームの送信処理
	 * フォームに不備がある場合、フォームヴューを再表示させ、エラーメッセージもRedirectAttributesを使って表示させる
	 * フォームに問題なければ、オーナー情報を保存して、オーナー詳細ページにリダイレクトする
	 */
	
	@PostMapping("/owners/new")
	public String processCreationForm(@Valid Owner owner, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in creating the owner");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}
		
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "New Owner created");
		return "redirect:/owners/" + owner.getId();
	}
	
	@GetMapping("/owners/find")
	public String initFindForm() {
		return "owners/findOwners";
	}
	
	/**
	 * @RequestParam(defaultValue = "1")はリクエストパラメータからページ番号を取得して、指定がない場合、1ページがデフォルトで設定される。
	 */
	
	@GetMapping("/owners")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result, Model model) {
		if (owner.getLastName() == null) {
			owner.setLastName("");
		}
		
		/**
		 * @findPaginatedForOwnersLastNameメソッドを呼び出して、指定されたページと姓で検索を行ってownerResultsに格納
		 */
		
		Page<Owner> ownerResults = findPaginatedForOwnersLastName(page, owner.getLastName());
		
		/**
		 * 検索結果がからの場合、BindingResultにエラーメッセージを追加してowners/findOwnersを返却
		 */
		
		if (ownerResults.isEmpty()) {
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		}
		
		/**
		 * 検索結果が一件の場合、そのオーナーの詳細ページにリダイレクトしする処理
		 */
		
		if (ownerResults.getTotalElements() == 1) {
			owner = ownerResults.iterator().next();
			return "redirect:/owners/" + owner.getId();
		}
		
		/**
		 * 検索結果が複数件の場合、ページネーションの情報をモデルに追加するためにaddPaginationModelメソッドを呼び出して、その結果のヴューを返却する
		 */
		
		return addPaginationModel(page, model, ownerResults);
	}
	
	/**
	 * ページネーションの情報をモデルに追加する処理
	 * listOwnersは現在のページに表示されるオーナーのリストを取得する
	 * モデルに今のページ、総ページ数、総項目数、オーナーのリストを追加する
	 * owners/ownersListヴューの返却
	 */
	
	private String addPaginationModel(int page, Model model, Page<Owner> paginated) {
		List<Owner> listOwners = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listOwners", listOwners);
		return "owners/ownersList";
	}
	
	/**
	 * ページネーションされたオーナーのリストを取得する処理
	 * 1ページに表示するオーナーの数を5に設定
	 * PageRequest.of(page - 1, pageSize)を使用してPageableオブジェクトを作成
	 * ページ番号は0から開始するのでpage-1で表現
	 */
	
	private Page<Owner> findPaginatedForOwnersLastName(int page, String lastname) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return owners.findByLastName(lastname, pageable);
	}
	
	/**
	 * @PathVariableから取得したownerIdを使用して、ownerを検索
	 * 取得したownerをモデルに格納
	 */
	
	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, Model model) {
		Owner owner = this.owners.findById(ownerId);
		model.addAttribute(owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}
	
	/**
	 * owner情報をアップデートする処理
	 * フォームに誤りがある場合、VIEWS_OWNER_CREATE_OR_UPDATE_FORMを返却してredirectAttributesエラーメッセージを出力する
	 * フォームに不備が無ければ、OwnerＩｄをownerのidにセットしてDBに登録する
	 */
	
	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result, @PathVariable("ownerId") int ownerId,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in updating the owner");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}
		
		owner.setId(ownerId);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Owner Values Updated");
		return "redirect:/owners/{ownerId}";
	}
	
	/**
	 * owners/ownerDetailsビューを表示するためのModelAndViewオブジェクトを作成
	 * 指定されたownerIdに対応するオーナーをDBから検索
	 * ownerオブジェクトがビューに渡され、ModelAndViewオブジェクトを返却
	 */
	
	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		Owner owner = this.owners.findById(ownerId);
		mav.addObject(owner);
		return mav;
	}
}