package org.springframework.samples.petclinic.system;

import org.springframework.web.bind.annotation.GetMapping;

class CrashController {
	
	@GetMapping("/oups")
	public String triggerException() {
		throw new RuntimeException("Expected: controller used to showcase what " + "happens when an exception is thrown");
	}
}