package examplefuncsplayer

import battlecode.common.*
import examplefuncsplayer.RobotPlayer.rc

abstract class SafeRobotBrain(rc: RobotController) {
    val US = rc.team
    val THEM = if (US == Team.A) Team.B else Team.A

    val robotType = rc.type

    abstract fun runTurn()

    fun safeRun() {
        while (true) {
            try {
                //Buy victory points if have enough to win
                if (RobotPlayer.rc.teamBullets >= RobotPlayer.rc.victoryPointCost * (1000 - RobotPlayer.rc.teamVictoryPoints)) {
                    RobotPlayer.rc.donate(RobotPlayer.rc.victoryPointCost * (1000 - RobotPlayer.rc.teamVictoryPoints))
                }
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

    fun moveWithLocalAvoidance(direction: Direction) {
        if (RobotPlayer.rc.canMove(direction)) {
            RobotPlayer.rc.move(direction)
        } else if (RobotPlayer.rc.canMove(Direction(direction.angleDegrees + 90f))) {
            RobotPlayer.rc.move(Direction(direction.angleDegrees + 90f))
        } else if (RobotPlayer.rc.canMove(Direction(direction.angleDegrees - 90f))) {
            RobotPlayer.rc.move(Direction(direction.angleDegrees - 90f))
        }
    }

    fun moveWithLocalAvoidance(location: MapLocation) {
        if (RobotPlayer.rc.canMove(location)) {
            RobotPlayer.rc.move(location)
        } else if (RobotPlayer.rc.canMove(Direction(Direction(RobotPlayer.rc.location, location).angleDegrees + 90f))) {
            RobotPlayer.rc.move(Direction(Direction(RobotPlayer.rc.location, location).angleDegrees + 90f))
        } else if (RobotPlayer.rc.canMove(Direction(Direction(RobotPlayer.rc.location, location).angleDegrees - 90f))) {
            RobotPlayer.rc.move(Direction(Direction(RobotPlayer.rc.location, location).angleDegrees - 90f))
        }
    }
}

class Vector {
    var x: Float
    var y: Float

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun length() = Math.sqrt((x * x + y * y).toDouble()).toFloat()

    override fun toString(): String = "Vector($x, $y)"
}


fun FromTo(from: MapLocation, to: MapLocation): Vector {
    return Vector(to.x - from.x, to.y - from.y)
}

