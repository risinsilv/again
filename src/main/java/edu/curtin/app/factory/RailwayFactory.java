package edu.curtin.app.factory;

import edu.curtin.app.model.Railway;
import edu.curtin.app.model.Town;
import edu.curtin.app.state.DualTrackUnderConstruction;
import edu.curtin.app.state.SingleTrackUnderConstruction;

public class RailwayFactory {
    public Railway createSingleTrack(Town a, Town b) {
        return new Railway(a, b, new SingleTrackUnderConstruction(), 5);
    }
    public Railway createDualTrack(Town a, Town b) {
        return new Railway(a, b, new DualTrackUnderConstruction(), 5);
    }
}
