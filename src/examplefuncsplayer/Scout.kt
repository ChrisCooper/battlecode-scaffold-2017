package examplefuncsplayer

import battlecode.common.RobotController
import battlecode.common.RobotType
import examplefuncsplayer.RobotPlayer.*

class Scout(rc: RobotController) : SafeRobotBrain(rc) {

    override fun runTurn() {
        val robots = rc.senseNearbyRobots(-1f, THEM)

        // If there are some...
        if (robots.isNotEmpty()) {
            val gardeners = robots.filter { r -> r.type == RobotType.GARDENER}

            if (gardeners.any()) { //Try to kill gardeners first
                moveWithLocalAvoidance(gardeners[0].location)
                if (rc.canFireSingleShot()) {
                    rc.fireSingleShot(rc.location.directionTo(gardeners[0].location))
                }
            } else { //No gardeners... try another enemy unit type
                if (rc.canFireSingleShot()) {
                    // ...Then fire a bullet in the direction of the enemy.
                    rc.fireSingleShot(rc.location.directionTo(robots[0].location))
                }
            }
        } else {
            moveWithLocalAvoidance(rc.getInitialArchonLocations(THEM)[0])
        }
    }
}