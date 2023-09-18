package gabrielhtg.icstardashboardsalesbackend.repository;

import gabrielhtg.icstardashboardsalesbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
