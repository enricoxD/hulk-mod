package gg.norisk.hulk.player

import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity

interface IHulkPlayer {
    var getCustomAttackReachDistance: Double
    var getCustomCreativeAttackReachDistance: Double
    var getCustomBlockReachDistance: Float
    var getCustomCreativeBlockReachDistance: Float
}