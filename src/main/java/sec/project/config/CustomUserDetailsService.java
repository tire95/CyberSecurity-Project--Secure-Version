package sec.project.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sec.project.domain.Account;
import sec.project.domain.Password;
import sec.project.repository.AccountRepository;
import sec.project.repository.PasswordRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    PasswordRepository passwordRepository;
    
    @PostConstruct
    public void init() {
        try (InputStream input = new FileInputStream("./src/main/resources/passwords.txt")) {
            Scanner reader = new Scanner(input);
            while(reader.hasNextLine()) {
                passwordRepository.save(new Password(reader.nextLine().toLowerCase()));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
}
