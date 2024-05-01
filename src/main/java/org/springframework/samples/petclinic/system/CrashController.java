package org.springframework.samples.petclinic.system;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CrashController {

	// <<<エラー画面に対して、GETリクエストを送るメソッド>>>
	@GetMapping("/oups")
	public String triggerException() {
		// エラーメッセージをスロー
		throw new RuntimeException(
				"Expected: controller used to showcase what " + "happens when an exception is thrown");
	}
}
