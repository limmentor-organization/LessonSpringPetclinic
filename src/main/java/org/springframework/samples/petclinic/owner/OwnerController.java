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
public class OwnerController {
	// オーナー登録/オーナー情報更新画面パス
	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";
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
	public OwnerController(OwnerRepository clinicService) {
		// Repository層導入
		this.owners = clinicService;
	}

	/*
	 * <<<Handlerメソッドの引数に対して処理するとき、idを除外する>>>
	 * 
	 * tips: @InitBinderについて
	 * ・@RequestMappingメソッドの全ての引数をサポートできるようになる
	 * ・流れは以下のようになる
	 * 「パラメタ→@InitBinder→@ModelAttribute→@RequestMapping→ModelAndView」
	 */
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		// 不正なリクエストパラメータを拒否する
		dataBinder.setDisallowedFields("id");
	}

	/*
	 * <<<OwnerをonwerとしてModel登録させるメソッド>>>
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
	public Owner findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
		return ownerId == null ? new Owner() : this.owners.findById(ownerId);
	}

	/*
	 * <<<オーナー登録画面に対して、GETリクエストするメソッド>>>
	 */
	@GetMapping("owners/new")
	public String initCreationForm(Map<String, Object> model) {
		// Ownerインスタンス生成
		Owner owner = new Owner();
		// Owner情報をownerとしてModel登録
		model.put("owner", owner);
		// オーナー情報登録画面に遷移
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	/*
	 * <<<オーナー登録画面に対して、POSTリクエストするメソッド>>>
	 * 
	 * tips:
	 * ●@Validについて：
	 * ・フォーム内の各フィールドを検証し、データの有効性を確保する
	 * 
	 * ●RedirectAttributesについて：
	 * ・リダイレクト先にパラメータを送れるようになる
	 * ・オブジェクトを送る場合は、ModelMapに格納する必要がある
	 */
	@PostMapping("/owners/new")
	public String processCreationForm(@Valid Owner owner, BindingResult result, RedirectAttributes redirectAttributes) {
		// バリデーションにかかった場合
		if (result.hasErrors()) {
			// エラーメッセージをmodelに登録
			redirectAttributes.addFlashAttribute("error", "There was an error in creating the owner.");
			// オーナー情報登録画面に遷移
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		// オーナー情報をDBに保存する
		this.owners.save(owner);
		// 登録完了メッセージをmodelに保存する
		redirectAttributes.addFlashAttribute("message", "New Owner Created");
		//　オーナー詳細画面にリダイレクトする
		return "redirect:/owners/" + owner.getId();
	}

	//<<<オーナー検索画面に対して、GETリクエストを送るメソッド>>>
	@GetMapping("/owners/find")
	public String initFindForm() {
		return "owners/findOwners";
	}

	/*
	 *  <<<オーナー一覧画面に対して、GETリクエストを送るメソッド>>>
	 *  
	 *  tips:
	 *  ●@RequestParamについて
	 *  ・defaultValueは、リクエストパラメータが指定されなかったときに作動する
	 *  ・html側でaタグのurlを「/owners?page=ページ番号」としてるため、そのリンクを押下したときに@RequestParamが「ページ番号」をキャッチする
	 */
	@GetMapping("/owners")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result,
			Model model) {
		// lastNameがnullの場合
		if (owner.getLastName() == null) {
			// OwnerのlastNameに空文字をセット
			owner.setLastName("");
		}

		// DBからPage型のOwner情報を取得する
		Page<Owner> ownersResults = findPaginatedForOwnersLastName(page, owner.getLastName());

		// DBから取得したOwner情報がなかった場合
		if (ownersResults.isEmpty()) {
			// messages.propertiesのnotFoundを再設定
			// 第一引数：Ownerのフィールド名、第二引数：messages.propertiesのキー、第三引数：入れたい値
			result.rejectValue("lastName", "notFound", "not found");
			// オーナー検索画面に遷移
			return "owners/findOwners";
		}

		// DBから取得したOwner情報が1件の場合
		if (ownersResults.getTotalElements() == 1) {
			// イテレータを用いて反復処理を行い、次の要素を取得する
			owner = ownersResults.iterator().next();
			// オーナー詳細画面にリダイレクト
			return "redirect:/owners/" + owner.getId();
		}

		// ページ単位のオーナー一覧画面を取得する（デフォルトページ1）
		return addPaginationModel(page, model, ownersResults);
	}

	/*
	 * <<<各ページのOwner情報を抽出し、ページ単位でmodelに登録してhtmlパスを返すメソッド>>>
	 * 
	 * tips:
	 * ●Page<>.getContent()について
	 * ・対応したページの内容をListで取得する
	 */
	private String addPaginationModel(int page, Model model, Page<Owner> paginated) {
		// ページの内容をListとして取得する
		List<Owner> listOwners = paginated.getContent();
		// 現在開いているページ番号
		model.addAttribute("currentPage", page);
		// 総ページ数
		model.addAttribute("totalPages", paginated.getTotalPages());
		// DBから取得したOwnerの総数
		model.addAttribute("totalItems", paginated.getTotalElements());
		// 現在開いてるページ分のOwnerリスト
		model.addAttribute("listOwners", listOwners);
		// オーナー一覧画面のパスを指定
		return "owners/ownersList";

	}

	/*
	 * <<<lastNameで絞ったPage型のOwnerを返すメソッド>>>
	 * 
	 * tips:
	 * ●Pageについて：
	 * ・Pageは、ページ情報を持ったListのようなもの
	 * 
	 * ●Pageableについて：
	 * ・RepositoryのQueryメソッドの引数にPageableオブジェクトを指定することで、該当ページの情報がPageオブジェクトとして返却される
	 */
	private Page<Owner> findPaginatedForOwnersLastName(int page, String lastName) {
		// 1ページあたりの表示件数
		int pageSize = 5;
		// ページ単位に表示する件数を定義（第一引数：ページ番号、第二引数：1ページあたりの表示件数）
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		// DBからlastNameで絞ったOwner情報を取得
		return owners.findByLastName(lastName, pageable);
	}

	/*
	 * <<<オーナー詳細画面に対して、GETリクエストを送るメソッド>>>
	 */
	@GetMapping("owners/{ownerId}/edit")
	public String initupdateOwnerForm(@PathVariable("ownerId") int ownerId, Model model) {
		// DBからオーナー情報を取得する
		Owner owner = this.owners.findById(ownerId);
		// オーナー情報をmodel登録する
		model.addAttribute(owner);
		// オーナー情報更新画面に遷移
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	/*
	 * <<<オーナー詳細画面に対して、POSTリクエストを送るメソッド>>>
	 * 
	 * tips:
	 * ●@Validについて：
	 * ・フォーム内の各フィールドを検証し、データの有効性を確保する
	 * 
	 * ●RedirectAttributesについて：
	 * ・リダイレクト先にパラメータを送れるようになる
	 * ・オブジェクトを送る場合は、ModelMapに格納する必要がある
	 */
	@PostMapping("/owners/{ownerId}/edit")
	public String processupdateOwnerForm(@Valid Owner owner, BindingResult result, @PathVariable("ownerId") int ownerId,
			RedirectAttributes redirectAttributes) {
		// バリデーションにかかった場合
		if (result.hasErrors()) {
			// エラーメッセージをmodelに登録する
			redirectAttributes.addFlashAttribute("error", "There was an error in creating the owner.");
			//　更新画面に遷移する
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		// urlからのid値をOwnerにセットする
		owner.setId(ownerId);
		// DBにOnwer情報を保存する
		this.owners.save(owner);
		// 登録完了メッセージをmodelに登録する
		redirectAttributes.addFlashAttribute("message", "New Owner Created");
		// オーナー詳細画面にリダイレクトする
		return "redirect:/owners/{ownerId}";
	}

	/*
	 * <<<オーナー詳細画面に対して、GETリクエストを送るメソッド>>>
	 * 
	 * tips:
	 * ●ModelAndViewについて
	 * ・インスタンス生成時の引数に対象画面のパスを入れる
	 * ・model登録したいオブジェクトはaddObjectメソッドを用いる
	 * ・handlerメソッドの戻り値はModelAndViewインスタンスを指定
	 */
	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		// ModelAndViewインスタンス初期化（オーナー詳細画面のパスを指定）
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		// DBからオーナーid条件でOwnerを取得する
		Owner owner = this.owners.findById(ownerId);
		// Ownerをmodelに登録する
		mav.addObject(owner);
		// mavを返す
		return mav;
	}
}
