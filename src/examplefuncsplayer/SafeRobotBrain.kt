package examplefuncsplayer

import battlecode.common.*

abstract class SafeRobotBrain(rc: RobotController) {
    val US = rc.team
    val THEM = if (US == Team.A) Team.B else Team.A

    val robotType = rc.type

    abstract fun runTurn()

    fun safeRun() {
        while (true) {
            try {
                this.runTurn()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Clock.`yield`()
        }
    }

    fun getNearbyFriendlies(type: RobotType): List<RobotInfo> {
        val nearbyFriendlies = RobotPlayer.rc.senseNearbyRobots(-1.0f, US)
        return nearbyFriendlies.filter { f -> f.type == type }
    }
}