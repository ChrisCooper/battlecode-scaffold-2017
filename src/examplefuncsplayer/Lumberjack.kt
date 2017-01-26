package examplefuncsplayer

import battlecode.common.*
import examplefuncsplayer.RobotPlayer.*

class Lumberjack(rc: RobotController) : SafeRobotBrain(rc) {
    override fun runTurn() {
        val trees = rc.senseNearbyTrees()
        val closestTree = getClosestNeutralTree(trees)

        if (closestTree != null) {
            rc.setIndicatorLine(rc.location, closestTree.location, 255, 50, 50)
            if (FromTo(rc.location, closestTree.location).length() - rc.type.bodyRadius - closestTree.radius < GameConstants.INTERACTION_DIST_FROM_EDGE) {
                if (rc.canChop(closestTree.ID)) {
                    rc.chop(closestTree.ID)
                }
            } else {
                val distanceToTreeEdge: Float = FromTo(rc.location, closestTree.location).length() - closestTree.radius
                val direction = Direction(rc.location, closestTree.location)
                if (rc.canMove(direction, distanceToTreeEdge)) {
                    rc.move(direction, distanceToTreeEdge)
                }
            }
        } else {
            //In case there are no trees near, move towards enemy base to find more neutral trees on the way
            moveWithLocalAvoidance(rc.getInitialArchonLocations(THEM)[0])
        }
    }

    private fun getClosestNeutralTree(trees: Array<out TreeInfo>): TreeInfo? {
        var closestTree: TreeInfo? = null

        if (trees.isNotEmpty()) {

            var length = FromTo(rc.location, trees[0].location).length()

            trees.filter { tree ->
                tree.team == Team.NEUTRAL
            }.forEach { tree ->
                if (closestTree == null) {
                    closestTree = tree
                } else {
                    val newLength = FromTo(rc.location, tree.location).length()
                    if (newLength < length) {
                        length = newLength
                        closestTree = tree
                    }
                }

            }
        }
        return closestTree
    }
}