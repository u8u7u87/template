package slimer.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Test {
	
	/**
	 * return a ResolveView
	 * @param name
	 * @param model
	 * @return
	 */
	@RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "test";
    }
	
	/**
	 * return a ResponseEntity,Without pages
	 * @param name
	 * @return
	 */
	@RequestMapping(value="/test/{name}")
	public ResponseEntity<Map<String, Object>> test(@PathVariable(value="name")String name) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("name", name);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
}
