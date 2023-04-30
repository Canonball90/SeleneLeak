package mod.selene.world;

import mod.selene.loader.EventHandler;

public class UpdateWalkingPlayerEvent extends EventHandler {
    private int iterations;

    public UpdateWalkingPlayerEvent(int stage) {
        super(stage);
    }

    /**
     * Gets the iterations
     *
     * @return The number of iterations
     */
    public int getIterations() {
        return iterations;
    }

    public void setIterations(int in) {
        iterations = in;
    }
}
