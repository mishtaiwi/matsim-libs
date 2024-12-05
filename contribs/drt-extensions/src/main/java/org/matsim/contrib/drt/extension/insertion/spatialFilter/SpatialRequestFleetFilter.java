package org.matsim.contrib.drt.extension.insertion.spatialFilter;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.index.strtree.STRtree;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.drt.optimizer.VehicleEntry;
import org.matsim.contrib.drt.optimizer.insertion.RequestFleetFilter;
import org.matsim.contrib.drt.passenger.DrtRequest;
import org.matsim.contrib.drt.schedule.DrtStopTask;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.schedule.DriveTask;
import org.matsim.contrib.dvrp.schedule.Schedule;
import org.matsim.contrib.dvrp.schedule.StayTask;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.contrib.dvrp.tracker.OnlineDriveTaskTracker;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.utils.geometry.GeometryUtils;

import java.util.*;

import static org.matsim.contrib.drt.schedule.DrtTaskBaseType.getBaseTypeOrElseThrow;

/**
 * @author nuehnel / MOIA
 */
public class SpatialRequestFleetFilter implements RequestFleetFilter {

    private STRtree tree = new STRtree();

    private final Fleet fleet;
    private final MobsimTimer mobsimTimer;
    private final double expansionIncrementFactor;
    private final double maxExpansion;
    private final double minExpansion;

    private final boolean returnAllIfEmpty;

    private final int minCandidates;

    private final double updateInterval;

    public SpatialRequestFleetFilter(Fleet fleet, MobsimTimer mobsimTimer,
                                     SpatialFilterInsertionSearchQSimModule.SpatialInsertionFilterSettings settings) {
        this.fleet = fleet;
        this.mobsimTimer = mobsimTimer;
        this.expansionIncrementFactor = settings.expansionIncrement();
        this.minExpansion = settings.minExpansion();
        this.maxExpansion = settings.maxExpansion();
        this.returnAllIfEmpty = settings.returnAllIfEmpty();
        this.minCandidates = settings.minCandidates();
        this.updateInterval = settings.updateInterval();
    }

    @Override
    public Collection<VehicleEntry> filter(DrtRequest drtRequest, Map<Id<DvrpVehicle>, VehicleEntry> vehicleEntries, double now) {
        if ((mobsimTimer.getTimeOfDay() % updateInterval) == 0) {
            buildTree();
        }
        return filterEntries(vehicleEntries, drtRequest);
    }

    private Collection<VehicleEntry> filterEntries(Map<Id<DvrpVehicle>, VehicleEntry> vehicleEntries, DrtRequest drtRequest) {
        List<Id<DvrpVehicle>> result = Collections.emptyList();
        Point point = GeometryUtils.createGeotoolsPoint(drtRequest.getFromLink().getToNode().getCoord());

        for (double expansion = minExpansion; expansion <= maxExpansion && result.size() < minCandidates; expansion*= expansionIncrementFactor) {
            Envelope envelopeInternal = point.getEnvelopeInternal();
            envelopeInternal.expandBy(expansion);
            result = tree.query(envelopeInternal);
        }

        if(result.size() < minCandidates) {
            if(returnAllIfEmpty) {
                return vehicleEntries.values();
            }
            return Collections.emptySet();
        }
        return extract(vehicleEntries, result);
    }

    private Collection<VehicleEntry> extract(Map<Id<DvrpVehicle>, VehicleEntry> vehicleEntries, List<Id<DvrpVehicle>> result) {
        Set<VehicleEntry> extracted = new LinkedHashSet<>();
        for (Id<DvrpVehicle> dvrpVehicleId : result) {
            extracted.add(vehicleEntries.get(dvrpVehicleId));
        }
        return extracted;
    }

    private void buildTree() {
        tree = new STRtree();
        for (DvrpVehicle vehicle : fleet.getVehicles().values()) {
            Schedule schedule = vehicle.getSchedule();
            Task startTask;
            Link start;
            if (schedule.getStatus() == Schedule.ScheduleStatus.STARTED) {
                startTask = schedule.getCurrentTask();
                start = switch (getBaseTypeOrElseThrow(startTask)) {
                    case DRIVE -> {
                        var driveTask = (DriveTask) startTask;
                        var diversionPoint = ((OnlineDriveTaskTracker) driveTask.getTaskTracker()).getDiversionPoint();
                        yield diversionPoint != null ? diversionPoint.link : //diversion possible
                                driveTask.getPath().getToLink();// too late for diversion
                    }
                    case STOP -> ((DrtStopTask) startTask).getLink();
                    case STAY -> ((StayTask) startTask).getLink();
                };
                tree.insert(GeometryUtils.createGeotoolsPoint(start.getCoord()).getEnvelopeInternal(), vehicle.getId());
            }
        }
    }
}
