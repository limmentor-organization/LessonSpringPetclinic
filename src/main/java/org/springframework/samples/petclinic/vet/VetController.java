package org.springframework.samples.petclinic.vet;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VetController {

	// Repository層変数宣言
	private final VetRepository vetRepository;

	// コンストラクタインジェクション（spring推奨）
	/*
	 * メリット：
	 * ・フィールドがfinal宣言でき、イミュータブルなオブジェクトにしたり、
	 * 、必要な依存関係のみを不変にすることができる
	 * 
	 * ・循環依存を防ぐことができる
	 */
	public VetController(VetRepository clinicService) {
		// Repository層導入
		this.vetRepository = clinicService;
	}

	/*
	 *  <<<獣医師一覧画面に対して、GETリクエストを送るメソッド>>>
	 *  
	 *  tips:
	 *  ●@RequestParamについて
	 *  ・defaultValueは、リクエストパラメータが指定されなかったときに作動する
	 *  ・html側でaタグのurlを「/owners?page=ページ番号」としてるため、そのリンクを押下したときに@RequestParamが「ページ番号」をキャッチする
	 */
	@GetMapping("/vets.html")
	public String showVetList(@RequestParam(defaultValue = "1") int page, Model model) {

		// Vetsインスタンスを生成する
		Vets vets = new Vets();
		Page<Vet> paginated = findPaginated(page);
		vets.getVetList().addAll(paginated.toList());
		return addPaginationModel(page, paginated, model);
	}

	/*
	 * <<<各ページのVet情報を抽出し、ページ単位でmodelに登録してhtmlパスを返すメソッド>>>
	 * 
	 * tips:
	 * ●Page<>.getContent()について
	 * ・対応したページの内容をListで取得する
	 */
	private String addPaginationModel(int page, Page<Vet> paginated, Model model) {
		// ページの内容をListとして取得する
		List<Vet> listVets = paginated.getContent();
		// 現在開いているページ番号
		model.addAttribute("currentPage", page);
		// 総ページ数
		model.addAttribute("totalPages", paginated.getTotalPages());
		// DBから取得したVetの総数
		model.addAttribute("totalItems", paginated.getTotalElements());
		// 現在開いているページ分のVetリスト
		model.addAttribute("listVets", listVets);
		// 獣医師一覧画面のパスを指定
		return "vets/vetList";
	}

	/*
	 * <<<Page型のVetを返すメソッド>>>
	 * 
	 * tips:
	 * ●Pageについて：
	 * ・Pageは、ページ情報を持ったListのようなもの
	 * 
	 * ●Pageableについて：
	 * ・RepositoryのQueryメソッドの引数にPageableオブジェクトを指定することで、該当ページの情報がPageオブジェクトとして返却される
	 */
	private Page<Vet> findPaginated(int page) {
		// 1ページあたりの表示件数
		int pageSize = 5;
		// ページ単位に表示する件数を定義（第一引数：ページ番号、第二引数：1ページあたりの表示件数）
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		// DBからVet情報を取得
		return vetRepository.findAll(pageable);
	}

	/* <<<vetデータをマッピングしたJSONを表示する、GETリクエストを送るメソッド
	 * 
	 * tips: @ResponseBodyについて
	 * ・戻り値がそのままレスポンスのボディーになる
	 */
	@GetMapping({ "/vets" })
	public @ResponseBody Vets showResourcesVetList() {
		// Vetsインスタンスを生成する
		Vets vets = new Vets();
		// DBから取得したVet情報を全てVetsに格納する
		vets.getVetList().addAll(this.vetRepository.findAll());
		// Vetsを返す
		return vets;
	}
}
