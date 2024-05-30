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
class VetController {
	
	private final VetRepository vetRepository;
	
	public VetController(VetRepository clinicService) {
		this.vetRepository = clinicService;
	}
	
	/**
	 * @GetMapping("/vets.html")はHTTP GETリクエストに応答し、"/vets.html"というパスにマッピングされる
	 * @RequestParam(defaultValue = "1") int pageはクエリパラメータとして、ページ番号を受け取る（デフォルト値は1）
	 * 獣医リストを取得し、ページング処理を行う。findPaginatedメソッドで獣医データをページ単位で取得し、モデルに追加
	 * 
	 */
	
	@GetMapping("/vets.html")
	public String showVetList(@RequestParam(defaultValue = "1") int page, Model model) {
		Vets vets = new Vets();
		Page<Vet> paginated = findPaginated(page);
		vets.getVetList().addAll(paginated.toList());
		return addPaginationModel(page, paginated, model);
	}
	
	/**
	 * ページネーション情報をモデルに追加します。
	 * 現在のページ、総ページ数、総項目数、獣医リストをモデルに追加
	 */
	
	private String addPaginationModel(int page, Page<Vet> paginated, Model model) {
		List<Vet> listVets = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("tptalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listVets", listVets);
		return "vets/vetList";
	}
	
	/**
	 * 指定されたページ番号とページサイズ（ここでは5件）で獣医データを取得
	 * PageRequest.of(page - 1, pageSize)はSpring Data JPAのPageRequestを使用してページング情報を作成
	 * vetRepository.findAll(pageable)はページングされた獣医データを取得する処理
	 */
	
	private Page<Vet> findPaginated(int page) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return vetRepository.findAll(pageable);
	}
	
	/**
	 * @ResponseBodyを付与することで返り値をhtmlではなく、JSON形式で返却できる
	 */
	
	@GetMapping({"/vets"})
	public @ResponseBody Vets showResourcesVetList() {
		Vets vets = new Vets();
		vets.getVetList().addAll(this.vetRepository.findAll());
		return vets;
	}
}