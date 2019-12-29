package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.Password;

public interface PasswordRepository extends JpaRepository<Password, Long> {
    
    Password findByPassword(String username);
    
}
