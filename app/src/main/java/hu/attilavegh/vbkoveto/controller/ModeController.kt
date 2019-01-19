package hu.attilavegh.vbkoveto.controller

import hu.attilavegh.vbkoveto.model.Mode

class ModeController {

    var mode: Mode = Mode.NORMAL

    fun normal(): Boolean {
        return mode == Mode.NORMAL
    }
}