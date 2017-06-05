package com.example.iedream.cameraswitchandroid;

/**
 * Created by iedream on 2017-06-02.
 */

public enum Proximity {
    Instant, Close, Medium, Far, Distant
}

class ProximityHelper
{
    public Proximity convertDistanceToProximity(double distance) {
        if (distance < 0.5) {
            return Proximity.Instant;
        } else if (distance < 2) {
            return Proximity.Close;
        } else if (distance < 5) {
            return Proximity.Medium;
        } else if (distance < 8) {
            return Proximity.Far;
        } else {
            return Proximity.Distant;
        }
    }

    public boolean include (Proximity currentProximity, Proximity settingProximity)
    {
        switch (settingProximity) {
            case Instant:
                switch (currentProximity) {
                    case Instant:
                        return true;
                    default:
                        return false;
                }
            case Close:
                switch (currentProximity) {
                    case Instant:
                    case Close:
                        return true;
                    default:
                        return false;
                }
            case Medium:
                switch (currentProximity) {
                    case Far:
                    case Distant:
                        return false;
                    default:
                        return true;
                }
            case Far:
                switch (currentProximity) {
                    case Distant:
                        return false;
                    default:
                        return true;
                }
            case Distant:
                return true;
        }
        return true;
    }
}
