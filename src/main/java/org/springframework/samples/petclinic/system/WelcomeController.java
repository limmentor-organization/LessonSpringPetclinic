package org.springframework.samples.petclinic.system;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

	// <<<Home画面に対して、GETリクエストを送るメソッド>>>
	@GetMapping("/")
	public String welcome() {
		// Home画面のパスを指定
		return "welcome";
	}
}
