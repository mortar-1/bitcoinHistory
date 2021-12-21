package mortar.BitcoinHistory;

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
    public String viewMain(Model model, @RequestParam(required = false) String from, @RequestParam(required = false) String to) {
        
        marketDataService.addAttributesToModelForPageMain(model, from, to);
        
        return "main";
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
