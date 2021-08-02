package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.controller.SectionDemandView;
import ir.proprog.enrollassist.domain.EnrollmentList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EnrollmentListRepository extends CrudRepository<EnrollmentList, Long> {
    @Query(value = "select new ir.proprog.enrollassist.controller.SectionDemandView(section.id, count(distinct list.owner)) from EnrollmentList list join list.sections as section group by section.id")
    List<SectionDemandView> findDemandForAllSections();
}