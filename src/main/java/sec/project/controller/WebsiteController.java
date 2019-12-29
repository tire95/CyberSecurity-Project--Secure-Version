package sec.project.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Account;
import sec.project.domain.Note;
import sec.project.repository.AccountRepository;
import sec.project.repository.NoteRepository;
import sec.project.repository.PasswordRepository;

@Controller
public class WebsiteController {

    @Autowired
    NoteRepository noteRepository;
    
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private PasswordRepository passwordRepository;
    
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login() {return "login";}
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String loadNotebook() {
        return "redirect:/notebook";
    }
    
    @RequestMapping(value = "/notebook", method = RequestMethod.GET)
    public String notebook(Model model) {
        List<Note> notes = accountRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getNotes();
        model.addAttribute("notes", notes);
        return "notebook";
    }
    
    @RequestMapping(value = "/notebook/{itemId}", method = RequestMethod.DELETE)
    public String remove(@PathVariable Long itemId) {
        noteRepository.delete(itemId);
        return "redirect:/notebook";
    }
    
    @RequestMapping(value = "/addNote", method = RequestMethod.POST)
    public String addNote(@RequestParam String title, @RequestParam String content) {
        Account account = accountRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Note note = new Note(title, content, account);
        List<Note> notes = account.getNotes();
        notes.add(note);
        account.setNotes(notes);
        accountRepository.save(account);
        noteRepository.save(note);
        return "redirect:/notebook";
    }
    
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String toRegister() {
        return "register";
    }
    
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam String username, @RequestParam String password, Model model) {
        if (accountRepository.findByUsername(username) != null || passwordRepository.findByPassword(password.toLowerCase()) != null) {
            String error = "Invalid username and/or password";
            model.addAttribute("errorMsg", error);
            return "register";
        }
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
        return "redirect:/login";
    }

    

}
