package examplefuncsplayer

import battlecode.common.RobotController
import examplefuncsplayer.RobotPlayer.*

class Archon(rc: RobotController) : SafeRobotBrain(rc) {
    override fun runTurn() {
        // Generate a random direction
        val dir = randomDirection()

        // Randomly attempt to build a gardener in this direction
        if (rc.canHireGardener(dir) && Math.random() < .1) {
            rc.hireGardener(dir)
        }

        // Move randomly
        tryMove(randomDirection())

    }

}