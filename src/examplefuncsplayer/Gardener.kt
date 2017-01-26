package examplefuncsplayer

import battlecode.common.*

import examplefuncsplayer.RobotPlayer.rc
class Gardener(rc: RobotController) : SafeRobotBrain(rc) {

    val FULL_CIRCLE = (Math.PI * 2)
    val NUM_CIRCLE_SPACES = 6f

    val TREE_DIRECTIONS = (1..5)
            .map { i -> FULL_CIRCLE * (i / NUM_CIRCLE_SPACES) }
            .map { rads -> Direction(rads.toFloat()) }

    val MIN_GARDENER_DISTANCE = 6.2f

    val UNIT_DIRECTION = 0
    var startedGardening = false


    //val PLANTING_SPACE = rc.

    override fun runTurn() {
        println("I am a gardener")

        // Build some trees
        //rc.setIndicatorLine(rc.location, rc.location.add(Direction.NORTH, 5f), 100, 100, 100)

        if (startedGardening) {
            plantAndWater()
        } else {
            findGardeningSpace()
        }

    }

    private fun plantAndWater() {
        TREE_DIRECTIONS.forEach { dir ->
            if (rc.canPlantTree(dir)) {
                rc.plantTree(dir)
            }
        }
        TREE_DIRECTIONS.forEach { dir ->
            val waterLocation = rc.location.add(dir, 1f)
            rc.setIndicatorDot(waterLocation, 200, 255, 200)
            if (rc.canWater(waterLocation)) {
                rc.water(waterLocation)
            }
        }
    }

    private fun findGardeningSpace() {

        val nearbyFriendlies = getNearbyFriendlies(RobotType.GARDENER)

        val buildingSpaceIsClear = !nearbyFriendlies.filter { robotInfo -> robotInfo.location.distanceTo(rc.location) <= MIN_GARDENER_DISTANCE }.any()

        if (buildingSpaceIsClear) {
            startedGardening = true
            plantAndWater()
        } else {
            moveAwayFrom(nearbyFriendlies)
        }
    }

    fun moveAwayFrom(bodies: List<BodyInfo>) {
        var weightedVector = Vector(0f, 0f)

        bodies.forEach { b ->
            val vector = FromTo(b.location, rc.location)
            weightedVector.x += vector.x
            weightedVector.y += vector.y
            println("Adding: $vector")

        }
        val direction = Direction(weightedVector.x, weightedVector.y)
        moveWithLocalAvoidance(direction)

        rc.setIndicatorLine(rc.location, rc.location.add(direction, weightedVector.length()), 100, 100, 255)
        println("Moving $weightedVector")

    }

    private fun moveWithLocalAvoidance(direction: Direction) {
        if (rc.canMove(direction)) {
            rc.move(direction)
        } else {

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



