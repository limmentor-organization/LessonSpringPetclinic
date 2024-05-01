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
public class VisitController {
	// Repository層変数宣言
	private OwnerRepository owners;

	// コンストラクタインジェクション（spring推奨）
	/*
	 * メリット：
	 * ・フィールドがfinal宣言でき、イミュータブルなオブジェクトにしたり、
	 * 、必要な依存関係のみを不変にすることができる
	 * 
	 * ・循環依存を防ぐことができる
	 */
	public VisitController(OwnerRepository owners) {
		// Repository層導入
		this.owners = owners;
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
	public void setAllownedFields(WebDataBinder dataBinder) {
		// 不正なリクエストパラメータを拒否する
		dataBinder.setDisallowedFields("id");
	}

	/*
	 * <<<VisitをvisitとしてModel登録させるメソッド>>>
	 * 
	 * tips:
	 *●@ModelAttributeについて：
	 *・Handlerメソッドが実行される前に、このメソッドが実行されるようになる
	 *・引数の"visit"はmodel.addAttributeの第一引数と同じ
	 *
	 *●@PathVariableについて：
	 *・required属性はfalseにすることで、URLのパラメータの有無を考慮しなくてよくなる
	 * → @ModelAttributeのメソッドに付与することで、エンドポイントがパラメタのあるものとそうでないもののHandlerメソッド両方で扱えるようになる
	 */
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId,
			Map<String, Object> model) {
		// ownerIdでDBからOwner情報を取得する
		Owner owner = this.owners.findById(ownerId);
		// Pet情報を取得する
		Pet pet = owner.getPet(petId);
		// Pet情報をModelに登録する
		model.put("pet", pet);
		// Owner情報をModelに登録する
		model.put("owner", owner);
		// Visitインスタンスを作成する
		Visit visit = new Visit();
		// Pet情報にVisit情報を追加する
		pet.addVisit(visit);
		// Pet情報を戻り値とする
		return visit;
	}

	/*
	 * <<<訪問情報登録/編集画面に対して、GETリクエストをするメソッド>>>
	 */
	@GetMapping("owners/{ownerId}/pets/{petId}/visits/new")
	public String initNewVisitForm() {
		// 訪問情報登録/編集画面のパスを指定
		return "pets/createOrUpdateVisitForm";
	}

	/*
	 * <<<訪問情報登録/編集画面に対して、POSTリクエストをするメソッド>>>
	 * 
	 * tips:
	 * ●@Validについて：
	 * ・フォーム内の各フィールドを検証し、データの有効性を確保する
	 * 
	 * ●RedirectAttributesについて：
	 * ・リダイレクト先にパラメータを送れるようになる
	 * ・オブジェクトを送る場合は、ModelMapに格納する必要がある
	 */
	@PostMapping("owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@ModelAttribute Owner owner, @PathVariable int petId, @Valid Visit visit,
			BindingResult result, RedirectAttributes redirectAttributes) {
		// バリデーションにかかった場合
		if (result.hasErrors()) {
			// 訪問情報登録/編集画面に遷移する
			return "pets/createOrUpdateVisitForm";
		}

		// OwnerにVisit情報を追加する
		owner.addVisit(petId, visit);
		// DBにOwner情報を登録する
		this.owners.save(owner);
		// 登録完了メッセージをModelに保存する
		redirectAttributes.addFlashAttribute("message", "Your vist has been boked");

		// オーナー詳細画面にリダイレクトする
		return "redirect:/owners/{ownerId}";
	}
}
