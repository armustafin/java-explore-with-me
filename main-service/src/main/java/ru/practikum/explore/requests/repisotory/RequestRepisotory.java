package ru.practikum.explore.requests.repisotory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practikum.explore.events.dto.Event;
import ru.practikum.explore.requests.dto.Request;
import ru.practikum.explore.requests.dto.StatusRequest;
import ru.practikum.explore.requests.dto.ViewRequst;

import java.util.List;

public interface RequestRepisotory extends JpaRepository<Request, Integer>, QuerydslPredicateExecutor<Request> {

    @Query("select req.event.id, count(req.id) " +
            "from Request as req " +
            "where (req.event IN (:events) AND (req.status = :sr) )" +
            "group by req.event  ")
    List<ViewRequst> findViewReqest(List<Event> events, StatusRequest sr);

    List<Request> findAllByStatusAndEvent(StatusRequest statusRequest, Event event);

    List<Request> findAllByRequesterId(Integer requesterid);

    List<Request> findAllByRequesterIdAndEventId(Integer requesterid, Integer eventId);
}
