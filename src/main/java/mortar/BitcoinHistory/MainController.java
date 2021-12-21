package mortar.BitcoinHistory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    
    @Autowired
    private MarketDataService marketDataService;
  
    @GetMapping("/")
    public String viewMain(Model model) {
                
        marketDataService.addAttributesToModelForPageMain(model);
        
        return "main";
    }
    
    @PostMapping("/")
    public String fetch(@RequestParam String from, @RequestParam String to) {
        
        marketDataService.fetch(from, to);
        
        return "redirect:/";
    }
    
    @PostMapping("/delete/{index}")
    public String deleteQuery(@PathVariable int index) {
    
        marketDataService.deleteQuery(index);
        
        return "redirect:/";
    }
    
    @PostMapping("/clear")
    public String clearSession() {
        
        marketDataService.clearSession();
        
        return "redirect:/";
    }
    
   
}
