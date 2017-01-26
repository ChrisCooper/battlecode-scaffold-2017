package examplefuncsplayer

import battlecode.common.*

import examplefuncsplayer.RobotPlayer.rc

class Gardener(rc: RobotController) : SafeRobotBrain(rc) {

    val FULL_CIRCLE = (Math.PI * 2)
    val NUM_CIRCLE_SPACES = 6f

    val TREE_DIRECTIONS = (1..5)
            .map { i -> FULL_CIRCLE * (i / NUM_CIRCLE_SPACES) }
            .map { rads -> Direction(rads.toFloat()) }

    val MIN_GARDENER_DISTANCE = 8f

    val UNIT_DIRECTION = 0
    var startedGardening = false
    var firstSpawned = false
    var hasSpawnedLumberjack = false
    var hasSpawnedScout = false


    //val PLANTING_SPACE = rc.

    override fun runTurn() {
        // Build some trees
        //rc.setIndicatorLine(rc.location, rc.location.add(Direction.NORTH, 5f), 100, 100, 100)

        if (!hasSpawnedScout) {
            spawnScout(Direction(0f))
        }
        if (!hasSpawnedLumberjack) {
            spawnLumberjack(Direction(0f))
        }

        if (startedGardening) {
            plantAndWater()
        } else {
            if (firstSpawned) {

                moveAwayFrom(rc.senseNearbyRobots(-1f, US).toList())
            }
            findGardeningSpace()
        }

    }

    private fun spawnScout(direction: Direction) {
        if (rc.canBuildRobot(RobotType.SCOUT, direction)) {
            rc.buildRobot(RobotType.SCOUT, direction)
            hasSpawnedScout = true
        }
    }

    private fun spawnLumberjack(direction: Direction) {
        if (rc.canBuildRobot(RobotType.LUMBERJACK, direction)) {
            rc.buildRobot(RobotType.LUMBERJACK, direction)
            hasSpawnedLumberjack = true
        }
    }

    private fun plantAndWater() {
        TREE_DIRECTIONS.forEach { dir ->
            if (rc.canPlantTree(dir)) {
                rc.plantTree(dir)
            }
        }
        val nearbyTrees = rc.senseNearbyTrees(robotType.bodyRadius + 1f, rc.team)
        if (nearbyTrees.isNotEmpty()) {
            var minHealthTree: TreeInfo = nearbyTrees[0]
            nearbyTrees.forEach { tree ->
                rc.setIndicatorDot(tree.location, 200, 255, 200)
                if (minHealthTree.health > tree.health) {
                    minHealthTree = tree
                }
            }
            if (rc.canWater(minHealthTree.ID)) {
                rc.setIndicatorDot(minHealthTree.location, 50, 255, 50)
                rc.water(minHealthTree.ID)
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


}



