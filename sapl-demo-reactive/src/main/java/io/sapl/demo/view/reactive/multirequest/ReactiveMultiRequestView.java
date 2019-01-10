package io.sapl.demo.view.reactive.multirequest;

import static io.sapl.api.pdp.multirequest.IdentifiableAction.READ_ID;
import static io.sapl.api.pdp.multirequest.IdentifiableSubject.AUTHENTICATION_ID;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.multirequest.IdentifiableAction;
import io.sapl.api.pdp.multirequest.IdentifiableDecision;
import io.sapl.api.pdp.multirequest.IdentifiableSubject;
import io.sapl.api.pdp.multirequest.MultiRequest;
import io.sapl.api.pdp.multirequest.RequestElements;
import io.sapl.demo.service.BloodPressureService;
import io.sapl.demo.service.HeartBeatService;
import io.sapl.demo.view.reactive.AbstractReactiveView;
import io.sapl.pep.SAPLAuthorizer;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringComponent("reactiveMultiRequestView")
@SpringView(name = "reactiveMultiRequest")
public class ReactiveMultiRequestView extends AbstractReactiveView {

    private static final String READ_HEART_BEAT_DATA_REQUEST_ID = "readHeartBeatData";
    private static final String READ_BLOOD_PRESSURE_DATA_REQUEST_ID = "readBloodPressureData";

    private final SAPLAuthorizer authorizer;

    private final Map<String, Decision> accessDecisions;

    @Autowired
    public ReactiveMultiRequestView(SAPLAuthorizer authorizer, HeartBeatService heartBeatService, BloodPressureService bloodPressureService) {
        super(heartBeatService, bloodPressureService);
        this.authorizer = authorizer;

        accessDecisions = new HashMap<>();
        accessDecisions.put(READ_HEART_BEAT_DATA_REQUEST_ID, Decision.DENY);
        accessDecisions.put(READ_BLOOD_PRESSURE_DATA_REQUEST_ID, Decision.DENY);
    }

    protected Flux<Object[]> getCombinedFlux() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        final MultiRequest multiRequest = new MultiRequest();
        multiRequest.addSubject(new IdentifiableSubject(AUTHENTICATION_ID, authentication));
        multiRequest.addAction(new IdentifiableAction(READ_ID, "read"));
        multiRequest.addResource("heartBeatData");
        multiRequest.addResource("bloodPressureData");
        multiRequest.addRequest(READ_HEART_BEAT_DATA_REQUEST_ID, new RequestElements(AUTHENTICATION_ID, READ_ID, "heartBeatData"));
        multiRequest.addRequest(READ_BLOOD_PRESSURE_DATA_REQUEST_ID, new RequestElements(AUTHENTICATION_ID, READ_ID, "bloodPressureData"));

        final Flux<IdentifiableDecision> accessDecisionFlux = authorizer.authorize(multiRequest)
                .subscribeOn(Schedulers.newElastic("pdp"));

        return Flux.combineLatest(
                accessDecisionFlux,
                getHeartBeatDataFlux(),
                getDiastolicBloodPressureDataFlux(),
                getSystolicBloodPressureDataFlux(),
                Function.identity());
    }

    protected void updateUI(Object[] fluxValues) {
        final IdentifiableDecision identifiableDecision = (IdentifiableDecision) fluxValues[0];
        accessDecisions.put(identifiableDecision.getRequestId(), identifiableDecision.getDecision());

        final Decision heartBeatDecision = accessDecisions.get(READ_HEART_BEAT_DATA_REQUEST_ID);
        final Decision bloodPressureDecision = accessDecisions.get(READ_BLOOD_PRESSURE_DATA_REQUEST_ID);

        final Integer heartBeat = (Integer) fluxValues[1];
        final Integer diastolic = (Integer) fluxValues[2];
        final Integer systolic = (Integer) fluxValues[3];

        updateUI(heartBeatDecision, bloodPressureDecision, heartBeat, diastolic, systolic);
    }
}
