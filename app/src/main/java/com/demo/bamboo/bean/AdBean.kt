package com.demo.bamboo.bean

class AdBean(
    val bambooId:String,
    val bambooFrom:String,
    val bambooType:String,
    val bambooSort:Int,
) {
    override fun toString(): String {
        return "AdBean(bambooId='$bambooId', bambooFrom='$bambooFrom', bambooType='$bambooType', bambooSort=$bambooSort)"
    }
}