package in.coempt.repository;

import in.coempt.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository  extends JpaRepository<Appointment, Long> {
    Appointment findByOrderNo(String orderId);
}
